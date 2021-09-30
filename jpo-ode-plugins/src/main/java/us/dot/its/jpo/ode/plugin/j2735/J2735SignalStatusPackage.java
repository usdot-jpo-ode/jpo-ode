package us.dot.its.jpo.ode.plugin.j2735;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735SignalStatusPackage extends Asn1Object {
    
    private static final long serialVersionUID = 1L;
    private J2735SignalRequesterInfo requester;
    private J2735IntersectionAccessPoint inboundOn;
    private J2735IntersectionAccessPoint outboundOn;
    private Integer minute;
    private Integer second;
    private Integer duration;
    private J2735PrioritizationResponseStatus status;

    public J2735SignalRequesterInfo getRequester() {
        return requester;
    }

    public void setRequester(J2735SignalRequesterInfo requester) {
        this.requester = requester;
    }

    public J2735IntersectionAccessPoint getInboundOn() {
        return inboundOn;
    }

    public void setInboundOn(J2735IntersectionAccessPoint inboundOn) {
        this.inboundOn = inboundOn;
    }

    public J2735IntersectionAccessPoint getOutboundOn() {
        return outboundOn;
    }

    public void setOutboundOn(J2735IntersectionAccessPoint outboundOn) {
        this.outboundOn = outboundOn;
    }

    public Integer getMinute() {
        return minute;
    }

    public void setMinute(Integer minute) {
        this.minute = minute;
    }

    public Integer getSecond() {
        return second;
    }

    public void setSecond(Integer second) {
        this.second = second;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public J2735PrioritizationResponseStatus getStatus() {
        return status;
    }

    public void setStatus(J2735PrioritizationResponseStatus status) {
        this.status = status;
    }
}
