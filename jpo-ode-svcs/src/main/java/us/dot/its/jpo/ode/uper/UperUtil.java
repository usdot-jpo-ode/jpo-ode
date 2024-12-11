package us.dot.its.jpo.ode.uper;

import java.util.HashMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.buf.HexUtils;
import org.json.JSONObject;
import us.dot.its.jpo.ode.model.OdeMsgPayload;
import us.dot.its.jpo.ode.util.JsonUtils;
import us.dot.its.jpo.ode.util.JsonUtils.JsonUtilsException;

/**
 * Utility class for handling and manipulating hexadecimal strings representing network packet data,
 * particularly those adhering to IEEE 1609.2 and 1609.3 standards.
 */
@Slf4j
public class UperUtil {

  private UperUtil() {
    throw new UnsupportedOperationException();
  }

  /**
   * Strips the IEEE 1609.2 security header (if it exists) and returns the payload from a given
   * hexadecimal string. The method searches for a specified start flag that indicates the beginning
   * of the payload.
   *
   * @param hexString        the input hexadecimal string from which the IEEE 1609.2 security header
   *                         needs to be stripped.
   * @param payloadStartFlag the start flag indicating the beginning of the payload.
   *
   * @return a string representing the payload without the IEEE 1609.2 security header, if the start
   *         flag is found.
   *
   * @throws StartFlagNotFoundException if the specified start flag is not found within the
   *                                    hexadecimal string.
   */
  public static String stripDot2Header(String hexString, String payloadStartFlag)
      throws StartFlagNotFoundException {
    hexString = hexString.toLowerCase();
    int startIndex = findValidStartFlagLocation(hexString, payloadStartFlag);
    if (startIndex == -1) {
      throw new StartFlagNotFoundException(
          "Start flag" + payloadStartFlag + " not found in message");
    }
    return stripTrailingZeros(hexString.substring(startIndex));
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
    } else {
      log.debug("Base packet: {}", hexPacketParsed);
      hexPacketParsed = stripTrailingZeros(hexPacketParsed);
      log.debug("Stripped packet: {}", hexPacketParsed);
    }
    return HexUtils.fromHexString(hexPacketParsed);
  }

  /**
   * Strips the 1609.3 and unsigned 1609.2 headers if they are present. Will return the payload with
   * a signed 1609.2 header if it is present. Otherwise, returns just the payload.
   */
  public static String stripDot3Header(String hexString, String payloadStartFlag) {
    int payloadStartIndex = findValidStartFlagLocation(hexString, payloadStartFlag);
    String headers = hexString.substring(0, payloadStartIndex);
    String payload = hexString.substring(payloadStartIndex);
    log.debug("Base payload: {}", payload);
    String strippedPayload = stripTrailingZeros(payload);
    log.debug("Stripped payload: {}", strippedPayload);
    // Look for the index of the start flag of a signed 1609.2 header
    int signedDot2StartIndex = headers.indexOf("038100");
    if (signedDot2StartIndex == -1) {
      return strippedPayload;
    } else {
      return headers.substring(signedDot2StartIndex) + strippedPayload;
    }
  }

  /**
   * Determines the message type based off the most likely start flag.
   *
   * @param payload The OdeMsgPayload to check the content of.
   */
  public static String determineMessageType(OdeMsgPayload payload) {
    String messageType = "";
    try {
      JSONObject payloadJson = JsonUtils.toJSONObject(payload.getData().toJson());
      String hexString = payloadJson.getString("bytes").toLowerCase();
      hexString = stripTrailingZeros(hexString);
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
   *         "PSM", or "SRM". If no valid type is found, returns an empty string.
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
   * Searches for the location of the given start flag in the provided hex string and ensures it is
   * on an even numbered byte. If the start flag is found at the beginning of the string or not
   * found at all, it returns immediately. Otherwise, it continues searching from the fifth
   * position. The method ensures that the found start flag is located on an even byte boundary.
   *
   * @param hexString the string representation of the message in hexadecimal format where the
   *                  search for the start flag will be conducted.
   * @param startFlag the specific flag pattern to locate within the given hex string, indicating
   *                  the start of a valid message.
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
   * Trims extra `00` bytes off of the end of an ASN1 payload string. This removes the padded bytes
   * added to the payload when receiving ASN1 payloads and leaves one remaining byte of `00`s for
   * decoding.
   *
   * @param payload The OdeMsgPayload as a string to trim.
   */
  public static String stripTrailingZeros(String payload) {
    // Remove trailing '0's
    while (payload.endsWith("0")) {
      payload = payload.substring(0, payload.length() - 1);
    }

    // Ensure the payload length is even
    if (payload.length() % 2 != 0) {
      payload += "0";
    }

    // Append '00' to ensure one remaining byte of '00's for decoding
    payload += "00";

    return payload;
  }
}
