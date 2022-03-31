package us.dot.its.jpo.ode.plugin.j2735;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735SignalRequestPackage extends Asn1Object {
    
    private static final long serialVersionUID = 1L;
    private J2735SignalRequest request;
    private Integer minute;
    private Integer second;
    private Integer duration;

    public J2735SignalRequest getRequest() {
        return request;
    }

    public void setRequest(J2735SignalRequest request) {
        this.request = request;
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
}

