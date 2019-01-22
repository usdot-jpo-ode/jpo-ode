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
import java.math.RoundingMode;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.util.JsonUtils;

public class ElevationBuilder {

   private ElevationBuilder() {
      throw new UnsupportedOperationException();
   }

   public static long j2735Elevation(BigDecimal elevation) {
      return elevation.scaleByPowerOfTen(1).setScale(0, RoundingMode.HALF_UP).intValue();
   }

   public static long j2735Elevation(JsonNode elevation) {
      return j2735Elevation(JsonUtils.decimalValue(elevation));
   }

   public static BigDecimal genericElevation(JsonNode elevation) {
      return genericElevation(elevation.asInt());
   }
   
   public static BigDecimal genericElevation(long elevation) {
      BigDecimal returnValue = null;

      if ((elevation != -4096) && (elevation < 61439) && (elevation > -4095)) {
         returnValue = BigDecimal.valueOf(elevation, 1);
      }
      else if (elevation >= 61439) {
         returnValue = BigDecimal.valueOf((long)61439, 1);
      }
      else if ((elevation <= -4095) && (elevation != -4096)) {
         returnValue = BigDecimal.valueOf((long)-4095, 1);
      }
      return returnValue;
   }
   
   
}
