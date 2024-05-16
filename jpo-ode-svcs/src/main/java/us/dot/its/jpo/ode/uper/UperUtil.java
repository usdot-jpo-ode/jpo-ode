package us.dot.its.jpo.ode.uper;

import java.util.HashMap;

import org.apache.tomcat.util.buf.HexUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.dot.its.jpo.ode.model.OdeMsgPayload;
import us.dot.its.jpo.ode.util.JsonUtils;
import us.dot.its.jpo.ode.util.JsonUtils.JsonUtilsException;

public class UperUtil {
    private static Logger logger = LoggerFactory.getLogger(UperUtil.class);

    // start flags for BSM, TIM, MAP, SPAT, SRM, SSM, and PSM
    private final static String BSM_START_FLAG = "0014";
    private final static String TIM_START_FLAG = "001f";
    private final static String SPAT_START_FLAG = "0013";
    private final static String SSM_START_FLAG = "001e";
    private final static String SRM_START_FLAG = "001d";
    private final static String MAP_START_FLAG = "0012"; 
    private final static String PSM_START_FLAG = "0020";

    // Strips the IEEE 1609.2 security header (if it exists) and returns the payload
    public static String stripDot2Header(String hexString, String payload_start_flag) {
        hexString = hexString.toLowerCase();
        int startIndex = hexString.indexOf(payload_start_flag);
        if (startIndex == -1)
            return "BAD DATA";
        return hexString.substring(startIndex, hexString.length());
    }

    /*
     * Strips the 1609.3 and unsigned 1609.2 headers if they are present.
     * Will return the payload with a signed 1609.2 header if it is present.
     * Otherwise, returns just the payload.
     */
    public static byte[] stripDot3Header(byte[] packet, HashMap<String, String> msgStartFlags) {
        String hexString = HexUtils.toHexString(packet);
        String hexPacketParsed = "";

        for (String start_flag : msgStartFlags.values()) {
            int payloadStartIndex = hexString.indexOf(start_flag);

            if (payloadStartIndex == -1)
                continue;

            String headers = hexString.substring(0, payloadStartIndex);
            String payload = hexString.substring(payloadStartIndex, hexString.length());
            
            // Look for the index of the start flag of a signed 1609.2 header, if one exists
            int signedDot2StartIndex = headers.indexOf("038100");
            if (signedDot2StartIndex == -1)
                hexPacketParsed = payload;
            else
                hexPacketParsed = headers.substring(signedDot2StartIndex, headers.length()) + payload;
            break;
        }

        if (hexPacketParsed.equals("")) {
            hexPacketParsed = hexString;
            logger.debug("Packet is not a BSM, TIM or Map message: " + hexPacketParsed);
        }

        return HexUtils.fromHexString(hexPacketParsed);
    }

    /*
     * Strips the 1609.3 and unsigned 1609.2 headers if they are present.
     * Will return the payload with a signed 1609.2 header if it is present.
     * Otherwise, returns just the payload.
     */
    public static String stripDot3Header(String hexString, String payload_start_flag) {
        int payloadStartIndex = hexString.indexOf(payload_start_flag);
        String headers = hexString.substring(0, payloadStartIndex);
        String payload = hexString.substring(payloadStartIndex, hexString.length());
        // Look for the index of the start flag of a signed 1609.2 header
        int signedDot2StartIndex = headers.indexOf("038100");
        if (signedDot2StartIndex == -1)
            return payload;
        else
            return headers.substring(signedDot2StartIndex, headers.length()) + payload;
    }

    	/**
		* Determines the message type based off the most likely start flag
		* 
		* @param payload The OdeMsgPayload to check the content of.
		*/
	public static String determineMessageType(OdeMsgPayload payload) {
		String messageType = "";
		try {
			JSONObject payloadJson = JsonUtils.toJSONObject(payload.getData().toJson());
			String hexString = payloadJson.getString("bytes").toLowerCase();

			HashMap<String, Integer> flagIndexes = new HashMap<String, Integer>();
			flagIndexes.put("MAP", hexString.indexOf(MAP_START_FLAG));
			flagIndexes.put("TIM", hexString.indexOf(TIM_START_FLAG));
			flagIndexes.put("SSM", hexString.indexOf(SSM_START_FLAG));
			flagIndexes.put("PSM", hexString.indexOf(PSM_START_FLAG));
			flagIndexes.put("SRM", hexString.indexOf(SRM_START_FLAG));

			int lowestIndex = Integer.MAX_VALUE;
			for (String key : flagIndexes.keySet()) {
				if (flagIndexes.get(key) == -1) {
					logger.debug("This message is not of type " + key);
					continue;
				}
				if (flagIndexes.get(key) < lowestIndex) {
					messageType = key;
					lowestIndex = flagIndexes.get(key);
				}
			}
		} catch (JsonUtilsException e) {
			logger.error("JsonUtilsException while checking message header. Stacktrace: " + e.toString());
		}
		return messageType;
	}

    // Get methods for message start flags
    public static String getBsmStartFlag() {
        return BSM_START_FLAG;
    }

    public static String getTimStartFlag() {
        return TIM_START_FLAG;
    }

    public static String getSpatStartFlag() {
        return SPAT_START_FLAG;
    }

    public static String getSsmStartFlag() {
        return SSM_START_FLAG;
    }

    public static String getSrmStartFlag() {
        return SRM_START_FLAG;
    }

    public static String getMapStartFlag() {
        return MAP_START_FLAG;
    }

    public static String getPsmStartFlag() {
        return PSM_START_FLAG;
    }

    public static String getStartFlag(String msgType) {
        switch (msgType) {
        case "BSM":
            return BSM_START_FLAG;
        case "TIM":
            return TIM_START_FLAG;
        case "SPAT":
            return SPAT_START_FLAG;
        case "SSM":
            return SSM_START_FLAG;
        case "SRM":
            return SRM_START_FLAG;
        case "MAP":
            return MAP_START_FLAG;
        case "PSM":
            return PSM_START_FLAG;
        default:
            return null;
        }
    }
}
