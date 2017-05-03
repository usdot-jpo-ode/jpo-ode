package us.dot.its.jpo.ode.vsdm;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oss.asn1.AbstractData;
import com.oss.asn1.Coder;
import com.oss.asn1.DecodeFailedException;
import com.oss.asn1.DecodeNotSupportedException;
import com.oss.asn1.EncodeFailedException;
import com.oss.asn1.EncodeNotSupportedException;

import gov.usdot.asn1.j2735.CVSampleMessageBuilder;
import gov.usdot.asn1.j2735.J2735Util;
import us.dot.its.jpo.ode.j2735.J2735;
import us.dot.its.jpo.ode.j2735.semi.ServiceRequest;
import us.dot.its.jpo.ode.j2735.semi.ServiceResponse;
import us.dot.its.jpo.ode.j2735.semi.VehSitDataMessage;

public class VsdmReceiver implements Runnable {
	private static final int DEFAULT_SELF_PORT = 4446;
	private static final int DEFAULT_BUFFER_LENGTH = 10000;

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private DatagramSocket socket = null;
	private static Coder coder;
	private int selfPort;

	public VsdmReceiver() {
		this(DEFAULT_SELF_PORT);
	}

	public VsdmReceiver(int port) {
		this.setSelfPort(port);
		coder = J2735.getPERUnalignedCoder();
		try {
			socket = new DatagramSocket(port);
			logger.info("SDC: Listening on port {}...", selfPort);
		} catch (SocketException e) {
			logger.error("SDC: Error creating socket", e);
		}
	}
	
	public int getSelfPort() {
		return selfPort;
	}

	public void setSelfPort(int selfPort) {
		this.selfPort = selfPort;
	}

	@Override
	public void run() {
		handleVsdServiceRequest();
		receiveVsdMessage();

		if (socket != null){
			logger.info("Closing VsdmReceiver Socket");
			socket.close();
		}	
	}

	private void handleVsdServiceRequest() {
		byte[] buffer = new byte[DEFAULT_BUFFER_LENGTH];
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
		try {
			logger.info("SDC: Waiting for VSD Deposit ServiceRequest ...");
			socket.receive(packet);
			logger.info("SDC: Received VSD deposit ServiceRequest ...");
			if (buffer.length > 0) {
				AbstractData request = J2735Util.decode(coder, buffer);
				if (request instanceof ServiceRequest) {
					logger.info("SDC: Printing VSD deposit ServiceRequest {}", request.toString());
				}
			}
		} catch (IOException e) {
			logger.error("SDC: Error receiving VSD Deposit ServiceRequest", e);
		} catch (DecodeFailedException | DecodeNotSupportedException e) {
			logger.error("SDC: Error decoding VSD Deposit ServiceRequest", e);
		}
		
		sendVsdServiceResponse(packet.getAddress(), packet.getPort());
	}
	
	private void sendVsdServiceResponse(InetAddress address, int port){
		ServiceResponse sr = CVSampleMessageBuilder.buildVehicleSituationDataServiceResponse();

		ByteArrayOutputStream sink = new ByteArrayOutputStream();
		try {
			coder.encode(sr, sink);
			byte[] payload = sink.toByteArray();
			int length = payload.length;

			logger.info("SDC: Sending VSD deposit ServiceResponse ...");
			socket.send(new DatagramPacket(payload, length, new InetSocketAddress(address, port)));
		} catch (EncodeFailedException | EncodeNotSupportedException e) {
			logger.error("SDC: Error encoding VSD Deposit ServiceResponse", e);
		} catch (IOException e) {
			logger.error("SDC: Error sending VSD Deposit ServiceResponse", e);
		}
	}
	
	private void receiveVsdMessage(){
		try {
			byte[] vsdBuffer = new byte[DEFAULT_BUFFER_LENGTH];
			DatagramPacket packet = new DatagramPacket(vsdBuffer, vsdBuffer.length);
			
			logger.info("SDC: Waiting for VSD message...");
			socket.receive(packet);
			logger.info("SDC: Received VSD message...");
			if (vsdBuffer.length > 0) {
				AbstractData vsd = J2735Util.decode(coder, vsdBuffer);
				if (vsd instanceof VehSitDataMessage) {
					logger.info("SDC: Printing VSD message {}", vsd.toString());
				}
			}
		} catch (DecodeFailedException | DecodeNotSupportedException e) {
			logger.error("SDC: Error decoding VSD message", e);
		} catch(IOException e){
			logger.error("SDC: Error receiving VSD message", e);
		}
	}
}