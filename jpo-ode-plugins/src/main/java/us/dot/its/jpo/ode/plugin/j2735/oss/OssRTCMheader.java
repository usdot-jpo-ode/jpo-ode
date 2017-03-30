package us.dot.its.jpo.ode.plugin.j2735.oss;

import us.dot.its.jpo.ode.j2735.dsrc.RTCMheader;
import us.dot.its.jpo.ode.plugin.j2735.J2735RTCMheader;

public class OssRTCMheader {

    private OssRTCMheader() {
       throw new UnsupportedOperationException();
    }

    public static J2735RTCMheader genericRTCMheader(RTCMheader rtcmHeader) {
        J2735RTCMheader header = new J2735RTCMheader();

        header.setOffsetSet(OssAntennaOffsetSet.genericAntennaOffsetSet(rtcmHeader.offsetSet));
        header.setStatus(OssGNSSstatus.genericGNSSstatus(rtcmHeader.status));

        return header;
    }

}
