package us.dot.its.jpo.ode.plugin.j2735;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;
import us.dot.its.jpo.ode.plugin.j2735.timstorage.Circle;

public class J2735GeometricProjection extends Asn1Object {
    private static final long serialVersionUID = 1L;

    private J2735HeadingSlice direction;
    private J2735Extent extent;
    private String laneWidth;
    private Circle circle;

    public J2735HeadingSlice getDirection() {
		return direction;
	}

	public void setDirection(J2735HeadingSlice direction) {
		this.direction = direction;
	}

    public J2735Extent getExtent() {
		return extent;
	}

	public void setExtent(J2735Extent extent) {
		this.extent = extent;
	}

    public String getLaneWidth() {
		return laneWidth;
	}

	public void setLaneWidth(String laneWidth) {
		this.laneWidth = laneWidth;
	}

    public Circle getCircle() {
		return circle;
	}

	public void setCircle(Circle circle) {
		this.circle = circle;
	}
}
