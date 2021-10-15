package us.dot.its.jpo.ode.plugin.j2735;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class J2735ConnectionLaneTest {

	@Test
	public void testGettersSetters() {
		J2735ConnectingLane connectionLane = new J2735ConnectingLane();
		connectionLane.setLane(10);
		//connectionLane.setManeuver(J2735AllowedManeuvers.caution);
		
		assertEquals(connectionLane.getLane(), 10);
		//assertEquals(connectionLane.getManeuver(), J2735AllowedManeuvers.caution);
	}
}