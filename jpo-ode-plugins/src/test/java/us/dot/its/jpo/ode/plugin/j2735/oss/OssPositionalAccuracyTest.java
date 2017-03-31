package us.dot.its.jpo.ode.plugin.j2735.oss;

import static org.junit.Assert.*;

import java.math.BigDecimal;

import org.junit.Test;

import us.dot.its.jpo.ode.j2735.dsrc.PositionalAccuracy;
import us.dot.its.jpo.ode.j2735.dsrc.SemiMajorAxisAccuracy;
import us.dot.its.jpo.ode.j2735.dsrc.SemiMajorAxisOrientation;
import us.dot.its.jpo.ode.j2735.dsrc.SemiMinorAxisAccuracy;
import us.dot.its.jpo.ode.plugin.j2735.J2735PositionalAccuracy;

/**
 * -- Summary --
 * JUnit test class for OssPositionalAccuracy
 * 
 * Verifies correct conversion from generic PositionalAccuracy to compliant-J2735PositionalAccuracy
 * 
 * Since these tests check the creation of composite classes, independent variables are set to 0
 * 
 * -- Documentation --
 * Data Frame: DF_PositionalAccuracy
 * Use: The DF_PositionalAccuracy data frame consists of various parameters of quality used to model the accuracy of 
 * the positional determination with respect to each given axis.
 * ASN.1 Representation:
 *    PositionalAccuracy ::= SEQUENCE {
 *       -- NMEA-183 values expressed in strict ASN form
 *       semiMajor SemiMajorAxisAccuracy,
 *       semiMinor SemiMinorAxisAccuracy,
 *       orientation SemiMajorAxisOrientation
 *       }
 *
 * Data Element: DE_SemiMajorAxisAccuracy
 * Use: The DE_SemiMajorAxisAccuracy data element is used to express the radius (length) of the semi-major axis of an
 * ellipsoid representing the accuracy which can be expected from a GNSS system in 5cm steps, typically at a one sigma
 * level of confidence.
 * ASN.1 Representation:
 *    SemiMajorAxisAccuracy ::= INTEGER (0..255)
 *       -- semi-major axis accuracy at one standard dev
 *       -- range 0-12.7 meter, LSB = .05m
 *       -- 254 = any value equal or greater than 12.70 meter
 *       -- 255 = unavailable semi-major axis value
 *    
 * Data Element: DE_SemiMinorAxisAccuracy
 * Use: The DE_SemiMinorAxisAccuracy data element is used to express the radius of the semi-minor axis of an ellipsoid
 * representing the accuracy which can be expected from a GNSS system in 5cm steps, typically at a one sigma level of
 * confidence.
 * ASN.1 Representation:
 *    SemiMinorAxisAccuracy ::= INTEGER (0..255)
 *       -- semi-minor axis accuracy at one standard dev
 *       -- range 0-12.7 meter, LSB = .05m
 *       -- 254 = any value equal or greater than 12.70 meter
 *       -- 255 = unavailable semi-minor axis value
 *     
 * Data Element: DE_SemiMajorAxisOrientation
 * Use: The DE_ SemiMajorAxisOrientation data element is used to orientate the angle of the semi-major axis of an
 * ellipsoid representing the accuracy which can be expected from a GNSS system with respect to the coordinate system.
 * ASN.1 Representation:
 *    SemiMajorAxisOrientation ::= INTEGER (0..65535)
 *       -- orientation of semi-major axis
 *       -- relative to true north (0~359.9945078786 degrees)
 *       -- LSB units of 360/65535 deg = 0.0054932479
 *       -- a value of 0 shall be 0 degrees
 *       -- a value of 1 shall be 0.0054932479 degrees
 *       -- a value of 65534 shall be 359.9945078786 deg
 *       -- a value of 65535 shall be used for orientation unavailable
 */
public class OssPositionalAccuracyTest {
    
    // SemiMajorAxisAccuracy tests
    
    /**
     * Test that the minimum semi major axis accuracy value (0) returns (0.00)
     * 
     * All other elements are independent variables for this test and are set to 0
     */
    @Test
    public void shouldReturnMinimumSemiMajorAxisAccuracy() {
        
        Integer testInput = 0;
        BigDecimal expectedValue = BigDecimal.ZERO.setScale(2);
        
        SemiMajorAxisAccuracy testMajorAccuracy = new SemiMajorAxisAccuracy(testInput);
        SemiMinorAxisAccuracy testMinorAccuracy = new SemiMinorAxisAccuracy(0);
        SemiMajorAxisOrientation testMajorOrientation = new SemiMajorAxisOrientation(0);
        
        PositionalAccuracy testPositionalAccuracy = new PositionalAccuracy(
                testMajorAccuracy,
                testMinorAccuracy,
                testMajorOrientation);
        
        BigDecimal actualValue = OssPositionalAccuracy
                .genericPositionalAccuracy(testPositionalAccuracy)
                .getSemiMajor();
        
        assertEquals(expectedValue, actualValue);
        
    }
    
    /**
     * Test that the maximum semi major axis accuracy value (254) returns (12.70)
     * 
     * All other elements are independent variables for this test and are set to 0
     */
    @Test
    public void shouldReturnMaximumSemiMajorAxisAccuracy() {
        
        Integer testInput = 254;
        BigDecimal expectedValue = BigDecimal.valueOf(12.70).setScale(2);
        
        SemiMajorAxisAccuracy testMajorAccuracy = new SemiMajorAxisAccuracy(testInput);
        SemiMinorAxisAccuracy testMinorAccuracy = new SemiMinorAxisAccuracy(0);
        SemiMajorAxisOrientation testMajorOrientation = new SemiMajorAxisOrientation(0);
        
        PositionalAccuracy testPositionalAccuracy = new PositionalAccuracy(
                testMajorAccuracy,
                testMinorAccuracy,
                testMajorOrientation);
        
        BigDecimal actualValue = OssPositionalAccuracy
                .genericPositionalAccuracy(testPositionalAccuracy)
                .getSemiMajor();
        
        assertEquals(expectedValue, actualValue);
        
    }
    
    /**
     * Test that the undefined semi major axis accuracy flag value (255) returns (undefined)
     */
    @Test
    public void shouldReturnUndefinedSemiMajorAxisAccuracy() {
        
        Integer testInput = 255;
        BigDecimal expectedValue = null;
        
        SemiMajorAxisAccuracy testMajorAccuracy = new SemiMajorAxisAccuracy(testInput);
        SemiMinorAxisAccuracy testMinorAccuracy = new SemiMinorAxisAccuracy(0);
        SemiMajorAxisOrientation testMajorOrientation = new SemiMajorAxisOrientation(0);
        
        PositionalAccuracy testPositionalAccuracy = new PositionalAccuracy(
                testMajorAccuracy,
                testMinorAccuracy,
                testMajorOrientation);
        
        BigDecimal actualValue = OssPositionalAccuracy
                .genericPositionalAccuracy(testPositionalAccuracy)
                .getSemiMajor();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test that an input semi major axis accuracy value below lower bound (0) throws IllegalArgumentException
     */
    @Test
    public void shouldThrowExceptionSemiMajorAxisAccuracyBelowLowerBound() {
        
        Integer testInput = -1;
        
        SemiMajorAxisAccuracy testMajorAccuracy = new SemiMajorAxisAccuracy(testInput);
        SemiMinorAxisAccuracy testMinorAccuracy = new SemiMinorAxisAccuracy(0);
        SemiMajorAxisOrientation testMajorOrientation = new SemiMajorAxisOrientation(0);
        
        PositionalAccuracy testPositionalAccuracy = new PositionalAccuracy(
                testMajorAccuracy,
                testMinorAccuracy,
                testMajorOrientation);
        
        try {
            J2735PositionalAccuracy actualValue = OssPositionalAccuracy
                    .genericPositionalAccuracy(testPositionalAccuracy);
            fail("Expected IllegalArgumentException");
        } catch (RuntimeException e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }
        
    }
    
    /**
     * Test that an input semi major axis accuracy value above upper bound (255) throws IllegalArgumentException
     */
    @Test
    public void shouldThrowExceptionSemiMajorAxisAccuracyAboveUpperBound() {
        
        Integer testInput = 256;
        
        SemiMajorAxisAccuracy testMajorAccuracy = new SemiMajorAxisAccuracy(testInput);
        SemiMinorAxisAccuracy testMinorAccuracy = new SemiMinorAxisAccuracy(0);
        SemiMajorAxisOrientation testMajorOrientation = new SemiMajorAxisOrientation(0);
        
        PositionalAccuracy testPositionalAccuracy = new PositionalAccuracy(
                testMajorAccuracy,
                testMinorAccuracy,
                testMajorOrientation);
        
        try {
            J2735PositionalAccuracy actualValue = OssPositionalAccuracy
                    .genericPositionalAccuracy(testPositionalAccuracy);
            fail("Expected IllegalArgumentException");
        } catch (RuntimeException e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }
        
    }
    
    // SemiMinorAxisAccuracy tests
    
    /**
     * Test that the minimum semi minor axis accuracy value (0) returns (0.00)
     */
    @Test
    public void shouldReturnMinimumSemiMinorAxisAccuracy() {
        
        Integer testInput = 0;
        BigDecimal expectedValue = BigDecimal.ZERO.setScale(2);
        
        SemiMajorAxisAccuracy testMajorAccuracy = new SemiMajorAxisAccuracy(0);
        SemiMinorAxisAccuracy testMinorAccuracy = new SemiMinorAxisAccuracy(testInput);
        SemiMajorAxisOrientation testMajorOrientation = new SemiMajorAxisOrientation(0);
        
        PositionalAccuracy testPositionalAccuracy = new PositionalAccuracy(
                testMajorAccuracy,
                testMinorAccuracy,
                testMajorOrientation);
        
        BigDecimal actualValue = OssPositionalAccuracy
                .genericPositionalAccuracy(testPositionalAccuracy)
                .getSemiMinor();
        
        assertEquals(expectedValue, actualValue);
        
    }
    
    /**
     * Test that the maximum semi-minor axis accuracy value (254) returns (12.70)
     */
    @Test
    public void shouldReturnMaximumSemiMinorAxisAccuracy() {
        
        Integer testInput = 254;
        BigDecimal expectedValue = BigDecimal.valueOf(12.70).setScale(2);
        
        SemiMajorAxisAccuracy testMajorAccuracy = new SemiMajorAxisAccuracy(0);
        SemiMinorAxisAccuracy testMinorAccuracy = new SemiMinorAxisAccuracy(testInput);
        SemiMajorAxisOrientation testMajorOrientation = new SemiMajorAxisOrientation(0);
        
        PositionalAccuracy testPositionalAccuracy = new PositionalAccuracy(
                testMajorAccuracy,
                testMinorAccuracy,
                testMajorOrientation);
        
        BigDecimal actualValue = OssPositionalAccuracy
                .genericPositionalAccuracy(testPositionalAccuracy)
                .getSemiMinor();
        
        assertEquals(expectedValue, actualValue);
    
    }
    
    /**
     * Test that maximum semi-minor axis accuracy undefined flag (255) returns (null)
     */
    @Test
    public void shouldReturnUndefinedSemiMinorAxisAccuracy() {
        
        Integer testInput = 255;
        BigDecimal expectedValue = null;
        
        SemiMajorAxisAccuracy testMajorAccuracy = new SemiMajorAxisAccuracy(0);
        SemiMinorAxisAccuracy testMinorAccuracy = new SemiMinorAxisAccuracy(testInput);
        SemiMajorAxisOrientation testMajorOrientation = new SemiMajorAxisOrientation(0);
        
        PositionalAccuracy testPositionalAccuracy = new PositionalAccuracy(
                testMajorAccuracy,
                testMinorAccuracy,
                testMajorOrientation);
        
        BigDecimal actualValue = OssPositionalAccuracy
                .genericPositionalAccuracy(testPositionalAccuracy)
                .getSemiMinor();
        
        assertEquals(expectedValue, actualValue);
       
    }
    
    /**
     * Test that a semi-minor axis accuracy value below the lower bound (0) throws IllegalArgumentException
     */
    @Test
    public void shouldThrowExceptionSemiMinorAxisAccuracyBelowLowerBound() {
        
        Integer testInput = -1;
        
        SemiMajorAxisAccuracy testMajorAccuracy = new SemiMajorAxisAccuracy(0);
        SemiMinorAxisAccuracy testMinorAccuracy = new SemiMinorAxisAccuracy(testInput);
        SemiMajorAxisOrientation testMajorOrientation = new SemiMajorAxisOrientation(0);
        
        PositionalAccuracy testPositionalAccuracy = new PositionalAccuracy(
                testMajorAccuracy,
                testMinorAccuracy,
                testMajorOrientation);
        
        try { 
            J2735PositionalAccuracy actualValue = OssPositionalAccuracy
                    .genericPositionalAccuracy(testPositionalAccuracy);
            fail("Expected IllegalArgumentException");
        } catch (RuntimeException e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }
        
    }
    
    /**
     * Test that a semi-minor axis accuracy value above the upper bound (255) throws IllegalArgumentException
     */
    @Test
    public void shouldThrowExceptionSemiMinorAxisAccuracyAboveUpperBound() {
        
        Integer testInput = 256;
        
        SemiMajorAxisAccuracy testMajorAccuracy = new SemiMajorAxisAccuracy(0);
        SemiMinorAxisAccuracy testMinorAccuracy = new SemiMinorAxisAccuracy(testInput);
        SemiMajorAxisOrientation testMajorOrientation = new SemiMajorAxisOrientation(0);
        
        PositionalAccuracy testPositionalAccuracy = new PositionalAccuracy(
                testMajorAccuracy,
                testMinorAccuracy,
                testMajorOrientation);
        
        try {
            J2735PositionalAccuracy actualValue = OssPositionalAccuracy
                    .genericPositionalAccuracy(testPositionalAccuracy);
            fail("Expected IllegalArgumentException");
        } catch (RuntimeException e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }
        
    }

    // SemiMajorOrientation test
    
    /**
     * Test that the minimum semi-major axis orientation value (0) returns (0)
     */
    @Test
    public void shouldReturnMinimumSemiMajorOrientation() {
        
        Integer testInput = 0;
        BigDecimal expectedValue = BigDecimal.ZERO.setScale(10);
        
        SemiMajorAxisAccuracy testMajorAccuracy = new SemiMajorAxisAccuracy(0);
        SemiMinorAxisAccuracy testMinorAccuracy = new SemiMinorAxisAccuracy(0);
        SemiMajorAxisOrientation testMajorOrientation = new SemiMajorAxisOrientation(testInput);
        
        PositionalAccuracy testPositionalAccuracy = new PositionalAccuracy(
                testMajorAccuracy,
                testMinorAccuracy,
                testMajorOrientation);
        
        BigDecimal actualValue = OssPositionalAccuracy
                .genericPositionalAccuracy(testPositionalAccuracy)
                .getOrientation();
        
        assertEquals(expectedValue, actualValue);
        
    }
    
    /**
     * Test that the semi-major axis orientation value (1) returns (0.0054932479)
     */
    @Test
    public void shouldReturnIncrementSemiMajorOrientation() {
        
        Integer testInput = 1;
        BigDecimal expectedValue = BigDecimal.valueOf(0.0054932479);
        
        SemiMajorAxisAccuracy testMajorAccuracy = new SemiMajorAxisAccuracy(0);
        SemiMinorAxisAccuracy testMinorAccuracy = new SemiMinorAxisAccuracy(0);
        SemiMajorAxisOrientation testMajorOrientation = new SemiMajorAxisOrientation(testInput);
        
        PositionalAccuracy testPositionalAccuracy = new PositionalAccuracy(
                testMajorAccuracy,
                testMinorAccuracy,
                testMajorOrientation);
        
        BigDecimal actualValue = OssPositionalAccuracy
                .genericPositionalAccuracy(testPositionalAccuracy)
                .getOrientation();
        
        assertEquals(expectedValue, actualValue);
        
    }
    
    /**
     * Test that the maximum semi-major axis orientation value (65534) returns (359.9945078786)
     */
    @Test
    public void shouldReturnMaximumSemiMajorOrientation() {
        
        Integer testInput = 65534;
        BigDecimal expectedValue = BigDecimal.valueOf(359.9945078786);
        
        SemiMajorAxisAccuracy testMajorAccuracy = new SemiMajorAxisAccuracy(0);
        SemiMinorAxisAccuracy testMinorAccuracy = new SemiMinorAxisAccuracy(0);
        SemiMajorAxisOrientation testMajorOrientation = new SemiMajorAxisOrientation(testInput);
        
        PositionalAccuracy testPositionalAccuracy = new PositionalAccuracy(
                testMajorAccuracy,
                testMinorAccuracy,
                testMajorOrientation);
        
        BigDecimal actualValue = OssPositionalAccuracy
                .genericPositionalAccuracy(testPositionalAccuracy)
                .getOrientation();
        
        assertEquals(expectedValue, actualValue);
        
    }
    
    /**
     * Test that the undefined semi-major axis orientation flag value (65535) returns (null)
     */
    @Test
    public void shouldReturnUndefinedSemiMajorOrientation() {
        
        Integer testInput = 65535;
        
        BigDecimal expectedValue = null;
        
        SemiMajorAxisAccuracy testMajorAccuracy = new SemiMajorAxisAccuracy(0);
        SemiMinorAxisAccuracy testMinorAccuracy = new SemiMinorAxisAccuracy(0);
        SemiMajorAxisOrientation testMajorOrientation = new SemiMajorAxisOrientation(testInput);
        
        PositionalAccuracy testPositionalAccuracy = new PositionalAccuracy(
                testMajorAccuracy,
                testMinorAccuracy,
                testMajorOrientation);
        
        BigDecimal actualValue = OssPositionalAccuracy
                .genericPositionalAccuracy(testPositionalAccuracy)
                .getOrientation();
        
        assertEquals(expectedValue, actualValue);
        
    }
    
    /**
     * Test that semi-major axis orientation value below the lower bound (0) throws IllegalArgumentException
     */
    @Test
    public void shouldThrowExceptionSemiMajorAxisOrientationBelowLowerBound() {
        
        Integer testInput = -1;
        
        SemiMajorAxisAccuracy testMajorAccuracy = new SemiMajorAxisAccuracy(0);
        SemiMinorAxisAccuracy testMinorAccuracy = new SemiMinorAxisAccuracy(0);
        SemiMajorAxisOrientation testMajorOrientation = new SemiMajorAxisOrientation(testInput);
        
        PositionalAccuracy testPositionalAccuracy = new PositionalAccuracy(
                testMajorAccuracy,
                testMinorAccuracy,
                testMajorOrientation);
        
        try {
            J2735PositionalAccuracy actualValue = OssPositionalAccuracy
                    .genericPositionalAccuracy(testPositionalAccuracy);
            fail("Expected IllegalArgumentException");
        } catch (RuntimeException e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }
        
    }
    
    /**
     * Test that a semi-major axis orientation value above the upper bound (65535) throws IllegalArgumentException
     */
    @Test
    public void shouldThrowExceptionSemiMajorAxisOrientationAboveUpperBound() {
        
        Integer testInput = 65536;
        
        SemiMajorAxisAccuracy testMajorAccuracy = new SemiMajorAxisAccuracy(0);
        SemiMinorAxisAccuracy testMinorAccuracy = new SemiMinorAxisAccuracy(0);
        SemiMajorAxisOrientation testMajorOrientation = new SemiMajorAxisOrientation(testInput);
        
        PositionalAccuracy testPositionalAccuracy = new PositionalAccuracy(
                testMajorAccuracy,
                testMinorAccuracy,
                testMajorOrientation);
        
        try {
            J2735PositionalAccuracy actualValue = OssPositionalAccuracy
                    .genericPositionalAccuracy(testPositionalAccuracy);
            fail("Expected IllegalArgumentException");
        } catch (RuntimeException e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }
        
    }
    
}
