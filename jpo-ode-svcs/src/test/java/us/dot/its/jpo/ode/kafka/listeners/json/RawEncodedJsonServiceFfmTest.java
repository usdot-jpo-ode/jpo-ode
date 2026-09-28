package us.dot.its.jpo.ode.kafka.listeners.json;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

  @Test
  void parsesPayloadBeforeMetadataAndSkipsUnrelatedFields() throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    RawEncodedJsonService service = new RawEncodedJsonService(mapper);
    byte[] packet = fixtureBytes();
    String json = "{\"payload\":{\"data\":{\"encoding\":\"UPER\",\"bytes\":\""
        + CodecUtils.toHex(packet)
        + "\"}},\"unrelated\":{\"values\":[1,2,3]},\"metadata\":{}}";

    var parsed = service.parseFfmRecord(json, SupportedMessageType.BSM);

    assertArrayEquals(packet, parsed.originalBytes());
    assertArrayEquals(packet, parsed.uperBytes());
  }

  @Test
  void duplicateFieldsKeepFinalMetadataAndPayloadValues() throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    RawEncodedJsonService service = new RawEncodedJsonService(mapper);
    byte[] packet = fixtureBytes();
    String hex = CodecUtils.toHex(packet);
    String json = "{\"metadata\":{\"schemaVersion\":4},"
        + "\"payload\":{\"data\":{\"bytes\":\"00\"}},"
        + "\"metadata\":{\"schemaVersion\":9},"
        + "\"payload\":{\"data\":{\"bytes\":\"" + hex + "\"}}}";

    var parsed = service.parseFfmRecord(json, SupportedMessageType.BSM);

    assertEquals(9, parsed.metadata().getSchemaVersion());
    assertArrayEquals(packet, parsed.uperBytes());
  }

  @Test
  void laterMistypedOrMissingValuesReplaceEarlierPayloadBytes() throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    RawEncodedJsonService service = new RawEncodedJsonService(mapper);
    String hex = CodecUtils.toHex(fixtureBytes());
    String[] invalidPayloads = {
        "{\"data\":{\"bytes\":\"" + hex + "\"},\"data\":false}",
        "{\"data\":{\"bytes\":\"" + hex + "\"},\"data\":{\"bytes\":17}}",
        "{\"data\":{\"bytes\":\"" + hex + "\"},\"data\":{}}",
        "false"
    };

    for (String payload : invalidPayloads) {
      String json = "{\"metadata\":{},\"payload\":{\"data\":{\"bytes\":\""
          + hex + "\"}},\"payload\":" + payload + "}";
      assertThrows(IllegalArgumentException.class,
          () -> service.parseFfmRecord(json, SupportedMessageType.BSM));
    }
  }

  @Test
  void invalidFinalMetadataMissingFieldsAndTruncatedInputAreRejected() throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    RawEncodedJsonService service = new RawEncodedJsonService(mapper);
    String valid = rawJson(mapper, fixtureBytes(), Map.of());
    String hex = CodecUtils.toHex(fixtureBytes());

    assertThrows(IllegalArgumentException.class, () -> service.parseFfmRecord(
        "{\"metadata\":{},\"metadata\":null,\"payload\":{\"data\":{\"bytes\":\""
            + hex + "\"}}}", SupportedMessageType.BSM));
    assertThrows(IllegalArgumentException.class, () -> service.parseFfmRecord(
        "{\"payload\":{\"data\":{\"bytes\":\"" + hex + "\"}}}",
        SupportedMessageType.BSM));
    assertThrows(IllegalArgumentException.class, () -> service.parseFfmRecord(
        "{\"metadata\":{}}", SupportedMessageType.BSM));
    assertThrows(Exception.class,
        () -> service.parseFfmRecord(valid.substring(0, valid.length() - 1),
            SupportedMessageType.BSM));
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
