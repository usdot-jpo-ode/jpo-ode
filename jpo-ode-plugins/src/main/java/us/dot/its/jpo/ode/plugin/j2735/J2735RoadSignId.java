package us.dot.its.jpo.ode.plugin.j2735;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735RoadSignId extends Asn1Object {
    private OdePosition3D position;
    private String viewAngle;
    private J2735MutcdCode mutcdCode;
    private String crc;

    public OdePosition3D getPosition() {
		return position;
	}

	public void setPosition(OdePosition3D position) {
		this.position = position;
	}

    public String getViewAngle() {
		return viewAngle;
	}

	public void setViewAngle(String viewAngle) {
		this.viewAngle = viewAngle;
	}

    public J2735MutcdCode getMutcdCode() {
		return mutcdCode;
	}

	public void setMutcdCode(J2735MutcdCode mutcdCode) {
		this.mutcdCode = mutcdCode;
	}

    public String getCrc() {
		return crc;
	}

	public void setCrc(String crc) {
		this.crc = crc;
	}
}
