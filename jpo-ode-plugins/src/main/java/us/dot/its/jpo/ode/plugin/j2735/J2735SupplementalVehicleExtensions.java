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
	private J2735WeatherReport weatherReport;
	private J2735WeatherProbe weatherProbe;
	private J2735ObstacleDetection obstacle;
	private J2735DisabledVehicle status;
	private J2735SpeedProfile speedProfile;
	private J2735RTCMPackage theRTCM;

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

	public J2735WeatherReport getWeatherReport() {
		return weatherReport;
	}

	public void setWeatherReport(J2735WeatherReport weatherReport) {
		this.weatherReport = weatherReport;
	}

	public J2735WeatherProbe getWeatherProbe() {
		return weatherProbe;
	}

	public void setWeatherProbe(J2735WeatherProbe weatherProbe) {
		this.weatherProbe = weatherProbe;
	}

	public J2735ObstacleDetection getObstacle() {
		return obstacle;
	}

	public void setObstacle(J2735ObstacleDetection obstacle) {
		this.obstacle = obstacle;
	}

	public J2735DisabledVehicle getStatus() {
		return status;
	}

	public void setStatus(J2735DisabledVehicle status) {
		this.status = status;
	}

	public J2735SpeedProfile getSpeedProfile() {
		return speedProfile;
	}

	public void setSpeedProfile(J2735SpeedProfile speedProfile) {
		this.speedProfile = speedProfile;
	}

	public J2735RTCMPackage getTheRTCM() {
		return theRTCM;
	}

	public void setTheRTCM(J2735RTCMPackage theRTCM) {
		this.theRTCM = theRTCM;
	}

}
