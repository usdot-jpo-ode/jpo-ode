package us.dot.its.jpo.ode.plugin.j2735;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class J2735IntersectionGeometryTest {

	@Test
	public void testGettersSetters() {
		J2735IntersectionGeometry intersectionGeometry = new J2735IntersectionGeometry();
		J2735IntersectionReferenceID id = new J2735IntersectionReferenceID();
		OdePosition3D refPoint = new OdePosition3D();
		J2735SpeedLimitList speedLimits = new J2735SpeedLimitList();
		J2735LaneList laneSet = new J2735LaneList();
		intersectionGeometry.setId(id);
		intersectionGeometry.setLaneSet(laneSet);
		intersectionGeometry.setRefPoint(refPoint);
		intersectionGeometry.setSpeedLimits(speedLimits);
		
		assertEquals(intersectionGeometry.getId(),id);
		assertEquals(intersectionGeometry.getLaneSet(),laneSet);
		assertEquals(intersectionGeometry.getSpeedLimits(),speedLimits);
		assertEquals(intersectionGeometry.getRefPoint(),refPoint);
	}
}

