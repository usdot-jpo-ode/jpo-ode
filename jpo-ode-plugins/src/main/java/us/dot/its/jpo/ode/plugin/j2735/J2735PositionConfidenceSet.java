package us.dot.its.jpo.ode.plugin.j2735;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735PositionConfidenceSet extends Asn1Object {
	private static final long serialVersionUID = 1L;

	public enum J2735PositionConfidence {
		UNAVAILABLE,
		A500M,
		A200M,
		A100M,
		A50M,
		A20M,
		A10M,
		A5M,
		A2M,
		A1M,
		A50CM,
		A20CM,
		A10CM,
		A5CM,
		A2CM,
		A1CM
	}
	public enum J2735ElevationConfidence {
		UNAVAILABLE,
		ELEV_500_00,
		ELEV_200_00,
		ELEV_100_00,
		ELEV_050_00,
		ELEV_020_00,
		ELEV_010_00,
		ELEV_005_00,
		ELEV_002_00,
		ELEV_001_00,
		ELEV_000_50,
		ELEV_000_20,
		ELEV_000_10,
		ELEV_000_05,
		ELEV_000_02,
		ELEV_000_01
	}
	
	private J2735PositionConfidence pos;
	private J2735ElevationConfidence elevation;

	public J2735PositionConfidence getPos() {
		return pos;
	}

	public void setPos(J2735PositionConfidence pos) {
		this.pos = pos;
	}

	public J2735ElevationConfidence getElevation() {
		return elevation;
	}

	public void setElevation(J2735ElevationConfidence elevation) {
		this.elevation = elevation;
	}

}
