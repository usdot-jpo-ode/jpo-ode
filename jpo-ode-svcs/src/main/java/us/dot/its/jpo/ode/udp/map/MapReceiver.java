package us.dot.its.jpo.ode.udp.map;

import java.net.DatagramPacket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import us.dot.its.jpo.ode.udp.AbstractUdpReceiverPublisher;
import us.dot.its.jpo.ode.udp.InvalidPayloadException;
import us.dot.its.jpo.ode.udp.UdpIngestPublisher;
import us.dot.its.jpo.ode.udp.controller.UDPReceiverProperties;
import us.dot.its.jpo.ode.uper.SupportedMessageType;

/**
 * The MapReceiver class is responsible for receiving UDP packets, decoding them, and publishing the
 * decoded JSON map to a specified Kafka topic. It extends the {@link AbstractUdpReceiverPublisher}
 * class to leverage UDP receiving capabilities.
 *
 * </p>MapReceiver listens on a specified port for incoming UDP packets encapsulating
 * map data, and decodes these packets. Upon successful decoding, the map data is published to a
 * Kafka topic using KafkaTemplate.
 */
@Slf4j
public class MapReceiver extends AbstractUdpReceiverPublisher {

  private final UdpIngestPublisher ingestPublisher;
  private final String publishTopic;

  /**
   * Constructs a new MapReceiver instance to receive UDP packets, decode them, and publish the
   * decoded map data to a specified Kafka topic.
   *
   * @param receiverProperties The properties that define the UDP receiver configuration, including
   *                           the port on which to listen and the buffer size for incoming
   *                           packets.
   * @param kafkaTemplate      The KafkaTemplate instance used to send messages to the Kafka topic.
   * @param publishTopic       The topic to which decoded map data should be published.
   */
  public MapReceiver(UDPReceiverProperties.ReceiverProperties receiverProperties,
      KafkaTemplate<String, String> kafkaTemplate, String publishTopic) {
    this(receiverProperties, UdpIngestPublisher.rawOnly(kafkaTemplate), publishTopic);
  }

  /**
   * Constructs a MapReceiver that publishes through the shared UDP ingest publisher.
   *
   * @param receiverProperties UDP port and buffer size
   * @param ingestPublisher raw-topic or direct-JSON publisher
   * @param publishTopic raw encoded topic used when direct JSON is off
   */
  public MapReceiver(UDPReceiverProperties.ReceiverProperties receiverProperties,
      UdpIngestPublisher ingestPublisher, String publishTopic) {
    super(receiverProperties.getReceiverPort(), receiverProperties.getBufferSize());

    this.ingestPublisher = ingestPublisher;
    this.publishTopic = publishTopic;
  }

  @Override
  public void run() {
    log.debug("Map UDP Receiver Service started.");

    byte[] buffer = new byte[bufferSize];
    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
    do {
      try {
        log.debug("Waiting for UDP Map packets...");
        socket.receive(packet);
        if (packet.getLength() > 0) {
          ingestPublisher.publish(packet, SupportedMessageType.MAP, publishTopic);
        }
      } catch (InvalidPayloadException e) {
        log.error("Error decoding packet", e);
      } catch (Exception e) {
        log.error("Error receiving packet", e);
      }
    } while (!isStopped());
  }

}
