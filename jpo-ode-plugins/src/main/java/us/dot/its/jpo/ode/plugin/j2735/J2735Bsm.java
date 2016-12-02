package us.dot.its.jpo.ode.plugin.j2735;

import java.util.ArrayList;
import java.util.List;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735Bsm extends Asn1Object {
	private static final long serialVersionUID = 1L;

	public J2735BsmCoreData coreData;
	public List<J2735BsmPart2Content> partII = 
			new ArrayList<J2735BsmPart2Content>();
}
