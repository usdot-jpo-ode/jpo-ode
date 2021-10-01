package us.dot.its.jpo.ode.plugin.j2735;

import java.util.HashMap;
import java.util.Map;

public enum J2735LaneAttributesTrackedVehicle {
	specRevocableLane(0), specCommuterRailRoadTrack(1), specLightRailRoadTrack(2), specHeavyRailRoadTrack(3),
	specOtherRailType(4);

	private int laneAttributesTrackedVehicleID;

	private static Map<Integer, J2735LaneAttributesTrackedVehicle> map = new HashMap<>();

	static {
		for (J2735LaneAttributesTrackedVehicle cur : J2735LaneAttributesTrackedVehicle.values()) {
			map.put(cur.laneAttributesTrackedVehicleID, cur);
		}
	}

	private J2735LaneAttributesTrackedVehicle(int id) {
		laneAttributesTrackedVehicleID = id;
	}

	public static J2735LaneAttributesTrackedVehicle valueOf(int id) {
		return map.get(id);
	}

	public int getLaneAttributesTrackedVehicleID() {
		return laneAttributesTrackedVehicleID;
	}

	public void setLaneAttributesTrackedVehicleID(int laneAttributesTrackedVehicleID) {
		this.laneAttributesTrackedVehicleID = laneAttributesTrackedVehicleID;
	}

}
