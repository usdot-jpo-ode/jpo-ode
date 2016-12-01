package us.dot.its.jpo.ode.plugin.j2735;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735PositionConfidenceSet implements Asn1Object {

	public enum J2735ElevationConfidence {
		unavailable,
		a500m,
		a200m,
		a100m,
		a50m,
		a20m,
		a10m,
		a5m,
		a2m,
		a1m,
		a50cm,
		a20cm,
		a10cm,
		a5cm,
		a2cm,
		a1cm
	}
	public enum J2735PositionConfidence {
		unavailable,
		elev_500_00,
		elev_200_00,
		elev_100_00,
		elev_050_00,
		elev_020_00,
		elev_010_00,
		elev_005_00,
		elev_002_00,
		elev_001_00,
		elev_000_50,
		elev_000_20,
		elev_000_10,
		elev_000_05,
		elev_000_02,
		elev_000_01

	}
	
	public J2735PositionConfidence pos;
	public J2735ElevationConfidence elevation;

}
