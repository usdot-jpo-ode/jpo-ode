/*******************************************************************************
 * Copyright 2018 572682
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package us.dot.its.jpo.ode.plugin.j2735;

import java.util.HashMap;
import java.util.Map;

public enum J2735DSRCmsgID {

   BasicSafetyMessage(20), TravelerInformation(31), SPATMessage(19), MAPMessage(18), SSMMessage(30), SRMMessage(29);

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
