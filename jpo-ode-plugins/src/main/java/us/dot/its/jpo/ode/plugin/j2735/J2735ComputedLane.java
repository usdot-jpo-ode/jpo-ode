package us.dot.its.jpo.ode.plugin.j2735;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735ComputedLane extends Asn1Object {
    
    private static final long serialVersionUID = 1L;
		private Integer referenceLaneId;
    private Integer offsetXaxis;
    private Integer offsetYaxis;
    private Integer rotateXY;
    private Integer scaleXaxis;
    private Integer scaleYaxis;

	public Integer getReferenceLaneId() {
		return referenceLaneId;
	}

	public void setReferenceLaneId(Integer referenceLaneId) {
		this.referenceLaneId = referenceLaneId;
	}

    public Integer getOffsetXaxis() {
		return offsetXaxis;
	}

	public void setOffsetXaxis(Integer offsetXaxis) {
		this.offsetXaxis = offsetXaxis;
	}
    
    public Integer getOffsetYaxis() {
		return offsetYaxis;
	}

	public void setOffsetYaxis(Integer offsetYaxis) {
		this.offsetYaxis = offsetYaxis;
	}

    public Integer getRotateXY() {
		return rotateXY;
	}

	public void setRotateXY(Integer rotateXY) {
		this.rotateXY = rotateXY;
	}

    public Integer getScaleXaxis() {
		return scaleXaxis;
	}

	public void setScaleXaxis(Integer scaleXaxis) {
		this.scaleXaxis = scaleXaxis;
	}

    public Integer getScaleYaxis() {
		return scaleYaxis;
	}

	public void setScaleYaxis(Integer scaleYaxis) {
		this.scaleYaxis = scaleYaxis;
	}
}
