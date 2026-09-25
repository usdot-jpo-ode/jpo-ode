package us.dot.its.jpo.ode.uper;

import java.util.Arrays;
import java.util.HashMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.buf.HexUtils;
import org.json.JSONObject;
import us.dot.its.jpo.ode.model.OdeMsgPayload;
import us.dot.its.jpo.ode.model.OdeObject;
import us.dot.its.jpo.ode.util.JsonUtils;
import us.dot.its.jpo.ode.util.JsonUtils.JsonUtilsException;

/**
 * Utility class for handling and manipulating hexadecimal strings representing network packet data,
 * particularly those adhering to IEEE 1609.2 and 1609.3 standards.
 */
@Slf4j
public class UperUtil {

  /** IEEE 1609.2 signed content marker ({@code 03 81 00}). */
  private static final byte[] SIGNED_DOT2_MARKER = {0x03, (byte) 0x81, 0x00};

  private UperUtil() {
    throw new UnsupportedOperationException();
  }

  /**
   * Strips the IEEE 1609.2 security header (if it exists) and returns the payload from a given
   * hexadecimal string. The method searches for a specified start flag that indicates the beginning
   * of the payload.
   *
   * @param hexString the input hexadecimal string from which the IEEE 1609.2 security header needs
   *        to be stripped.
   * @param payloadStartFlag the start flag indicating the beginning of the payload.
   * @return a string representing the payload without the IEEE 1609.2 security header, if the start
   *         flag is found.
   * @throws StartFlagNotFoundException if the specified start flag is not found within the
   *         hexadecimal string.
   */
  public static String stripDot2Header(String hexString, String payloadStartFlag)
      throws StartFlagNotFoundException {
    hexString = hexString.toLowerCase();
    int startIndex = findValidStartFlagLocation(hexString, payloadStartFlag);
    if (startIndex == -1) {
      throw new StartFlagNotFoundException(
          "Start flag '%s' not found in message: '%s'".formatted(payloadStartFlag, hexString));
    }
    return hexString.substring(startIndex);
  }

  /**
   * Byte-oriented equivalent of {@link #stripDot2Header(String, String)}.
   */
  public static byte[] stripDot2Header(byte[] packet, byte[] payloadStartFlag)
      throws StartFlagNotFoundException {
    int startIndex = findValidStartFlagLocation(packet, payloadStartFlag);
    if (startIndex == -1) {
      throw new StartFlagNotFoundException(
          "Start flag not found in binary message (flag length=%d, packet length=%d)"
              .formatted(payloadStartFlag.length, packet.length));
    }
    if (startIndex == 0) {
      return packet;
    }
    return Arrays.copyOfRange(packet, startIndex, packet.length);
  }

  /**
   * Strips the 1609.3 and unsigned 1609.2 headers if they are present. Will return the payload with
   * a signed 1609.2 header if it is present. Otherwise, returns just the payload.
   */
  public static byte[] stripDot3Header(byte[] packet, HashMap<String, String> msgStartFlags) {

    String hexString = HexUtils.toHexString(packet);
    String hexPacketParsed = "";

    for (String startFlag : msgStartFlags.values()) {
      int payloadStartIndex = findValidStartFlagLocation(hexString, startFlag);
      if (payloadStartIndex == -1) {
        continue;
      }

      String headers = hexString.substring(0, payloadStartIndex);
      String payload = hexString.substring(payloadStartIndex);

      // Look for the index of the start flag of a signed 1609.2 header, if one exists
      int signedDot2StartIndex = headers.indexOf("038100");
      if (signedDot2StartIndex == -1) {
        hexPacketParsed = payload;
      } else {
        hexPacketParsed = headers.substring(signedDot2StartIndex) + payload;
      }
      break;
    }

    if (hexPacketParsed.isEmpty()) {
      hexPacketParsed = hexString;
      log.debug("Packet is not a BSM, TIM or Map message: {}", hexPacketParsed);
    }
    return HexUtils.fromHexString(hexPacketParsed);
  }

  /**
   * Byte-oriented strip of 1609.3 / unsigned 1609.2 headers for a known message start flag.
   * Avoids allocating hex strings on the UDP hot path.
   */
  public static byte[] stripDot3Header(byte[] packet, byte[] payloadStartFlag) {
    int payloadStartIndex = findValidStartFlagLocation(packet, payloadStartFlag);
    if (payloadStartIndex == -1) {
      return packet;
    }
    int signedDot2StartIndex = indexOf(packet, SIGNED_DOT2_MARKER, 0, payloadStartIndex);
    int from = signedDot2StartIndex == -1 ? payloadStartIndex : signedDot2StartIndex;
    if (from == 0) {
      return packet;
    }
    return Arrays.copyOfRange(packet, from, packet.length);
  }

  /**
   * Strips the 1609.3 and unsigned 1609.2 headers if they are present. Will return the payload with
   * a signed 1609.2 header if it is present. Otherwise, returns just the payload.
   */
  public static String stripDot3Header(String hexString, String payloadStartFlag) {
    int payloadStartIndex = findValidStartFlagLocation(hexString, payloadStartFlag);
    String headers = hexString.substring(0, payloadStartIndex);
    String payload = hexString.substring(payloadStartIndex);
    if (log.isDebugEnabled()) {
      log.debug("Base payload: {}", payload);
    }
    // Look for the index of the start flag of a signed 1609.2 header
    int signedDot2StartIndex = headers.indexOf("038100");
    if (signedDot2StartIndex == -1) {
      return payload;
    } else {
      return headers.substring(signedDot2StartIndex) + payload;
    }
  }

  /**
   * Determines the message type based off the most likely start flag.
   *
   * @param payload The OdeMsgPayload to check the content of.
   */
  public static String determineMessageType(OdeMsgPayload<OdeObject> payload) {
    String messageType = "";
    try {
      JSONObject payloadJson = JsonUtils.toJSONObject(payload.getData().toJson());
      String hexString = payloadJson.getString("bytes").toLowerCase();
      messageType = determineHexPacketType(hexString);

    } catch (JsonUtilsException e) {
      log.error("JsonUtilsException while checking message header.", e);
    }
    return messageType;
  }

  /**
   * Determines the type of hex packet based on predefined start flags for various message types
   * defined by {@link SupportedMessageType}.
   *
   * @param hexString the hexadecimal string representing a packet whose type is to be determined
   * @return a string indicating the type of the packet, such as "MAP", "SPAT", "TIM", "BSM", "SSM",
   *         "PSM", "SRM", "SDSM", "RTCM", or "RSM". If no valid type is found, returns an empty string.
   */
  public static String determineHexPacketType(String hexString) {
    HashMap<String, Integer> flagIndexes = new HashMap<>();

    flagIndexes.put("MAP",
        findValidStartFlagLocation(hexString, SupportedMessageType.MAP.getStartFlag()));
    flagIndexes.put("SPAT",
        findValidStartFlagLocation(hexString, SupportedMessageType.SPAT.getStartFlag()));
    flagIndexes.put("TIM",
        findValidStartFlagLocation(hexString, SupportedMessageType.TIM.getStartFlag()));
    flagIndexes.put("BSM",
        findValidStartFlagLocation(hexString, SupportedMessageType.BSM.getStartFlag()));
    flagIndexes.put("SSM",
        findValidStartFlagLocation(hexString, SupportedMessageType.SSM.getStartFlag()));
    flagIndexes.put("PSM",
        findValidStartFlagLocation(hexString, SupportedMessageType.PSM.getStartFlag()));
    flagIndexes.put("SRM",
        findValidStartFlagLocation(hexString, SupportedMessageType.SRM.getStartFlag()));
    flagIndexes.put("SDSM",
        findValidStartFlagLocation(hexString, SupportedMessageType.SDSM.getStartFlag()));
    flagIndexes.put("RTCM",
        findValidStartFlagLocation(hexString, SupportedMessageType.RTCM.getStartFlag()));
    flagIndexes.put("RSM",
        findValidStartFlagLocation(hexString, SupportedMessageType.RSM.getStartFlag()));

    int lowestIndex = Integer.MAX_VALUE;
    String messageType = "";
    for (String key : flagIndexes.keySet()) {
      if (flagIndexes.get(key) == -1) {
        log.debug("This message is not of type {}", key);
        continue;
      }
      if (flagIndexes.get(key) < lowestIndex) {
        messageType = key;
        lowestIndex = flagIndexes.get(key);
      }
    }
    return messageType;
  }

  /**
   * Determines message type from raw packet bytes without hex-encoding the payload.
   *
   * @param packet payload bytes (already trimmed to {@code DatagramPacket} length)
   * @return message type name such as {@code "BSM"}, or empty string if unknown
   */
  public static String determinePacketType(byte[] packet) {
    int lowestIndex = Integer.MAX_VALUE;
    String messageType = "";
    for (SupportedMessageType type : SupportedMessageType.values()) {
      int index = findValidStartFlagLocation(packet, type.getStartFlagBytes());
      if (index == -1) {
        continue;
      }
      if (index < lowestIndex) {
        messageType = type.name();
        lowestIndex = index;
      }
    }
    return messageType;
  }

  /**
   * Searches for the location of the given start flag in the provided hex string and ensures it is
   * on an even numbered byte. If the start flag is found at the beginning of the string or not
   * found at all, it returns immediately. Otherwise, it continues searching from the fifth
   * position. The method ensures that the found start flag is located on an even byte boundary.
   *
   * @param hexString the string representation of the message in hexadecimal format where the
   *        search for the start flag will be conducted.
   * @param startFlag the specific flag pattern to locate within the given hex string, indicating
   *        the start of a valid message.
   * @return the index of the start flag within the hex string if found, and located on an even byte
   *         boundary; -1 if not found.
   */
  public static int findValidStartFlagLocation(String hexString, String startFlag) {
    int index = hexString.indexOf(startFlag);

    // If the message has a header, make sure not to misidentify the message by the header
    if (index == 0 || index == -1) {
      return index;
    } else {
      index = hexString.indexOf(startFlag, 4);
    }

    // Make sure start flag is on an even numbered byte
    while (index != -1 && index % 2 != 0) {
      index = hexString.indexOf(startFlag, index + 1);
    }
    return index;
  }

  /**
   * Byte-oriented start-flag search. Matches the hex variant's semantics: accept a hit at offset 0,
   * otherwise search from byte offset 2 (skipping a possible 2-byte header prefix).
   *
   * @return byte offset of the start flag, or {@code -1} if not found
   */
  public static int findValidStartFlagLocation(byte[] data, byte[] startFlag) {
    int index = indexOf(data, startFlag, 0, data.length);
    if (index == 0 || index == -1) {
      return index;
    }
    return indexOf(data, startFlag, 2, data.length);
  }

  private static int indexOf(byte[] data, byte[] pattern, int from, int toExclusive) {
    if (data == null || pattern == null || pattern.length == 0
        || from < 0 || toExclusive > data.length || from > toExclusive) {
      return -1;
    }
    int lastStart = toExclusive - pattern.length;
    outer:
    for (int i = from; i <= lastStart; i++) {
      for (int j = 0; j < pattern.length; j++) {
        if (data[i + j] != pattern[j]) {
          continue outer;
        }
      }
      return i;
    }
    return -1;
  }

}
