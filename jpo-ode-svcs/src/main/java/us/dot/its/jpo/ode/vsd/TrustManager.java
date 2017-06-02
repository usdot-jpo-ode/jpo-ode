package us.dot.its.jpo.ode.vsd;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.time.ZonedDateTime;
import java.util.Arrays;
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
import us.dot.its.jpo.ode.j2735.semi.ConnectionPoint;
import us.dot.its.jpo.ode.j2735.semi.IPv4Address;
import us.dot.its.jpo.ode.j2735.semi.IPv6Address;
import us.dot.its.jpo.ode.j2735.semi.IpAddress;
import us.dot.its.jpo.ode.j2735.semi.PortNumber;
import us.dot.its.jpo.ode.j2735.semi.ServiceRequest;
import us.dot.its.jpo.ode.j2735.semi.ServiceResponse;

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
    private DatagramSocket inboundSocket = null;
    private DatagramSocket outboundSocket = null;

    private ExecutorService execService;
    private boolean trustEstablished;

	public TrustManager(OdeProperties odeProps) {
		this.odeProperties = odeProps;
		try {
			inboundSocket = new DatagramSocket(odeProps.getVsdReceiverPort());
			logger.info("Created inbound socket on port {}", odeProps.getVsdReceiverPort());
		} catch (SocketException e) {
			logger.error("Error creating socket with port " + odeProps.getVsdReceiverPort(), e);
		}
		
        try {
            outboundSocket = new DatagramSocket(odeProps.getResponseReceiverPort());
            logger.info("Created inbound socket on port {}", odeProps.getResponseReceiverPort());
        } catch (SocketException e) {
            logger.error("Error creating socket with port " + odeProps.getResponseReceiverPort(), e);
        }

        execService = Executors.newCachedThreadPool(Executors.defaultThreadFactory());
	}
	
	public ServiceRequest createServiceRequest() throws TrustManagerException {
	    ServiceRequest request = new ServiceRequest();
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
		                new PortNumber(odeProperties.getResponseReceiverPort()));
		request.setDestination(returnAddr);
		
		logger.debug("Response Destination {}:{}", ipAddr.toString(), odeProperties.getResponseReceiverPort());

		return request;
	}

	public ServiceResponse receiveServiceResponse() throws TrustManagerException {
        ServiceResponse servResponse = null;
		try {
			byte[] buffer = new byte[odeProperties.getVsdBufferSize()];
			logger.debug("Waiting for ServiceResponse from SDC...");
			DatagramPacket responeDp = new DatagramPacket(buffer, buffer.length);
			outboundSocket.receive(responeDp);

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
        response.dialogID = request.getDialogID();
        
        ZonedDateTime expiresAt = ZonedDateTime.now().plusSeconds(odeProperties.getServiceRespExpirationSeconds());
        response.expiration = new DDateTime(
                new DYear(expiresAt.getYear()), 
                new DMonth(expiresAt.getMonthValue()), 
                new DDay(expiresAt.getDayOfMonth()), 
                new DHour(expiresAt.getHour()), 
                new DMinute(expiresAt.getMinute()), 
                new DSecond(expiresAt.getSecond()),
                new DOffset(0));
        
        response.groupID = request.getGroupID();
        response.requestID = request.getRequestID();
        response.seqID = request.getSeqID();
        response.hash = response.getHash();
        return response;
	}

    public void sendServiceResponse(ServiceResponse response, String ip, int port) {
        try {
            logger.debug("Sending ServiceResponse {} to {}:{}",
                    response.toString(),
                    ip,
                    port);
            
            byte[] responseBytes = J2735Util.encode(coder, response);
            inboundSocket.send(new DatagramPacket(responseBytes, responseBytes.length,
                    new InetSocketAddress(ip, port)));
        } catch (IOException e) {
            logger.error("Error Sending ServiceResponse", e);
        }
    }

    public void sendServiceRequest(ServiceRequest request, String ip, int port) {
        try {
            logger.debug("Sending ServiceRequest {} to {}:{}",
                    request.toString(),
                    ip,
                    port);
            
            byte[] requestBytes = J2735Util.encode(coder, request);
            inboundSocket.send(new DatagramPacket(requestBytes,requestBytes.length,
                    new InetSocketAddress(ip, port)));
        } catch (IOException e) {
            logger.error("Error ServiceRequest", e);
        }
    }

    public boolean establishTrust(String ip, int port)
            throws SocketException, TrustManagerException {
        if (this.outboundSocket != null && !trustEstablished) {
            logger.debug("Closing outbound socket with port {}", 
                    odeProperties.getVsdSenderPort());
            outboundSocket.close();
            outboundSocket = null;
        }
        
        if (this.outboundSocket == null) {
            outboundSocket = new DatagramSocket(odeProperties.getVsdSenderPort());
        }
        
        // Launch a trust manager thread to listen for the service response
        Future<ServiceResponse> f = execService.submit(this);
        
        ServiceRequest request = createServiceRequest();
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
