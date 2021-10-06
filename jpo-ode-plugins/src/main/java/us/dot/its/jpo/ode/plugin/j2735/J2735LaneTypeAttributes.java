package us.dot.its.jpo.ode.plugin.j2735;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735LaneTypeAttributes  extends Asn1Object {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private   J2735LaneAttributesVehicle vehicle;        // motor vehicle lanes
	private   J2735LaneAttributesCrosswalk crosswalk;   // pedestrian crosswalks
	private   J2735LaneAttributesBike  bikeLane;        // bike lanes
	private   J2735LaneAttributesSidewalk sidewalk;    // pedestrian sidewalk paths
	private   J2735LaneAttributesBarrier median;     // medians & channelization
	private   J2735LaneAttributesStriping  striping;    // roadway markings
	private   J2735LaneAttributesTrackedVehicle trackedVehicle; // trains and trolleys
	private   J2735LaneAttributesParking parking;        // parking and stopping lanes
	public J2735LaneAttributesVehicle getVehicle() {
		return vehicle;
	}
	public void setVehicle(J2735LaneAttributesVehicle vehicle) {
		this.vehicle = vehicle;
	}
	public J2735LaneAttributesCrosswalk getCrosswalk() {
		return crosswalk;
	}
	public void setCrosswalk(J2735LaneAttributesCrosswalk crosswalk) {
		this.crosswalk = crosswalk;
	}
	public J2735LaneAttributesBike getBikeLane() {
		return bikeLane;
	}
	public void setBikeLane(J2735LaneAttributesBike bikeLane) {
		this.bikeLane = bikeLane;
	}
	public J2735LaneAttributesSidewalk getSidewalk() {
		return sidewalk;
	}
	public void setSidewalk(J2735LaneAttributesSidewalk sidewalk) {
		this.sidewalk = sidewalk;
	}
	public J2735LaneAttributesBarrier getMedian() {
		return median;
	}
	public void setMedian(J2735LaneAttributesBarrier median) {
		this.median = median;
	}
	public J2735LaneAttributesStriping getStriping() {
		return striping;
	}
	public void setStriping(J2735LaneAttributesStriping striping) {
		this.striping = striping;
	}
	public J2735LaneAttributesTrackedVehicle getTrackedVehicle() {
		return trackedVehicle;
	}
	public void setTrackedVehicle(J2735LaneAttributesTrackedVehicle trackedVehicle) {
		this.trackedVehicle = trackedVehicle;
	}
	public J2735LaneAttributesParking getParking() {
		return parking;
	}
	public void setParking(J2735LaneAttributesParking parking) {
		this.parking = parking;
	}
	
	
}
