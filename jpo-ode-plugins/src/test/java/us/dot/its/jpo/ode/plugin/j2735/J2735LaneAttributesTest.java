package us.dot.its.jpo.ode.plugin.j2735;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class J2735LaneAttributesTest {

	@Test
	public void testGettersSetters() {
		J2735LaneAttributes laneAttributes = new J2735LaneAttributes();
		J2735LaneTypeAttributes laneType= new J2735LaneTypeAttributes();
		laneAttributes.setDirectionalUse(J2735LaneDirection.egressPath);
		laneAttributes.setLaneType(laneType);
		laneAttributes.setShareWith(J2735LaneSharing.busVehicleTraffic);
		
		assertEquals(laneAttributes.getDirectionalUse(),J2735LaneDirection.egressPath);
		assertEquals(laneAttributes.getShareWith(),J2735LaneSharing.busVehicleTraffic);
		assertEquals(laneAttributes.getLaneType(),laneType);
	}
}
