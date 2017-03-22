package us.dot.its.jpo.ode.plugin.j2735;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735WeatherProbe extends Asn1Object {
	private static final long serialVersionUID = 1L;

	private Integer airPressure;
	private Integer airTemp;
	private J2735WiperSet rainRates;

	public Integer getAirPressure() {
		return airPressure;
	}

	public void setAirPressure(Integer airPressure) {
		this.airPressure = airPressure;
	}

	public Integer getAirTemp() {
		return airTemp;
	}

	public void setAirTemp(Integer airTemp) {
		this.airTemp = airTemp;
	}

	public J2735WiperSet getRainRates() {
		return rainRates;
	}

	public void setRainRates(J2735WiperSet rainRates) {
		this.rainRates = rainRates;
	}

}
