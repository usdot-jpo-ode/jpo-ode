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

import gov.usdot.asn1.j2735.CVSampleMessageBuilder;
import gov.usdot.asn1.j2735.CVTypeHelper;
import gov.usdot.asn1.j2735.J2735Util;
import us.dot.its.jpo.ode.j2735.J2735;
import us.dot.its.jpo.ode.j2735.semi.ServiceRequest;
import us.dot.its.jpo.ode.j2735.semi.ServiceResponse;
import us.dot.its.jpo.ode.j2735.semi.VehSitDataMessage;
import us.dot.its.jpo.ode.j2735.semi.VsmType;

public class VsdmDepositor {
	private static final String DEFAULT_TARGET_HOST = "127.0.0.1";
	private static final int DEFAULT_TARGET_PORT = 4446;
	private static final int DEFAULT_SELF_PORT = 4444;
	private static final int DEFAULT_TIMEOUT = 5000;
	private static final int DEFAULT_BUFFER_LENGTH = 10000;
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private static Coder coder;
	private String targetHost;
	private int targetPort;
	private int selfPort;
	private DatagramSocket socket = null;

	public VsdmDepositor() {
		this(DEFAULT_TARGET_HOST, DEFAULT_TARGET_PORT, DEFAULT_SELF_PORT);
	}

	public VsdmDepositor(String targetHost, int targetPort, int selfPort) {
		this.setTargetHost(targetHost);
		this.setTargetPort(targetPort);
		this.setSelfPort(selfPort);
		coder = J2735.getPERUnalignedCoder();
		try {
			socket = new DatagramSocket(selfPort);
			socket.setSoTimeout(DEFAULT_TIMEOUT);
		} catch (SocketException e) {
			logger.error("ODE: Error creating socket", e);
		}
	}

	public String getTargetHost() {
		return targetHost;
	}

	public void setTargetHost(String targetHost) {
		this.targetHost = targetHost;
	}

	public int getTargetPort() {
		return targetPort;
	}

	public void setTargetPort(int targetPort) {
		this.targetPort = targetPort;
	}

	public int getSourcePort() {
		return selfPort;
	}

	public void setSelfPort(int sourcePort) {
		this.selfPort = sourcePort;
	}

	public void depositVsdm() {
		logger.info("ODE: Initializing VSD deposit to SDC ...");
		sendVsdServiceRequest();
		receiveVsdServiceResponse();
		sendVsdMessage();

		if (socket != null){
			logger.info("Closing VsdmDepositor Socket");
			socket.close();
		}	
	}

	private void sendVsdServiceRequest() {
		ServiceRequest sr = CVSampleMessageBuilder.buildVehicleSituationDataServiceRequest();
		ByteArrayOutputStream sink = new ByteArrayOutputStream();
		try {
			coder.encode(sr, sink);
			byte[] payload = sink.toByteArray();
			logger.info("ODE: Sending VSD Deposit ServiceRequest ...");
			socket.send(new DatagramPacket(payload, payload.length, new InetSocketAddress(targetHost, targetPort)));
		} catch (EncodeFailedException | EncodeNotSupportedException e) {
			logger.error("ODE: Error Encoding VSD Deposit ServiceRequest", e);
		} catch (IOException e) {
			logger.error("ODE: Error Sending VSD Deposit ServiceRequest", e);
		} 
	}
	
	private void receiveVsdServiceResponse(){
		try {
			byte[] buffer = new byte[DEFAULT_BUFFER_LENGTH];
			logger.info("ODE: Waiting for VSD deposit ServiceResponse...");
			DatagramPacket responeDp = new DatagramPacket(buffer, buffer.length);
			socket.receive(responeDp);

			logger.info("ODE: Received VSD Deposit ServiceResponse");
			if (buffer.length > 0) {
				AbstractData response = J2735Util.decode(coder, buffer);
				if (response instanceof ServiceResponse) {
					logger.info("ODE: Printing VSD Deposit ServiceResponse {}", response.toString());
				}
			}
		} catch (Exception e) {
			logger.error("ODE: Failed to receive VSD Deposit ServiceResponse", e);
		}
	}
	
	private void sendVsdMessage(){
		logger.info("ODE: Preparing VSD message deposit...");
		VehSitDataMessage vsdm;
		try {
			vsdm = CVSampleMessageBuilder.buildVehSitDataMessage();
			VsmType vsmType = new VsmType(CVTypeHelper.VsmType.VEHSTAT.arrayValue());
			vsdm.setType(vsmType);
			byte[] encodedMsg = CVSampleMessageBuilder.messageToEncodedBytes(vsdm);
			logger.info("ODE: Sending VSD message to SDC...");
			socket.send(
					new DatagramPacket(encodedMsg, encodedMsg.length, new InetSocketAddress(targetHost, targetPort)));
		} catch ( EncodeFailedException | EncodeNotSupportedException e) {
			logger.error("ODE: Failed to encode VSD Message", e);
		} catch (IOException e) {
			logger.error("ODE: Failed to send VSD Message", e);
		}
	}
}
