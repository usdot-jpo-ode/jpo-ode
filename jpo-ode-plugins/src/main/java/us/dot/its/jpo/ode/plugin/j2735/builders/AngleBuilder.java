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

public class AngleBuilder {
    
    private AngleBuilder() {
       throw new UnsupportedOperationException();
    }

    public static BigDecimal genericAngle(JsonNode angle) {

        if (angle.asInt() < 0 || angle.asInt() > 28800) {
            throw new IllegalArgumentException("Angle value out of bounds");
        }

        BigDecimal result = null;

        if (angle.asLong() != 28800) {
            result = longToDecimal(angle.asLong());
        }

        return result;
    }

    public static BigDecimal longToDecimal(long longValue) {
        
        BigDecimal result = null;
        
        if (longValue != 28800) {
            result = BigDecimal.valueOf(longValue * 125, 4);
        }
        
        return result;
    }
    
    public static JsonNode angle(long ang) {
       return JsonUtils.newObjectNode("angle", 
          BigDecimal.valueOf(ang).divide(BigDecimal.valueOf(0.0125), 0, RoundingMode.HALF_UP).intValue());
    }
    
    public static int angle(BigDecimal ang) {
       return ang.divide(BigDecimal.valueOf(0.0125), 0, RoundingMode.HALF_UP).intValue();
    }

}
