package us.dot.its.jpo.ode.plugins.oss.j2735;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.junit.Test;

import us.dot.its.jpo.ode.j2735.dsrc.Angle;

/**
 * To be added
 *
 */
public class OssAngleTest {
	
	/**
	 * Tests that an input angle of (0) returns an output angle of (0)
	 * @param
	 */
	@Test
	public void shouldReturnZeroAngle() {
		
		Angle zeroAngle = new Angle(0);
		
		BigDecimal actualValue = OssAngle.genericAngle(zeroAngle);
		
		BigDecimal expectedValue = new BigDecimal(0);
		
		assertEquals(actualValue.longValue(), expectedValue.longValue());
		
	}
	/**
	 * Tests that the max input angle (28799) is equal to the max decimal value (359.9875)
	 * @param
	 */
	
	@Test
	public void shouldReturnMaxAngle() {
		
		BigDecimal expectedValue = new BigDecimal(359.9875)
				.setScale(4, RoundingMode.DOWN);
		
		Angle maxAngle = new Angle(28799);
		
		BigDecimal actualValue = OssAngle.genericAngle(maxAngle)
				.setScale(4, RoundingMode.DOWN);
		
		assertEquals(actualValue, expectedValue);
	}
	
	/**
	 * Tests that a random angle from (0..28800) is accurately converted to (0..360)
	 * @param
	 */
	@Test
	public void shouldReturnRandomAngle() {
		
		Integer randomNumber = (int)(Math.random() * 28800);
		
		Double expectedNumber = (long)(randomNumber) * 0.0125;
		
		BigDecimal expectedValue = new BigDecimal(expectedNumber)
				.setScale(4, RoundingMode.DOWN);
		
		Angle actualAngle = new Angle(randomNumber);
		
		BigDecimal actualValue = OssAngle.genericAngle(actualAngle)
				.setScale(4, RoundingMode.DOWN);
		
		assertEquals(actualValue, expectedValue);
	}

}
