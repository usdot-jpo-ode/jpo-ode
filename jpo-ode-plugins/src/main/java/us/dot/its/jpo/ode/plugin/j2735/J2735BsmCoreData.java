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

public class J2735BsmCoreData extends Asn1Object {
	private static final long serialVersionUID = 1L;

	private Integer msgCnt;
	private String id;
	private Integer secMark;

	private OdePosition3D position;
	private J2735AccelerationSet4Way accelSet;
	private J2735PositionalAccuracy accuracy;

	private J2735TransmissionState transmission;
	private BigDecimal speed;
	private BigDecimal heading;
	private BigDecimal angle;
	private J2735BrakeSystemStatus brakes;
	private J2735VehicleSize size;

	public Integer getMsgCnt() {
		return msgCnt;
	}

	public void setMsgCnt(Integer msgCnt) {
		this.msgCnt = msgCnt;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getSecMark() {
		return secMark;
	}

	public void setSecMark(Integer secMark) {
		this.secMark = secMark;
	}

	public OdePosition3D getPosition() {
		return position;
	}

	public void setPosition(OdePosition3D position) {
		this.position = position;
	}

	public J2735AccelerationSet4Way getAccelSet() {
		return accelSet;
	}

	public void setAccelSet(J2735AccelerationSet4Way accelSet) {
		this.accelSet = accelSet;
	}

	public J2735PositionalAccuracy getAccuracy() {
		return accuracy;
	}

	public void setAccuracy(J2735PositionalAccuracy accuracy) {
		this.accuracy = accuracy;
	}

	public J2735TransmissionState getTransmission() {
		return transmission;
	}

	public void setTransmission(J2735TransmissionState transmission) {
		this.transmission = transmission;
	}

	public BigDecimal getSpeed() {
		return speed;
	}

	public void setSpeed(BigDecimal speed) {
		this.speed = speed;
	}

	public BigDecimal getHeading() {
		return heading;
	}

	public void setHeading(BigDecimal heading) {
		this.heading = heading;
	}

	public BigDecimal getAngle() {
		return angle;
	}

	public void setAngle(BigDecimal angle) {
		this.angle = angle;
	}

	public J2735BrakeSystemStatus getBrakes() {
		return brakes;
	}

	public void setBrakes(J2735BrakeSystemStatus brakes) {
		this.brakes = brakes;
	}

	public J2735VehicleSize getSize() {
		return size;
	}

	public void setSize(J2735VehicleSize size) {
		this.size = size;
	}

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((accelSet == null) ? 0 : accelSet.hashCode());
        result = prime * result + ((accuracy == null) ? 0 : accuracy.hashCode());
        result = prime * result + ((angle == null) ? 0 : angle.hashCode());
        result = prime * result + ((brakes == null) ? 0 : brakes.hashCode());
        result = prime * result + ((heading == null) ? 0 : heading.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((msgCnt == null) ? 0 : msgCnt.hashCode());
        result = prime * result + ((position == null) ? 0 : position.hashCode());
        result = prime * result + ((secMark == null) ? 0 : secMark.hashCode());
        result = prime * result + ((size == null) ? 0 : size.hashCode());
        result = prime * result + ((speed == null) ? 0 : speed.hashCode());
        result = prime * result + ((transmission == null) ? 0 : transmission.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        J2735BsmCoreData other = (J2735BsmCoreData) obj;
        if (accelSet == null) {
            if (other.accelSet != null)
                return false;
        } else if (!accelSet.equals(other.accelSet))
            return false;
        if (accuracy == null) {
            if (other.accuracy != null)
                return false;
        } else if (!accuracy.equals(other.accuracy))
            return false;
        if (angle == null) {
            if (other.angle != null)
                return false;
        } else if (!angle.equals(other.angle))
            return false;
        if (brakes == null) {
            if (other.brakes != null)
                return false;
        } else if (!brakes.equals(other.brakes))
            return false;
        if (heading == null) {
            if (other.heading != null)
                return false;
        } else if (!heading.equals(other.heading))
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (msgCnt == null) {
            if (other.msgCnt != null)
                return false;
        } else if (!msgCnt.equals(other.msgCnt))
            return false;
        if (position == null) {
            if (other.position != null)
                return false;
        } else if (!position.equals(other.position))
            return false;
        if (secMark == null) {
            if (other.secMark != null)
                return false;
        } else if (!secMark.equals(other.secMark))
            return false;
        if (size == null) {
            if (other.size != null)
                return false;
        } else if (!size.equals(other.size))
            return false;
        if (speed == null) {
            if (other.speed != null)
                return false;
        } else if (!speed.equals(other.speed))
            return false;
        if (transmission != other.transmission)
            return false;
        return true;
    }

}
