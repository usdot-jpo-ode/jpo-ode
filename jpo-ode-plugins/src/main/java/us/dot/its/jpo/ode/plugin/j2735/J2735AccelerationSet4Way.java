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

public class J2735AccelerationSet4Way extends Asn1Object {
	private static final long serialVersionUID = 1L;

	private BigDecimal accelLat;
	private BigDecimal accelLong;
	private BigDecimal accelVert;
	private BigDecimal accelYaw;
	public BigDecimal getAccelLat() {
		return accelLat;
	}
	public void setAccelLat(BigDecimal accelLat) {
		this.accelLat = accelLat;
	}
	public BigDecimal getAccelLong() {
		return accelLong;
	}
	public void setAccelLong(BigDecimal accelLong) {
		this.accelLong = accelLong;
	}
	public BigDecimal getAccelVert() {
		return accelVert;
	}
	public void setAccelVert(BigDecimal accelVert) {
		this.accelVert = accelVert;
	}
	public BigDecimal getAccelYaw() {
		return accelYaw;
	}
	public void setAccelYaw(BigDecimal accelYaw) {
		this.accelYaw = accelYaw;
	}
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((accelLat == null) ? 0 : accelLat.hashCode());
        result = prime * result + ((accelLong == null) ? 0 : accelLong.hashCode());
        result = prime * result + ((accelVert == null) ? 0 : accelVert.hashCode());
        result = prime * result + ((accelYaw == null) ? 0 : accelYaw.hashCode());
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
        J2735AccelerationSet4Way other = (J2735AccelerationSet4Way) obj;
        if (accelLat == null) {
            if (other.accelLat != null)
                return false;
        } else if (!accelLat.equals(other.accelLat))
            return false;
        if (accelLong == null) {
            if (other.accelLong != null)
                return false;
        } else if (!accelLong.equals(other.accelLong))
            return false;
        if (accelVert == null) {
            if (other.accelVert != null)
                return false;
        } else if (!accelVert.equals(other.accelVert))
            return false;
        if (accelYaw == null) {
            if (other.accelYaw != null)
                return false;
        } else if (!accelYaw.equals(other.accelYaw))
            return false;
        return true;
    }

}
