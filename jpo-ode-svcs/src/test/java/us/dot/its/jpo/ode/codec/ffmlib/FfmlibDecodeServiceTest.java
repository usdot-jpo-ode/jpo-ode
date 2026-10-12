package us.dot.its.jpo.ode.codec.ffmlib;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import j2735ffm.ConvertException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import us.dot.its.jpo.asn.j2735.r2024.BasicSafetyMessage.BasicSafetyMessage;
import us.dot.its.jpo.asn.j2735.r2024.BasicSafetyMessage.BasicSafetyMessageMessageFrame;
import us.dot.its.jpo.asn.j2735.r2024.MapData.MapData;
import us.dot.its.jpo.asn.j2735.r2024.MapData.MapDataMessageFrame;
import us.dot.its.jpo.asn.j2735.r2024.MessageFrame.DSRCmsgID;
import us.dot.its.jpo.asn.j2735.r2024.MessageFrame.MessageFrame;
import us.dot.its.jpo.asn.j2735.r2024.PersonalSafetyMessage.PersonalSafetyMessage;
import us.dot.its.jpo.asn.j2735.r2024.PersonalSafetyMessage.PersonalSafetyMessageMessageFrame;
import us.dot.its.jpo.asn.j2735.r2024.RoadSafetyMessage.RoadSafetyMessage;
import us.dot.its.jpo.asn.j2735.r2024.RoadSafetyMessage.RoadSafetyMessageMessageFrame;
import us.dot.its.jpo.asn.j2735.r2024.RTCMcorrections.RTCMcorrections;
import us.dot.its.jpo.asn.j2735.r2024.RTCMcorrections.RTCMcorrectionsMessageFrame;
import us.dot.its.jpo.asn.j2735.r2024.SensorDataSharingMessage.SensorDataSharingMessage;
import us.dot.its.jpo.asn.j2735.r2024.SensorDataSharingMessage.SensorDataSharingMessageMessageFrame;
import us.dot.its.jpo.asn.j2735.r2024.SignalRequestMessage.SignalRequestMessage;
import us.dot.its.jpo.asn.j2735.r2024.SignalRequestMessage.SignalRequestMessageMessageFrame;
import us.dot.its.jpo.asn.j2735.r2024.SignalStatusMessage.SignalStatusMessage;
import us.dot.its.jpo.asn.j2735.r2024.SignalStatusMessage.SignalStatusMessageMessageFrame;
import us.dot.its.jpo.asn.j2735.r2024.SPAT.SPAT;
import us.dot.its.jpo.asn.j2735.r2024.SPAT.SPATMessageFrame;
import us.dot.its.jpo.asn.j2735.r2024.TravelerInformation.TravelerInformation;
import us.dot.its.jpo.asn.j2735.r2024.TravelerInformation.TravelerInformationMessageFrame;
import us.dot.its.jpo.ode.codec.ffmlib.FfmlibMessageFrameCodec.IntermediateDecodeResult;
import us.dot.its.jpo.ode.codec.ffmlib.FfmlibMessageFrameCodec.IntermediateEncoding;
import us.dot.its.jpo.ode.kafka.listeners.json.RawEncodedJsonService;
import us.dot.its.jpo.ode.kafka.topics.JsonTopics;
import us.dot.its.jpo.ode.kafka.topics.RawEncodedJsonTopics;
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
import us.dot.its.jpo.ode.udp.UdpHexDecoder;
import us.dot.its.jpo.ode.util.CodecUtils;
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
  private static final List<KnownJerFixture> KNOWN_JER_FIXTURES = List.of(
      new KnownJerFixture(SupportedMessageType.BSM, 20, "BasicSafetyMessage",
          BasicSafetyMessageMessageFrame.class),
      new KnownJerFixture(SupportedMessageType.MAP, 18, "MapData", MapDataMessageFrame.class),
      new KnownJerFixture(SupportedMessageType.SPAT, 19, "SPAT", SPATMessageFrame.class),
      new KnownJerFixture(SupportedMessageType.TIM, 31, "TravelerInformation",
          TravelerInformationMessageFrame.class),
      new KnownJerFixture(SupportedMessageType.PSM, 32, "PersonalSafetyMessage",
          PersonalSafetyMessageMessageFrame.class),
      new KnownJerFixture(SupportedMessageType.SRM, 29, "SignalRequestMessage",
          SignalRequestMessageMessageFrame.class),
      new KnownJerFixture(SupportedMessageType.SSM, 30, "SignalStatusMessage",
          SignalStatusMessageMessageFrame.class),
      new KnownJerFixture(SupportedMessageType.SDSM, 41, "SensorDataSharingMessage",
          SensorDataSharingMessageMessageFrame.class),
      new KnownJerFixture(SupportedMessageType.RTCM, 28, "RTCMcorrections",
          RTCMcorrectionsMessageFrame.class),
      new KnownJerFixture(SupportedMessageType.RSM, 33, "RoadSafetyMessage",
          RoadSafetyMessageMessageFrame.class));
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
    ObjectMapper leafMapper = new ObjectMapper();
    lenient().when(simpleObjectMapper.readerFor(MapData.class))
        .thenReturn(leafMapper.readerFor(MapData.class));
    lenient().when(simpleObjectMapper.readerFor(SPAT.class))
        .thenReturn(leafMapper.readerFor(SPAT.class));
    lenient().when(simpleObjectMapper.readerFor(TravelerInformation.class))
        .thenReturn(leafMapper.readerFor(TravelerInformation.class));
    lenient().when(simpleObjectMapper.readerFor(PersonalSafetyMessage.class))
        .thenReturn(leafMapper.readerFor(PersonalSafetyMessage.class));
    lenient().when(simpleObjectMapper.readerFor(SignalRequestMessage.class))
        .thenReturn(leafMapper.readerFor(SignalRequestMessage.class));
    lenient().when(simpleObjectMapper.readerFor(SignalStatusMessage.class))
        .thenReturn(leafMapper.readerFor(SignalStatusMessage.class));
    lenient().when(simpleObjectMapper.readerFor(SensorDataSharingMessage.class))
        .thenReturn(leafMapper.readerFor(SensorDataSharingMessage.class));
    lenient().when(simpleObjectMapper.readerFor(RTCMcorrections.class))
        .thenReturn(leafMapper.readerFor(RTCMcorrections.class));
    lenient().when(simpleObjectMapper.readerFor(RoadSafetyMessage.class))
        .thenReturn(leafMapper.readerFor(RoadSafetyMessage.class));
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
    assertEquals(0.0, meterRegistry.get("ode.ffmlib.decode.messages")
        .tag("type", "BSM").tag("source", "import").counter().count(), 0.0);
    assertEquals(1.0, meterRegistry.get("ode.ffmlib.decode.total").timer().count());
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
              SupportedMessageType.BSM));
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
  void mismatchedGeneratedTimFrameFromKnownBsmFallbackIsRejected() throws Exception {
    byte[] jer = "{\"messageId\":31,\"value\":{\"TravelerInformation\":{}}}"
        .getBytes(StandardCharsets.UTF_8);
    when(ffmlibCodec.uperToIntermediate(any()))
        .thenReturn(new IntermediateDecodeResult(jer, IntermediateEncoding.JER));
    TravelerInformationMessageFrame timFrame = new TravelerInformationMessageFrame();
    timFrame.setValue(new TravelerInformation());
    when(messageFrameReader.readValue(jer)).thenReturn(timFrame);
    OdeMessageFrameMetadata metadata = new OdeMessageFrameMetadata();
    metadata.setSchemaVersion(9);

    IllegalArgumentException failure = assertThrows(IllegalArgumentException.class,
        () -> decodeService.prepareRaw(metadata, new byte[] {0x00, 0x14}, "unexpected-frame",
            SupportedMessageType.BSM));

    assertTrue(failure.getMessage().contains("does not match"));
    verify(messageFrameReader).readValue(jer);
    assertEquals(0.0, meterRegistry.get("ode.ffmlib.decode.messages")
        .tag("type", "BSM").tag("source", "udp").counter().count());
    assertEquals(1.0, meterRegistry.get("ode.ffmlib.decode.failures")
        .tag("type", "BSM").tag("source", "udp").tag("reason", "mapping")
        .counter().count());
  }

  @Test
  void wrappedRecognizedConvertExceptionRemainsMalformedInput() throws Exception {
    ConvertException nativeFailure = mock(ConvertException.class);
    when(nativeFailure.getMessage()).thenReturn("MessageFrame: Error decoding PDU");
    RuntimeException wrappedFailure = new IllegalStateException("native conversion wrapper",
        nativeFailure);
    when(ffmlibCodec.uperToIntermediate(any())).thenThrow(wrappedFailure);
    OdeMessageFrameMetadata metadata = new OdeMessageFrameMetadata();
    metadata.setSchemaVersion(9);

    IllegalArgumentException failure = assertThrows(IllegalArgumentException.class,
        () -> decodeService.prepareRaw(metadata, new byte[] {0x00, 0x14}, "malformed-native",
            SupportedMessageType.BSM));

    assertSame(wrappedFailure, failure.getCause());
    assertEquals(1.0, meterRegistry.get("ode.ffmlib.decode.failures")
        .tag("type", "BSM").tag("source", "udp").tag("reason", "native_input")
        .counter().count());
    assertTrue(meterRegistry.find("ode.ffmlib.decode.failures").counters().stream()
        .allMatch(counter -> counter.count() == 1.0));
  }

  @Test
  void malformedLookingPlainRuntimeFromNativeIsInfrastructureFailure() {
    RuntimeException nativeFailure = new IllegalStateException("MessageFrame: Error decoding PDU");
    when(ffmlibCodec.uperToIntermediate(any())).thenThrow(nativeFailure);
    OdeMessageFrameMetadata metadata = new OdeMessageFrameMetadata();
    metadata.setSchemaVersion(9);

    FfmlibDecodeService.InfrastructureFailure failure = assertThrows(
        FfmlibDecodeService.InfrastructureFailure.class,
        () -> decodeService.prepareRaw(metadata, new byte[] {0x00, 0x14}, "unknown-native",
            SupportedMessageType.BSM));

    assertSame(nativeFailure, failure.getCause());
    assertEquals(1.0, meterRegistry.get("ode.ffmlib.decode.failures")
        .tag("type", "BSM").tag("source", "udp").tag("reason", "infrastructure")
        .counter().count());
  }

  @Test
  void missingNativeCodecIsInfrastructureFailureAndCountsOnce() {
    when(ffmlibCodecProvider.getIfAvailable()).thenReturn(null);
    OdeMessageFrameMetadata metadata = new OdeMessageFrameMetadata();
    metadata.setSchemaVersion(9);

    FfmlibDecodeService.InfrastructureFailure failure = assertThrows(
        FfmlibDecodeService.InfrastructureFailure.class,
        () -> decodeService.prepareRaw(metadata, new byte[] {0x00, 0x14}, "missing-native",
            SupportedMessageType.BSM));

    assertTrue(failure.getCause().getMessage().contains("codec is unavailable"));
    assertEquals(1.0, meterRegistry.get("ode.ffmlib.decode.failures")
        .tag("type", "BSM").tag("source", "udp").tag("reason", "infrastructure")
        .counter().count());
  }

  @Test
  void bsmJerUsesDirectReaderWithoutGenericFrameMapping() throws Exception {
    byte[] jer = "{\"messageId\":20,\"value\":{\"BasicSafetyMessage\":{}}}"
        .getBytes(StandardCharsets.UTF_8);
    when(jsonTopics.getBsm()).thenReturn(BSM_TOPIC);
    when(ffmlibCodec.uperToIntermediate(any()))
        .thenReturn(new IntermediateDecodeResult(jer, IntermediateEncoding.JER));
    when(bsmValueReader.readValue(any(JsonParser.class))).thenAnswer(invocation -> {
      JsonParser parser = invocation.getArgument(0);
      parser.skipChildren();
      return new BasicSafetyMessage();
    });
    OdeMessageFrameMetadata metadata = new OdeMessageFrameMetadata();
    metadata.setSchemaVersion(9);

    FfmlibDecodeService.PreparedDecodedMessage decoded = decodeService.prepareRaw(metadata,
        new byte[] {0x00, 0x14}, "direct-bsm", SupportedMessageType.BSM);

    assertEquals(BSM_TOPIC, decoded.topic());
    verify(bsmValueReader).readValue(any(JsonParser.class));
    verify(messageFrameReader, never()).readValue(any(byte[].class));
  }

  @Test
  void knownJerFramesMapAllTenTypesAndAllowValueBeforeMessageId() throws Exception {
    when(bsmValueReader.readValue(any(JsonParser.class))).thenAnswer(invocation -> {
      JsonParser parser = invocation.getArgument(0);
      parser.skipChildren();
      return new BasicSafetyMessage();
    });
    List<byte[]> jers = KNOWN_JER_FIXTURES.stream()
        .map(fixture -> ("{\"unrelated\":{\"items\":[1,2]},\"value\":{\""
            + fixture.choiceName() + "\":{}},\"messageId\":" + fixture.messageId()
            + ",\"ignored\":true}").getBytes(StandardCharsets.UTF_8))
        .toList();
    when(ffmlibCodec.uperToIntermediate(any())).thenAnswer(invocation -> {
      byte[] input = invocation.getArgument(0);
      int fixtureIndex = Byte.toUnsignedInt(input[0]) - 1;
      return new IntermediateDecodeResult(jers.get(fixtureIndex), IntermediateEncoding.JER);
    });

    ObjectMapper outputMapper = new ObjectMapper();
    for (int index = 0; index < KNOWN_JER_FIXTURES.size(); index++) {
      KnownJerFixture fixture = KNOWN_JER_FIXTURES.get(index);
      String expectedTopic = stubJsonTopic(fixture.type());
      byte[] uper = {(byte) (index + 1)};
      OdeMessageFrameMetadata metadata = new OdeMessageFrameMetadata();
      metadata.setSchemaVersion(9);

      FfmlibDecodeService.PreparedDecodedMessage decoded = decodeService.prepareRaw(
          metadata, uper, "direct-" + fixture.type().name(), fixture.type());

      assertEquals(fixture.type().name(), decoded.type());
      assertEquals(expectedTopic, decoded.topic());
      JsonNode frame = outputMapper.readTree(decoded.json()).path("payload");
      assertEquals(fixture.frameClass().getName(), frame.path("dataType").asText());
      assertEquals(fixture.messageId(), frame.path("data").path("messageId").asInt());
      assertTrue(frame.path("data").path("value").path(fixture.choiceName()).isObject());
    }
    verify(messageFrameReader, never()).readValue(any(byte[].class));
  }

  @Test
  void knownJerFallsBackToGenericReaderForAmbiguousOrMalformedFrames() throws Exception {
    List<String> fallbackJers = List.of(
        "{\"messageId\":31,\"value\":{\"TravelerInformation\":{}}}",
        "{\"value\":{\"BasicSafetyMessage\":{}}}",
        "{\"messageId\":20}",
        "{\"messageId\":\"20\",\"value\":{\"BasicSafetyMessage\":{}}}",
        "{\"messageId\":20.0,\"value\":{\"BasicSafetyMessage\":{}}}",
        "{\"messageId\":true,\"value\":{\"BasicSafetyMessage\":{}}}",
        "{\"messageId\":null,\"value\":{\"BasicSafetyMessage\":{}}}",
        "{\"messageId\":{},\"value\":{\"BasicSafetyMessage\":{}}}",
        "{\"messageId\":[],\"value\":{\"BasicSafetyMessage\":{}}}",
        "{\"messageId\":20,\"messageId\":20,\"value\":{\"BasicSafetyMessage\":{}}}",
        "{\"messageId\":20,\"value\":{\"BasicSafetyMessage\":{}},\"value\":{}}",
        "{\"messageId\":20,\"value\":null}",
        "{\"messageId\":20,\"value\":{\"BasicSafetyMessage\":[]}}",
        "{\"messageId\":20,\"value\":{\"BasicSafetyMessage\":{},\"Other\":{}}}",
        "{\"messageId\":20,\"value\":{\"BasicSafetyMessage\":{},\"BasicSafetyMessage\":{}}}",
        "{\"messageId\":20,\"value\":{\"Other\":{}}}",
        "{\"messageId\":20,\"value\":{\"BasicSafetyMessage\":{}}} []",
        "{\"messageId\":");
    when(ffmlibCodec.uperToIntermediate(any())).thenAnswer(invocation ->
        new IntermediateDecodeResult(invocation.getArgument(0), IntermediateEncoding.JER));
    when(jsonTopics.getBsm()).thenReturn(BSM_TOPIC);
    BasicSafetyMessageMessageFrame genericFrame = new BasicSafetyMessageMessageFrame();
    genericFrame.setValue(new BasicSafetyMessage());

    for (String jerText : fallbackJers) {
      byte[] jer = jerText.getBytes(StandardCharsets.UTF_8);
      when(messageFrameReader.readValue(jer)).thenReturn(genericFrame);
      OdeMessageFrameMetadata metadata = new OdeMessageFrameMetadata();
      metadata.setSchemaVersion(9);

      FfmlibDecodeService.PreparedDecodedMessage decoded = decodeService.prepareRaw(metadata,
          jer, "fallback-jer", SupportedMessageType.BSM);

      assertEquals(BSM_TOPIC, decoded.topic());
      verify(messageFrameReader).readValue(jer);
    }
    assertTrue(meterRegistry.find("ode.ffmlib.decode.failures").counters().isEmpty());
  }

  @Test
  void knownJerFallsBackWhenDirectBsmValueMappingThrows() throws Exception {
    byte[] jer = "{\"messageId\":20,\"value\":{\"BasicSafetyMessage\":{}}}"
        .getBytes(StandardCharsets.UTF_8);
    when(ffmlibCodec.uperToIntermediate(any()))
        .thenReturn(new IntermediateDecodeResult(jer, IntermediateEncoding.JER));
    when(jsonTopics.getBsm()).thenReturn(BSM_TOPIC);
    when(bsmValueReader.readValue(any(JsonParser.class)))
        .thenThrow(new JsonProcessingException("Cannot map BSM value") {});
    BasicSafetyMessageMessageFrame genericFrame = new BasicSafetyMessageMessageFrame();
    genericFrame.setValue(new BasicSafetyMessage());
    when(messageFrameReader.readValue(any(byte[].class))).thenReturn(genericFrame);
    OdeMessageFrameMetadata metadata = new OdeMessageFrameMetadata();
    metadata.setSchemaVersion(9);

    FfmlibDecodeService.PreparedDecodedMessage decoded = decodeService.prepareRaw(metadata,
        new byte[] {0x00, 0x14}, "direct-reader-fallback", SupportedMessageType.BSM);

    assertEquals(BSM_TOPIC, decoded.topic());
    assertEquals("BSM", decoded.type());
    ArgumentCaptor<byte[]> genericInput = ArgumentCaptor.forClass(byte[].class);
    verify(messageFrameReader).readValue(genericInput.capture());
    assertSame(jer, genericInput.getValue());
    verify(bsmValueReader).readValue(any(JsonParser.class));
    assertTrue(meterRegistry.find("ode.ffmlib.decode.failures").counters().isEmpty());
  }

  @Test
  void knownJerRecordsOneMappingFailureWhenDirectAndGenericReadersThrow() throws Exception {
    byte[] jer = "{\"messageId\":20,\"value\":{\"BasicSafetyMessage\":{}}}"
        .getBytes(StandardCharsets.UTF_8);
    when(ffmlibCodec.uperToIntermediate(any()))
        .thenReturn(new IntermediateDecodeResult(jer, IntermediateEncoding.JER));
    when(bsmValueReader.readValue(any(JsonParser.class)))
        .thenThrow(new JsonProcessingException("Cannot map BSM value") {});
    when(messageFrameReader.readValue(any(byte[].class)))
        .thenThrow(new JsonProcessingException("Cannot map generic frame") {});
    OdeMessageFrameMetadata metadata = new OdeMessageFrameMetadata();
    metadata.setSchemaVersion(9);

    assertThrows(IllegalArgumentException.class,
        () -> decodeService.prepareRaw(metadata, new byte[] {0x00, 0x14},
            "direct-and-generic-reader-failure", SupportedMessageType.BSM));

    verify(bsmValueReader).readValue(any(JsonParser.class));
    verify(messageFrameReader).readValue(jer);
    assertEquals(1.0, meterRegistry.get("ode.ffmlib.decode.failures")
        .tag("type", "BSM").tag("source", "udp").tag("reason", "mapping")
        .counter().count());
  }

  @Test
  void unknownKnownTypeCallerRetainsGenericMessageFrameMapping() throws Exception {
    byte[] jer = "{\"messageId\":20,\"value\":{\"BasicSafetyMessage\":{}}}"
        .getBytes(StandardCharsets.UTF_8);
    when(ffmlibCodec.uperToIntermediate(any()))
        .thenReturn(new IntermediateDecodeResult(jer, IntermediateEncoding.JER));
    when(jsonTopics.getBsm()).thenReturn(BSM_TOPIC);
    BasicSafetyMessageMessageFrame genericFrame = new BasicSafetyMessageMessageFrame();
    genericFrame.setValue(new BasicSafetyMessage());
    when(messageFrameReader.readValue(jer)).thenReturn(genericFrame);
    OdeMessageFrameMetadata metadata = new OdeMessageFrameMetadata();
    metadata.setSchemaVersion(9);

    FfmlibDecodeService.PreparedDecodedMessage decoded = decodeService.prepareRaw(
        metadata, new byte[] {0x00, 0x14}, "generic-frame");

    assertEquals(BSM_TOPIC, decoded.topic());
    assertEquals("BSM", decoded.type());
    verify(messageFrameReader).readValue(jer);
    verify(bsmValueReader, never()).readValue(any(JsonParser.class));
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
            SupportedMessageType.BSM));

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
  void importedSignedEnvelopeDecodesItsEmbeddedMessageFrame() throws Exception {
    stubSuccessfulDecode();
    OdeMessageFrameMetadata metadata = new OdeMessageFrameMetadata();
    metadata.setSchemaVersion(9);
    OdeAsn1Data input = new OdeAsn1Data(
        metadata, new OdeAsn1Payload(new OdeHexByteArray("038100" + BSM_HEX)));

    decodeService.decode(input, "signed-bsm");

    verify(ffmlibCodec).uperToIntermediate(eq(CodecUtils.fromHex(BSM_HEX)));
    verify(outputPublisher).publish(any());
    assertTrue(meterRegistry.find("ode.ffmlib.decode.failures").counters().isEmpty());
    assertEquals(1.0, meterRegistry.get("ode.ffmlib.decode.messages")
        .tag("type", "BSM").tag("source", "import").counter().count(), 0.0);
  }

  @Test
  void importedEnvelopeWithoutMessageFrameIsRejected() {
    OdeMessageFrameMetadata metadata = new OdeMessageFrameMetadata();
    metadata.setSchemaVersion(9);
    OdeAsn1Data input = new OdeAsn1Data(
        metadata, new OdeAsn1Payload(new OdeHexByteArray("038100")));

    assertThrows(IllegalArgumentException.class, () -> decodeService.decode(input, "no-message"));
    verify(ffmlibCodec, never()).uperToIntermediate(any());
    verify(outputPublisher, never()).publish(any());
    assertEquals(1.0, meterRegistry.get("ode.ffmlib.decode.failures")
        .tag("type", "unknown").tag("source", "import").tag("reason", "invalid_payload")
        .counter().count());
  }

  @Test
  void rawTopicSignedEnvelopeDecodesStrippedUperAndPreservesSecurityMetadata()
      throws Exception {
    stubSuccessfulDecode();
    OdeMessageFrameMetadata metadata = new OdeMessageFrameMetadata();
    metadata.setSchemaVersion(9);
    metadata.setAsn1("038100" + BSM_HEX);
    metadata.setCertPresent(true);
    metadata.setSecurityResultCode(SecurityResultCode.spduCryptoVerificationFailure);
    byte[] strippedUperBytes = CodecUtils.fromHex(BSM_HEX);

    var prepared = decodeService.prepareRaw(metadata, strippedUperBytes, "replayed-signed",
        SupportedMessageType.BSM);

    verify(ffmlibCodec).uperToIntermediate(eq(strippedUperBytes));
    var publishedMetadata = new ObjectMapper().readTree(prepared.json()).path("metadata");
    assertEquals("038100" + BSM_HEX, publishedMetadata.path("asn1").asText());
    assertTrue(publishedMetadata.path("isCertPresent").asBoolean());
    assertEquals("spduCryptoVerificationFailure",
        publishedMetadata.path("securityResultCode").asText());
    assertTrue(meterRegistry.find("ode.ffmlib.decode.failures").counters().isEmpty());

    decodeService.recordRawConfirmed(prepared);
    assertEquals(1.0, meterRegistry.get("ode.ffmlib.decode.messages")
        .tag("type", "BSM").tag("source", "udp").counter().count(), 0.0);
  }

  @ParameterizedTest
  @EnumSource(SupportedMessageType.class)
  void rawTopicSignedEnvelopeBehindWsmpHeadersDecodesItsExtractedMessageFrame(
      SupportedMessageType type) throws Exception {
    stubSuccessfulDecode(type);
    String topic = stubJsonTopic(type);
    OdeMessageFrameMetadata metadata = new OdeMessageFrameMetadata();
    metadata.setCertPresent(true);
    metadata.setSecurityResultCode(SecurityResultCode.spduCryptoVerificationFailure);
    byte[] strippedUper = CodecUtils.fromHex(type.getStartFlag() + "AA");

    var prepared = decodeService.prepareRaw(metadata, strippedUper, "signed-wsmp", type);

    verify(ffmlibCodec).uperToIntermediate(eq(strippedUper));
    assertEquals(topic, prepared.topic());
    assertTrue(metadata.isCertPresent());
    assertEquals(SecurityResultCode.spduCryptoVerificationFailure,
        metadata.getSecurityResultCode());
    assertTrue(meterRegistry.find("ode.ffmlib.decode.failures").counters().isEmpty());
  }

  @Test
  void directRawDecodeStillRejectsAnUnstrippedSignedEnvelope() {
    OdeMessageFrameMetadata metadata = new OdeMessageFrameMetadata();
    metadata.setSchemaVersion(9);
    byte[] unstripped = CodecUtils.fromHex("0381000014");

    assertThrows(UnsupportedOperationException.class,
        () -> decodeService.decode(metadata, unstripped, "unstripped-signed"));

    verify(ffmlibCodec, never()).uperToIntermediate(any());
    assertEquals(1.0, meterRegistry.get("ode.ffmlib.decode.failures")
        .tag("type", "unknown").tag("source", "udp").tag("reason", "signed_payload")
        .counter().count(), 0.0);
  }

  @ParameterizedTest
  @EnumSource(SupportedMessageType.class)
  void importedSignedEnvelopeMatchesExternalHeaderStripping(SupportedMessageType type)
      throws Exception {
    stubSuccessfulDecode();
    OdeMessageFrameMetadata metadata = new OdeMessageFrameMetadata();
    metadata.setCertPresent(true);
    metadata.setSecurityResultCode(SecurityResultCode.spduCryptoVerificationFailure);
    OdeAsn1Data input = new OdeAsn1Data(metadata,
        new OdeAsn1Payload(new OdeHexByteArray("0001038100" + type.getStartFlag() + "AA038100")));
    OdeAsn1Data external = new RawEncodedJsonService(new ObjectMapper())
        .addEncodingAndMutateBytes(JsonUtils.toJson(input, false), type,
            OdeMessageFrameMetadata.class);

    decodeService.decode(input, "signed-import-wsmp");

    byte[] expected = CodecUtils.fromHex(((OdeHexByteArray) external.getPayload().getData()).getBytes());
    verify(ffmlibCodec).uperToIntermediate(eq(expected));
    assertTrue(metadata.isCertPresent());
    assertEquals(SecurityResultCode.spduCryptoVerificationFailure, metadata.getSecurityResultCode());
    assertTrue(meterRegistry.find("ode.ffmlib.decode.failures").counters().isEmpty());
  }

  @Test
  void unsignedWsmpPayloadWithSignatureMarkerInsideMessageFrameStillDecodes() throws Exception {
    stubSuccessfulDecode();
    byte[] uper = CodecUtils.fromHex(BSM_HEX + "038100");
    OdeMessageFrameMetadata metadata = new OdeMessageFrameMetadata();

    var prepared = decodeService.prepareRaw(metadata, uper, "unsigned-wsmp",
        SupportedMessageType.BSM);

    verify(ffmlibCodec).uperToIntermediate(eq(uper));
    assertEquals(BSM_TOPIC, prepared.topic());
    assertFalse(metadata.isCertPresent());
    assertTrue(meterRegistry.find("ode.ffmlib.decode.failures").counters().isEmpty());
  }

  @Test
  @SuppressWarnings("unchecked")
  void signedUdpPacketBehindWsmpHeadersDecodesAndConfirmsBeforeAcknowledgement()
      throws Exception {
    stubSuccessfulDecode();
    byte[] original = CodecUtils.fromHex("0001038100" + BSM_HEX);
    DatagramPacket packet = new DatagramPacket(original, original.length,
        InetAddress.getLoopbackAddress(), 12345);
    ObjectMapper mapper = new ObjectMapper();
    var rawTree = (com.fasterxml.jackson.databind.node.ObjectNode)
        mapper.readTree(UdpHexDecoder.buildJsonBsmFromPacket(packet));
    var rawMetadata = (com.fasterxml.jackson.databind.node.ObjectNode) rawTree.get("metadata");
    rawMetadata.put("isCertPresent", true);
    rawMetadata.put("securityResultCode",
        SecurityResultCode.spduCryptoVerificationFailure.name());
    String rawJson = mapper.writeValueAsString(rawTree);
    RawEncodedJsonService rawService = new RawEncodedJsonService(mapper);
    var parsed = rawService.parseFfmRecord(rawJson, SupportedMessageType.BSM);
    OdeAsn1Data external = rawService.addEncodingAndMutateBytes(rawJson,
        SupportedMessageType.BSM, OdeMessageFrameMetadata.class);
    assertArrayEquals(original, parsed.originalBytes());
    assertEquals(CodecUtils.toHex(original), parsed.metadata().getAsn1());
    assertEquals(BSM_HEX, CodecUtils.toHex(parsed.uperBytes()));
    assertEquals(((OdeHexByteArray) external.getPayload().getData()).getBytes(),
        CodecUtils.toHex(parsed.uperBytes()));
    assertTrue(parsed.metadata().isCertPresent());
    assertEquals(SecurityResultCode.spduCryptoVerificationFailure,
        parsed.metadata().getSecurityResultCode());
    assertTrue(parsed.metadata().getEncodings().stream()
        .allMatch(encoding -> "MessageFrame".equals(encoding.getElementType())));

    KafkaTemplate<String, String> quarantineProducer = mock(KafkaTemplate.class);
    CompletableFuture<FfmlibOutputPublisher.PublicationOutcome> confirmation =
        new CompletableFuture<>();
    when(outputPublisher.publish(any())).thenReturn(confirmation);
    RawEncodedJsonTopics rawTopics = mock(RawEncodedJsonTopics.class);
    String rawTopic = "topic.OdeRawEncodedBSMJson";
    FfmlibRawJsonListener listener = new FfmlibRawJsonListener(rawService, decodeService,
        outputPublisher, quarantineProducer, rawTopics, mock(FfmlibCommitTracker.class),
        meterRegistry);
    listener.markStartupComplete();
    Acknowledgment acknowledgment = mock(Acknowledgment.class);
    ConsumerRecord<String, String> record =
        new ConsumerRecord<>(rawTopic, 0, 8, "signed-wsmp", rawJson);
    AtomicReference<Throwable> failure = new AtomicReference<>();
    Thread worker = new Thread(() -> {
      try {
        listener.bsm(record, acknowledgment);
      } catch (Throwable error) {
        failure.set(error);
      }
    });
    worker.start();
    try {
      ArgumentCaptor<FfmlibDecodeService.PreparedDecodedMessage> captor =
          ArgumentCaptor.forClass(FfmlibDecodeService.PreparedDecodedMessage.class);
      verify(outputPublisher, timeout(1000)).publish(captor.capture());
      var decoded = captor.getValue();
      var decodedMetadata = mapper.readTree(decoded.json()).path("metadata");
      assertEquals(CodecUtils.toHex(original), decodedMetadata.path("asn1").asText());
      assertTrue(decodedMetadata.path("isCertPresent").asBoolean());
      assertEquals("spduCryptoVerificationFailure",
          decodedMetadata.path("securityResultCode").asText());
      verify(acknowledgment, never()).acknowledge();
      verify(quarantineProducer, never()).send(any(ProducerRecord.class));
      verify(ffmlibCodec).uperToIntermediate(eq(parsed.uperBytes()));

      confirmation.complete(FfmlibOutputPublisher.PublicationOutcome.PUBLISHED);
    } finally {
      worker.join(1000);
    }
    assertFalse(worker.isAlive());
    assertEquals(null, failure.get());
    verify(acknowledgment).acknowledge();
    assertEquals(1.0, meterRegistry.get("ode.ffmlib.decode.messages")
        .tag("type", "BSM").tag("source", "udp").counter().count(), 0.0);
    assertTrue(meterRegistry.find("ode.ffmlib.decode.failures").counters().isEmpty());
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
    stubSuccessfulDecode(SupportedMessageType.BSM);
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  private void stubSuccessfulDecode(SupportedMessageType type) throws Exception {
    lenient().when(jsonTopics.getBsm()).thenReturn(BSM_TOPIC);
    lenient().when(ffmlibCodec.uperToIntermediate(any()))
        .thenReturn(new IntermediateDecodeResult("{}".getBytes(StandardCharsets.UTF_8),
            IntermediateEncoding.JER));
    MessageFrame frame = generatedFrame(type);
    lenient().when(messageFrameReader.readValue(any(byte[].class))).thenReturn(frame);
  }

  private MessageFrame<?> generatedFrame(SupportedMessageType type) {
    return switch (type) {
      case BSM -> new BasicSafetyMessageMessageFrame();
      case MAP -> new MapDataMessageFrame();
      case SPAT -> new SPATMessageFrame();
      case TIM -> new TravelerInformationMessageFrame();
      case PSM -> new PersonalSafetyMessageMessageFrame();
      case SRM -> new SignalRequestMessageMessageFrame();
      case SSM -> new SignalStatusMessageMessageFrame();
      case SDSM -> new SensorDataSharingMessageMessageFrame();
      case RTCM -> new RTCMcorrectionsMessageFrame();
      case RSM -> new RoadSafetyMessageMessageFrame();
    };
  }

  private String stubJsonTopic(SupportedMessageType type) {
    String topic = "topic." + type.name();
    switch (type) {
      case BSM -> when(jsonTopics.getBsm()).thenReturn(topic);
      case TIM -> when(jsonTopics.getTim()).thenReturn(topic);
      case SPAT -> when(jsonTopics.getSpat()).thenReturn(topic);
      case SSM -> when(jsonTopics.getSsm()).thenReturn(topic);
      case SRM -> when(jsonTopics.getSrm()).thenReturn(topic);
      case MAP -> when(jsonTopics.getMap()).thenReturn(topic);
      case PSM -> when(jsonTopics.getPsm()).thenReturn(topic);
      case SDSM -> when(jsonTopics.getSdsm()).thenReturn(topic);
      case RTCM -> when(jsonTopics.getRtcm()).thenReturn(topic);
      case RSM -> when(jsonTopics.getRsm()).thenReturn(topic);
    }
    return topic;
  }

  private record KnownJerFixture(SupportedMessageType type, int messageId, String choiceName,
      Class<?> frameClass) {
  }
}
