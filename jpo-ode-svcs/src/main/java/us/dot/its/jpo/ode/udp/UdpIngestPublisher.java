package us.dot.its.jpo.ode.udp;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import java.net.DatagramPacket;
import java.time.Duration;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import us.dot.its.jpo.ode.codec.ffmlib.Asn1CodecModeProperties;
import us.dot.its.jpo.ode.codec.ffmlib.FfmlibDirectUdpDecoder;
import us.dot.its.jpo.ode.codec.ffmlib.FfmlibProperties;
import us.dot.its.jpo.ode.model.OdeAsn1Data;
import us.dot.its.jpo.ode.model.OdeAsn1Payload;
import us.dot.its.jpo.ode.model.OdeLogMetadata.RecordType;
import us.dot.its.jpo.ode.model.OdeMessageFrameMetadata.Source;
import us.dot.its.jpo.ode.model.OdeMsgMetadata.GeneratedBy;
import us.dot.its.jpo.ode.udp.UdpHexDecoder.UdpDecodeInput;
import us.dot.its.jpo.ode.uper.SupportedMessageType;
import us.dot.its.jpo.ode.util.JsonUtils;

/**
 * Publishes received UDP datagrams. External mode and FFM raw-topic routing publish every datagram
 * to its raw encoded Kafka topic for later decoding. FFM direct routing decodes on the receiver
 * thread and publishes decoded JSON, publishing the raw record too unless
 * {@code ode.ffmlib.raw-publish=false}.
 */
@Component
@Slf4j
public class UdpIngestPublisher {

  private static final Map<SupportedMessageType, Profile> PROFILES = profiles();

  private final KafkaTemplate<String, String> rawKafka;
  private final FfmlibDirectUdpDecoder direct;
  private final boolean rawPublish;
  private final MeterRegistry meters;
  private final AtomicInteger inFlight = new AtomicInteger();
  private final Map<String, Timer> confirmations = new ConcurrentHashMap<>();
  private final Map<String, Timer> receiptToConfirmations = new ConcurrentHashMap<>();
  private final Map<String, Counter> failures = new ConcurrentHashMap<>();

  /**
   * Creates the UDP publisher, using the FFM raw producer and, for direct routing, the receiver
   * decoder when FFM mode is active.
   */
  @Autowired
  public UdpIngestPublisher(
      @Qualifier("kafkaTemplate") KafkaTemplate<String, String> externalKafka,
      @Qualifier("ffmlibRawKafkaTemplate")
      ObjectProvider<KafkaTemplate<String, String>> ffmlibRawKafka,
      ObjectProvider<FfmlibDirectUdpDecoder> directDecoder,
      Asn1CodecModeProperties mode, FfmlibProperties ffmlibProperties, MeterRegistry meters) {
    this(selectProducer(externalKafka, ffmlibRawKafka, mode.isFfm()),
        mode.isFfm() ? directDecoder.getIfAvailable() : null,
        !mode.isFfm() || ffmlibProperties.publishesRawUdp(), meters);
  }

  UdpIngestPublisher(KafkaTemplate<String, String> rawKafka, MeterRegistry meters) {
    this(rawKafka, null, true, meters);
  }

  UdpIngestPublisher(KafkaTemplate<String, String> rawKafka, FfmlibDirectUdpDecoder direct,
      boolean rawPublish, MeterRegistry meters) {
    this.rawKafka = rawKafka;
    this.direct = direct;
    this.rawPublish = direct == null || rawPublish;
    this.meters = meters;
    Gauge.builder("ode.ffmlib.raw.publication.in.flight", inFlight, AtomicInteger::get)
        .register(meters);
  }

  /** Test helper that uses the supplied raw producer. */
  public static UdpIngestPublisher rawOnly(KafkaTemplate<String, String> rawKafka) {
    return new UdpIngestPublisher(rawKafka,
        new io.micrometer.core.instrument.simple.SimpleMeterRegistry());
  }

  /** Test helper for FFM direct routing with the supplied raw producer and decoder. */
  public static UdpIngestPublisher direct(KafkaTemplate<String, String> rawKafka,
      FfmlibDirectUdpDecoder direct, boolean rawPublish) {
    return new UdpIngestPublisher(rawKafka, direct, rawPublish,
        new io.micrometer.core.instrument.simple.SimpleMeterRegistry());
  }

  private static KafkaTemplate<String, String> selectProducer(
      KafkaTemplate<String, String> externalKafka,
      ObjectProvider<KafkaTemplate<String, String>> ffmlibRawKafka,
      boolean ffmlibMode) {
    KafkaTemplate<String, String> producer = ffmlibMode ? ffmlibRawKafka.getIfAvailable() : null;
    return producer == null ? externalKafka : producer;
  }

  /**
   * Publishes one UDP datagram. The raw JSON contract is unchanged in every route.
   *
   * @param packet received UDP datagram
   * @param type J2735 type used to strip transport headers and prepare metadata
   * @param rawTopic configured raw topic for this message type
   * @throws InvalidPayloadException when the packet has no matching start flag
   */
  public void publish(DatagramPacket packet, SupportedMessageType type, String rawTopic)
      throws InvalidPayloadException {
    long receivedAt = System.nanoTime();
    UdpDecodeInput input = prepare(packet, type);
    // Serialize the raw record before direct decoding, which updates the metadata in place.
    String rawJson = rawPublish ? toRawJson(input) : null;
    if (rawPublish) {
      publishRaw(rawTopic, rawJson, receivedAt);
    }
    if (direct != null) {
      direct.decodeAndPublish(input, type, rawTopic,
          () -> rawJson != null ? rawJson : rebuildRawJson(packet, type));
    }
  }

  private static UdpDecodeInput prepare(DatagramPacket packet, SupportedMessageType type)
      throws InvalidPayloadException {
    Profile profile = PROFILES.get(type);
    return UdpHexDecoder.prepareDecodeInput(packet, type, profile.recordType(),
        profile.source(), profile.generatedBy(), profile.includeDetails());
  }

  private static String toRawJson(UdpDecodeInput input) {
    OdeAsn1Data data = new OdeAsn1Data(input.metadata(), new OdeAsn1Payload(input.uperBytes()));
    return JsonUtils.toJson(data, false);
  }

  /**
   * Rebuilds the raw record for a dead-letter write when raw publishing is off. Decode failures
   * are reported synchronously, so the receiver has not reused the packet buffer yet.
   */
  private static String rebuildRawJson(DatagramPacket packet, SupportedMessageType type) {
    try {
      return toRawJson(prepare(packet, type));
    } catch (InvalidPayloadException error) {
      throw new IllegalStateException("UDP datagram could not be prepared a second time", error);
    }
  }

  private void publishRaw(String rawTopic, String json, long receivedAt) {
    if (json == null || json.isBlank()) {
      failures.computeIfAbsent(rawTopic, topic -> meters.counter(
          "ode.ffmlib.raw.publication.failures", "topic", topic)).increment();
      log.error("Unable to serialize UDP raw record for {}", rawTopic);
      return;
    }
    long start = System.nanoTime();
    inFlight.incrementAndGet();
    try {
      rawKafka.send(rawTopic, json).whenComplete((result, error) -> {
        inFlight.decrementAndGet();
        if (error != null) {
          failures.computeIfAbsent(rawTopic, topic -> meters.counter(
              "ode.ffmlib.raw.publication.failures", "topic", topic)).increment();
          log.error("Unable to publish UDP raw record to {}", rawTopic, error);
        } else if (result == null) {
          log.debug("UDP raw record publication to {} was skipped by the producer", rawTopic);
        } else {
          confirmations.computeIfAbsent(rawTopic, topic -> Timer.builder(
              "ode.ffmlib.raw.publication.confirmation").tag("topic", topic)
              .minimumExpectedValue(Duration.ofNanos(1_000))
              .publishPercentileHistogram().register(meters))
              .record(System.nanoTime() - start, TimeUnit.NANOSECONDS);
          receiptToConfirmations.computeIfAbsent(rawTopic, topic -> Timer.builder(
              "ode.ffmlib.udp.receipt.to.raw.confirmation").tag("topic", topic)
              .minimumExpectedValue(Duration.ofNanos(1_000))
              .publishPercentileHistogram().register(meters))
              .record(System.nanoTime() - receivedAt, TimeUnit.NANOSECONDS);
        }
      });
    } catch (RuntimeException error) {
      inFlight.decrementAndGet();
      failures.computeIfAbsent(rawTopic, topic -> meters.counter(
          "ode.ffmlib.raw.publication.failures", "topic", topic)).increment();
      log.error("Unable to submit UDP raw record to {}", rawTopic, error);
    }
  }

  private static Map<SupportedMessageType, Profile> profiles() {
    Map<SupportedMessageType, Profile> profiles = new EnumMap<>(SupportedMessageType.class);
    profiles.put(SupportedMessageType.BSM,
        new Profile(RecordType.bsmTx, Source.EV, GeneratedBy.OBU, true));
    profiles.put(SupportedMessageType.TIM,
        new Profile(RecordType.timMsg, Source.RSU, GeneratedBy.RSU, false));
    profiles.put(SupportedMessageType.MAP,
        new Profile(RecordType.mapTx, Source.RSU, GeneratedBy.RSU, false));
    profiles.put(SupportedMessageType.SPAT,
        new Profile(RecordType.spatTx, Source.RSU, GeneratedBy.RSU, false));
    profiles.put(SupportedMessageType.PSM,
        new Profile(RecordType.psmTx, Source.RSU, GeneratedBy.UNKNOWN, false));
    profiles.put(SupportedMessageType.SRM,
        new Profile(RecordType.srmTx, Source.RSU, GeneratedBy.OBU, false));
    profiles.put(SupportedMessageType.SSM,
        new Profile(RecordType.ssmTx, Source.RSU, GeneratedBy.RSU, false));
    profiles.put(SupportedMessageType.SDSM,
        new Profile(RecordType.sdsmTx, Source.RSU, GeneratedBy.RSU, false));
    profiles.put(SupportedMessageType.RTCM,
        new Profile(RecordType.rtcmTx, Source.RSU, GeneratedBy.RSU, false));
    profiles.put(SupportedMessageType.RSM,
        new Profile(RecordType.rsmTx, Source.RSU, GeneratedBy.RSU, false));
    return profiles;
  }

  private record Profile(RecordType recordType, Source source, GeneratedBy generatedBy,
      boolean includeDetails) {
  }
}
