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

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735GNSSstatusNames;
import us.dot.its.jpo.ode.plugin.j2735.J2735RTCMheader;

public class RTCMheaderBuilder {

   static final String STATUS = "status";
   static final String OFFSET_SET = "offsetSet";

   private RTCMheaderBuilder() {
      throw new UnsupportedOperationException();
   }

   public static J2735RTCMheader genericRTCMheader(JsonNode rtcmHeader) {
      J2735RTCMheader header = new J2735RTCMheader();

      header.setOffsetSet(AntennaOffsetSetBuilder.genericAntennaOffsetSet(rtcmHeader.get(OFFSET_SET)));
      header.setStatus(
            BitStringBuilder.genericBitString(rtcmHeader.get(STATUS), J2735GNSSstatusNames.values()));

      return header;
   }

}
