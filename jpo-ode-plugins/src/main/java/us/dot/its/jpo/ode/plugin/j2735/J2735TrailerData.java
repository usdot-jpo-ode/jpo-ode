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

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735TrailerData extends Asn1Object {
	private static final long serialVersionUID = 1L;

	private J2735PivotPointDescription connection;
	private Integer sspRights;
	private List<J2735TrailerUnitDescription> units = new ArrayList<>();

	public J2735PivotPointDescription getConnection() {
		return connection;
	}

	public void setConnection(J2735PivotPointDescription connection) {
		this.connection = connection;
	}

	public Integer getSspRights() {
		return sspRights;
	}

	public void setSspRights(Integer sspRights) {
		this.sspRights = sspRights;
	}

	public List<J2735TrailerUnitDescription> getUnits() {
		return units;
	}

	public void setUnits(List<J2735TrailerUnitDescription> units) {
		this.units = units;
	}

}
