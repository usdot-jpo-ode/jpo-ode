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

public class TimeOffsetBuilder {

    private static final Integer TIME_OFFSET_LOWER_BOUND = 1;
    private static final Integer TIME_OFFSET_UPPER_BOUND = 65535;
    
    private TimeOffsetBuilder() {
       throw new UnsupportedOperationException();
    }

    public static BigDecimal genericTimeOffset(JsonNode timeOffset) {

        BigDecimal result;

        if (timeOffset.asInt() == 65535) {
            result = null;
        } else if (timeOffset.asInt() < TIME_OFFSET_LOWER_BOUND) {
            throw new IllegalArgumentException("Time offset value below lower bound [1]");
        } else if (timeOffset.asInt() > TIME_OFFSET_UPPER_BOUND) {
            result = BigDecimal.valueOf(655.34);
        } else {
            result = BigDecimal.valueOf(timeOffset.asLong(), 2);
        }

        return result;
    }

}
