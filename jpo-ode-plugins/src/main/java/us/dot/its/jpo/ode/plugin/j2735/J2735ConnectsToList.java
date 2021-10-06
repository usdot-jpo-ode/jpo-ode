package us.dot.its.jpo.ode.plugin.j2735;

import java.util.ArrayList;
import java.util.List;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735ConnectsToList extends Asn1Object {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<J2735Connection> connectsTo = new ArrayList<>();

	public List<J2735Connection> getConnectsTo() {
		return connectsTo;
	}

	public void setConnectsTo(List<J2735Connection> connectsTo) {
		this.connectsTo = connectsTo;
	}
}
