package us.dot.its.jpo.ode.codec.ffmlib;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.kafka.core.KafkaTemplate;
import us.dot.its.jpo.asn.j2735.r2024.BasicSafetyMessage.BasicSafetyMessageMessageFrame;
import us.dot.its.jpo.asn.j2735.r2024.BasicSafetyMessage.BasicSafetyMessage;
import us.dot.its.jpo.asn.j2735.r2024.MessageFrame.DSRCmsgID;
import us.dot.its.jpo.asn.j2735.r2024.MessageFrame.MessageFrame;
import us.dot.its.jpo.ode.codec.ffmlib.FfmlibMessageFrameCodec.IntermediateDecodeResult;
import us.dot.its.jpo.ode.codec.ffmlib.FfmlibMessageFrameCodec.IntermediateEncoding;
import us.dot.its.jpo.ode.kafka.topics.JsonTopics;
import us.dot.its.jpo.ode.model.OdeAsn1Data;
import us.dot.its.jpo.ode.model.OdeAsn1Payload;
import us.dot.its.jpo.ode.model.OdeHexByteArray;
import us.dot.its.jpo.ode.model.OdeLogMetadata.RecordType;
import us.dot.its.jpo.ode.model.OdeLogMetadata.SecurityResultCode;
import us.dot.its.jpo.ode.model.OdeLogMsgMetadataLocation;
import us.dot.its.jpo.ode.model.OdeMessageFrameMetadata;
import us.dot.its.jpo.ode.model.OdeMessageFrameMetadata.Source;
import us.dot.its.jpo.ode.model.OdeMsgMetadata.GeneratedBy;
import us.dot.its.jpo.ode.model.ReceivedMessageDetails;
import us.dot.its.jpo.ode.model.RxSource;
import us.dot.its.jpo.ode.uper.SupportedMessageType;
import us.dot.its.jpo.ode.util.DateTimeUtils;
import us.dot.its.jpo.ode.util.JsonUtils;

@ExtendWith(MockitoExtension.class)
class FfmlibDecodeServiceTest {

  private static final String BSM_TOPIC = "topic.OdeBsmJson";
  private static final String BSM_HEX =
      "001480ADDA7CDE5517E962C66947240CB711E804C8B106B7DB7B12B3056B8AA1AA4E838D00400F86822A3CD398D89E1B"
          + "B8405B72C3C7A398C3CAFF63338526C646F4FFF524AD9E404039D5DA2FA62FEB57E305B552C7BE088B61E52A6BFC8CAF"
          + "5AF64414F3E4513FEC189F8B5E1138B824A48B29BA1F43CB12CE296BCA3DFA8F651AB44AB1B81B633B797D5645DAA4ED"
          + "ADAB4AC22A0BC38AB361443395BAA2C81CC4538E7413E9C8C3F696BB2C9B6B0000";
  @Mock
  private FfmlibMessageFrameCodec ffmlibCodec;
  @Mock
  private ObjectProvider<FfmlibMessageFrameCodec> ffmlibCodecProvider;
  @Mock
  private JsonTopics jsonTopics;
  @Mock
  private KafkaTemplate<String, String> kafkaTemplate;
  @Mock
  private ObjectProvider<FfmlibOutputPublisher> outputPublisherProvider;
  @Mock
  private FfmlibOutputPublisher outputPublisher;
  @Mock
  private ObjectMapper simpleObjectMapper;
  @Mock
  private ObjectReader messageFrameReader;
  @Mock
  private ObjectReader bsmValueReader;
  private SimpleMeterRegistry meterRegistry;
  private FfmlibDecodeService decodeService;

  @BeforeEach
  void setUp() {
    meterRegistry = new SimpleMeterRegistry();
    Asn1CodecModeProperties modeProperties = new Asn1CodecModeProperties();
    modeProperties.setCodecMode(Asn1CodecModeProperties.CodecMode.ffm);
    lenient().when(ffmlibCodecProvider.getIfAvailable()).thenReturn(ffmlibCodec);
    lenient().when(kafkaTemplate.send(any(), any(), any()))
        .thenReturn(CompletableFuture.completedFuture(null));
    lenient().when(outputPublisherProvider.getObject()).thenReturn(outputPublisher);
    lenient().when(outputPublisher.publish(any()))
        .thenReturn(CompletableFuture.completedFuture(
            FfmlibOutputPublisher.PublicationOutcome.PUBLISHED));
    lenient().when(simpleObjectMapper.readerFor(MessageFrame.class)).thenReturn(messageFrameReader);
    lenient().when(simpleObjectMapper.readerFor(us.dot.its.jpo.asn.j2735.r2024.BasicSafetyMessage.BasicSafetyMessage.class))
        .thenReturn(bsmValueReader);
    lenient().when(simpleObjectMapper.getFactory()).thenReturn(new JsonFactory());
    FfmlibProperties properties = new FfmlibProperties();
    decodeService = new FfmlibDecodeService(
        ffmlibCodecProvider,
        properties,
        modeProperties,
        jsonTopics,
        kafkaTemplate,
        outputPublisherProvider,
        simpleObjectMapper,
        meterRegistry,
        "topic.Asn1DecoderInput");
  }

  @AfterEach
  void tearDown() {
    decodeService.shutdown();
  }

  @Test
  void decodeOdeAsn1DataPublishesJson() throws Exception {
    stubSuccessfulDecode();

    OdeMessageFrameMetadata metadata = new OdeMessageFrameMetadata();
    metadata.setOriginIp("10.0.0.5");
    metadata.setOdeReceivedAt(DateTimeUtils.now());
    metadata.setSchemaVersion(9);
    OdeAsn1Data asn1Data = new OdeAsn1Data(metadata, new OdeAsn1Payload(new OdeHexByteArray(BSM_HEX)));

    decodeService.decode(asn1Data, "key-1");

    ArgumentCaptor<FfmlibDecodeService.PreparedDecodedMessage> messageCaptor =
        ArgumentCaptor.forClass(FfmlibDecodeService.PreparedDecodedMessage.class);
    verify(outputPublisher).publish(messageCaptor.capture());
    assertEquals(BSM_TOPIC, messageCaptor.getValue().topic());
    assertEquals("key-1", messageCaptor.getValue().key());
    assertTrue(messageCaptor.getValue().json().contains("10.0.0.5"));
    assertFalse(messageCaptor.getValue().json().contains("asnDecodeLatencyMs"));

    assertEquals(1.0, meterRegistry.get("ode.ffmlib.decode.asn").timer().count(), 0.0);
    assertEquals(1.0, meterRegistry.get("ode.ffmlib.decode.total").timer().count(), 0.0);
    assertEquals(1.0, meterRegistry.get("ode.ffmlib.decode.end.to.end")
        .tag("type", "BSM").timer().count(), 0.0);
    assertEquals(1.0, meterRegistry.get("ode.ffmlib.decode.messages")
        .tag("type", "BSM").tag("source", "import").counter().count(), 0.0);
    assertEquals(0.0, meterRegistry.get("ode.ffmlib.decode.messages")
        .tag("type", "TIM").tag("source", "import").counter().count(), 0.0);
    assertTrue(meterRegistry.find("ode.ffmlib.decode.failures").counters().isEmpty());
  }

  @Test
  void disabledJsonDestinationIsACompletedImportOutcome() throws Exception {
    stubSuccessfulDecode();
    when(outputPublisher.publish(any())).thenReturn(CompletableFuture.completedFuture(
        FfmlibOutputPublisher.PublicationOutcome.SKIPPED_DISABLED));
    OdeMessageFrameMetadata metadata = new OdeMessageFrameMetadata();
    metadata.setSchemaVersion(9);

    decodeService.decode(new OdeAsn1Data(metadata,
        new OdeAsn1Payload(new OdeHexByteArray(BSM_HEX))), "disabled-import");

    verify(outputPublisher).publish(any());
    verify(kafkaTemplate, never()).send(any(), any(), any());
    assertEquals(1.0, meterRegistry.get("ode.ffmlib.decode.messages")
        .tag("type", "BSM").tag("source", "import").counter().count(), 0.0);
    assertTrue(meterRegistry.find("ode.ffmlib.decode.failures").counters().isEmpty());
  }

  @Test
  void failedJsonPublicationIsRetriableAndNotCountedAsDecoded() throws Exception {
    stubSuccessfulDecode();
    when(outputPublisher.publish(any()))
        .thenReturn(CompletableFuture.failedFuture(new IllegalStateException("broker unavailable")));
    OdeMessageFrameMetadata metadata = new OdeMessageFrameMetadata();
    metadata.setOdeReceivedAt(DateTimeUtils.now());
    metadata.setSchemaVersion(9);

    assertThrows(FfmlibDecodeService.PublishFailure.class,
        () -> decodeService.decode(metadata, new byte[] {0x00, 0x14}, "key-1"));

    assertEquals(0.0, meterRegistry.get("ode.ffmlib.decode.messages")
        .tag("type", "BSM").tag("source", "udp").counter().count(), 0.0);
    assertEquals(1.0, meterRegistry.get("ode.ffmlib.decode.failures")
        .tag("type", "BSM").tag("source", "udp")
        .tag("reason", "publish").counter().count(), 0.0);
    assertTrue(meterRegistry.find("ode.ffmlib.decode.end.to.end").timers().isEmpty());
  }

  @Test
  void emptyDecodedSerializationFailsBeforePublishing() throws Exception {
    stubSuccessfulDecode();
    OdeMessageFrameMetadata metadata = new OdeMessageFrameMetadata();
    metadata.setSchemaVersion(9);

    try (MockedStatic<JsonUtils> jsonUtils = mockStatic(JsonUtils.class)) {
      jsonUtils.when(() -> JsonUtils.toJson(any(), eq(false))).thenReturn("");
      assertThrows(IllegalArgumentException.class,
          () -> decodeService.prepareRaw(metadata, new byte[] {0x00, 0x14}, "empty-json-raw",
              SupportedMessageType.BSM, new byte[] {0x00, 0x14}));
    }

    verify(outputPublisher, never()).publish(any());
    assertEquals(1.0, meterRegistry.get("ode.ffmlib.decode.failures")
        .tag("type", "BSM").tag("source", "udp").tag("reason", "serialization")
        .counter().count());
  }

  @Test
  void failedDecodedSerializationPropagatesThroughTheImportFailurePath() throws Exception {
    stubSuccessfulDecode();
    OdeMessageFrameMetadata metadata = new OdeMessageFrameMetadata();
    metadata.setSchemaVersion(9);
    OdeAsn1Data input = new OdeAsn1Data(metadata,
        new OdeAsn1Payload(new OdeHexByteArray(BSM_HEX)));

    try (MockedStatic<JsonUtils> jsonUtils = mockStatic(JsonUtils.class)) {
      jsonUtils.when(() -> JsonUtils.toJson(any(), eq(false))).thenReturn("");
      assertThrows(IllegalArgumentException.class,
          () -> decodeService.decode(input, "empty-json-import"));
    }

    verify(outputPublisher, never()).publish(any());
    double importFailures = meterRegistry.find("ode.ffmlib.decode.failures")
        .tag("source", "import").counters().stream().mapToDouble(counter -> counter.count()).sum();
    assertEquals(1.0, importFailures);
  }

  @Test
  void rawTopicDecodeUsesUdpMetricSource() throws Exception {
    stubSuccessfulDecode();
    OdeMessageFrameMetadata metadata = new OdeMessageFrameMetadata();
    metadata.setOdeReceivedAt(DateTimeUtils.now());
    metadata.setSchemaVersion(9);

    decodeService.decode(metadata, new byte[] {0x00, 0x14}, "udp-key");

    assertEquals(1.0, meterRegistry.get("ode.ffmlib.decode.messages")
        .tag("type", "BSM").tag("source", "udp").counter().count(), 0.0);
    assertEquals(0.0, meterRegistry.get("ode.ffmlib.decode.messages")
        .tag("type", "BSM").tag("source", "import").counter().count(), 0.0);
  }

  @Test
  void bsmWithUnexpectedMessageIdUsesGenericFrameReader() throws Exception {
    byte[] jer = "{\"messageId\":31,\"value\":{\"TravelerInformation\":{}}}"
        .getBytes(StandardCharsets.UTF_8);
    when(jsonTopics.getBsm()).thenReturn(BSM_TOPIC);
    when(ffmlibCodec.uperToIntermediate(any()))
        .thenReturn(new IntermediateDecodeResult(jer, IntermediateEncoding.JER));
    MessageFrame<?> frame = mock(BasicSafetyMessageMessageFrame.class);
    when(messageFrameReader.readValue(jer)).thenReturn(frame);
    OdeMessageFrameMetadata metadata = new OdeMessageFrameMetadata();
    metadata.setSchemaVersion(9);

    FfmlibDecodeService.PreparedDecodedMessage decoded = decodeService.prepareRaw(metadata,
        new byte[] {0x00, 0x14}, "unexpected-frame", SupportedMessageType.BSM,
        new byte[] {0x00, 0x14});

    assertEquals(BSM_TOPIC, decoded.topic());
    assertEquals("BSM", decoded.type());
    verify(messageFrameReader).readValue(jer);
  }

  @Test
  void bsmJerUsesDirectReaderWithoutGenericFrameMapping() throws Exception {
    byte[] jer = "{\"messageId\":20,\"value\":{\"BasicSafetyMessage\":{}}}"
        .getBytes(StandardCharsets.UTF_8);
    when(jsonTopics.getBsm()).thenReturn(BSM_TOPIC);
    when(ffmlibCodec.uperToIntermediate(any()))
        .thenReturn(new IntermediateDecodeResult(jer, IntermediateEncoding.JER));
    when(bsmValueReader.readValue(any(JsonParser.class))).thenReturn(new BasicSafetyMessage());
    OdeMessageFrameMetadata metadata = new OdeMessageFrameMetadata();
    metadata.setSchemaVersion(9);

    FfmlibDecodeService.PreparedDecodedMessage decoded = decodeService.prepareRaw(metadata,
        new byte[] {0x00, 0x14}, "direct-bsm", SupportedMessageType.BSM,
        new byte[] {0x00, 0x14});

    assertEquals(BSM_TOPIC, decoded.topic());
    verify(bsmValueReader).readValue(any(JsonParser.class));
    verify(messageFrameReader, never()).readValue(any(byte[].class));
  }

  @Test
  void malformedJerRecordsMappingFailure() throws Exception {
    byte[] jer = "{\"messageId\":".getBytes(StandardCharsets.UTF_8);
    when(ffmlibCodec.uperToIntermediate(any()))
        .thenReturn(new IntermediateDecodeResult(jer, IntermediateEncoding.JER));
    when(messageFrameReader.readValue(jer))
        .thenThrow(new JsonProcessingException("Malformed JER") {});
    OdeMessageFrameMetadata metadata = new OdeMessageFrameMetadata();
    metadata.setSchemaVersion(9);

    IllegalArgumentException failure = assertThrows(IllegalArgumentException.class,
        () -> decodeService.prepareRaw(metadata, new byte[] {0x00, 0x14}, "malformed-jer",
            SupportedMessageType.BSM, new byte[] {0x00, 0x14}));

    assertTrue(failure.getMessage().contains("JER"));
    assertEquals(1.0, meterRegistry.get("ode.ffmlib.decode.failures")
        .tag("type", "BSM").tag("source", "udp").tag("reason", "mapping")
        .counter().count());
  }

  @Test
  void decodeOdeAsn1DataPreservesReceivedAtAndLogFileMetadata() throws Exception {
    stubSuccessfulDecode();

    OdeMessageFrameMetadata metadata = new OdeMessageFrameMetadata();
    metadata.setOdeReceivedAt("2026-08-28T12:34:56.789Z");
    metadata.setLogFileName("rxMsg.gz");
    metadata.setRecordType(RecordType.rxMsg);
    metadata.setSecurityResultCode(SecurityResultCode.success);
    metadata.setRecordGeneratedAt("2026-08-28T12:34:55.000Z");
    metadata.setRecordGeneratedBy(GeneratedBy.OBU);
    metadata.setSource(Source.RV);
    ReceivedMessageDetails receivedMessageDetails = new ReceivedMessageDetails(
        new OdeLogMsgMetadataLocation("40.1", "-105.2", "1600", "15", "90"),
        RxSource.RV);
    metadata.setReceivedMessageDetails(receivedMessageDetails);
    metadata.setSchemaVersion(9);
    OdeAsn1Data asn1Data = new OdeAsn1Data(
        metadata, new OdeAsn1Payload(new OdeHexByteArray(BSM_HEX)));

    decodeService.decode(asn1Data, "log-file-key");

    ArgumentCaptor<FfmlibDecodeService.PreparedDecodedMessage> messageCaptor =
        ArgumentCaptor.forClass(FfmlibDecodeService.PreparedDecodedMessage.class);
    verify(outputPublisher).publish(messageCaptor.capture());
    org.mockito.Mockito.verifyNoInteractions(kafkaTemplate);
    var publishedMetadata = new com.fasterxml.jackson.databind.ObjectMapper()
        .readTree(messageCaptor.getValue().json()).path("metadata");
    assertEquals("2026-08-28T12:34:56.789Z",
        publishedMetadata.path("odeReceivedAt").asText());
    assertEquals("rxMsg.gz", publishedMetadata.path("logFileName").asText());
    assertEquals("rxMsg", publishedMetadata.path("recordType").asText());
    assertEquals("success", publishedMetadata.path("securityResultCode").asText());
    assertEquals("2026-08-28T12:34:55.000Z",
        publishedMetadata.path("recordGeneratedAt").asText());
    assertEquals("OBU", publishedMetadata.path("recordGeneratedBy").asText());
    assertEquals("RV", publishedMetadata.path("source").asText());
    assertEquals("RV",
        publishedMetadata.path("receivedMessageDetails").path("rxSource").asText());
    assertEquals("40.1", publishedMetadata.path("receivedMessageDetails")
        .path("locationData").path("latitude").asText());
  }

  @Test
  void decodeOdeAsn1DataAddsReceivedAtWhenMissing() throws Exception {
    stubSuccessfulDecode();

    OdeMessageFrameMetadata metadata = new OdeMessageFrameMetadata();
    metadata.setOdeReceivedAt(null);
    metadata.setSchemaVersion(9);
    OdeAsn1Data asn1Data = new OdeAsn1Data(
        metadata, new OdeAsn1Payload(new OdeHexByteArray(BSM_HEX)));

    decodeService.decode(asn1Data, "missing-received-at");

    ArgumentCaptor<FfmlibDecodeService.PreparedDecodedMessage> messageCaptor =
        ArgumentCaptor.forClass(FfmlibDecodeService.PreparedDecodedMessage.class);
    verify(outputPublisher).publish(messageCaptor.capture());
    String odeReceivedAt = new com.fasterxml.jackson.databind.ObjectMapper()
        .readTree(messageCaptor.getValue().json()).path("metadata").path("odeReceivedAt").asText();
    assertTrue(!odeReceivedAt.isBlank());
  }

  @Test
  void signedIeee1609EnvelopeIsRejected() {
    OdeMessageFrameMetadata metadata = new OdeMessageFrameMetadata();
    metadata.setSchemaVersion(9);
    OdeAsn1Data input = new OdeAsn1Data(
        metadata, new OdeAsn1Payload(new OdeHexByteArray("038100")));

    UnsupportedOperationException failure = assertThrows(UnsupportedOperationException.class,
        () -> decodeService.decode(input, "signed-bsm"));
    assertTrue(failure.getMessage().contains("use external codec mode"));
    assertEquals(1.0, meterRegistry.get("ode.ffmlib.decode.failures")
        .tag("type", "unknown")
        .tag("source", "import")
        .tag("reason", "signed_payload")
        .counter()
        .count(), 0.0);
    assertEquals(0.0, meterRegistry.get("ode.ffmlib.decode.messages")
        .tag("type", "BSM").tag("source", "import").counter().count(), 0.0);
  }

  @Test
  void encryptedIeee1609EnvelopeIsRejected() {
    OdeMessageFrameMetadata metadata = new OdeMessageFrameMetadata();
    metadata.setSchemaVersion(9);
    OdeAsn1Data input = new OdeAsn1Data(
        metadata, new OdeAsn1Payload(new OdeHexByteArray("038100")));

    assertThrows(UnsupportedOperationException.class, () -> decodeService.decode(input, "encrypted"));
  }

  @Test
  void rawTopicSignedEnvelopeIsCheckedBeforeStrippedUperDecode() {
    OdeMessageFrameMetadata metadata = new OdeMessageFrameMetadata();
    metadata.setSchemaVersion(9);
    byte[] originalSignedBytes = {(byte) 0x03, (byte) 0x81, 0x00, 0x00, 0x14};

    assertThrows(UnsupportedOperationException.class, () -> decodeService.prepareRaw(metadata,
        new byte[] {0x00, 0x14}, "replayed-signed", SupportedMessageType.BSM,
        originalSignedBytes));

    verify(ffmlibCodec, never()).uperToIntermediate(any());
    assertEquals(1.0, meterRegistry.get("ode.ffmlib.decode.failures")
        .tag("type", "BSM").tag("source", "udp").tag("reason", "signed_payload")
        .counter().count(), 0.0);
  }

  @Test
  void externalModeRetainsKafkaDecoderInputRollbackPath() {
    decodeService.shutdown();
    Asn1CodecModeProperties modeProperties = new Asn1CodecModeProperties();
    modeProperties.setCodecMode(Asn1CodecModeProperties.CodecMode.external);
    decodeService = new FfmlibDecodeService(
        ffmlibCodecProvider,
        new FfmlibProperties(),
        modeProperties,
        jsonTopics,
        kafkaTemplate,
        outputPublisherProvider,
        simpleObjectMapper,
        meterRegistry,
        "topic.Asn1DecoderInput");
    OdeMessageFrameMetadata metadata = new OdeMessageFrameMetadata();
    OdeAsn1Data input = new OdeAsn1Data(
        metadata, new OdeAsn1Payload(new OdeHexByteArray("0014")));

    decodeService.decode(input, "external-key");

    verify(kafkaTemplate).send(
        eq("topic.Asn1DecoderInput"), eq("external-key"), any(String.class));
    assertEquals(0.0, meterRegistry.get("ode.ffmlib.decode.messages")
        .tag("type", "BSM").tag("source", "import").counter().count(), 0.0);
    assertTrue(meterRegistry.find("ode.ffmlib.decode.failures").counters().isEmpty());
  }

  @Test
  @SuppressWarnings({"rawtypes", "unchecked"})
  void unmappedMessageTypeIsCountedAsDropped() throws Exception {
    when(ffmlibCodec.uperToIntermediate(any()))
        .thenReturn(new IntermediateDecodeResult("{}".getBytes(StandardCharsets.UTF_8),
            IntermediateEncoding.JER));
    MessageFrame frame = mock(BasicSafetyMessageMessageFrame.class);
    DSRCmsgID msgId = mock(DSRCmsgID.class);
    when(frame.getMessageId()).thenReturn(msgId);
    when(msgId.name()).thenReturn(Optional.of("notAMessage"));
    when(messageFrameReader.readValue(any(byte[].class))).thenReturn(frame);

    OdeMessageFrameMetadata metadata = new OdeMessageFrameMetadata();
    metadata.setSchemaVersion(9);
    assertThrows(IllegalArgumentException.class, () -> decodeService.decode(
        new OdeAsn1Data(metadata, new OdeAsn1Payload(new OdeHexByteArray(BSM_HEX))),
        "drop-key"));

    verify(outputPublisher, never()).publish(any());
    verify(kafkaTemplate, never()).send(any(), any(), any());
    assertEquals(1.0, meterRegistry.get("ode.ffmlib.decode.dropped")
        .tag("type", "unknown")
        .tag("source", "import")
        .tag("reason", "unmapped_topic")
        .counter()
        .count(), 0.0);
    assertEquals(0.0, meterRegistry.get("ode.ffmlib.decode.messages")
        .tag("type", "BSM").tag("source", "import").counter().count(), 0.0);
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  private void stubSuccessfulDecode() throws Exception {
    lenient().when(jsonTopics.getBsm()).thenReturn(BSM_TOPIC);
    lenient().when(ffmlibCodec.uperToIntermediate(any()))
        .thenReturn(new IntermediateDecodeResult("{}".getBytes(StandardCharsets.UTF_8),
            IntermediateEncoding.JER));
    MessageFrame frame = mock(BasicSafetyMessageMessageFrame.class);
    DSRCmsgID msgId = mock(DSRCmsgID.class);
    // Import path resolves topic from messageId; UDP path uses knownType and may skip these.
    lenient().when(frame.getMessageId()).thenReturn(msgId);
    lenient().when(msgId.name()).thenReturn(Optional.of("basicSafetyMessage"));
    lenient().when(messageFrameReader.readValue(any(byte[].class))).thenReturn(frame);
  }
}
