package us.dot.its.jpo.ode.plugins.oss.j2735;

import us.dot.its.jpo.ode.j2735.dsrc.VehicleSize;
import us.dot.its.jpo.ode.plugin.j2735.J2735VehicleSize;

public class OssVehicleSize {
	private VehicleSize vehicleSize;
	private J2735VehicleSize genericVehicleSize;

	public OssVehicleSize(VehicleSize size) {
		this.vehicleSize = size;
		this.genericVehicleSize = new J2735VehicleSize();
		this.genericVehicleSize.setLength(size.length.intValue());
		this.genericVehicleSize.setWidth(size.width.intValue());
	}

	public J2735VehicleSize getGenericVehicleSize() {
		return genericVehicleSize;
	}

	public VehicleSize getVehicleSize() {
		return vehicleSize;
	}

}
