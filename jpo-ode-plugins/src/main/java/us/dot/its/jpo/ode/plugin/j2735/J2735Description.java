package us.dot.its.jpo.ode.plugin.j2735;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735Description extends Asn1Object {
    private static final long serialVersionUID = 1L;

    private J2735OffsetSystem path;
    private J2735GeometricProjection geometry;
    private String oldRegion; //TODO

    public J2735OffsetSystem getPath() {
		return path;
	}

	public void setPath(J2735OffsetSystem path) {
		this.path = path;
	}

    public J2735GeometricProjection getGeometry() {
		return geometry;
	}

	public void setGeometry(J2735GeometricProjection geometry) {
		this.geometry = geometry;
	}

    public String getOldRegion() {
		return oldRegion;
	}

	public void setOldRegion(String oldRegion) {
		this.oldRegion = oldRegion;
	}
}
