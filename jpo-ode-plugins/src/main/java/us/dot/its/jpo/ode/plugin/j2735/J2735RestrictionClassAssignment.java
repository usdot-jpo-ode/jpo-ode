package us.dot.its.jpo.ode.plugin.j2735;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735RestrictionClassAssignment extends Asn1Object {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Integer id;
	private J2735RestrictionUserTypeList users;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public J2735RestrictionUserTypeList getUsers() {
		return users;
	}

	public void setUsers(J2735RestrictionUserTypeList users) {
		this.users = users;
	}

}
