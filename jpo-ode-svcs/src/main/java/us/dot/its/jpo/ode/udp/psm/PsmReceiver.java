package us.dot.its.jpo.ode.udp.psm;

import java.net.DatagramPacket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import us.dot.its.jpo.ode.kafka.producer.DisabledTopicException;
import us.dot.its.jpo.ode.udp.AbstractUdpReceiverPublisher;
import us.dot.its.jpo.ode.udp.InvalidPayloadException;
import us.dot.its.jpo.ode.udp.UdpHexDecoder;
import us.dot.its.jpo.ode.udp.controller.UDPReceiverProperties.ReceiverProperties;

/**
 * The PsmReceiver class extends AbstractUdpReceiverPublisher and is responsible for receiving
 * UDP packets containing PSM (Position & Status Message) data, decoding them from their
 * hex representation to JSON format, and then publishing the JSON data to a Kafka topic.
 *
 * </p>
 * The class utilizes a KafkaTemplate for publishing messages and a configurable topic
 * name where the decoded PSM JSON messages are sent.
 */
@Slf4j
public class PsmReceiver extends AbstractUdpReceiverPublisher {

  private final KafkaTemplate<String, String> psmPublisher;
  private final String publishTopic;

  /**
   * Constructs a PsmReceiver object that listens for UDP packets containing
   * Position & Status Message (PSM) data, decodes them, and publishes the decoded
   * JSON data to a specified Kafka topic.
   *
   * @param receiverProperties The properties containing configuration details such as the port to
   *                           listen on and buffer size.
   * @param kafkaTemplate The KafkaTemplate used to publish messages to a Kafka topic.
   * @param publishTopic The name of the Kafka topic to which decoded PSM JSON messages
   *                     should be published.
   */
  public PsmReceiver(ReceiverProperties receiverProperties,
      KafkaTemplate<String, String> kafkaTemplate, String publishTopic) {
    super(receiverProperties.getReceiverPort(), receiverProperties.getBufferSize());

    this.publishTopic = publishTopic;
    this.psmPublisher = kafkaTemplate;
  }

  @Override
  public void run() {
    log.debug("PSM UDP Receiver Service started.");

    byte[] buffer = new byte[bufferSize];
    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
    do {
      try {
        log.debug("Waiting for UDP PSM packets...");
        socket.receive(packet);
        if (packet.getLength() > 0) {
          String psmJson = UdpHexDecoder.buildJsonPsmFromPacket(packet);
          if (psmJson != null) {
            psmPublisher.send(publishTopic, psmJson);
          }
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
