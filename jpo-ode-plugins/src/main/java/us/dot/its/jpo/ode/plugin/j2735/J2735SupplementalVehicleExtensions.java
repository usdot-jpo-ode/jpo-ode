package us.dot.its.jpo.ode.plugin.j2735;

import java.util.ArrayList;
import java.util.List;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735SupplementalVehicleExtensions extends Asn1Object implements J2735BsmPart2Extension {
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
	private List<J2735RegionalContent> regional = new ArrayList<>();

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

	public List<J2735RegionalContent> getRegional() {
		return regional;
	}

	public void setRegional(List<J2735RegionalContent> regional) {
		this.regional = regional;
	}

}
