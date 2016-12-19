package us.dot.its.jpo.ode.plugin.j2735.oss;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;

import org.junit.Ignore;
import org.junit.Test;

import us.dot.its.jpo.ode.j2735.dsrc.Acceleration;
import us.dot.its.jpo.ode.j2735.dsrc.AccelerationSet4Way;
import us.dot.its.jpo.ode.j2735.dsrc.VerticalAcceleration;
import us.dot.its.jpo.ode.j2735.dsrc.YawRate;
import us.dot.its.jpo.ode.plugin.j2735.oss.OssAccelerationSet4Way;

/**
 * ------------- -- Summary --
 * 
 * JUnit test class for OssAccelerationSet4Way
 * 
 * Verifies that OssAccelerationSet4Way properly converts a generic
 * AccelerationSet4Way object to compliant J2735AccelerationSet4Way as per
 * defined in the ASN.1 specification.
 * 
 * -------------------------- -- ASN.1 Documentation --
 * 
 * Data Frame: DF_AccelerationSet4Way
 * 
 * Use: This data frame is a set of acceleration values in 3 orthogonal
 * directions of the vehicle and with yaw rotation rates, expressed as a
 * structure. The positive longitudinal axis is to the front of the vehicle. The
 * positive lateral axis is to the right side of the vehicle (facing forward).
 * Positive yaw is to the right (clockwise). A positive vertical "z" axis is
 * downward with the zero point at the bottom of the vehicle's tires. The frame
 * of reference and axis of rotation used shall be accordance with that defined
 * in Section 11 of this standard.
 * 
 * ASN.1 Representation: AccelerationSet4Way ::= SEQUENCE { long Acceleration,
 * -- Along the Vehicle Longitudinal axis lat Acceleration, -- Along the Vehicle
 * Lateral axis vert VerticalAcceleration, -- Along the Vehicle Vertical axis
 * yaw YawRate }
 * 
 * Data Element: DE_Acceleration
 * 
 * Use: The DE_Acceleration data element represents the signed acceleration of
 * the vehicle along some known axis in units of 0.01 meters per second squared.
 * A range of over 2Gs is supported. The coordinate system is as defined in
 * Section 11.4. Longitudinal acceleration is the acceleration along the X axis
 * or the vehicle's direction of travel which is generally in parallel with a
 * front to rear centerline. Negative values indicate deceleration, and possible
 * braking action. Lateral acceleration is the acceleration along the Y axis or
 * perpendicular to the vehicle's general direction of travel in parallel with a
 * left-to right centerline.
 * 
 * ASN.1 Representation: Acceleration ::= INTEGER (-2000..2001) -- LSB units are
 * 0.01 m/s^2 -- the value 2000 shall be used for values greater than 2000 --
 * the value -2000 shall be used for values less than -2000 -- a value of 2001
 * shall be used for Unavailable
 *
 * Data Element: DE_VerticalAcceleration
 * 
 * Use: A data element representing the signed vertical acceleration of the
 * vehicle along the vertical axis in units of 0.02 G (where 9.80665 meters per
 * second squared is one G, i.e., 0.02 G = 0.1962 meters per second squared).
 * 
 * ASN.1 Representation: VerticalAcceleration ::= INTEGER (-127..127) -- LSB
 * units of 0.02 G steps over -2.52 to +2.54 G -- The value +127 shall be used
 * for ranges >= 2.54 G -- The value -126 shall be used for ranges <= 2.52 G --
 * The value -127 shall be used for unavailable
 * 
 * Data Element: DE_YawRate
 * 
 * Use: The DE_YawRate data element provides the Yaw Rate of the vehicle, a
 * signed value (to the right being positive) expressed in 0.01 degrees per
 * second. The element can used represent a vehicle's rotation about its
 * vertical axis within a certain time period, often at the time a Probe Data
 * snapshot was taken. Another element, the Yaw Rate Confidence Element provides
 * additional information on the coarseness of the Yaw Rate element also in
 * degrees per second.
 * 
 * ASN.1 Representation: YawRate ::= INTEGER (-32767..32767) -- LSB units of
 * 0.01 degrees per second (signed)
 */
public class OssAccelerationSet4WayTest {

    /**
     * Test that longitudinal acceleration values greater than +2001 are
     * returned as +20.00
     * 
     * Lateral acceleration, vertical acceleration, and yaw rate are independent
     * variables for this test and are set to 0.
     */
    @Test
    public void shouldReduceLongAccelToUpperLimit() {

        Integer testInput = 2002;
        BigDecimal expectedValue = BigDecimal.valueOf(20.00);

        Acceleration testLong = new Acceleration(testInput);
        Acceleration testLat = new Acceleration(0);
        VerticalAcceleration testVert = new VerticalAcceleration(0);
        YawRate testYaw = new YawRate(0);

        AccelerationSet4Way testAccelSet = new AccelerationSet4Way(testLong, testLat, testVert, testYaw);

        BigDecimal actualValue = OssAccelerationSet4Way.genericAccelerationSet4Way(testAccelSet).getAccelLong();

        assertEquals(expectedValue, actualValue);

    }

    /**
     * Test that longitudinal acceleration values less than -2000 are returned
     * as -20.00
     * 
     * Lateral acceleration, vertical acceleration, and yaw rate are independent
     * variables for this test and are set to 0.
     */
    @Test
    public void shouldIncreaseLongAccelToLowerLimit() {

        Integer testInput = -2001;
        BigDecimal expectedValue = BigDecimal.valueOf(-20.00);

        Acceleration testLong = new Acceleration(testInput);
        Acceleration testLat = new Acceleration(0);
        VerticalAcceleration testVert = new VerticalAcceleration(0);
        YawRate testYaw = new YawRate(0);

        AccelerationSet4Way testAccelSet = new AccelerationSet4Way(testLong, testLat, testVert, testYaw);

        BigDecimal actualValue = OssAccelerationSet4Way.genericAccelerationSet4Way(testAccelSet).getAccelLong();

        assertEquals(expectedValue, actualValue);

    }

    /**
     * Test that longitudinal acceleration value of 2001 indicates unavailable
     * by returning null
     * 
     * Lateral acceleration, vertical acceleration, and yaw rate are independent
     * variables for this test and are set to 0
     */
    @Test
    public void shouldDefaultLongToNull() {

        Integer testValue = 2001;
        BigDecimal expectedValue = null;

        Acceleration testLong = new Acceleration(testValue);
        Acceleration testLat = new Acceleration(0);
        VerticalAcceleration testVert = new VerticalAcceleration(0);
        YawRate testYaw = new YawRate(0);

        AccelerationSet4Way testAccelSet = new AccelerationSet4Way(testLong, testLat, testVert, testYaw);

        BigDecimal actualValue = OssAccelerationSet4Way.genericAccelerationSet4Way(testAccelSet).getAccelLong();

        assertEquals(expectedValue, actualValue);

    }

    /**
     * Test that a known longitudinal acceleration value (1034) correctly
     * returns as (10.34)
     * 
     * Lateral acceleration, vertical acceleration, and yaw rate are independent
     * variables for this test and are set to 0
     */
    @Test
    public void shouldReturnKnownLongAcceleration() {

        Integer testValue = 1034;
        BigDecimal expectedValue = BigDecimal.valueOf(10.34);

        Acceleration testLong = new Acceleration(testValue);
        Acceleration testLat = new Acceleration(0);
        VerticalAcceleration testVert = new VerticalAcceleration(0);
        YawRate testYaw = new YawRate(0);

        AccelerationSet4Way testAccelSet = new AccelerationSet4Way(testLong, testLat, testVert, testYaw);

        BigDecimal actualValue = OssAccelerationSet4Way.genericAccelerationSet4Way(testAccelSet).getAccelLong();

        assertEquals(expectedValue, actualValue);

    }

    /**
     * Test that lateral acceleration values greater than +2001 are returned as
     * +20.00
     * 
     * Longitudinal acceleration, vertical acceleration, and yaw rate are
     * independent variables for this test and are set to 0
     */
    @Test
    public void shouldReduceToUpperLatLimit() {

        Integer testInput = 2002;
        BigDecimal expectedValue = BigDecimal.valueOf(20.00);

        Acceleration testLong = new Acceleration(0);
        Acceleration testLat = new Acceleration(testInput);
        VerticalAcceleration testVert = new VerticalAcceleration(0);
        YawRate testYaw = new YawRate(0);

        AccelerationSet4Way testAccelSet = new AccelerationSet4Way(testLong, testLat, testVert, testYaw);

        BigDecimal actualValue = OssAccelerationSet4Way.genericAccelerationSet4Way(testAccelSet).getAccelLat();

        assertEquals(expectedValue, actualValue);

    }

    /**
     * Test that lateral acceleration values less than -2000 are returned as
     * -20.00
     * 
     * Longitudinal acceleration, vertical acceleration, and yaw rate are
     * independent variables for this test and are set to 0
     */
    @Test
    public void shouldIncreaseLatAccelToLowerLimit() {

        Integer testInput = -2001;
        BigDecimal expectedValue = BigDecimal.valueOf(-20.00);

        Acceleration testLong = new Acceleration(0);
        Acceleration testLat = new Acceleration(testInput);
        VerticalAcceleration testVert = new VerticalAcceleration(0);
        YawRate testYaw = new YawRate(0);

        AccelerationSet4Way testAccelSet = new AccelerationSet4Way(testLong, testLat, testVert, testYaw);

        BigDecimal actualValue = OssAccelerationSet4Way.genericAccelerationSet4Way(testAccelSet).getAccelLat();

        assertEquals(expectedValue, actualValue);

    }

    /**
     * Test that lateral acceleration value of 2001 indicates unavailable by
     * returning null
     * 
     * Longitudinal acceleration, vertical acceleration, and yaw rate are
     * independent variables for this test and are set to 0;
     */
    @Test
    public void shouldReturnLatAccelUndefined() {

        Integer testInput = 2001;
        BigDecimal expectedValue = null;

        Acceleration testLong = new Acceleration(0);
        Acceleration testLat = new Acceleration(testInput);
        VerticalAcceleration testVert = new VerticalAcceleration(0);
        YawRate testYaw = new YawRate(0);

        AccelerationSet4Way testAccelSet = new AccelerationSet4Way(testLong, testLat, testVert, testYaw);

        BigDecimal actualValue = OssAccelerationSet4Way.genericAccelerationSet4Way(testAccelSet).getAccelLat();

        assertEquals(expectedValue, actualValue);

    }

    /**
     * Test that a known lateral acceleration value (-567) correctly returns as
     * (-5.67)
     * 
     * Longitudinal acceleration, vertical acceleration, and yaw rate are
     * independent variables for this test and are set to 0.
     */
    @Test
    public void shouldReturnKnownLatAccel() {

        Integer testInput = -567;
        BigDecimal expectedValue = BigDecimal.valueOf(-5.67);

        Acceleration testLong = new Acceleration(0);
        Acceleration testLat = new Acceleration(testInput);
        VerticalAcceleration testVert = new VerticalAcceleration(0);
        YawRate testYaw = new YawRate(0);

        AccelerationSet4Way testAccelSet = new AccelerationSet4Way(testLong, testLat, testVert, testYaw);

        BigDecimal actualValue = OssAccelerationSet4Way.genericAccelerationSet4Way(testAccelSet).getAccelLat();

        assertEquals(expectedValue, actualValue);

    }

    /**
     * Test that vertical acceleration rates greater than +127 are returned as
     * +2.54
     * 
     * Longitudinal acceleration, lateral acceleration, and yaw rate are
     * independent variables for this test and are set to 0
     */
    @Test
    public void shouldReduceVertAccelToUpperLimit() {

        Integer testInput = 128;
        BigDecimal expectedValue = BigDecimal.valueOf(2.54);

        Acceleration testLong = new Acceleration(0);
        Acceleration testLat = new Acceleration(0);
        VerticalAcceleration testVert = new VerticalAcceleration(testInput);
        YawRate testYaw = new YawRate(0);

        AccelerationSet4Way testAccelSet = new AccelerationSet4Way(testLong, testLat, testVert, testYaw);

        BigDecimal actualValue = OssAccelerationSet4Way.genericAccelerationSet4Way(testAccelSet).getAccelVert();

        assertEquals(expectedValue, actualValue);

    }

    /**
     * Test that vertical acceleration rates less than -127 are returned as
     * -2.52
     * 
     * Longitudinal acceleration, lateral acceleration, and yaw rate are
     * independent variables for this test and are set to 0
     */
    @Test
    public void shouldIncreaseVertAccelToLowerLimit() {

        Integer testInput = -128;
        BigDecimal expectedValue = BigDecimal.valueOf(-2.52);

        Acceleration testLong = new Acceleration(0);
        Acceleration testLat = new Acceleration(0);
        VerticalAcceleration testVert = new VerticalAcceleration(testInput);
        YawRate testYaw = new YawRate(0);

        AccelerationSet4Way testAccelSet = new AccelerationSet4Way(testLong, testLat, testVert, testYaw);

        BigDecimal actualValue = OssAccelerationSet4Way.genericAccelerationSet4Way(testAccelSet).getAccelVert();

        assertEquals(expectedValue, actualValue);

    }

    /**
     * Test that vertical acceleration value of -127 indicates unavailable by
     * returning null
     * 
     * Longitudinal acceleration, lateral acceleration, and yaw rate are
     * independent variables for this test and are set to 0.
     */
    @Test
    public void shouldReturnVertAccelUndefined() {

        Integer testInput = -127;
        BigDecimal expectedValue = null;

        Acceleration testLong = new Acceleration(0);
        Acceleration testLat = new Acceleration(0);
        VerticalAcceleration testVert = new VerticalAcceleration(testInput);
        YawRate testYaw = new YawRate(0);

        AccelerationSet4Way testAccelSet = new AccelerationSet4Way(testLong, testLat, testVert, testYaw);

        BigDecimal actualValue = OssAccelerationSet4Way.genericAccelerationSet4Way(testAccelSet).getAccelVert();

        assertEquals(expectedValue, actualValue);

    }

    /**
     * Test that a known vertical acceleration value (62) correctly returns as
     * (1.24)
     * 
     * Longitudinal acceleration, lateral acceleration, and yaw rate are
     * independent variables for this test and are set to 0
     */
    @Test
    public void shouldReturnKnownVertAccel() {

        Integer testInput = 62;
        BigDecimal expectedValue = BigDecimal.valueOf(1.24);

        Acceleration testLong = new Acceleration(0);
        Acceleration testLat = new Acceleration(0);
        VerticalAcceleration testVert = new VerticalAcceleration(testInput);
        YawRate testYaw = new YawRate(0);

        AccelerationSet4Way testAccelSet = new AccelerationSet4Way(testLong, testLat, testVert, testYaw);

        BigDecimal actualValue = OssAccelerationSet4Way.genericAccelerationSet4Way(testAccelSet).getAccelVert();

        assertEquals(expectedValue, actualValue);

    }

    /**
     * Test that the minimum yaw rate (-32767) returns (-327.67)
     * 
     * Longitudinal acceleration, lateral acceleration, and vertical
     * acceleration are independent variables for this test and are set to 0.
     */
    @Test
    public void shouldReturnMinimumYawRate() {

        Integer testInput = -32767;
        BigDecimal expectedValue = BigDecimal.valueOf(-327.67);

        Acceleration testLong = new Acceleration(0);
        Acceleration testLat = new Acceleration(0);
        VerticalAcceleration testVert = new VerticalAcceleration(0);
        YawRate testYaw = new YawRate(testInput);

        AccelerationSet4Way testAccelSet = new AccelerationSet4Way(testLong, testLat, testVert, testYaw);

        BigDecimal actualValue = OssAccelerationSet4Way.genericAccelerationSet4Way(testAccelSet).getAccelYaw();

        assertEquals(expectedValue, actualValue);

    }

    /**
     * Test that the maximum yaw rate (32767) correctly (327.67)
     * 
     * Longitudinal acceleration, lateral acceleration, and vertical
     * acceleration are independent variables for this test and are set to 0
     */
    @Test
    public void shouldReturnMaximumYawRate() {

        Integer testInput = 32767;
        BigDecimal expectedValue = BigDecimal.valueOf(327.67);

        Acceleration testLong = new Acceleration(0);
        Acceleration testLat = new Acceleration(0);
        VerticalAcceleration testVert = new VerticalAcceleration(0);
        YawRate testYaw = new YawRate(testInput);

        AccelerationSet4Way testAccelSet = new AccelerationSet4Way(testLong, testLat, testVert, testYaw);

        BigDecimal actualValue = OssAccelerationSet4Way.genericAccelerationSet4Way(testAccelSet).getAccelYaw();

        assertEquals(expectedValue, actualValue);

    }

    /**
     * Test that a known yaw rate value of (-10953) returns (-109.53)
     * 
     * Longitudinal acceleration, lateral acceleration, and vertical
     * acceleration are independent variables for this test and are set to 0
     */
    @Test
    public void shouldReturnKnownYawRate() {

        Integer testInput = -10953;
        BigDecimal expectedValue = BigDecimal.valueOf(-109.53);

        Acceleration testLong = new Acceleration(0);
        Acceleration testLat = new Acceleration(0);
        VerticalAcceleration testVert = new VerticalAcceleration(0);
        YawRate testYaw = new YawRate(testInput);

        AccelerationSet4Way testAccelSet = new AccelerationSet4Way(testLong, testLat, testVert, testYaw);

        BigDecimal actualValue = OssAccelerationSet4Way.genericAccelerationSet4Way(testAccelSet).getAccelYaw();

        assertEquals(expectedValue, actualValue);

    }

    /**
     * Note - @Ignore tag added as this test may be unnecessary
     * 
     * Test that an input value above the upper yaw rate bound (32767) throws an
     * IllegalArgumentException
     * 
     * Longitudinal acceleration, lateral acceleration, and vertical
     * acceleration are independent variables for this test and are set to 0
     */
    @Test
    public void shouldThrowExceptionYawRateAboveUpperBound() {

        Integer testInput = 32768;

        Acceleration testLong = new Acceleration(0);
        Acceleration testLat = new Acceleration(0);
        VerticalAcceleration testVert = new VerticalAcceleration(0);
        YawRate testYaw = new YawRate(testInput);

        AccelerationSet4Way testAccelSet = new AccelerationSet4Way(testLong, testLat, testVert, testYaw);

        try {
            BigDecimal actualValue = OssAccelerationSet4Way.genericAccelerationSet4Way(testAccelSet).getAccelYaw();
            fail("Expected IllegalArgumentException");
        } catch (Exception e) {
            assertTrue(e.getClass().equals(IllegalArgumentException.class));
        }

    }

    /**
     * Note - @Ignore tag added as this test may be unnecessary
     * 
     * Test that an input value below the lower yaw rate bound (-32767) throws
     * an IllegalArgumentException
     * 
     * Longitudinal acceleration, lateral acceleration, and vertical
     * acceleration are independent variables for this test and are set to 0
     */
    @Test
    public void shouldThrowExceptionYawRateBelowLowerBound() {

        Integer testInput = -32768;

        Acceleration testLong = new Acceleration(0);
        Acceleration testLat = new Acceleration(0);
        VerticalAcceleration testVert = new VerticalAcceleration(0);
        YawRate testYaw = new YawRate(testInput);

        AccelerationSet4Way testAccelSet = new AccelerationSet4Way(testLong, testLat, testVert, testYaw);

        try {
            BigDecimal actualValue = OssAccelerationSet4Way.genericAccelerationSet4Way(testAccelSet).getAccelYaw();
            fail("Expected IllegalArgumentException");
        } catch (Exception e) {
            assertTrue(e.getClass().equals(IllegalArgumentException.class));
        }

    }

}
