package us.dot.its.jpo.ode.plugin.j2735;

import java.util.HashMap;
import java.util.Map;

public enum J2735LaneAttributesBike {
	bikeRevocableLane(0), pedestrianUseAllowed(1), isBikeFlyOverLane(2), fixedCycleTime(3), biDirectionalCycleTimes(4),
	isolatedByBarrier(5), unsignalizedSegmentsPresent(6);

	private int laneAttributesBikeID;

	private static Map<Integer, J2735LaneAttributesBike> map = new HashMap<>();

	static {
		for (J2735LaneAttributesBike cur : J2735LaneAttributesBike.values()) {
			map.put(cur.laneAttributesBikeID, cur);
		}
	}

	private J2735LaneAttributesBike(int id) {
		laneAttributesBikeID = id;
	}

	public static J2735LaneAttributesBike valueOf(int id) {
		return map.get(id);
	}

	public int getLaneAttributesBikeID() {
		return laneAttributesBikeID;
	}

	public void setLaneAttributesBikeID(int laneAttributesBikeID) {
		this.laneAttributesBikeID = laneAttributesBikeID;
	}

}
