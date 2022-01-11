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

import us.dot.its.jpo.ode.plugin.j2735.J2735PivotPointDescription;

public class PivotPointDescriptionBuilder {

    private static final String PIVOT_OFFSET = "pivotOffset";
    private static final Integer PIVOT_OFFSET_LOWER_BOUND = -1024;
    private static final Integer PIVOT_OFFSET_UPPER_BOUND = 1023;
    
    private PivotPointDescriptionBuilder() {
       throw new UnsupportedOperationException();
    }

    public static J2735PivotPointDescription genericPivotPointDescription(JsonNode ppd) {
        J2735PivotPointDescription gppd = new J2735PivotPointDescription();

        if (ppd.get(PIVOT_OFFSET).asInt() < PIVOT_OFFSET_LOWER_BOUND
                || ppd.get(PIVOT_OFFSET).asInt() > PIVOT_OFFSET_UPPER_BOUND) {
            throw new IllegalArgumentException("Pivot offset value out of bounds [-1024.1023]");
        } else if (ppd.get(PIVOT_OFFSET).asInt() == -1024) {
            gppd.setPivotOffset(null);
        } else {
            gppd.setPivotOffset(BigDecimal.valueOf(ppd.get(PIVOT_OFFSET).asInt(), 2));
        }

        gppd.setPivotAngle(AngleBuilder.genericAngle(ppd.get("pivotAngle")));
        gppd.setPivots(ppd.get("pivots").asBoolean());

        return gppd;
    }

}
