package us.dot.its.jpo.ode.plugin.j2735;

import java.util.ArrayList;
import java.util.List;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735MovementEventList extends Asn1Object {
	private static final long serialVersionUID = 1L;
	private List<J2735MovementEvent> movementEventList = new ArrayList<>();

	public List<J2735MovementEvent> getMovementEventList() {
		return movementEventList;
	}

	public void setMovementEventList(List<J2735MovementEvent> movementEventList) {
		this.movementEventList = movementEventList;
	}
}
