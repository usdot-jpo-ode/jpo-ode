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
package us.dot.its.jpo.ode.plugin.j2735;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735SpeedandHeadingandThrottleConfidence extends Asn1Object {
	private static final long serialVersionUID = 1L;

	private J2735HeadingConfidence heading;
	private J2735SpeedConfidence speed;
	private J2735ThrottleConfidence throttle;

	public J2735HeadingConfidence getHeading() {
		return heading;
	}

	public void setHeading(J2735HeadingConfidence heading) {
		this.heading = heading;
	}

	public J2735SpeedConfidence getSpeed() {
		return speed;
	}

	public void setSpeed(J2735SpeedConfidence speed) {
		this.speed = speed;
	}

	public J2735ThrottleConfidence getThrottle() {
		return throttle;
	}

	public void setThrottle(J2735ThrottleConfidence throttle) {
		this.throttle = throttle;
	}

}
