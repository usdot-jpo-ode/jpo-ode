package us.dot.its.jpo.ode.udp.srm;

import java.net.DatagramPacket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import us.dot.its.jpo.ode.kafka.producer.DisabledTopicException;
import us.dot.its.jpo.ode.udp.AbstractUdpReceiverPublisher;
import us.dot.its.jpo.ode.udp.InvalidPayloadException;
import us.dot.its.jpo.ode.udp.UdpHexDecoder;
import us.dot.its.jpo.ode.udp.controller.UDPReceiverProperties.ReceiverProperties;

/**
 * SrmReceiver is responsible for receiving UDP packets containing SRM (Signal Request Message)
 * data, decoding them, and publishing the decoded messages to a specified Kafka topic.
 *
 * </p>
 * This class extends the AbstractUdpReceiverPublisher and overrides its run method to implement
 * the logic for receiving packets, processing them, and sending the result to Kafka.
 *
 * </p>
 * It utilizes a KafkaTemplate for sending messages to Kafka and uses a DatagramSocket to listen
 * for incoming UDP packets on a specified port.
 */
@Slf4j
public class SrmReceiver extends AbstractUdpReceiverPublisher {

  private final KafkaTemplate<String, String> srmPublisher;
  private final String publishTopic;

  /**
   * Constructs an instance of SrmReceiver which is responsible for receiving UDP packets
   * carrying SRM data, decoding them, and publishing the results to a Kafka topic.
   *
   * @param receiverProperties the properties for configuring the UDP receiver, including port
   *        and buffer size.
   * @param kafkaTemplate the KafkaTemplate to be used for publishing decoded messages to Kafka.
   * @param publishTopic the Kafka topic to which the decoded SRM messages will be published.
   */
  public SrmReceiver(ReceiverProperties receiverProperties,
      KafkaTemplate<String, String> kafkaTemplate, String publishTopic) {
    super(receiverProperties.getReceiverPort(), receiverProperties.getBufferSize());

    this.publishTopic = publishTopic;
    this.srmPublisher = kafkaTemplate;
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
          String srmJson = UdpHexDecoder.buildJsonSrmFromPacket(packet);
          if (srmJson != null) {
            srmPublisher.send(publishTopic, srmJson);
          }
        }
      } catch (DisabledTopicException e) {
        log.warn(e.getMessage());
      }  catch (InvalidPayloadException e) {
        log.error("Error decoding packet", e);
      } catch (Exception e) {
        log.error("Error receiving packet", e);
      }
    } while (!isStopped());
  }
}
