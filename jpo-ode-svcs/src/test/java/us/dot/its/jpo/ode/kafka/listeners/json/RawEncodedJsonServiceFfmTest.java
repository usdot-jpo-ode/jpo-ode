package us.dot.its.jpo.ode.kafka.listeners.json;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
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

  @Test
  void leanParserMatchesLegacyStrippedBytesAndMetadataWithoutAddingEncoding() throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    RawEncodedJsonService service = new RawEncodedJsonService(mapper);
    byte[] packet = fixtureBytes();
    String json = rawJson(mapper, packet, Map.of(
        "asn1", CodecUtils.toHex(packet),
        "schemaVersion", "9",
        "serialId", Map.of("streamId", "fixed-stream-id", "bundleSize", 4, "bundleId", 5,
            "recordId", 3, "serialNumber", 23),
        "odeReceivedAt", "2025-01-02T03:04:05.000Z"));

    var lean = service.parseFfmDecodeInput(json, SupportedMessageType.BSM);
    var legacy = service.parseFfmRecord(json, SupportedMessageType.BSM);
    OdeAsn1Data external = service.addEncodingAndMutateBytes(json, SupportedMessageType.BSM,
        OdeMessageFrameMetadata.class);
    byte[] externalBytes = HexUtils.fromHexString(
        ((OdeHexByteArray) external.getPayload().getData()).getBytes());
    ObjectNode leanMetadata = mapper.valueToTree(lean.metadata());
    ObjectNode legacyMetadata = mapper.valueToTree(legacy.metadata());
    leanMetadata.remove("encodings");
    legacyMetadata.remove("encodings");

    assertArrayEquals(packet, lean.uperBytes());
    assertArrayEquals(legacy.uperBytes(), lean.uperBytes());
    assertArrayEquals(externalBytes, lean.uperBytes());
    assertEquals(legacyMetadata, leanMetadata);
    assertEquals(9, lean.metadata().getSchemaVersion());
    assertEquals(CodecUtils.toHex(packet), lean.metadata().getAsn1());
    assertNull(lean.metadata().getEncodings());
  }

  @Test
  void leanParserAcceptsPayloadBeforeMetadataAndSkipsUnknownNestedValues() throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    RawEncodedJsonService service = new RawEncodedJsonService(mapper);
    byte[] packet = fixtureBytes();
    String hex = CodecUtils.toHex(packet);
    String json = "{\"payload\":{\"unrelated\":[1,{\"value\":false}],"
        + "\"data\":{\"encoding\":\"UPER\",\"bytes\":\"" + hex + "\"}},"
        + "\"unknown\":{\"deep\":[1,2,3]},\"metadata\":{\"asn1\":\"" + hex
        + "\"}}";

    var parsed = service.parseFfmDecodeInput(json, SupportedMessageType.BSM);

    assertArrayEquals(packet, parsed.uperBytes());
    assertEquals(hex, parsed.metadata().getAsn1());
    assertNull(parsed.metadata().getEncodings());
  }

  @Test
  void leanParserPreservesSignedWrapperAndIncomingSecurityMetadata() throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    RawEncodedJsonService service = new RawEncodedJsonService(mapper);
    byte[] packet = fixtureBytes();
    String signedHex = "038100" + CodecUtils.toHex(packet);
    String json = "{\"payload\":{\"data\":{\"bytes\":\"" + signedHex
        + "\"}},\"metadata\":{\"asn1\":\"" + signedHex
        + "\",\"isCertPresent\":true,\"securityResultCode\":"
        + "\"spduCryptoVerificationFailure\",\"encodings\":{\"encodings\":{"
        + "\"elementName\":\"incoming\",\"elementType\":\"MessageFrame\","
        + "\"encodingRule\":\"COER\"}}}}";

    var parsed = service.parseFfmDecodeInput(json, SupportedMessageType.BSM);

    assertArrayEquals(packet, parsed.uperBytes());
    assertEquals(signedHex, parsed.metadata().getAsn1());
    assertTrue(parsed.metadata().isCertPresent());
    assertEquals(us.dot.its.jpo.ode.model.OdeLogMetadata.SecurityResultCode
        .spduCryptoVerificationFailure, parsed.metadata().getSecurityResultCode());
    assertEquals(1, parsed.metadata().getEncodings().size());
    assertEquals("incoming", parsed.metadata().getEncodings().get(0).getElementName());
    assertEquals(us.dot.its.jpo.ode.model.Asn1Encoding.EncodingRule.COER,
        parsed.metadata().getEncodings().get(0).getEncodingRule());
  }

  @Test
  void leanParserUsesLastPayloadValuesIncludingNestedWrongTypes() throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    RawEncodedJsonService service = new RawEncodedJsonService(mapper);
    String hex = CodecUtils.toHex(fixtureBytes());
    String[] invalidPayloads = {
        "{\"data\":{\"bytes\":\"" + hex + "\"},\"data\":false}",
        "{\"data\":{\"bytes\":\"" + hex + "\"},\"data\":{\"bytes\":17}}",
        "{\"data\":{\"bytes\":\"" + hex + "\"},\"data\":{}}",
        "false",
        "null",
        "{}",
        "{\"data\":{\"bytes\":\"" + hex + "\"},\"data\":{\"bytes\":\""
            + hex + "\"},\"data\":{\"bytes\":false}}"
    };

    for (String payload : invalidPayloads) {
      String json = "{\"metadata\":{},\"payload\":{\"data\":{\"bytes\":\""
          + hex + "\"}},\"payload\":" + payload + "}";
      assertThrows(IllegalArgumentException.class,
          () -> service.parseFfmDecodeInput(json, SupportedMessageType.BSM));
    }

    String finalValid = "{\"metadata\":{},\"payload\":{\"data\":{\"bytes\":\"00\"}},"
        + "\"payload\":{\"unknown\":[1,{\"ignored\":true}],\"data\":{\"bytes\":\""
        + hex + "\"}}}";
    assertArrayEquals(fixtureBytes(), service.parseFfmDecodeInput(finalValid,
        SupportedMessageType.BSM).uperBytes());
  }

  @Test
  void leanParserFallsBackForRepeatedMetadataAndKeepsFinalValidObject() throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    RawEncodedJsonService service = new RawEncodedJsonService(mapper);
    String hex = CodecUtils.toHex(fixtureBytes());
    String json = "{\"metadata\":{\"securityResultCode\":\"not-a-code\"},"
        + "\"payload\":{\"data\":{\"bytes\":\"" + hex + "\"}},"
        + "\"metadata\":{\"schemaVersion\":11,\"asn1\":\"" + hex + "\"}}";

    var parsed = service.parseFfmDecodeInput(json, SupportedMessageType.BSM);

    assertEquals(11, parsed.metadata().getSchemaVersion());
    assertEquals(hex, parsed.metadata().getAsn1());
    assertArrayEquals(fixtureBytes(), parsed.uperBytes());
  }

  @Test
  void leanParserRejectsMissingNullAndNonObjectMetadataAndTruncatedInput() throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    RawEncodedJsonService service = new RawEncodedJsonService(mapper);
    String hex = CodecUtils.toHex(fixtureBytes());
    String payload = "\"payload\":{\"data\":{\"bytes\":\"" + hex + "\"}}";

    for (String json : new String[] {"{" + payload + "}",
        "{\"metadata\":null," + payload + "}", "{\"metadata\":[]," + payload + "}",
        "{\"metadata\":\"scalar\"," + payload + "}"}) {
      assertThrows(IllegalArgumentException.class,
          () -> service.parseFfmDecodeInput(json, SupportedMessageType.BSM));
    }
    assertThrows(IllegalArgumentException.class, () -> service.parseFfmDecodeInput(
        "{\"metadata\":{},\"metadata\":null," + payload + "}",
        SupportedMessageType.BSM));
    assertThrows(Exception.class, () -> service.parseFfmDecodeInput(
        "{\"metadata\":{},\"payload\":{\"data\":{\"bytes\":\"" + hex,
        SupportedMessageType.BSM));
  }

  @Test
  void metadataArrayIsRejectedEvenWhenMapperUnwrapsSingleValueArrays() throws Exception {
    ObjectMapper mapper = new ObjectMapper()
        .enable(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS);
    RawEncodedJsonService service = new RawEncodedJsonService(mapper);
    String payload = "\"payload\":{\"data\":{\"bytes\":\""
        + CodecUtils.toHex(fixtureBytes()) + "\"}}";

    for (String metadata : new String[] {"[{}]", "\"scalar\"", "null"}) {
      String json = "{\"metadata\":" + metadata + "," + payload + "}";
      assertThrows(IllegalArgumentException.class,
          () -> service.parseFfmDecodeInput(json, SupportedMessageType.BSM));
      assertThrows(IllegalArgumentException.class,
          () -> service.parseFfmRecord(json, SupportedMessageType.BSM));
    }
  }

  @Test
  void leanParserValidatesMetadataAsn1HexWithoutDecodingIt() throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    RawEncodedJsonService service = new RawEncodedJsonService(mapper);
    String payload = CodecUtils.toHex(fixtureBytes());

    var valid = service.parseFfmDecodeInput("{\"metadata\":{\"asn1\":\"aBcD\"},"
        + "\"payload\":{\"data\":{\"bytes\":\"" + payload + "\"}}}",
        SupportedMessageType.BSM);
    assertEquals("aBcD", valid.metadata().getAsn1());
    for (String asn1 : new String[] {"aBc", "gg", "é0"}) {
      String json = "{\"metadata\":{\"asn1\":\"" + asn1
          + "\"},\"payload\":{\"data\":{\"bytes\":\"" + payload + "\"}}}";
      assertThrows(IllegalArgumentException.class,
          () -> service.parseFfmDecodeInput(json, SupportedMessageType.BSM));
    }
    for (String asn1 : new String[] {"null", "\"\"", "\"   \""}) {
      String json = "{\"metadata\":{\"asn1\":" + asn1
          + "},\"payload\":{\"data\":{\"bytes\":\"" + payload + "\"}}}";
      assertArrayEquals(fixtureBytes(), service.parseFfmDecodeInput(json,
          SupportedMessageType.BSM).uperBytes());
    }

    String duplicateFinalValid = "{\"metadata\":{\"asn1\":\"gg\"},"
        + "\"payload\":{\"data\":{\"bytes\":\"" + payload + "\"}},"
        + "\"metadata\":{\"asn1\":\"" + payload + "\"}}";
    assertEquals(payload, service.parseFfmDecodeInput(duplicateFinalValid,
        SupportedMessageType.BSM).metadata().getAsn1());
  }

  private static byte[] fixtureBytes() throws Exception {
    Path fixture = Path.of("src/test/resources/us/dot/its/jpo/ode/udp/bsm/"
        + "BsmReceiverTest_ValidBSM.txt");
    return HexUtils.fromHexString(Files.readString(fixture).trim());
  }

  private static String rawJson(ObjectMapper mapper, byte[] packet, Map<String, ?> metadata)
      throws Exception {
    return mapper.writeValueAsString(Map.of(
        "metadata", metadata,
        "payload", Map.of("data", Map.of("bytes", CodecUtils.toHex(packet)))));
  }
}
