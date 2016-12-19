package us.dot.its.jpo.ode.plugin.j2735.oss;

import us.dot.its.jpo.ode.j2735.dsrc.SpeedandHeadingandThrottleConfidence;
import us.dot.its.jpo.ode.plugin.j2735.J2735HeadingConfidence;
import us.dot.its.jpo.ode.plugin.j2735.J2735SpeedConfidence;
import us.dot.its.jpo.ode.plugin.j2735.J2735SpeedandHeadingandThrottleConfidence;
import us.dot.its.jpo.ode.plugin.j2735.J2735ThrottleConfidence;

public class OssSpeedandHeadingandThrottleConfidence {

	public static J2735SpeedandHeadingandThrottleConfidence genericSpeedandHeadingandThrottleConfidence(
			SpeedandHeadingandThrottleConfidence speedConfidence) {
		J2735SpeedandHeadingandThrottleConfidence shtc = new J2735SpeedandHeadingandThrottleConfidence();
		
		shtc.heading = J2735HeadingConfidence.values()[speedConfidence.heading.indexOf()];
		shtc.speed = J2735SpeedConfidence.values()[speedConfidence.speed.indexOf()];
		shtc.throttle = J2735ThrottleConfidence.values()[speedConfidence.throttle.indexOf()];
		
		return shtc ;
	}

}
