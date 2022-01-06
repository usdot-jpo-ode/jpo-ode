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
      J2735WiperStatus enumStatusFront;
      try {
         enumStatusFront = J2735WiperStatus.valueOf(wiperSet.get("statusFront").fieldNames().next().toUpperCase());
      } catch (IllegalArgumentException e) {
         enumStatusFront = J2735WiperStatus.UNAVAILABLE;
      }
      gws.setStatusFront(enumStatusFront);

      int rateFront = wiperSet.get("rateFront").asInt();
      if (rateFront < RATE_LOWER_BOUND || RATE_UPPER_BOUND < rateFront) {
         throw new IllegalArgumentException(
               String.format("Front wiper rate out of bounds [%d,%d]", RATE_LOWER_BOUND, RATE_UPPER_BOUND));
      }
      gws.setRateFront(rateFront);

      // statusRear and rateRear are optional elements
      JsonNode statusRearNode = wiperSet.get("statusRear");
      if (statusRearNode != null) {
         J2735WiperStatus enumStatusRear;
         try {
            enumStatusRear = J2735WiperStatus.valueOf(statusRearNode.fieldNames().next().toUpperCase());
         } catch (IllegalArgumentException e) {
            enumStatusRear = J2735WiperStatus.UNAVAILABLE;
         }
         gws.setStatusRear(enumStatusRear);
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
