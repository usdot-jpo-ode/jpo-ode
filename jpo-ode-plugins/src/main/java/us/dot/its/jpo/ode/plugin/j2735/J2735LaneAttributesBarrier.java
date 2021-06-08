package us.dot.its.jpo.ode.plugin.j2735;

import java.util.HashMap;
import java.util.Map;

public enum J2735LaneAttributesBarrier {
	medianRevocableLane(0), median(1), whiteLineHashing(2), stripedLines(3), doubleStripedLines(4), trafficCones(5),
	constructionBarrier(6), trafficChannels(7), lowCurbs(8), highCurbs(9);

	private int laneAttributesBarrierID;

	private static Map<Integer, J2735LaneAttributesBarrier> map = new HashMap<>();

	static {
		for (J2735LaneAttributesBarrier cur : J2735LaneAttributesBarrier.values()) {
			map.put(cur.laneAttributesBarrierID, cur);
		}
	}

	private J2735LaneAttributesBarrier(int id) {
		laneAttributesBarrierID = id;
	}

	public static J2735LaneAttributesBarrier valueOf(int id) {
		return map.get(id);
	}

	public int getLaneAttributesBarrierID() {
		return laneAttributesBarrierID;
	}

	public void setLaneAttributesBarrierID(int laneAttributesBarrierID) {
		this.laneAttributesBarrierID = laneAttributesBarrierID;
	}

}
