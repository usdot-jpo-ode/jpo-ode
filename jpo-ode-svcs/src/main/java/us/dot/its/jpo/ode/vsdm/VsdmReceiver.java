package us.dot.its.jpo.ode.vsdm;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.oss.asn1.AbstractData;
import com.oss.asn1.Coder;
import com.oss.asn1.DecodeFailedException;
import com.oss.asn1.DecodeNotSupportedException;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.SerializableMessageProducerPool;
import us.dot.its.jpo.ode.asn1.j2735.J2735Util;
import us.dot.its.jpo.ode.j2735.J2735;
import us.dot.its.jpo.ode.j2735.dsrc.BasicSafetyMessage;
import us.dot.its.jpo.ode.j2735.semi.ServiceRequest;
import us.dot.its.jpo.ode.j2735.semi.ServiceResponse;
import us.dot.its.jpo.ode.j2735.semi.VehSitDataMessage;
import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;
import us.dot.its.jpo.ode.plugin.j2735.oss.OssBsm;
import us.dot.its.jpo.ode.plugin.j2735.oss.OssBsmPart2Content.OssBsmPart2Exception;
import us.dot.its.jpo.ode.util.JsonUtils;
import us.dot.its.jpo.ode.wrapper.MessageProducer;

public class VsdmReceiver implements Runnable {

	private static Logger logger = LoggerFactory.getLogger(VsdmReceiver.class);
	private static Coder coder = J2735.getPERUnalignedCoder();

	private DatagramSocket socket;

	private OdeProperties odeProperties;

	private SerializableMessageProducerPool<String, byte[]> messageProducerPool;

	@Autowired
	public VsdmReceiver(OdeProperties odeProps) {
		
		this.odeProperties = odeProps;

		try {
			socket = new DatagramSocket(odeProperties.getReceiverPort());
			logger.info("[VSDM Receiver] Created UDP socket bound to port ", odeProperties.getReceiverPort());
		} catch (SocketException e) {
			logger.error("[VSDM Receiver] Error creating socket with port ", odeProperties.getReceiverPort(), e);
		}
		
		messageProducerPool = new SerializableMessageProducerPool<>(odeProperties);
	}

	@Override
	public void run() {

		logger.info("Vsdm Receiver Service started.");

		byte[] buffer = new byte[odeProperties.getVsdmBufferSize()];

		DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

		Boolean stopped = false;
		while (!stopped) {

			try {
				logger.info("VSDM RECEIVER - Waiting for UDP packets...");
				socket.receive(packet);
				logger.info("VSDM RECEIVER - Packet received.");
				String obuIp = packet.getAddress().getHostAddress();
				int obuPort = packet.getPort();

				if (buffer.length > 0) {
					logger.info("VSDM RECEIVER - Received data:", buffer);
					decodeData(buffer, obuIp, obuPort);
				}
			} catch (IOException e) {
				logger.error("VSDM RECEIVER - Error receiving UDP packet", e);
				stopped = true;
			}
		}
	}

	private void decodeData(byte[] msg, String obuIp, int obuPort) {
		try {
			AbstractData decoded = J2735Util.decode(coder, msg);
			logger.info("VSDM RECEIVER - Decoded the message");
			if (decoded instanceof ServiceRequest) {
				logger.info("VSDM RECEIVER - Received ServiceRequest: ", decoded.toString());
				ServiceRequest request = (ServiceRequest) decoded;
				VsdmDepositorAgent depositorAgent = new VsdmDepositorAgent(odeProperties, request, obuIp, obuPort);
				Thread depositorAgentThread = new Thread(depositorAgent, "VsdmDepositorAgentThread");
				depositorAgentThread.start();
			} else if (decoded instanceof VehSitDataMessage) {
				logger.info("VSDM RECEIVER - Received VSDM, publishing to Kafka topic");
				publishVsdm(msg);
				extractAndPublishBsms((VehSitDataMessage) decoded);
			} else {
				logger.error("[VSDM Receiver] Error, unknown message type received.");
			}
		} catch (DecodeFailedException | DecodeNotSupportedException e) {
			logger.error("[VSDM Receiver] Error, unable to decode UDP message", e);
		}

	}
	
	private void extractAndPublishBsms(VehSitDataMessage msg) {
		
		List<BasicSafetyMessage> bsmList = null;
		try {
			 bsmList = VsdToBsmConverter.convert(msg);
		} catch (Exception e) {
			logger.error("VSDM RECEIVER - Unable to convert VehSitDataMessage bundle to BSM list.", e);
			return;
		}
		
		for (BasicSafetyMessage entry : bsmList) {
			try {
				J2735Bsm convertedBsm = OssBsm.genericBsm(entry);
				String bsmJson = JsonUtils.toJson(convertedBsm, odeProperties.getVsdmVerboseJson());
				logger.debug("VSDM RECEIVER - Published BSM to kafka: {}", bsmJson);
				publishBsm(bsmJson);
			} catch (OssBsmPart2Exception e) {
				logger.error("[VSDM Receiver] Error, unable to convert BSM: ", e);
			}
		}
	}

	private void publishBsm(String msg) {
		MessageProducer
				.defaultStringMessageProducer(odeProperties.getKafkaBrokers(), odeProperties.getKafkaProducerType())
				.send(odeProperties.getKafkaTopicBsmJSON(), null, msg);
		logger.info("VSDM RECEIVER - Published bsm to kafka...");
	}

	private void publishVsdm(byte[] data) {
		MessageProducer<String, byte[]> producer = messageProducerPool.checkOut();
		producer.send(odeProperties.getKafkaTopicVsdm(), null, data);
		messageProducerPool.checkIn(producer);
		logger.info("VSDM RECEIVER - Published vsd to kafka...");
	}

}
