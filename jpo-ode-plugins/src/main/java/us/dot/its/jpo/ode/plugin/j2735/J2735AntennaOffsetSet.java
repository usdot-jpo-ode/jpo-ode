package us.dot.its.jpo.ode.plugin.j2735;

import java.math.BigDecimal;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735AntennaOffsetSet extends Asn1Object {
	private static final long serialVersionUID = 1L;
	
	private BigDecimal antOffsetX;
	private BigDecimal antOffsetY;
	private BigDecimal antOffsetZ;
    public BigDecimal getAntOffsetX() {
        return antOffsetX;
    }
    public void setAntOffsetX(BigDecimal antOffsetX) {
        this.antOffsetX = antOffsetX;
    }
    public BigDecimal getAntOffsetY() {
        return antOffsetY;
    }
    public void setAntOffsetY(BigDecimal antOffsetY) {
        this.antOffsetY = antOffsetY;
    }
    public BigDecimal getAntOffsetZ() {
        return antOffsetZ;
    }
    public void setAntOffsetZ(BigDecimal antOffsetZ) {
        this.antOffsetZ = antOffsetZ;
    }

}
