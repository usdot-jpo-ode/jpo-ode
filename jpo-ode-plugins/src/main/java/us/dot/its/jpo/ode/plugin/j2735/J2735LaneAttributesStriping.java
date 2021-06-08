package us.dot.its.jpo.ode.plugin.j2735;

import java.util.HashMap;
import java.util.Map;

public enum J2735LaneAttributesStriping {
	stripeToConnectingLanesRevocableLane(0), stripeDrawOnLeft(1), stripeDrawOnRight(2), stripeToConnectingLanesLeft(3),
	stripeToConnectingLanesRight(4), stripeToConnectingLanesAhead(5);

	private int laneAttributesStripingID;

	private static Map<Integer, J2735LaneAttributesStriping> map = new HashMap<>();

	static {
		for (J2735LaneAttributesStriping cur : J2735LaneAttributesStriping.values()) {
			map.put(cur.laneAttributesStripingID, cur);
		}
	}

	private J2735LaneAttributesStriping(int id) {
		laneAttributesStripingID = id;
	}

	public static J2735LaneAttributesStriping valueOf(int id) {
		return map.get(id);
	}

	public int getLaneAttributesStripingID() {
		return laneAttributesStripingID;
	}

	public void setLaneAttributesStripingID(int laneAttributesStripingID) {
		this.laneAttributesStripingID = laneAttributesStripingID;
	}

}
