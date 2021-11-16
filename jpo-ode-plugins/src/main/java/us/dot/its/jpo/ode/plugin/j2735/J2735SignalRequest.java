package us.dot.its.jpo.ode.plugin.j2735;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735SignalRequest extends Asn1Object {
    
    private static final long serialVersionUID = 1L;
    private J2735IntersectionReferenceID id;
    private Integer requestID;
    private J2735PriorityRequestType requestType;
    private J2735IntersectionAccessPoint inBoundLane;
    private J2735IntersectionAccessPoint outBoundLane;

    public J2735IntersectionReferenceID getId() {
        return id;
    }

    public void setId(J2735IntersectionReferenceID id) {
        this.id = id;
    }

    public Integer getRequestID() {
        return requestID;
    }

    public void setRequestID(Integer requestID) {
        this.requestID = requestID;
    }

    public J2735PriorityRequestType getRequestType() {
        return requestType;
    }

    public void setRequestType(J2735PriorityRequestType requestType) {
        this.requestType = requestType;
    }

    public J2735IntersectionAccessPoint getInBoundLane() {
        return inBoundLane;
    }

    public void setInBoundLane(J2735IntersectionAccessPoint inBoundLane) {
        this.inBoundLane = inBoundLane;
    }

    public J2735IntersectionAccessPoint getOutBoundLane() {
        return outBoundLane;
    }

    public void setOutBoundLane(J2735IntersectionAccessPoint outBoundLane) {
        this.outBoundLane = outBoundLane;
    }
}
