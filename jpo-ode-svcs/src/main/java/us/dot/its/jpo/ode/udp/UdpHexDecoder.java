package us.dot.its.jpo.ode.udp;

import java.net.DatagramPacket;
import java.util.EnumMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.buf.HexUtils;
import us.dot.its.jpo.ode.model.OdeAsn1Data;
import us.dot.its.jpo.ode.model.OdeAsn1Payload;
import us.dot.its.jpo.ode.model.OdeLogMetadata.RecordType;
import us.dot.its.jpo.ode.model.OdeLogMetadata.SecurityResultCode;
import us.dot.its.jpo.ode.model.OdeLogMsgMetadataLocation;
import us.dot.its.jpo.ode.model.OdeMessageFrameMetadata;
import us.dot.its.jpo.ode.model.OdeMessageFrameMetadata.Source;
import us.dot.its.jpo.ode.model.OdeMsgMetadata.GeneratedBy;
import us.dot.its.jpo.ode.model.ReceivedMessageDetails;
import us.dot.its.jpo.ode.model.RxSource;
import us.dot.its.jpo.ode.uper.StartFlagNotFoundException;
import us.dot.its.jpo.ode.uper.SupportedMessageType;
import us.dot.its.jpo.ode.uper.UperUtil;
import us.dot.its.jpo.ode.util.CodecUtils;
import us.dot.its.jpo.ode.util.DateTimeUtils;
import us.dot.its.jpo.ode.util.JsonUtils;

/**
 * The `UdpHexDecoder` class provides functionalities to decode UDP DatagramPackets into specific
 * JSON representations based on the message type. It supports the extraction and conversion of the
 * packet payloads into ASN.1 payloads for various message types such as MAP, SPAT, TIM, BSM, SSM,
 * SRM, and PSM.
 *
 * <p>The class logs details about the packet's origin and ensures the payload contains the correct
 * message type start flag. It provides methods to build JSON objects encapsulating metadata about
 * each packet's origin, source, record type, and security information.
 *
 * <p>The same metadata can be built from packet bytes that were received earlier, for example from
 * an S3 archive, with {@link #buildAsn1Data(byte[], SupportedMessageType, String, String)}.
 *
 * <p>Note that this class cannot be instantiated.
 */
@Slf4j
public class UdpHexDecoder {

  /**
   * The metadata values the UDP receivers set for each message type.
   */
  private record MessageProfile(RecordType recordType, Source source, GeneratedBy generatedBy,
                                boolean includeReceivedMessageDetails) {
  }

  private static final Map<SupportedMessageType, MessageProfile> PROFILES =
      new EnumMap<>(SupportedMessageType.class);

  static {
    PROFILES.put(SupportedMessageType.MAP,
        new MessageProfile(RecordType.mapTx, Source.RSU, GeneratedBy.RSU, false));
    PROFILES.put(SupportedMessageType.SPAT,
        new MessageProfile(RecordType.spatTx, Source.RSU, GeneratedBy.RSU, false));
    PROFILES.put(SupportedMessageType.TIM,
        new MessageProfile(RecordType.timMsg, Source.RSU, GeneratedBy.RSU, false));
    PROFILES.put(SupportedMessageType.BSM,
        new MessageProfile(RecordType.bsmTx, Source.EV, GeneratedBy.OBU, true));
    PROFILES.put(SupportedMessageType.SSM,
        new MessageProfile(RecordType.ssmTx, Source.RSU, GeneratedBy.RSU, false));
    PROFILES.put(SupportedMessageType.SRM,
        new MessageProfile(RecordType.srmTx, Source.RSU, GeneratedBy.OBU, false));
    PROFILES.put(SupportedMessageType.PSM,
        new MessageProfile(RecordType.psmTx, Source.RSU, GeneratedBy.UNKNOWN, false));
    PROFILES.put(SupportedMessageType.SDSM,
        new MessageProfile(RecordType.sdsmTx, Source.RSU, GeneratedBy.RSU, false));
    PROFILES.put(SupportedMessageType.RTCM,
        new MessageProfile(RecordType.rtcmTx, Source.RSU, GeneratedBy.RSU, false));
    PROFILES.put(SupportedMessageType.RSM,
        new MessageProfile(RecordType.rsmTx, Source.RSU, GeneratedBy.RSU, false));
  }

  private UdpHexDecoder() {
    throw new UnsupportedOperationException();
  }

  /**
   * Result of extracting an ASN.1 payload from a UDP packet: the stripped payload used for decode,
   * plus the full received hex (before 1609.3 / 1609.2 header stripping) for metadata.
   */
  private record Asn1PayloadExtraction(OdeAsn1Payload payload, String untrimmedPayloadHex) {
  }

  /**
   * Extracts the payload from the given {@link DatagramPacket} and converts it into an
   * {@link OdeAsn1Payload} object. The method validates that the payload contains the necessary
   * start flag for the specified message type.
   *
   * @param packet the DatagramPacket containing the data
   * @param msgType the type of message expected in the payload
   * @return the extracted OdeAsn1Payload from the packet
   * @throws InvalidPayloadException if the payload is null or does not contain the expected start
   *         flag
   */
  public static OdeAsn1Payload getPayloadHexString(DatagramPacket packet,
      SupportedMessageType msgType) throws InvalidPayloadException {
    return extractAsn1Payload(packetBytes(packet), msgType).payload();
  }

  private static byte[] packetBytes(DatagramPacket packet) throws InvalidPayloadException {
    // retrieve the buffer from the packet
    byte[] buffer = packet.getData();
    if (buffer == null) {
      throw new InvalidPayloadException("Buffer is null, no payload to extract");
    }

    // retrieve the payload from the buffer
    return retrieveRelevantBytes(packet.getLength(), buffer, packet.getOffset());
  }

  private static Asn1PayloadExtraction extractAsn1Payload(byte[] payload,
      SupportedMessageType msgType) throws InvalidPayloadException {
    if (payload == null) {
      throw new InvalidPayloadException("Payload is null, no payload to extract");
    }

    // Lowercase hex for start-flag matching (flags use a-f, e.g. TIM "001f"); uppercase for
    // metadata.asn1 to match OdeHexByteArray / CodecUtils serialization.
    String untrimmedPayloadHexLower = HexUtils.toHexString(payload).toLowerCase();
    if (!untrimmedPayloadHexLower.contains(msgType.getStartFlag())) {
      throw new InvalidPayloadException("Payload does not contain start flag");
    }

    log.debug("Full {} packet: {}", msgType, untrimmedPayloadHexLower);

    String strippedHex =
        UperUtil.stripDot3Header(untrimmedPayloadHexLower, msgType.getStartFlag()).toLowerCase();

    // Adding the dot2 header stripping here to handle the case where the signed 1609.2 header is
    // present.
    try {
      strippedHex = UperUtil.stripDot2Header(strippedHex, msgType.getStartFlag());
    } catch (StartFlagNotFoundException e) {
      log.debug("Error stripping dot2 header: {}", e.getMessage());
    }

    log.debug("Stripped {} packet: {}", msgType, strippedHex);

    return new Asn1PayloadExtraction(new OdeAsn1Payload(HexUtils.fromHexString(strippedHex)),
        CodecUtils.toHex(payload));
  }

  /**
   * Converts the data from the given {@link DatagramPacket} into a JSON string representing a MAP
   * message. It extracts metadata and payload, then structures them into a JSON format.
   *
   * @param packet the DatagramPacket containing the MAP data
   * @return a JSON string representing the MAP message
   * @throws InvalidPayloadException if the payload extraction fails
   */
  public static String buildJsonMapFromPacket(DatagramPacket packet)
      throws InvalidPayloadException {
    return buildJsonFromPacket(packet, SupportedMessageType.MAP);
  }

  /**
   * Converts the data from the given {@link DatagramPacket} into a JSON string representing an SPAT
   * message. It extracts metadata and payload, then structures them into a JSON format.
   *
   * @param packet the DatagramPacket containing the SPAT data
   * @return a JSON string representing the SPAT message
   * @throws InvalidPayloadException if the payload extraction fails
   */
  public static String buildJsonSpatFromPacket(DatagramPacket packet)
      throws InvalidPayloadException {
    return buildJsonFromPacket(packet, SupportedMessageType.SPAT);
  }

  /**
   * Converts the data from the given {@link DatagramPacket} into a JSON string representing a TIM
   * message. It extracts metadata and payload, then structures them into a JSON format.
   *
   * @param packet the DatagramPacket containing the TIM data
   * @return a JSON string representing the TIM message
   * @throws InvalidPayloadException if the payload extraction fails
   */
  public static String buildJsonTimFromPacket(DatagramPacket packet)
      throws InvalidPayloadException {
    return buildJsonFromPacket(packet, SupportedMessageType.TIM);
  }

  /**
   * Converts the data from the given {@link DatagramPacket} into a JSON string representing a BSM
   * message. It extracts metadata and payload, then structures them into a JSON format.
   *
   * @param packet the DatagramPacket containing the BSM data
   * @return a JSON string representing the BSM message
   * @throws InvalidPayloadException if the payload extraction fails
   */
  public static String buildJsonBsmFromPacket(DatagramPacket packet)
      throws InvalidPayloadException {
    return buildJsonFromPacket(packet, SupportedMessageType.BSM);
  }

  /**
   * Converts the data from the given {@link DatagramPacket} into a JSON string representing an SSM
   * message. It extracts metadata and payload, then structures them into a JSON format.
   *
   * @param packet the DatagramPacket containing the SSM data
   * @return a JSON string representing the SSM message
   * @throws InvalidPayloadException if the payload extraction fails
   */
  public static String buildJsonSsmFromPacket(DatagramPacket packet)
      throws InvalidPayloadException {
    return buildJsonFromPacket(packet, SupportedMessageType.SSM);
  }

  /**
   * Converts the data from the given {@link DatagramPacket} into a JSON string representing an SRM
   * message. It extracts metadata and payload, then structures them into a JSON format.
   *
   * @param packet the DatagramPacket containing the SRM data
   * @return a JSON string representing the SRM message
   * @throws InvalidPayloadException if the payload extraction fails
   */
  public static String buildJsonSrmFromPacket(DatagramPacket packet)
      throws InvalidPayloadException {
    return buildJsonFromPacket(packet, SupportedMessageType.SRM);
  }

  /**
   * Converts the data from the given {@link DatagramPacket} into a JSON string representing a PSM
   * message. It extracts metadata and payload, then structures them into a JSON format.
   *
   * @param packet the DatagramPacket containing the PSM data
   * @return a JSON string representing the PSM message
   * @throws InvalidPayloadException if the payload extraction fails
   */
  public static String buildJsonPsmFromPacket(DatagramPacket packet)
      throws InvalidPayloadException {
    return buildJsonFromPacket(packet, SupportedMessageType.PSM);
  }

  /**
   * Converts the data from the given {@link DatagramPacket} into a JSON string representing a SDSM
   * message. It extracts metadata and payload, then structures them into a JSON format.
   *
   * @param packet the DatagramPacket containing the SDSM data
   * @return a JSON string representing the SDSM message
   * @throws InvalidPayloadException if the payload extraction fails
   */
  public static String buildJsonSdsmFromPacket(DatagramPacket packet)
      throws InvalidPayloadException {
    return buildJsonFromPacket(packet, SupportedMessageType.SDSM);
  }

  /**
   * Converts the data from the given {@link DatagramPacket} into a JSON string representing a RTCM
   * message. It extracts metadata and payload, then structures them into a JSON format.
   *
   * @param packet the DatagramPacket containing the RTCM data
   * @return a JSON string representing the RTCM message
   * @throws InvalidPayloadException if the payload extraction fails
   */
  public static String buildJsonRtcmFromPacket(DatagramPacket packet)
      throws InvalidPayloadException {
    return buildJsonFromPacket(packet, SupportedMessageType.RTCM);
  }

  /**
   * Converts the data from the given {@link DatagramPacket} into a JSON string representing a RSM
   * message. It extracts metadata and payload, then structures them into a JSON format.
   *
   * @param packet the DatagramPacket containing the RSM data
   * @return a JSON string representing the RSM message
   * @throws InvalidPayloadException if the payload extraction fails
   */
  public static String buildJsonRsmFromPacket(DatagramPacket packet)
      throws InvalidPayloadException {
    return buildJsonFromPacket(packet, SupportedMessageType.RSM);
  }

  private static String buildJsonFromPacket(DatagramPacket packet,
      SupportedMessageType messageType) throws InvalidPayloadException {
    MessageProfile profile = PROFILES.get(messageType);
    return JsonUtils.toJson(buildAsn1DataFromPacket(packet, messageType, profile.recordType(),
        profile.source(), profile.generatedBy(), profile.includeReceivedMessageDetails()), false);
  }

  /**
   * Given a buffer containing the full payload, this method retrieves and returns only the relevant
   * bytes of the message, excluding any padded bytes.
   *
   * @param length The length of the message
   * @param buffer The buffer containing the full message and possibly padded bytes
   * @param offset The position in the buffer where the message starts
   * @return The relevant bytes of the message
   */
  private static byte[] retrieveRelevantBytes(int length, byte[] buffer, int offset) {
    byte[] relevantPayload = new byte[length];
    System.arraycopy(buffer, offset, relevantPayload, 0, length);
    return relevantPayload;
  }

  /**
   * Builds an ASN.1 data object from a given DatagramPacket.
   *
   * @param packet The DatagramPacket containing the data
   * @param messageType The type of message to be decoded
   * @param recordType The type of record to be set in the metadata
   * @param source The source of the message
   * @param generatedBy The entity that generated the message
   * @return The ASN.1 data object
   * @throws InvalidPayloadException if the payload extraction fails
   */
  public static OdeAsn1Data buildAsn1DataFromPacket(DatagramPacket packet,
      SupportedMessageType messageType, RecordType recordType, Source source,
      GeneratedBy generatedBy, boolean includeReceivedMessageDetails)
      throws InvalidPayloadException {

    String senderIp = packet.getAddress().getHostAddress();
    int senderPort = packet.getPort();
    log.debug("Packet received from {}:{}", senderIp, senderPort);

    return buildAsn1Data(packetBytes(packet), messageType, recordType, source, generatedBy,
        includeReceivedMessageDetails, senderIp, DateTimeUtils.now());
  }

  /**
   * Builds the same ASN.1 data object that the UDP receiver of the message type builds, from the
   * bytes of a packet that was received earlier. The metadata keeps the given receive time and
   * origin IP, so a re-ingested packet can be correlated with its original record.
   *
   * @param packetBytes the full received packet, including any 1609.3 and 1609.2 headers
   * @param messageType the type of message in the packet
   * @param originIp the IP address the packet was originally received from, may be empty
   * @param odeReceivedAt the time the ODE originally received the packet
   * @return The ASN.1 data object
   * @throws InvalidPayloadException if the packet does not contain the message type's start flag
   */
  public static OdeAsn1Data buildAsn1Data(byte[] packetBytes, SupportedMessageType messageType,
      String originIp, String odeReceivedAt) throws InvalidPayloadException {
    MessageProfile profile = PROFILES.get(messageType);
    return buildAsn1Data(packetBytes, messageType, profile.recordType(), profile.source(),
        profile.generatedBy(), profile.includeReceivedMessageDetails(), originIp, odeReceivedAt);
  }

  private static OdeAsn1Data buildAsn1Data(byte[] packetBytes, SupportedMessageType messageType,
      RecordType recordType, Source source, GeneratedBy generatedBy,
      boolean includeReceivedMessageDetails, String originIp, String odeReceivedAt)
      throws InvalidPayloadException {

    // Create OdeMsgPayload and OdeLogMetadata objects and populate them
    Asn1PayloadExtraction extracted = extractAsn1Payload(packetBytes, messageType);
    OdeAsn1Payload payload = extracted.payload();
    OdeMessageFrameMetadata metadata =
        new OdeMessageFrameMetadata(payload, extracted.untrimmedPayloadHex());

    // Add header data for the decoding process
    metadata.setOdeReceivedAt(odeReceivedAt);

    metadata.setOriginIp(originIp);
    metadata.setSource(source);
    metadata.setRecordType(recordType);
    metadata.setRecordGeneratedBy(generatedBy);
    metadata.setSecurityResultCode(SecurityResultCode.success);

    if (includeReceivedMessageDetails) {
      ReceivedMessageDetails receivedMessageDetails = new ReceivedMessageDetails();
      OdeLogMsgMetadataLocation locationData = new OdeLogMsgMetadataLocation("unavailable",
          "unavailable", "unavailable", "unavailable", "unavailable");
      receivedMessageDetails.setRxSource(RxSource.RSU);
      receivedMessageDetails.setLocationData(locationData);
      metadata.setReceivedMessageDetails(receivedMessageDetails);
    }

    return new OdeAsn1Data(metadata, payload);
  }

}
