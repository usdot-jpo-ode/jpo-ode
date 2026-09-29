package us.dot.its.jpo.ode.udp.portmapped;

import java.net.DatagramPacket;
import java.net.InetAddress;

import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.buf.HexUtils;
import org.springframework.kafka.core.KafkaTemplate;
import us.dot.its.jpo.ode.kafka.topics.RawEncodedJsonTopics;
import us.dot.its.jpo.ode.udp.InvalidPayloadException;
import us.dot.its.jpo.ode.udp.controller.UDPReceiverProperties.ReceiverProperties;
import us.dot.its.jpo.ode.udp.generic.GenericReceiver;
import us.dot.its.jpo.ode.udp.generic.GenericReceiver.UnsupportedMessageTypeException;

/**
 * PortMappedConfigurableReceiver is a class that listens for UDP packets and processes them based on the
 * determined message type. It extends GenericReceiver to take advantage multi message routing
 *
 * </p>The class is designed to handle all {@link us.dot.its.jpo.ode.uper.SupportedMessageType}
 * message types encoded in UDP packets such as and routes them to the appropriate Kafka topic.
 */
@Slf4j
public class PortMappedConfigurableReceiver extends GenericReceiver {

  private final PortMappedIngestConfig.PortMappedIngestSource ingestConfig;

  /**
   * Constructs a new GenericReceiver with the specified properties, Kafka template, and raw encoded
   * JSON topics.
   *
   * @param props                the receiver properties containing configuration settings such as
   *                             port and buffer size
   * @param ingestConfig         the configuration object containing the ingest settings
   */
    public PortMappedConfigurableReceiver(ReceiverProperties props, KafkaTemplate<String, String> kafkaTemplate,
      RawEncodedJsonTopics rawEncodedJsonTopics, PortMappedIngestConfig.PortMappedIngestSource ingestConfig) {
      super(props, kafkaTemplate, rawEncodedJsonTopics);
      log.info("Creating PortMappedConfigurableReceiver with port " + props.getReceiverPort() + " and buffer size " + props.getBufferSize() + " and Remap IP " + ingestConfig.getOriginIp());

      this.ingestConfig = ingestConfig;
  }

  @Override
  public void run() {
    log.debug("");

    byte[] buffer;
    do {
      buffer = new byte[bufferSize];
      // packet should be recreated on each loop to prevent latent data in buffer
      DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
      try {
        socket.receive(packet);
        byte[] payload = packet.getData();
        if ((packet.getLength() <= 0) || (payload == null)) {
          log.debug("Skipping empty payload");
          continue;
        }

        

        senderIp = this.ingestConfig.getOriginIp();
        InetAddress senderAddress = InetAddress.getByName(senderIp);
        packet.setAddress(senderAddress);
        senderPort = packet.getPort();
        log.debug("Packet received from {}:{}", senderIp, senderPort);

        String payloadHexString = HexUtils.toHexString(payload).toLowerCase();
        log.debug("Raw Payload {}", payloadHexString);

        routeMessageByMessageType(this.ingestConfig.getType(), packet);

      } catch (UnsupportedMessageTypeException e) {
        log.error("Unsupported Message Type", e);
      } catch (InvalidPayloadException e) {
        log.error("Error decoding packet", e);
      } catch (Exception e) {
        log.error("Error receiving packet", e);
      }
    } while (!isStopped());
  }
}
