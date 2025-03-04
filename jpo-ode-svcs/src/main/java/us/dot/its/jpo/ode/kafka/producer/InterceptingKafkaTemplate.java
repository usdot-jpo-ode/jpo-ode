package us.dot.its.jpo.ode.kafka.producer;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
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

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * InterceptingKafkaTemplate is an extension of the KafkaTemplate class designed
 * to introduce a
 * mechanism for selectively preventing messages from being sent to certain
 * Kafka topics. This
 * functionality is implemented via a set of "disabledTopics", which contains
 * topic names that
 * should be blocked from receiving messages.
 *
 * @param <K> the type of message key
 * @param <V> the type of message value
 */
@Slf4j
public class InterceptingKafkaTemplate<K, V> extends KafkaTemplate<K, V> {

  private final Set<String> disabledTopics;
  private final MeterRegistry meterRegistry;

  private ObjectMapper mapper = new ObjectMapper();
  JsonFactory factory = mapper.getFactory();

  /**
   * Create an instance using the supplied producer factory and autoFlush false.
   *
   * @param producerFactory the producer factory.
   */
  public InterceptingKafkaTemplate(
      ProducerFactory<K, V> producerFactory, Set<String> disabledTopics, MeterRegistry meterRegistry) {
    super(producerFactory);
    this.disabledTopics = disabledTopics;
    this.meterRegistry = meterRegistry;
  }

  /**
   * Send the producer record if the producerRecord's topic is not contained in
   * the disabledTopics.
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

    // For String values, extract the originIp from the JSON metadata if it is
    // present
    String originIp = null;
    if (producerRecord.value() instanceof String) {
      String stringValue = (String) producerRecord.value();
      try {
        JsonNode rootNode = mapper.readTree(stringValue);
        if (rootNode.has("metadata")) {
          JsonNode metadataNode = rootNode.get("metadata");
          if (metadataNode.has("originIp")) {
            originIp = metadataNode.get("originIp").asText();
          }
        }
      } catch (JsonProcessingException e) {
        log.error("Error processing JSON", e);
      }
    }

    // If the originIp is not null, increment the RSU's messages counter for the
    // topic being produced to
    if (originIp != null) {
      Counter.builder("kafka.produced.rsu.messages")
          .description("Number of produced Kafka messages by RSU")
          .tags("topic", producerRecord.topic(), "rsu_ip", originIp)
          .register(meterRegistry)
          .increment();
    }

    // Increment the total number of produced messages for the topic being produced
    // to for overall message
    Counter.builder("kafka.produced.messages")
        .description("Number of produced Kafka messages")
        .tags("topic", producerRecord.topic())
        .register(meterRegistry)
        .increment();

    return super.doSend(producerRecord, observation);
  }

}
