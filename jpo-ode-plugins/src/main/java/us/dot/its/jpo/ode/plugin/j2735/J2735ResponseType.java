package us.dot.its.jpo.ode.plugin.j2735;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public enum J2735ResponseType implements Asn1Object {
	notInUseOrNotEquipped,
	emergency,
	nonEmergency,
	pursuit,
	stationary,
	slowMoving,
	stopAndGoMovement
}
