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

public class J2735WeatherReport extends Asn1Object {
	private static final long serialVersionUID = 1L;

	private Integer friction;
	private J2735EssPrecipYesNo isRaining;
	private J2735EssPrecipSituation precipSituation;
	private BigDecimal rainRate;
	private BigDecimal roadFriction;
	private Integer solarRadiation;

	public Integer getFriction() {
		return friction;
	}

	public void setFriction(Integer friction) {
		this.friction = friction;
	}

	public J2735EssPrecipYesNo getIsRaining() {
		return isRaining;
	}

	public void setIsRaining(J2735EssPrecipYesNo isRaining) {
		this.isRaining = isRaining;
	}

	public J2735EssPrecipSituation getPrecipSituation() {
		return precipSituation;
	}

	public void setPrecipSituation(J2735EssPrecipSituation precipSituation) {
		this.precipSituation = precipSituation;
	}

	public BigDecimal getRainRate() {
		return rainRate;
	}

	public void setRainRate(BigDecimal rainRate) {
		this.rainRate = rainRate;
	}

	public BigDecimal getRoadFriction() {
		return roadFriction;
	}

	public void setRoadFriction(BigDecimal roadFriction) {
		this.roadFriction = roadFriction;
	}

	public Integer getSolarRadiation() {
		return solarRadiation;
	}

	public void setSolarRadiation(Integer solarRadiation) {
		this.solarRadiation = solarRadiation;
	}

}
