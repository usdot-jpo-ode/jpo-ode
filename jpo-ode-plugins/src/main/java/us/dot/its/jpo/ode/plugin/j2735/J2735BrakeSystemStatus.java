package us.dot.its.jpo.ode.plugin.j2735;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735BrakeSystemStatus extends Asn1Object {
    private static final long serialVersionUID = 1L;

    private J2735BrakeAppliedStatus wheelBrakes;
    private String traction;
    private String abs;
    private String scs;
    private String brakeBoost;
    private String auxBrakes;

    public J2735BrakeAppliedStatus getWheelBrakes() {
        return wheelBrakes;
    }

    public void setWheelBrakes(J2735BrakeAppliedStatus wheelBrakes) {
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((abs == null) ? 0 : abs.hashCode());
        result = prime * result + ((auxBrakes == null) ? 0 : auxBrakes.hashCode());
        result = prime * result + ((brakeBoost == null) ? 0 : brakeBoost.hashCode());
        result = prime * result + ((scs == null) ? 0 : scs.hashCode());
        result = prime * result + ((traction == null) ? 0 : traction.hashCode());
        result = prime * result + ((wheelBrakes == null) ? 0 : wheelBrakes.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        J2735BrakeSystemStatus other = (J2735BrakeSystemStatus) obj;
        if (abs == null) {
            if (other.abs != null)
                return false;
        } else if (!abs.equals(other.abs))
            return false;
        if (auxBrakes == null) {
            if (other.auxBrakes != null)
                return false;
        } else if (!auxBrakes.equals(other.auxBrakes))
            return false;
        if (brakeBoost == null) {
            if (other.brakeBoost != null)
                return false;
        } else if (!brakeBoost.equals(other.brakeBoost))
            return false;
        if (scs == null) {
            if (other.scs != null)
                return false;
        } else if (!scs.equals(other.scs))
            return false;
        if (traction == null) {
            if (other.traction != null)
                return false;
        } else if (!traction.equals(other.traction))
            return false;
        if (wheelBrakes == null) {
            if (other.wheelBrakes != null)
                return false;
        } else if (!wheelBrakes.equals(other.wheelBrakes))
            return false;
        return true;
    }

}
