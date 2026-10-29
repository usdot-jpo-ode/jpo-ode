package us.dot.its.jpo.ode.codec.ffmlib;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import j2735ffm.MessageFrameCodec;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.kafka.core.KafkaTemplate;
import us.dot.its.jpo.asn.j2735.r2024.MessageFrame.MessageFrame;
import us.dot.its.jpo.ode.coder.OdeMessageFrameDataCreatorHelper;
import us.dot.its.jpo.ode.kafka.listeners.json.RawEncodedJsonService;
import us.dot.its.jpo.ode.kafka.topics.JsonTopics;
import us.dot.its.jpo.ode.model.OdeAsn1Data;
import us.dot.its.jpo.ode.model.OdeHexByteArray;
import us.dot.its.jpo.ode.model.OdeLogMetadata.RecordType;
import us.dot.its.jpo.ode.model.OdeMessageFrameMetadata.Source;
import us.dot.its.jpo.ode.model.OdeMessageFramePayload;
import us.dot.its.jpo.ode.model.OdeMsgMetadata.GeneratedBy;
import us.dot.its.jpo.ode.udp.UdpHexDecoder;
import us.dot.its.jpo.ode.udp.UdpIngestPublisher;
import us.dot.its.jpo.ode.uper.SupportedMessageType;
import us.dot.its.jpo.ode.util.CodecUtils;
import us.dot.its.jpo.ode.util.JsonUtils;

/** Exercises the real native decoder and mapper against UDP type fixtures. */
class FfmlibNativeFixturesTest {

  private static final List<Fixture> FIXTURES = List.of(
      new Fixture(SupportedMessageType.BSM, "udp/bsm/BsmReceiverTest_ValidBSM.txt",
          "topic.OdeBsmJson"),
      new Fixture(SupportedMessageType.SPAT, "udp/spat/SpatReceiverTest_ValidSPAT.txt",
          "topic.OdeSpatJson"),
      new Fixture(SupportedMessageType.MAP, "udp/map/MapReceiverTest_ValidMAP.txt",
          "topic.OdeMapJson"),
      new Fixture(SupportedMessageType.TIM, "udp/tim/TimReceiverTest_ValidTIM.txt",
          "topic.OdeTimJson"),
      new Fixture(SupportedMessageType.SRM, "udp/srm/SrmReceiverTest_ValidData.txt",
          "topic.OdeSrmJson"),
      new Fixture(SupportedMessageType.SSM, "udp/ssm/SsmReceiverTest_ValidSSM.txt",
          "topic.OdeSsmJson"),
      new Fixture(SupportedMessageType.PSM, "udp/psm/PsmReceiverTest_ValidPSM.txt",
          "topic.OdePsmJson"),
      new Fixture(SupportedMessageType.SDSM, "udp/sdsm/SdsmReceiverTest_ValidSDSM.txt",
          "topic.OdeSdsmJson"),
      new Fixture(SupportedMessageType.RTCM, "udp/rtcm/RtcmReceiverTest_ValidRTC.txt",
          "topic.OdeRtcmJson"),
      new Fixture(SupportedMessageType.RSM, "udp/rsm/RsmReceiverTest_ValidRSM.txt",
          "topic.OdeRsmJson"));

  @Test
  void decodesAndMapsUdpFixturesWithTheNativeLibrary() throws Exception {
    Path nativeLibrary;
    try {
      nativeLibrary = FfmlibNativeLibraryLoader.resolve("");
    } catch (IllegalStateException missing) {
      assumeTrue(false, "FFMLib native library is unavailable on this platform");
      return;
    }

    FfmlibProperties properties = new FfmlibProperties();
    properties.setNativeLibraryPath(nativeLibrary.toString());
    MessageFrameCodec nativeCodec = new MessageFrameCodec(properties.getTextBufferSize(),
        properties.getUperBufferSize(), properties.getErrorBufferSize(), nativeLibrary);
    FfmlibMessageFrameCodec codec = new FfmlibMessageFrameCodec(nativeCodec,
        new SimpleMeterRegistry());
    ObjectProvider<FfmlibMessageFrameCodec> codecProvider = mock(ObjectProvider.class);
    when(codecProvider.getIfAvailable()).thenReturn(codec);
    ObjectProvider<FfmlibOutputPublisher> outputProvider = mock(ObjectProvider.class);

    Asn1CodecModeProperties mode = new Asn1CodecModeProperties();
    mode.setCodecMode(Asn1CodecModeProperties.CodecMode.ffm);
    JsonTopics topics = jsonTopics();
    KafkaTemplate<String, String> kafka = mock(KafkaTemplate.class);
    ObjectMapper jerMapper = new ObjectMapper();
    FfmlibDecodeService decoder = new FfmlibDecodeService(codecProvider, properties, mode, topics,
        kafka, outputProvider, jerMapper, new SimpleMeterRegistry(), "topic.Asn1DecoderInput");
    ObjectMapper jsonMapper = new ObjectMapper();
    RawEncodedJsonService rawService = new RawEncodedJsonService(jsonMapper);

    for (Fixture fixture : FIXTURES) {
      byte[] received = hexFixture(fixture.path());
      DatagramPacket packet = new DatagramPacket(received, received.length,
          InetAddress.getLoopbackAddress(), 12345);
      UdpHexDecoder.UdpDecodeInput input = UdpHexDecoder.prepareDecodeInput(packet,
          fixture.type(), RecordType.bsmTx, Source.RSU, GeneratedBy.UNKNOWN, false);

      FfmlibDecodeService.PreparedDecodedMessage decoded = decoder.prepareRaw(input.metadata(),
          input.uperBytes(), "fixture-key", fixture.type());
      byte[] jer = codec.uperToIntermediate(input.uperBytes()).bytes();
      MessageFrame<?> genericFrame = jerMapper.readValue(jer, MessageFrame.class);
      JsonNode expectedPayload = jsonMapper.readTree(
          JsonUtils.toJson(new OdeMessageFramePayload(genericFrame), false));
      JsonNode actualPayload = jsonMapper.readTree(decoded.json()).path("payload");
      assertEquals(expectedPayload, actualPayload,
          fixture.path() + " direct JER mapping must preserve the generic JSON model");
      if (fixture.type() == SupportedMessageType.BSM) {
        decodesSyntheticSignedBsmThroughTheRawParser(decoder, received, jsonMapper);
      }

      assertEquals(fixture.type().name(), decoded.type(), fixture.path());
      assertEquals(fixture.outputTopic(), decoded.topic(), fixture.path());
      JsonNode json = jsonMapper.readTree(decoded.json());
      assertNotNull(json.get("metadata"), fixture.path());
      assertNotNull(json.get("payload"), fixture.path());
      assertTrue(json.get("payload").size() > 0, fixture.path());

      String rawJson = rawJsonForFixture(jsonMapper, received);
      RawEncodedJsonService.FfmRawRecord legacyInput =
          rawService.parseFfmRecord(rawJson, fixture.type());
      RawEncodedJsonService.FfmDecodeInput leanInput =
          rawService.parseFfmDecodeInput(rawJson, fixture.type());
      assertNotSame(legacyInput.metadata(), leanInput.metadata(), fixture.path());

      FfmlibDecodeService.PreparedDecodedMessage legacyDecoded = decoder.prepareRaw(
          legacyInput.metadata(), legacyInput.uperBytes(), "fixture-equivalence", null);
      FfmlibDecodeService.PreparedDecodedMessage leanDecoded = decoder.prepareRaw(
          leanInput.metadata(), leanInput.uperBytes(), "fixture-equivalence", fixture.type());

      assertEquals(legacyDecoded.topic(), leanDecoded.topic(), fixture.path());
      assertEquals(legacyDecoded.type(), leanDecoded.type(), fixture.path());
      JsonNode legacyJson = jsonMapper.readTree(legacyDecoded.json());
      JsonNode leanJson = jsonMapper.readTree(leanDecoded.json());
      assertEquals(legacyJson, leanJson, fixture.path());
      assertEquals(legacyJson.path("metadata"), leanJson.path("metadata"), fixture.path());
      assertEquals(legacyJson.path("payload"), leanJson.path("payload"), fixture.path());
    }
  }

  @Test
  @SuppressWarnings("unchecked")
  void directRouteDecodesTheSameJsonAsTheRawTopicRoundTrip() throws Exception {
    FfmlibMessageFrameCodec codec = nativeCodec();
    FfmlibDecodeService decoder = ffmDecoder(codec);
    RawEncodedJsonService rawService = new RawEncodedJsonService(new ObjectMapper());
    ObjectMapper jsonMapper = new ObjectMapper();
    List<Fixture> fixtures = new ArrayList<>(FIXTURES);
    fixtures.add(new Fixture(SupportedMessageType.BSM, "signed-wsmp-bsm", "topic.OdeBsmJson"));

    for (Fixture fixture : fixtures) {
      byte[] received = fixture.path().equals("signed-wsmp-bsm")
          ? signedBsm(hexFixture("udp/bsm/BsmReceiverTest_ValidBSM.txt"))
          : hexFixture(fixture.path());
      DatagramPacket packet = new DatagramPacket(received, received.length,
          InetAddress.getLoopbackAddress(), 12345);
      String rawTopic = "topic.OdeRawEncoded" + fixture.type().name() + "Json";
      KafkaTemplate<String, String> rawKafka = mock(KafkaTemplate.class);
      when(rawKafka.send(anyString(), anyString())).thenReturn(new CompletableFuture<>());
      FfmlibDirectUdpDecoder direct = mock(FfmlibDirectUdpDecoder.class);
      AtomicReference<String> directJson = new AtomicReference<>();
      doAnswer(invocation -> {
        UdpHexDecoder.UdpDecodeInput input = invocation.getArgument(0);
        directJson.set(decoder.prepareRaw(input.metadata(), input.uperBytes(), null,
            fixture.type()).json());
        return null;
      }).when(direct).decodeAndPublish(any(), eq(fixture.type()), eq(rawTopic), any());

      UdpIngestPublisher.direct(rawKafka, direct, true).publish(packet, fixture.type(), rawTopic);

      ArgumentCaptor<String> rawJson = ArgumentCaptor.forClass(String.class);
      verify(rawKafka).send(eq(rawTopic), rawJson.capture());
      RawEncodedJsonService.FfmDecodeInput rawInput =
          rawService.parseFfmDecodeInput(rawJson.getValue(), fixture.type());
      String rawRouteJson = decoder.prepareRaw(rawInput.metadata(), rawInput.uperBytes(), null,
          fixture.type()).json();
      assertNotNull(directJson.get(), fixture.path());
      assertEquals(jsonMapper.readTree(rawRouteJson), jsonMapper.readTree(directJson.get()),
          fixture.path() + " direct routing must publish the raw-topic route's JSON");
    }
  }

  @SuppressWarnings("unchecked")
  private static FfmlibDecodeService ffmDecoder(FfmlibMessageFrameCodec codec) {
    ObjectProvider<FfmlibMessageFrameCodec> codecProvider = mock(ObjectProvider.class);
    when(codecProvider.getIfAvailable()).thenReturn(codec);
    Asn1CodecModeProperties mode = new Asn1CodecModeProperties();
    mode.setCodecMode(Asn1CodecModeProperties.CodecMode.ffm);
    return new FfmlibDecodeService(codecProvider, new FfmlibProperties(), mode, jsonTopics(),
        mock(KafkaTemplate.class), mock(ObjectProvider.class), new ObjectMapper(),
        new SimpleMeterRegistry(), "topic.Asn1DecoderInput");
  }

  private static byte[] signedBsm(byte[] validBsmUper) {
    // Synthetic IEEE 1609.2 wrapper for header stripping only; it carries no real signature.
    byte[] wrapper = org.apache.tomcat.util.buf.HexUtils.fromHexString("0001038100");
    byte[] received = new byte[wrapper.length + validBsmUper.length];
    System.arraycopy(wrapper, 0, received, 0, wrapper.length);
    System.arraycopy(validBsmUper, 0, received, wrapper.length, validBsmUper.length);
    return received;
  }

  @Test
  void readMessageFrameSelectsTopLevelFrameOverMetadataDecoy() throws Exception {
    FfmlibMessageFrameCodec codec = nativeCodec();
    String odeXml = odeXmlWithMetadataDecoy(
        codec.uperToXer(hexFixture("udp/rsm/RsmReceiverTest_ValidRSM.txt")));

    MessageFrame<?> frame = OdeMessageFrameDataCreatorHelper.readMessageFrame(odeXml,
        new XmlMapper());

    assertInstanceOf(us.dot.its.jpo.asn.j2735.r2024.RoadSafetyMessage
        .RoadSafetyMessageMessageFrame.class, frame,
        "a metadata payload/data/MessageFrame decoy must not replace the top-level frame");
  }

  @Test
  @Disabled("Requires the jpo-asn-pojos ordered XER generic binding fix (follow-up)")
  void reencodesNativeRsmFixtureWithOriginalUperBytes() throws Exception {
    FfmlibMessageFrameCodec codec = nativeCodec();
    byte[] originalUper = hexFixture("udp/rsm/RsmReceiverTest_ValidRSM.txt");
    String odeXml = odeXmlWithMetadataDecoy(codec.uperToXer(originalUper));
    XmlMapper xmlMapper = new XmlMapper();

    MessageFrame<?> frame = OdeMessageFrameDataCreatorHelper.readMessageFrame(odeXml, xmlMapper);
    String canonicalXer = xmlMapper.writeValueAsString(frame);
    assertArrayEquals(originalUper, codec.xerToUper(canonicalXer),
        "generic wrapper binding and XER serialization must preserve RSM choice order");

    ObjectProvider<FfmlibMessageFrameCodec> codecProvider = mock(ObjectProvider.class);
    when(codecProvider.getIfAvailable()).thenReturn(codec);
    FfmlibEncodeService encoder = new FfmlibEncodeService(codecProvider, xmlMapper);
    String encodedXml = encoder.encodeOdeAsn1Xml(odeXml);
    JsonNode encodedBytes = xmlMapper.readTree(encodedXml).path("payload").path("data")
        .path("MessageFrame").path("bytes");
    assertTrue(encodedBytes.isTextual(), "FFMLib encode branch must return MessageFrame bytes");
    assertArrayEquals(originalUper, CodecUtils.fromHex(encodedBytes.asText()),
        "FFMLib XML encode must preserve the original RSM UPER bytes");
  }

  private static FfmlibMessageFrameCodec nativeCodec() {
    Path nativeLibrary = FfmlibNativeTestSupport.requireLibraryOrSkip();
    FfmlibProperties properties = new FfmlibProperties();
    MessageFrameCodec nativeCodec = new MessageFrameCodec(properties.getTextBufferSize(),
        properties.getUperBufferSize(), properties.getErrorBufferSize(), nativeLibrary);
    return new FfmlibMessageFrameCodec(nativeCodec, new SimpleMeterRegistry());
  }

  private static String odeXmlWithMetadataDecoy(String nativeXer) {
    String decoyFrame = "<MessageFrame><messageId>19</messageId><value><SPAT/>"
        + "</value></MessageFrame>";
    return "<OdeAsn1Data><metadata><payload><data>" + decoyFrame
        + "</data></payload><payloadType>us.dot.its.jpo.ode.model.OdeAsn1Payload</payloadType>"
        + "</metadata><payload><dataType>MessageFrame</dataType><data>" + nativeXer
        + "</data></payload></OdeAsn1Data>";
  }

  private static String rawJsonForFixture(ObjectMapper mapper, byte[] received)
      throws Exception {
    String hex = CodecUtils.toHex(received);
    ObjectNode raw = mapper.createObjectNode();
    ObjectNode metadata = raw.putObject("metadata");
    metadata.put("asn1", hex);
    metadata.put("odeReceivedAt", "2026-10-09T00:00:00.000Z");
    metadata.put("schemaVersion", 9);
    ObjectNode serialId = metadata.putObject("serialId");
    serialId.put("streamId", "native-fixture-equivalence");
    serialId.put("bundleSize", 1);
    serialId.put("bundleId", 0);
    serialId.put("recordId", 0);
    serialId.put("serialNumber", 0);
    raw.putObject("payload").putObject("data")
        .put("encoding", "UPER")
        .put("bytes", hex);
    return mapper.writeValueAsString(raw);
  }

  private static byte[] hexFixture(String relativePath) throws Exception {
    Path fixture = Path.of("src/test/resources/us/dot/its/jpo/ode", relativePath);
    String hex = Files.readString(fixture).trim();
    return org.apache.tomcat.util.buf.HexUtils.fromHexString(hex);
  }

  private static void decodesSyntheticSignedBsmThroughTheRawParser(
      FfmlibDecodeService decoder, byte[] validBsmUper, ObjectMapper jsonMapper) throws Exception {
    // This synthetic wrapper exercises header stripping only; it carries no real signature.
    byte[] wrapper = org.apache.tomcat.util.buf.HexUtils.fromHexString("0001038100");
    byte[] received = new byte[wrapper.length + validBsmUper.length];
    System.arraycopy(wrapper, 0, received, 0, wrapper.length);
    System.arraycopy(validBsmUper, 0, received, wrapper.length, validBsmUper.length);
    DatagramPacket packet = new DatagramPacket(received, received.length,
        InetAddress.getLoopbackAddress(), 12345);
    String rawJson = UdpHexDecoder.buildJsonBsmFromPacket(packet);
    RawEncodedJsonService rawService = new RawEncodedJsonService(new ObjectMapper());
    var parsed = rawService.parseFfmRecord(rawJson, SupportedMessageType.BSM);
    OdeAsn1Data external = rawService.addEncodingAndMutateBytes(rawJson,
        SupportedMessageType.BSM, us.dot.its.jpo.ode.model.OdeMessageFrameMetadata.class);
    byte[] externalUper = org.apache.tomcat.util.buf.HexUtils.fromHexString(
        ((OdeHexByteArray) external.getPayload().getData()).getBytes());

    assertArrayEquals(received, parsed.originalBytes());
    assertArrayEquals(validBsmUper, externalUper);
    assertArrayEquals(externalUper, parsed.uperBytes());

    FfmlibDecodeService.PreparedDecodedMessage decoded = decoder.prepareRaw(parsed.metadata(),
        parsed.uperBytes(), "synthetic-signed-bsm", SupportedMessageType.BSM);
    assertEquals("BSM", decoded.type());
    assertEquals("topic.OdeBsmJson", decoded.topic());
    JsonNode output = jsonMapper.readTree(decoded.json());
    assertNotNull(output.get("payload"));
    assertTrue(output.path("payload").size() > 0);
  }

  private static JsonTopics jsonTopics() {
    JsonTopics topics = new JsonTopics();
    topics.setBsm("topic.OdeBsmJson");
    topics.setSpat("topic.OdeSpatJson");
    topics.setMap("topic.OdeMapJson");
    topics.setTim("topic.OdeTimJson");
    topics.setSrm("topic.OdeSrmJson");
    topics.setSsm("topic.OdeSsmJson");
    topics.setPsm("topic.OdePsmJson");
    topics.setSdsm("topic.OdeSdsmJson");
    topics.setRtcm("topic.OdeRtcmJson");
    topics.setRsm("topic.OdeRsmJson");
    return topics;
  }

  private record Fixture(SupportedMessageType type, String path, String outputTopic) {
  }
}
