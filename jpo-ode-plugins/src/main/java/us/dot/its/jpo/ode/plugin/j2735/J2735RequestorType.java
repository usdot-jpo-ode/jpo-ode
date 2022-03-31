package us.dot.its.jpo.ode.plugin.j2735;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735RequestorType extends Asn1Object {

    private static final long serialVersionUID = 1L;
    private J2735BasicVehicleRole role;
    private J2735RequestSubRole subrole;
    private J2735RequestImportanceLevel request;
    private Integer iso3883;
    private J2735VehicleType hpmsType;

    public J2735BasicVehicleRole getRole() {
        return role;
    }

    public void setRole(J2735BasicVehicleRole role) {
        this.role = role;
    }

    public J2735RequestSubRole getSubrole() {
        return subrole;
    }

    public void setSubrole(J2735RequestSubRole subrole) {
        this.subrole = subrole;
    }

    public J2735RequestImportanceLevel getRequest() {
        return request;
    }

    public void setRequest(J2735RequestImportanceLevel request) {
        this.request = request;
    }

    public Integer getIso3883() {
        return iso3883;
    }

    public void setIso3883(Integer iso3883) {
        this.iso3883 = iso3883;
    }

    public J2735VehicleType getHpmsType() {
        return hpmsType;
    }

    public void setHpmsType(J2735VehicleType hpmsType) {
        this.hpmsType = hpmsType;
    }
}
