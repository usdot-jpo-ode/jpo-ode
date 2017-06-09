package us.dot.its.jpo.ode.udp.isd;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.oss.asn1.AbstractData;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.j2735.J2735;
import us.dot.its.jpo.ode.j2735.semi.IntersectionSituationData;
import us.dot.its.jpo.ode.j2735.semi.ServiceRequest;
import us.dot.its.jpo.ode.udp.AbstractUdpReceiverPublisher;

public class IsdReceiver extends AbstractUdpReceiverPublisher {

	private static Logger logger = LoggerFactory.getLogger(IsdReceiver.class);

	@Autowired
	public IsdReceiver(OdeProperties odeProps) {
		super(odeProps, odeProps.getIsdReceiverPort(), odeProps.getIsdBufferSize());
	}

	@Override
	public void run() {

		logger.debug("Starting {}...", this.getClass().getSimpleName());

		byte[] buffer = new byte[odeProperties.getIsdBufferSize()];

		DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

		while (!isStopped()) {

			try {
				logger.debug("Waiting for UDP packets...");
				socket.receive(packet);
				if (packet.getLength() > 0) {
					senderIp = packet.getAddress().getHostAddress();
					senderPort = packet.getPort();
					logger.debug("Packet received from {}:{}", senderIp, senderPort);

					// extract the actualPacket from the buffer
					byte[] payload = Arrays.copyOf(packet.getData(), packet.getLength());
					processPacket(payload);
				}
			} catch (IOException e) {
				logger.error("Error receiving packet", e);
			} catch (UdpReceiverException e) {
				logger.error("Error decoding packet", e);
			}
		}
	}

	private void processPacket(byte[] data) throws UdpReceiverException {
		AbstractData decoded = super.decodeData(data);
		try {
			if (decoded instanceof ServiceRequest) {
				senderPort = ((ServiceRequest)decoded).getDestination().getPort().intValue();
				senderIp = ((ServiceRequest)decoded).getDestination().getAddress().toString();
				logger.error("Service request response destination {}:{}", senderPort, senderIp);
				sendResponse(decoded, socket);
			} else if (decoded instanceof IntersectionSituationData) {
				logger.debug("Received ISD");
				publish(data, odeProperties.getKafkaTopicEncodedIsd());
			} else {
				logger.error("Unknown message type received {}", decoded.getClass().getName());
			}
		} catch (Exception e) {
			logger.error("Error processing message", e);
		}
	}

}
