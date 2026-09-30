package us.dot.its.jpo.ode.codec.ffmlib;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import j2735ffm.MessageFrameCodec;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.kafka.core.KafkaTemplate;
import us.dot.its.jpo.asn.j2735.r2024.MessageFrame.MessageFrame;
import us.dot.its.jpo.ode.kafka.topics.JsonTopics;
import us.dot.its.jpo.ode.model.OdeLogMetadata.RecordType;
import us.dot.its.jpo.ode.model.OdeMessageFrameMetadata.Source;
import us.dot.its.jpo.ode.model.OdeMessageFramePayload;
import us.dot.its.jpo.ode.model.OdeMsgMetadata.GeneratedBy;
import us.dot.its.jpo.ode.udp.UdpHexDecoder;
import us.dot.its.jpo.ode.uper.SupportedMessageType;
import us.dot.its.jpo.ode.util.JsonUtils;

/** Exercises the real native decoder and mapper against every unsigned UDP type fixture. */
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
  void decodesAndMapsAllUnsignedUdpFixturesWithTheNativeLibrary() throws Exception {
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

    for (Fixture fixture : FIXTURES) {
      byte[] received = hexFixture(fixture.path());
      DatagramPacket packet = new DatagramPacket(received, received.length,
          InetAddress.getLoopbackAddress(), 12345);
      UdpHexDecoder.UdpDecodeInput input = UdpHexDecoder.prepareDecodeInput(packet,
          fixture.type(), RecordType.bsmTx, Source.RSU, GeneratedBy.UNKNOWN, false);

      FfmlibDecodeService.PreparedDecodedMessage decoded = decoder.prepareRaw(input.metadata(),
          input.uperBytes(), "fixture-key", fixture.type(), received);
      if (fixture.type() == SupportedMessageType.BSM) {
        byte[] jer = codec.uperToIntermediate(input.uperBytes()).bytes();
        MessageFrame<?> genericFrame = jerMapper.readValue(jer, MessageFrame.class);
        JsonNode expectedPayload = jsonMapper.readTree(
            JsonUtils.toJson(new OdeMessageFramePayload(genericFrame), false));
        JsonNode actualPayload = jsonMapper.readTree(decoded.json()).path("payload");
        assertEquals(expectedPayload, actualPayload,
            fixture.path() + " direct BSM JER mapping must preserve the generic JSON model");
      }

      assertEquals(fixture.type().name(), decoded.type(), fixture.path());
      assertEquals(fixture.outputTopic(), decoded.topic(), fixture.path());
      JsonNode json = jsonMapper.readTree(decoded.json());
      assertNotNull(json.get("metadata"), fixture.path());
      assertNotNull(json.get("payload"), fixture.path());
      assertTrue(json.get("payload").size() > 0, fixture.path());
    }
  }

  private static byte[] hexFixture(String relativePath) throws Exception {
    Path fixture = Path.of("src/test/resources/us/dot/its/jpo/ode", relativePath);
    String hex = Files.readString(fixture).trim();
    return org.apache.tomcat.util.buf.HexUtils.fromHexString(hex);
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
