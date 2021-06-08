package us.dot.its.jpo.ode.plugin.j2735;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735RestrictionUserType extends Asn1Object {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private J2735RestrictionAppliesTo basicType;

	public J2735RestrictionAppliesTo getBasicType() {
		return basicType;
	}

	public void setBasicType(J2735RestrictionAppliesTo basicType) {
		this.basicType = basicType;
	}

}
