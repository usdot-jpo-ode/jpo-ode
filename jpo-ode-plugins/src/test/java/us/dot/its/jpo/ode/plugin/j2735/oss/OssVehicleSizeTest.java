package us.dot.its.jpo.ode.plugin.j2735.oss;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import us.dot.its.jpo.ode.j2735.dsrc.VehicleLength;
import us.dot.its.jpo.ode.j2735.dsrc.VehicleSize;
import us.dot.its.jpo.ode.j2735.dsrc.VehicleWidth;
import us.dot.its.jpo.ode.plugin.j2735.J2735VehicleSize;

/**
 * -- Summary --
 * JUnit test class for OssVehicleSize
 * 
 * Verifies correct conversion from generic vehicle size to compliant-J2735VehicleSize
 * 
 * -- Documentation --
 * Data Frame: DF_VehicleSize
 * Use: The DF_VehicleSize is a data frame representing the vehicle length and vehicle width in a single data concept.
 * ASN.1 Representation:
 *    VehicleSize ::= SEQUENCE {
 *    width VehicleWidth,
 *    length VehicleLength
 *    }
 *
 * Data Element: DE_VehicleWidth
 * Use: The width of the vehicle expressed in centimeters, unsigned. The width shall be the widest point of the 
 * vehicle with all factory installed equipment. The value zero shall be sent when data is unavailable.
 * ASN.1 Representation:
 *    VehicleWidth ::= INTEGER (0..1023) -- LSB units are 1 cm with a range of >10 meters
 * 
 * Data Element: DE_VehicleLength
 * Use: The length of the vehicle measured from the edge of the front bumper to the edge of the rear bumper expressed 
 * in centimeters, unsigned. It should be noted that this value is often combined with a vehicle width value to form 
 * a data frame. The value zero shall be sent when data is unavailable.
 * ASN.1 Representation:
 *    VehicleLength ::= INTEGER (0.. 4095) -- LSB units of 1 cm with a range of >40 meters
 */
public class OssVehicleSizeTest {

    // VehicleWidth tests
    
    /**
     * Test that the undefined vehicle width value (0) returns null
     */
    @Test
    public void shouldReturnUndefinedVehicleWidth() {
        
        Integer testInput = 0;
        Integer expectedValue = null;
        
        VehicleWidth testWidth = new VehicleWidth(testInput);
        VehicleLength testLength = new VehicleLength(0);
        
        VehicleSize testVehicleSize = new VehicleSize(testWidth, testLength);
        
        OssVehicleSize actualVehicleSize = new OssVehicleSize(testVehicleSize);
        
        Integer actualValue = actualVehicleSize.getGenericVehicleSize().getWidth();
        
        assertEquals(expectedValue, actualValue);
        
    }
    
    /**
     * Test that the minimum vehicle width value (1) returns (1)
     */
    @Test
    public void shouldReturnMinimumVehicleWidth() {

        Integer testInput = 1;
        Integer expectedValue = 1;
        
        VehicleWidth testWidth = new VehicleWidth(testInput);
        VehicleLength testLength = new VehicleLength(0);
        
        VehicleSize testVehicleSize = new VehicleSize(testWidth, testLength);
        
        J2735VehicleSize actualVehicleSize = new OssVehicleSize(testVehicleSize).getGenericVehicleSize();
        
        Integer actualValue = actualVehicleSize.getWidth();
        
        assertEquals(expectedValue, actualValue);
        
    }
    
    /**
     * Test a minimum corner case vehicle width value (2) returns (2)
     */
    @Test
    public void shouldReturnCornerCaseMinimumVehicleWidth() {
        
        Integer testInput = 2;
        Integer expectedValue = 2;
        
        VehicleWidth testWidth = new VehicleWidth(testInput);
        VehicleLength testLength = new VehicleLength(0);
        
        VehicleSize testVehicleSize = new VehicleSize(testWidth, testLength);
        
        OssVehicleSize actualVehicleSize = new OssVehicleSize(testVehicleSize);
        
        Integer actualValue = actualVehicleSize.getGenericVehicleSize().getWidth();
        
        assertEquals(expectedValue, actualValue);
        
    }
    
    /**
     * Test that a maximum corner case vehicle width value (1022) returns (1022)
     */
    @Test
    public void shouldReturnCornerCaseMaximumVehicleWidth() {

        Integer testInput = 1022;
        Integer expectedValue = 1022;
        
        VehicleWidth testWidth = new VehicleWidth(testInput);
        VehicleLength testLength = new VehicleLength(0);
        
        VehicleSize testVehicleSize = new VehicleSize(testWidth, testLength);
        
        OssVehicleSize actualVehicleSize = new OssVehicleSize(testVehicleSize);
        
        Integer actualValue = actualVehicleSize.getGenericVehicleSize().getWidth();
        
        assertEquals(expectedValue, actualValue);
        
    }
    
    /**
     * Test that the maximum vehicle width value (1023) returns (1023)
     */
    @Test
    public void shouldReturnMaximumVehicleWidth() {

        Integer testInput = 1023;
        Integer expectedValue = 1023;
        
        VehicleWidth testWidth = new VehicleWidth(testInput);
        VehicleLength testLength = new VehicleLength(0);
        
        VehicleSize testVehicleSize = new VehicleSize(testWidth, testLength);
        
        OssVehicleSize actualVehicleSize = new OssVehicleSize(testVehicleSize);
        
        Integer actualValue = actualVehicleSize.getGenericVehicleSize().getWidth();
        
        assertEquals(expectedValue, actualValue);
        
    }
    
    /**
     * Test that a vehicle width value (-1) below the lower bound (0) throws IllegalArgumentException
     */
    @Test
    public void shouldThrowExceptionVehicleWidthBelowLowerBound() {

        Integer testInput = -1;
        
        VehicleWidth testWidth = new VehicleWidth(testInput);
        VehicleLength testLength = new VehicleLength(0);
        
        VehicleSize testVehicleSize = new VehicleSize(testWidth, testLength);
        
        try {
            new OssVehicleSize(testVehicleSize);
            fail("Expected IllegalArgumentException");
        } catch (RuntimeException e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }
        
    }
    
    /**
     * Test that a vehicle width value (1024) above the upper bound (1023) throws IllegalArgumentException
     */
    @Test
    public void shouldThrowExceptionVehicleWidthAboveUpperBound() {

        Integer testInput = 1024;
        
        VehicleWidth testWidth = new VehicleWidth(testInput);
        VehicleLength testLength = new VehicleLength(0);
        
        VehicleSize testVehicleSize = new VehicleSize(testWidth, testLength);
        
        try {
            new OssVehicleSize(testVehicleSize);
            fail("Expected IllegalArgumentException");
        } catch (RuntimeException e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }
    }
    
    // VehicleLength tests
    
    /**
     * Test that undefined vehicle length flag value (0) returns (null)
     */
    @Test
    public void shouldReturnUndefinedVehicleLength() {

        Integer testInput = 0;
        Integer expectedValue = null;
        
        VehicleWidth testWidth = new VehicleWidth(0);
        VehicleLength testLength = new VehicleLength(testInput);
        
        VehicleSize testVehicleSize = new VehicleSize(testWidth, testLength);
        
        OssVehicleSize actualVehicleSize = new OssVehicleSize(testVehicleSize);
        
        Integer actualValue = actualVehicleSize.getGenericVehicleSize().getLength();
        
        assertEquals(expectedValue, actualValue);
        
    }
    
    /**
     * Test that the minimum vehicle length value (1) returns (1)
     */
    @Test
    public void shouldReturnMinimumVehicleLength() {

        Integer testInput = 1;
        Integer expectedValue = 1;
        
        VehicleWidth testWidth = new VehicleWidth(0);
        VehicleLength testLength = new VehicleLength(testInput);
        
        VehicleSize testVehicleSize = new VehicleSize(testWidth, testLength);
        
        OssVehicleSize actualVehicleSize = new OssVehicleSize(testVehicleSize);
        
        Integer actualValue = actualVehicleSize.getGenericVehicleSize().getLength();
        
        assertEquals(expectedValue, actualValue);
        
    }
    
    /**
     * Test minimum vehicle length corner case value (2) returns (2)
     */
    @Test
    public void shouldReturnCornerCaseMinimumVehicleLength() {

        Integer testInput = 2;
        Integer expectedValue = 2;
        
        VehicleWidth testWidth = new VehicleWidth(0);
        VehicleLength testLength = new VehicleLength(testInput);
        
        VehicleSize testVehicleSize = new VehicleSize(testWidth, testLength);
        
        OssVehicleSize actualVehicleSize = new OssVehicleSize(testVehicleSize);
        
        Integer actualValue = actualVehicleSize.getGenericVehicleSize().getLength();
        
        assertEquals(expectedValue, actualValue);
        
    }
    
    /**
     * Test maximum vehicle length corner case value (4094) returns (4094)
     */
    @Test
    public void shouldReturnCornerCaseMaximumVehicleLength() {
        
        Integer testInput = 4094;
        Integer expectedValue = 4094;
        
        VehicleWidth testWidth = new VehicleWidth(0);
        VehicleLength testLength = new VehicleLength(testInput);
        
        VehicleSize testVehicleSize = new VehicleSize(testWidth, testLength);
        
        OssVehicleSize actualVehicleSize = new OssVehicleSize(testVehicleSize);
        
        Integer actualValue = actualVehicleSize.getGenericVehicleSize().getLength();
        
        assertEquals(expectedValue, actualValue);
        
    }
    
    /**
     * Test that the maximum vehicle length value (4095) returns (4095)
     */
    @Test
    public void shouldReturnMaximumVehicleLength() {

        Integer testInput = 4095;
        Integer expectedValue = 4095;
        
        VehicleWidth testWidth = new VehicleWidth(0);
        VehicleLength testLength = new VehicleLength(testInput);
        
        VehicleSize testVehicleSize = new VehicleSize(testWidth, testLength);
        
        OssVehicleSize actualVehicleSize = new OssVehicleSize(testVehicleSize);
        
        Integer actualValue = actualVehicleSize.getGenericVehicleSize().getLength();
        
        assertEquals(expectedValue, actualValue);
        
    }
    
    /**
     * Test that a vehicle length value (-1) below the lower bound (0) throws IllegalArgumentException
     */
    @Test
    public void shouldThrowExceptionVehicleLengthBelowLowerBound() {

        Integer testInput = -1;
        
        VehicleWidth testWidth = new VehicleWidth(0);
        VehicleLength testLength = new VehicleLength(testInput);
        
        VehicleSize testVehicleSize = new VehicleSize(testWidth, testLength);
        
        try {
            new OssVehicleSize(testVehicleSize);
            fail("Expected IllegalArgumentException");
        } catch (RuntimeException e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }
        
    }
    
    /**
     * Test that a vehicle length value (4096) above the upper bound (4095) throws IllegalArgumentException
     */
    @Test
    public void shouldThrowExceptionVehicleLengthAboveUpperBound() {

        Integer testInput = 4096;
        
        VehicleWidth testWidth = new VehicleWidth(0);
        VehicleLength testLength = new VehicleLength(testInput);
        
        VehicleSize testVehicleSize = new VehicleSize(testWidth, testLength);
        
        try {
            new OssVehicleSize(testVehicleSize);
            fail("Expected IllegalArgumentException");
        } catch (RuntimeException e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }
        
    }

}
