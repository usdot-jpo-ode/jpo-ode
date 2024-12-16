package us.dot.its.jpo.ode.udp.tim;

import java.net.DatagramPacket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import us.dot.its.jpo.ode.udp.AbstractUdpReceiverPublisher;
import us.dot.its.jpo.ode.udp.InvalidPayloadException;
import us.dot.its.jpo.ode.udp.UdpHexDecoder;
import us.dot.its.jpo.ode.udp.controller.UDPReceiverProperties.ReceiverProperties;

/**
 * The TimReceiver class extends the AbstractUdpReceiverPublisher and is responsible for receiving
 * UDP packets containing TIM (Traveler Information Message) data. Upon receiving a packet, it
 * decodes the TIM data and forwards it to a specified Kafka topic using a KafkaTemplate.
 */
@Slf4j
public class TimReceiver extends AbstractUdpReceiverPublisher {

  private final KafkaTemplate<String, String> timPublisher;
  private final String publishTopic;

  /**
   * Constructs a TimReceiver for receiving UDP packets and forwarding TIM (Traveler Information
   * Message) data to a Kafka topic.
   *
   * @param receiverProperties contains configuration properties such as receiver port and buffer
   *                           size needed for establishing the UDP receiver.
   * @param kafkaTemplate      the KafkaTemplate used for sending the TIM data to the specified
   *                           Kafka topic after decoding.
   * @param publishTopic       the name of the Kafka topic to which the decoded TIM data should be
   *                           published.
   */
  public TimReceiver(ReceiverProperties receiverProperties,
      KafkaTemplate<String, String> kafkaTemplate, String publishTopic) {
    super(receiverProperties.getReceiverPort(), receiverProperties.getBufferSize());

    this.publishTopic = publishTopic;
    this.timPublisher = kafkaTemplate;
  }

  @Override
  public void run() {
    log.debug("TIM UDP Receiver Service started.");

    byte[] buffer = new byte[bufferSize];

    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

    do {
      try {
        log.debug("Waiting for UDP TIM packets...");
        socket.receive(packet);
        if (packet.getLength() > 0) {

          String timJson = UdpHexDecoder.buildJsonTimFromPacket(packet);
          if (timJson != null) {
            timPublisher.send(publishTopic, timJson);
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
