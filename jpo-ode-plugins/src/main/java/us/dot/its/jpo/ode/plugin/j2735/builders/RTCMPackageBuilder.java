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
package us.dot.its.jpo.ode.plugin.j2735.builders;

import java.util.Iterator;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735RTCMPackage;

public class RTCMPackageBuilder {

    static final String RTCM_HEADER = "rtcmHeader";
    static final String MSGS = "msgs";

    private RTCMPackageBuilder() {
       throw new UnsupportedOperationException();
    }

    public static J2735RTCMPackage genericRTCMPackage(JsonNode rtcmPackage) {
        J2735RTCMPackage rtcm = new J2735RTCMPackage();

        Iterator<JsonNode> iter = rtcmPackage.get(MSGS).elements();

        while (iter.hasNext()) {
            rtcm.getMsgs().add(iter.next().asText());
        }
        // Optional element
        if (rtcmPackage.get(RTCM_HEADER) != null) {
            rtcm.setRtcmHeader(RTCMheaderBuilder.genericRTCMheader(rtcmPackage.get(RTCM_HEADER)));
        }

        return rtcm;
    }

}
