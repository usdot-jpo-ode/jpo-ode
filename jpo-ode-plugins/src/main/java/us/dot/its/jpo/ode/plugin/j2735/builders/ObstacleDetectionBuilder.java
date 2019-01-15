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

import us.dot.its.jpo.ode.plugin.j2735.J2735ObstacleDetection;

public class ObstacleDetectionBuilder {

   public enum J2735VertEventNames {
      rightRear, rightFront, leftRear, leftFront, notEquipped
   }

   private static final Integer DIST_LOWER_BOUND = 0;
   private static final Integer DIST_UPPER_BOUND = 32767;

   private ObstacleDetectionBuilder() {
      throw new UnsupportedOperationException();
   }

   public static J2735ObstacleDetection genericObstacleDetection(JsonNode obstacleDetection) {

      // Bounds check
      int obDist = obstacleDetection.get("obDist").asInt();
      if (obDist < DIST_LOWER_BOUND || DIST_UPPER_BOUND < obDist) {
         throw new IllegalArgumentException(
               String.format("Distance out of bounds [%d..%d]", DIST_LOWER_BOUND, DIST_UPPER_BOUND));
      }

      // Required elements
      J2735ObstacleDetection ob = new J2735ObstacleDetection();
      ob.setObDist(obDist);
      ob.setObDirect(AngleBuilder.genericAngle(obstacleDetection.get("obDirect")));
      ob.setDateTime(DDateTimeBuilder.genericDDateTime(obstacleDetection.get("dateTime")));

      // Optional elements
      JsonNode description = obstacleDetection.get("description");

      if (description != null) {
         ob.setDescription(description.asInt());
      }

      JsonNode locationDetails = obstacleDetection.get("locationDetails");
      if (locationDetails != null) {
         ob.setLocationDetails(NamedNumberBuilder.genericGenericLocations(locationDetails));
      }

      ob.setVertEvent(
            BitStringBuilder.genericBitString(obstacleDetection.get("vertEvent"), J2735VertEventNames.values()));

      return ob;
   }

}
