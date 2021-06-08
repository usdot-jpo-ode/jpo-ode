package us.dot.its.jpo.ode.plugin.j2735;

import java.util.ArrayList;
import java.util.List;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735RestrictionClassList extends Asn1Object {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private List<J2735RestrictionClassAssignment> restrictionList = new ArrayList<>();

	public List<J2735RestrictionClassAssignment> getRestrictionList() {
		return restrictionList;
	}

	public void setRestrictionList(List<J2735RestrictionClassAssignment> restrictionList) {
		this.restrictionList = restrictionList;
	}

}
