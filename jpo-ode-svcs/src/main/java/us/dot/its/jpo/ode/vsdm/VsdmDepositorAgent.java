package us.dot.its.jpo.ode.vsdm;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oss.asn1.AbstractData;
import com.oss.asn1.Coder;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.asn1.j2735.J2735Util;
import us.dot.its.jpo.ode.j2735.J2735;
import us.dot.its.jpo.ode.j2735.semi.ServiceResponse;

public class VsdmDepositorAgent {
	private int port;
	private OdeProperties odeProps;
	private static Coder coder = J2735.getPERUnalignedCoder();
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private DatagramSocket socket = null;
	
	public VsdmDepositorAgent(int port, OdeProperties odeProps){
		this.port = port;
		this.odeProps = odeProps;
		try {
			socket = new DatagramSocket(this.port);
			logger.info("ODE: Created depositor Socket with port " + this.port);
		} catch (SocketException e) {
			logger.error("ODE: Error creating socket with port " + this.port, e);
		}
	}
	
	public ServiceResponse deposit(byte[] payload){
		send(payload);
		return receiveVsdServiceResponse();
	}
	
	public void send(byte[] payload){
		try {
			logger.info("ODE: Sending VSD Deposit ServiceRequest ...");
			socket.send(new DatagramPacket(payload, payload.length, new InetSocketAddress(odeProps.getSdcIp(), odeProps.getSdcPort())));
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
				if (J2735Util.isExpired(servResponse.getExpiration())){
					logger.info("ODE: VSD Deposit ServiceResponse Expired");
					return null;
				}
					
				logger.info("ODE: Printing VSD Deposit ServiceResponse {}", response.toString());
				return servResponse;
			}

		} catch (Exception e) {
			logger.error("ODE: Error Receiving VSD Deposit ServiceResponse", e);
		}
		return null;
	}
	

}
