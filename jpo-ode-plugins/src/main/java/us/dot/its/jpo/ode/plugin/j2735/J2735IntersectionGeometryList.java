package us.dot.its.jpo.ode.plugin.j2735;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735IntersectionGeometryList extends Asn1Object {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<J2735IntersectionGeometry> intersectionGeometry = new ArrayList<>();

	@JsonProperty("intersectionGeometry")
	public List<J2735IntersectionGeometry> getIntersections() {
		return intersectionGeometry;
	}

	public void setIntersections(List<J2735IntersectionGeometry> intersectionGeometry) {
		this.intersectionGeometry = intersectionGeometry;
	}

}
