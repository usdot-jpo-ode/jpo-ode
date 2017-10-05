package us.dot.its.jpo.ode.plugin.j2735.builders;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735RTCMheader;

public class RTCMheaderBuilder {

    private RTCMheaderBuilder() {
       throw new UnsupportedOperationException();
    }

    public static J2735RTCMheader genericRTCMheader(JsonNode rtcmHeader) {
        J2735RTCMheader header = new J2735RTCMheader();

        header.setOffsetSet(AntennaOffsetSetBuilder.genericAntennaOffsetSet(rtcmHeader.offsetSet));
        header.setStatus(GNSSstatusBuilder.genericGNSSstatus(rtcmHeader.status));

        return header;
    }

}
