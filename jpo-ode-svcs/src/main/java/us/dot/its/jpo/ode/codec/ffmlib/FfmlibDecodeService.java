package us.dot.its.jpo.ode.codec.ffmlib;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import us.dot.its.jpo.asn.j2735.r2024.MessageFrame.DSRCmsgID;
import us.dot.its.jpo.asn.j2735.r2024.MessageFrame.MessageFrame;
import us.dot.its.jpo.ode.codec.ffmlib.FfmlibMessageFrameCodec.IntermediateDecodeResult;
import us.dot.its.jpo.ode.codec.ffmlib.FfmlibMessageFrameCodec.IntermediateEncoding;
import us.dot.its.jpo.ode.kafka.topics.JsonTopics;
import us.dot.its.jpo.ode.model.OdeAsn1Data;
import us.dot.its.jpo.ode.model.OdeHexByteArray;
import us.dot.its.jpo.ode.model.OdeMessageFrameData;
import us.dot.its.jpo.ode.model.OdeMessageFrameMetadata;
import us.dot.its.jpo.ode.model.OdeMessageFramePayload;
import us.dot.its.jpo.ode.model.RxSource;
import us.dot.its.jpo.ode.uper.StartFlagNotFoundException;
import us.dot.its.jpo.ode.uper.SupportedMessageType;
import us.dot.its.jpo.ode.uper.UperUtil;
import us.dot.its.jpo.ode.util.CodecUtils;
import us.dot.its.jpo.ode.util.DateTimeUtils;
import us.dot.its.jpo.ode.util.JsonUtils;

/**
 * Primary in-process J2735 UPER decode service backed by the FFMLib native codec.
 *
 * <p>UDP receivers enqueue stripped UPER bytes. Decode workers call {@link #prepareRaw} and publish
 * Ode JSON without waiting for the producer acknowledgement.
 */
@Slf4j
@Service
public class FfmlibDecodeService {

  private static final byte[] SIGNED_DOT2_PREFIX = {0x03, (byte) 0x81, 0x00};
  private static final String IEEE_PDU = "Ieee1609Dot2Data";
  private static final String SOURCE_UDP = "udp";
  private static final String SOURCE_IMPORT = "import";
  private static final String TYPE_UNKNOWN = "unknown";

  private final ObjectProvider<FfmlibMessageFrameCodec> ffmlibCodec;
  private final Asn1CodecModeProperties modeProperties;
  private final String externalDecoderInputTopic;
  private final JsonTopics jsonTopics;
  private final KafkaTemplate<String, String> kafkaTemplate;
  private final XmlMapper simpleXmlMapper;
  private final MeterRegistry meterRegistry;
  private final Timer nativeTimer;
  private final Timer pojoTimer;
  private final Timer asnDecodeTimer;
  private final Timer jsonTimer;
  private final Timer sendTimer;
  private final Timer totalTimer;
  private final Map<String, Timer> endToEndTimers = new ConcurrentHashMap<>();

  /**
   * Constructs the FFMLib decode service with the native codec, topic, and metric dependencies.
   *
   * @param ffmlibCodec the optional native codec for FFM mode
   * @param properties FFMLib runtime properties
   * @param modeProperties codec-mode selection
   * @param jsonTopics decoded JSON topic names
   * @param kafkaTemplate Kafka producer
   * @param simpleXmlMapper XML mapper for J2735 XML processing
   * @param meterRegistry Micrometer registry for decode timers
   * @param externalDecoderInputTopic legacy external decoder input topic name
   */
  public FfmlibDecodeService(
      ObjectProvider<FfmlibMessageFrameCodec> ffmlibCodec,
      FfmlibProperties properties,
      Asn1CodecModeProperties modeProperties,
      JsonTopics jsonTopics,
      @Qualifier("kafkaTemplate") KafkaTemplate<String, String> kafkaTemplate,
      @Qualifier("simpleXmlMapper") XmlMapper simpleXmlMapper,
      MeterRegistry meterRegistry,
      @Value("${ode.kafka.topics.asn1.decoder-input}") String externalDecoderInputTopic) {
    this.ffmlibCodec = ffmlibCodec;
    this.modeProperties = modeProperties;
    this.externalDecoderInputTopic = externalDecoderInputTopic;
    this.jsonTopics = jsonTopics;
    this.kafkaTemplate = kafkaTemplate;
    this.simpleXmlMapper = simpleXmlMapper;
    this.meterRegistry = meterRegistry;
    for (SupportedMessageType type : SupportedMessageType.values()) {
      decodedCounter(type.name(), SOURCE_UDP);
      decodedCounter(type.name(), SOURCE_IMPORT);
    }
    this.nativeTimer = Timer.builder("ode.ffmlib.decode.stage")
        .tag("stage", "native").register(meterRegistry);
    this.pojoTimer = Timer.builder("ode.ffmlib.decode.stage")
        .tag("stage", "pojo").register(meterRegistry);
    this.asnDecodeTimer = Timer.builder("ode.ffmlib.decode.asn")
        .description("ASN.1 decode latency covering native conversion and POJO mapping")
        .register(meterRegistry);
    this.jsonTimer = Timer.builder("ode.ffmlib.decode.stage")
        .tag("stage", "json").register(meterRegistry);
    this.sendTimer = Timer.builder("ode.ffmlib.decode.stage")
        .tag("stage", "send").register(meterRegistry);
    this.totalTimer = Timer.builder("ode.ffmlib.decode.total").register(meterRegistry);
  }

  /** Decodes an imported log record directly to its Ode JSON topic. */
  public void decode(OdeAsn1Data asn1Data, String key) {
    if (!modeProperties.isFfm()) {
      try {
        kafkaTemplate.send(externalDecoderInputTopic, key, JsonUtils.toJson(asn1Data, false));
      } catch (Exception error) {
        throw new IllegalArgumentException("Unable to publish external ASN.1 decoder input", error);
      }
      return;
    }
    try {
      long prepStart = System.nanoTime();
      OdeHexByteArray hexBytes = (OdeHexByteArray) asn1Data.getPayload().getData();
      byte[] encoded = CodecUtils.fromHex(hexBytes.getBytes());
      if (isIeee1609(encoded, (OdeMessageFrameMetadata) asn1Data.getMetadata())) {
        recordFailure(TYPE_UNKNOWN, SOURCE_IMPORT, "signed_payload");
        throw new UnsupportedOperationException(
            "Signed IEEE 1609.2 payloads are not supported by j2735-2024-ffm-lib "
                + "3.0.0-beta1; use external codec mode for signed messages");
      }
      String messageType = UperUtil.determineMessageType(asn1Data.getPayload());
      SupportedMessageType type = SupportedMessageType.valueOf(messageType);
      byte[] uperBytes = UperUtil.stripDot3Header(encoded, type.getStartFlagBytes());
      if (!isIeee1609(uperBytes, (OdeMessageFrameMetadata) asn1Data.getMetadata())) {
        try {
          uperBytes = UperUtil.stripDot2Header(uperBytes, type.getStartFlagBytes());
        } catch (StartFlagNotFoundException error) {
          throw new IllegalArgumentException("Imported ASN.1 start flag was not found", error);
        }
      }
      log.debug("Prepared raw {} ASN.1 payload in {}us", key,
          (System.nanoTime() - prepStart) / 1000);

      // totalTimer is recorded inside runPublishDecoded (same as the UDP worker path).
      runPublishDecoded(
          (OdeMessageFrameMetadata) asn1Data.getMetadata(), uperBytes, key, null, SOURCE_IMPORT);
    } catch (UnsupportedOperationException failure) {
      throw failure;
    } catch (DecodeFailure failure) {
      throw failure;
    } catch (ClassCastException e) {
      recordFailure(TYPE_UNKNOWN, SOURCE_IMPORT, "invalid_payload");
      log.error("FFMLib decode failed (unexpected payload type) for key {}: {}", key, e.getMessage(),
          e);
      throw new IllegalArgumentException("Unexpected ASN.1 payload type", e);
    } catch (Exception e) {
      String reason = e instanceof IllegalArgumentException ? "invalid_payload" : "unexpected";
      recordFailure(TYPE_UNKNOWN, SOURCE_IMPORT, reason);
      log.error("FFMLib decode unexpected error for key {}: {}", key, e.getMessage(), e);
      throw new IllegalArgumentException("Unable to decode ASN.1 payload", e);
    }
  }

  /** Decodes bytes extracted from a raw Kafka record without another hex conversion. */
  public void decode(OdeMessageFrameMetadata metadata, byte[] uperBytes, String key) {
    if (!modeProperties.isFfm()) {
      throw new IllegalStateException("Raw-topic decode requires FFM mode");
    }
    runPublishDecoded(metadata, uperBytes, key, null, SOURCE_UDP);
  }

  /** Prepares one raw-topic output without waiting for a Kafka producer acknowledgement. */
  public PreparedDecodedMessage prepareRaw(OdeMessageFrameMetadata metadata, byte[] uperBytes,
      String key) {
    if (!modeProperties.isFfm()) {
      throw new IllegalStateException("Raw-topic decode requires FFM mode");
    }
    long start = System.nanoTime();
    try {
      return prepareDecoded(metadata, uperBytes, key, null, SOURCE_UDP, start);
    } catch (RuntimeException error) {
      totalTimer.record(System.nanoTime() - start, TimeUnit.NANOSECONDS);
      throw error;
    }
  }

  /** Records a confirmed raw-topic output; called only after Kafka confirms the send. */
  public void recordRawConfirmed(PreparedDecodedMessage prepared) {
    recordConfirmed(prepared);
    totalTimer.record(System.nanoTime() - prepared.startNanos(), TimeUnit.NANOSECONDS);
  }

  /** Records a raw-topic output that could not be confirmed. */
  public void recordRawPublishFailure(PreparedDecodedMessage prepared) {
    recordFailure(prepared.type(), prepared.source(), "publish");
    totalTimer.record(System.nanoTime() - prepared.startNanos(), TimeUnit.NANOSECONDS);
  }

  /** Immutable output of native decode and JSON mapping, ready for asynchronous publication. */
  public record PreparedDecodedMessage(String topic, String key, String json,
      OdeMessageFrameMetadata metadata, String type, String source, long startNanos,
      long sendStartedNanos) {
  }

  private void runPublishDecoded(
      OdeMessageFrameMetadata metadata,
      byte[] uperBytes,
      String key,
      SupportedMessageType knownType,
      String source) {
    long totalStart = System.nanoTime();
    try {
      PreparedDecodedMessage prepared =
          prepareDecoded(metadata, uperBytes, key, knownType, source, totalStart);
      try {
        kafkaTemplate.send(prepared.topic(), key, prepared.json()).get(10, TimeUnit.SECONDS);
      } catch (InterruptedException error) {
        Thread.currentThread().interrupt();
        recordFailure(prepared.type(), source, "publish");
        throw new PublishFailure("Interrupted while publishing decoded ASN.1 payload", error);
      } catch (Exception error) {
        recordFailure(prepared.type(), source, "publish");
        throw new PublishFailure("Unable to publish decoded ASN.1 payload", error);
      }
      recordConfirmed(prepared);
    } finally {
      totalTimer.record(System.nanoTime() - totalStart, TimeUnit.NANOSECONDS);
    }
  }

  private PreparedDecodedMessage prepareDecoded(
      OdeMessageFrameMetadata metadata,
      byte[] uperBytes,
      String key,
      SupportedMessageType knownType,
      String source,
      long startNanos) {
    String type = knownType == null ? TYPE_UNKNOWN : knownType.name();
    try {
      long nativeStart = System.nanoTime();
      byte[] messageFrameBytes = unwrapIeee1609IfPresent(metadata, uperBytes);
      IntermediateDecodeResult intermediate = codec().uperToIntermediate(messageFrameBytes);
      long nativeNanos = System.nanoTime() - nativeStart;
      nativeTimer.record(nativeNanos, TimeUnit.NANOSECONDS);

      long pojoStart = System.nanoTime();
      final MessageFrame<?> messageFrame = parseMessageFrame(intermediate);
      long pojoNanos = System.nanoTime() - pojoStart;
      pojoTimer.record(pojoNanos, TimeUnit.NANOSECONDS);
      type = messageType(knownType, messageFrame);

      long asnDecodeNanos = nativeNanos + pojoNanos;
      asnDecodeTimer.record(asnDecodeNanos, TimeUnit.NANOSECONDS);
      if (metadata.getOdeReceivedAt() == null || metadata.getOdeReceivedAt().isBlank()) {
        metadata.setOdeReceivedAt(DateTimeUtils.now());
      }
      metadata.setEncodings(null);
      if (metadata.getReceivedMessageDetails() != null
          && metadata.getReceivedMessageDetails().getRxSource() == null) {
        metadata.getReceivedMessageDetails().setRxSource(RxSource.NA);
      }
      if (metadata.getSchemaVersion() <= 4) {
        metadata.setReceivedMessageDetails(null);
      }

      OdeMessageFramePayload payload = new OdeMessageFramePayload(messageFrame);
      OdeMessageFrameData frameData = new OdeMessageFrameData(metadata, payload);

      long jsonStart = System.nanoTime();
      final String json = JsonUtils.toJson(frameData, false);
      jsonTimer.record(System.nanoTime() - jsonStart, TimeUnit.NANOSECONDS);

      String topic = resolveJsonTopic(knownType, messageFrame);
      if (topic == null) {
        recordDropped(type, source);
        throw new DecodeFailure("No JSON topic mapped for decoded message", null);
      }

      if (log.isDebugEnabled()) {
        log.debug(
            "FFMLib decode key={} encoding={} native={}us pojo={}us totalAsn={}us topic={}",
            key,
            intermediate.encoding(),
            nativeNanos / 1000,
            pojoNanos / 1000,
            asnDecodeNanos / 1000,
            topic);
      }
      return new PreparedDecodedMessage(topic, key, json, metadata, type, source, startNanos,
          System.nanoTime());
    } catch (UnsupportedOperationException failure) {
      recordFailure(type, source, "signed_payload");
      throw failure;
    } catch (DecodeFailure failure) {
      throw failure;
    } catch (PublishFailure failure) {
      throw failure;
    } catch (JsonProcessingException e) {
      log.error("FFMLib decode failed (JSON/XML processing) for key {}: {}", key, e.getMessage(), e);
      recordFailure(type, source, "mapping");
      throw new DecodeFailure("Unable to map decoded ASN.1 XER", e);
    } catch (Exception e) {
      String reason = e instanceof UnsupportedOperationException ? "signed_payload" : "unexpected";
      log.error("FFMLib decode unexpected error for key {}: {}", key, e.getMessage(), e);
      recordFailure(type, source, reason);
      throw new DecodeFailure("Unable to decode ASN.1 payload", e);
    }
  }

  private void recordConfirmed(PreparedDecodedMessage prepared) {
    sendTimer.record(System.nanoTime() - prepared.sendStartedNanos(), TimeUnit.NANOSECONDS);
    recordDecoded(prepared.type(), prepared.source());
    recordEndToEndLatency(prepared.metadata(), prepared.type());
  }

  private void recordDecoded(String type, String source) {
    decodedCounter(type, source).increment();
  }

  private void recordEndToEndLatency(OdeMessageFrameMetadata metadata, String type) {
    if (metadata.getOdeReceivedAt() == null || metadata.getLogFileName() != null) {
      return;
    }
    try {
      Duration elapsed = Duration.between(Instant.parse(metadata.getOdeReceivedAt()),
          Instant.now());
      // Imported historical timestamps are not live UDP latency samples.
      if (!elapsed.isNegative() && elapsed.compareTo(Duration.ofMinutes(5)) < 0) {
        endToEndTimers.computeIfAbsent(type, messageType ->
            Timer.builder("ode.ffmlib.decode.end.to.end")
                .tag("type", messageType)
                .publishPercentileHistogram()
                .register(meterRegistry)).record(elapsed);
      }
    } catch (RuntimeException error) {
      log.debug("Cannot measure FFM end-to-end latency for key timestamp {}",
          metadata.getOdeReceivedAt());
    }
  }

  private Counter decodedCounter(String type, String source) {
    return meterRegistry.counter(
        "ode.ffmlib.decode.messages",
        "type", type,
        "source", source);
  }

  private void recordFailure(String type, String source, String reason) {
    meterRegistry.counter(
        "ode.ffmlib.decode.failures",
        "type", type,
        "source", source,
        "reason", reason)
        .increment();
  }

  private void recordDropped(String type, String source) {
    meterRegistry.counter(
        "ode.ffmlib.decode.dropped",
        "type", type,
        "source", source,
        "reason", "unmapped_topic")
        .increment();
  }

  private static String messageType(SupportedMessageType knownType, MessageFrame<?> messageFrame) {
    if (knownType != null) {
      return knownType.name();
    }
    if (messageFrame == null || messageFrame.getMessageId() == null) {
      return TYPE_UNKNOWN;
    }
    String messageName = messageFrame.getMessageId().name().orElse(TYPE_UNKNOWN);
    return switch (messageName) {
      case "basicSafetyMessage" -> SupportedMessageType.BSM.name();
      case "travelerInformation" -> SupportedMessageType.TIM.name();
      case "mapData" -> SupportedMessageType.MAP.name();
      case "signalPhaseAndTimingMessage" -> SupportedMessageType.SPAT.name();
      case "personalSafetyMessage" -> SupportedMessageType.PSM.name();
      case "signalStatusMessage" -> SupportedMessageType.SSM.name();
      case "signalRequestMessage" -> SupportedMessageType.SRM.name();
      case "sensorDataSharingMessage" -> SupportedMessageType.SDSM.name();
      case "rtcmCorrections" -> SupportedMessageType.RTCM.name();
      case "roadSafetyMessage" -> SupportedMessageType.RSM.name();
      default -> TYPE_UNKNOWN;
    };
  }

  /** Decode failure already recorded in Micrometer. */
  private static final class DecodeFailure extends IllegalArgumentException {
    private DecodeFailure(String message, Throwable cause) {
      super(message, cause);
    }
  }

  /** Retriable failure while confirming an output Kafka record. */
  public static final class PublishFailure extends RuntimeException {
    public PublishFailure(String message, Throwable cause) {
      super(message, cause);
    }
  }

  private MessageFrame<?> parseMessageFrame(IntermediateDecodeResult intermediate)
      throws JsonProcessingException {
    return simpleXmlMapper.readValue(intermediate.text(), MessageFrame.class);
  }

  private FfmlibMessageFrameCodec codec() {
    FfmlibMessageFrameCodec codec = ffmlibCodec.getIfAvailable();
    if (codec == null) {
      throw new IllegalStateException("FFM codec is unavailable while ode.asn1.codec-mode=ffm");
    }
    return codec;
  }

  private byte[] unwrapIeee1609IfPresent(OdeMessageFrameMetadata metadata, byte[] encoded) {
    if (!isIeee1609(encoded, metadata)) {
      metadata.setCertPresent(false);
      return encoded;
    }

    throw new UnsupportedOperationException(
        "Signed IEEE 1609.2 payloads are not supported by j2735-2024-ffm-lib 3.0.0-beta1; "
            + "use external codec mode for signed messages");
  }

  private static boolean isIeee1609(byte[] encoded, OdeMessageFrameMetadata metadata) {
    if (encoded.length >= SIGNED_DOT2_PREFIX.length) {
      boolean prefix = true;
      for (int i = 0; i < SIGNED_DOT2_PREFIX.length; i++) {
        prefix &= encoded[i] == SIGNED_DOT2_PREFIX[i];
      }
      if (prefix) {
        return true;
      }
    }
    return metadata.getEncodings() != null && metadata.getEncodings().stream()
        .anyMatch(encoding -> IEEE_PDU.equals(encoding.getElementType()));
  }

  private String resolveJsonTopic(SupportedMessageType knownType, MessageFrame<?> messageFrame) {
    if (knownType != null) {
      String topic = resolveJsonTopic(knownType);
      if (topic != null) {
        return topic;
      }
    }
    DSRCmsgID msgId = messageFrame.getMessageId();
    String messageName = msgId.name().orElse("Unknown");
    return resolveJsonTopicByMessageName(messageName);
  }

  private String resolveJsonTopic(SupportedMessageType msgType) {
    return switch (msgType) {
      case BSM -> jsonTopics.getBsm();
      case TIM -> jsonTopics.getTim();
      case MAP -> jsonTopics.getMap();
      case SPAT -> jsonTopics.getSpat();
      case PSM -> jsonTopics.getPsm();
      case SSM -> jsonTopics.getSsm();
      case SRM -> jsonTopics.getSrm();
      case SDSM -> jsonTopics.getSdsm();
      case RTCM -> jsonTopics.getRtcm();
      case RSM -> jsonTopics.getRsm();
    };
  }

  private String resolveJsonTopicByMessageName(String messageName) {
    return switch (messageName) {
      case "basicSafetyMessage" -> jsonTopics.getBsm();
      case "travelerInformation" -> jsonTopics.getTim();
      case "mapData" -> jsonTopics.getMap();
      case "signalPhaseAndTimingMessage" -> jsonTopics.getSpat();
      case "personalSafetyMessage" -> jsonTopics.getPsm();
      case "signalStatusMessage" -> jsonTopics.getSsm();
      case "signalRequestMessage" -> jsonTopics.getSrm();
      case "sensorDataSharingMessage" -> jsonTopics.getSdsm();
      case "rtcmCorrections" -> jsonTopics.getRtcm();
      case "roadSafetyMessage" -> jsonTopics.getRsm();
      default -> null;
    };
  }

  public void shutdown() {
    // Retained as a source-compatible lifecycle hook after removing the excluded worker pool.
  }
}
