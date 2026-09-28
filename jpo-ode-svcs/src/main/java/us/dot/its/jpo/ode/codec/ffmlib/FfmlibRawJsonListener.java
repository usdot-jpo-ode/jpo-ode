package us.dot.its.jpo.ode.codec.ffmlib;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import us.dot.its.jpo.ode.codec.ffmlib.FfmlibDecodeService.PreparedDecodedMessage;
import us.dot.its.jpo.ode.codec.ffmlib.FfmlibOutputPublisher.PublicationOutcome;
import us.dot.its.jpo.ode.kafka.listeners.json.RawEncodedJsonService;
import us.dot.its.jpo.ode.kafka.topics.RawEncodedJsonTopics;
import us.dot.its.jpo.ode.uper.SupportedMessageType;

/** Consumes durable raw UDP records, confirms decoded JSON or quarantine, then commits the input. */
@Component
@Slf4j
@ConditionalOnProperty(name = "ode.asn1.codec-mode", havingValue = "ffm")
public class FfmlibRawJsonListener implements HealthIndicator {

  private static final int MAX_PUBLISH_ATTEMPTS = 3;
  private static final long PUBLISH_TIMEOUT_SECONDS = 10;
  private static final long RETRY_DELAY_MILLIS = 25;
  private static final Duration PUBLISH_CONFIRMATION_BUDGET = Duration.ofSeconds(30);

  private final RawEncodedJsonService rawService;
  private final FfmlibDecodeService decoder;
  private final FfmlibOutputPublisher output;
  private final KafkaTemplate<String, String> quarantineProducer;
  private final RawEncodedJsonTopics topics;
  private final FfmlibCommitTracker commitTracker;
  private final Timer rawAgeTimer;
  private final Timer parseTimer;
  private final long publishConfirmationBudgetNanos;
  private final Map<String, String> unhealthyListeners = new ConcurrentHashMap<>();
  private volatile boolean startupComplete;

  /**
   * Creates the durable raw-message listener.
   *
   * @param rawService parses the established raw JSON contract
   * @param decoder prepares native decoding and output metadata
   * @param output confirms decoded JSON publication
   * @param quarantineProducer confirms dead-letter publication
   * @param topics configured raw topic names
   */
  @Autowired
  public FfmlibRawJsonListener(RawEncodedJsonService rawService, FfmlibDecodeService decoder,
      FfmlibOutputPublisher output,
      @org.springframework.beans.factory.annotation.Qualifier("ffmlibOutputKafkaTemplate")
      KafkaTemplate<String, String> quarantineProducer,
      RawEncodedJsonTopics topics, FfmlibCommitTracker commitTracker, MeterRegistry meters) {
    this(rawService, decoder, output, quarantineProducer, topics, commitTracker, meters,
        PUBLISH_CONFIRMATION_BUDGET);
  }

  FfmlibRawJsonListener(RawEncodedJsonService rawService, FfmlibDecodeService decoder,
      FfmlibOutputPublisher output,
      @org.springframework.beans.factory.annotation.Qualifier("ffmlibOutputKafkaTemplate")
      KafkaTemplate<String, String> quarantineProducer,
      RawEncodedJsonTopics topics, FfmlibCommitTracker commitTracker, MeterRegistry meters,
      Duration publishConfirmationBudget) {
    this.rawService = rawService;
    this.decoder = decoder;
    this.output = output;
    this.quarantineProducer = quarantineProducer;
    this.topics = topics;
    this.commitTracker = commitTracker;
    this.publishConfirmationBudgetNanos = publishConfirmationBudget.toNanos();
    this.rawAgeTimer = Timer.builder("ode.ffmlib.raw.record.age")
        .description("Millisecond-quantized Kafka CreateTime to listener-entry age")
        .minimumExpectedValue(Duration.ofMillis(1)).publishPercentileHistogram().register(meters);
    this.parseTimer = Timer.builder("ode.ffmlib.decode.stage")
        .tag("stage", "parse").minimumExpectedValue(Duration.ofNanos(1_000))
        .publishPercentileHistogram().register(meters);
  }

  @KafkaListener(id = "RawEncodedBSMJsonRouter", groupId = "RawEncodedBSMJsonRouter",
      topics = "${ode.kafka.topics.raw-encoded-json.bsm}",
      containerFactory = "ffmlibKafkaListenerContainerFactory",
      concurrency = "${ode.ffmlib.listener-concurrency:4}", autoStartup = "false")
  public void bsm(ConsumerRecord<String, String> record, Acknowledgment acknowledgment) {
    consume(SupportedMessageType.BSM, "RawEncodedBSMJsonRouter", record, acknowledgment);
  }

  @KafkaListener(id = "RawEncodedSPATJsonRouter", groupId = "RawEncodedSPATJsonRouter",
      topics = "${ode.kafka.topics.raw-encoded-json.spat}",
      containerFactory = "ffmlibKafkaListenerContainerFactory",
      concurrency = "${ode.ffmlib.listener-concurrency:4}", autoStartup = "false")
  public void spat(ConsumerRecord<String, String> record, Acknowledgment acknowledgment) {
    consume(SupportedMessageType.SPAT, "RawEncodedSPATJsonRouter", record, acknowledgment);
  }

  @KafkaListener(id = "RawEncodedMAPJsonRouter", groupId = "RawEncodedMAPJsonRouter",
      topics = "${ode.kafka.topics.raw-encoded-json.map}",
      containerFactory = "ffmlibKafkaListenerContainerFactory",
      concurrency = "${ode.ffmlib.listener-concurrency:4}", autoStartup = "false")
  public void map(ConsumerRecord<String, String> record, Acknowledgment acknowledgment) {
    consume(SupportedMessageType.MAP, "RawEncodedMAPJsonRouter", record, acknowledgment);
  }

  @KafkaListener(id = "RawEncodedTIMJsonRouter", groupId = "RawEncodedTIMJsonRouter",
      topics = "${ode.kafka.topics.raw-encoded-json.tim}",
      containerFactory = "ffmlibKafkaListenerContainerFactory",
      concurrency = "${ode.ffmlib.listener-concurrency:4}", autoStartup = "false")
  public void tim(ConsumerRecord<String, String> record, Acknowledgment acknowledgment) {
    consume(SupportedMessageType.TIM, "RawEncodedTIMJsonRouter", record, acknowledgment);
  }

  @KafkaListener(id = "RawEncodedSRMJsonRouter", groupId = "RawEncodedSRMJsonRouter",
      topics = "${ode.kafka.topics.raw-encoded-json.srm}",
      containerFactory = "ffmlibKafkaListenerContainerFactory",
      concurrency = "${ode.ffmlib.listener-concurrency:4}", autoStartup = "false")
  public void srm(ConsumerRecord<String, String> record, Acknowledgment acknowledgment) {
    consume(SupportedMessageType.SRM, "RawEncodedSRMJsonRouter", record, acknowledgment);
  }

  @KafkaListener(id = "RawEncodedSSMJsonRouter", groupId = "RawEncodedSSMJsonRouter",
      topics = "${ode.kafka.topics.raw-encoded-json.ssm}",
      containerFactory = "ffmlibKafkaListenerContainerFactory",
      concurrency = "${ode.ffmlib.listener-concurrency:4}", autoStartup = "false")
  public void ssm(ConsumerRecord<String, String> record, Acknowledgment acknowledgment) {
    consume(SupportedMessageType.SSM, "RawEncodedSSMJsonRouter", record, acknowledgment);
  }

  @KafkaListener(id = "RawEncodedPSMJsonRouter", groupId = "RawEncodedPSMJsonRouter",
      topics = "${ode.kafka.topics.raw-encoded-json.psm}",
      containerFactory = "ffmlibKafkaListenerContainerFactory",
      concurrency = "${ode.ffmlib.listener-concurrency:4}", autoStartup = "false")
  public void psm(ConsumerRecord<String, String> record, Acknowledgment acknowledgment) {
    consume(SupportedMessageType.PSM, "RawEncodedPSMJsonRouter", record, acknowledgment);
  }

  @KafkaListener(id = "RawEncodedSDSMJsonRouter", groupId = "RawEncodedSDSMJsonRouter",
      topics = "${ode.kafka.topics.raw-encoded-json.sdsm}",
      containerFactory = "ffmlibKafkaListenerContainerFactory",
      concurrency = "${ode.ffmlib.listener-concurrency:4}", autoStartup = "false")
  public void sdsm(ConsumerRecord<String, String> record, Acknowledgment acknowledgment) {
    consume(SupportedMessageType.SDSM, "RawEncodedSDSMJsonRouter", record, acknowledgment);
  }

  @KafkaListener(id = "RawEncodedRTCMJsonRouter", groupId = "RawEncodedRTCMJsonRouter",
      topics = "${ode.kafka.topics.raw-encoded-json.rtcm}",
      containerFactory = "ffmlibKafkaListenerContainerFactory",
      concurrency = "${ode.ffmlib.listener-concurrency:4}", autoStartup = "false")
  public void rtcm(ConsumerRecord<String, String> record, Acknowledgment acknowledgment) {
    consume(SupportedMessageType.RTCM, "RawEncodedRTCMJsonRouter", record, acknowledgment);
  }

  @KafkaListener(id = "RawEncodedRSMJsonRouter", groupId = "RawEncodedRSMJsonRouter",
      topics = "${ode.kafka.topics.raw-encoded-json.rsm}",
      containerFactory = "ffmlibKafkaListenerContainerFactory",
      concurrency = "${ode.ffmlib.listener-concurrency:4}", autoStartup = "false")
  public void rsm(ConsumerRecord<String, String> record, Acknowledgment acknowledgment) {
    consume(SupportedMessageType.RSM, "RawEncodedRSMJsonRouter", record, acknowledgment);
  }

  @Override
  public Health health() {
    if (!startupComplete) {
      return Health.down().withDetail("ffmStartup", "listeners_not_ready")
          .withDetail("ffmListeners", Map.copyOf(unhealthyListeners)).build();
    }
    if (unhealthyListeners.isEmpty()) {
      return Health.up().build();
    }
    return Health.down().withDetail("ffmListeners", Map.copyOf(unhealthyListeners)).build();
  }

  /** Marks the service ready after every configured raw consumer has joined its group. */
  void markStartupComplete() {
    startupComplete = true;
  }

  /** Records startup failure while keeping the service not ready. */
  void markStartupFailure(Throwable error) {
    startupComplete = false;
    unhealthyListeners.put("startup", error.getClass().getSimpleName());
  }

  /** Marks a raw listener unhealthy after Kafka reports an offset commit failure. */
  void recordCommitFailure(String listenerId, Exception error) {
    unhealthyListeners.put(listenerId, "offset_commit_" + error.getClass().getSimpleName());
    log.error("Offset commit failed for FFM listener {}; confirmed output may replay", listenerId,
        error);
  }

  private void consume(SupportedMessageType type, String listenerId,
      ConsumerRecord<String, String> record, Acknowledgment acknowledgment) {
    PreparedDecodedMessage prepared;
    long listenerEntry = System.currentTimeMillis();
    if (record.timestamp() > 0) {
      rawAgeTimer.record(Math.max(0, listenerEntry - record.timestamp()), TimeUnit.MILLISECONDS);
    }
    long parseStart = System.nanoTime();
    RawEncodedJsonService.FfmRawRecord raw;
    try {
      raw = rawService.parseFfmRecord(record.value(), type);
    } catch (Exception error) {
      parseTimer.record(System.nanoTime() - parseStart, TimeUnit.NANOSECONDS);
      quarantine(record, listenerId, rawTopic(type), category(error), 1);
      acknowledge(acknowledgment);
      return;
    }
    parseTimer.record(System.nanoTime() - parseStart, TimeUnit.NANOSECONDS);

    try {
      prepared = decoder.prepareRaw(raw.metadata(), raw.uperBytes(), record.key(), type,
          raw.originalBytes());
    } catch (Exception error) {
      quarantine(record, listenerId, rawTopic(type), category(error), 1);
      acknowledge(acknowledgment);
      return;
    }

    PublicationOutcome outcome;
    try {
      outcome = publishWithRetry(prepared);
    } catch (RuntimeException error) {
      unhealthyListeners.put(listenerId, error.getClass().getSimpleName());
      throw error;
    }
    if (outcome == PublicationOutcome.SKIPPED_DISABLED) {
      acknowledge(acknowledgment);
      return;
    }
    decoder.recordRawConfirmed(prepared);
    acknowledge(acknowledgment);
  }

  private void acknowledge(Acknowledgment acknowledgment) {
    commitTracker.beginCommit();
    try {
      acknowledgment.acknowledge();
      commitTracker.completeSynchronousCommit();
    } catch (RuntimeException error) {
      commitTracker.cancelCommit();
      throw error;
    }
  }

  private PublicationOutcome publishWithRetry(PreparedDecodedMessage message) {
    long deadline = System.nanoTime() + publishConfirmationBudgetNanos;
    for (int attempt = 1; attempt <= MAX_PUBLISH_ATTEMPTS; attempt++) {
      long remaining = deadline - System.nanoTime();
      if (remaining <= 0) {
        throw failedPublication(message, new TimeoutException(
            "FFM output was not confirmed within the 30-second overall budget"));
      }
      var confirmation = output.publish(message);
      try {
        remaining = deadline - System.nanoTime();
        if (remaining <= 0) {
          throw new TimeoutException(
              "FFM output was not confirmed within the 30-second overall budget");
        }
        return confirmation.get(remaining, TimeUnit.NANOSECONDS);
      } catch (InterruptedException error) {
        Thread.currentThread().interrupt();
        decoder.recordRawPublishFailure(message);
        throw new IllegalStateException("FFM output confirmation interrupted", error);
      } catch (TimeoutException error) {
        // The Kafka send may still complete. Do not overlap it with another application send.
        throw failedPublication(message, error);
      } catch (ExecutionException error) {
        if (attempt == MAX_PUBLISH_ATTEMPTS || System.nanoTime() >= deadline) {
          throw failedPublication(message, error);
        }
        try {
          long delayNanos = Math.min(TimeUnit.MILLISECONDS.toNanos(RETRY_DELAY_MILLIS),
              Math.max(0, deadline - System.nanoTime()));
          if (delayNanos > 0) {
            TimeUnit.NANOSECONDS.sleep(delayNanos);
          }
        } catch (InterruptedException interrupted) {
          Thread.currentThread().interrupt();
          decoder.recordRawPublishFailure(message);
          throw new IllegalStateException("FFM publication retry interrupted", interrupted);
        }
      }
    }
    throw new IllegalStateException("FFM publication attempts ended unexpectedly");
  }

  private RuntimeException failedPublication(PreparedDecodedMessage message, Throwable error) {
    decoder.recordRawPublishFailure(message);
    return new ExhaustedPublishAttemptsException(error);
  }

  private void quarantine(ConsumerRecord<String, String> original, String listenerId,
      String sourceTopic, String category, int attempts) {
    String dltTopic = sourceTopic + ".FFM.DLT";
    ProducerRecord<String, String> deadLetter = new ProducerRecord<>(dltTopic, original.key(),
        original.value());
    deadLetter.headers()
        .add("source-topic", bytes(original.topic()))
        .add("source-partition", bytes(Integer.toString(original.partition())))
        .add("source-offset", bytes(Long.toString(original.offset())))
        .add("source-timestamp", bytes(Long.toString(original.timestamp())))
        .add("failure-category", bytes(category))
        .add("attempt-count", bytes(Integer.toString(attempts)));
    try {
      quarantineProducer.send(deadLetter).get(PUBLISH_TIMEOUT_SECONDS, TimeUnit.SECONDS);
    } catch (InterruptedException error) {
      Thread.currentThread().interrupt();
      unhealthyListeners.put(listenerId, error.getClass().getSimpleName());
      log.error("Interrupted publishing FFM quarantine record to {}; leaving {} offset {} "
          + "uncommitted", dltTopic, original.topic(), original.offset(), error);
      throw new IllegalStateException("FFM quarantine publication interrupted", error);
    } catch (Exception error) {
      unhealthyListeners.put(listenerId, error.getClass().getSimpleName());
      log.error("Unable to publish FFM quarantine record to {}; leaving {} offset {} uncommitted",
          dltTopic, original.topic(), original.offset(), error);
      throw new IllegalStateException("FFM quarantine publication failed", error);
    }
  }

  private String rawTopic(SupportedMessageType type) {
    return switch (type) {
      case BSM -> topics.getBsm();
      case SPAT -> topics.getSpat();
      case MAP -> topics.getMap();
      case TIM -> topics.getTim();
      case SRM -> topics.getSrm();
      case SSM -> topics.getSsm();
      case PSM -> topics.getPsm();
      case SDSM -> topics.getSdsm();
      case RTCM -> topics.getRtcm();
      case RSM -> topics.getRsm();
    };
  }

  private static String category(Exception error) {
    if (error instanceof UnsupportedOperationException
        || error.getCause() instanceof UnsupportedOperationException) {
      return "unsupported_signed";
    }
    if (error instanceof ExhaustedPublishAttemptsException) {
      return "publish_failure";
    }
    return error instanceof JsonProcessingException ? "malformed_json" : "malformed_payload";
  }

  private static byte[] bytes(String value) {
    return value.getBytes(StandardCharsets.UTF_8);
  }

  private static final class ExhaustedPublishAttemptsException extends RuntimeException {
    private ExhaustedPublishAttemptsException(Throwable cause) {
      super("FFM output publication failed after retries", cause);
    }
  }
}
