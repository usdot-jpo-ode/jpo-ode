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

import java.util.Arrays;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;


public class J2735RegionalContent extends Asn1Object {
	private static final long serialVersionUID = 1L;
	
	public enum RegionId {
				NOREGION(J2735RegionalExtension.class), //Use default supplied stubs
				ADDGRPA(J2735RegionalExtension.class),  //RegionId ::= 1 -- USA
				ADDGRPB(J2735RegionalExtension.class),  //RegionId ::= 2 -- Japan
				ADDGRPC(J2735RegionalExtension.class)   //RegionId ::= 3 -- EU
				; 
		
		Class<?> type;

		private RegionId(Class<?> type) {
			this.type = type;
		}
		
		
	}

	private Integer id;
	private byte[] value;
	public Integer getId() {
		return id;
	}
	public J2735RegionalContent setId(Integer id) {
		this.id = id;
		return this;
	}
	public byte[] getValue() {
		return value;
	}
	public J2735RegionalContent setValue(byte[] value) {
		this.value = value;
		return this;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + Arrays.hashCode(value);
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
		J2735RegionalContent other = (J2735RegionalContent) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (!Arrays.equals(value, other.value))
			return false;
		return true;
	}
}
