package us.dot.its.jpo.ode.udp.generic;

import java.net.DatagramPacket;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.buf.HexUtils;
import us.dot.its.jpo.ode.codec.ffmlib.FfmlibDecodeService;
import us.dot.its.jpo.ode.udp.AbstractUdpReceiverPublisher;
import us.dot.its.jpo.ode.udp.InvalidPayloadException;
import us.dot.its.jpo.ode.udp.UnsupportedMessageTypeException;
import us.dot.its.jpo.ode.udp.controller.UDPReceiverProperties.ReceiverProperties;
import us.dot.its.jpo.ode.uper.SupportedMessageType;
import us.dot.its.jpo.ode.uper.UperUtil;

/**
 * GenericReceiver listens for UDP packets, auto-detects the J2735 message type, and decodes
 * directly in-process via {@link FfmlibDecodeService}. No intermediate Kafka topic is used.
 */
@Slf4j
public class GenericReceiver extends AbstractUdpReceiverPublisher {

  private final FfmlibDecodeService decodeService;

  public GenericReceiver(ReceiverProperties props, FfmlibDecodeService decodeService) {
    super(props.getReceiverPort(), props.getBufferSize());
    this.decodeService = decodeService;
  }

  @Override
  public void run() {
    log.debug("Generic UDP Receiver Service started.");

    // Reuse one buffer / packet like typed receivers — avoid per-loop allocation.
    byte[] buffer = new byte[bufferSize];
    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
    do {
      String detectedMessageType = null;
      try {
        log.debug("Waiting for Generic UDP packets...");
        packet.setData(buffer);
        packet.setLength(buffer.length);
        socket.receive(packet);
        if (packet.getLength() <= 0 || packet.getData() == null) {
          log.debug("Skipping empty payload");
          continue;
        }

        senderIp = packet.getAddress().getHostAddress();
        senderPort = packet.getPort();
        log.debug("Packet received from {}:{}", senderIp, senderPort);

        // Type-detect only on the received length — not the full buffer capacity.
        byte[] payload = Arrays.copyOfRange(
            packet.getData(), packet.getOffset(), packet.getOffset() + packet.getLength());
        if (log.isDebugEnabled()) {
          log.debug("Raw Payload {}", HexUtils.toHexString(payload).toLowerCase());
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
      throws InvalidPayloadException, UnsupportedMessageTypeException {
    log.debug("Detected Message Type {}", messageType);
    try {
      SupportedMessageType msgType = SupportedMessageType.valueOf(messageType);
      decodeService.decode(packet, msgType);
    } catch (IllegalArgumentException e) {
      throw new UnsupportedMessageTypeException(messageType);
    }
  }
}
