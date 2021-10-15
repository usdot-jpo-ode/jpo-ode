package us.dot.its.jpo.ode.plugin.j2735;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735GenericLane extends Asn1Object {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer laneID;
	private String name;
	private Integer ingressApproach;
	private Integer egressApproach;
	private J2735LaneAttributes laneAttributes;
	private J2735BitString maneuvers;
	private J2735NodeListXY nodeList;
	private J2735ConnectsToList connectsTo;
	private J2735OverlayLaneList overlays;

	public Integer getLaneID() {
		return laneID;
	}

	public void setLaneID(Integer laneID) {
		this.laneID = laneID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getIngressApproach() {
		return ingressApproach;
	}

	public void setIngressApproach(Integer ingressApproach) {
		this.ingressApproach = ingressApproach;
	}

	public Integer getEgressApproach() {
		return egressApproach;
	}

	public void setEgressApproach(Integer egressApproach) {
		this.egressApproach = egressApproach;
	}

	public J2735LaneAttributes getLaneAttributes() {
		return laneAttributes;
	}

	public void setLaneAttributes(J2735LaneAttributes laneAttributes) {
		this.laneAttributes = laneAttributes;
	}

	public J2735BitString getManeuvers() {
		return maneuvers;
	}

	public void setManeuvers(J2735BitString maneuvers) {
		this.maneuvers = maneuvers;
	}

	public J2735NodeListXY getNodeList() {
		return nodeList;
	}

	public void setNodeList(J2735NodeListXY nodeList) {
		this.nodeList = nodeList;
	}

	public J2735ConnectsToList getConnectsTo() {
		return connectsTo;
	}

	public void setConnectsTo(J2735ConnectsToList connectsTo) {
		this.connectsTo = connectsTo;
	}

	public J2735OverlayLaneList getOverlays() {
		return overlays;
	}

	public void setOverlays(J2735OverlayLaneList overlays) {
		this.overlays = overlays;
	}

}
