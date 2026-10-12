package us.dot.its.jpo.ode.codec.ffmlib;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import java.time.Duration;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import us.dot.its.jpo.ode.codec.ffmlib.FfmlibDecodeService.PreparedDecodedMessage;
import us.dot.its.jpo.ode.kafka.OdeKafkaProperties;

/** Publishes FFM JSON and completes only after Kafka confirms the record. */
@Component
@ConditionalOnProperty(name = "ode.asn1.codec-mode", havingValue = "ffm")
public class FfmlibOutputPublisher {
  private final KafkaTemplate<String, String> output;
  private final MeterRegistry meters;
  private final Set<String> disabledTopics;
  private final AtomicInteger inFlight = new AtomicInteger();
  private final Map<String, Timer> confirmations = new ConcurrentHashMap<>();
  private final Map<String, Counter> failures = new ConcurrentHashMap<>();
  private final Map<String, Counter> produced = new ConcurrentHashMap<>();
  private final Map<String, Counter> skipped = new ConcurrentHashMap<>();

  /**
   * Creates the publisher for decoded JSON records.
   *
   * @param output dedicated FFM producer
   * @param properties topics that must not be written
   * @param meters output gauges and timers
   */
  public FfmlibOutputPublisher(
      @Qualifier("ffmlibOutputKafkaTemplate") KafkaTemplate<String, String> output,
      OdeKafkaProperties properties, MeterRegistry meters) {
    this.output = output;
    this.disabledTopics = properties.getDisabledTopics();
    this.meters = meters;
    Gauge.builder("ode.ffmlib.output.in.flight", inFlight, AtomicInteger::get).register(meters);
  }

  /**
   * Sends one decoded JSON record. A disabled topic is counted as a skip and is not sent. Kafka
   * publication failures complete the returned future exceptionally for the listener to retry.
   *
   * @param message decoded output
   * @return future with the explicit publication outcome, or an exceptional future on failure
   */
  public CompletableFuture<PublicationOutcome> publish(PreparedDecodedMessage message) {
    long start = System.nanoTime();
    CompletableFuture<PublicationOutcome> result = new CompletableFuture<>();
    if (isDisabled(message.topic())) {
      skipped(message.topic()).increment();
      result.complete(PublicationOutcome.SKIPPED_DISABLED);
      return result;
    }
    inFlight.incrementAndGet();
    try {
      produced(message.topic()).increment();
      String originIp = message.metadata().getOriginIp();
      if (originIp != null) {
        meters.counter("kafka.produced.rsu.messages", "topic", message.topic(),
            "rsu_ip", originIp).increment();
      }
      output.send(message.topic(), message.key(), message.json())
          .whenComplete((sent, error) -> finish(message, start, result, error));
    } catch (RuntimeException error) {
      finish(message, start, result, error);
    }
    return result;
  }

  /**
   * Reports whether publishing to the topic is disabled.
   *
   * @param topic Kafka topic name
   * @return true when the topic is in the disabled set
   */
  public boolean isDisabled(String topic) {
    return disabledTopics != null && disabledTopics.contains(topic);
  }

  private void finish(PreparedDecodedMessage message, long start,
      CompletableFuture<PublicationOutcome> result, Throwable error) {
    inFlight.decrementAndGet();
    if (error == null) {
      confirmations.computeIfAbsent(message.topic(), topic -> Timer.builder(
          "ode.ffmlib.output.confirmation").tag("topic", topic)
          .minimumExpectedValue(Duration.ofNanos(1_000)).publishPercentileHistogram()
          .register(meters))
          .record(System.nanoTime() - start, TimeUnit.NANOSECONDS);
      result.complete(PublicationOutcome.PUBLISHED);
    } else {
      failures(message.topic()).increment();
      result.completeExceptionally(error);
    }
  }

  private Counter produced(String topic) {
    return produced.computeIfAbsent(topic, name -> meters.counter("kafka.produced.messages",
        "topic", name));
  }

  private Counter failures(String topic) {
    return failures.computeIfAbsent(topic, name -> meters.counter("ode.ffmlib.output.failures",
        "topic", name));
  }

  private Counter skipped(String topic) {
    return skipped.computeIfAbsent(topic, name -> meters.counter("ode.ffmlib.output.skipped",
        "topic", name, "reason", "disabled_topic"));
  }

  /** Successful internal publication outcomes. Actual Kafka failures complete exceptionally. */
  public enum PublicationOutcome {
    PUBLISHED,
    SKIPPED_DISABLED
  }
}
