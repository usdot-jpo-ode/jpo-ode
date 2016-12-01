package us.dot.its.jpo.ode.plugin.j2735;

import java.util.ArrayList;
import java.util.List;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735Bsm implements Asn1Object {

	public J2735BsmCoreData coreData;
	public List<J2735BsmPart2Content> partII = 
			new ArrayList<J2735BsmPart2Content>();
}
