package us.dot.its.jpo.ode.plugin.j2735;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class J2735LaneAttributesTest {
	@Mock
	J2735LaneAttributes laneAttributes;

	@Mock
	J2735LaneTypeAttributes laneType;

	@Test
	public void testGettersSetters() {
		laneAttributes.setDirectionalUse(J2735LaneDirection.egressPath);
		laneAttributes.setLaneType(laneType);
		laneAttributes.setShareWith(J2735LaneSharing.busVehicleTraffic);
		
		Mockito.when(laneAttributes.getDirectionalUse()).thenReturn(J2735LaneDirection.egressPath);
		Mockito.when(laneAttributes.getShareWith()).thenReturn(J2735LaneSharing.busVehicleTraffic);
		Mockito.when(laneAttributes.getLaneType()).thenReturn(laneType);
	}
}
