package us.dot.its.jpo.ode.plugin.j2735;

public class J2735VehicleSafetyExtensions extends J2735BsmPart2ExtensionBase {
	private static final long serialVersionUID = 1L;

	private J2735VehicleEventFlags events;
	private J2735PathHistory pathHistory;
	private J2735PathPrediction pathPrediction;
	private J2735ExteriorLights lights;

	public J2735VehicleEventFlags getEvents() {
		return events;
	}

	public void setEvents(J2735VehicleEventFlags events) {
		this.events = events;
	}

	public J2735PathHistory getPathHistory() {
		return pathHistory;
	}

	public void setPathHistory(J2735PathHistory pathHistory) {
		this.pathHistory = pathHistory;
	}

	public J2735PathPrediction getPathPrediction() {
		return pathPrediction;
	}

	public void setPathPrediction(J2735PathPrediction pathPrediction) {
		this.pathPrediction = pathPrediction;
	}

	public J2735ExteriorLights getLights() {
		return lights;
	}

	public void setLights(J2735ExteriorLights lights) {
		this.lights = lights;
	}
}
