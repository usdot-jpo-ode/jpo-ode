package us.dot.its.jpo.ode.udp.psm;

import java.net.DatagramPacket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import us.dot.its.jpo.ode.udp.AbstractUdpReceiverPublisher;
import us.dot.its.jpo.ode.udp.InvalidPayloadException;
import us.dot.its.jpo.ode.udp.UdpIngestPublisher;
import us.dot.its.jpo.ode.udp.controller.UDPReceiverProperties.ReceiverProperties;
import us.dot.its.jpo.ode.uper.SupportedMessageType;

/**
 * The PsmReceiver class extends AbstractUdpReceiverPublisher and is responsible for receiving UDP
 * packets containing PSM (Personal Safety Message) data, decoding them from their hex
 * representation to JSON format, and then publishing the JSON data to a Kafka topic.
 *
 * </p>
 * The class utilizes a KafkaTemplate for publishing messages and a configurable topic name where
 * the decoded PSM JSON messages are sent.
 */
@Slf4j
public class PsmReceiver extends AbstractUdpReceiverPublisher {

  private final UdpIngestPublisher ingestPublisher;
  private final String publishTopic;

  /**
   * Constructs a PsmReceiver object that listens for UDP packets containing Personal Safety
   * Message (PSM) data, decodes them, and publishes the decoded JSON data to a specified Kafka
   * topic.
   *
   * @param receiverProperties The properties containing configuration details such as the port to
   *                           listen on and buffer size.
   * @param kafkaTemplate      The KafkaTemplate used to publish messages to a Kafka topic.
   * @param publishTopic       The name of the Kafka topic to which decoded PSM JSON messages should
   *                           be published.
   */
  public PsmReceiver(ReceiverProperties receiverProperties,
      KafkaTemplate<String, String> kafkaTemplate, String publishTopic) {
    this(receiverProperties, UdpIngestPublisher.rawOnly(kafkaTemplate), publishTopic);
  }

  /**
   * Constructs a PsmReceiver that publishes through the shared UDP ingest publisher.
   *
   * @param receiverProperties UDP port and buffer size
   * @param ingestPublisher raw-topic or direct-JSON publisher
   * @param publishTopic raw encoded topic used when direct JSON is off
   */
  public PsmReceiver(ReceiverProperties receiverProperties, UdpIngestPublisher ingestPublisher,
      String publishTopic) {
    super(receiverProperties.getReceiverPort(), receiverProperties.getBufferSize());

    this.publishTopic = publishTopic;
    this.ingestPublisher = ingestPublisher;
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
          ingestPublisher.publish(packet, SupportedMessageType.PSM, publishTopic);
        }
      } catch (InvalidPayloadException e) {
        log.error("Error decoding packet", e);
      } catch (Exception e) {
        log.error("Error receiving packet", e);
      }
    } while (!isStopped());
  }
}
