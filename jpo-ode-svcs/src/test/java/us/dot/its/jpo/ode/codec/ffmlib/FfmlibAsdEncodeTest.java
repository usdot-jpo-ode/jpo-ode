package us.dot.its.jpo.ode.codec.ffmlib;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import j2735ffm.AsnEncoding;
import j2735ffm.MessageFrameCodec;
import java.nio.file.Files;
import java.nio.file.Path;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;
import us.dot.its.jpo.ode.util.CodecUtils;
import us.dot.its.jpo.ode.util.XmlUtils;

class FfmlibAsdEncodeTest {

  private static final Path ASD_FIXTURE = Path.of("..", "asn1_codec", "unit-test-data", "ASD.xml");

  @Test
  void beta2EncodesAndDecodesTimAsd() throws Exception {
    Path library = FfmlibNativeTestSupport.requireLibraryOrSkip();
    FfmlibProperties properties = new FfmlibProperties();
    MessageFrameCodec nativeCodec = new MessageFrameCodec(properties.getTextBufferSize(),
        properties.getUperBufferSize(), properties.getErrorBufferSize(), library);
    FfmlibMessageFrameCodec codec = new FfmlibMessageFrameCodec(nativeCodec,
        new SimpleMeterRegistry());
    ObjectProvider<FfmlibMessageFrameCodec> provider = mock(ObjectProvider.class);
    when(provider.getIfAvailable()).thenReturn(codec);
    FfmlibEncodeService encoder = new FfmlibEncodeService(provider, new XmlMapper());

    String input = Files.readString(ASD_FIXTURE);
    String output = encoder.encodeOdeAsn1Xml(input);
    JSONObject encoded = XmlUtils.toJSONObject(output).getJSONObject("OdeAsn1Data");
    assertEquals("OdeAsdPayload",
        encoded.getJSONObject("metadata").getString("payloadType").replace(
            "us.dot.its.jpo.ode.model.", ""));
    byte[] uper = CodecUtils.fromHex(encoded.getJSONObject("payload").getJSONObject("data")
        .getJSONObject("AdvisorySituationData").getString("bytes"));
    String decoded = codec.decodeToXer(uper, "AdvisorySituationData", AsnEncoding.UPER);

    assertTrue(decoded.contains("<AdvisorySituationData>"));
    assertTrue(decoded.contains("<groupID>00000000</groupID>"));
    assertTrue(decoded.contains("<requestID>08478278</requestID>"));
    assertTrue(decoded.contains("<advisoryMessage>001480AD"));
    assertArrayEquals(uper, codec.encodeFromXer(decoded, "AdvisorySituationData", AsnEncoding.UPER));
  }

  @Test
  void invalidAsdDoesNotProduceEncodedOutput() throws Exception {
    Path library = FfmlibNativeTestSupport.requireLibraryOrSkip();
    FfmlibProperties properties = new FfmlibProperties();
    MessageFrameCodec nativeCodec = new MessageFrameCodec(properties.getTextBufferSize(),
        properties.getUperBufferSize(), properties.getErrorBufferSize(), library);
    ObjectProvider<FfmlibMessageFrameCodec> provider = mock(ObjectProvider.class);
    FfmlibMessageFrameCodec codec = new FfmlibMessageFrameCodec(nativeCodec,
        new SimpleMeterRegistry());
    when(provider.getIfAvailable()).thenReturn(codec);
    String input = Files.readString(ASD_FIXTURE).replace("<requestID>08478278</requestID>",
        "<requestID>invalid</requestID>");

    FfmlibEncodeService encoder = new FfmlibEncodeService(provider, new XmlMapper());
    assertThrows(RuntimeException.class, () -> encoder.encodeOdeAsn1Xml(input));
  }
}
