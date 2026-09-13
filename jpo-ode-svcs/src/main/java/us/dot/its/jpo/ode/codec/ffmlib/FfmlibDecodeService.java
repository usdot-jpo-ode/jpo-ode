package us.dot.its.jpo.ode.codec.ffmlib;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import java.net.DatagramPacket;
import java.util.HexFormat;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import us.dot.its.jpo.asn.j2735.r2024.MessageFrame.DSRCmsgID;
import us.dot.its.jpo.asn.j2735.r2024.MessageFrame.MessageFrame;
import us.dot.its.jpo.ode.codec.ffmlib.FfmlibMessageFrameCodec.IntermediateDecodeResult;
import us.dot.its.jpo.ode.codec.ffmlib.FfmlibMessageFrameCodec.IntermediateEncoding;
import us.dot.its.jpo.ode.kafka.topics.JsonTopics;
import us.dot.its.jpo.ode.kafka.topics.RawEncodedJsonTopics;
import us.dot.its.jpo.ode.model.OdeAsn1Data;
import us.dot.its.jpo.ode.model.OdeHexByteArray;
import us.dot.its.jpo.ode.model.OdeLogMetadata.RecordType;
import us.dot.its.jpo.ode.model.OdeMessageFrameData;
import us.dot.its.jpo.ode.model.OdeMessageFrameMetadata;
import us.dot.its.jpo.ode.model.OdeMessageFrameMetadata.Source;
import us.dot.its.jpo.ode.model.OdeMessageFramePayload;
import us.dot.its.jpo.ode.model.OdeMsgMetadata.GeneratedBy;
import us.dot.its.jpo.ode.model.RxSource;
import us.dot.its.jpo.ode.udp.InvalidPayloadException;
import us.dot.its.jpo.ode.udp.UdpHexDecoder;
import us.dot.its.jpo.ode.udp.UdpHexDecoder.UdpDecodeInput;
import us.dot.its.jpo.ode.uper.SupportedMessageType;
import us.dot.its.jpo.ode.util.CodecUtils;
import us.dot.its.jpo.ode.util.DateTimeUtils;
import us.dot.its.jpo.ode.util.JsonUtils;

/**
 * Primary in-process J2735 UPER decode service backed by the FFMLib native codec.
 *
 * <p>UDP path: packet preparation, native decode, POJO mapping, JSON serialization, and Kafka
 * publication run synchronously on the receiver path, preserving the existing delivery contract.
 *
 * <p>Import path ({@link #decode(OdeAsn1Data, String)}) runs synchronously on the Kafka listener
 * thread.
 */
@Slf4j
@Service
public class FfmlibDecodeService {

  private static final byte[] SIGNED_DOT2_PREFIX = {0x03, (byte) 0x81, 0x00};
  private static final String IEEE_PDU = "Ieee1609Dot2Data";

  private record MessageTypeConfig(
      RecordType recordType, Source source, GeneratedBy generatedBy, boolean includeRxDetails) {}

  private static final Map<SupportedMessageType, MessageTypeConfig> MSG_TYPE_CONFIGS = Map.of(
      SupportedMessageType.BSM,  new MessageTypeConfig(RecordType.bsmTx,  Source.EV,  GeneratedBy.OBU,     true),
      SupportedMessageType.TIM,  new MessageTypeConfig(RecordType.timMsg, Source.RSU, GeneratedBy.RSU,     false),
      SupportedMessageType.MAP,  new MessageTypeConfig(RecordType.mapTx,  Source.RSU, GeneratedBy.RSU,     false),
      SupportedMessageType.SPAT, new MessageTypeConfig(RecordType.spatTx, Source.RSU, GeneratedBy.RSU,     false),
      SupportedMessageType.SSM,  new MessageTypeConfig(RecordType.ssmTx,  Source.RSU, GeneratedBy.RSU,     false),
      SupportedMessageType.SRM,  new MessageTypeConfig(RecordType.srmTx,  Source.RSU, GeneratedBy.OBU,     false),
      SupportedMessageType.PSM,  new MessageTypeConfig(RecordType.psmTx,  Source.RSU, GeneratedBy.UNKNOWN, false),
      SupportedMessageType.SDSM, new MessageTypeConfig(RecordType.sdsmTx, Source.RSU, GeneratedBy.RSU,     false),
      SupportedMessageType.RTCM, new MessageTypeConfig(RecordType.rtcmTx, Source.RSU, GeneratedBy.RSU,     false),
      SupportedMessageType.RSM,  new MessageTypeConfig(RecordType.rsmTx,  Source.RSU, GeneratedBy.RSU,     false)
  );

  private final ObjectProvider<FfmlibMessageFrameCodec> ffmlibCodec;
  private final Asn1CodecModeProperties modeProperties;
  private final RawEncodedJsonTopics rawEncodedJsonTopics;
  private final String externalDecoderInputTopic;
  private final JsonTopics jsonTopics;
  private final KafkaTemplate<String, String> kafkaTemplate;
  private final XmlMapper simpleXmlMapper;
  private final Timer nativeTimer;
  private final Timer pojoTimer;
  private final Timer jsonTimer;
  private final Timer sendTimer;
  private final Timer totalTimer;
  private final Counter failureCounter;

  public FfmlibDecodeService(
      ObjectProvider<FfmlibMessageFrameCodec> ffmlibCodec,
      FfmlibProperties properties,
      Asn1CodecModeProperties modeProperties,
      JsonTopics jsonTopics,
      RawEncodedJsonTopics rawEncodedJsonTopics,
      KafkaTemplate<String, String> kafkaTemplate,
      @Qualifier("simpleXmlMapper") XmlMapper simpleXmlMapper,
      MeterRegistry meterRegistry,
      @Value("${ode.kafka.topics.asn1.decoder-input}") String externalDecoderInputTopic) {
    this.ffmlibCodec = ffmlibCodec;
    this.modeProperties = modeProperties;
    this.rawEncodedJsonTopics = rawEncodedJsonTopics;
    this.externalDecoderInputTopic = externalDecoderInputTopic;
    this.jsonTopics = jsonTopics;
    this.kafkaTemplate = kafkaTemplate;
    this.simpleXmlMapper = simpleXmlMapper;
    this.nativeTimer = Timer.builder("ode.ffmlib.decode.stage")
        .tag("stage", "native").register(meterRegistry);
    this.pojoTimer = Timer.builder("ode.ffmlib.decode.stage")
        .tag("stage", "pojo").register(meterRegistry);
    this.jsonTimer = Timer.builder("ode.ffmlib.decode.stage")
        .tag("stage", "json").register(meterRegistry);
    this.sendTimer = Timer.builder("ode.ffmlib.decode.stage")
        .tag("stage", "send").register(meterRegistry);
    this.totalTimer = Timer.builder("ode.ffmlib.decode.total").register(meterRegistry);
    this.failureCounter = Counter.builder("ode.ffmlib.decode.failures")
        .description("In-process ASN.1 decode operations that failed")
        .register(meterRegistry);
  }

  /**
   * Decodes a UDP packet on the receiver path and publishes the decoded JSON payload.
   */
  public void decode(DatagramPacket packet, SupportedMessageType msgType)
      throws InvalidPayloadException {
    MessageTypeConfig config = MSG_TYPE_CONFIGS.get(msgType);
    if (config == null) {
      log.warn("FFMLib decode: unsupported message type {}", msgType);
      return;
    }

    long prepStart = System.nanoTime();
    UdpDecodeInput input = UdpHexDecoder.prepareDecodeInput(
        packet, msgType, config.recordType(), config.source(), config.generatedBy(),
        config.includeRxDetails());
    log.debug("Prepared {} UDP packet for FFMLib decode in {}us", msgType,
        (System.nanoTime() - prepStart) / 1000);

    // Hand off owned copies (uper bytes + metadata) — safe after receive buffer reuse.
    final OdeMessageFrameMetadata metadata = input.metadata();
    final byte[] uperBytes = input.uperBytes();
    if (!modeProperties.isFfm()) {
      OdeAsn1Data raw = new OdeAsn1Data(metadata, new us.dot.its.jpo.ode.model.OdeAsn1Payload(uperBytes));
      try {
        kafkaTemplate.send(resolveRawTopic(msgType), JsonUtils.toJson(raw, false));
      } catch (Exception error) {
        throw new InvalidPayloadException(
            "Unable to publish external ASN.1 decode input: " + error.getMessage());
      }
      return;
    }
    runPublishDecoded(metadata, uperBytes, null, msgType);
  }

  /**
   * Decodes an already-parsed {@link OdeAsn1Data} — used by the file-import path through
   * {@code RawEncoded*JsonRouter} and {@code topic.OdeRawEncoded*Json}.
   */
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
      byte[] uperBytes = CodecUtils.fromHex(hexBytes.getBytes());
      log.debug("Prepared raw {} ASN.1 payload in {}us", key,
          (System.nanoTime() - prepStart) / 1000);

      // totalTimer is recorded inside runPublishDecoded (same as the UDP worker path).
      runPublishDecoded(
          (OdeMessageFrameMetadata) asn1Data.getMetadata(), uperBytes, key, null);
    } catch (ClassCastException e) {
      log.error("FFMLib decode failed (unexpected payload type) for key {}: {}", key, e.getMessage(),
          e);
      throw new IllegalArgumentException("Unexpected ASN.1 payload type", e);
    } catch (Exception e) {
      log.error("FFMLib decode unexpected error for key {}: {}", key, e.getMessage(), e);
      throw new IllegalArgumentException("Unable to decode ASN.1 payload", e);
    }
  }

  private void runPublishDecoded(
      OdeMessageFrameMetadata metadata,
      byte[] uperBytes,
      String key,
      SupportedMessageType knownType) {
    long totalStart = System.nanoTime();
    try {
      publishDecoded(metadata, uperBytes, key, knownType);
    } catch (RuntimeException error) {
      failureCounter.increment();
      throw error;
    } finally {
      totalTimer.record(System.nanoTime() - totalStart, TimeUnit.NANOSECONDS);
    }
  }

  private void publishDecoded(
      OdeMessageFrameMetadata metadata,
      byte[] uperBytes,
      String key,
      SupportedMessageType knownType) {
    try {
      long nativeStart = System.nanoTime();
      byte[] messageFrameBytes = unwrapIeee1609IfPresent(metadata, uperBytes);
      IntermediateDecodeResult intermediate = codec().uperToIntermediate(messageFrameBytes);
      long nativeNanos = System.nanoTime() - nativeStart;
      nativeTimer.record(nativeNanos, TimeUnit.NANOSECONDS);

      long pojoStart = System.nanoTime();
      MessageFrame<?> messageFrame = parseMessageFrame(intermediate);
      long pojoNanos = System.nanoTime() - pojoStart;
      pojoTimer.record(pojoNanos, TimeUnit.NANOSECONDS);

      long asnDecodeLatencyMs = (nativeNanos + pojoNanos) / 1_000_000;
      if (metadata.getOdeReceivedAt() == null || metadata.getOdeReceivedAt().isBlank()) {
        metadata.setOdeReceivedAt(DateTimeUtils.now());
      }
      metadata.setAsnDecodeLatencyMs(Long.valueOf(asnDecodeLatencyMs));
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
      String json = JsonUtils.toJson(frameData, false);
      jsonTimer.record(System.nanoTime() - jsonStart, TimeUnit.NANOSECONDS);

      String topic = resolveJsonTopic(knownType, messageFrame);
      if (topic == null) {
        log.warn("FFMLib decode: no topic mapped for message, key {} dropped.", key);
        return;
      }

      long sendStart = System.nanoTime();
      kafkaTemplate.send(topic, key, json);
      sendTimer.record(System.nanoTime() - sendStart, TimeUnit.NANOSECONDS);

      if (log.isDebugEnabled()) {
        log.debug(
            "FFMLib decode key={} encoding={} native={}us pojo={}us totalAsn={}ms topic={}",
            key,
            intermediate.encoding(),
            nativeNanos / 1000,
            pojoNanos / 1000,
            asnDecodeLatencyMs,
            topic);
      }
    } catch (JsonProcessingException e) {
      log.error("FFMLib decode failed (JSON/XML processing) for key {}: {}", key, e.getMessage(), e);
      throw new IllegalArgumentException("Unable to map decoded ASN.1 XER", e);
    } catch (Exception e) {
      log.error("FFMLib decode unexpected error for key {}: {}", key, e.getMessage(), e);
      throw new IllegalArgumentException("Unable to decode ASN.1 payload", e);
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

    String xer = codec().decodeToXer(encoded, IEEE_PDU, j2735ffm.AsnEncoding.COER);
    try {
      JsonNode ieee = simpleXmlMapper.readTree(xer);
      JsonNode content = ieee.path("content");
      JsonNode unsecured;
      if (content.has("signedData")) {
        JsonNode signed = content.path("signedData");
        applySignedMetadata(metadata, signed);
        unsecured = signed.path("tbsData").path("payload").path("data")
            .path("content").path("unsecuredData");
      } else if (content.has("unsecuredData")) {
        metadata.setCertPresent(false);
        unsecured = content.path("unsecuredData");
      } else if (content.has("encryptedData")) {
        throw new IllegalArgumentException(
            "Encrypted Ieee1609Dot2Data cannot be decoded without decryption");
      } else {
        throw new IllegalArgumentException(
            "Ieee1609Dot2Data has no inline unsecuredData payload; external payloads are unsupported");
      }
      if (!unsecured.isTextual() || unsecured.textValue().isBlank()) {
        throw new IllegalArgumentException(
            "Ieee1609Dot2Data has no inline unsecuredData payload; external payloads are unsupported");
      }
      return HexFormat.of().parseHex(unsecured.textValue().replaceAll("\\s", ""));
    } catch (JsonProcessingException error) {
      throw new IllegalArgumentException("Unable to parse Ieee1609Dot2Data XER", error);
    }
  }

  private void applySignedMetadata(OdeMessageFrameMetadata metadata, JsonNode signed) {
    JsonNode header = signed.path("tbsData").path("headerInfo");
    if (header.has("psid")) {
      metadata.setPsid(number(header.path("psid")));
    }
    if (header.has("generationTime")) {
      metadata.setGenerationTime(
          DateTimeUtils.ieee1609Time64ToIso(number(header.path("generationTime"))));
    }
    if (header.has("expiryTime")) {
      metadata.setExpiryTime(
          DateTimeUtils.ieee1609Time64ToIso(number(header.path("expiryTime"))));
    }

    JsonNode certificates = signed.path("signer").path("certificate");
    metadata.setCertPresent(!certificates.isMissingNode() && !certificates.isNull()
        && (!certificates.isArray() || !certificates.isEmpty()));
    JsonNode validity = certificates.findValue("validityPeriod");
    if (validity != null && validity.has("start")) {
      long start = number(validity.path("start"));
      metadata.setCertificateStartTime(DateTimeUtils.ieee1609Time32ToIso(start));
      long durationSeconds = durationSeconds(validity.path("duration"));
      if (durationSeconds >= 0) {
        metadata.setCertificateExpiryTime(
            DateTimeUtils.ieee1609Time32ToIso(Math.addExact(start, durationSeconds)));
      }
    }
  }

  private static long durationSeconds(JsonNode duration) {
    if (duration.has("microseconds")) {
      return number(duration.path("microseconds")) / 1_000_000L;
    }
    if (duration.has("milliseconds")) {
      return number(duration.path("milliseconds")) / 1_000L;
    }
    if (duration.has("seconds")) {
      return number(duration.path("seconds"));
    }
    if (duration.has("minutes")) {
      return Math.multiplyExact(number(duration.path("minutes")), 60L);
    }
    if (duration.has("hours")) {
      return Math.multiplyExact(number(duration.path("hours")), 3_600L);
    }
    if (duration.has("sixtyHours")) {
      return Math.multiplyExact(number(duration.path("sixtyHours")), 216_000L);
    }
    if (duration.has("years")) {
      return Math.multiplyExact(number(duration.path("years")), 31_557_600L);
    }
    return -1L;
  }

  private static long number(JsonNode value) {
    return value.isIntegralNumber() ? value.longValue() : Long.parseLong(value.asText());
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

  private String resolveRawTopic(SupportedMessageType msgType) {
    return switch (msgType) {
      case BSM -> rawEncodedJsonTopics.getBsm();
      case TIM -> rawEncodedJsonTopics.getTim();
      case MAP -> rawEncodedJsonTopics.getMap();
      case SPAT -> rawEncodedJsonTopics.getSpat();
      case PSM -> rawEncodedJsonTopics.getPsm();
      case SSM -> rawEncodedJsonTopics.getSsm();
      case SRM -> rawEncodedJsonTopics.getSrm();
      case SDSM -> rawEncodedJsonTopics.getSdsm();
      case RTCM -> rawEncodedJsonTopics.getRtcm();
      case RSM -> rawEncodedJsonTopics.getRsm();
    };
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
