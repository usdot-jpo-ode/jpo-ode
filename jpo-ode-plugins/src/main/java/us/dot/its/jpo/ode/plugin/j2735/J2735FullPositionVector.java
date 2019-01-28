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

import java.math.BigDecimal;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735FullPositionVector extends Asn1Object {
	private static final long serialVersionUID = 1L;

	private OdePosition3D position;
	private BigDecimal heading;
	private J2735PositionalAccuracy posAccuracy;
	private J2735PositionConfidenceSet posConfidence;
	private J2735TransmissionAndSpeed speed;
	private J2735SpeedandHeadingandThrottleConfidence speedConfidence;
	private J2735TimeConfidence timeConfidence;
	private J2735DDateTime utcTime;

	public OdePosition3D getPosition() {
		return position;
	}

	public void setPosition(OdePosition3D position) {
		this.position = position;
	}

	public BigDecimal getHeading() {
		return heading;
	}

	public void setHeading(BigDecimal heading) {
		this.heading = heading;
	}

	public J2735PositionalAccuracy getPosAccuracy() {
		return posAccuracy;
	}

	public void setPosAccuracy(J2735PositionalAccuracy posAccuracy) {
		this.posAccuracy = posAccuracy;
	}

	public J2735PositionConfidenceSet getPosConfidence() {
		return posConfidence;
	}

	public void setPosConfidence(J2735PositionConfidenceSet posConfidence) {
		this.posConfidence = posConfidence;
	}

	public J2735TransmissionAndSpeed getSpeed() {
		return speed;
	}

	public void setSpeed(J2735TransmissionAndSpeed speed) {
		this.speed = speed;
	}

	public J2735SpeedandHeadingandThrottleConfidence getSpeedConfidence() {
		return speedConfidence;
	}

	public void setSpeedConfidence(J2735SpeedandHeadingandThrottleConfidence speedConfidence) {
		this.speedConfidence = speedConfidence;
	}

	public J2735TimeConfidence getTimeConfidence() {
		return timeConfidence;
	}

	public void setTimeConfidence(J2735TimeConfidence timeConfidence) {
		this.timeConfidence = timeConfidence;
	}

	public J2735DDateTime getUtcTime() {
		return utcTime;
	}

	public void setUtcTime(J2735DDateTime utcTime) {
		this.utcTime = utcTime;
	}

}
