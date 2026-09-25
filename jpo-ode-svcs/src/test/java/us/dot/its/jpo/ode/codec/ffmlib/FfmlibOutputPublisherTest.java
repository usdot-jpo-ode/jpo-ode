package us.dot.its.jpo.ode.codec.ffmlib;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
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
  void producerFailureCompletesWithoutAnotherSend() {
    KafkaTemplate<String, String> producer = mock(KafkaTemplate.class);
    FfmlibDecodeService decoder = mock(FfmlibDecodeService.class);
    var message = message();
    when(producer.send(message.topic(), message.key(), message.json()))
        .thenReturn(CompletableFuture.failedFuture(new TimeoutException("broker")));
    var publisher = new FfmlibOutputPublisher(producer, decoder, new OdeKafkaProperties(),
        new SimpleMeterRegistry());

    assertThrows(ExecutionException.class, () -> publisher.publish(message).get(5, TimeUnit.SECONDS));

    verify(producer, times(1)).send(message.topic(), message.key(), message.json());
    verify(decoder).recordRawPublishFailure(message);
  }

  @Test
  @SuppressWarnings("unchecked")
  void disabledTopicFailsPromptlyWithoutSending() {
    KafkaTemplate<String, String> producer = mock(KafkaTemplate.class);
    FfmlibDecodeService decoder = mock(FfmlibDecodeService.class);
    OdeKafkaProperties properties = new OdeKafkaProperties();
    properties.setDisabledTopics(Set.of("json"));
    var publisher = new FfmlibOutputPublisher(producer, decoder, properties,
        new SimpleMeterRegistry());

    assertThrows(ExecutionException.class, () -> publisher.publish(message()).get(1,
        TimeUnit.SECONDS));
    verify(producer, times(0)).send("json", "key", "{}");
  }

  private static FfmlibDecodeService.PreparedDecodedMessage message() {
    return new FfmlibDecodeService.PreparedDecodedMessage("json", "key", "{}",
        new OdeMessageFrameMetadata(), "BSM", "udp", System.nanoTime(), System.nanoTime());
  }
}
