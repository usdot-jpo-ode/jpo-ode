package us.dot.its.jpo.ode.plugin.j2735.builders;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735Tim;
import us.dot.its.jpo.ode.plugin.j2735.J2735TravelerDataFrame;

public class TIMBuilder {
    private TIMBuilder() {
		throw new UnsupportedOperationException();
	}

	public static J2735Tim genericTim(JsonNode TimMessage) {
		J2735Tim genericTim = new J2735Tim();

		JsonNode msgCnt = TimMessage.get("msgCnt");
		if (msgCnt != null) {
			genericTim.setMsgCnt(msgCnt.asText());
		}

		JsonNode timeStamp = TimMessage.get("timeStamp");
		if (timeStamp != null) {
			genericTim.setTimeStamp(timeStamp.asText());
		}

		JsonNode packetID = TimMessage.get("packetID");
		if (packetID != null) {
			genericTim.setPacketID(packetID.asText());
		}

        // The decoder makes a null URL a literal string value of "null" when it is not specified
        JsonNode urlB = TimMessage.get("urlB");
        if (urlB != null) {
            String urlBValue = urlB.asText();
            if ("null".equals(urlBValue)) {
                urlBValue = null;
            }
            genericTim.setUrlB(urlBValue);
        }

        JsonNode dataFrames = TimMessage.get("dataFrames");
		if (dataFrames != null) {
            JsonNode travelerDataFrame = dataFrames.get("TravelerDataFrame");
            if (travelerDataFrame != null) {
                List<J2735TravelerDataFrame> dfList = new ArrayList<>();

                if (travelerDataFrame.isArray()) {
                    Iterator<JsonNode> elements = travelerDataFrame.elements();
    
                    while (elements.hasNext()) {
                        dfList.add(TravelerDataFrameBuilder.genericTravelerDataFrame(elements.next()));
                    }
                } else {
                    dfList.add(TravelerDataFrameBuilder.genericTravelerDataFrame(travelerDataFrame));
                }

                genericTim.setDataFrames(dfList.toArray(new J2735TravelerDataFrame[0]));
            }
		}

		return genericTim;
	}
}
