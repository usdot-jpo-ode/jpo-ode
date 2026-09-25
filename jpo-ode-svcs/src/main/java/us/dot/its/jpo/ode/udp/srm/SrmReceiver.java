package us.dot.its.jpo.ode.udp.srm;

import java.net.DatagramPacket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import us.dot.its.jpo.ode.udp.AbstractUdpReceiverPublisher;
import us.dot.its.jpo.ode.udp.InvalidPayloadException;
import us.dot.its.jpo.ode.udp.UdpIngestPublisher;
import us.dot.its.jpo.ode.udp.controller.UDPReceiverProperties.ReceiverProperties;
import us.dot.its.jpo.ode.uper.SupportedMessageType;

/**
 * SrmReceiver is responsible for receiving UDP packets containing SRM (Signal Request Message)
 * data, decoding them, and publishing the decoded messages to a specified Kafka topic.
 *
 * </p>
 * This class extends the AbstractUdpReceiverPublisher and overrides its run method to implement the
 * logic for receiving packets, processing them, and sending the result to Kafka.
 *
 * </p>
 * It utilizes a KafkaTemplate for sending messages to Kafka and uses a DatagramSocket to listen for
 * incoming UDP packets on a specified port.
 */
@Slf4j
public class SrmReceiver extends AbstractUdpReceiverPublisher {

  private final UdpIngestPublisher ingestPublisher;
  private final String publishTopic;

  /**
   * Constructs an instance of SrmReceiver which is responsible for receiving UDP packets carrying
   * SRM data, decoding them, and publishing the results to a Kafka topic.
   *
   * @param receiverProperties the properties for configuring the UDP receiver, including port and
   *                           buffer size.
   * @param kafkaTemplate      the KafkaTemplate to be used for publishing decoded messages to
   *                           Kafka.
   * @param publishTopic       the Kafka topic to which the decoded SRM messages will be published.
   */
  public SrmReceiver(ReceiverProperties receiverProperties,
      KafkaTemplate<String, String> kafkaTemplate, String publishTopic) {
    this(receiverProperties, UdpIngestPublisher.rawOnly(kafkaTemplate), publishTopic);
  }

  /**
   * Constructs an SrmReceiver that publishes through the shared UDP ingest publisher.
   *
   * @param receiverProperties UDP port and buffer size
   * @param ingestPublisher raw-topic or direct-JSON publisher
   * @param publishTopic raw encoded topic used when direct JSON is off
   */
  public SrmReceiver(ReceiverProperties receiverProperties, UdpIngestPublisher ingestPublisher,
      String publishTopic) {
    super(receiverProperties.getReceiverPort(), receiverProperties.getBufferSize());

    this.publishTopic = publishTopic;
    this.ingestPublisher = ingestPublisher;
  }

  @Override
  public void run() {
    log.debug("SRM UDP Receiver Service started.");

    byte[] buffer = new byte[bufferSize];
    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
    do {
      try {
        log.debug("Waiting for UDP SRM packets...");
        socket.receive(packet);
        if (packet.getLength() > 0) {
          ingestPublisher.publish(packet, SupportedMessageType.SRM, publishTopic);
        }
      } catch (InvalidPayloadException e) {
        log.error("Error decoding packet", e);
      } catch (Exception e) {
        log.error("Error receiving packet", e);
      }
    } while (!isStopped());
  }
}
