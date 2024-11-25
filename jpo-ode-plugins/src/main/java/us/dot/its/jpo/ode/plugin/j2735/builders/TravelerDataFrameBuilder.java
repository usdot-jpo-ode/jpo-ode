package us.dot.its.jpo.ode.plugin.j2735.builders;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735GeographicalPath;
import us.dot.its.jpo.ode.plugin.j2735.J2735MsgId;
import us.dot.its.jpo.ode.plugin.j2735.J2735TravelerDataFrame;
import us.dot.its.jpo.ode.plugin.j2735.J2735TravelerInfoType;

public class TravelerDataFrameBuilder {
    private TravelerDataFrameBuilder() {
        throw new UnsupportedOperationException();
    }

    public static J2735TravelerDataFrame genericTravelerDataFrame(JsonNode travelerDataFrame) {
        J2735TravelerDataFrame genericTravelerDataFrame = new J2735TravelerDataFrame();

        JsonNode notUsed = travelerDataFrame.get("notUsed");
        if (notUsed != null) {
            genericTravelerDataFrame.setNotUsed(notUsed.asText());
        }

        JsonNode frameType = travelerDataFrame.get("frameType");
        if (frameType != null) {
            String frameTypeValue = frameType.fields().next().getKey();
            genericTravelerDataFrame.setFrameType(J2735TravelerInfoType.valueOf(frameTypeValue));
        }

        JsonNode msgId = travelerDataFrame.get("msgId");
        if (msgId != null) {
            J2735MsgId msgIdObj = new J2735MsgId();

            JsonNode furtherInfoID = msgId.get("furtherInfoID");
            if (furtherInfoID != null) {
                msgIdObj.setFurtherInfoId(furtherInfoID.asText());
            }

            JsonNode roadSignID = msgId.get("roadSignID");
            if (roadSignID != null) {
                msgIdObj.setRoadSignID(RoadSignIdBuilder.genericRoadSignId(roadSignID));
            }

            genericTravelerDataFrame.setMsgId(msgIdObj);
        }

        JsonNode startYear = travelerDataFrame.get("startYear");
        if (startYear != null) {
            genericTravelerDataFrame.setStartYear(startYear.asText());
        }

        JsonNode startTime = travelerDataFrame.get("startTime");
        if (startTime != null) {
            genericTravelerDataFrame.setStartTime(startTime.asText());
        }

        JsonNode durationTime = travelerDataFrame.get("durationTime");
        if (durationTime != null) {
            genericTravelerDataFrame.setDurationTime(durationTime.asText());
        }

        JsonNode priority = travelerDataFrame.get("priority");
        if (priority != null) {
            genericTravelerDataFrame.setPriority(priority.asText());
        }

        JsonNode notUsed1 = travelerDataFrame.get("notUsed1");
        if (notUsed1 != null) {
            genericTravelerDataFrame.setNotUsed1(notUsed1.asText());
        }

        JsonNode regions = travelerDataFrame.get("regions");
        if (regions != null) {
            JsonNode geographicalPath = regions.get("GeographicalPath");
            if (geographicalPath != null) {
                List<J2735GeographicalPath> gpList = new ArrayList<>();

                if (geographicalPath.isArray()) {
                    Iterator<JsonNode> elements = geographicalPath.elements();
    
                    while (elements.hasNext()) {
                        gpList.add(GeographicalPathBuilder.genericGeographicalPath(elements.next()));
                    }
                } else {
                    gpList.add(GeographicalPathBuilder.genericGeographicalPath(geographicalPath));
                }

                genericTravelerDataFrame.setRegions(gpList.toArray(new J2735GeographicalPath[0]));
            }
        }

        JsonNode notUsed2 = travelerDataFrame.get("notUsed2");
        if (notUsed2 != null) {
            genericTravelerDataFrame.setNotUsed2(notUsed2.asText());
        }

        JsonNode notUsed3 = travelerDataFrame.get("notUsed3");
        if (notUsed3 != null) {
            genericTravelerDataFrame.setNotUsed3(notUsed3.asText());
        }

        JsonNode content = travelerDataFrame.get("content");
        if (content != null) {
            genericTravelerDataFrame.setContent(ContentBuilder.genericContent(content));
        }

        // The decoder makes a null URL a literal string value of "null" when it is not specified
        JsonNode url = travelerDataFrame.get("url");
        if (url != null) {
            String urlValue = url.asText();
            if ("null".equals(urlValue)) {
                urlValue = null;
            }
            genericTravelerDataFrame.setUrl(urlValue);
        }

        return genericTravelerDataFrame;
    }
}
