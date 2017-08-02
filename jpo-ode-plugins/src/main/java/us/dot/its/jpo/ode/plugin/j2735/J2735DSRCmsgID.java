package us.dot.its.jpo.ode.plugin.j2735;

import java.util.HashMap;
import java.util.Map;

public enum J2735DSRCmsgID {

    BASICSAFETYMESSAGE(20), TRAVELERINFORMATION(31);

    private int msgID;

    private static Map<Integer, J2735DSRCmsgID> map = new HashMap<>();

    static {
        for (J2735DSRCmsgID cur : J2735DSRCmsgID.values()) {
            map.put(cur.msgID, cur);
        }
    }

    private J2735DSRCmsgID(int id) {
        msgID = id;
    }

    public static J2735DSRCmsgID valueOf(int id) {
        return map.get(id);
    }

    public int getMsgID() {
        return msgID;
    }

    public void setMsgID(int msgID) {
        this.msgID = msgID;
    }

    
}