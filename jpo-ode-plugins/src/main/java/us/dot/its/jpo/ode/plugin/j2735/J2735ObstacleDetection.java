package us.dot.its.jpo.ode.plugin.j2735;

import java.math.BigDecimal;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735ObstacleDetection extends Asn1Object {
	private static final long serialVersionUID = 1L;

	private J2735DDateTime dateTime;
	private Integer description;
	private J2735NamedNumber locationDetails;
	private BigDecimal obDirect;
	private Integer obDist;
	private J2735VertEvent vertEvent;

	public J2735DDateTime getDateTime() {
		return dateTime;
	}

	public void setDateTime(J2735DDateTime dateTime) {
		this.dateTime = dateTime;
	}

	public Integer getDescription() {
		return description;
	}

	public void setDescription(Integer description) {
		this.description = description;
	}

	public J2735NamedNumber getLocationDetails() {
		return locationDetails;
	}

	public void setLocationDetails(J2735NamedNumber locationDetails) {
		this.locationDetails = locationDetails;
	}

	public BigDecimal getObDirect() {
		return obDirect;
	}

	public void setObDirect(BigDecimal obDirect) {
		this.obDirect = obDirect;
	}

	public Integer getObDist() {
		return obDist;
	}

	public void setObDist(Integer obDist) {
		this.obDist = obDist;
	}

	public J2735VertEvent getVertEvent() {
		return vertEvent;
	}

	public void setVertEvent(J2735VertEvent vertEvent) {
		this.vertEvent = vertEvent;
	}

}
