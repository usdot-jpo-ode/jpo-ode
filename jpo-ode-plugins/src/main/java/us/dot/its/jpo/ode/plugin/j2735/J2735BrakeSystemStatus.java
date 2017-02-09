package us.dot.its.jpo.ode.plugin.j2735;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735BrakeSystemStatus extends Asn1Object {
    private static final long serialVersionUID = 1L;

    private J2735BitString wheelBrakes;
    private String traction;
    private String abs;
    private String scs;
    private String brakeBoost;
    private String auxBrakes;

    public J2735BitString getWheelBrakes() {
        return wheelBrakes;
    }

    public void setWheelBrakes(J2735BitString wheelBrakes) {
        this.wheelBrakes = wheelBrakes;
    }

    public String getTraction() {
        return traction;
    }

    public void setTraction(String traction) {
        this.traction = traction;
    }

    public String getAbs() {
        return abs;
    }

    public void setAbs(String abs) {
        this.abs = abs;
    }

    public String getScs() {
        return scs;
    }

    public void setScs(String scs) {
        this.scs = scs;
    }

    public String getBrakeBoost() {
        return brakeBoost;
    }

    public void setBrakeBoost(String brakeBoost) {
        this.brakeBoost = brakeBoost;
    }

    public String getAuxBrakes() {
        return auxBrakes;
    }

    public void setAuxBrakes(String auxBrakes) {
        this.auxBrakes = auxBrakes;
    }

}
