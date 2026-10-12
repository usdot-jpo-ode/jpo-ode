package us.dot.its.jpo.ode.codec.ffmlib;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import java.io.IOException;
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
import us.dot.its.jpo.asn.j2735.r2024.BasicSafetyMessage.BasicSafetyMessage;
import us.dot.its.jpo.asn.j2735.r2024.BasicSafetyMessage.BasicSafetyMessageMessageFrame;
import us.dot.its.jpo.asn.j2735.r2024.MapData.MapData;
import us.dot.its.jpo.asn.j2735.r2024.MapData.MapDataMessageFrame;
import us.dot.its.jpo.asn.j2735.r2024.MessageFrame.DSRCmsgID;
import us.dot.its.jpo.asn.j2735.r2024.MessageFrame.MessageFrame;
import us.dot.its.jpo.asn.j2735.r2024.PersonalSafetyMessage.PersonalSafetyMessage;
import us.dot.its.jpo.asn.j2735.r2024.PersonalSafetyMessage.PersonalSafetyMessageMessageFrame;
import us.dot.its.jpo.asn.j2735.r2024.RTCMcorrections.RTCMcorrections;
import us.dot.its.jpo.asn.j2735.r2024.RTCMcorrections.RTCMcorrectionsMessageFrame;
import us.dot.its.jpo.asn.j2735.r2024.RoadSafetyMessage.RoadSafetyMessage;
import us.dot.its.jpo.asn.j2735.r2024.RoadSafetyMessage.RoadSafetyMessageMessageFrame;
import us.dot.its.jpo.asn.j2735.r2024.SPAT.SPAT;
import us.dot.its.jpo.asn.j2735.r2024.SPAT.SPATMessageFrame;
import us.dot.its.jpo.asn.j2735.r2024.SensorDataSharingMessage.SensorDataSharingMessage;
import us.dot.its.jpo.asn.j2735.r2024.SensorDataSharingMessage.SensorDataSharingMessageMessageFrame;
import us.dot.its.jpo.asn.j2735.r2024.SignalRequestMessage.SignalRequestMessage;
import us.dot.its.jpo.asn.j2735.r2024.SignalRequestMessage.SignalRequestMessageMessageFrame;
import us.dot.its.jpo.asn.j2735.r2024.SignalStatusMessage.SignalStatusMessage;
import us.dot.its.jpo.asn.j2735.r2024.SignalStatusMessage.SignalStatusMessageMessageFrame;
import us.dot.its.jpo.asn.j2735.r2024.TravelerInformation.TravelerInformation;
import us.dot.its.jpo.asn.j2735.r2024.TravelerInformation.TravelerInformationMessageFrame;
import us.dot.its.jpo.ode.codec.ffmlib.FfmlibMessageFrameCodec.IntermediateDecodeResult;
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
 * <p>FFM listeners consume durable raw-topic records and confirm JSON publication before they
 * acknowledge their input offsets.
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
  private final ObjectProvider<FfmlibOutputPublisher> outputPublisher;
  private final ObjectReader messageFrameReader;
  private final ObjectReader bsmValueReader;
  private final JsonFactory jerFactory;
  private final MeterRegistry meterRegistry;
  private final Timer nativeTimer;
  private final Timer pojoTimer;
  private final Timer asnDecodeTimer;
  private final Timer jsonTimer;
  private final Timer sendTimer;
  private final Timer totalTimer;
  private final Map<String, Timer> endToEndTimers = new ConcurrentHashMap<>();
  private final Map<SupportedMessageType, JerBinding> jerBindings;

  /**
   * Constructs the FFMLib decode service with the native codec, topic, and metric dependencies.
   *
   * @param ffmlibCodec the optional native codec for FFM mode
   * @param properties FFMLib runtime properties
   * @param modeProperties codec-mode selection
   * @param jsonTopics decoded JSON topic names
   * @param kafkaTemplate Kafka producer used for the external decoder path
   * @param outputPublisher FFM JSON publisher used by the optional in-process route
   * @param simpleObjectMapper JSON mapper for J2735 JER processing
   * @param meterRegistry Micrometer registry for decode timers
   * @param externalDecoderInputTopic legacy external decoder input topic name
   */
  public FfmlibDecodeService(
      ObjectProvider<FfmlibMessageFrameCodec> ffmlibCodec,
      FfmlibProperties properties,
      Asn1CodecModeProperties modeProperties,
      JsonTopics jsonTopics,
      @Qualifier("kafkaTemplate") KafkaTemplate<String, String> kafkaTemplate,
      ObjectProvider<FfmlibOutputPublisher> outputPublisher,
      @Qualifier("simpleObjectMapper") ObjectMapper simpleObjectMapper,
      MeterRegistry meterRegistry,
      @Value("${ode.kafka.topics.asn1.decoder-input}") String externalDecoderInputTopic) {
    this.ffmlibCodec = ffmlibCodec;
    this.modeProperties = modeProperties;
    this.externalDecoderInputTopic = externalDecoderInputTopic;
    this.jsonTopics = jsonTopics;
    this.kafkaTemplate = kafkaTemplate;
    this.outputPublisher = outputPublisher;
    this.messageFrameReader = simpleObjectMapper.readerFor(MessageFrame.class);
    this.bsmValueReader = simpleObjectMapper.readerFor(BasicSafetyMessage.class);
    this.jerBindings = Map.of(
        SupportedMessageType.BSM,
        new JerBinding(20, "BasicSafetyMessage", bsmValueReader),
        SupportedMessageType.MAP,
        new JerBinding(18, "MapData", simpleObjectMapper.readerFor(MapData.class)),
        SupportedMessageType.SPAT,
        new JerBinding(19, "SPAT", simpleObjectMapper.readerFor(SPAT.class)),
        SupportedMessageType.TIM,
        new JerBinding(31, "TravelerInformation",
            simpleObjectMapper.readerFor(TravelerInformation.class)),
        SupportedMessageType.PSM,
        new JerBinding(32, "PersonalSafetyMessage",
            simpleObjectMapper.readerFor(PersonalSafetyMessage.class)),
        SupportedMessageType.SRM,
        new JerBinding(29, "SignalRequestMessage",
            simpleObjectMapper.readerFor(SignalRequestMessage.class)),
        SupportedMessageType.SSM,
        new JerBinding(30, "SignalStatusMessage",
            simpleObjectMapper.readerFor(SignalStatusMessage.class)),
        SupportedMessageType.SDSM,
        new JerBinding(41, "SensorDataSharingMessage",
            simpleObjectMapper.readerFor(SensorDataSharingMessage.class)),
        SupportedMessageType.RTCM,
        new JerBinding(28, "RTCMcorrections", simpleObjectMapper.readerFor(RTCMcorrections.class)),
        SupportedMessageType.RSM,
        new JerBinding(33, "RoadSafetyMessage",
            simpleObjectMapper.readerFor(RoadSafetyMessage.class)));
    this.jerFactory = simpleObjectMapper.getFactory();
    this.meterRegistry = meterRegistry;
    for (SupportedMessageType type : SupportedMessageType.values()) {
      decodedCounter(type.name(), SOURCE_UDP);
      decodedCounter(type.name(), SOURCE_IMPORT);
    }
    this.nativeTimer = Timer.builder("ode.ffmlib.decode.stage")
        .tag("stage", "native").minimumExpectedValue(Duration.ofNanos(1_000))
        .publishPercentileHistogram().register(meterRegistry);
    this.pojoTimer = Timer.builder("ode.ffmlib.decode.stage")
        .tag("stage", "jer_mapping").minimumExpectedValue(Duration.ofNanos(1_000))
        .publishPercentileHistogram().register(meterRegistry);
    this.asnDecodeTimer = Timer.builder("ode.ffmlib.decode.asn")
        .description("ASN.1 decode latency covering native conversion and POJO mapping")
        .minimumExpectedValue(Duration.ofNanos(1_000)).publishPercentileHistogram()
        .register(meterRegistry);
    this.jsonTimer = Timer.builder("ode.ffmlib.decode.stage")
        .tag("stage", "json").minimumExpectedValue(Duration.ofNanos(1_000))
        .publishPercentileHistogram().register(meterRegistry);
    this.sendTimer = Timer.builder("ode.ffmlib.decode.stage")
        .tag("stage", "send").minimumExpectedValue(Duration.ofNanos(1_000))
        .publishPercentileHistogram().register(meterRegistry);
    this.totalTimer = Timer.builder("ode.ffmlib.decode.total")
        .minimumExpectedValue(Duration.ofNanos(1_000)).publishPercentileHistogram()
        .register(meterRegistry);
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
      String messageType = UperUtil.determineMessageType(asn1Data.getPayload());
      SupportedMessageType type = SupportedMessageType.valueOf(messageType);
      byte[] uperBytes = UperUtil.stripDot3Header(encoded, type.getStartFlagBytes());
      try {
        uperBytes = UperUtil.stripDot2Header(uperBytes, type.getStartFlagBytes());
      } catch (StartFlagNotFoundException error) {
        throw new IllegalArgumentException("Imported ASN.1 start flag was not found", error);
      }
      log.debug("Prepared raw {} ASN.1 payload in {}us", key,
          (System.nanoTime() - prepStart) / 1000);

      OdeMessageFrameMetadata metadata = (OdeMessageFrameMetadata) asn1Data.getMetadata();
      // The external route's XML round trip renders absent optional strings as empty strings.
      metadata.setOriginIp(metadata.getOriginIp() == null ? "" : metadata.getOriginIp());
      metadata.setOdePacketID(metadata.getOdePacketID() == null ? "" : metadata.getOdePacketID());
      metadata.setOdeTimStartDateTime(
          metadata.getOdeTimStartDateTime() == null ? "" : metadata.getOdeTimStartDateTime());
      runPublishDecoded(metadata, uperBytes, key, null, SOURCE_IMPORT);
    } catch (UnsupportedOperationException | InfrastructureFailure | DecodeFailure failure) {
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

  /** Prepares one raw-topic output; the listener confirms it before acknowledging its input. */
  public PreparedDecodedMessage prepareRaw(OdeMessageFrameMetadata metadata, byte[] uperBytes,
      String key) {
    return prepareRaw(metadata, uperBytes, key, null);
  }

  /**
   * Prepares a raw-topic output from UPER bytes whose transport and security envelopes have
   * already been stripped by the raw-record parser. The FFM path decodes the extracted
   * MessageFrame bytes and does not inspect or verify the original security envelope.
   *
   * @param metadata raw-record metadata, updated in place for the decoded output
   * @param uperBytes extracted MessageFrame UPER bytes
   * @param key Kafka record key
   * @param knownType message type of the raw topic, or {@code null} when unknown
   * @return the decoded output, ready for confirmed publication
   */
  public PreparedDecodedMessage prepareRaw(OdeMessageFrameMetadata metadata, byte[] uperBytes,
      String key, SupportedMessageType knownType) {
    if (!modeProperties.isFfm()) {
      throw new IllegalStateException("Raw-topic decode requires FFM mode");
    }
    long start = System.nanoTime();
    try {
      return prepareDecoded(metadata, uperBytes, key, knownType, SOURCE_UDP, start, true);
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

  /** Immutable output of native decode and JSON mapping, ready for confirmed publication. */
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
        if (modeProperties.isFfm()) {
          FfmlibOutputPublisher.PublicationOutcome outcome =
              outputPublisher.getObject().publish(prepared).get(10, TimeUnit.SECONDS);
          if (outcome == FfmlibOutputPublisher.PublicationOutcome.SKIPPED_DISABLED) {
            return;
          }
        } else {
          kafkaTemplate.send(prepared.topic(), key, prepared.json()).get(10, TimeUnit.SECONDS);
        }
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
    return prepareDecoded(metadata, uperBytes, key, knownType, source, startNanos,
        SOURCE_IMPORT.equals(source));
  }

  private PreparedDecodedMessage prepareDecoded(
      OdeMessageFrameMetadata metadata,
      byte[] uperBytes,
      String key,
      SupportedMessageType knownType,
      String source,
      long startNanos,
      boolean messageFrameAlreadyExtracted) {
    String type = knownType == null ? TYPE_UNKNOWN : knownType.name();
    try {
      long nativeStart = System.nanoTime();
      // Imports and raw-topic records already stripped the envelope; retain security metadata.
      byte[] messageFrameBytes = messageFrameAlreadyExtracted
          ? uperBytes : unwrapIeee1609IfPresent(metadata, uperBytes);
      IntermediateDecodeResult intermediate;
      try {
        intermediate = codec().uperToIntermediate(messageFrameBytes);
      } catch (RuntimeException nativeError) {
        if (FfmlibNativeErrors.isMalformedInput(nativeError)) {
          throw new NativeInputFailure(nativeError);
        }
        throw new InfrastructureFailure("FFMLib native UPER conversion failed", nativeError);
      }
      long nativeNanos = System.nanoTime() - nativeStart;
      nativeTimer.record(nativeNanos, TimeUnit.NANOSECONDS);

      long pojoStart = System.nanoTime();
      final MessageFrame<?> messageFrame;
      try {
        messageFrame = parseMessageFrame(intermediate, knownType);
      } catch (IOException | RuntimeException mappingError) {
        recordFailure(type, source, "mapping");
        Throwable mappingCause = mappingError instanceof JsonProcessingException
            ? mappingError
            : JsonMappingException.from((JsonParser) null,
                "Unable to map decoded ASN.1 JER", mappingError);
        throw new DecodeFailure("Unable to map decoded ASN.1 JER", mappingCause);
      }
      long pojoNanos = System.nanoTime() - pojoStart;
      pojoTimer.record(pojoNanos, TimeUnit.NANOSECONDS);
      type = messageType(knownType, messageFrame);
      if (knownType != null && !matchesKnownMessageType(messageFrame, knownType)) {
        recordFailure(type, source, "mapping");
        throw new DecodeFailure("Decoded MessageFrame id does not match the raw topic type", null);
      }

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
      if (json == null || json.isBlank()) {
        recordFailure(type, source, "serialization");
        throw new DecodeFailure("Unable to serialize decoded ASN.1 JSON", null);
      }

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
    } catch (InfrastructureFailure failure) {
      recordFailure(type, source, "infrastructure");
      throw failure;
    } catch (NativeInputFailure failure) {
      recordFailure(type, source, "native_input");
      throw new DecodeFailure("Native codec rejected malformed ASN.1 input", failure.getCause());
    } catch (DecodeFailure | PublishFailure failure) {
      throw failure;
    } catch (Exception e) {
      log.error("FFMLib decode infrastructure failure for key {}: {}", key, e.getMessage(), e);
      recordFailure(type, source, "infrastructure");
      throw new InfrastructureFailure("Unable to decode ASN.1 payload", e);
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

  /** Native codec operational failure that must not be quarantined as bad input. */
  public static final class InfrastructureFailure extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public InfrastructureFailure(String message, Throwable cause) {
      super(message, cause);
    }
  }

  private static final class NativeInputFailure extends RuntimeException {
    private NativeInputFailure(Throwable cause) {
      super(cause);
    }
  }

  private boolean matchesKnownMessageType(MessageFrame<?> messageFrame,
      SupportedMessageType knownType) {
    if (messageFrame == null || messageFrame.getMessageId() == null) {
      return false;
    }
    JerBinding binding = jerBindings.get(knownType);
    return binding != null && messageFrame.getMessageId().getValue() == binding.expectedMessageId();
  }

  /** Retriable failure while confirming an output Kafka record. */
  public static final class PublishFailure extends RuntimeException {
    public PublishFailure(String message, Throwable cause) {
      super(message, cause);
    }
  }

  private MessageFrame<?> parseMessageFrame(IntermediateDecodeResult intermediate,
      SupportedMessageType knownType) throws IOException {
    if (knownType != null) {
      try {
        MessageFrame<?> directFrame = parseKnownJerFrame(intermediate.bytes(), knownType);
        if (directFrame != null) {
          return directFrame;
        }
      } catch (IOException error) {
        log.debug("Direct JER mapping failed; falling back to generic MessageFrame mapping", error);
      }
    }
    return messageFrameReader.readValue(intermediate.bytes());
  }

  private MessageFrame<?> parseKnownJerFrame(byte[] jer, SupportedMessageType knownType)
      throws IOException {
    JerBinding binding = jerBindings.get(knownType);
    if (binding == null) {
      return null;
    }

    try (JsonParser parser = jerFactory.createParser(jer)) {
      if (parser.nextToken() != JsonToken.START_OBJECT) {
        return null;
      }
      ParsedJerFrame parsed = parseKnownJerObject(parser, binding);
      if (parsed == null || parser.nextToken() != null
          || !parsed.messageIdSeen || !parsed.valueSeen
          || parsed.messageId != binding.expectedMessageId()) {
        return null;
      }
      return buildKnownFrame(knownType, parsed.value);
    }
  }

  private ParsedJerFrame parseKnownJerObject(JsonParser parser, JerBinding binding)
      throws IOException {
    ParsedJerFrame parsed = new ParsedJerFrame();
    JsonToken token;
    while ((token = parser.nextToken()) != JsonToken.END_OBJECT) {
      if (token != JsonToken.FIELD_NAME
          || !parseKnownJerField(parser, parser.currentName(), binding, parsed)) {
        return null;
      }
    }
    return parsed;
  }

  private boolean parseKnownJerField(JsonParser parser, String field, JerBinding binding,
      ParsedJerFrame parsed) throws IOException {
    JsonToken fieldValue = parser.nextToken();
    if (fieldValue == null) {
      return false;
    }
    if ("messageId".equals(field)) {
      if (parsed.messageIdSeen || fieldValue != JsonToken.VALUE_NUMBER_INT) {
        return false;
      }
      parsed.messageIdSeen = true;
      parsed.messageId = parser.getIntValue();
      return true;
    }
    if ("value".equals(field)) {
      if (parsed.valueSeen || fieldValue != JsonToken.START_OBJECT) {
        return false;
      }
      parsed.valueSeen = true;
      parsed.value = parseKnownJerValue(parser, binding);
      return parsed.value != null;
    }
    parser.skipChildren();
    return true;
  }

  private Object parseKnownJerValue(JsonParser parser, JerBinding binding) throws IOException {
    int choiceCount = 0;
    Object candidateValue = null;
    boolean expectedChoiceSeen = false;
    JsonToken choiceToken;
    while ((choiceToken = parser.nextToken()) != JsonToken.END_OBJECT) {
      if (choiceToken != JsonToken.FIELD_NAME) {
        return null;
      }
      String choice = parser.currentName();
      JsonToken choiceValue = parser.nextToken();
      if (choiceValue == null) {
        return null;
      }
      choiceCount++;
      if (binding.choiceName().equals(choice)) {
        if (choiceCount != 1 || choiceValue != JsonToken.START_OBJECT) {
          return null;
        }
        expectedChoiceSeen = true;
        candidateValue = binding.valueReader().readValue(parser);
      } else {
        parser.skipChildren();
      }
    }
    if (choiceCount != 1 || !expectedChoiceSeen || candidateValue == null) {
      return null;
    }
    return candidateValue;
  }

  private MessageFrame<?> buildKnownFrame(SupportedMessageType knownType, Object value) {
    return switch (knownType) {
      case BSM -> {
        BasicSafetyMessageMessageFrame frame = new BasicSafetyMessageMessageFrame();
        frame.setValue((BasicSafetyMessage) value);
        yield frame;
      }
      case MAP -> {
        MapDataMessageFrame frame = new MapDataMessageFrame();
        frame.setValue((MapData) value);
        yield frame;
      }
      case SPAT -> {
        SPATMessageFrame frame = new SPATMessageFrame();
        frame.setValue((SPAT) value);
        yield frame;
      }
      case TIM -> {
        TravelerInformationMessageFrame frame = new TravelerInformationMessageFrame();
        frame.setValue((TravelerInformation) value);
        yield frame;
      }
      case PSM -> {
        PersonalSafetyMessageMessageFrame frame = new PersonalSafetyMessageMessageFrame();
        frame.setValue((PersonalSafetyMessage) value);
        yield frame;
      }
      case SRM -> {
        SignalRequestMessageMessageFrame frame = new SignalRequestMessageMessageFrame();
        frame.setValue((SignalRequestMessage) value);
        yield frame;
      }
      case SSM -> {
        SignalStatusMessageMessageFrame frame = new SignalStatusMessageMessageFrame();
        frame.setValue((SignalStatusMessage) value);
        yield frame;
      }
      case SDSM -> {
        SensorDataSharingMessageMessageFrame frame = new SensorDataSharingMessageMessageFrame();
        frame.setValue((SensorDataSharingMessage) value);
        yield frame;
      }
      case RTCM -> {
        RTCMcorrectionsMessageFrame frame = new RTCMcorrectionsMessageFrame();
        frame.setValue((RTCMcorrections) value);
        yield frame;
      }
      case RSM -> {
        RoadSafetyMessageMessageFrame frame = new RoadSafetyMessageMessageFrame();
        frame.setValue((RoadSafetyMessage) value);
        yield frame;
      }
    };
  }

  private record JerBinding(int expectedMessageId, String choiceName, ObjectReader valueReader) {
  }

  private static final class ParsedJerFrame {
    private int messageId = -1;
    private boolean messageIdSeen;
    private boolean valueSeen;
    private Object value;
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
        "Unstripped IEEE 1609.2 payloads are not decoded by direct FFM decode; "
            + "provide extracted MessageFrame UPER bytes or use external codec mode");
  }

  private static boolean isIeee1609(byte[] encoded, OdeMessageFrameMetadata metadata) {
    return isIeee1609(encoded, metadata, null);
  }

  private static boolean isIeee1609(byte[] encoded, OdeMessageFrameMetadata metadata,
      SupportedMessageType knownType) {
    if (metadata.getEncodings() != null && metadata.getEncodings().stream()
        .anyMatch(encoding -> IEEE_PDU.equals(encoding.getElementType()))) {
      return true;
    }
    if (hasSignedDot2Prefix(encoded)) {
      return true;
    }
    String messageType = knownType == null ? UperUtil.determinePacketType(encoded) : knownType.name();
    if (messageType.isEmpty()) {
      return false;
    }
    // Preserve the external decoder's header handling: only search before the MessageFrame,
    // and retain the signed 1609.2 envelope while stripping WSMP / unsigned headers.
    byte[] envelope = UperUtil.stripDot3Header(encoded,
        SupportedMessageType.valueOf(messageType).getStartFlagBytes());
    return hasSignedDot2Prefix(envelope);
  }

  private static boolean hasSignedDot2Prefix(byte[] encoded) {
    if (encoded.length >= SIGNED_DOT2_PREFIX.length) {
      boolean prefix = true;
      for (int i = 0; i < SIGNED_DOT2_PREFIX.length; i++) {
        prefix &= encoded[i] == SIGNED_DOT2_PREFIX[i];
      }
      if (prefix) {
        return true;
      }
    }
    return false;
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
