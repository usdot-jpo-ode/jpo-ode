package us.dot.its.jpo.ode.codec.ffmlib;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.apache.kafka.common.errors.TimeoutException;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;
import us.dot.its.jpo.ode.kafka.OdeKafkaProperties;
import us.dot.its.jpo.ode.model.OdeMessageFrameMetadata;

class FfmlibOutputPublisherTest {
  @Test
  @SuppressWarnings("unchecked")
  void producerFailureCompletesExceptionallyForListenerRetry() {
    KafkaTemplate<String, String> producer = mock(KafkaTemplate.class);
    var message = message();
    when(producer.send(message.topic(), message.key(), message.json()))
        .thenReturn(CompletableFuture.failedFuture(new TimeoutException("broker")));
    var publisher = new FfmlibOutputPublisher(producer, new OdeKafkaProperties(),
        new SimpleMeterRegistry());

    assertThrows(ExecutionException.class, () -> publisher.publish(message).get(5, TimeUnit.SECONDS));

    verify(producer, times(1)).send(message.topic(), message.key(), message.json());
  }

  @Test
  @SuppressWarnings("unchecked")
  void disabledTopicIsSkippedWithoutSendingOrFailing() throws Exception {
    KafkaTemplate<String, String> producer = mock(KafkaTemplate.class);
    OdeKafkaProperties properties = new OdeKafkaProperties();
    properties.setDisabledTopics(Set.of("json"));
    SimpleMeterRegistry meters = new SimpleMeterRegistry();
    var publisher = new FfmlibOutputPublisher(producer, properties, meters);
    var message = message();

    assertEquals(FfmlibOutputPublisher.PublicationOutcome.SKIPPED_DISABLED,
        publisher.publish(message).get(1, TimeUnit.SECONDS));

    verify(producer, never()).send(any(), any(), any());
    assertEquals(1.0, meters.counter("ode.ffmlib.output.skipped", "topic", "json",
        "reason", "disabled_topic").count());
    assertEquals(0.0, meters.counter("ode.ffmlib.output.failures", "topic", "json").count());
  }

  @Test
  @SuppressWarnings("unchecked")
  void kafkaConfirmationCompletesWithPublishedOutcomeAndRecordsLatency() throws Exception {
    KafkaTemplate<String, String> producer = mock(KafkaTemplate.class);
    var message = message();
    when(producer.send(message.topic(), message.key(), message.json()))
        .thenReturn(CompletableFuture.completedFuture(null));
    SimpleMeterRegistry meters = new SimpleMeterRegistry();
    var publisher = new FfmlibOutputPublisher(producer, new OdeKafkaProperties(), meters);

    assertEquals(FfmlibOutputPublisher.PublicationOutcome.PUBLISHED,
        publisher.publish(message).get(1, TimeUnit.SECONDS));
    assertEquals(0.0, meters.get("ode.ffmlib.output.in.flight").gauge().value());
    assertTrue(meters.get("ode.ffmlib.output.confirmation").timer().count() == 1);
  }

  @Test
  void unresolvedKafkaSendRemainsTrackedUntilItsActualCompletion() {
    KafkaTemplate<String, String> producer = mock(KafkaTemplate.class);
    CompletableFuture<org.springframework.kafka.support.SendResult<String, String>> send =
        new CompletableFuture<>();
    when(producer.send(any(), any(), any())).thenReturn(send);
    SimpleMeterRegistry meters = new SimpleMeterRegistry();
    var publisher = new FfmlibOutputPublisher(producer, new OdeKafkaProperties(), meters);

    var confirmation = publisher.publish(message());

    assertFalse(confirmation.isDone());
    assertEquals(1.0, meters.get("ode.ffmlib.output.in.flight").gauge().value());
    send.complete(null);
    assertEquals(0.0, meters.get("ode.ffmlib.output.in.flight").gauge().value());
    assertEquals(FfmlibOutputPublisher.PublicationOutcome.PUBLISHED, confirmation.join());
  }

  private static FfmlibDecodeService.PreparedDecodedMessage message() {
    return new FfmlibDecodeService.PreparedDecodedMessage("json", "key", "{}",
        new OdeMessageFrameMetadata(), "BSM", "udp", System.nanoTime(), System.nanoTime());
  }
}
