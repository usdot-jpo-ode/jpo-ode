package us.dot.its.jpo.ode.plugin.j2735;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735Connection extends Asn1Object {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private J2735ConnectingLane connectingLane;
	private J2735IntersectionReferenceID remoteIntersection;
	private Integer signalGroup;
	private Integer userClass;
	private Integer connectionID;

	public J2735ConnectingLane getConnectingLane() {
		return connectingLane;
	}

	public void setConnectingLane(J2735ConnectingLane connectingLane) {
		this.connectingLane = connectingLane;
	}

	public J2735IntersectionReferenceID getRemoteIntersection() {
		return remoteIntersection;
	}

	public void setRemoteIntersection(J2735IntersectionReferenceID remoteIntersection) {
		this.remoteIntersection = remoteIntersection;
	}

	public Integer getSignalGroup() {
		return signalGroup;
	}

	public void setSignalGroup(Integer signalGroup) {
		this.signalGroup = signalGroup;
	}

	public Integer getUserClass() {
		return userClass;
	}

	public void setUserClass(Integer userClass) {
		this.userClass = userClass;
	}

	public Integer getConnectionID() {
		return connectionID;
	}

	public void setConnectionID(Integer connectionID) {
		this.connectionID = connectionID;
	}

}
