package us.dot.its.jpo.ode.bsm;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.codec.binary.Hex;
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
import us.dot.its.jpo.ode.coder.BsmCoder;
import us.dot.its.jpo.ode.j2735.J2735;
import us.dot.its.jpo.ode.j2735.dsrc.BasicSafetyMessage;
import us.dot.its.jpo.ode.j2735.semi.VehSitDataMessage;
import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;
import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;
import us.dot.its.jpo.ode.plugin.j2735.oss.OssAsn1Coder;
import us.dot.its.jpo.ode.plugin.j2735.oss.OssBsm;
import us.dot.its.jpo.ode.plugin.j2735.oss.OssBsmPart2Content.OssBsmPart2Exception;
import us.dot.its.jpo.ode.util.JsonUtils;
import us.dot.its.jpo.ode.util.SerializationUtils;
import us.dot.its.jpo.ode.wrapper.MessageProducer;

public class BsmReceiver implements Runnable {

	private static Logger logger = LoggerFactory.getLogger(BsmReceiver.class);

	private DatagramSocket socket;

	private OdeProperties odeProperties;

	private SerializableMessageProducerPool<String, byte[]> messageProducerPool;
	private MessageProducer<String, String> bsmProducer;
	private OssAsn1Coder asn1Coder;

	@Autowired
	public BsmReceiver(OdeProperties odeProps) {

		this.odeProperties = odeProps;
		asn1Coder = new OssAsn1Coder();

		try {
			socket = new DatagramSocket(odeProperties.getBsmReceiverPort());
			logger.info("Created UDP socket bound to port {}", odeProperties.getBsmReceiverPort());
		} catch (SocketException e) {
			logger.error("Error creating socket with port " + odeProperties.getBsmReceiverPort(), e);
		}

		messageProducerPool = new SerializableMessageProducerPool<>(odeProperties);

		// Create a String producer for hex BSMs
		bsmProducer = MessageProducer.defaultStringMessageProducer(odeProperties.getKafkaBrokers(),
				odeProperties.getKafkaProducerType());
	}

	@Override
	public void run() {

		logger.debug("Bsm Receiver Service started.");

		byte[] buffer = new byte[odeProperties.getBsmBufferSize()];

		DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

		boolean stopped = false;
		while (!stopped) {
			try {
				logger.debug("Waiting for UDP packets...");
				socket.receive(packet);
				logger.debug("Packet received.");

				// extract the actualPacket from the buffer
				byte[] actualPacket = Arrays.copyOf(packet.getData(), packet.getLength());
				if (packet.getLength() > 0) {
					logger.debug("\nReceived packet in hex: \n{}\n", Hex.encodeHexString(actualPacket));
					decodeData(actualPacket);
				}
			} catch (IOException e) {
				logger.error("Error receiving packet", e);
			}
		}
	}

	private void decodeData(byte[] msg) {
		Asn1Object decoded = asn1Coder.decodeUPERBsmBytes(msg);
		if (decoded instanceof J2735Bsm) {
			logger.debug("Received BSM");
			publishBsms((J2735Bsm)decoded);
		} else {
			logger.error("Unknown message type received {}", decoded.getClass().getName());
		}
	}

	private void publishBsms(J2735Bsm bsm) {
		logger.debug("Publishing j2735 bsm");
		publishBsm(bsm);

		String bsmJson = JsonUtils.toJson(bsm, odeProperties.getVsdmVerboseJson());
		publishBsm(bsmJson);
		logger.debug("Published bsm to the topics {} and {}", odeProperties.getKafkaTopicBsmSerializedPojo(),
				odeProperties.getKafkaTopicBsmRawJson());
	}

	public void publishBsm(String msg) {
		bsmProducer.send(odeProperties.getKafkaTopicBsmRawJson(), null, msg);
	}

	public void publishBsm(Asn1Object msg) {
		MessageProducer<String, byte[]> producer = messageProducerPool.checkOut();
		producer.send(odeProperties.getKafkaTopicBsmSerializedPojo(), null,
				new SerializationUtils<J2735Bsm>().serialize((J2735Bsm) msg));
		messageProducerPool.checkIn(producer);
	}
}
