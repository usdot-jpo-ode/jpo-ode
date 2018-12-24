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
package us.dot.its.jpo.ode.asn1.j2735.msg.ids;

public class DSRCMessageID extends ConnectedVehicleMessageID {

	private long dsrcMsgID = 0;
	
	public DSRCMessageID(long dsrcMsgID) {
		this.dsrcMsgID = dsrcMsgID;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (dsrcMsgID ^ (dsrcMsgID >>> 32));
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
		DSRCMessageID other = (DSRCMessageID) obj;
		if (dsrcMsgID != other.dsrcMsgID)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "DSRCMessageID [dsrcMsgID=" + dsrcMsgID + "]";
	}

	@Override
	public long getMessageId() {
		return dsrcMsgID;
	}

}
