package us.dot.its.jpo.ode.plugin.j2735;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class J2735ConnectionLaneTest {

	@Test
	public void testGettersSetters() {
		J2735ConnectingLane connectionLane = new J2735ConnectingLane();
		connectionLane.setLane(10);
		assertEquals(connectionLane.getLane(), 10);

		J2735BitString maneuver = new J2735BitString();

		char[] chars = "010000000000".toCharArray();

		for (char i = 0; i < chars.length; i++) {
			if (i >= J2735AllowedManeuvers.values().length) {
				break;
			}
			String bitName = J2735AllowedManeuvers.values()[i].name();
			Boolean bitStatus = (chars[i] == '1');
			maneuver.put(bitName, bitStatus);
		}
		connectionLane.setManeuver(maneuver);
		assertEquals(connectionLane.getManeuver(), maneuver);
	}
}