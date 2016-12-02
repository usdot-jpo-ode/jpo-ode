package us.dot.its.jpo.ode.plugin.j2735;

import java.util.ArrayList;
import java.util.List;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735SupplementalVehicleExtensions extends Asn1Object implements J2735BsmPart2Extension {
	private static final long serialVersionUID = 1L;
	
	public Integer classification;
	public J2735VehicleClassification classDetails;
	public J2735VehicleData vehicleData;
	public J2735WeatherReport weatherReport;
	public J2735WeatherProbe weatherProbe;
	public J2735ObstacleDetection obstacle;
	public J2735DisabledVehicle status;
	public J2735SpeedProfile speedProfile;
	public J2735RTCMPackage theRTCM;
	public List<J2735RegionalContent> regional = 
			new ArrayList<J2735RegionalContent>();

}
