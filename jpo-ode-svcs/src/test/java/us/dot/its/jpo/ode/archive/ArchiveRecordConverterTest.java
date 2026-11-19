package us.dot.its.jpo.ode.archive;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static us.dot.its.jpo.ode.archive.ArchiveTestData.BSM_HEX;
import static us.dot.its.jpo.ode.archive.ArchiveTestData.compactLine;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.DatagramPacket;
import java.net.InetAddress;
import org.apache.tomcat.util.buf.HexUtils;
import org.junit.jupiter.api.Test;
import us.dot.its.jpo.ode.archive.ArchiveRecordConverter.ConvertedRecord;
import us.dot.its.jpo.ode.archive.ArchiveRecordConverter.LineFormat;
import us.dot.its.jpo.ode.config.SerializationConfig;
import us.dot.its.jpo.ode.kafka.listeners.json.RawEncodedJsonService;
import us.dot.its.jpo.ode.model.OdeAsn1Data;
import us.dot.its.jpo.ode.model.OdeMessageFrameMetadata;
import us.dot.its.jpo.ode.udp.UdpHexDecoder;
import us.dot.its.jpo.ode.uper.SupportedMessageType;

class ArchiveRecordConverterTest {

  private static final String RECEIVED_AT = "2026-10-29T22:07:29.253Z";
  private static final String ORIGIN_IP = "10.10.0.3";

  private final ObjectMapper mapper = new SerializationConfig().objectMapper();

  @Test
  void detectsLineFormats() throws Exception {
    assertEquals(LineFormat.COMPACT,
        ArchiveRecordConverter.detect(mapper.readTree(compactLine(RECEIVED_AT, ORIGIN_IP, "00"))));
    assertEquals(LineFormat.FULL,
        ArchiveRecordConverter.detect(mapper.readTree("{\"metadata\":{},\"payload\":{}}")));
    assertEquals(LineFormat.UNKNOWN, ArchiveRecordConverter.detect(mapper.readTree("{\"a\":1}")));
    assertEquals(LineFormat.UNKNOWN, ArchiveRecordConverter.detect(mapper.readTree("[1]")));
  }

  @Test
  void compactSignedMapMatchesUdpRecordExceptReceiveTimeAndOrigin() throws Exception {
    String packetHex = ArchiveTestData.mapPacketHex("1609.2 Signature header");
    byte[] packet = HexUtils.fromHexString(packetHex);
    JsonNode udp = mapper.readTree(UdpHexDecoder.buildJsonMapFromPacket(
        new DatagramPacket(packet, packet.length, InetAddress.getLoopbackAddress(), 1)));

    ConvertedRecord converted = ArchiveRecordConverter.fromCompact(
        mapper.readTree(compactLine(RECEIVED_AT, ORIGIN_IP, packetHex)),
        SupportedMessageType.MAP);
    JsonNode imported = mapper.readTree(converted.json());

    assertEquals(SupportedMessageType.MAP, converted.messageType());
    JsonNode metadata = imported.get("metadata");
    assertEquals(RECEIVED_AT, metadata.get("odeReceivedAt").asText());
    assertEquals(ORIGIN_IP, metadata.get("originIp").asText());
    for (String field : new String[] {"recordType", "source", "recordGeneratedBy",
        "securityResultCode", "asn1", "payloadType", "schemaVersion", "sanitized"}) {
      assertEquals(udp.get("metadata").get(field), metadata.get(field), field);
    }
    assertEquals(udp.get("payload"), imported.get("payload"));
    assertTrue(imported.get("payload").get("data").get("bytes").asText().startsWith("0012"),
        "The 1609.2 header should be stripped from the payload");
  }

  @Test
  void compactBsmGetsReceivedMessageDetailsLikeUdp() throws Exception {
    ConvertedRecord converted = ArchiveRecordConverter.fromCompact(
        mapper.readTree(compactLine(RECEIVED_AT, ORIGIN_IP, BSM_HEX)), SupportedMessageType.BSM);
    JsonNode metadata = mapper.readTree(converted.json()).get("metadata");

    assertEquals("bsmTx", metadata.get("recordType").asText());
    assertEquals("EV", metadata.get("source").asText());
    assertEquals("RSU", metadata.at("/receivedMessageDetails/rxSource").asText());
  }

  @Test
  void compactLineDetectsMessageTypeWhenUnknown() throws Exception {
    ConvertedRecord converted = ArchiveRecordConverter.fromCompact(
        mapper.readTree(compactLine(RECEIVED_AT, "", BSM_HEX)), null);

    assertEquals(SupportedMessageType.BSM, converted.messageType());
    assertEquals("", mapper.readTree(converted.json()).at("/metadata/originIp").asText());
  }

  @Test
  void convertedRecordIsAcceptedByRawEncodedJsonServiceWithMetadataKept() throws Exception {
    String packetHex = ArchiveTestData.mapPacketHex("1609.2 Signature header");
    ConvertedRecord converted = ArchiveRecordConverter.fromCompact(
        mapper.readTree(compactLine(RECEIVED_AT, ORIGIN_IP, packetHex)),
        SupportedMessageType.MAP);

    OdeAsn1Data data = new RawEncodedJsonService(mapper).addEncodingAndMutateBytes(
        converted.json(), SupportedMessageType.MAP, OdeMessageFrameMetadata.class);

    OdeMessageFrameMetadata metadata = (OdeMessageFrameMetadata) data.getMetadata();
    assertEquals(RECEIVED_AT, metadata.getOdeReceivedAt());
    assertEquals(ORIGIN_IP, metadata.getOriginIp());
    assertEquals(packetHex.toUpperCase(), metadata.getAsn1());
  }

  @Test
  void redecodedFullRecordKeepsOriginalRecordTypeAndSource() throws Exception {
    String decoded = "{\"metadata\":{\"odeReceivedAt\":\"" + RECEIVED_AT + "\",\"originIp\":\"\","
        + "\"recordType\":\"rxMsg\",\"source\":\"EV\",\"recordGeneratedBy\":\"OBU\","
        + "\"asn1\":\"" + BSM_HEX + "\"},\"payload\":{\"data\":{}}}";

    ConvertedRecord converted =
        ArchiveRecordConverter.fromDecoded(mapper.readTree(decoded), SupportedMessageType.BSM);
    JsonNode metadata = mapper.readTree(converted.json()).get("metadata");

    assertEquals(RECEIVED_AT, metadata.get("odeReceivedAt").asText());
    assertEquals("rxMsg", metadata.get("recordType").asText());
    assertEquals("EV", metadata.get("source").asText());
    assertEquals("OBU", metadata.get("recordGeneratedBy").asText());
  }

  @Test
  void rejectsInvalidCompactLines() throws Exception {
    JsonNode noAsn1 = mapper.readTree("{\"odeReceivedAt\":\"" + RECEIVED_AT + "\",\"asn1\":\"\"}");
    assertThrows(ArchiveImportException.class,
        () -> ArchiveRecordConverter.fromCompact(noAsn1, SupportedMessageType.BSM));

    JsonNode badHex = mapper.readTree(compactLine(RECEIVED_AT, ORIGIN_IP, "00ZZ"));
    assertThrows(ArchiveImportException.class,
        () -> ArchiveRecordConverter.fromCompact(badHex, SupportedMessageType.BSM));

    JsonNode wrongType = mapper.readTree(compactLine(RECEIVED_AT, ORIGIN_IP, BSM_HEX));
    assertThrows(ArchiveImportException.class,
        () -> ArchiveRecordConverter.fromCompact(wrongType, SupportedMessageType.TIM));

    JsonNode noTime = mapper.readTree("{\"originIp\":\"1.2.3.4\",\"asn1\":\"" + BSM_HEX + "\"}");
    assertThrows(ArchiveImportException.class,
        () -> ArchiveRecordConverter.fromCompact(noTime, SupportedMessageType.BSM));

    JsonNode noType = mapper.readTree(compactLine(RECEIVED_AT, ORIGIN_IP, "ABCDEF"));
    ArchiveImportException e = assertThrows(ArchiveImportException.class,
        () -> ArchiveRecordConverter.fromCompact(noType, null));
    assertFalse(e.getMessage().isEmpty());
  }
}
