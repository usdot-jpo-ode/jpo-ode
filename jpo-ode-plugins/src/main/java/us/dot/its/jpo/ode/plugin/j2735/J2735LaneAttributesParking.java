package us.dot.its.jpo.ode.plugin.j2735;

import java.util.HashMap;
import java.util.Map;

public enum J2735LaneAttributesParking {
	parkingRevocableLane(0), parallelParkingInUse(1), headInParkingInUse(2), doNotParkZone(3), parkingForBusUse(4),
	parkingForTaxiUse(5), noPublicParkingUse(6);

	private int laneAttributesParkingID;

	private static Map<Integer, J2735LaneAttributesParking> map = new HashMap<>();

	static {
		for (J2735LaneAttributesParking cur : J2735LaneAttributesParking.values()) {
			map.put(cur.laneAttributesParkingID, cur);
		}
	}

	private J2735LaneAttributesParking(int id) {
		laneAttributesParkingID = id;
	}

	public static J2735LaneAttributesParking valueOf(int id) {
		return map.get(id);
	}

	public int getLaneAttributesParkingID() {
		return laneAttributesParkingID;
	}

	public void setLaneAttributesParkingID(int laneAttributesParkingID) {
		this.laneAttributesParkingID = laneAttributesParkingID;
	}

}
