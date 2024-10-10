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
    private static final String BSM_START_FLAG = "0014";
    private static final String TIM_START_FLAG = "001f";
    private static final String SPAT_START_FLAG = "0013";
    private static final String SSM_START_FLAG = "001e";
    private static final String SRM_START_FLAG = "001d";
    private static final String MAP_START_FLAG = "0012"; 
    private static final String PSM_START_FLAG = "0020";

    public enum SupportedMessageTypes {
        BSM, TIM, SPAT, SSM, SRM, MAP, PSM
    }

    // Strips the IEEE 1609.2 security header (if it exists) and returns the payload
    public static String stripDot2Header(String hexString, String payload_start_flag) {
        hexString = hexString.toLowerCase();
        int startIndex = findValidStartFlagLocation(hexString, payload_start_flag);
        if (startIndex == -1)
            return "BAD DATA";
        String strippedPayload = stripTrailingZeros(hexString.substring(startIndex, hexString.length()));
        return strippedPayload;
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
            int payloadStartIndex = findValidStartFlagLocation(hexString, start_flag);
            if (payloadStartIndex == -1){
                continue;
            }
                
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
        } else {
            logger.debug("Base packet: " + hexPacketParsed);
            hexPacketParsed = stripTrailingZeros(hexPacketParsed);
            logger.debug("Stripped packet: " + hexPacketParsed);
        }
        return HexUtils.fromHexString(hexPacketParsed);
    }

    /*
     * Strips the 1609.3 and unsigned 1609.2 headers if they are present.
     * Will return the payload with a signed 1609.2 header if it is present.
     * Otherwise, returns just the payload.
     */
    public static String stripDot3Header(String hexString, String payload_start_flag) {
        int payloadStartIndex = findValidStartFlagLocation(hexString,payload_start_flag);
        String headers = hexString.substring(0, payloadStartIndex);
        String payload = hexString.substring(payloadStartIndex, hexString.length());
        logger.debug("Base payload: " + payload);
        String strippedPayload = stripTrailingZeros(payload);
        logger.debug("Stripped payload: " + strippedPayload);
        // Look for the index of the start flag of a signed 1609.2 header
        int signedDot2StartIndex = headers.indexOf("038100");
        if (signedDot2StartIndex == -1)
            return strippedPayload;
        else
            return headers.substring(signedDot2StartIndex, headers.length()) + strippedPayload;
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
            hexString = stripTrailingZeros(hexString);
            messageType = determineHexPacketType(hexString);

		} catch (JsonUtilsException e) {
			logger.error("JsonUtilsException while checking message header. Stacktrace: " + e.toString());
		}
		return messageType;
	}

    public static String determineHexPacketType(String hexString){

        String messageType = "";
        HashMap<String, Integer> flagIndexes = new HashMap<String, Integer>();
        
        flagIndexes.put("MAP", findValidStartFlagLocation(hexString, MAP_START_FLAG));
        flagIndexes.put("SPAT", findValidStartFlagLocation(hexString, SPAT_START_FLAG));
	    flagIndexes.put("TIM", findValidStartFlagLocation(hexString, TIM_START_FLAG));
        flagIndexes.put("BSM", findValidStartFlagLocation(hexString, BSM_START_FLAG));
        flagIndexes.put("SSM", findValidStartFlagLocation(hexString, SSM_START_FLAG));
        flagIndexes.put("PSM", findValidStartFlagLocation(hexString, PSM_START_FLAG));
        flagIndexes.put("SRM", findValidStartFlagLocation(hexString, SRM_START_FLAG));

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
        return messageType;
    }

    public static int findValidStartFlagLocation(String hexString, String startFlag){
        int index = hexString.indexOf(startFlag);

        // If the message has a header, make sure not to missidentify the message by the header
        
        if(index == 0 || index == -1){
            return index;
        }
        else{
            index = hexString.indexOf(startFlag,4); 
        }
		
        // Make sure start flag is on an even numbered byte
        while(index != -1 && index %2 != 0){
            index = hexString.indexOf(startFlag, index+1);
        }
        return index;
    }

    
    /**
		* Trims extra `00` bytes off of the end of an ASN1 payload string
		* This is remove the padded bytes added to the payload when receiving ASN1 payloads
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

    public static String getStartFlag(SupportedMessageTypes msgType) {
        switch (msgType) {
        case SupportedMessageTypes.BSM:
            return BSM_START_FLAG;
        case SupportedMessageTypes.TIM:
            return TIM_START_FLAG;
        case SupportedMessageTypes.SPAT:
            return SPAT_START_FLAG;
        case SupportedMessageTypes.SSM:
            return SSM_START_FLAG;
        case SupportedMessageTypes.SRM:
            return SRM_START_FLAG;
        case SupportedMessageTypes.MAP:
            return MAP_START_FLAG;
        case SupportedMessageTypes.PSM:
            return PSM_START_FLAG;
        default:
            return null;
        }
    }
}
