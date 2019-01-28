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

import us.dot.its.jpo.ode.plugin.j2735.J2735HeadingConfidence;
import us.dot.its.jpo.ode.plugin.j2735.J2735SpeedConfidence;
import us.dot.its.jpo.ode.plugin.j2735.J2735SpeedandHeadingandThrottleConfidence;
import us.dot.its.jpo.ode.plugin.j2735.J2735ThrottleConfidence;

public class SpeedandHeadingandThrottleConfidenceBuilder {

    private static final String THROTTLE = "throttle";
    private static final String SPEED = "speed";
    private static final String HEADING = "heading";
    private static final long HEADING_LOWER_BOUND = 0;
    private static final long HEADING_UPPER_BOUND = 7;
    private static final long SPEED_LOWER_BOUND = 0;
    private static final long SPEED_UPPER_BOUND = 7;
    private static final long THROTTLE_LOWER_BOUND = 0;
    private static final long THROTTLE_UPPER_BOUND = 3;
    
    private SpeedandHeadingandThrottleConfidenceBuilder(){
       throw new UnsupportedOperationException();
    }

    public static J2735SpeedandHeadingandThrottleConfidence genericSpeedandHeadingandThrottleConfidence(
            JsonNode speedConfidence) {

        if (speedConfidence.get(HEADING).asLong() < HEADING_LOWER_BOUND
                || speedConfidence.get(HEADING).asLong() > HEADING_UPPER_BOUND) {
            throw new IllegalArgumentException("HeadingConfidence out of bounds [0..7]");
        }

        if (speedConfidence.get(SPEED).asLong() < SPEED_LOWER_BOUND
                || speedConfidence.get(SPEED).asLong() > SPEED_UPPER_BOUND) {
            throw new IllegalArgumentException("SpeedConfidence out of bounds [0..7]");
        }

        if (speedConfidence.get(THROTTLE).asLong() < THROTTLE_LOWER_BOUND
                || speedConfidence.get(THROTTLE).asLong() > THROTTLE_UPPER_BOUND) {
            throw new IllegalArgumentException("ThrottleConfidence out of bounds [0..3]");
        }

        J2735SpeedandHeadingandThrottleConfidence shtc = new J2735SpeedandHeadingandThrottleConfidence();

        shtc.setHeading(J2735HeadingConfidence.valueOf(speedConfidence.get(HEADING).asText().replaceAll("-", "_").toUpperCase()));
        shtc.setSpeed(J2735SpeedConfidence.valueOf(speedConfidence.get(SPEED).asText().replaceAll("-", "_").toUpperCase()));
        shtc.setThrottle(J2735ThrottleConfidence.valueOf(speedConfidence.get(THROTTLE).asText().replaceAll("-", "_").toUpperCase()));

        return shtc;
    }

}
