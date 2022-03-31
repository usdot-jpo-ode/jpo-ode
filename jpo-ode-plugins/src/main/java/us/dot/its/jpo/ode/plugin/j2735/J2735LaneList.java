package us.dot.its.jpo.ode.plugin.j2735;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735LaneList extends Asn1Object {
	private static final long serialVersionUID = 1L;
	private List<J2735GenericLane> GenericLane = new ArrayList<>();

	@JsonProperty("GenericLane")
	public List<J2735GenericLane> getLaneSet() {
		return GenericLane;
	}

	public void setLaneSet(List<J2735GenericLane> GenericLane) {
		this.GenericLane = GenericLane;
	}

}