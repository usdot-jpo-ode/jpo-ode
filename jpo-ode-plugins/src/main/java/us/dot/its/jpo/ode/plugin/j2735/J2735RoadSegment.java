package us.dot.its.jpo.ode.plugin.j2735;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735RoadSegment extends Asn1Object {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String name;
	private J2735RoadSegmentReferenceID id;
	private Integer revision;
	private J2735Position3D refPoint;
	private J2735LaneWidth laneWidth;
	private J2735SpeedLimitList speedLimits;
	private J2735RoadLaneSetList roadLaneSet;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public J2735RoadSegmentReferenceID getId() {
		return id;
	}

	public void setId(J2735RoadSegmentReferenceID id) {
		this.id = id;
	}

	public Integer getRevision() {
		return revision;
	}

	public void setRevision(Integer revision) {
		this.revision = revision;
	}

	public J2735Position3D getRefPoint() {
		return refPoint;
	}

	public void setRefPoint(J2735Position3D refPoint) {
		this.refPoint = refPoint;
	}

	public J2735LaneWidth getLaneWidth() {
		return laneWidth;
	}

	public void setLaneWidth(J2735LaneWidth laneWidth) {
		this.laneWidth = laneWidth;
	}

	public J2735SpeedLimitList getSpeedLimits() {
		return speedLimits;
	}

	public void setSpeedLimits(J2735SpeedLimitList speedLimits) {
		this.speedLimits = speedLimits;
	}

	public J2735RoadLaneSetList getRoadLaneSet() {
		return roadLaneSet;
	}

	public void setRoadLaneSet(J2735RoadLaneSetList roadLaneSet) {
		this.roadLaneSet = roadLaneSet;
	}

}
