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

import java.math.BigDecimal;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735PathHistoryPoint;

public class PathHistoryPointBuilder {

   private static final String HEADING = "heading";
  private static final String POS_ACCURACY = "posAccuracy";
  private static final String SPEED = "speed";

  private PathHistoryPointBuilder() {
      throw new UnsupportedOperationException();
   }

   public static J2735PathHistoryPoint genericPathHistoryPoint(JsonNode pathHistoryPoint) {

      J2735PathHistoryPoint php = new J2735PathHistoryPoint();

      long latOffset = pathHistoryPoint.get("latOffset").asLong();
      long lonOffset = pathHistoryPoint.get("lonOffset").asLong();
      long elevationOffset = pathHistoryPoint.get("elevationOffset").asLong();
      long timeOffset = pathHistoryPoint.get("timeOffset").asLong();
      
      
      
      // Required elements
      if (latOffset == -131072) {
         php.setLatOffset(null);
      } else if (latOffset < -131072) {
         php.setLatOffset(BigDecimal.valueOf(-0.0131071));
      } else if (latOffset > 131071) {
         php.setLatOffset(BigDecimal.valueOf(0.0131071));
      } else {
         php.setLatOffset(BigDecimal.valueOf(latOffset, 7));
      }

      if (lonOffset == -131072) {
         php.setLonOffset(null);
      } else if (lonOffset < -131072) {
         php.setLonOffset(BigDecimal.valueOf(-0.0131071));
      } else if (lonOffset > 131071) {
         php.setLonOffset(BigDecimal.valueOf(0.0131071));
      } else {
         php.setLonOffset(BigDecimal.valueOf(lonOffset, 7));
      }

      if (elevationOffset == -2048) {
         php.setElevationOffset(null);
      } else if (elevationOffset < -2048) {
         php.setElevationOffset(BigDecimal.valueOf(-204.7));
      } else if (elevationOffset > 2047) {
         php.setElevationOffset(BigDecimal.valueOf(204.7));
      } else {
         php.setElevationOffset(BigDecimal.valueOf(elevationOffset, 1));
      }

      if (timeOffset == 65535) {
         php.setElevationOffset(null);
      } else if (timeOffset <= 0) {
         throw new IllegalArgumentException("timeOffset value out of bounds [below 0]");
      } else if (timeOffset >= 65534) {
         php.setTimeOffset(BigDecimal.valueOf(655.34));
      } else {
         php.setTimeOffset(BigDecimal.valueOf(timeOffset, 2));
      }
      

      
      // Optional elements
      if (pathHistoryPoint.get(SPEED) != null) {
         php.setSpeed(SpeedOrVelocityBuilder.genericSpeed(pathHistoryPoint.get(SPEED)));
      }
      if (pathHistoryPoint.get(POS_ACCURACY) != null) {
         php.setPosAccuracy(PositionalAccuracyBuilder.genericPositionalAccuracy(pathHistoryPoint.get(POS_ACCURACY)));
      }
      
      
      
      if (pathHistoryPoint.get(HEADING) != null) {
         if (pathHistoryPoint.get(HEADING).asLong() == 240) {
            php.setHeading(null);
      } else if (pathHistoryPoint.get(HEADING).asLong() < 0) {
         throw new IllegalArgumentException("heading value out of bounds [below 0]");
      } else if (pathHistoryPoint.get(HEADING).asLong() > 240) {
         throw new IllegalArgumentException("heading value out of bounds [above 240]");
      } else {
         php.setHeading(BigDecimal.valueOf((pathHistoryPoint.get(HEADING).asDouble() * 1.5)).setScale(1));
      }
      
      }
      
      return php;
   }

}
