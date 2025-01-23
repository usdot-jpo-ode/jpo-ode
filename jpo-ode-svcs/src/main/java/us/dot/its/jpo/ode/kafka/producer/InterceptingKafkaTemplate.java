package us.dot.its.jpo.ode.kafka.producer;

import io.micrometer.observation.Observation;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.SendResult;
import org.springframework.lang.NonNull;

/**
 * InterceptingKafkaTemplate is an extension of the KafkaTemplate class designed to introduce a
 * mechanism for selectively preventing messages from being sent to certain Kafka topics. This
 * functionality is implemented via a set of "disabledTopics", which contains topic names that
 * should be blocked from receiving messages.
 *
 * @param <K> the type of message key
 * @param <V> the type of message value
 */
@Slf4j
public class InterceptingKafkaTemplate<K, V> extends KafkaTemplate<K, V> {

  private final Set<String> disabledTopics;

  /**
   * Create an instance using the supplied producer factory and autoFlush false.
   *
   * @param producerFactory the producer factory.
   */
  public InterceptingKafkaTemplate(
      ProducerFactory<K, V> producerFactory, Set<String> disabledTopics) {
    super(producerFactory);
    this.disabledTopics = disabledTopics;
  }

  /**
   * Send the producer record if the producerRecord's topic is not contained in the disabledTopics.
   *
   * @param producerRecord the producer record.
   * @param observation    the observation.
   * @return a Future for the {@link RecordMetadata RecordMetadata}.
   */
  @Override
  protected CompletableFuture<SendResult<K, V>> doSend(
      final ProducerRecord<K, V> producerRecord,
      @NonNull Observation observation) {
    if (disabledTopics.contains(producerRecord.topic())) {
      log.debug("Blocked attempt to send data to disabled topic {}", producerRecord.topic());
      return new CompletableFuture<>();
    }

    return super.doSend(producerRecord, observation);
  }

}
