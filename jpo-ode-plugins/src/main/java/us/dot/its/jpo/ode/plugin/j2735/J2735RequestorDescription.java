package us.dot.its.jpo.ode.plugin.j2735;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735RequestorDescription extends Asn1Object {
    
    private static final long serialVersionUID = 1L;
    private J2735VehicleID id;
    private J2735RequestorType type;
    private J2735RequestorPositionVector position;
    private String name;
    private String routeName;
    private J2735BitString transitStatus;
    private J2735TransitVehicleOccupancy transitOccupancy;
    private Integer transitSchedule;

    public J2735VehicleID getId() {
        return id;
    }

    public void setId(J2735VehicleID id) {
        this.id = id;
    }

    public J2735RequestorType getType() {
        return type;
    }

    public void setType(J2735RequestorType type) {
        this.type = type;
    }

    public J2735RequestorPositionVector getPosition() {
        return position;
    }

    public void setPosition(J2735RequestorPositionVector position) {
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public J2735BitString getTransitStatus() {
        return transitStatus;
    }

    public void setTransitStatus(J2735BitString transitStatus) {
        this.transitStatus = transitStatus;
    }

    public J2735TransitVehicleOccupancy getTransitOccupancy() {
        return transitOccupancy;
    }

    public void setTransitOccupancy(J2735TransitVehicleOccupancy transitOccupancy) {
        this.transitOccupancy = transitOccupancy;
    }

    public Integer getTransitSchedule() {
        return transitSchedule;
    }

    public void setTransitSchedule(Integer transitSchedule) {
        this.transitSchedule = transitSchedule;
    }
}
