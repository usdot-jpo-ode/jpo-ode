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

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735Bsm extends Asn1Object {
	private static final long serialVersionUID = 1L;

	private J2735BsmCoreData coreData;
	
	@JacksonXmlElementWrapper(useWrapping=false)
	private List<J2735BsmPart2Content> partII = new ArrayList<>();

	public J2735BsmCoreData getCoreData() {
		return coreData;
	}

	public void setCoreData(J2735BsmCoreData coreData) {
		this.coreData = coreData;
	}

	public List<J2735BsmPart2Content> getPartII() {
		return partII;
	}

	public void setPartII(List<J2735BsmPart2Content> partII) {
		this.partII = partII;
	}

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((coreData == null) ? 0 : coreData.hashCode());
        result = prime * result + ((partII == null) ? 0 : partII.hashCode());
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
        J2735Bsm other = (J2735Bsm) obj;
        if (coreData == null) {
            if (other.coreData != null)
                return false;
        } else if (!coreData.equals(other.coreData))
            return false;
        if (partII == null) {
            if (other.partII != null)
                return false;
        } else if (!partII.equals(other.partII))
            return false;
        return true;
    }
	
	
}
