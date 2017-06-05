package us.dot.its.jpo.ode.dds;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.oss.asn1.AbstractData;
import com.oss.asn1.Coder;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.asn1.j2735.J2735Util;
import us.dot.its.jpo.ode.j2735.J2735;
import us.dot.its.jpo.ode.j2735.dsrc.DDateTime;
import us.dot.its.jpo.ode.j2735.dsrc.DDay;
import us.dot.its.jpo.ode.j2735.dsrc.DHour;
import us.dot.its.jpo.ode.j2735.dsrc.DMinute;
import us.dot.its.jpo.ode.j2735.dsrc.DMonth;
import us.dot.its.jpo.ode.j2735.dsrc.DOffset;
import us.dot.its.jpo.ode.j2735.dsrc.DSecond;
import us.dot.its.jpo.ode.j2735.dsrc.DYear;
import us.dot.its.jpo.ode.j2735.dsrc.TemporaryID;
import us.dot.its.jpo.ode.j2735.semi.ConnectionPoint;
import us.dot.its.jpo.ode.j2735.semi.GroupID;
import us.dot.its.jpo.ode.j2735.semi.IPv4Address;
import us.dot.its.jpo.ode.j2735.semi.IPv6Address;
import us.dot.its.jpo.ode.j2735.semi.IpAddress;
import us.dot.its.jpo.ode.j2735.semi.PortNumber;
import us.dot.its.jpo.ode.j2735.semi.SemiDialogID;
import us.dot.its.jpo.ode.j2735.semi.SemiSequenceID;
import us.dot.its.jpo.ode.j2735.semi.ServiceRequest;
import us.dot.its.jpo.ode.j2735.semi.ServiceResponse;
import us.dot.its.jpo.ode.j2735.semi.Sha256Hash;

/*
 * This class receives service request from the OBU and forwards it to the SDC.
 * It also receives service response from SDC and forwards it back to the OBU.
 */
public class TrustManager implements Callable<ServiceResponse> {
	public class TrustManagerException extends Exception {

        private static final long serialVersionUID = 1L;

        public TrustManagerException(String string) {
            super(string);
        }

        public TrustManagerException(String string, Exception e) {
            super(string, e);
        }

    }

    private OdeProperties odeProperties;
	private static Coder coder = J2735.getPERUnalignedCoder();
	private Logger logger = LoggerFactory.getLogger(this.getClass());
    private DatagramSocket socket = null;

    private ExecutorService execService;
    private boolean trustEstablished = false;

	public TrustManager(OdeProperties odeProps, DatagramSocket socket) {
		this.odeProperties = odeProps;
		this.socket = socket;
		
        execService = Executors.newCachedThreadPool(Executors.defaultThreadFactory());
	}
	
	public ServiceRequest createServiceRequest(SemiDialogID dialogID) throws TrustManagerException {
	    GroupID groupID = new GroupID(OdeProperties.JPO_ODE_GROUP_ID);
	    Random randgen = new Random();
        TemporaryID requestID = new TemporaryID(
                ByteBuffer.allocate(4).putInt(randgen.nextInt(256)).array());

        ServiceRequest request = new ServiceRequest(
	            dialogID, SemiSequenceID.svcReq, groupID, requestID);
		IpAddress ipAddr = new IpAddress();
		if (!StringUtils.isEmpty(odeProperties.getExternalIpv4())) {
	        ipAddr.setIpv4Address(
	                new IPv4Address(J2735Util.ipToBytes(odeProperties.getExternalIpv4())));
		} else if (!StringUtils.isEmpty(odeProperties.getExternalIpv6())) {
            ipAddr.setIpv6Address(
                    new IPv6Address(J2735Util.ipToBytes(odeProperties.getExternalIpv6())));
        } else {
		    throw new TrustManagerException(
		            "Invlaid ode.externalIpv4 [" + odeProperties.getExternalIpv4() + 
		            "] and ode.externalIpv6 [" + odeProperties.getExternalIpv6()
		            + "] properties");
		}
		
		ConnectionPoint returnAddr = 
		        new ConnectionPoint(
		                ipAddr, 
		                new PortNumber(socket.getLocalPort()));
		request.setDestination(returnAddr);
		
		logger.debug("Response Destination {}:{}", returnAddr.getAddress().toString(), returnAddr.getPort().intValue());

		return request;
	}

	public ServiceResponse receiveServiceResponse() throws TrustManagerException {
        ServiceResponse servResponse = null;
		try {
			byte[] buffer = new byte[odeProperties.getServiceResponseBufferSize()];
			logger.debug("Waiting for ServiceResponse from SDC...");
			DatagramPacket responeDp = new DatagramPacket(buffer, buffer.length);
			socket.receive(responeDp);

			if (buffer.length <= 0)
				throw new TrustManagerException("Received empty service response");

			AbstractData response = J2735Util.decode(coder, buffer);
			logger.debug("Received ServiceResponse {}", response.toString());
			if (response instanceof ServiceResponse) {
				servResponse = (ServiceResponse) response;
				if (J2735Util.isExpired(servResponse.getExpiration())) {
	                throw new TrustManagerException("ServiceResponse Expired");
				}
				
				byte[] actualPacket = Arrays.copyOf(responeDp.getData(), responeDp.getLength());
				logger.debug("\nServiceResponse in hex: \n{}\n", Hex.encodeHexString(actualPacket));
			}
		} catch (Exception e) {
			throw new TrustManagerException("Error Receiving Service Response", e);
		}
		
        return servResponse;
	}

	public ServiceResponse createServiceResponse(ServiceRequest request) {
        ServiceResponse response = new ServiceResponse();
        response.setDialogID(request.getDialogID());
        
        ZonedDateTime expiresAt = ZonedDateTime.now().plusSeconds(odeProperties.getServiceRespExpirationSeconds());
        response.setExpiration(new DDateTime(
                new DYear(expiresAt.getYear()), 
                new DMonth(expiresAt.getMonthValue()), 
                new DDay(expiresAt.getDayOfMonth()), 
                new DHour(expiresAt.getHour()), 
                new DMinute(expiresAt.getMinute()), 
                new DSecond(expiresAt.getSecond()),
                new DOffset(0)));
        
        response.setGroupID(request.getGroupID());
        response.setRequestID(request.getRequestID());
        response.setSeqID(SemiSequenceID.svcResp);
        
        response.setHash(new Sha256Hash(ByteBuffer.allocate(32).putInt(1).array()));
        return response;
	}

    public void sendServiceResponse(ServiceResponse response, String ip, int port) {
        try {
            logger.debug("Sending ServiceResponse {} to {}:{}",
                    response.toString(),
                    ip,
                    port);
            
            byte[] responseBytes = J2735Util.encode(coder, response);
            socket.send(new DatagramPacket(responseBytes, responseBytes.length,
                    new InetSocketAddress(ip, port)));
        } catch (IOException e) {
            logger.error("Error Sending ServiceResponse", e);
        }
    }

    public void sendServiceRequest(ServiceRequest request, String ip, int port) {
        try {
            trustEstablished = false;
            logger.debug("Sending ServiceRequest {} to {}:{}",
                    request.toString(),
                    ip,
                    port);
            
            byte[] requestBytes = J2735Util.encode(coder, request);
            socket.send(new DatagramPacket(requestBytes,requestBytes.length,
                    new InetSocketAddress(ip, port)));
        } catch (IOException e) {
            logger.error("Error ServiceRequest", e);
        }
    }

    public boolean establishTrust(String ip, int port, SemiDialogID dialogId)
            throws SocketException, TrustManagerException {
        if (this.socket != null && !trustEstablished) {
            logger.debug("Closing outbound socket with port {}", port);
            socket.close();
            socket = null;
        }
        
        if (this.socket == null) {
            socket = new DatagramSocket(port);
        }
        
        // Launch a trust manager thread to listen for the service response
        Future<ServiceResponse> f = execService.submit(this);
        
        ServiceRequest request = createServiceRequest(dialogId);
        // send the service request
        this.sendServiceRequest(request, ip, port);
        
        // Wait for service response
        try {
            ServiceResponse response = f.get(
                    odeProperties.getServiceRespExpirationSeconds(), 
                    TimeUnit.SECONDS);
            
            logger.info("Received ServiceResponse {}", response.toString());
            if (response.getRequestID().equals(request.getRequestID())) {
                trustEstablished = true;
            }
                
        } catch (Exception e) {
            throw new TrustManagerException("Did not receive Service Response within alotted " + 
                    + odeProperties.getServiceRespExpirationSeconds() +
                    " seconds", e);
        }
        return trustEstablished;
    }


    @Override
    public ServiceResponse call() throws Exception {
        return receiveServiceResponse();
    }

    public boolean isTrustEstablished() {
        return trustEstablished;
    }

    public void setTrustEstablished(boolean trustEstablished) {
        this.trustEstablished = trustEstablished;
    }
    
    
}
