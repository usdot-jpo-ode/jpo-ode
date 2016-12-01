package us.dot.its.jpo.ode.plugin.j2735;

import java.util.List;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735PathHistory implements Asn1Object {
	public J2735FullPositionVector initialPosition;
	public J2735BitString currGNSSstatus;
	public List<J2735PathHistoryPoint> crumbData;
}
