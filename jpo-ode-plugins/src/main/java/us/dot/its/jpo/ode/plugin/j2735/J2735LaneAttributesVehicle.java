package us.dot.its.jpo.ode.plugin.j2735;

import java.util.HashMap;
import java.util.Map;

public enum J2735LaneAttributesVehicle {
	isVehicleRevocableLane(0), isVehicleFlyOverLane(1), hovLaneUseOnly(2), restrictedToBusUse(3),
	restrictedToTaxiUse(4), restrictedFromPublicUse(5), hasIRbeaconCoverage(6), permissionOnRequest(7);

	private int laneAttrvehicleID;

	private static Map<Integer, J2735LaneAttributesVehicle> map = new HashMap<>();

	static {
		for (J2735LaneAttributesVehicle cur : J2735LaneAttributesVehicle.values()) {
			map.put(cur.laneAttrvehicleID, cur);
		}
	}

	private J2735LaneAttributesVehicle(int id) {
		laneAttrvehicleID = id;
	}

	public static J2735LaneAttributesVehicle valueOf(int id) {
		return map.get(id);
	}

	public int getLaneAttrvehicleID() {
		return laneAttrvehicleID;
	}

	public void setLaneAttrvehicleID(int laneAttrvehicleID) {
		this.laneAttrvehicleID = laneAttrvehicleID;
	}

}
