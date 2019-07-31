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

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735PositionConfidenceSet;
import us.dot.its.jpo.ode.plugin.j2735.J2735PositionConfidenceSet.J2735ElevationConfidence;
import us.dot.its.jpo.ode.plugin.j2735.J2735PositionConfidenceSet.J2735PositionConfidence;

public class PositionConfidenceSetBuilder {
    
    private static final String ELEVATION = "elevation";
    private static final String POS = "pos";
    private static final long POS_LOWER_BOUND = 0L;
    private static final long POS_UPPER_BOUND = 15L;
    private static final long ELEV_LOWER_BOUND = 0L;
    private static final long ELEV_UPPER_BOUND = 15L;
    
    private PositionConfidenceSetBuilder() {
       throw new UnsupportedOperationException();
    }

    public static J2735PositionConfidenceSet genericPositionConfidenceSet(JsonNode posConfidence) {
        
        if (posConfidence.get(POS).asLong() < POS_LOWER_BOUND || 
                posConfidence.get(POS).asLong() > POS_UPPER_BOUND) {
            throw new IllegalArgumentException("PositionConfidence value out of bounds");
        }

        if (posConfidence.get(ELEVATION).asLong() < ELEV_LOWER_BOUND || 
                posConfidence.get(ELEVATION).asLong() > ELEV_UPPER_BOUND) {
            throw new IllegalArgumentException("ElevationConfidence value out of bounds");
        }

        J2735PositionConfidenceSet pc = new J2735PositionConfidenceSet();
        
        pc.setPos(J2735PositionConfidence.valueOf(posConfidence.get(POS).asText().replaceAll("-", "_").toUpperCase()));
        pc.setElevation(J2735ElevationConfidence.valueOf(posConfidence.get(ELEVATION).asText().replaceAll("-", "_").toUpperCase()));
        
        return pc;
    }

}
