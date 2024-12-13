package us.dot.its.jpo.ode.udp.spat;

import java.net.DatagramPacket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import us.dot.its.jpo.ode.udp.AbstractUdpReceiverPublisher;
import us.dot.its.jpo.ode.udp.InvalidPayloadException;
import us.dot.its.jpo.ode.udp.UdpHexDecoder;
import us.dot.its.jpo.ode.udp.controller.UDPReceiverProperties.ReceiverProperties;

/**
 * The SpatReceiver class is responsible for receiving UDP packets containing SPaT (Signal Phase and
 * Timing) data, decoding these packets, and publishing the resulting JSON string to a specified
 * Kafka topic.
 *
 * </p>
 * This class extends the AbstractUdpReceiverPublisher, allowing it to run as a separate thread and
 * handle incoming UDP packets. It utilizes a Kafka template to publish decoded data.
 */
@Slf4j
public class SpatReceiver extends AbstractUdpReceiverPublisher {

  private final KafkaTemplate<String, String> spatPublisher;
  private final String publishTopic;

  /**
   * Constructs a SpatReceiver object that is responsible for receiving UDP packets containing SPaT
   * (Signal Phase and Timing) data, decoding these packets, and publishing the resulting JSON
   * string to a specified Kafka topic.
   *
   * @param receiverProperties the properties for the receiver including the port and buffer size
   * @param kafkaTemplate      the Kafka template used for publishing the decoded SPaT data
   * @param publishTopic       the Kafka topic to which the decoded SPaT data will be published
   */
  public SpatReceiver(
      ReceiverProperties receiverProperties, KafkaTemplate<String, String> kafkaTemplate,
      String publishTopic) {
    super(receiverProperties.getReceiverPort(), receiverProperties.getBufferSize());

    this.publishTopic = publishTopic;
    this.spatPublisher = kafkaTemplate;
  }

  @Override
  public void run() {
    log.debug("SPaT UDP Receiver Service started.");

    byte[] buffer = new byte[bufferSize];
    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
    do {
      try {
        log.debug("Waiting for UDP SPaT packets...");
        socket.receive(packet);
        if (packet.getLength() > 0) {
          String spatJson = UdpHexDecoder.buildJsonSpatFromPacket(packet);
          if (spatJson != null) {
            spatPublisher.send(publishTopic, spatJson);
          }
        }
      } catch (InvalidPayloadException e) {
        log.error("Error decoding packet", e);
      } catch (Exception e) {
        log.error("Error receiving packet", e);
      }
    } while (!isStopped());
  }
}
