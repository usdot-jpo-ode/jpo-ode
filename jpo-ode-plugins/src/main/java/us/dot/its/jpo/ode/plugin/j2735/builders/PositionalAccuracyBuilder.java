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

import us.dot.its.jpo.ode.plugin.j2735.J2735PositionalAccuracy;

public class PositionalAccuracyBuilder {
    
    private static final String ORIENTATION = "orientation";
    private static final String SEMI_MINOR = "semiMinor";
    private static final String SEMI_MAJOR = "semiMajor";

    private PositionalAccuracyBuilder() {
       throw new UnsupportedOperationException();
    }
    
    public static J2735PositionalAccuracy genericPositionalAccuracy(JsonNode positionalAccuracy) {

        if (positionalAccuracy.get(SEMI_MAJOR).asInt() < 0 || positionalAccuracy.get(SEMI_MAJOR).asInt() > 255) {
            throw new IllegalArgumentException("SemiMajorAccuracy value out of bounds");
        }

        if (positionalAccuracy.get(SEMI_MINOR).asInt() < 0 || positionalAccuracy.get(SEMI_MINOR).asInt() > 255) {
            throw new IllegalArgumentException("SemiMinorAccuracy value out of bounds");
        }

        if (positionalAccuracy.get(ORIENTATION).asInt() < 0 || positionalAccuracy.get(ORIENTATION).asInt() > 65535) {
            throw new IllegalArgumentException("SemiMajorOrientation value out of bounds");
        }

        J2735PositionalAccuracy genericPositionalAccuracy = new J2735PositionalAccuracy();

        if (positionalAccuracy.get(SEMI_MAJOR).asInt() != 255) {
            genericPositionalAccuracy.setSemiMajor(BigDecimal.valueOf(positionalAccuracy.get(SEMI_MAJOR).asInt() * (long)5, 2));
        }

        if (positionalAccuracy.get(SEMI_MINOR).asInt() != 255) {
            genericPositionalAccuracy.setSemiMinor(BigDecimal.valueOf(positionalAccuracy.get(SEMI_MINOR).asInt() * (long)5, 2));
        }

        if (positionalAccuracy.get(ORIENTATION).asInt() != 65535) {

            genericPositionalAccuracy.setOrientation(BigDecimal
                    .valueOf((0.0054932479) * (double) (positionalAccuracy.get(ORIENTATION).asLong()))
                    .setScale(10, RoundingMode.HALF_EVEN));

        }
        return genericPositionalAccuracy;

    }

}
