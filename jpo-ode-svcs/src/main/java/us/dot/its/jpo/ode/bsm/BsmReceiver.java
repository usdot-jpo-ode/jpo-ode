package us.dot.its.jpo.ode.bsm;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Arrays;

import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;
import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;
import us.dot.its.jpo.ode.plugin.j2735.oss.OssAsn1Coder;
import us.dot.its.jpo.ode.util.JsonUtils;
import us.dot.its.jpo.ode.util.SerializationUtils;
import us.dot.its.jpo.ode.wrapper.MessageProducer;

public class BsmReceiver implements Runnable {

	private static Logger logger = LoggerFactory.getLogger(BsmReceiver.class);

	private DatagramSocket socket;

	private OdeProperties odeProperties;

    private MessageProducer<String, byte[]> byteArrayProducer;
    private MessageProducer<String, String> stringProducer;
	private OssAsn1Coder asn1Coder;
	private boolean stopped = false;

	@Autowired
	public BsmReceiver(OdeProperties odeProps) {

		this.odeProperties = odeProps;
		asn1Coder = new OssAsn1Coder();

		try {
			socket = new DatagramSocket(odeProperties.getBsmReceiverPort());
			logger.debug("Created UDP socket bound to port {}", odeProperties.getBsmReceiverPort());
		} catch (SocketException e) {
			logger.error("Error creating socket with port {}", odeProperties.getBsmReceiverPort(), e);
		}

        // Create a String producer for hex BSMs
        stringProducer = MessageProducer.defaultStringMessageProducer(odeProperties.getKafkaBrokers(),
                odeProperties.getKafkaProducerType());

        // Create a ByteArray producer for UPER BSMs and VSDs
        byteArrayProducer = MessageProducer.defaultByteArrayMessageProducer(odeProperties.getKafkaBrokers(),
                odeProperties.getKafkaProducerType());
	}
	
	public void setStopped(boolean val){
		this.stopped = val;
	}

	@Override
	public void run() {

		logger.debug("Bsm Receiver Service started.");

		byte[] buffer = new byte[odeProperties.getBsmBufferSize()];

		DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

		do {
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
		}while (!stopped);
	}

	public void decodeData(byte[] msg) {
		Asn1Object decoded = asn1Coder.decodeUPERBsmBytes(msg);
		if (decoded instanceof J2735Bsm) {
			logger.debug("Received BSM");
			publishBsms((J2735Bsm) decoded);
		} else {
			logger.error("Unknown message type received {}", decoded.getClass().getName());
		}
	}

	private void publishBsms(J2735Bsm bsm) {
		logger.debug("Publishing j2735 bsm");
		publishBsm(bsm);

		String bsmJson = JsonUtils.toJson(bsm, odeProperties.getVerboseJson());
        logger.debug("Published bsm to the topics {} and {}", 
                odeProperties.getKafkaTopicBsmSerializedPojo(),
                odeProperties.getKafkaTopicBsmRawJson());
		publishBsm(bsmJson);
	}

	private void publishBsm(String msg) {
        stringProducer.send(odeProperties.getKafkaTopicBsmRawJson(), null, msg);
	}

	private void publishBsm(Asn1Object msg) {
        logger.debug("Publishing BSM to topic {}", odeProperties.getKafkaTopicBsmSerializedPojo());
        byteArrayProducer.send(odeProperties.getKafkaTopicBsmSerializedPojo(), null,
                new SerializationUtils<J2735Bsm>().serialize((J2735Bsm) msg));
	}
}
