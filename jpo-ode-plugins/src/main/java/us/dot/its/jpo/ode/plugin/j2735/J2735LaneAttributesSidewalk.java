package us.dot.its.jpo.ode.plugin.j2735;

import java.util.HashMap;
import java.util.Map;

public enum J2735LaneAttributesSidewalk {
	sidewalkRevocableLane(0), bicyleUseAllowed(1), isSidewalkFlyOverLane(2), walkBikes(3);

	private int laneAttributesSidewalkID;

	private static Map<Integer, J2735LaneAttributesSidewalk> map = new HashMap<>();

	static {
		for (J2735LaneAttributesSidewalk cur : J2735LaneAttributesSidewalk.values()) {
			map.put(cur.laneAttributesSidewalkID, cur);
		}
	}

	private J2735LaneAttributesSidewalk(int id) {
		laneAttributesSidewalkID = id;
	}

	public static J2735LaneAttributesSidewalk valueOf(int id) {
		return map.get(id);
	}

	public int getLaneAttributesSidewalkID() {
		return laneAttributesSidewalkID;
	}

	public void setLaneAttributesSidewalkID(int laneAttributesSidewalkID) {
		this.laneAttributesSidewalkID = laneAttributesSidewalkID;
	}

}
