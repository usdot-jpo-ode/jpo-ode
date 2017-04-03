package us.dot.its.jpo.ode.plugin.j2735.oss;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;

import org.junit.Test;

import us.dot.its.jpo.ode.j2735.dsrc.Angle;
import us.dot.its.jpo.ode.j2735.dsrc.Offset_B11;
import us.dot.its.jpo.ode.j2735.dsrc.PivotPointDescription;
import us.dot.its.jpo.ode.j2735.dsrc.PivotingAllowed;

/**
 * -- Summary --
 * JUnit test class for OssPivotPointDescription
 * 
 * Verifies correct conversion from generic PivotPointDescription to compliant-J2735PivotPointDescription
 * 
 * -- Documentation --
 * Data Frame: DF_PivotPointDescription
 * Use: The DF_PivotPointDescription data frame is used to describe the geometric relationship between a vehicle 
 * and a trailer; or a dolly and another object to which it is connected. This point of connection can be fixed 
 * (non-pivoting) or can rotate in the horizontal plane at the connection point. The connection point itself is 
 * presumed to be along the centerline of the object in question. Rotation in the vertical plane (pitch and roll) 
 * is not modeled. The offset of the PivotPointDescription is with respect to the length and tangential to the 
 * width of the object in question. It should be noted that the length and width values are typically sent in the 
 * same message in which the PivotPointDescription is used. Given the known length of an object, the magnitude and 
 * sign of the pivotOffset projects the point of connection/rotation along the object’s centerline. If either of 
 * the objects pivots (has the element PivotingAllowed set true), the connection point pivots and the heading of 
 * the vehicle changes. The current angle between the two objects (one expressed with respect to the next) is 
 * provided by the pivotAngle entry. It should be noted that this is the only dynamic value when the vehicle is 
 * underway. It should also be noted that the heading and reported positions of the trailers are given with respect 
 * to the object in front of them. Only the lead vehicle and its BSM contain the absolute LLH and heading angle.
 * ASN.1 Representation:
 *    PivotPointDescription ::= SEQUENCE {
 *       pivotOffset Offset-B11,
 *          -- This gives a +- 10m range from the edge of the outline
 *          -- measured from the edge of the length of this unit
 *          -- a negative value is offset to inside the units
 *          -- a positive value is offset beyond the unit
 *       pivotAngle Angle,
 *          -- Measured between the center-line of this unit
 *          -- and the unit ahead which is pulling it.
 *          -- This value is required to project the units relative position,
 *       pivots PivotingAllowed,
 *          -- true if this unit can rotate about the pivot connection point
 *       ...
 *       }
 * 
 * Data Element: DE_Offset_B11
 * Use: An 11-bit delta offset in X or Y direction from some known point. For non-vehicle centric coordinate 
 * frames of reference, offset is positive to the East (X) and to the North (Y) directions. The most negative 
 * value shall be used to indicate an unknown value.
 * ASN.1 Representation:
 *    Offset-B11 ::= INTEGER (-1024..1023) 
 *       -- a range of +- 10.23 meters
 * 
 * Data Element: DE_Angle
 * Use: The DE_Angle data element Angle is used to describe an angular measurement in units of degrees. This 
 * data element is often used as a heading direction when in motion. In this use, the current heading of the 
 * sending device is expressed in unsigned units of 0.0125 degrees from North, such that 28799 such degrees 
 * represent 359.9875 degrees. North shall be defined as the axis defined by the WGS-84 coordinate system and 
 * its reference ellipsoid. Any angle "to the east" is defined as the positive direction. A value of 28800 shall 
 * be used when Angle is unavailable.
 * ASN.1 Representation:
 *    Angle ::= INTEGER (0..28800) 
 *       -- LSB of 0.0125 degrees 
 *       -- A range of 0 to 359.9875 degrees
 * 
 * Data Element: DE_PivotingAllowed
 * Use: The DE_PivotingAllowed data element is a flag set to true when the described connection point allows 
 * pivoting to occur. It is used to describe a trailer or dolly connection point.
 * ASN.1 Representation:
 *    PivotingAllowed ::= BOOLEAN
 *
 */
public class OssPivotPointDescriptionTest {
    
    // PivotOffset tests
    /**
     * Test undefined pivot offset flag value (-1024) returns (null)
     */
    @Test
    public void shouldReturnUndefinedPivotOffset() {
        
        Integer testInput = -1024;
        BigDecimal expectedValue = null;
        
        Offset_B11 testPivotOffset = new Offset_B11(testInput);
        Angle testPivotAngle = new Angle(0);
        PivotingAllowed testPivotingAllowed = new PivotingAllowed(false);
        
        PivotPointDescription testPivotPointDescription = new PivotPointDescription(
                testPivotOffset,
                testPivotAngle,
                testPivotingAllowed);
        
        BigDecimal actualValue = OssPivotPointDescription
                .genericPivotPointDescription(testPivotPointDescription)
                .getPivotOffset();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test minimum pivot offset value (-1023) returns (-10.23)
     */
    @Test
    public void shouldReturnMinimumPivotOffset() {
        
        Integer testInput = -1023;
        BigDecimal expectedValue = BigDecimal.valueOf(-10.23);
        
        Offset_B11 testPivotOffset = new Offset_B11(testInput);
        Angle testPivotAngle = new Angle(0);
        PivotingAllowed testPivotingAllowed = new PivotingAllowed(false);
        
        PivotPointDescription testPivotPointDescription = new PivotPointDescription(
                testPivotOffset,
                testPivotAngle,
                testPivotingAllowed);
        
        BigDecimal actualValue = OssPivotPointDescription
                .genericPivotPointDescription(testPivotPointDescription)
                .getPivotOffset();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test corner case minimum pivot offset value (-1022) returns (-10.22)
     */
    @Test
    public void shouldReturnCornerCaseMinimumPivotOffset() {
        
        Integer testInput = -1022;
        BigDecimal expectedValue = BigDecimal.valueOf(-10.22);
        
        Offset_B11 testPivotOffset = new Offset_B11(testInput);
        Angle testPivotAngle = new Angle(0);
        PivotingAllowed testPivotingAllowed = new PivotingAllowed(false);
        
        PivotPointDescription testPivotPointDescription = new PivotPointDescription(
                testPivotOffset,
                testPivotAngle,
                testPivotingAllowed);
        
        BigDecimal actualValue = OssPivotPointDescription
                .genericPivotPointDescription(testPivotPointDescription)
                .getPivotOffset();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test known, middle pivot offset value (-44) returns (-0.44)
     */
    @Test
    public void shouldReturnMiddlePivotOffset() {
        
        Integer testInput = -44;
        BigDecimal expectedValue = BigDecimal.valueOf(-00.44);
        
        Offset_B11 testPivotOffset = new Offset_B11(testInput);
        Angle testPivotAngle = new Angle(0);
        PivotingAllowed testPivotingAllowed = new PivotingAllowed(false);
        
        PivotPointDescription testPivotPointDescription = new PivotPointDescription(
                testPivotOffset,
                testPivotAngle,
                testPivotingAllowed);
        
        BigDecimal actualValue = OssPivotPointDescription
                .genericPivotPointDescription(testPivotPointDescription)
                .getPivotOffset();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test corner case maximum pivot offset value (1022) returns (10.22)
     */
    @Test
    public void shouldReturnCornerCaseMaximumPivotOffset() {
        
        Integer testInput = 1022;
        BigDecimal expectedValue = BigDecimal.valueOf(10.22);
        
        Offset_B11 testPivotOffset = new Offset_B11(testInput);
        Angle testPivotAngle = new Angle(0);
        PivotingAllowed testPivotingAllowed = new PivotingAllowed(false);
        
        PivotPointDescription testPivotPointDescription = new PivotPointDescription(
                testPivotOffset,
                testPivotAngle,
                testPivotingAllowed);
        
        BigDecimal actualValue = OssPivotPointDescription
                .genericPivotPointDescription(testPivotPointDescription)
                .getPivotOffset();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test maximum pivot offset value (1023) returns (10.23)
     */
    @Test
    public void shouldReturnMaximumPivotOffset() {
        
        Integer testInput = 1023;
        BigDecimal expectedValue = BigDecimal.valueOf(10.23);
        
        Offset_B11 testPivotOffset = new Offset_B11(testInput);
        Angle testPivotAngle = new Angle(0);
        PivotingAllowed testPivotingAllowed = new PivotingAllowed(false);
        
        PivotPointDescription testPivotPointDescription = new PivotPointDescription(
                testPivotOffset,
                testPivotAngle,
                testPivotingAllowed);
        
        BigDecimal actualValue = OssPivotPointDescription
                .genericPivotPointDescription(testPivotPointDescription)
                .getPivotOffset();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test pivot offset value (-1025) below lower bound (-1024) throws IllegalArgumentException
     */
    @Test
    public void shouldThrowExceptionPivotOffsetBelowLowerBound() {
        
        Integer testInput = -1025;
        
        Offset_B11 testPivotOffset = new Offset_B11(testInput);
        Angle testPivotAngle = new Angle(0);
        PivotingAllowed testPivotingAllowed = new PivotingAllowed(false);
        
        PivotPointDescription testPivotPointDescription = new PivotPointDescription(
                testPivotOffset,
                testPivotAngle,
                testPivotingAllowed);
        
        try {
           OssPivotPointDescription
                   .genericPivotPointDescription(testPivotPointDescription)
                   .getPivotOffset();
           fail("Expected IllegalArgumentException");
        } catch (RuntimeException e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }
    }
    
    /**
     * Test pivot offset value (1024) above upper bound (1023) throws IllegalArgumentException
     */
    @Test
    public void shouldThrowExceptionPivotOffsetAboveUpperBound() {
        
        Integer testInput = 1024;
        
        Offset_B11 testPivotOffset = new Offset_B11(testInput);
        Angle testPivotAngle = new Angle(0);
        PivotingAllowed testPivotingAllowed = new PivotingAllowed(false);
        
        PivotPointDescription testPivotPointDescription = new PivotPointDescription(
                testPivotOffset,
                testPivotAngle,
                testPivotingAllowed);
        
        try {
           OssPivotPointDescription
                   .genericPivotPointDescription(testPivotPointDescription)
                   .getPivotOffset();
           fail("Expected IllegalArgumentException");
        } catch (RuntimeException e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }
    }
    
    // pivotAngle tests
    /**
     * Test undefined pivot angle flag value (28800) returns (null)
     */
    @Test
    public void shouldReturnUndefinedPivotAngle() {
        
        Integer testInput = 28800;
        BigDecimal expectedValue = null;
        
        Offset_B11 testPivotOffset = new Offset_B11(0);
        Angle testPivotAngle = new Angle(testInput);
        PivotingAllowed testPivotingAllowed = new PivotingAllowed(false);
        
        PivotPointDescription testPivotPointDescription = new PivotPointDescription(
                testPivotOffset,
                testPivotAngle,
                testPivotingAllowed);
        
        BigDecimal actualValue = OssPivotPointDescription
                .genericPivotPointDescription(testPivotPointDescription)
                .getPivotAngle();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test minimum pivot angle value (0) returns (0.0000)
     */
    @Test
    public void shouldReturnMinimumPivotAngle() {
        
        Integer testInput = 0;
        BigDecimal expectedValue = BigDecimal.ZERO.setScale(4);
        
        Offset_B11 testPivotOffset = new Offset_B11(0);
        Angle testPivotAngle = new Angle(testInput);
        PivotingAllowed testPivotingAllowed = new PivotingAllowed(false);
        
        PivotPointDescription testPivotPointDescription = new PivotPointDescription(
                testPivotOffset,
                testPivotAngle,
                testPivotingAllowed);
        
        BigDecimal actualValue = OssPivotPointDescription
                .genericPivotPointDescription(testPivotPointDescription)
                .getPivotAngle();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test corner case minimum pivot angle value (1) returns (0.0125)
     */
    @Test
    public void shouldReturnCornerCaseMinimumPivotAngle() {
        
        Integer testInput = 1;
        BigDecimal expectedValue = BigDecimal.valueOf(0.0125);
        
        Offset_B11 testPivotOffset = new Offset_B11(0);
        Angle testPivotAngle = new Angle(testInput);
        PivotingAllowed testPivotingAllowed = new PivotingAllowed(false);
        
        PivotPointDescription testPivotPointDescription = new PivotPointDescription(
                testPivotOffset,
                testPivotAngle,
                testPivotingAllowed);
        
        BigDecimal actualValue = OssPivotPointDescription
                .genericPivotPointDescription(testPivotPointDescription)
                .getPivotAngle();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test known, middle pivot angle value (14511) returns (181.3875)
     */
    @Test
    public void shouldReturnMiddlePivotAngle() {
        
        Integer testInput = 14511;
        BigDecimal expectedValue = BigDecimal.valueOf(181.3875);
        
        Offset_B11 testPivotOffset = new Offset_B11(0);
        Angle testPivotAngle = new Angle(testInput);
        PivotingAllowed testPivotingAllowed = new PivotingAllowed(false);
        
        PivotPointDescription testPivotPointDescription = new PivotPointDescription(
                testPivotOffset,
                testPivotAngle,
                testPivotingAllowed);
        
        BigDecimal actualValue = OssPivotPointDescription
                .genericPivotPointDescription(testPivotPointDescription)
                .getPivotAngle();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test corner case maximum pivot angle angle (28798) returns (359.9750)
     */
    @Test
    public void shouldReturnCornerCaseMaximumPivotAngle() {
        
        Integer testInput = 28798;
        BigDecimal expectedValue = BigDecimal.valueOf(359.9750).setScale(4);
        
        Offset_B11 testPivotOffset = new Offset_B11(0);
        Angle testPivotAngle = new Angle(testInput);
        PivotingAllowed testPivotingAllowed = new PivotingAllowed(false);
        
        PivotPointDescription testPivotPointDescription = new PivotPointDescription(
                testPivotOffset,
                testPivotAngle,
                testPivotingAllowed);
        
        BigDecimal actualValue = OssPivotPointDescription
                .genericPivotPointDescription(testPivotPointDescription)
                .getPivotAngle();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test maximum pivot angle value (28799) returns (359.9875)
     */
    @Test
    public void shouldReturnMaximumPivotAngle() {
        
        Integer testInput = 28799;
        BigDecimal expectedValue = BigDecimal.valueOf(359.9875);
        
        Offset_B11 testPivotOffset = new Offset_B11(0);
        Angle testPivotAngle = new Angle(testInput);
        PivotingAllowed testPivotingAllowed = new PivotingAllowed(false);
        
        PivotPointDescription testPivotPointDescription = new PivotPointDescription(
                testPivotOffset,
                testPivotAngle,
                testPivotingAllowed);
        
        BigDecimal actualValue = OssPivotPointDescription
                .genericPivotPointDescription(testPivotPointDescription)
                .getPivotAngle();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test that a pivot angle (-1) below the lower bound (0) throws IllegalArgumentException
     */
    @Test
    public void shouldThrowExceptionPivotAngleBelowLowerBound() {
        
        Integer testInput = -1;
        
        Offset_B11 testPivotOffset = new Offset_B11(0);
        Angle testPivotAngle = new Angle(testInput);
        PivotingAllowed testPivotingAllowed = new PivotingAllowed(false);
        
        PivotPointDescription testPivotPointDescription = new PivotPointDescription(
                testPivotOffset,
                testPivotAngle,
                testPivotingAllowed);
        
        try {
           OssPivotPointDescription
                   .genericPivotPointDescription(testPivotPointDescription)
                   .getPivotAngle();
           fail("Expected IllegalArgumentException");
        } catch (RuntimeException e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }
    }
    
    /**
     * Test that a pivot angle (28801) above the upper bound (28800) throws IllegalArgumentException
     */
    @Test
    public void shouldThrowExceptionPivotAngleAboveUpperBound() {
        
        Integer testInput = 28801;
        
        Offset_B11 testPivotOffset = new Offset_B11(0);
        Angle testPivotAngle = new Angle(testInput);
        PivotingAllowed testPivotingAllowed = new PivotingAllowed(false);
        
        PivotPointDescription testPivotPointDescription = new PivotPointDescription(
                testPivotOffset,
                testPivotAngle,
                testPivotingAllowed);
        
        try {
           OssPivotPointDescription
                   .genericPivotPointDescription(testPivotPointDescription)
                   .getPivotAngle();
           fail("Expected IllegalArgumentException");
        } catch (RuntimeException e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }
    }
    
    // PivotingAllowed tests
    /**
     * Test pivoting allowed value (false) returns (false)
     */
    @Test
    public void shouldReturnFalsePivotingAllowed() {
        
        Boolean testInput = false;
        Boolean expectedValue = false;
        
        Offset_B11 testPivotOffset = new Offset_B11(0);
        Angle testPivotAngle = new Angle(0);
        PivotingAllowed testPivotingAllowed = new PivotingAllowed(testInput);
        
        PivotPointDescription testPivotPointDescription = new PivotPointDescription(
                testPivotOffset,
                testPivotAngle,
                testPivotingAllowed);
        
        Boolean actualValue = OssPivotPointDescription
                .genericPivotPointDescription(testPivotPointDescription)
                .getPivots();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test pivoting allowed value (true) returns (true)
     */
    @Test
    public void shouldReturnTruePivotingAllowed() {
        
        Boolean testInput = true;
        Boolean expectedValue = true;
        
        Offset_B11 testPivotOffset = new Offset_B11(0);
        Angle testPivotAngle = new Angle(0);
        PivotingAllowed testPivotingAllowed = new PivotingAllowed(testInput);
        
        PivotPointDescription testPivotPointDescription = new PivotPointDescription(
                testPivotOffset,
                testPivotAngle,
                testPivotingAllowed);
        
        Boolean actualValue = OssPivotPointDescription
                .genericPivotPointDescription(testPivotPointDescription)
                .getPivots();
        
        assertEquals(expectedValue, actualValue);
    }
    
    @Test
    public void testConstructorIsPrivate() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
      Constructor<OssPivotPointDescription> constructor = OssPivotPointDescription.class.getDeclaredConstructor();
      assertTrue(Modifier.isPrivate(constructor.getModifiers()));
      constructor.setAccessible(true);
      try {
        constructor.newInstance();
        fail("Expected IllegalAccessException.class");
      } catch (Exception e) {
        assertEquals(InvocationTargetException.class, e.getClass());
      }
    }

}
