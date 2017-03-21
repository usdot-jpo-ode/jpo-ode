package us.dot.its.jpo.ode.plugin.j2735;

import java.math.BigDecimal;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735TransmissionAndSpeed extends Asn1Object {
	private static final long serialVersionUID = 1L;

	private BigDecimal speed;
	private J2735TransmissionState transmisson;

	public BigDecimal getSpeed() {
		return speed;
	}

	public void setSpeed(BigDecimal speed) {
		this.speed = speed;
	}

	public J2735TransmissionState getTransmisson() {
		return transmisson;
	}

	public void setTransmisson(J2735TransmissionState transmisson) {
		this.transmisson = transmisson;
	}

}
