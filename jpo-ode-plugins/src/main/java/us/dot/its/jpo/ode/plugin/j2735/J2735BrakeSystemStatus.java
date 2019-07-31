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

public class J2735BrakeSystemStatus extends Asn1Object {
    private static final long serialVersionUID = 1L;

    private J2735BitString wheelBrakes;
    private String traction;
    private String abs;
    private String scs;
    private String brakeBoost;
    private String auxBrakes;

    public J2735BitString getWheelBrakes() {
        return wheelBrakes;
    }

    public void setWheelBrakes(J2735BitString wheelBrakes) {
        this.wheelBrakes = wheelBrakes;
    }

    public String getTraction() {
        return traction;
    }

    public void setTraction(String traction) {
        this.traction = traction;
    }

    public String getAbs() {
        return abs;
    }

    public void setAbs(String abs) {
        this.abs = abs;
    }

    public String getScs() {
        return scs;
    }

    public void setScs(String scs) {
        this.scs = scs;
    }

    public String getBrakeBoost() {
        return brakeBoost;
    }

    public void setBrakeBoost(String brakeBoost) {
        this.brakeBoost = brakeBoost;
    }

    public String getAuxBrakes() {
        return auxBrakes;
    }

    public void setAuxBrakes(String auxBrakes) {
        this.auxBrakes = auxBrakes;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((abs == null) ? 0 : abs.hashCode());
        result = prime * result + ((auxBrakes == null) ? 0 : auxBrakes.hashCode());
        result = prime * result + ((brakeBoost == null) ? 0 : brakeBoost.hashCode());
        result = prime * result + ((scs == null) ? 0 : scs.hashCode());
        result = prime * result + ((traction == null) ? 0 : traction.hashCode());
        result = prime * result + ((wheelBrakes == null) ? 0 : wheelBrakes.hashCode());
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
        J2735BrakeSystemStatus other = (J2735BrakeSystemStatus) obj;
        if (abs == null) {
            if (other.abs != null)
                return false;
        } else if (!abs.equals(other.abs))
            return false;
        if (auxBrakes == null) {
            if (other.auxBrakes != null)
                return false;
        } else if (!auxBrakes.equals(other.auxBrakes))
            return false;
        if (brakeBoost == null) {
            if (other.brakeBoost != null)
                return false;
        } else if (!brakeBoost.equals(other.brakeBoost))
            return false;
        if (scs == null) {
            if (other.scs != null)
                return false;
        } else if (!scs.equals(other.scs))
            return false;
        if (traction == null) {
            if (other.traction != null)
                return false;
        } else if (!traction.equals(other.traction))
            return false;
        if (wheelBrakes == null) {
            if (other.wheelBrakes != null)
                return false;
        } else if (!wheelBrakes.equals(other.wheelBrakes))
            return false;
        return true;
    }

}
