package us.dot.its.jpo.ode.plugin.j2735.oss;

import java.util.Iterator;

import us.dot.its.jpo.ode.j2735.dsrc.RTCMPackage;
import us.dot.its.jpo.ode.j2735.dsrc.RTCMmessage;
import us.dot.its.jpo.ode.plugin.j2735.J2735RTCMPackage;
import us.dot.its.jpo.ode.util.CodecUtils;

public class OssRTCMPackage {

    private OssRTCMPackage() {
       throw new UnsupportedOperationException();
    }

    public static J2735RTCMPackage genericRTCMPackage(RTCMPackage theRTCM) {
        J2735RTCMPackage rtcm = new J2735RTCMPackage();

        Iterator<RTCMmessage> iter = theRTCM.msgs.elements.iterator();

        while (iter.hasNext()) {
            rtcm.getMsgs().add(CodecUtils.toHex(iter.next().byteArrayValue()));
        }
        // Optional element
        if (theRTCM.rtcmHeader != null) {
            rtcm.setRtcmHeader(OssRTCMheader.genericRTCMheader(theRTCM.rtcmHeader));
        }

        return rtcm;
    }

}
