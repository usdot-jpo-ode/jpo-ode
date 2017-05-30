package us.dot.its.jpo.ode.vsdm;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.Arrays;

import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oss.asn1.AbstractData;
import com.oss.asn1.Coder;
import com.oss.asn1.DecodeFailedException;
import com.oss.asn1.DecodeNotSupportedException;
import com.oss.asn1.EncodeFailedException;
import com.oss.asn1.EncodeNotSupportedException;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.asn1.j2735.J2735Util;
import us.dot.its.jpo.ode.j2735.J2735;
import us.dot.its.jpo.ode.j2735.semi.ConnectionPoint;
import us.dot.its.jpo.ode.j2735.semi.IPv4Address;
import us.dot.its.jpo.ode.j2735.semi.IpAddress;
import us.dot.its.jpo.ode.j2735.semi.PortNumber;
import us.dot.its.jpo.ode.j2735.semi.ServiceRequest;
import us.dot.its.jpo.ode.j2735.semi.ServiceResponse;

/*
 * This class receives service request from the OBU and forwards it to the SDC.
 * It also receives service response from SDC and forwards it back to the OBU.
 */
public class ReqResForwarder implements Runnable {
	private OdeProperties odeProps;
	private static Coder coder = J2735.getPERUnalignedCoder();
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private DatagramSocket socket = null;
	private byte[] payload;
	private String obuReturnAddr;
	private int obuReturnPort;


	public ReqResForwarder(OdeProperties odeProps, ServiceRequest request, String obuIp, int obuPort) {
		this.odeProps = odeProps;
		this.obuReturnAddr = obuIp;
		this.obuReturnPort = obuPort;
		this.payload = createRequest(request);
		try {
			socket = new DatagramSocket(odeProps.getForwarderPort());
			logger.debug("Created depositor Socket with port {}", odeProps.getForwarderPort());
		} catch (SocketException e) {
			logger.error("Error creating socket with port {} {}", odeProps.getForwarderPort(), e);
		}
	}
	
	public byte[] getPayload(){
		return this.payload;
	}

	public byte[] createRequest(ServiceRequest request) {
		IpAddress ipAddr = new IpAddress();
		ipAddr.setIpv4Address(new IPv4Address(J2735Util.ipToBytes(odeProps.getReturnIp())));
		ConnectionPoint newReturnAddr = new ConnectionPoint(ipAddr, new PortNumber(odeProps.getForwarderPort()));
		if (request.hasDestination()) {
			logger.debug("Service Request contains destination field");
			logger.debug("Old OBU destination IP: {} Source Port: {}", this.obuReturnAddr, this.obuReturnPort);
			if(request.getDestination().hasAddress()){
				byte[] ipBytes = null;
				if(request.getDestination().getAddress().hasIpv4Address())
					ipBytes = request.getDestination().getAddress().getIpv4Address().byteArrayValue();
				else if(request.getDestination().getAddress().hasIpv6Address())
					ipBytes = request.getDestination().getAddress().getIpv6Address().byteArrayValue();
				
				this.obuReturnAddr = J2735Util.ipToString(ipBytes);
			}
				
			this.obuReturnPort = request.getDestination().getPort().intValue();
			logger.debug("New OBU destination IP: {} Source Port: {}", this.obuReturnAddr, this.obuReturnPort);
		}

		request.setDestination(newReturnAddr);
		logger.debug("New ODE destination IP: {} Source Port: {}", odeProps.getReturnIp(), odeProps.getForwarderPort());

		ByteArrayOutputStream sink = new ByteArrayOutputStream();
		try {
			coder.encode(request, sink);
		} catch (EncodeFailedException | EncodeNotSupportedException e) {
			logger.error("Error Encoding VSD ServiceRequest {}", e);
		}

		return sink.toByteArray();
	}

	public void send() {
		try {
			logger.debug("\nModified ServiceRequest in hex: \n{}\n", Hex.encodeHexString(payload));
			logger.debug("Sending VSD ServiceRequest to SDC IP: {} Port: {}", odeProps.getSdcIp(),
					odeProps.getSdcPort());
			socket.send(new DatagramPacket(payload, payload.length,
					new InetSocketAddress(odeProps.getSdcIp(), odeProps.getSdcPort())));
		} catch (IOException e) {
			logger.error("Error Sending VSD ServiceRequest {}", e);
		}
	}

	public ServiceResponse receiveVsdServiceResponse() {
		try {
			byte[] buffer = new byte[odeProps.getVsdmBufferSize()];
			logger.debug("Waiting for VSD ServiceResponse from SDC...");
			DatagramPacket responeDp = new DatagramPacket(buffer, buffer.length);
			socket.receive(responeDp);

			if (buffer.length <= 0)
				return null;

			AbstractData response = J2735Util.decode(coder, buffer);
			logger.debug("Received VSD ServiceResponse {}", response.toString());
			if (response instanceof ServiceResponse) {
				ServiceResponse servResponse = (ServiceResponse) response;
				if (J2735Util.isExpired(servResponse.getExpiration())) {
					logger.info("VSD ServiceResponse Expired");
					return null;
				}
				byte[] actualPacket = Arrays.copyOf(responeDp.getData(), responeDp.getLength());
				logger.debug("\nServiceResponse in hex: \n{}\n", Hex.encodeHexString(actualPacket));
				forwardServiceResponseToObu(actualPacket);
				return servResponse;
			}

		} catch (IOException e) {
			logger.error("Error Receiving VSD Deposit ServiceResponse {}", e);
		} catch (DecodeFailedException | DecodeNotSupportedException e) {
			logger.error("Error Decoding VSD Deposit ServiceResponse {}", e);
		}
		return null;
	}

	public void forwardServiceResponseToObu(byte[] response) {
		try {
			logger.debug("Sending VSD ServiceResponse to OBU IP: {} and ObuPort: {}", this.obuReturnAddr, this.obuReturnPort);
			socket.send(new DatagramPacket(response, response.length,
					new InetSocketAddress(this.obuReturnAddr, this.obuReturnPort)));
		} catch (IOException e) {
			logger.error("Error Sending VSD ServiceResponse {}", e);
		}
	}

	@Override
	public void run() {
		send();
		receiveVsdServiceResponse();
		if (socket != null) {
			logger.debug("Closing forwarder socket with port {}", odeProps.getForwarderPort());
			socket.close();
		}
	}
}
