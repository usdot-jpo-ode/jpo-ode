package us.dot.its.jpo.ode.plugin.j2735;

import java.util.ArrayList;
import java.util.List;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735TrailerData extends Asn1Object {
	private static final long serialVersionUID = 1L;

	public J2735PivotPointDescription connection;
	public Integer sspRights;
	public List<J2735TrailerUnitDescription> units = new ArrayList<J2735TrailerUnitDescription>();

}
