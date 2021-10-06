package us.dot.its.jpo.ode.plugin.j2735;

import java.util.HashMap;
import java.util.Map;

public enum J2735LaneDirection {
	ingressPath(0),egressPath(1);
	 private int directUseId;

    private static Map<Integer, J2735LaneDirection> map = new HashMap<>();

    static {
        for (J2735LaneDirection cur : J2735LaneDirection.values()) {
            map.put(cur.directUseId, cur);
        }
    }

    private J2735LaneDirection(int id) {
    	directUseId = id;
    }

    public static J2735LaneDirection valueOf(int id) {
        return map.get(id);
    }

	public int getDirectUseId() {
		return directUseId;
	}

	public void setDirectUseId(int directUseId) {
		this.directUseId = directUseId;
	}

}
