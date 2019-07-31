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

import java.io.Serializable;
import java.math.BigDecimal;

public class J2735VehicleData implements Serializable{
	private static final long serialVersionUID = 1L;
	private J2735BumperHeights bumpers;
	private BigDecimal height;
	private Integer mass;
	private Integer trailerWeight;

	public J2735BumperHeights getBumpers() {
		return bumpers;
	}

	public void setBumpers(J2735BumperHeights bumpers) {
		this.bumpers = bumpers;
	}

	public BigDecimal getHeight() {
		return height;
	}

	public void setHeight(BigDecimal height) {
		this.height = height;
	}

	public Integer getMass() {
		return mass;
	}

	public void setMass(Integer mass) {
		this.mass = mass;
	}

	public Integer getTrailerWeight() {
		return trailerWeight;
	}

	public void setTrailerWeight(Integer trailerWeight) {
		this.trailerWeight = trailerWeight;
	}

}
