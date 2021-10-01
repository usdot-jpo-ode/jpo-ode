package us.dot.its.jpo.ode.plugin.j2735;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class J2735ConnectionLaneTest {
	@Mock
	J2735ConnectingLane connectionLane;

	@Test
	public void testGettersSetters() {
		connectionLane.setLane(10);
		connectionLane.setManeuver(J2735AllowedManeuvers.caution);

		Mockito.when(connectionLane.getLane()).thenReturn(10);
		Mockito.when(connectionLane.getManeuver()).thenReturn(J2735AllowedManeuvers.caution);
	}
}