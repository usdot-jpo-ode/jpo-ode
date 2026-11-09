package us.dot.its.jpo.ode.archive;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Locale;
import org.apache.tomcat.util.buf.HexUtils;
import us.dot.its.jpo.ode.model.OdeAsn1Data;
import us.dot.its.jpo.ode.model.OdeLogMetadata.RecordType;
import us.dot.its.jpo.ode.model.OdeMessageFrameMetadata;
import us.dot.its.jpo.ode.model.OdeMessageFrameMetadata.Source;
import us.dot.its.jpo.ode.model.OdeMsgMetadata.GeneratedBy;
import us.dot.its.jpo.ode.udp.InvalidPayloadException;
import us.dot.its.jpo.ode.udp.UdpHexDecoder;
import us.dot.its.jpo.ode.uper.SupportedMessageType;
import us.dot.its.jpo.ode.uper.UperUtil;
import us.dot.its.jpo.ode.util.JsonUtils;

/**
 * Converts archived records into the raw encoded records the ODE decodes.
 *
 * <p>The records are built with {@link UdpHexDecoder}, so they have the same metadata as records
 * received over UDP, except that the archived receive time and origin IP are kept.
 */
public final class ArchiveRecordConverter {

  private ArchiveRecordConverter() {
  }

  /**
   * The format of one archived line.
   */
  public enum LineFormat {
    /** A compact archive line, {@code {"odeReceivedAt":..., "originIp":..., "asn1":...}}. */
    COMPACT,
    /** A full record with {@code metadata} and {@code payload}. */
    FULL,
    /** Anything else. */
    UNKNOWN
  }

  /**
   * Detects the format of an archived line.
   *
   * @param line the parsed line
   * @return the line format
   */
  public static LineFormat detect(JsonNode line) {
    if (line == null || !line.isObject()) {
      return LineFormat.UNKNOWN;
    }
    if (line.has("metadata") && line.has("payload")) {
      return LineFormat.FULL;
    }
    if (line.hasNonNull("asn1")) {
      return LineFormat.COMPACT;
    }
    return LineFormat.UNKNOWN;
  }

  /**
   * Converts a compact archive line into the JSON of a raw encoded record.
   *
   * @param line        the parsed compact line
   * @param messageType the message type, or null to detect it from the start flags in
   *                    {@code asn1}
   * @return the converted record
   * @throws ArchiveImportException if the line has no valid {@code asn1} for the message type
   */
  public static ConvertedRecord fromCompact(JsonNode line, SupportedMessageType messageType)
      throws ArchiveImportException {
    String asn1 = text(line, "asn1");
    SupportedMessageType type = messageType != null ? messageType : detectMessageType(asn1);
    OdeAsn1Data data = build(asn1, type, text(line, "originIp"), text(line, "odeReceivedAt"));
    return new ConvertedRecord(type, JsonUtils.toJson(data, false));
  }

  /**
   * Converts a full decoded record into the JSON of a raw encoded record, so that it is decoded
   * again. The record type, source and generator of the original metadata are kept.
   *
   * @param line        the parsed decoded record
   * @param messageType the message type of the decoded topic
   * @return the converted record
   * @throws ArchiveImportException if the record has no valid {@code metadata.asn1}
   */
  public static ConvertedRecord fromDecoded(JsonNode line, SupportedMessageType messageType)
      throws ArchiveImportException {
    JsonNode metadata = line.path("metadata");
    OdeAsn1Data data = build(text(metadata, "asn1"), messageType, text(metadata, "originIp"),
        text(metadata, "odeReceivedAt"));
    OdeMessageFrameMetadata frameMetadata = (OdeMessageFrameMetadata) data.getMetadata();
    RecordType recordType = enumValue(RecordType.class, text(metadata, "recordType"));
    if (recordType != null) {
      frameMetadata.setRecordType(recordType);
    }
    Source source = enumValue(Source.class, text(metadata, "source"));
    if (source != null) {
      frameMetadata.setSource(source);
    }
    GeneratedBy generatedBy = enumValue(GeneratedBy.class, text(metadata, "recordGeneratedBy"));
    if (generatedBy != null) {
      frameMetadata.setRecordGeneratedBy(generatedBy);
    }
    return new ConvertedRecord(messageType, JsonUtils.toJson(data, false));
  }

  private static OdeAsn1Data build(String asn1, SupportedMessageType type, String originIp,
      String odeReceivedAt) throws ArchiveImportException {
    if (asn1 == null || asn1.isEmpty()) {
      throw new ArchiveImportException("Record has no asn1");
    }
    if (odeReceivedAt == null || odeReceivedAt.isEmpty()) {
      throw new ArchiveImportException("Record has no odeReceivedAt");
    }
    byte[] packet;
    try {
      packet = HexUtils.fromHexString(asn1);
    } catch (IllegalArgumentException e) {
      throw new ArchiveImportException("asn1 is not valid hex", e);
    }
    try {
      return UdpHexDecoder.buildAsn1Data(packet, type, originIp == null ? "" : originIp,
          odeReceivedAt);
    } catch (InvalidPayloadException e) {
      throw new ArchiveImportException("asn1 is not a " + type + ": " + e.getMessage(), e);
    }
  }

  private static SupportedMessageType detectMessageType(String asn1)
      throws ArchiveImportException {
    if (asn1 == null || asn1.isEmpty()) {
      throw new ArchiveImportException("Record has no asn1");
    }
    String type = UperUtil.determineHexPacketType(asn1.toLowerCase(Locale.ROOT));
    if (type.isEmpty()) {
      throw new ArchiveImportException("No supported message type found in asn1");
    }
    return SupportedMessageType.valueOf(type);
  }

  private static String text(JsonNode node, String field) {
    JsonNode value = node.get(field);
    return value == null || value.isNull() ? null : value.asText();
  }

  private static <E extends Enum<E>> E enumValue(Class<E> type, String name) {
    if (name == null) {
      return null;
    }
    try {
      return Enum.valueOf(type, name);
    } catch (IllegalArgumentException e) {
      return null;
    }
  }

  /**
   * A record converted into a raw encoded record.
   *
   * @param messageType the message type, which selects the raw encoded topic
   * @param json        the raw encoded record JSON
   */
  public record ConvertedRecord(SupportedMessageType messageType, String json) {
  }
}
