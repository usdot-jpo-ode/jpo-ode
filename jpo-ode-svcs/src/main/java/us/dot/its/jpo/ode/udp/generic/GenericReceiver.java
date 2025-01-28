package us.dot.its.jpo.ode.udp.generic;

import io.netty.handler.codec.UnsupportedMessageTypeException;
import java.net.DatagramPacket;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.buf.HexUtils;
import org.springframework.kafka.core.KafkaTemplate;
import us.dot.its.jpo.ode.kafka.topics.RawEncodedJsonTopics;
import us.dot.its.jpo.ode.udp.AbstractUdpReceiverPublisher;
import us.dot.its.jpo.ode.udp.InvalidPayloadException;
import us.dot.its.jpo.ode.udp.UdpHexDecoder;
import us.dot.its.jpo.ode.udp.controller.UDPReceiverProperties.ReceiverProperties;
import us.dot.its.jpo.ode.uper.UperUtil;

/**
 * GenericReceiver is a class that listens for UDP packets and processes them based on the
 * determined message type. It extends AbstractUdpReceiverPublisher to take advantage of the
 * runnable interface for running the receiver service in a separate thread.
 *
 * </p>The class is designed to handle all {@link us.dot.its.jpo.ode.uper.SupportedMessageType}
 * message types encoded in UDP packets such as and routes them to the appropriate Kafka topic.
 */
@Slf4j
public class GenericReceiver extends AbstractUdpReceiverPublisher {

  private final KafkaTemplate<String, String> publisher;
  private final RawEncodedJsonTopics rawEncodedJsonTopics;

  /**
   * Constructs a new GenericReceiver with the specified properties, Kafka template, and raw encoded
   * JSON topics.
   *
   * @param props                the receiver properties containing configuration settings such as
   *                             port and buffer size
   * @param kafkaTemplate        the KafkaTemplate used for publishing messages
   * @param rawEncodedJsonTopics the configuration object containing the topics used to publish
   *                             messages
   */
  public GenericReceiver(ReceiverProperties props, KafkaTemplate<String, String> kafkaTemplate,
      RawEncodedJsonTopics rawEncodedJsonTopics) {
    super(props.getReceiverPort(), props.getBufferSize());

    this.publisher = kafkaTemplate;
    this.rawEncodedJsonTopics = rawEncodedJsonTopics;
  }

  @Override
  public void run() {
    log.debug("Generic UDP Receiver Service started.");

    byte[] buffer;
    do {
      buffer = new byte[bufferSize];
      // packet should be recreated on each loop to prevent latent data in buffer
      DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
      try {
        log.debug("Waiting for Generic UDP packets...");
        socket.receive(packet);
        byte[] payload = packet.getData();
        if ((packet.getLength() <= 0) || (payload == null)) {
          log.debug("Skipping empty payload");
          continue;
        }

        senderIp = packet.getAddress().getHostAddress();
        senderPort = packet.getPort();
        log.debug("Packet received from {}:{}", senderIp, senderPort);

        String payloadHexString = HexUtils.toHexString(payload).toLowerCase();
        log.debug("Raw Payload {}", payloadHexString);

        String messageType = UperUtil.determineHexPacketType(payloadHexString);
        routeMessageByMessageType(messageType, packet);

      } catch (UnsupportedMessageTypeException e) {
        log.error("Unsupported Message Type", e);
      } catch (InvalidPayloadException e) {
        log.error("Error decoding packet", e);
      } catch (Exception e) {
        log.error("Error receiving packet", e);
      }
    } while (!isStopped());
  }

  private void routeMessageByMessageType(
      String messageType,
      DatagramPacket packet
  ) throws InvalidPayloadException, UnsupportedMessageTypeException {
    log.debug("Detected Message Type {}", messageType);
    switch (messageType) {
      case "MAP" -> {
        String mapJson = UdpHexDecoder.buildJsonMapFromPacket(packet);
        log.debug("Sending Data to Topic {}", mapJson);
        if (mapJson != null) {
          publisher.send(rawEncodedJsonTopics.getMap(), mapJson);
        }
      }
      case "SPAT" -> {
        String spatJson = UdpHexDecoder.buildJsonSpatFromPacket(packet);
        if (spatJson != null) {
          publisher.send(rawEncodedJsonTopics.getSpat(), spatJson);
        }
      }
      case "TIM" -> {
        String timJson = UdpHexDecoder.buildJsonTimFromPacket(packet);
        if (timJson != null) {
          publisher.send(rawEncodedJsonTopics.getTim(), timJson);
        }
      }
      case "BSM" -> {
        String bsmJson = UdpHexDecoder.buildJsonBsmFromPacket(packet);
        if (bsmJson != null) {
          publisher.send(rawEncodedJsonTopics.getBsm(), bsmJson);
        }
      }
      case "SSM" -> {
        String ssmJson = UdpHexDecoder.buildJsonSsmFromPacket(packet);
        if (ssmJson != null) {
          publisher.send(rawEncodedJsonTopics.getSsm(), ssmJson);
        }
      }
      case "SRM" -> {
        String srmJson = UdpHexDecoder.buildJsonSrmFromPacket(packet);
        if (srmJson != null) {
          publisher.send(rawEncodedJsonTopics.getSrm(), srmJson);
        }
      }
      case "PSM" -> {
        String psmJson = UdpHexDecoder.buildJsonPsmFromPacket(packet);
        if (psmJson != null) {
          publisher.send(rawEncodedJsonTopics.getPsm(), psmJson);
        }
      }
      default -> throw new UnsupportedMessageTypeException(messageType);
    }
  }
}
