package us.dot.its.jpo.ode.plugin.j2735;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735SRM extends Asn1Object {
    
    private static final long serialVersionUID = 1L;
    private Integer timeStamp;
    private Integer second;
    private Integer sequenceNumber;
    private J2735SignalRequestList requests;
    private J2735RequestorDescription requestor;

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

    public J2735SignalRequestList getRequests() {
        return requests;
    }

    public void setRequests(J2735SignalRequestList requests) {
        this.requests = requests;
    }

    public J2735RequestorDescription getRequestor() {
        return requestor;
    }

    public void setRequestor(J2735RequestorDescription requestor) {
        this.requestor = requestor;
    }
}
