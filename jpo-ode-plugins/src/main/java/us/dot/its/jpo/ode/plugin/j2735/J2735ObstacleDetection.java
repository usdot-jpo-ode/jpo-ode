package us.dot.its.jpo.ode.plugin.j2735;

import java.math.BigDecimal;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735ObstacleDetection extends Asn1Object {
	private static final long serialVersionUID = 1L;

	public J2735DDateTime dateTime;
	public Integer description;
	public J2735NamedNumber locationDetails;
	public BigDecimal obDirect;
	public Integer obDist;
	public J2735BitString vertEvent;

}
