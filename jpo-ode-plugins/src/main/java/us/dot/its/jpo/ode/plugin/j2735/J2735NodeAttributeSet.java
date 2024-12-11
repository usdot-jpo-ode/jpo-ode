package us.dot.its.jpo.ode.plugin.j2735;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735NodeAttributeSet extends Asn1Object {
	private static final long serialVersionUID = 1L;

	private J2735NodeAttribute[] localNode;
	private J2735SegmentAttribute[] disabled;
	private J2735SegmentAttribute[] enabled;
	private J2735LaneDataAttribute[] data;
	private Integer dWidth;
	private Integer dElevation;

	public J2735NodeAttribute[] getLocalNode() {
		return localNode;
	}

	public void setLocalNode(J2735NodeAttribute[] localNode) {
		this.localNode = localNode;
	}

	public J2735SegmentAttribute[] getDisabled() {
		return disabled;
	}

	public void setDisabled(J2735SegmentAttribute[] disabled) {
		this.disabled = disabled;
	}

	public J2735SegmentAttribute[] getEnabled() {
		return enabled;
	}

	public void setEnabled(J2735SegmentAttribute[] enabled) {
		this.enabled = enabled;
	}

	public J2735LaneDataAttribute[] getData() {
		return data;
	}

	public void setData(J2735LaneDataAttribute[] data) {
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
