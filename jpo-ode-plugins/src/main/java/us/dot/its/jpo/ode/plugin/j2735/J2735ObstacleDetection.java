package us.dot.its.jpo.ode.plugin.j2735;

import java.math.BigDecimal;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735ObstacleDetection implements Asn1Object {

	public J2735DDateTime dateTime;
	public Integer description;
	public J2735NamedNumber locationDetails;
	public BigDecimal obDirect;
	public Integer obDist;
	public J2735BitString vertEvent;

}
