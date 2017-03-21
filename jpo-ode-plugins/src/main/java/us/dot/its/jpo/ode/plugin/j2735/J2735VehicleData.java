package us.dot.its.jpo.ode.plugin.j2735;

import java.math.BigDecimal;

public class J2735VehicleData {

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
