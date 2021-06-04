package us.dot.its.jpo.ode.plugin.j2735;

import java.util.HashMap;
import java.util.Map;

public enum J2735AllowedManeuvers {
	maneuverStraightAllowed      (0), 
	maneuverLeftAllowed          (1),  
	maneuverRightAllowed         (2), 
	maneuverUTurnAllowed         (3), 
	maneuverLeftTurnOnRedAllowed (4), 
	maneuverRightTurnOnRedAllowed (5), 
	maneuverLaneChangeAllowed    (6), 
	maneuverNoStoppingAllowed    (7), 
	yieldAllwaysRequired         (8), 
	goWithHalt                   (9), 
	caution                      (10), 
	reserved1                    (11);

	private int AllowedManeuversID;

	private static Map<Integer, J2735AllowedManeuvers> map = new HashMap<>();

	static {
		for (J2735AllowedManeuvers cur : J2735AllowedManeuvers.values()) {
			map.put(cur.AllowedManeuversID, cur);
		}
	}

	private J2735AllowedManeuvers(int id) {
		AllowedManeuversID = id;
	}

	public static J2735AllowedManeuvers valueOf(int id) {
		return map.get(id);
	}

	public int getAllowedManeuversID() {
		return AllowedManeuversID;
	}

	public void setAllowedManeuversID(int allowedManeuversID) {
		AllowedManeuversID = allowedManeuversID;
	}
	
}
