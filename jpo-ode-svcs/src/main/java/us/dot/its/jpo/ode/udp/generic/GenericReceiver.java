package us.dot.its.jpo.ode.udp.generic;

import java.net.DatagramPacket;
import java.util.Arrays;
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
 * </p>
 * The class is designed to handle all {@link us.dot.its.jpo.ode.uper.SupportedMessageType} message
 * types encoded in UDP packets and routes them to the appropriate Kafka topic.
 */
@Slf4j
public class GenericReceiver extends AbstractUdpReceiverPublisher {

  private final KafkaTemplate<String, String> publisher;
  private final RawEncodedJsonTopics rawEncodedJsonTopics;

  /**
   * Constructs a new GenericReceiver with the specified properties, Kafka template, and raw encoded
   * JSON topics.
   *
   * @param props the receiver properties containing configuration settings such as port and buffer
   *        size
   * @param kafkaTemplate the KafkaTemplate used for publishing messages
   * @param rawEncodedJsonTopics the configuration object containing the topics used to publish
   *        messages
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
      String detectedMessageType = null;
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

        detectedMessageType = UperUtil.determineHexPacketType(payloadHexString);
        routeMessageByMessageType(detectedMessageType, packet);

      } catch (UnsupportedMessageTypeException e) {
        log.error("Unsupported message type (detected={}, exception={}). Packet: {}.",
            detectedMessageType, e.getMessage(), describePacketForLog(packet), e);
      } catch (InvalidPayloadException e) {
        log.error("Error decoding {} packet ({}). Packet: {}.",
            detectedMessageType != null ? detectedMessageType : "unknown", e.getMessage(),
            describePacketForLog(packet), e);
      } catch (Exception e) {
        log.error("Error receiving or processing packet (detectedMessageType={}). Packet: {}.",
            detectedMessageType, describePacketForLog(packet), e);
      }
    } while (!isStopped());
  }

  /**
   * Formats UDP packet metadata and a hex preview of the received bytes for troubleshooting. Hex is
   * capped to avoid flooding logs on large payloads.
   */
  private static String describePacketForLog(DatagramPacket packet) {
    if (packet == null) {
      return "(null packet)";
    }
    StringBuilder sb = new StringBuilder(128);
    if (packet.getAddress() != null) {
      sb.append("from ").append(packet.getAddress().getHostAddress()).append(':')
          .append(packet.getPort()).append(", ");
    }
    int len = packet.getLength();
    sb.append("length=").append(len);
    if (len <= 0 || packet.getData() == null) {
      return sb.toString();
    }
    int off = packet.getOffset();
    byte[] data = packet.getData();

    String hex = HexUtils.toHexString(Arrays.copyOfRange(data, off, data.length)).toLowerCase();
    sb.append(", hex=").append(hex);
    return sb.toString();
  }

  protected void routeMessageByMessageType(String messageType, DatagramPacket packet)
      throws InvalidPayloadException, UnsupportedMessageTypeException {
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
      case "SDSM" -> {
        String sdsmJson = UdpHexDecoder.buildJsonSdsmFromPacket(packet);
        if (sdsmJson != null) {
          publisher.send(rawEncodedJsonTopics.getSdsm(), sdsmJson);
        }
      }
      case "RTCM" -> {
        String rtcmJson = UdpHexDecoder.buildJsonRtcmFromPacket(packet);
        if (rtcmJson != null) {
          publisher.send(rawEncodedJsonTopics.getRtcm(), rtcmJson);
        }
      }
      case "RSM" -> {
        String rsmJson = UdpHexDecoder.buildJsonRsmFromPacket(packet);
        if (rsmJson != null) {
          publisher.send(rawEncodedJsonTopics.getRsm(), rsmJson);
        }
      }
      default -> throw new UnsupportedMessageTypeException(messageType);
    }
  }

  /**
   * Exception class for Unsupported Message Types.
   */
  public static class UnsupportedMessageTypeException extends Exception {
    /**
     * Constructs a new UnsupportedMessageTypeException with the specified detail message.
     *
     * @param message the detail message
     */
    public UnsupportedMessageTypeException(String message) {
      super(message);
    }
  }
}