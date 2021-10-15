package us.dot.its.jpo.ode.plugin.j2735;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735LaneTypeAttributes  extends Asn1Object {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private   J2735BitString vehicle;        // motor vehicle lanes - J2735LaneAttributesVehicle
	private   J2735BitString crosswalk;   // pedestrian crosswalks - J2735LaneAttributesCrosswalk
	private   J2735BitString  bikeLane;        // bike lanes - J2735LaneAttributesBike
	private   J2735BitString sidewalk;    // pedestrian sidewalk paths - J2735LaneAttributesSidewalk
	private   J2735BitString median;     // medians & channelization - J2735LaneAttributesBarrier
	private   J2735BitString  striping;    // roadway markings - J2735LaneAttributesStriping
	private   J2735BitString trackedVehicle; // trains and trolleys - J2735LaneAttributesTrackedVehicle
	private   J2735BitString parking;        // parking and stopping lanes - J2735LaneAttributesParking

	public J2735BitString getVehicle() {
		return vehicle;
	}
	public void setVehicle(J2735BitString vehicle) {
		this.vehicle = vehicle;
	}
	public J2735BitString getCrosswalk() {
		return crosswalk;
	}
	public void setCrosswalk(J2735BitString crosswalk) {
		this.crosswalk = crosswalk;
	}
	public J2735BitString getBikeLane() {
		return bikeLane;
	}
	public void setBikeLane(J2735BitString bikeLane) {
		this.bikeLane = bikeLane;
	}
	public J2735BitString getSidewalk() {
		return sidewalk;
	}
	public void setSidewalk(J2735BitString sidewalk) {
		this.sidewalk = sidewalk;
	}
	public J2735BitString getMedian() {
		return median;
	}
	public void setMedian(J2735BitString median) {
		this.median = median;
	}
	public J2735BitString getStriping() {
		return striping;
	}
	public void setStriping(J2735BitString striping) {
		this.striping = striping;
	}
	public J2735BitString getTrackedVehicle() {
		return trackedVehicle;
	}
	public void setTrackedVehicle(J2735BitString trackedVehicle) {
		this.trackedVehicle = trackedVehicle;
	}
	public J2735BitString getParking() {
		return parking;
	}
	public void setParking(J2735BitString parking) {
		this.parking = parking;
	}
	
	
}
