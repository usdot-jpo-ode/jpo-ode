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

import us.dot.its.jpo.ode.plugin.j2735.J2735TransmissionAndSpeed;
import us.dot.its.jpo.ode.plugin.j2735.J2735TransmissionState;

public class TransmissionAndSpeedBuilder {

    private static final String TRANSMISSION = "transmission";
    private static final long TRANSMISSION_LOWER_BOUND = 0L;
    private static final long TRANSMISSION_UPPER_BOUND = 7L;
    
    private TransmissionAndSpeedBuilder() {
       throw new UnsupportedOperationException();
    }

    public static J2735TransmissionAndSpeed genericTransmissionAndSpeed(JsonNode ts) {

        if (ts.get(TRANSMISSION).asLong() < TRANSMISSION_LOWER_BOUND
                || ts.get(TRANSMISSION).asLong() > TRANSMISSION_UPPER_BOUND) {
            throw new IllegalArgumentException("Transmission value out of bounds [0..7]");
        }

        J2735TransmissionAndSpeed gts = new J2735TransmissionAndSpeed();

        gts.setSpeed(SpeedOrVelocityBuilder.genericVelocity(ts.get("speed")));
        gts.setTransmisson(J2735TransmissionState.valueOf(ts.get(TRANSMISSION).asText().toUpperCase()));

        return gts;
    }

}
