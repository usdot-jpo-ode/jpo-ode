package us.dot.its.jpo.ode.plugin.j2735;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class J2735LaneAttributesTest {

	@Test
	public void testDirectionalUseGetterSetter() {
		J2735LaneAttributes laneAttributes = new J2735LaneAttributes();
		J2735BitString directionalUse = new J2735BitString();

		char[] chars = "01".toCharArray();

		for (char i = 0; i < chars.length; i++) {
			if (i >= J2735LaneDirection.values().length) {
				break;
			}
			String bitName = J2735LaneDirection.values()[i].name();
			Boolean bitStatus = (chars[i] == '1');
			directionalUse.put(bitName, bitStatus);
		}
		laneAttributes.setDirectionalUse(directionalUse);

		assertEquals(laneAttributes.getDirectionalUse(), directionalUse);
	}

	@Test
	public void testShareWithGetterSetter() {
		J2735LaneAttributes laneAttributes = new J2735LaneAttributes();
		J2735BitString shareWith = new J2735BitString();

		char[] chars = "1000000000".toCharArray();

		for (char i = 0; i < chars.length; i++) {
			if (i >= J2735LaneSharing.values().length) {
				break;
			}
			String bitName = J2735LaneSharing.values()[i].name();
			Boolean bitStatus = (chars[i] == '1');
			shareWith.put(bitName, bitStatus);
		}
		laneAttributes.setShareWith(shareWith);

		assertEquals(laneAttributes.getShareWith(), shareWith);
	}

	@Test
	public void testLaneTypeGetterSetter() {
		J2735LaneAttributes laneAttributes = new J2735LaneAttributes();
		J2735LaneTypeAttributes laneTypeAttributes = new J2735LaneTypeAttributes();
		J2735BitString vehicle = new J2735BitString();

		char[] chars = "00000100".toCharArray();

		for (char i = 0; i < chars.length; i++) {
			if (i >= J2735LaneAttributesVehicle.values().length) {
				break;
			}
			String bitName = J2735LaneAttributesVehicle.values()[i].name();
			Boolean bitStatus = (chars[i] == '1');
			vehicle.put(bitName, bitStatus);
		}
		laneTypeAttributes.setVehicle(vehicle);
		laneAttributes.setLaneType(laneTypeAttributes);

		assertEquals(laneAttributes.getLaneType().getVehicle(), vehicle);
	}
}
