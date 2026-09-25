package us.dot.its.jpo.ode.udp.generic;

import java.net.DatagramPacket;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.buf.HexUtils;
import org.springframework.kafka.core.KafkaTemplate;
import us.dot.its.jpo.ode.kafka.topics.RawEncodedJsonTopics;
import us.dot.its.jpo.ode.udp.AbstractUdpReceiverPublisher;
import us.dot.its.jpo.ode.udp.InvalidPayloadException;
import us.dot.its.jpo.ode.udp.UdpIngestPublisher;
import us.dot.its.jpo.ode.udp.controller.UDPReceiverProperties.ReceiverProperties;
import us.dot.its.jpo.ode.uper.SupportedMessageType;
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

  private final UdpIngestPublisher publisher;
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
    this(props, UdpIngestPublisher.rawOnly(kafkaTemplate), rawEncodedJsonTopics);
  }

  /**
   * Constructs a GenericReceiver that publishes through the shared UDP ingest publisher.
   *
   * @param props UDP port and buffer size
   * @param publisher raw-topic or direct-JSON publisher
   * @param rawEncodedJsonTopics raw topics used when direct JSON is off
   */
  public GenericReceiver(ReceiverProperties props, UdpIngestPublisher publisher,
      RawEncodedJsonTopics rawEncodedJsonTopics) {
    super(props.getReceiverPort(), props.getBufferSize());

    this.publisher = publisher;
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
        if ((packet.getLength() <= 0) || (packet.getData() == null)) {
          log.debug("Skipping empty payload");
          continue;
        }
        final byte[] payload = Arrays.copyOfRange(packet.getData(), packet.getOffset(),
            packet.getOffset() + packet.getLength());

        senderIp = packet.getAddress().getHostAddress();
        senderPort = packet.getPort();
        log.debug("Packet received from {}:{}", senderIp, senderPort);

        if (log.isDebugEnabled()) {
          log.debug("Raw Payload {}", HexUtils.toHexString(payload));
        }
        detectedMessageType = UperUtil.determinePacketType(payload);
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

    String hex = HexUtils.toHexString(Arrays.copyOfRange(data, off, off + len)).toLowerCase();
    sb.append(", hex=").append(hex);
    return sb.toString();
  }

  protected void routeMessageByMessageType(String messageType, DatagramPacket packet)
      throws InvalidPayloadException, InterruptedException, UnsupportedMessageTypeException {
    log.debug("Detected Message Type {}", messageType);
    switch (messageType) {
      case "MAP" -> publisher.publish(packet, SupportedMessageType.MAP, rawEncodedJsonTopics.getMap());
      case "SPAT" -> publisher.publish(packet, SupportedMessageType.SPAT,
          rawEncodedJsonTopics.getSpat());
      case "TIM" -> publisher.publish(packet, SupportedMessageType.TIM, rawEncodedJsonTopics.getTim());
      case "BSM" -> publisher.publish(packet, SupportedMessageType.BSM, rawEncodedJsonTopics.getBsm());
      case "SSM" -> publisher.publish(packet, SupportedMessageType.SSM, rawEncodedJsonTopics.getSsm());
      case "SRM" -> publisher.publish(packet, SupportedMessageType.SRM, rawEncodedJsonTopics.getSrm());
      case "PSM" -> publisher.publish(packet, SupportedMessageType.PSM, rawEncodedJsonTopics.getPsm());
      case "SDSM" -> publisher.publish(packet, SupportedMessageType.SDSM,
          rawEncodedJsonTopics.getSdsm());
      case "RTCM" -> publisher.publish(packet, SupportedMessageType.RTCM,
          rawEncodedJsonTopics.getRtcm());
      case "RSM" -> publisher.publish(packet, SupportedMessageType.RSM, rawEncodedJsonTopics.getRsm());
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
