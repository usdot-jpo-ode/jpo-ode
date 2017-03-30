package us.dot.its.jpo.ode.plugin.j2735.oss;

import static org.junit.Assert.*;

import java.math.BigDecimal;

import org.junit.Test;

import us.dot.its.jpo.ode.j2735.dsrc.VehicleHeight;
import us.dot.its.jpo.ode.plugin.j2735.oss.OssHeight;

/**
 * -- Summary --
 * JUnit test class for OssHeight
 * 
 * Verifies correct conversion from generic vehicle height to J2735-compliant vehicle height
 * 
 * -- Documentation --
 * Data Element: DE_VehicleHeightUse: The height of the vehicle, measured from the ground to the highest surface, 
 * excluding any antenna(s), and expressed in units of 5 cm. In cases of vehicles with adjustable ride heights, camper
 * shells, and other devices which may cause the overall height to vary, the largest possible height will be used.
 * ASN.1 Representation:
 *    VehicleHeight ::= INTEGER (0..127)
 *       -- the height of the vehicle
 *       -- LSB units of 5 cm, range to 6.35 meters
 */
public class OssHeightTest {

    /**
     * Test that a minimum vehicle height of (0) is correctly converted to (0.00)
     */
    @Test
    public void shouldReturnMinimumHeight() {

        Integer testInput = 0;
        BigDecimal expectedValue = BigDecimal.ZERO.setScale(2);

        VehicleHeight testHeight = new VehicleHeight(testInput);
        BigDecimal actualValue = OssHeight.genericHeight(testHeight);

        assertEquals(expectedValue, actualValue);

    }

    /**
     * Test that a maximum vehicle height of (127) returns (6.35)
     */
    @Test
    public void shouldReturnMaximumHeight() {

        Integer testInput = 127;
        BigDecimal expectedValue = BigDecimal.valueOf(6.35);
        
        VehicleHeight testHeight = new VehicleHeight(testInput);
        BigDecimal actualValue = OssHeight.genericHeight(testHeight);

        assertEquals(expectedValue, actualValue);
    }

    /**
     * Test that a known vehicle height of (65) returns (3.25)
     */
    @Test
    public void shouldReturnKnownHeight() {

        Integer testValue = 65;
        BigDecimal expectedValue = BigDecimal.valueOf(3.25);
        
        VehicleHeight testHeight = new VehicleHeight(testValue);
        BigDecimal actualValue = OssHeight.genericHeight(testHeight);

        assertEquals(expectedValue, actualValue);

    }

    /**
     * Test that an invalid height above (127) throws exception
     *
     */
    @Test
    public void shouldThrowExceptionAboveUpperBound() {

        Integer testValue = 128;
        VehicleHeight testHeight = new VehicleHeight(testValue);

        try {
            OssHeight.genericHeight(testHeight);
            fail("Expected IllegalArgumentException");
        } catch (RuntimeException e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }

    }

    /**
     * Test that an invalid height below (0) throws exception
     */
    @Test
    public void shouldThrowExceptionBelowLowerBound() {

        Integer testValue = -1;
        VehicleHeight testHeight = new VehicleHeight(testValue);

        try {
            OssHeight.genericHeight(testHeight);
            fail("Expected IllegalArgumentException");
        } catch (RuntimeException e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }

    }

}
