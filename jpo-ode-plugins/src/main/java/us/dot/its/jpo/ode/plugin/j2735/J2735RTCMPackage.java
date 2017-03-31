package us.dot.its.jpo.ode.plugin.j2735;

import java.util.ArrayList;
import java.util.List;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735RTCMPackage extends Asn1Object {
    private static final long serialVersionUID = 1L;

    private List<String> msgs = new ArrayList<>();
    private J2735RTCMheader rtcmHeader;

    public List<String> getMsgs() {
        return msgs;
    }

    public void setMsgs(List<String> msgs) {
        this.msgs = msgs;
    }

    public J2735RTCMheader getRtcmHeader() {
        return rtcmHeader;
    }

    public void setRtcmHeader(J2735RTCMheader rtcmHeader) {
        this.rtcmHeader = rtcmHeader;
    }

}
