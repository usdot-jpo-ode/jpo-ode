package us.dot.its.jpo.ode.udp.bsm;

import java.net.DatagramPacket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import us.dot.its.jpo.ode.udp.AbstractUdpReceiverPublisher;
import us.dot.its.jpo.ode.udp.InvalidPayloadException;
import us.dot.its.jpo.ode.udp.UdpHexDecoder;
import us.dot.its.jpo.ode.udp.controller.UDPReceiverProperties;

/**
 * The BsmReceiver class is responsible for receiving UDP packets containing Basic Safety Message
 * (BSM) data, decoding the packets, and publishing the decoded message to a specified Kafka topic.
 * It extends the AbstractUdpReceiverPublisher, leveraging its capabilities to receive UDP packets
 * asynchronously.
 */
@Slf4j
public class BsmReceiver extends AbstractUdpReceiverPublisher {

  private final KafkaTemplate<String, String> bsmPublisher;
  private final String publishTopic;

  /**
   * Constructs a BsmReceiver object that is responsible for receiving UDP packets, decoding Basic
   * Safety Message (BSM) data, and publishing the decoded message to a specified Kafka topic.
   *
   * @param receiverProperties properties that configure the UDP receiver, including the port and
   *                           buffer size.
   * @param template           KafkaTemplate used for sending messages to the Kafka broker.
   * @param publishTopic       the Kafka topic to which the decoded BSM data should be published.
   */
  public BsmReceiver(UDPReceiverProperties.ReceiverProperties receiverProperties,
      KafkaTemplate<String, String> template, String publishTopic) {
    super(receiverProperties.getReceiverPort(), receiverProperties.getBufferSize());

    this.publishTopic = publishTopic;
    this.bsmPublisher = template;
  }

  @Override
  public void run() {
    log.debug("BSM UDP Receiver Service started.");

    byte[] buffer = new byte[bufferSize];
    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
    do {
      try {
        log.debug("Waiting for UDP BSM packets...");
        this.socket.receive(packet);
        if ((packet.getLength() <= 0)) {
          log.debug("Skipping empty payload");
          continue;
        }

        String bsmJson = UdpHexDecoder.buildJsonBsmFromPacket(packet);
        if (bsmJson != null) {
          log.debug("Publishing String data to {}", publishTopic);

          var sendResult = bsmPublisher.send(publishTopic, bsmJson);
          sendResult.whenCompleteAsync((result, error) -> {
            if (error != null) {
              log.error("Error sending BSM to Kafka", error);
            }
          });
        }

      } catch (InvalidPayloadException e) {
        log.error("Error decoding packet", e);
      } catch (Exception e) {
        log.error("Error receiving packet", e);
      }
    } while (!isStopped());
  }
}
