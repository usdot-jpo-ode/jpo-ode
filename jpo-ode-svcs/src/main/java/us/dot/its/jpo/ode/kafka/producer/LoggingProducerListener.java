package us.dot.its.jpo.ode.kafka.producer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.support.ProducerListener;
import org.springframework.lang.Nullable;

/**
 * The LoggingProducerListener is a Kafka ProducerListener implementation that handles logging for
 * Kafka producer events.
 *
 * <p>This class uses Slf4j for logging and should be added to Kafka producer
 * configurations where logging of producer activity is required.</p>
 *
 * @param <K> the type of the key for Kafka producer records
 * @param <V> the type of the value for Kafka producer records
 */
@Slf4j
public final class LoggingProducerListener<K, V>
    implements ProducerListener<K, V> {

  @Override
  public void onSuccess(ProducerRecord<K, V> producerRecord, RecordMetadata recordMetadata) {
    log.debug("Successfully produced to topic {} with key {} ", producerRecord.topic(), producerRecord.key());
  }

  @Override
  public void onError(
      ProducerRecord<K, V> producerRecord,
      @Nullable RecordMetadata recordMetadata,
      Exception exception) {
    log.error("Failed to produce to topic {} with key {} with exception: {}", producerRecord.topic(), producerRecord.key(), exception.getMessage());
  }
}
