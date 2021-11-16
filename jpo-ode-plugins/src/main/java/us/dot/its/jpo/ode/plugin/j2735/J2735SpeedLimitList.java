package us.dot.its.jpo.ode.plugin.j2735;

import java.util.ArrayList;
import java.util.List;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735SpeedLimitList extends Asn1Object {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<J2735RegulatorySpeedLimit> speedLimits = new ArrayList<>();

	public List<J2735RegulatorySpeedLimit> getSpeedLimits() {
		return speedLimits;
	}

	public void setSpeedLimits(List<J2735RegulatorySpeedLimit> speedLimits) {
		this.speedLimits = speedLimits;
	}

}
