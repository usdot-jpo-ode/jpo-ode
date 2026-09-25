package us.dot.its.jpo.ode.udp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;
import us.dot.its.jpo.ode.codec.ffmlib.FfmlibDecodeService;
import us.dot.its.jpo.ode.codec.ffmlib.FfmlibDecodeService.PreparedDecodedMessage;
import us.dot.its.jpo.ode.codec.ffmlib.FfmlibOutputPublisher;
import us.dot.its.jpo.ode.model.OdeMessageFrameMetadata;
import us.dot.its.jpo.ode.udp.UdpHexDecoder.UdpDecodeInput;
import us.dot.its.jpo.ode.uper.SupportedMessageType;

class UdpIngestPublisherTest {

  @Test
  @SuppressWarnings("unchecked")
  void rawPathPublishesEncodedJsonAndDoesNotDecode() throws Exception {
    KafkaTemplate<String, String> rawKafka = mock(KafkaTemplate.class);
    FfmlibDecodeService decoder = mock(FfmlibDecodeService.class);
    FfmlibOutputPublisher output = mock(FfmlibOutputPublisher.class);
    UdpIngestPublisher publisher = new UdpIngestPublisher(rawKafka, false, 1, 4, decoder, output,
        new SimpleMeterRegistry());
    DatagramPacket packet = new DatagramPacket(new byte[] {0x00, 0x14}, 2, InetAddress.getLoopbackAddress(),
        1);

    publisher.publish(packet, SupportedMessageType.BSM, "topic.OdeRawEncodedBSMJson");

    verify(rawKafka).send(eq("topic.OdeRawEncodedBSMJson"), any(String.class));
    verify(decoder, never()).prepareRaw(any(), any(), any());
    verify(output, never()).publish(any());
  }

  @Test
  @SuppressWarnings("unchecked")
  void directPathPublishesOdeJsonAndSkipsTheRawTopic() throws Exception {
    KafkaTemplate<String, String> rawKafka = mock(KafkaTemplate.class);
    FfmlibDecodeService decoder = mock(FfmlibDecodeService.class);
    FfmlibOutputPublisher output = mock(FfmlibOutputPublisher.class);
    PreparedDecodedMessage decoded = new PreparedDecodedMessage("topic.OdeBsmJson", null, "{}",
        new OdeMessageFrameMetadata(), "BSM", "udp", 1L, 1L);
    when(decoder.prepareRaw(any(), any(), isNull())).thenReturn(decoded);
    when(output.publish(decoded)).thenReturn(CompletableFuture.completedFuture(null));
    UdpIngestPublisher publisher = new UdpIngestPublisher(rawKafka, true, 1, 4, decoder, output,
        new SimpleMeterRegistry());
    publisher.start();

    publisher.enqueue(new UdpDecodeInput(new OdeMessageFrameMetadata(), new byte[] {0x00, 0x14}));

    verify(output, timeout(2000)).publish(decoded);
    verify(rawKafka, never()).send(any(), any());
    publisher.close();
  }

  @Test
  @SuppressWarnings("unchecked")
  void directDecodeFailureDoesNotPublish() throws Exception {
    KafkaTemplate<String, String> rawKafka = mock(KafkaTemplate.class);
    FfmlibDecodeService decoder = mock(FfmlibDecodeService.class);
    FfmlibOutputPublisher output = mock(FfmlibOutputPublisher.class);
    when(decoder.prepareRaw(any(), any(), isNull()))
        .thenThrow(new IllegalArgumentException("malformed"));
    SimpleMeterRegistry meters = new SimpleMeterRegistry();
    UdpIngestPublisher publisher = new UdpIngestPublisher(rawKafka, true, 1, 4, decoder, output,
        meters);
    publisher.start();

    publisher.enqueue(new UdpDecodeInput(new OdeMessageFrameMetadata(), new byte[] {0x01}));

    long deadline = System.nanoTime() + 2_000_000_000L;
    while (meters.counter("ode.ffmlib.direct.decode.failures").count() < 1
        && System.nanoTime() < deadline) {
      Thread.sleep(10);
    }
    assertEquals(1.0, meters.counter("ode.ffmlib.direct.decode.failures").count());
    verify(output, never()).publish(any());
    verify(rawKafka, never()).send(any(), any());
    publisher.close();
  }

  @Test
  void fullDecodeQueueBlocksTheCaller() throws Exception {
    UdpIngestPublisher publisher = new UdpIngestPublisher(mock(KafkaTemplate.class), true, 1, 1,
        mock(FfmlibDecodeService.class), mock(FfmlibOutputPublisher.class),
        new SimpleMeterRegistry());
    UdpDecodeInput input = new UdpDecodeInput(new OdeMessageFrameMetadata(), new byte[] {0x01});
    publisher.enqueue(input);
    Thread blocked = new Thread(() -> {
      try {
        publisher.enqueue(input);
      } catch (InterruptedException error) {
        Thread.currentThread().interrupt();
      }
    });
    blocked.start();
    Thread.sleep(150);
    assertTrue(blocked.isAlive());
    publisher.close();
    blocked.join(1000);
  }
}
