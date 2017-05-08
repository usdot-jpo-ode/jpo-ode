package us.dot.its.jpo.ode.common.asn1;

import java.util.HashMap;

public class SignalLightStateDecoder {
	
	public static final String GREEN = "Green";
	public static final String RED = "Red";
	public static final String YELLOW = "Yellow";
	
	private static final char GREEN_CODE = '1';
	private static final char YELLOW_CODE = '2';
	private static final char RED_CODE = '4';
	private static final char FLASHING_GREEN_CODE = '9';
	private static final char FLASHING_YELLOW_CODE = 'A';
	private static final char FLASHING_RED_CODE = 'C';
	
	// indexes from right to left, zero based
	private static final int BALL_INDEX = 0;
	private static final int LEFT_ARROW_INDEX = 1;
	private static final int RIGHT_ARROW_INDEX = 2;
	private static final int STRAIGHT_ARROW_INDEX = 3;
	private static final int SOFT_LEFT_ARROW_INDEX = 4;
	private static final int SOFT_RIGHT_ARROW_INDEX = 5;
	private static final int U_TURN_ARROW_INDEX = 6;
	
	private static HashMap<Character,String> colorLookup = new HashMap<Character,String>();
	private static HashMap<Integer,String> arrowLookup = new HashMap<Integer,String>();
	
	static {
		colorLookup.put(GREEN_CODE, "green");
		colorLookup.put(YELLOW_CODE, "yellow");
		colorLookup.put(RED_CODE, "red");
		colorLookup.put(FLASHING_GREEN_CODE, "flashing green");
		colorLookup.put(FLASHING_YELLOW_CODE, "flashing yellow");
		colorLookup.put(FLASHING_RED_CODE, "flashing red");
		
		arrowLookup.put(BALL_INDEX, "ball");
		arrowLookup.put(LEFT_ARROW_INDEX, "left arrow");
		arrowLookup.put(RIGHT_ARROW_INDEX, "right arrow");
		arrowLookup.put(STRAIGHT_ARROW_INDEX, "straight arrow");
		arrowLookup.put(SOFT_LEFT_ARROW_INDEX, "soft left arrow");
		arrowLookup.put(SOFT_RIGHT_ARROW_INDEX, "soft right arrow");
		arrowLookup.put(U_TURN_ARROW_INDEX, "u-turn arrow");
	}

	public static String decodeSignalLightState(int state) {
		StringBuilder sb = new StringBuilder();
		String hexString = Integer.toHexString(state);
		int index = 0;
		for (int i = (hexString.length()-1); i > -1; i--) {
			char code = Character.toUpperCase(hexString.charAt(i));
			if (code != '0') {
				sb.append(colorLookup.get(code)).append(" ").append(arrowLookup.get(index)).append(", ");
			}
			index++;
		}
		System.out.println(sb.toString());
		return sb.toString();
	}
}
