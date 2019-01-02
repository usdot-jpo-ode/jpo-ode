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

public class RoadwayCrownAngleBuilder {

   private RoadwayCrownAngleBuilder() {
      throw new UnsupportedOperationException();
   }

   public static int roadwayCrownAngle(BigDecimal angle) {

      BigDecimal min = BigDecimal.valueOf(-38.1);
      BigDecimal max = BigDecimal.valueOf(38.1);
      BigDecimal minZero = BigDecimal.valueOf(-0.15);
      BigDecimal maxZero = BigDecimal.valueOf(0.15);

      if (angle == null) {
         return 128;
      } else if (angle.compareTo(min) >= 0 && angle.compareTo(max) <= 0) {
         if (angle.compareTo(minZero) >= 0 && angle.compareTo(maxZero) <= 0) {

            return 0;
         } else {
            return angle.divide(BigDecimal.valueOf(0.3), 0, RoundingMode.HALF_UP).intValue();
         }
      } else {
         throw new IllegalArgumentException("RoadwayCrownAngle is out of bounds");
      }
   }
}
