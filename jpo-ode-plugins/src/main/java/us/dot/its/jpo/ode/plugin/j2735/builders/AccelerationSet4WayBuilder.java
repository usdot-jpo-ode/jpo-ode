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

import us.dot.its.jpo.ode.plugin.j2735.J2735AccelerationSet4Way;

public class AccelerationSet4WayBuilder {

   private AccelerationSet4WayBuilder() {
      throw new UnsupportedOperationException();
   }

   public static J2735AccelerationSet4Way genericAccelerationSet4Way(JsonNode accelSet) {
      J2735AccelerationSet4Way genericAccelerationSet4Way = new J2735AccelerationSet4Way();
      
      int accelLong = accelSet.get("long").asInt();
      int accelLat = accelSet.get("lat").asInt();
      int accelVert = accelSet.get("vert").asInt();
      int accelYaw = accelSet.get("yaw").asInt();

      // Acceleration ::= INTEGER (-2000..2001)
      // -- LSB units are 0.01 m/s^2
      // -- the value 2000 shall be used for values greater than 2000
      // -- the value -2000 shall be used for values less than -2000
      // -- a value of 2001 shall be used for Unavailable
      if (accelLong == 2001) {
         genericAccelerationSet4Way.setAccelLong(null);
      } else if (accelLong < -2000) {
         genericAccelerationSet4Way.setAccelLong(BigDecimal.valueOf(-2000, 2));
      } else if (accelLong > 2001) {
         genericAccelerationSet4Way.setAccelLong(BigDecimal.valueOf(2000, 2));
      } else {
         genericAccelerationSet4Way.setAccelLong(BigDecimal.valueOf(accelLong, 2));
      }

      if (accelLat == 2001) {
         genericAccelerationSet4Way.setAccelLat(null);
      } else if (accelLat < -2000) {
         genericAccelerationSet4Way.setAccelLat(BigDecimal.valueOf(-2000,2));
      } else if (accelLat > 2001) {
         genericAccelerationSet4Way.setAccelLat(BigDecimal.valueOf(2000,2));
      } else {
         genericAccelerationSet4Way.setAccelLat(BigDecimal.valueOf(accelLat, 2));
      }

      // VerticalAcceleration ::= INTEGER (-127..127)
      // -- LSB units of 0.02 G steps over -2.52 to +2.54 G
      // -- The value +127 shall be used for ranges >= 2.54 G
      // -- The value -126 shall be used for ranges <= -2.52 G
      // -- The value -127 shall be used for unavailable
      if (accelVert == -127) {
         genericAccelerationSet4Way.setAccelVert(null);
      } else if (accelVert < -127) {
         genericAccelerationSet4Way.setAccelVert(BigDecimal.valueOf(-2.52));
      } else if (accelVert > 127) {
         genericAccelerationSet4Way.setAccelVert(BigDecimal.valueOf(2.54));
      } else {
         genericAccelerationSet4Way.setAccelVert(BigDecimal.valueOf(accelVert * (long) 2, 2));

      }

      // YawRate ::= INTEGER (-32767..32767)
      // -- LSB units of 0.01 degrees per second (signed)
      if (accelYaw <= 32767 && accelYaw >= -32767) {
         genericAccelerationSet4Way.setAccelYaw(BigDecimal.valueOf(accelYaw, 2));
      } else {
         throw new IllegalArgumentException("Yaw rate out of bounds");
      }


      return genericAccelerationSet4Way;
   }

}
