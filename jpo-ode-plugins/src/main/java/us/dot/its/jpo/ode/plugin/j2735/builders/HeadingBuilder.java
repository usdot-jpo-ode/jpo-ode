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

public class HeadingBuilder {
    
   
  private HeadingBuilder() {
    super();
  }

  public static BigDecimal genericHeading(JsonNode heading) {
      return genericHeading(heading.asLong());
   }

   public static BigDecimal genericHeading(long heading) {
      BigDecimal angle = AngleBuilder.longToDecimal(heading);
      return (angle == null ? null : angle.setScale(4, RoundingMode.HALF_DOWN));
   }

    public static BigDecimal genericCoarseHeading(JsonNode coarseHeading) {

        if (coarseHeading.asInt() < 0 || coarseHeading.asInt() > 240) {
            throw new IllegalArgumentException("Coarse heading value out of bounds");
        }

        BigDecimal result = null;

        if (coarseHeading.asInt() != 240) {
            result = BigDecimal.valueOf(coarseHeading.asLong() * 15, 1);
        }

        return result;
    }

}
