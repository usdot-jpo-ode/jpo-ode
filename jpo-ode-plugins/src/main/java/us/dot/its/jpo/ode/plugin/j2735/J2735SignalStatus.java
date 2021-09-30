package us.dot.its.jpo.ode.plugin.j2735;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735SignalStatus extends Asn1Object {
    
    private static final long serialVersionUID = 1L;
    private Integer sequenceNumber;
    private J2735IntersectionReferenceID id;
    private J2735SignalStatusPackageList sigStatus;

    public Integer getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(Integer sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public J2735IntersectionReferenceID getId() {
        return id;
    }

    public void setId(J2735IntersectionReferenceID id) {
        this.id = id;
    }

    public J2735SignalStatusPackageList getSigStatus() {
        return sigStatus;
    }

    public void setSigStatus(J2735SignalStatusPackageList sigStatus) {
        this.sigStatus = sigStatus;
    }
}
