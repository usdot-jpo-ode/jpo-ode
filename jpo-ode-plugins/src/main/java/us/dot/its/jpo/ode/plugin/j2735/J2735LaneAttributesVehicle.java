package us.dot.its.jpo.ode.plugin.j2735;

public enum J2735LaneAttributesVehicle {
	isVehicleRevocableLane,
	// this lane may be activated or not based
	// on the current SPAT message contents
	// if not asserted, the lane is ALWAYS present
	isVehicleFlyOverLane,
	// path of lane is not at grade
	hovLaneUseOnly, 
	restrictedToBusUse, 
	restrictedToTaxiUse, 
	restrictedFromPublicUse,
	hasIRbeaconCoverage, 
	permissionOnRequest // e.g. to inform about a lane for e-cars
}
