package us.dot.its.jpo.ode.kafka.listeners.json;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import org.apache.tomcat.util.buf.HexUtils;
import org.junit.jupiter.api.Test;
import us.dot.its.jpo.ode.model.OdeAsn1Data;
import us.dot.its.jpo.ode.model.OdeHexByteArray;
import us.dot.its.jpo.ode.model.OdeMessageFrameMetadata;
import us.dot.its.jpo.ode.uper.SupportedMessageType;
import us.dot.its.jpo.ode.util.CodecUtils;

class RawEncodedJsonServiceFfmTest {

  @Test
  void parsesRawJsonOnceAndMatchesTheEstablishedRawRouterPayload() throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    RawEncodedJsonService service = new RawEncodedJsonService(mapper);
    byte[] packet = fixtureBytes();
    String json = rawJson(mapper, packet, Map.of());

    var parsed = service.parseFfmRecord(json, SupportedMessageType.BSM);
    OdeAsn1Data legacy = service.addEncodingAndMutateBytes(json, SupportedMessageType.BSM,
        OdeMessageFrameMetadata.class);
    String legacyHex = ((OdeHexByteArray) legacy.getPayload().getData()).getBytes();

    assertEquals(legacyHex, CodecUtils.toHex(parsed.uperBytes()));
    assertArrayEquals(packet, parsed.originalBytes());
    assertEquals(legacy.getMetadata().getEncodings().size(),
        parsed.metadata().getEncodings().size());
  }

  @Test
  void prefersMetadataAsn1BytesForSignedPayloadDetection() throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    RawEncodedJsonService service = new RawEncodedJsonService(mapper);
    byte[] packet = fixtureBytes();
    byte[] signedOriginal = HexUtils.fromHexString("038100" + CodecUtils.toHex(packet));
    String json = rawJson(mapper, packet,
        Map.of("asn1", CodecUtils.toHex(signedOriginal)));

    var parsed = service.parseFfmRecord(json, SupportedMessageType.BSM);

    assertArrayEquals(signedOriginal, parsed.originalBytes());
    assertArrayEquals(packet, parsed.uperBytes());
  }

  private static byte[] fixtureBytes() throws Exception {
    Path fixture = Path.of("src/test/resources/us/dot/its/jpo/ode/udp/bsm/"
        + "BsmReceiverTest_ValidBSM.txt");
    return HexUtils.fromHexString(Files.readString(fixture).trim());
  }

  private static String rawJson(ObjectMapper mapper, byte[] packet, Map<String, String> metadata)
      throws Exception {
    return mapper.writeValueAsString(Map.of(
        "metadata", metadata,
        "payload", Map.of("data", Map.of("bytes", CodecUtils.toHex(packet)))));
  }
}
