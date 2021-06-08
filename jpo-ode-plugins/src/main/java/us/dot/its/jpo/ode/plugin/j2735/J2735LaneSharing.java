package us.dot.its.jpo.ode.plugin.j2735;

import java.util.HashMap;
import java.util.Map;

public enum J2735LaneSharing {
	overlappingLaneDescriptionProvided(0)  ,
	multipleLanesTreatedAsOneLane       (1),
	otherNonMotorizedTrafficTypes       (2),
	individualMotorizedVehicleTraffic   (3), 
	busVehicleTraffic                   (4),
	taxiVehicleTraffic                  (5),
	pedestriansTraffic                  (6),
    cyclistVehicleTraffic               (7),
    trackedVehicleTraffic               (8),
    pedestrianTraffic                   (9);
	 private int LaneSharingID;

	    private static Map<Integer, J2735LaneSharing> map = new HashMap<>();

	    static {
	        for (J2735LaneSharing cur : J2735LaneSharing.values()) {
	            map.put(cur.LaneSharingID, cur);
	        }
	    }

	    private J2735LaneSharing(int id) {
	    	LaneSharingID = id;
	    }

	    public static J2735LaneSharing valueOf(int id) {
	        return map.get(id);
	    }

	    public int getLaneSharingID() {
	        return LaneSharingID;
	    }

	    public void setLaneSharingID(int LaneSharingID) {
	        this.LaneSharingID = LaneSharingID;
	    }

}
