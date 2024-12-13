package us.dot.its.jpo.ode.kafka.producer;

import java.util.Map;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

/**
 * Through a set of disabled topics provided during initialization, this interceptor enforces that
 * messages are not sent to these topics. If an attempt is made to send a message to a disabled
 * topic, the interceptor throws a DisabledTopicException, effectively preventing the message from
 * being dispatched.
 *
 * </p>The class also provides default logging behavior upon message acknowledgement and
 * during interceptor closure.
 *
 * @param <K> the type of the key for Kafka producer records
 * @param <V> the type of the value for Kafka producer records
 */
@Slf4j
public class DisabledTopicsProducerInterceptor<K, V>
    implements ProducerInterceptor<K, V> {

  private final Set<String> disabledTopics;

  /**
   * Constructs a DisabledTopicsProducerInterceptor with a specified set of disabled topics.
   * This set determines which topics are restricted from message dispatching.
   *
   * @param disabledTopics a set of topic names that are marked as disabled. Messages sent to these
   *                       topics will result in a DisabledTopicException being thrown, ensuring
   *                       they are not dispatched.
   */
  protected DisabledTopicsProducerInterceptor(Set<String> disabledTopics) {
    this.disabledTopics = disabledTopics;
  }

  @Override
  public ProducerRecord<K, V> onSend(ProducerRecord<K, V> producerRecord) {
    if (disabledTopics.contains(producerRecord.topic())) {
      throw new DisabledTopicException(producerRecord.topic());
    }
    return producerRecord;
  }

  @Override
  public void onAcknowledgement(RecordMetadata recordMetadata, Exception e) {
    log.debug("Acknowledged message with offset {} on partition {}", recordMetadata.offset(),
        recordMetadata.partition());
    if (e != null) {
      log.error("Error acknowledging message", e);
    }
  }

  @Override
  public void close() {
    log.debug("Closing ProducerInterceptor");
  }

  @Override
  public void configure(Map<String, ?> configs) {
    // Default implementation
  }
}