package us.dot.its.jpo.ode.plugin.j2735;

import java.util.ArrayList;
import java.util.List;

public class J2735VehicleClassification {

	public J2735FuelType fuelType;
	public J2735VehicleType hpmsType;
	public Integer iso3883;
	public Integer keyType;
	public List<J2735RegionalContent> regional = 
			new ArrayList<J2735RegionalContent>();
	public J2735ResponderGroupAffected responderType;
	public J2735NamedNumber responseEquip;
	public J2735BasicVehicleRole role;
	public J2735NamedNumber vehicleType;

}
