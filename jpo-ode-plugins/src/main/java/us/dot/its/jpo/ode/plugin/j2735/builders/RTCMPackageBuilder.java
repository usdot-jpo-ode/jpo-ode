package us.dot.its.jpo.ode.plugin.j2735.builders;

import java.util.Iterator;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735RTCMPackage;

public class RTCMPackageBuilder {

    private RTCMPackageBuilder() {
       throw new UnsupportedOperationException();
    }

    public static J2735RTCMPackage genericRTCMPackage(JsonNode rtcmPackage) {
        J2735RTCMPackage rtcm = new J2735RTCMPackage();

        Iterator<JsonNode> iter = rtcmPackage.get("msgs").elements();

        while (iter.hasNext()) {
            rtcm.getMsgs().add(iter.next().asText());
        }
        // Optional element
        if (rtcmPackage.get("rtcmHeader") != null) {
            rtcm.setRtcmHeader(RTCMheaderBuilder.genericRTCMheader(rtcmPackage.get("rtcmHeader")));
        }

        return rtcm;
    }

}
