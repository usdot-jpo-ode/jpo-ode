package us.dot.its.jpo.ode.udp.sdsm;

import java.net.DatagramPacket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import us.dot.its.jpo.ode.udp.AbstractUdpReceiverPublisher;
import us.dot.its.jpo.ode.udp.InvalidPayloadException;
import us.dot.its.jpo.ode.udp.UdpIngestPublisher;
import us.dot.its.jpo.ode.udp.controller.UDPReceiverProperties.ReceiverProperties;
import us.dot.its.jpo.ode.uper.SupportedMessageType;

/**
 * The SdsmReceiver class extends AbstractUdpReceiverPublisher and is responsible for receiving UDP
 * packets containing SDSM (Sensor Data Sharing Message) data, decoding them from their hex
 * representation to JSON format, and then publishing the JSON data to a Kafka topic.
 *
 * </p>
 * The class utilizes a KafkaTemplate for publishing messages and a configurable topic name where
 * the decoded SDSM JSON messages are sent.
 */
@Slf4j
public class SdsmReceiver extends AbstractUdpReceiverPublisher {

  private final UdpIngestPublisher ingestPublisher;
  private final String publishTopic;

  /**
   * Constructs a SdsmReceiver object that listens for UDP packets containing Sensor Data Sharing
   * Message (SDSM) data, decodes them, and publishes the decoded JSON data to a specified Kafka
   * topic.
   *
   * @param receiverProperties The properties containing configuration details such as the port to
   *                           listen on and buffer size.
   * @param kafkaTemplate      The KafkaTemplate used to publish messages to a Kafka topic.
   * @param publishTopic       The name of the Kafka topic to which decoded SDSM JSON messages should
   *                           be published.
   */
  public SdsmReceiver(ReceiverProperties receiverProperties,
      KafkaTemplate<String, String> kafkaTemplate, String publishTopic) {
    this(receiverProperties, UdpIngestPublisher.rawOnly(kafkaTemplate), publishTopic);
  }

  /**
   * Constructs an SdsmReceiver that publishes through the shared UDP ingest publisher.
   *
   * @param receiverProperties UDP port and buffer size
   * @param ingestPublisher raw-topic or direct-JSON publisher
   * @param publishTopic raw encoded topic used when direct JSON is off
   */
  public SdsmReceiver(ReceiverProperties receiverProperties, UdpIngestPublisher ingestPublisher,
      String publishTopic) {
    super(receiverProperties.getReceiverPort(), receiverProperties.getBufferSize());

    this.publishTopic = publishTopic;
    this.ingestPublisher = ingestPublisher;
  }

  @Override
  public void run() {
    log.info("SDSM UDP Receiver Service started.");

    byte[] buffer = new byte[bufferSize];
    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
    do {
      try {
        log.info("Waiting for UDP SDSM packets...");
        socket.receive(packet);
        if (packet.getLength() > 0) {
          ingestPublisher.publish(packet, SupportedMessageType.SDSM, publishTopic);
        }
      } catch (InvalidPayloadException e) {
        log.error("Error decoding packet", e);
      } catch (Exception e) {
        log.error("Error receiving packet", e);
      }
    } while (!isStopped());
  }
}