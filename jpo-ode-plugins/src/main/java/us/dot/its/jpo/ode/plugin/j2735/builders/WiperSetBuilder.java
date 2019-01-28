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

import us.dot.its.jpo.ode.plugin.j2735.J2735WiperSet;
import us.dot.its.jpo.ode.plugin.j2735.J2735WiperStatus;

public class WiperSetBuilder {

   private static final int STATUS_LOWER_BOUND = 0;
   private static final int STATUS_UPPER_BOUND = 6;
   private static final int RATE_LOWER_BOUND = 0;
   private static final int RATE_UPPER_BOUND = 127;

   private WiperSetBuilder() {
      throw new UnsupportedOperationException();
   }

   /**
    * Converts ASN wiper set to human readable values.
    * @param wiperSet
    * @return
    */
   public static J2735WiperSet genericWiperSet(JsonNode wiperSet) {

      J2735WiperSet gws = new J2735WiperSet();

      // statusFront and rateFront are required elements
      int statusFront = wiperSet.get("statusFront").asInt();
      int rateFront = wiperSet.get("rateFront").asInt();

      if (statusFront < STATUS_LOWER_BOUND || STATUS_UPPER_BOUND < statusFront) {
         throw new IllegalArgumentException(
               String.format("Front wiper status out of bounds [%d,%d]", STATUS_LOWER_BOUND, STATUS_UPPER_BOUND));
      }

      gws.setStatusFront(J2735WiperStatus.values()[statusFront]);

      if (rateFront < RATE_LOWER_BOUND || RATE_UPPER_BOUND < rateFront) {
         throw new IllegalArgumentException(
               String.format("Front wiper rate out of bounds [%d,%d]", RATE_LOWER_BOUND, RATE_UPPER_BOUND));
      }

      gws.setRateFront(rateFront);

      // statusRear and rateRear are optional elements
      JsonNode statusRearNode = wiperSet.get("statusRear");
      if (statusRearNode != null) {
         int statusRear = statusRearNode.asInt();

         if (statusRear < STATUS_LOWER_BOUND || STATUS_UPPER_BOUND < statusRear) {
            throw new IllegalArgumentException(String.format("Rear wiper status value out of bounds [%d,%d]",
                  STATUS_LOWER_BOUND, STATUS_UPPER_BOUND));
         }

         gws.setStatusRear(J2735WiperStatus.values()[statusRear]);
      }

      JsonNode rateRearNode = wiperSet.get("rateRear");
      if (rateRearNode != null) {
         int rateRear = rateRearNode.asInt();

         if (rateRear < RATE_LOWER_BOUND || RATE_UPPER_BOUND < rateRear) {
            throw new IllegalArgumentException(
                  String.format("Rear wiper rate out of bounds [%d,%d]", RATE_LOWER_BOUND, RATE_UPPER_BOUND));
         }

         gws.setRateRear(rateRear);
      }

      return gws;
   }

}
