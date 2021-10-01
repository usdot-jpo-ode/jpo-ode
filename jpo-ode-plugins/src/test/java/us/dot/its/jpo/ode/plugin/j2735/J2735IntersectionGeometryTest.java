package us.dot.its.jpo.ode.plugin.j2735;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class J2735IntersectionGeometryTest {
	@Mock
	J2735IntersectionGeometry intersectionGeometry;
	
	@Mock
	J2735IntersectionReferenceID id;
	
	@Mock
	J2735Position3D refPoint;
	
	@Mock
	J2735SpeedLimitList speedLimits;
	
	@Mock
	J2735LaneList laneSet;

	@Test
	public void testGettersSetters() {
		intersectionGeometry.setId(id);
		intersectionGeometry.setLaneSet(laneSet);
		intersectionGeometry.setRefPoint(refPoint);
		intersectionGeometry.setSpeedLimits(speedLimits);
		
		Mockito.when(intersectionGeometry.getId()).thenReturn(id);
		Mockito.when(intersectionGeometry.getLaneSet()).thenReturn(laneSet);
		Mockito.when(intersectionGeometry.getSpeedLimits()).thenReturn(speedLimits);
		Mockito.when(intersectionGeometry.getRefPoint()).thenReturn(refPoint);
	}
}

