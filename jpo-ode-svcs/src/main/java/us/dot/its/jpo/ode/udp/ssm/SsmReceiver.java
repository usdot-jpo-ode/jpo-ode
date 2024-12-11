package us.dot.its.jpo.ode.udp.ssm;

import java.net.DatagramPacket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import us.dot.its.jpo.ode.kafka.producer.DisabledTopicException;
import us.dot.its.jpo.ode.udp.AbstractUdpReceiverPublisher;
import us.dot.its.jpo.ode.udp.InvalidPayloadException;
import us.dot.its.jpo.ode.udp.UdpHexDecoder;
import us.dot.its.jpo.ode.udp.controller.UDPReceiverProperties.ReceiverProperties;

/**
 * The SsmReceiver class is responsible for receiving UDP packets and publishing them as JSON
 * messages to a specified Kafka topic. It extends the functionality of AbstractUdpReceiverPublisher
 * to handle UDP packet reception and decoding.
 */
@Slf4j
public class SsmReceiver extends AbstractUdpReceiverPublisher {

  private final KafkaTemplate<String, String> ssmPublisher;
  private final String publishTopic;

  /**
   * Constructs an SsmReceiver to handle UDP packets and publish them to a specified Kafka topic.
   *
   * @param receiverProperties Properties object containing the receiver configuration like port and
   *                           buffer size.
   * @param kafkaTemplate      Kafka template used to send messages to a Kafka topic.
   * @param publishTopic       The Kafka topic to which the decoded UDP packets will be published.
   */
  public SsmReceiver(ReceiverProperties receiverProperties,
      KafkaTemplate<String, String> kafkaTemplate, String publishTopic) {
    super(receiverProperties.getReceiverPort(), receiverProperties.getBufferSize());

    this.publishTopic = publishTopic;
    this.ssmPublisher = kafkaTemplate;
  }

  @Override
  public void run() {
    log.debug("SSM UDP Receiver Service started.");

    byte[] buffer = new byte[bufferSize];
    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
    do {
      try {
        log.debug("Waiting for UDP SSM packets...");
        socket.receive(packet);
        if (packet.getLength() > 0) {
          String ssmJson = UdpHexDecoder.buildJsonSsmFromPacket(packet);
          if (ssmJson != null) {
            ssmPublisher.send(publishTopic, ssmJson);
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
