package us.dot.its.jpo.ode.plugin.j2735.oss;

import us.dot.its.jpo.ode.j2735.dsrc.SpeedandHeadingandThrottleConfidence;
import us.dot.its.jpo.ode.plugin.j2735.J2735HeadingConfidence;
import us.dot.its.jpo.ode.plugin.j2735.J2735SpeedConfidence;
import us.dot.its.jpo.ode.plugin.j2735.J2735SpeedandHeadingandThrottleConfidence;
import us.dot.its.jpo.ode.plugin.j2735.J2735ThrottleConfidence;

public class OssSpeedandHeadingandThrottleConfidence {

    private static final long HEADING_LOWER_BOUND = 0;
    private static final long HEADING_UPPER_BOUND = 7;
    private static final long SPEED_LOWER_BOUND = 0;
    private static final long SPEED_UPPER_BOUND = 7;
    private static final long THROTTLE_LOWER_BOUND = 0;
    private static final long THROTTLE_UPPER_BOUND = 3;

    public static J2735SpeedandHeadingandThrottleConfidence genericSpeedandHeadingandThrottleConfidence(
            SpeedandHeadingandThrottleConfidence speedConfidence) {

        if (speedConfidence.heading.longValue() < HEADING_LOWER_BOUND
                || speedConfidence.heading.longValue() > HEADING_UPPER_BOUND) {
            throw new IllegalArgumentException("HeadingConfidence out of bounds [0..7]");
        }

        if (speedConfidence.speed.longValue() < SPEED_LOWER_BOUND
                || speedConfidence.speed.longValue() > SPEED_UPPER_BOUND) {
            throw new IllegalArgumentException("SpeedConfidence out of bounds [0..7]");
        }

        if (speedConfidence.throttle.longValue() < THROTTLE_LOWER_BOUND
                || speedConfidence.throttle.longValue() > THROTTLE_UPPER_BOUND) {
            throw new IllegalArgumentException("ThrottleConfidence out of bounds [0..3]");
        }

        J2735SpeedandHeadingandThrottleConfidence shtc = new J2735SpeedandHeadingandThrottleConfidence();

        shtc.heading = J2735HeadingConfidence.values()[speedConfidence.heading.indexOf()];
        shtc.speed = J2735SpeedConfidence.values()[speedConfidence.speed.indexOf()];
        shtc.throttle = J2735ThrottleConfidence.values()[speedConfidence.throttle.indexOf()];

        return shtc;
    }

}
