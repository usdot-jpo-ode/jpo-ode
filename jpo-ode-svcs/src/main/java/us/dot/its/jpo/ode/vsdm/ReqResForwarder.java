package us.dot.its.jpo.ode.vsdm;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oss.asn1.AbstractData;
import com.oss.asn1.Coder;
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
			socket = new DatagramSocket(odeProps.getServiceRequestSenderPort());
			logger.info("ODE: Created depositor Socket with port " + odeProps.getServiceRequestSenderPort());
		} catch (SocketException e) {
			logger.error("ODE: Error creating socket with port " + odeProps.getServiceRequestSenderPort(), e);
		}
	}

	public byte[] createRequest(ServiceRequest request) {
		IpAddress ipAddr = new IpAddress();
		ipAddr.setIpv4Address(new IPv4Address(J2735Util.ipToBytes(odeProps.getReturnIp())));
		ConnectionPoint newReturnAddr = new ConnectionPoint(ipAddr, new PortNumber(odeProps.getReturnPort()));

		if (request.hasDestination()) {
			logger.info("Received Service Request contains destination field");
			logger.info("Old destination IP: {} Source Port: {}", this.obuReturnAddr, this.obuReturnPort);
			byte[] ipBytes = request.getDestination().getAddress().getIpv4Address().byteArrayValue();
			this.obuReturnAddr = J2735Util.ipToString(ipBytes);
			this.obuReturnPort = request.getDestination().getPort().intValue();
			logger.info("New destination IP: {} Source Port: {}", this.obuReturnAddr, this.obuReturnPort);
		}

		request.setDestination(newReturnAddr);

		ByteArrayOutputStream sink = new ByteArrayOutputStream();
		try {
			coder.encode(request, sink);
		} catch (EncodeFailedException | EncodeNotSupportedException e) {
			logger.error("ODE: Error Encoding VSD Deposit ServiceRequest", e);
		}

		return sink.toByteArray();
	}

	public void send() {
		try {
			logger.info("ODE: Sending VSD Deposit ServiceRequest to IP: {} Port: {}", odeProps.getSdcIp(),
					odeProps.getSdcPort());
			socket.send(new DatagramPacket(payload, payload.length,
					new InetSocketAddress(odeProps.getSdcIp(), odeProps.getSdcPort())));
		} catch (IOException e) {
			logger.error("ODE: Error Sending VSD Deposit ServiceRequest", e);
		}
	}

	public ServiceResponse receiveVsdServiceResponse() {
		try {
			byte[] buffer = new byte[odeProps.getVsdmBufferSize()];
			logger.info("ODE: Waiting for VSD deposit ServiceResponse...");
			DatagramPacket responeDp = new DatagramPacket(buffer, buffer.length);
			socket.receive(responeDp);

			logger.info("ODE: Received VSD Deposit ServiceResponse");

			if (buffer.length <= 0)
				return null;

			AbstractData response = J2735Util.decode(coder, buffer);
			if (response instanceof ServiceResponse) {
				ServiceResponse servResponse = (ServiceResponse) response;
				if (J2735Util.isExpired(servResponse.getExpiration())) {
					logger.info("ODE: VSD Deposit ServiceResponse Expired");
					return null;
				}

				logger.info("ODE: Printing VSD Deposit ServiceResponse {}", response.toString());
				forwardServiceResponseToObu(buffer);
				return servResponse;
			}

		} catch (Exception e) {
			logger.error("ODE: Error Receiving VSD Deposit ServiceResponse", e);
		}
		return null;
	}

	public void forwardServiceResponseToObu(byte[] response) {
		try {
			logger.info("Obu IP: {} and ObuPort: {}", this.obuReturnAddr, this.obuReturnPort);
			logger.info("ODE: Sending VSD Deposit ServiceResponse to OBU ...");
			socket.send(new DatagramPacket(response, response.length,
					new InetSocketAddress(this.obuReturnAddr, this.obuReturnPort)));
		} catch (IOException e) {
			logger.error("ODE: Error Sending VSD Deposit ServiceRequest", e);
		}
	}

	@Override
	public void run() {
		send();
		receiveVsdServiceResponse();
		if (socket != null)
			socket.close();
	}
}
