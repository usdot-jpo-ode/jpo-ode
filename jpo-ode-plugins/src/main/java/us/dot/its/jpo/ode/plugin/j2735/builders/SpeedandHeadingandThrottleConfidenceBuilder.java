package us.dot.its.jpo.ode.plugin.j2735.builders;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735HeadingConfidence;
import us.dot.its.jpo.ode.plugin.j2735.J2735SpeedConfidence;
import us.dot.its.jpo.ode.plugin.j2735.J2735SpeedandHeadingandThrottleConfidence;
import us.dot.its.jpo.ode.plugin.j2735.J2735ThrottleConfidence;

public class SpeedandHeadingandThrottleConfidenceBuilder {

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

        if (speedConfidence.get("heading").asLong() < HEADING_LOWER_BOUND
                || speedConfidence.get("heading").asLong() > HEADING_UPPER_BOUND) {
            throw new IllegalArgumentException("HeadingConfidence out of bounds [0..7]");
        }

        if (speedConfidence.get("speed").asLong() < SPEED_LOWER_BOUND
                || speedConfidence.get("speed").asLong() > SPEED_UPPER_BOUND) {
            throw new IllegalArgumentException("SpeedConfidence out of bounds [0..7]");
        }

        if (speedConfidence.get("throttle").asLong() < THROTTLE_LOWER_BOUND
                || speedConfidence.get("throttle").asLong() > THROTTLE_UPPER_BOUND) {
            throw new IllegalArgumentException("ThrottleConfidence out of bounds [0..3]");
        }

        J2735SpeedandHeadingandThrottleConfidence shtc = new J2735SpeedandHeadingandThrottleConfidence();

        shtc.setHeading(J2735HeadingConfidence.valueOf(speedConfidence.get("heading").asText().replaceAll("-", "_").toUpperCase()));
        shtc.setSpeed(J2735SpeedConfidence.valueOf(speedConfidence.get("speed").asText().replaceAll("-", "_").toUpperCase()));
        shtc.setThrottle(J2735ThrottleConfidence.valueOf(speedConfidence.get("throttle").asText().replaceAll("-", "_").toUpperCase()));

        return shtc;
    }

}
