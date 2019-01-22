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

public class J2735RTCMheader extends Asn1Object {
	private static final long serialVersionUID = 1L;

	private J2735AntennaOffsetSet offsetSet;
	private J2735BitString status;

	public J2735AntennaOffsetSet getOffsetSet() {
		return offsetSet;
	}

	public J2735RTCMheader setOffsetSet(J2735AntennaOffsetSet offsetSet) {
		this.offsetSet = offsetSet;
		return this;
	}

	public J2735BitString getStatus() {
		return status;
	}

	public J2735RTCMheader setStatus(J2735BitString status) {
		this.status = status;
		return this;
	}

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((offsetSet == null) ? 0 : offsetSet.hashCode());
    result = prime * result + ((status == null) ? 0 : status.hashCode());
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
    J2735RTCMheader other = (J2735RTCMheader) obj;
    if (offsetSet == null) {
      if (other.offsetSet != null)
        return false;
    } else if (!offsetSet.equals(other.offsetSet))
      return false;
    if (status == null) {
      if (other.status != null)
        return false;
    } else if (!status.equals(other.status))
      return false;
    return true;
  }

}
