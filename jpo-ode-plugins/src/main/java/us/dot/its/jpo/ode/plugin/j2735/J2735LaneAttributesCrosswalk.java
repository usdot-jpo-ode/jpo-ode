package us.dot.its.jpo.ode.plugin.j2735;

public enum J2735LaneAttributesCrosswalk {
	 //With bits as defined:
		   //MUTCD provides no suitable "types" to use here
		   crosswalkRevocableLane ,
		                           //this lane may be activated or not based
		                           //on the current SPAT message contents
		                           //if not asserted, the lane is ALWAYS present
		   bicyleUseAllowed       ,
		                           //The path allows bicycle traffic, 
		                           //if not set, this mode is prohibited
		   isXwalkFlyOverLane     ,
		                           //path of lane is not at grade
		   fixedCycleTime         ,
		                           //ped walk phases use preset times
		                           //i.e. there is not a 'push to cross' button
		   biDirectionalCycleTimes ,
		                           //ped walk phases use different SignalGroupID
		                           //for each direction. The first SignalGroupID
		                           //in the first Connection represents 'inbound'
		                           //flow (the direction of travel towards the first 
		                           //node point) while second SignalGroupID in the 
		                           //next Connection entry represents the 'outbound'
		                           //flow. And use of RestrictionClassID entries
		                           //in the Connect follow this same pattern in pairs.
		   hasPushToWalkButton     ,
		                           //Has a demand input
		   audioSupport            ,
		                           //audio crossing cues present
		   rfSignalRequestPresent  ,
		                           //Supports RF push to walk technologies
		   unsignalizedSegmentsPresent  
		                           //The lane path consists of one of more segments
		                           //which are not part of a signal group ID
		   //Bits 9~15 reserved and set to zero
}
