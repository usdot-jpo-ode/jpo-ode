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

public class J2735VehicleClassification implements Serializable{
	private static final long serialVersionUID = 1L;
	private J2735FuelType fuelType;
	private J2735VehicleType hpmsType;
	private Integer iso3883;
	private Integer keyType;
	private J2735ResponderGroupAffected responderType;
	private J2735NamedNumber responseEquip;
	private J2735BasicVehicleRole role;
	private J2735NamedNumber vehicleType;

	public J2735FuelType getFuelType() {
		return fuelType;
	}

	public void setFuelType(J2735FuelType fuelType) {
		this.fuelType = fuelType;
	}

	public J2735VehicleType getHpmsType() {
		return hpmsType;
	}

	public void setHpmsType(J2735VehicleType hpmsType) {
		this.hpmsType = hpmsType;
	}

	public Integer getIso3883() {
		return iso3883;
	}

	public void setIso3883(Integer iso3883) {
		this.iso3883 = iso3883;
	}

	public Integer getKeyType() {
		return keyType;
	}

	public void setKeyType(Integer keyType) {
		this.keyType = keyType;
	}

	public J2735ResponderGroupAffected getResponderType() {
		return responderType;
	}

	public void setResponderType(J2735ResponderGroupAffected responderType) {
		this.responderType = responderType;
	}

	public J2735NamedNumber getResponseEquip() {
		return responseEquip;
	}

	public void setResponseEquip(J2735NamedNumber responseEquip) {
		this.responseEquip = responseEquip;
	}

	public J2735BasicVehicleRole getRole() {
		return role;
	}

	public void setRole(J2735BasicVehicleRole role) {
		this.role = role;
	}

	public J2735NamedNumber getVehicleType() {
		return vehicleType;
	}

	public void setVehicleType(J2735NamedNumber vehicleType) {
		this.vehicleType = vehicleType;
	}

}
