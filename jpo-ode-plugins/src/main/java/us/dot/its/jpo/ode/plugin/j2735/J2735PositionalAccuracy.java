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

public class J2735PositionalAccuracy extends Asn1Object {
	private static final long serialVersionUID = 1L;

	private BigDecimal semiMajor;
	private BigDecimal semiMinor;
	private BigDecimal orientation;

	public BigDecimal getSemiMajor() {
		return semiMajor;
	}

	public void setSemiMajor(BigDecimal semiMajor) {
		this.semiMajor = semiMajor;
	}

	public BigDecimal getSemiMinor() {
		return semiMinor;
	}

	public void setSemiMinor(BigDecimal semiMinor) {
		this.semiMinor = semiMinor;
	}

	public BigDecimal getOrientation() {
		return orientation;
	}

	public void setOrientation(BigDecimal orientation) {
		this.orientation = orientation;
	}

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((orientation == null) ? 0 : orientation.hashCode());
        result = prime * result + ((semiMajor == null) ? 0 : semiMajor.hashCode());
        result = prime * result + ((semiMinor == null) ? 0 : semiMinor.hashCode());
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
        J2735PositionalAccuracy other = (J2735PositionalAccuracy) obj;
        if (orientation == null) {
            if (other.orientation != null)
                return false;
        } else if (!orientation.equals(other.orientation))
            return false;
        if (semiMajor == null) {
            if (other.semiMajor != null)
                return false;
        } else if (!semiMajor.equals(other.semiMajor))
            return false;
        if (semiMinor == null) {
            if (other.semiMinor != null)
                return false;
        } else if (!semiMinor.equals(other.semiMinor))
            return false;
        return true;
    }

}
