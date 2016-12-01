package us.dot.its.jpo.ode.plugin.j2735;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;


public class J2735BsmPart2Content implements Asn1Object{
	public enum J2735BsmPart2Id {
		vehicleSafetyExt(J2735VehicleSafetyExtensions.class),
		specialVehicleExt(J2735SpecialVehicleExtensions.class),
		supplementalVehicleExt(J2735SupplementalVehicleExtensions.class);
		
		Class<?> type;

		private J2735BsmPart2Id(Class<?> type) {
			this.type = type;
		}
		
		
	}

	public J2735BsmPart2Id id;
	public J2735BsmPart2Extension value;
}
