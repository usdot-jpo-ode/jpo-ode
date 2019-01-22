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

import us.dot.its.jpo.ode.plugin.j2735.J2735BumperHeights;

public class BumperHeightsBuilder {

    private static final String REAR = "rear";
    private static final String FRONT = "front";

    private BumperHeightsBuilder() {
       throw new UnsupportedOperationException();
    }

    public static J2735BumperHeights genericBumperHeights(JsonNode bumperHeights) {

        if (bumperHeights.get(FRONT).asInt() < 0 || bumperHeights.get(REAR).asInt() < 0) {
            throw new IllegalArgumentException("Bumper height value below lower bound");
        }

        if (bumperHeights.get(FRONT).asInt() > 127 || bumperHeights.get(REAR).asInt() > 127) {
            throw new IllegalArgumentException("Bumper height value above upper bound");
        }

        J2735BumperHeights bhs = new J2735BumperHeights();
        bhs.setFront(BigDecimal.valueOf(bumperHeights.get(FRONT).asLong(), 2));
        bhs.setRear(BigDecimal.valueOf(bumperHeights.get(REAR).asLong(), 2));
        return bhs;
    }

}
