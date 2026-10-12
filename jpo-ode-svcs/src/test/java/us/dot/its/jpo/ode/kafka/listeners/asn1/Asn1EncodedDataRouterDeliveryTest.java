package us.dot.its.jpo.ode.kafka.listeners.asn1;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.LockSupport;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import us.dot.its.jpo.ode.OdeTimJsonTopology;
import us.dot.its.jpo.ode.codec.ffmlib.Asn1CodecModeProperties;
import us.dot.its.jpo.ode.codec.ffmlib.FfmlibEncodeService;
import us.dot.its.jpo.ode.kafka.topics.Asn1CoderTopics;
import us.dot.its.jpo.ode.kafka.topics.JsonTopics;
import us.dot.its.jpo.ode.rsu.RsuDepositor;
import us.dot.its.jpo.ode.security.SecurityServicesClient;
import us.dot.its.jpo.ode.security.SecurityServicesProperties;

class Asn1EncodedDataRouterDeliveryTest {

  @Test
  void usesOneDeadlineForMultipleSendsAndDoesNotCancelALateSend() throws Exception {
    AtomicLong secondTimeoutNanos = new AtomicLong();
    CompletableFuture<SendResult<String, String>> first = new CompletableFuture<>() {
      @Override
      public SendResult<String, String> get(long timeout, TimeUnit unit) {
        LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(40));
        return mockSendResult();
      }
    };
    CompletableFuture<SendResult<String, String>> second = new CompletableFuture<>() {
      @Override
      public SendResult<String, String> get(long timeout, TimeUnit unit) throws TimeoutException {
        secondTimeoutNanos.set(unit.toNanos(timeout));
        throw new TimeoutException("controlled pending send");
      }
    };

    var failure = assertThrows(Asn1EncodedDataRouter.Asn1EncodedDataRouterException.class,
        () -> router().awaitKafkaSends(List.of(first, second), TimeUnit.MILLISECONDS.toNanos(200)));

    assertTrue(failure.getMessage().contains("Timed out"));
    assertTrue(secondTimeoutNanos.get() < TimeUnit.MILLISECONDS.toNanos(190),
        "the second future should receive only the remaining shared budget");
    assertFalse(second.isCancelled());
    assertTrue(second.complete(mockSendResult()), "the timed-out future remains completable");
  }

  private static Asn1EncodedDataRouter router() {
    Asn1CodecModeProperties mode = new Asn1CodecModeProperties();
    mode.setCodecMode(Asn1CodecModeProperties.CodecMode.ffm);
    return new Asn1EncodedDataRouter(new Asn1CoderTopics(), new JsonTopics(),
        new SecurityServicesProperties(), mock(OdeTimJsonTopology.class), mock(RsuDepositor.class),
        mock(SecurityServicesClient.class), mockKafka(), "topic.SdxDepositorInput",
        new ObjectMapper(), new XmlMapper(), mode, mock(FfmlibEncodeService.class));
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  private static KafkaTemplate<String, String> mockKafka() {
    return mock(KafkaTemplate.class);
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  private static SendResult<String, String> mockSendResult() {
    return mock(SendResult.class);
  }
}
