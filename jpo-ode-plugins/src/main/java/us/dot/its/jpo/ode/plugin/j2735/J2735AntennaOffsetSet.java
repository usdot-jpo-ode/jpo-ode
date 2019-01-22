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

public class J2735AntennaOffsetSet extends Asn1Object {
    private static final long serialVersionUID = 1L;

    private BigDecimal antOffsetX;
    private BigDecimal antOffsetY;
    private BigDecimal antOffsetZ;

    public BigDecimal getAntOffsetX() {
        return antOffsetX;
    }

    public J2735AntennaOffsetSet setAntOffsetX(BigDecimal antOffsetX) {
        this.antOffsetX = antOffsetX;
        return this;
    }

    public BigDecimal getAntOffsetY() {
        return antOffsetY;
    }

    public J2735AntennaOffsetSet setAntOffsetY(BigDecimal antOffsetY) {
        this.antOffsetY = antOffsetY;
        return this;
    }

    public BigDecimal getAntOffsetZ() {
        return antOffsetZ;
    }

    public J2735AntennaOffsetSet setAntOffsetZ(BigDecimal antOffsetZ) {
        this.antOffsetZ = antOffsetZ;
        return this;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((antOffsetX == null) ? 0 : antOffsetX.hashCode());
      result = prime * result + ((antOffsetY == null) ? 0 : antOffsetY.hashCode());
      result = prime * result + ((antOffsetZ == null) ? 0 : antOffsetZ.hashCode());
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
      J2735AntennaOffsetSet other = (J2735AntennaOffsetSet) obj;
      if (antOffsetX == null) {
        if (other.antOffsetX != null)
          return false;
      } else if (!antOffsetX.equals(other.antOffsetX))
        return false;
      if (antOffsetY == null) {
        if (other.antOffsetY != null)
          return false;
      } else if (!antOffsetY.equals(other.antOffsetY))
        return false;
      if (antOffsetZ == null) {
        if (other.antOffsetZ != null)
          return false;
      } else if (!antOffsetZ.equals(other.antOffsetZ))
        return false;
      return true;
    }

}
