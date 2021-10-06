package us.dot.its.jpo.ode.plugin.j2735;

import java.util.HashMap;
import java.util.Map;

public enum J2735LaneAttributesCrosswalk {
	crosswalkRevocableLane(0), bicyleUseAllowed(1), isXwalkFlyOverLane(2), fixedCycleTime(3),
	biDirectionalCycleTimes(4), hasPushToWalkButton(5), audioSupport(6), rfSignalRequestPresent(7),
	unsignalizedSegmentsPresent(8);

	private int laneAttributesCrosswalkID;

	private static Map<Integer, J2735LaneAttributesCrosswalk> map = new HashMap<>();

	static {
		for (J2735LaneAttributesCrosswalk cur : J2735LaneAttributesCrosswalk.values()) {
			map.put(cur.laneAttributesCrosswalkID, cur);
		}
	}

	private J2735LaneAttributesCrosswalk(int id) {
		laneAttributesCrosswalkID = id;
	}

	public static J2735LaneAttributesCrosswalk valueOf(int id) {
		return map.get(id);
	}

	public int getLaneAttributesCrosswalkID() {
		return laneAttributesCrosswalkID;
	}

	public void setLaneAttributesCrosswalkID(int laneAttributesCrosswalkID) {
		this.laneAttributesCrosswalkID = laneAttributesCrosswalkID;
	}

}
