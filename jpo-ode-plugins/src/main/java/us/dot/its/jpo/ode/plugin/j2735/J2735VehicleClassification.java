package us.dot.its.jpo.ode.plugin.j2735;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class J2735VehicleClassification implements Serializable{
	private static final long serialVersionUID = 1L;
	private J2735FuelType fuelType;
	private J2735VehicleType hpmsType;
	private Integer iso3883;
	private Integer keyType;
	private List<J2735RegionalContent> regional = new ArrayList<J2735RegionalContent>();
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

	public List<J2735RegionalContent> getRegional() {
		return regional;
	}

	public void setRegional(List<J2735RegionalContent> regional) {
		this.regional = regional;
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
