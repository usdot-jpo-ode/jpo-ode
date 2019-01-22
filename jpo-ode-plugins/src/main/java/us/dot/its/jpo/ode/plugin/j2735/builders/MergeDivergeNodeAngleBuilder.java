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

public class MergeDivergeNodeAngleBuilder {

   private MergeDivergeNodeAngleBuilder() {
      throw new UnsupportedOperationException();
   }

   public static int mergeDivergeNodeAngle(BigDecimal angle) {
      return angle.divide(BigDecimal.valueOf(1.5), 0, RoundingMode.HALF_UP).intValue();
   }
}
