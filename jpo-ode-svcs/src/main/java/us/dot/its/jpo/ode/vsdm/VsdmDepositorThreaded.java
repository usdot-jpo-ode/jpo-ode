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

public class VsdmDepositorThreaded implements Runnable{

	@Override
	public void run() {
		String targetHost = "104.130.170.234";
		int targetPort = 46753;
		
		int vsdmSenderPort = 6666;
		VsdmSender vsdmSender = new VsdmSender(targetHost, targetPort, vsdmSenderPort);
		Thread vsdmSenderThread = new Thread(vsdmSender, "VsdmSenderThread");
		vsdmSenderThread.start();
		
		int srSenderPort = 5556;
		ServiceRequestSender serviceRequestSender = new ServiceRequestSender(targetHost, targetPort, srSenderPort);
		Thread serviceRequestSenderThread = new Thread(serviceRequestSender, "serviceRequestSenderThread");
		serviceRequestSenderThread.start();
		
		try {
			vsdmSenderThread.join();
			serviceRequestSenderThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}	
	}
}

class ServiceRequestSender implements Runnable{
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private DatagramSocket socket = null;
	private static Coder coder = J2735.getPERUnalignedCoder();
	private String targetHost;
	private int targetPort;
	private int selfPort;
	
	public ServiceRequestSender(String targetHost, int targetPort, int selfPort){
		this.targetHost = targetHost;
		this.targetPort = targetPort;
		this.selfPort = selfPort;
		coder = J2735.getPERUnalignedCoder();
		try {
			socket = new DatagramSocket(this.selfPort);
			logger.info("ODE: Created ServiceRequestSender Socket with port " + this.selfPort);
		} catch (SocketException e) {
			logger.error("ODE: Error creating socket with port " + this.selfPort, e);
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
		} catch (EncodeFailedException | EncodeNotSupportedException | IOException e) {
			logger.error("ODE: Error Sending VSD Deposit ServiceRequest", e);
		}
	}

	@Override
	public void run() {
		sendVsdServiceRequest();
		if(socket != null){
			logger.info("Closing ServiceRequestSender Socket with port " + this.selfPort);
			socket.close();
		}
			
	}
}

class VsdmSender implements Runnable{
	private static final String DEFAULT_TARGET_HOST = "127.0.0.1";
	private static final int DEFAULT_TARGET_PORT = 4446;
	private static final int DEFAULT_SELF_PORT = 4444;
	private static final int DEFAULT_TIMEOUT = 5000;
	private static final int DEFAULT_BUFFER_LENGTH = 10000;
	
	private static final double DEFAULT_LAT = 43.394444;	// Test Lat/Lon for VSD in Wyoming area
	private static final double DEFAULT_LON = -107.595;
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private static Coder coder;
	private String targetHost;
	private int targetPort;
	private int selfPort;
	private DatagramSocket socket = null;

	public VsdmSender() {
		this(DEFAULT_TARGET_HOST, DEFAULT_TARGET_PORT, DEFAULT_SELF_PORT);
	}

	public VsdmSender(String targetHost, int targetPort, int selfPort) {
		this.setTargetHost(targetHost);
		this.setTargetPort(targetPort);
		this.setSelfPort(selfPort);
		coder = J2735.getPERUnalignedCoder();
		try {
			socket = new DatagramSocket(selfPort);
			logger.info("ODE: Created VsdmSender Socket with port " + this.selfPort);
			socket.setSoTimeout(DEFAULT_TIMEOUT);
		} catch (SocketException e) {
			logger.error("ODE: Error creating socket with port " + this.selfPort, e);
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
		receiveVsdServiceResponse();
		sendVsdMessage();

		if (socket != null){
			logger.info("Closing VsdmSender Socket with port " + this.selfPort);
			socket.close();
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
			logger.error("ODE: Error Receiving VSD Deposit ServiceResponse", e);
		}
	}
	
	private void sendVsdMessage(){
		logger.info("ODE: Preparing VSD message deposit...");
		VehSitDataMessage vsdm;
		try {
			vsdm = CVSampleMessageBuilder.buildVehSitDataMessage(DEFAULT_LAT, DEFAULT_LON);
			VsmType vsmType = new VsmType(CVTypeHelper.VsmType.VEHSTAT.arrayValue());
			vsdm.setType(vsmType);
			byte[] encodedMsg = CVSampleMessageBuilder.messageToEncodedBytes(vsdm);
			logger.info("ODE: Sending VSD message to SDC...");
			socket.send(
					new DatagramPacket(encodedMsg, encodedMsg.length, new InetSocketAddress(targetHost, targetPort)));
			logger.info("ODE: Sent VSD message to SDC");
		} catch ( EncodeFailedException | EncodeNotSupportedException | IOException e) {
			logger.error("ODE: Error Sending VSD Message", e);
		}
	}

	@Override
	public void run() {
		depositVsdm();
	}
}
