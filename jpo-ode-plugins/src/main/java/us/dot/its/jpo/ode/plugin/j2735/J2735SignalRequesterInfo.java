package us.dot.its.jpo.ode.plugin.j2735;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735SignalRequesterInfo extends Asn1Object {
    
    private static final long serialVersionUID = 1L;
    private J2735VehicleID id;
    private Integer request;
    private Integer sequenceNumber;
    private J2735BasicVehicleRole role;
    private J2735RequestorType typeData;

    public J2735VehicleID getId() {
        return id;
    }

    public void setId(J2735VehicleID id) {
        this.id = id;
    }

    public Integer getRequest() {
        return request;
    }

    public void setRequest(Integer request) {
        this.request = request;
    }

    public Integer getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(Integer sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public J2735BasicVehicleRole getRole() {
        return role;
    }

    public void setRole(J2735BasicVehicleRole role) {
        this.role = role;
    }

    public J2735RequestorType getTypeData() {
        return typeData;
    }

    public void setTypeData(J2735RequestorType typeData) {
        this.typeData = typeData;
    }
}
