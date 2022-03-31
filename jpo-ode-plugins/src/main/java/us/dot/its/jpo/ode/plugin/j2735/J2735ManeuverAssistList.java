package us.dot.its.jpo.ode.plugin.j2735;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735ManeuverAssistList extends Asn1Object {
	private static final long serialVersionUID = 1L;
	@JacksonXmlElementWrapper(useWrapping = false)
	private List<J2735ConnectionManueverAssist> maneuverAssistList = new ArrayList<>();

	public List<J2735ConnectionManueverAssist> getManeuverAssistList() {
		return maneuverAssistList;
	}

	public void setManeuverAssistList(List<J2735ConnectionManueverAssist> maneuverAssistList) {
		this.maneuverAssistList = maneuverAssistList;
	}

}
