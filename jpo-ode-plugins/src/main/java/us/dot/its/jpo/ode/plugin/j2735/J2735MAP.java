package us.dot.its.jpo.ode.plugin.j2735;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735MAP extends Asn1Object {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Integer timeStamp;
	private Integer msgIssueRevision;
	private J2735LayerType layerType;
	private Integer layerID;
	private J2735IntersectionGeometryList intersections;
	private J2735RoadSegmentList roadSegments;
	private J2735DataParameters dataParameters;
	private J2735RestrictionClassList restrictionList;

	public Integer getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(Integer timeStamp) {
		this.timeStamp = timeStamp;
	}

	public Integer getMsgIssueRevision() {
		return msgIssueRevision;
	}

	public void setMsgIssueRevision(Integer msgIssueRevision) {
		this.msgIssueRevision = msgIssueRevision;
	}

	public J2735LayerType getLayerType() {
		return layerType;
	}

	public void setLayerType(J2735LayerType layerType) {
		this.layerType = layerType;
	}

	public Integer getLayerID() {
		return layerID;
	}

	public void setLayerID(Integer layerID) {
		this.layerID = layerID;
	}

	public J2735IntersectionGeometryList getIntersections() {
		return intersections;
	}

	public void setIntersections(J2735IntersectionGeometryList intersections) {
		this.intersections = intersections;
	}

	public J2735RoadSegmentList getRoadSegments() {
		return roadSegments;
	}

	public void setRoadSegments(J2735RoadSegmentList roadSegments) {
		this.roadSegments = roadSegments;
	}

	public J2735DataParameters getDataParameters() {
		return dataParameters;
	}

	public void setDataParameters(J2735DataParameters dataParameters) {
		this.dataParameters = dataParameters;
	}

	public J2735RestrictionClassList getRestrictionList() {
		return restrictionList;
	}

	public void setRestrictionList(J2735RestrictionClassList restrictionList) {
		this.restrictionList = restrictionList;
	}

}
