package us.dot.its.jpo.ode.codec.ffmlib;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.context.annotation.Conditional;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import us.dot.its.jpo.ode.codec.ffmlib.FfmlibDecodeService.PreparedDecodedMessage;
import us.dot.its.jpo.ode.codec.ffmlib.FfmlibOutputPublisher.PublicationOutcome;
import us.dot.its.jpo.ode.udp.UdpHexDecoder.UdpDecodeInput;
import us.dot.its.jpo.ode.uper.SupportedMessageType;

/**
 * Decodes UDP datagrams on the receiver thread and publishes decoded JSON without waiting for
 * Kafka confirmation (at-most-once). Decode failures are quarantined to the message type's
 * dead-letter topic; publication failures are counted and logged, and the datagram is dropped.
 */
@Component
@Slf4j
@Conditional(FfmlibUdpRoutingConditions.Direct.class)
public class FfmlibDirectUdpDecoder implements HealthIndicator {

  static final String INGEST_ROUTE = "direct";

  private final FfmlibDecodeService decoder;
  private final FfmlibOutputPublisher output;
  private final KafkaTemplate<String, String> quarantineProducer;
  private final MeterRegistry meters;
  private final int infrastructureHaltThreshold;
  private final Map<SupportedMessageType, AtomicInteger> consecutiveInfrastructureFailures =
      new ConcurrentHashMap<>();
  private final Map<String, String> unhealthyTypes = new ConcurrentHashMap<>();
  private final Map<String, Counter> publishFailures = new ConcurrentHashMap<>();
  private final Map<String, Counter> quarantineFailures = new ConcurrentHashMap<>();
  private final Map<SupportedMessageType, Counter> infrastructureQuarantines =
      new ConcurrentHashMap<>();

  /**
   * Creates the direct UDP decoder.
   *
   * @param decoder in-process FFM decoder
   * @param output decoded JSON publisher
   * @param quarantineProducer dead-letter producer, shared with decoded output
   * @param ffmlibProperties infrastructure failure threshold for health
   * @param meters failure counters
   */
  public FfmlibDirectUdpDecoder(FfmlibDecodeService decoder, FfmlibOutputPublisher output,
      @Qualifier("ffmlibOutputKafkaTemplate") KafkaTemplate<String, String> quarantineProducer,
      FfmlibProperties ffmlibProperties, MeterRegistry meters) {
    this.decoder = decoder;
    this.output = output;
    this.quarantineProducer = quarantineProducer;
    this.meters = meters;
    this.infrastructureHaltThreshold = ffmlibProperties.getInfrastructureHaltThreshold();
  }

  /**
   * Decodes one datagram and publishes its JSON. Returns once the send is submitted; Kafka
   * confirmation is handled asynchronously.
   *
   * @param input stripped MessageFrame bytes and metadata; the metadata is updated in place
   * @param type message type of the receiving port or detected by the generic receiver
   * @param rawTopic raw encoded topic for the type, used to name the dead-letter topic
   * @param rawJsonOnFailure raw encoded JSON for the dead-letter record, built only on failure
   */
  public void decodeAndPublish(UdpDecodeInput input, SupportedMessageType type, String rawTopic,
      Supplier<String> rawJsonOnFailure) {
    PreparedDecodedMessage prepared;
    try {
      prepared = decoder.prepareRaw(input.metadata(), input.uperBytes(), null, type);
    } catch (RuntimeException error) {
      reject(type, rawTopic, rawJsonOnFailure, error);
      return;
    }
    recordSuccess(type);
    output.publish(prepared).whenComplete((outcome, error) -> {
      if (error != null) {
        decoder.recordRawPublishFailure(prepared);
        publishFailures.computeIfAbsent(prepared.topic(), topic -> meters.counter(
            "ode.ffmlib.direct.publish.failures", "topic", topic)).increment();
        log.error("Unable to publish directly decoded {} to {}; the datagram is dropped", type,
            prepared.topic(), error);
      } else if (outcome == PublicationOutcome.PUBLISHED) {
        decoder.recordRawConfirmed(prepared);
      }
    });
  }

  @Override
  public Health health() {
    if (unhealthyTypes.isEmpty()) {
      return Health.up().withDetail("ffmUdpRouting", INGEST_ROUTE).build();
    }
    return Health.down().withDetail("ffmUdpRouting", INGEST_ROUTE)
        .withDetails(Map.copyOf(unhealthyTypes)).build();
  }

  private void reject(SupportedMessageType type, String rawTopic,
      Supplier<String> rawJsonOnFailure, RuntimeException error) {
    String category;
    if (FfmlibFailures.findInfrastructureFailure(error) != null) {
      category = FfmlibFailures.INFRASTRUCTURE;
      int consecutive = consecutiveInfrastructureFailures
          .computeIfAbsent(type, key -> new AtomicInteger()).incrementAndGet();
      if (consecutive >= infrastructureHaltThreshold) {
        unhealthyTypes.put(type.name(), "infrastructure_failures_" + consecutive);
      }
      infrastructureQuarantines.computeIfAbsent(type, key -> meters.counter(
          "ode.ffmlib.infrastructure.quarantined", "type", key.name())).increment();
      log.error("FFM native decode failed operationally for a {} datagram ({} consecutive);"
          + " quarantining it", type, consecutive, error);
    } else {
      category = FfmlibFailures.category(error, true, false);
      recordSuccess(type);
      log.debug("Quarantining undecodable {} datagram as {}", type, category, error);
    }
    quarantine(rawTopic, rawJsonOnFailure, category);
  }

  private void recordSuccess(SupportedMessageType type) {
    AtomicInteger failures = consecutiveInfrastructureFailures.get(type);
    if (failures != null) {
      failures.set(0);
    }
    unhealthyTypes.remove(type.name());
  }

  private void quarantine(String rawTopic, Supplier<String> rawJsonOnFailure, String category) {
    String dltTopic = FfmlibDeadLetterTopics.forRawTopic(rawTopic);
    try {
      ProducerRecord<String, String> deadLetter = new ProducerRecord<>(dltTopic, null,
          rawJsonOnFailure.get());
      deadLetter.headers()
          .add("source-topic", bytes(rawTopic))
          .add("source-timestamp", bytes(Long.toString(System.currentTimeMillis())))
          .add("failure-category", bytes(category))
          .add("ingest-route", bytes(INGEST_ROUTE))
          .add("attempt-count", bytes("1"));
      quarantineProducer.send(deadLetter).whenComplete((sent, error) -> {
        if (error != null) {
          quarantineFailed(dltTopic, error);
        }
      });
    } catch (RuntimeException error) {
      quarantineFailed(dltTopic, error);
    }
  }

  private void quarantineFailed(String dltTopic, Throwable error) {
    quarantineFailures.computeIfAbsent(dltTopic, topic -> meters.counter(
        "ode.ffmlib.direct.quarantine.failures", "topic", topic)).increment();
    log.error("Unable to quarantine a directly decoded datagram to {}", dltTopic, error);
  }

  private static byte[] bytes(String value) {
    return value.getBytes(StandardCharsets.UTF_8);
  }
}
