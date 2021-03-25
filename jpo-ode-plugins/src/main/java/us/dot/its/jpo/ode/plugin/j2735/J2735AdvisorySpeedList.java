package us.dot.its.jpo.ode.plugin.j2735;

import java.util.ArrayList;
import java.util.List;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735AdvisorySpeedList extends Asn1Object {
	private static final long serialVersionUID = 1L;
	private List<J2735AdvisorySpeed> advisorySpeedList = new ArrayList<>();

	public List<J2735AdvisorySpeed> getAdvisorySpeedList() {
		return advisorySpeedList;
	}

	public void setAdvisorySpeedList(List<J2735AdvisorySpeed> advisorySpeedList) {
		this.advisorySpeedList = advisorySpeedList;
	}
}
