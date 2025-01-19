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

public class J2735SupplementalVehicleExtensions extends J2735BsmPart2ExtensionBase {
	private static final long serialVersionUID = 1L;

	private Integer classification;
	private J2735VehicleClassification classDetails;
	private J2735VehicleData vehicleData;
	private J2735WeatherReport doNotUse1;
	private J2735WeatherProbe doNotUse2;
	private J2735ObstacleDetection doNotUse3;
	private J2735DisabledVehicle status;
	private J2735SpeedProfile doNotUse4;
	private J2735RTCMPackage doNotUse5;

	public Integer getClassification() {
		return classification;
	}

	public void setClassification(Integer classification) {
		this.classification = classification;
	}

	public J2735VehicleClassification getClassDetails() {
		return classDetails;
	}

	public void setClassDetails(J2735VehicleClassification classDetails) {
		this.classDetails = classDetails;
	}

	public J2735VehicleData getVehicleData() {
		return vehicleData;
	}

	public void setVehicleData(J2735VehicleData vehicleData) {
		this.vehicleData = vehicleData;
	}

	public J2735WeatherReport getDoNotUse1() {
		return doNotUse1;
	}

	public void setDoNotUse1(J2735WeatherReport doNotUse1) {
		this.doNotUse1 = doNotUse1;
	}

	public J2735WeatherProbe getDoNotUse2() {
		return doNotUse2;
	}

	public void setDoNotUse2(J2735WeatherProbe doNotUse2) {
		this.doNotUse2 = doNotUse2;
	}

	public J2735ObstacleDetection getDoNotUse3() {
		return doNotUse3;
	}

	public void setDoNotUse3(J2735ObstacleDetection doNotUse3) {
		this.doNotUse3 = doNotUse3;
	}

	public J2735DisabledVehicle getStatus() {
		return status;
	}

	public void setStatus(J2735DisabledVehicle status) {
		this.status = status;
	}

	public J2735SpeedProfile getDoNotUse4() {
		return doNotUse4;
	}

	public void setDoNotUse4(J2735SpeedProfile doNotUse4) {
		this.doNotUse4 = doNotUse4;
	}

	public J2735RTCMPackage getDoNotUse5() {
		return doNotUse5;
	}

	public void setDoNotUse5(J2735RTCMPackage doNotUse5) {
		this.doNotUse5 = doNotUse5;
	}

}
