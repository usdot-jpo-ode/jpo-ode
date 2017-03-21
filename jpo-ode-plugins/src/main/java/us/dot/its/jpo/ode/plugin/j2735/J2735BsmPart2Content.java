package us.dot.its.jpo.ode.plugin.j2735;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735BsmPart2Content extends Asn1Object {
	private static final long serialVersionUID = 1L;

	public enum J2735BsmPart2Id {
		vehicleSafetyExt(J2735VehicleSafetyExtensions.class), specialVehicleExt(
				J2735SpecialVehicleExtensions.class), supplementalVehicleExt(J2735SupplementalVehicleExtensions.class);

		Class<?> type;

		private J2735BsmPart2Id(Class<?> type) {
			this.type = type;
		}

	}

	private J2735BsmPart2Id id;
	private J2735BsmPart2Extension value;

	public J2735BsmPart2Id getId() {
		return id;
	}

	public void setId(J2735BsmPart2Id id) {
		this.id = id;
	}

	public J2735BsmPart2Extension getValue() {
		return value;
	}

	public void setValue(J2735BsmPart2Extension value) {
		this.value = value;
	}
}
