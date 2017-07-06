package us.dot.its.jpo.ode.model;

import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;

public class OdeVehicleData extends OdeData {

    private static final long serialVersionUID = 7061315628111448390L;

    private J2735Bsm bsm;

    public OdeVehicleData() {
        super();
        this.bsm = new J2735Bsm();
    }

    public OdeVehicleData(J2735Bsm bsm) {
        super();
        this.bsm = bsm;
    }

    public J2735Bsm getBsm() {
        return bsm;
    }

    public void setBsm(J2735Bsm bsm) {
        this.bsm = bsm;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((bsm == null) ? 0 : bsm.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        OdeVehicleData other = (OdeVehicleData) obj;
        if (bsm == null) {
            if (other.bsm != null)
                return false;
        } else if (!bsm.equals(other.bsm))
            return false;
        return true;
    }
    
    
}
