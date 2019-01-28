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

public class J2735ObstacleDetection extends Asn1Object {
	private static final long serialVersionUID = 1L;

	private J2735DDateTime dateTime;
	private Integer description;
	private J2735NamedNumber locationDetails;
	private BigDecimal obDirect;
	private Integer obDist;
	private J2735BitString vertEvent;

	public J2735DDateTime getDateTime() {
		return dateTime;
	}

	public void setDateTime(J2735DDateTime dateTime) {
		this.dateTime = dateTime;
	}

	public Integer getDescription() {
		return description;
	}

	public void setDescription(Integer description) {
		this.description = description;
	}

	public J2735NamedNumber getLocationDetails() {
		return locationDetails;
	}

	public void setLocationDetails(J2735NamedNumber locationDetails) {
		this.locationDetails = locationDetails;
	}

	public BigDecimal getObDirect() {
		return obDirect;
	}

	public void setObDirect(BigDecimal obDirect) {
		this.obDirect = obDirect;
	}

	public Integer getObDist() {
		return obDist;
	}

	public void setObDist(Integer obDist) {
		this.obDist = obDist;
	}

	public J2735BitString getVertEvent() {
		return vertEvent;
	}

	public void setVertEvent(J2735BitString vertEvent) {
		this.vertEvent = vertEvent;
	}

}
