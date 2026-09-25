package us.dot.its.jpo.ode.udp.rtcm;

import java.net.DatagramPacket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import us.dot.its.jpo.ode.udp.AbstractUdpReceiverPublisher;
import us.dot.its.jpo.ode.udp.InvalidPayloadException;
import us.dot.its.jpo.ode.udp.UdpIngestPublisher;
import us.dot.its.jpo.ode.udp.controller.UDPReceiverProperties.ReceiverProperties;
import us.dot.its.jpo.ode.uper.SupportedMessageType;

/**
 * The RtcmReceiver class is responsible for receiving UDP packets containing RTCM data,
 * decoding the packets, and publishing the decoded message to a specified Kafka topic.
 * It extends the AbstractUdpReceiverPublisher, leveraging its capabilities to receive UDP packets
 * asynchronously.
 */
@Slf4j
public class RtcmReceiver extends AbstractUdpReceiverPublisher {
  private final UdpIngestPublisher ingestPublisher;
  private final String publishTopic;

  /**
   * Constructs a RtcmReceiver object that is responsible for receiving UDP packets, decoding RTCM
   * data, and publishing the decoded message to a specified Kafka topic.
   *
   * @param receiverProperties The properties that configure the UDP receiver, including the port
   *                           and buffer size.
   * @param kafkaTemplate      The KafkaTemplate used for sending messages to the Kafka broker.
   * @param publishTopic       The Kafka topic to which the decoded RTCM data should be published.
   */
  public RtcmReceiver(ReceiverProperties receiverProperties,
      KafkaTemplate<String, String> kafkaTemplate, String publishTopic) {
    this(receiverProperties, UdpIngestPublisher.rawOnly(kafkaTemplate), publishTopic);
  }

  /**
   * Constructs an RtcmReceiver that publishes through the shared UDP ingest publisher.
   *
   * @param receiverProperties UDP port and buffer size
   * @param ingestPublisher raw-topic or direct-JSON publisher
   * @param publishTopic raw encoded topic used when direct JSON is off
   */
  public RtcmReceiver(ReceiverProperties receiverProperties, UdpIngestPublisher ingestPublisher,
      String publishTopic) {
    super(receiverProperties.getReceiverPort(), receiverProperties.getBufferSize());

    this.publishTopic = publishTopic;
    this.ingestPublisher = ingestPublisher;
  }

  @Override
  public void run() {
    log.debug("RTCM UDP Receiver Service started.");

    byte[] buffer = new byte[bufferSize];
    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
    do {
      try {
        log.info("Waiting for UDP RTCM packets...");
        socket.receive(packet);
        if (packet.getLength() > 0) {
          ingestPublisher.publish(packet, SupportedMessageType.RTCM, publishTopic);
        }
      } catch (InvalidPayloadException e) {
        log.error("Error decoding packet", e);
      } catch (Exception e) {
        log.error("Error receiving packet", e);
      }
    } while (!isStopped());
  }
}
