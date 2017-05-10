package us.dot.its.jpo.ode.vsdm;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
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

	@Autowired
	public VsdmReceiver(OdeProperties odeProps) {
		
		this.odeProperties = odeProps;

		try {
			socket = new DatagramSocket(odeProperties.getVsdmPort());
			logger.info("[VSDM Receiver] Created UDP socket bound to port", odeProperties.getVsdmPort());
		} catch (SocketException e) {
			logger.error("[VSDM Receiver] Error creating socket with port ", odeProperties.getVsdmPort(), e);
		}
	}

	@Override
	public void run() {

		while (true) {
			
			DatagramPacket packet = new DatagramPacket(new byte[odeProperties.getVsdmBufferSize()], odeProperties.getVsdmBufferSize());

			try {
				socket.receive(packet);
			} catch (IOException e) {
				logger.error("[VSDM Receiver] Error receiving UDP packet");
			}

			InputStream ins = new ByteArrayInputStream(packet.getData());
			AbstractData decoded = null;
			try {
				coder.decode(ins, decoded);
			} catch (DecodeFailedException | DecodeNotSupportedException e) {
				logger.error("[VSDM Receiver] Error, unable to decode UDP message", e);
			}

			if (decoded instanceof ServiceRequest || decoded instanceof ServiceResponse) {
				// VsdmDepositor.send(decoded);
			} else if (decoded instanceof VehSitDataMessage) {
				// VsdmDepositor.send(decoded);
				List<BasicSafetyMessage> bsmList = VsdToBsmConverter.convert((VehSitDataMessage) decoded);
				for (BasicSafetyMessage entry : bsmList) {
					try {
						J2735Bsm convertedBsm = OssBsm.genericBsm(entry);
						String bsmJson = JsonUtils.toJson(convertedBsm, odeProperties.getVsdmVerboseJson());
				        
						publish(bsmJson);

		        logger.debug("Published: {}", bsmJson);
					} catch (OssBsmPart2Exception e) {
						logger.error("[VSDM Receiver] Error, unable to convert BSM: ", e);
					}

				}

			} else {
				logger.error("[VSDM Receiver] Error, unknown message type received: ", decoded);
			}

		}

	}
	
	private void publish(String msg) {
		
		MessageProducer
        .defaultStringMessageProducer(odeProperties.getKafkaBrokers(), odeProperties.getKafkaProducerType())
        .send(odeProperties.getKafkaTopicBsmJSON(), null, msg);
	}

}
