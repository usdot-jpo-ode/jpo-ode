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

public class J2735BsmPart2Content extends Asn1Object {
	private static final long serialVersionUID = 1L;

	public enum J2735BsmPart2Id {
	   VehicleSafetyExtensions(J2735VehicleSafetyExtensions.class), SpecialVehicleExtensions(
				J2735SpecialVehicleExtensions.class), SupplementalVehicleExtensions(J2735SupplementalVehicleExtensions.class);

		Class<?> type;

		private J2735BsmPart2Id(Class<?> type) {
			this.type = type;
		}

	}

	private J2735BsmPart2Id id;
	private J2735BsmPart2ExtensionBase value;

	public J2735BsmPart2Id getId() {
		return id;
	}

	public void setId(J2735BsmPart2Id id) {
		this.id = id;
	}

	public J2735BsmPart2ExtensionBase getValue() {
		return value;
	}

	public void setValue(J2735BsmPart2ExtensionBase value) {
		this.value = value;
	}

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((value == null) ? 0 : value.hashCode());
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
        J2735BsmPart2Content other = (J2735BsmPart2Content) obj;
        if (id != other.id)
            return false;
        if (value == null) {
            if (other.value != null)
                return false;
        } else if (!value.equals(other.value))
            return false;
        return true;
    }
}
