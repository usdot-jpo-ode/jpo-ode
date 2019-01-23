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

public class ScaleB12Builder {

   private ScaleB12Builder() {
      throw new UnsupportedOperationException();
   }

   public static int scaleB12(BigDecimal scale) {
      return scale.subtract(BigDecimal.valueOf(100)).multiply(BigDecimal.valueOf(20)).intValue();
   }
}
