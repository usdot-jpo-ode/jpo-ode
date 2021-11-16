package us.dot.its.jpo.ode.plugin.j2735;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735NodeAttributeSetXY extends Asn1Object {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private J2735NodeAttributeXYList localNode;
	private J2735SegmentAttributeXYList disabled;
	private J2735SegmentAttributeXYList enabled;
	private J2735LaneDataAttributeList data;
	private Integer dWidth;
	private Integer dElevation;

	public J2735NodeAttributeXYList getLocalNode() {
		return localNode;
	}

	public void setLocalNode(J2735NodeAttributeXYList localNode) {
		this.localNode = localNode;
	}

	public J2735SegmentAttributeXYList getDisabled() {
		return disabled;
	}

	public void setDisabled(J2735SegmentAttributeXYList disabled) {
		this.disabled = disabled;
	}

	public J2735SegmentAttributeXYList getEnabled() {
		return enabled;
	}

	public void setEnabled(J2735SegmentAttributeXYList enabled) {
		this.enabled = enabled;
	}

	public J2735LaneDataAttributeList getData() {
		return data;
	}

	public void setData(J2735LaneDataAttributeList data) {
		this.data = data;
	}

	public Integer getdWidth() {
		return dWidth;
	}

	public void setdWidth(Integer dWidth) {
		this.dWidth = dWidth;
	}

	public Integer getdElevation() {
		return dElevation;
	}

	public void setdElevation(Integer dElevation) {
		this.dElevation = dElevation;
	}

}
