package us.dot.its.jpo.ode.udp.map;

import java.net.DatagramPacket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import us.dot.its.jpo.ode.kafka.producer.DisabledTopicException;
import us.dot.its.jpo.ode.udp.AbstractUdpReceiverPublisher;
import us.dot.its.jpo.ode.udp.InvalidPayloadException;
import us.dot.its.jpo.ode.udp.UdpHexDecoder;
import us.dot.its.jpo.ode.udp.controller.UDPReceiverProperties;

/**
 * The MapReceiver class is responsible for receiving UDP packets, decoding
 * them, and publishing the decoded JSON map to a specified Kafka topic.
 * It extends the {@link AbstractUdpReceiverPublisher} class to leverage UDP
 * receiving capabilities.
 *
 * </p>MapReceiver listens on a specified port for incoming UDP packets encapsulating
 * map data, and decodes these packets. Upon successful decoding, the map data is published
 * to a Kafka topic using KafkaTemplate.
 */
@Slf4j
public class MapReceiver extends AbstractUdpReceiverPublisher {

  KafkaTemplate<String, String> kafkaTemplate;
  private final String publishTopic;

  /**
   * Constructs a new MapReceiver instance to receive UDP packets, decode them,
   * and publish the decoded map data to a specified Kafka topic.
   *
   * @param receiverProperties The properties that define the UDP receiver
   *                           configuration, including the port on which to
   *                           listen and the buffer size for incoming packets.
   * @param kafkaTemplate      The KafkaTemplate instance used to send messages
   *                           to the Kafka topic.
   * @param publishTopic       The topic to which decoded map data should be
   *                           published.
   */
  public MapReceiver(UDPReceiverProperties.ReceiverProperties receiverProperties,
      KafkaTemplate<String, String> kafkaTemplate, String publishTopic) {
    super(receiverProperties.getReceiverPort(), receiverProperties.getBufferSize());
    this.kafkaTemplate = kafkaTemplate;
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
          String mapJson = UdpHexDecoder.buildJsonMapFromPacket(packet);
          if (mapJson != null) {
            kafkaTemplate.send(publishTopic, mapJson);
          }
        } else {
          log.debug("Ignoring empty packet from {}", packet.getSocketAddress());
        }
      } catch (DisabledTopicException e) {
        log.warn(e.getMessage());
      } catch (InvalidPayloadException e) {
        log.error("Error decoding packet", e);
      } catch (Exception e) {
        log.error("Error receiving packet", e);
      }
    } while (!isStopped());
  }

}
