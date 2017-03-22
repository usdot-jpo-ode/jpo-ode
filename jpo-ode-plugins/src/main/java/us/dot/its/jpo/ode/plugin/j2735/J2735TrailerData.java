package us.dot.its.jpo.ode.plugin.j2735;

import java.util.ArrayList;
import java.util.List;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735TrailerData extends Asn1Object {
	private static final long serialVersionUID = 1L;

	private J2735PivotPointDescription connection;
	private Integer sspRights;
	private List<J2735TrailerUnitDescription> units = new ArrayList<J2735TrailerUnitDescription>();

	public J2735PivotPointDescription getConnection() {
		return connection;
	}

	public void setConnection(J2735PivotPointDescription connection) {
		this.connection = connection;
	}

	public Integer getSspRights() {
		return sspRights;
	}

	public void setSspRights(Integer sspRights) {
		this.sspRights = sspRights;
	}

	public List<J2735TrailerUnitDescription> getUnits() {
		return units;
	}

	public void setUnits(List<J2735TrailerUnitDescription> units) {
		this.units = units;
	}

}
