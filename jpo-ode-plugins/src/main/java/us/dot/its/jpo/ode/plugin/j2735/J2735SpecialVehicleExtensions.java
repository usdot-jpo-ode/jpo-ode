package us.dot.its.jpo.ode.plugin.j2735;

public class J2735SpecialVehicleExtensions extends J2735BsmPart2ExtensionBase {
	private static final long serialVersionUID = 1L;

	private J2735EmergencyDetails vehicleAlerts;
	private J2735EventDescription description;
	private J2735TrailerData trailers;

	public J2735EmergencyDetails getVehicleAlerts() {
		return vehicleAlerts;
	}

	public void setVehicleAlerts(J2735EmergencyDetails vehicleAlerts) {
		this.vehicleAlerts = vehicleAlerts;
	}

	public J2735EventDescription getDescription() {
		return description;
	}

	public void setDescription(J2735EventDescription description) {
		this.description = description;
	}

	public J2735TrailerData getTrailers() {
		return trailers;
	}

	public void setTrailers(J2735TrailerData trailers) {
		this.trailers = trailers;
	}

}
