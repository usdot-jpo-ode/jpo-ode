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

public class LatitudeBuilder {

   private LatitudeBuilder() {
      throw new UnsupportedOperationException();
   }

   public static long j2735Latitude(BigDecimal latitude) {
      return latitude.scaleByPowerOfTen(7).setScale(0, RoundingMode.HALF_UP).intValue();
   }

   public static long j2735Latitude(JsonNode latitude) {
      return j2735Latitude(JsonUtils.decimalValue(latitude));
   }

   public static BigDecimal genericLatitude(JsonNode latitude) {
      return genericLatitude(latitude.asInt());
   }
   
   public static BigDecimal genericLatitude(long latitude) {
      BigDecimal returnValue = null;

      if (latitude != 900000001) {
         returnValue = BigDecimal.valueOf(latitude, 7);
      }
      return returnValue;
   }
}
