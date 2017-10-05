package us.dot.its.jpo.ode.plugin.j2735.builders;

import java.util.Iterator;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735RTCMPackage;
import us.dot.its.jpo.ode.util.CodecUtils;

public class RTCMPackageBuilder {

    private RTCMPackageBuilder() {
       throw new UnsupportedOperationException();
    }

    public static J2735RTCMPackage genericRTCMPackage(JsonNode rtcmPackage) {
        J2735RTCMPackage rtcm = new J2735RTCMPackage();

        Iterator<JsonNode> iter = rtcmPackage.msgs.elements.iterator();

        while (iter.hasNext()) {
            rtcm.getMsgs().add(CodecUtils.toHex(iter.next().byteArrayValue()));
        }
        // Optional element
        if (rtcmPackage.rtcmHeader != null) {
            rtcm.setRtcmHeader(RTCMheaderBuilder.genericRTCMheader(rtcmPackage.rtcmHeader));
        }

        return rtcm;
    }

}
