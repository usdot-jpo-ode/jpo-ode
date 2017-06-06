package us.dot.its.jpo.ode.vsdm;

import com.oss.asn1.AbstractData;
import com.oss.asn1.Coder;
import com.oss.asn1.DecodeFailedException;
import com.oss.asn1.DecodeNotSupportedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.SerializableMessageProducerPool;
import us.dot.its.jpo.ode.asn1.j2735.J2735Util;
import us.dot.its.jpo.ode.isdm.IsdDepositor;
import us.dot.its.jpo.ode.j2735.J2735;
import us.dot.its.jpo.ode.j2735.dsrc.BasicSafetyMessage;
import us.dot.its.jpo.ode.j2735.semi.IntersectionSituationData;
import us.dot.its.jpo.ode.j2735.semi.ServiceRequest;
import us.dot.its.jpo.ode.j2735.semi.VehSitDataMessage;
import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;
import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;
import us.dot.its.jpo.ode.plugin.j2735.oss.OssBsm;
import us.dot.its.jpo.ode.plugin.j2735.oss.OssBsmPart2Content.OssBsmPart2Exception;
import us.dot.its.jpo.ode.util.JsonUtils;
import us.dot.its.jpo.ode.util.SerializationUtils;
import us.dot.its.jpo.ode.wrapper.MessageProducer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VsdmReceiver implements Runnable {

	private static Logger logger = LoggerFactory.getLogger(VsdmReceiver.class);
	private static Coder coder = J2735.getPERUnalignedCoder();

	private DatagramSocket socket;

	private OdeProperties odeProperties;

	private SerializableMessageProducerPool<String, byte[]> messageProducerPool;
	private MessageProducer<String, String> bsmProducer;
	private ExecutorService execService;

	@Autowired
	public VsdmReceiver(OdeProperties odeProps) {

		this.odeProperties = odeProps;

		try {
			socket = new DatagramSocket(odeProperties.getReceiverPort());
			logger.info("Created UDP socket bound to port {}", odeProperties.getReceiverPort());
		} catch (SocketException e) {
			logger.error("Error creating socket with port " + odeProperties.getReceiverPort(), e);
		}

		messageProducerPool = new SerializableMessageProducerPool<>(odeProperties);

		// Create a String producer for hex BSMs
		bsmProducer = MessageProducer.defaultStringMessageProducer(odeProperties.getKafkaBrokers(),
				odeProperties.getKafkaProducerType());

		this.execService = Executors.newCachedThreadPool(Executors.defaultThreadFactory());
	}

	@Override
	public void run() {

		logger.debug("Vsdm Receiver Service started.");

		byte[] buffer = new byte[odeProperties.getVsdmBufferSize()];

		DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

		Boolean stopped = false;
		while (!stopped) {

			try {
				logger.debug("Waiting for UDP packets...");
				socket.receive(packet);
				logger.debug("Packet received.");
				String obuIp = packet.getAddress().getHostAddress();
				int obuPort = packet.getPort();
				
				//extract the actualPacket from the buffer
				byte[] actualPacket = Arrays.copyOf(packet.getData(), packet.getLength());
				if (packet.getLength() > 0) {
					decodeData(actualPacket, obuIp, obuPort);
				}
			} catch (IOException e) {
				logger.error("Error receiving packet", e);
//				stopped = true;
			}
		}
	}

	private void decodeData(byte[] msg, String obuIp, int obuPort) {
		try {
			AbstractData decoded = J2735Util.decode(coder, msg);
			logger.debug("good here");
			if (decoded instanceof ServiceRequest) {
				logger.debug("Received ServiceRequest:\n{} \n", decoded.toString());
				ServiceRequest request = (ServiceRequest) decoded;
				ReqResForwarder forwarder = new ReqResForwarder(odeProperties, request, obuIp, obuPort);
				execService.submit(forwarder);
			} else if (decoded instanceof VehSitDataMessage) {
				logger.debug("Received VSD and forwarding it to SDC");
				VsdDepositor depositor = new VsdDepositor(odeProperties, msg);

				execService.submit(depositor);

				publishVsdm(msg);

				extractAndPublishBsms((VehSitDataMessage) decoded);
			}else if (decoded instanceof IntersectionSituationData){
				logger.debug("Received ISD and forwarding it to SDC");
				IsdDepositor isdDepositor = new IsdDepositor(odeProperties, msg);

				execService.submit(isdDepositor);

//				publishVsdm(msg);

//				extractAndPublishBsms((VehSitDataMessage) decoded);
			} else {
				logger.error("Unknown message type received {}", decoded.getClass().getName());
			}
		} catch (DecodeFailedException | DecodeNotSupportedException e) {
			logger.error("Unable to decode UDP message", e);
		}
	}

	private void extractAndPublishBsms(VehSitDataMessage msg) {
		List<BasicSafetyMessage> bsmList = null;
		try {
			bsmList = VsdToBsmConverter.convert(msg);
		} catch (IllegalArgumentException e) {
			logger.error("Unable to convert VehSitDataMessage bundle to BSM list", e);
			return;
		}

		int i = 1;
		for (BasicSafetyMessage entry : bsmList) {
			try {
				J2735Bsm convertedBsm = OssBsm.genericBsm(entry);
				publishBsm(convertedBsm);
				
				String bsmJson = JsonUtils.toJson(convertedBsm, odeProperties.getVsdmVerboseJson());
				publishBsm(bsmJson);
				logger.debug("Published bsm {} to the topics {} and {}", 
				        i++, 
				        odeProperties.getKafkaTopicBsmSerializedPojo(),
				        odeProperties.getKafkaTopicBsmRawJson());
			} catch (OssBsmPart2Exception e) {
				logger.error("Unable to convert BSM", e);
			}
		}
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

	private void publishVsdm(byte[] data) {
		MessageProducer<String, byte[]> producer = messageProducerPool.checkOut();
		producer.send(odeProperties.getKafkaTopicVsdm(), null, data);
		messageProducerPool.checkIn(producer);
		logger.debug("Published vsd to the topic {}", odeProperties.getKafkaTopicVsdm());
	}

}
