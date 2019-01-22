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

import us.dot.its.jpo.ode.plugin.j2735.J2735PrivilegedEvents;

public class PrivilegedEventsBuilder {

   private static final String SSP_RIGHTS = "sspRights";

   public enum J2735PrivilegedEventFlagsNames {
      peUnavailable, peEmergencyResponse, peEmergencyLightsActive, peEmergencySoundActive, peNonEmergencyLightsActive,
      peNonEmergencySoundActive,
   }

   private static final Integer SSP_LOWER_BOUND = 0;
   private static final Integer SSP_UPPER_BOUND = 31;

   private PrivilegedEventsBuilder() {
      throw new UnsupportedOperationException();
   }

   public static J2735PrivilegedEvents genericPrivilegedEvents(JsonNode events) {

      if (events.get(SSP_RIGHTS).asInt() < SSP_LOWER_BOUND || events.get(SSP_RIGHTS).asInt() > SSP_UPPER_BOUND) {
         throw new IllegalArgumentException("SSPindex value out of bounds [0..31]");
      }

      J2735PrivilegedEvents pe = new J2735PrivilegedEvents();

      pe.setEvent(BitStringBuilder.genericBitString(events.get("event"), J2735PrivilegedEventFlagsNames.values()));
      pe.setSspRights(events.get(SSP_RIGHTS).asInt());

      return pe;
   }

}
