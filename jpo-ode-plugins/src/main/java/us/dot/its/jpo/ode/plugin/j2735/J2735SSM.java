package us.dot.its.jpo.ode.plugin.j2735;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735SSM extends Asn1Object {
    
    private static final long serialVersionUID = 1L;
    private Integer timeStamp;
    private Integer second;
    private Integer sequenceNumber;
    private J2735SignalStatusList status;

    public Integer getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Integer timeStamp) {
        this.timeStamp = timeStamp;
    }

    public Integer getSecond() {
        return second;
    }

    public void setSecond(Integer second) {
        this.second = second;
    }

    public Integer getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(Integer sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public J2735SignalStatusList getStatus() {
        return status;
    }

    public void setStatus(J2735SignalStatusList status) {
        this.status = status;
    }
}
