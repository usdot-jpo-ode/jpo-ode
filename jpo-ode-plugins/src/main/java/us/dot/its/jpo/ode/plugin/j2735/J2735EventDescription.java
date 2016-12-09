package us.dot.its.jpo.ode.plugin.j2735;

import java.util.ArrayList;
import java.util.List;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735EventDescription extends Asn1Object {
	private static final long serialVersionUID = 1L;

	public List<Integer> description;
	public J2735Extent extent;
	public J2735BitString heading;
	public String priority;
	public List<J2735RegionalContent> regional = 
			new ArrayList<J2735RegionalContent>();
	public Integer typeEvent;

}
