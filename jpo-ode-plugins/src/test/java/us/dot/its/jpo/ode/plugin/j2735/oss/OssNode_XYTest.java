package us.dot.its.jpo.ode.plugin.j2735.oss;

import static org.junit.Assert.*;

import java.math.BigDecimal;

import org.junit.Test;

import us.dot.its.jpo.ode.j2735.dsrc.Node_XY_24b;
import us.dot.its.jpo.ode.j2735.dsrc.Offset_B12;
import us.dot.its.jpo.ode.plugin.j2735.J2735Node_XY;

/**
 * -- Summary --
 * JUnit test class for OssNode_XY
 * 
 * Verifies correct conversion from Node-XY-24b to compliant J2735Node_XY
 * 
 * Note that DE_Offset_B12 elements are tested by OssOffsetTest class
 * 
 * -- Documentation --
 * Data Frame: DF_Node_XY_24b
 * Use: A 24-bit node type with offset values from the last point in X and Y.
 * ASN.1 Representation:
 *    Node-XY-24b ::= SEQUENCE {
 *       x Offset-B12,
 *       y Offset-B12
 *       }
 *
 * Data Element: DE_Offset_B12
 * Use: A 12-bit delta offset in X, Y or Z direction from some known point. For non-vehicle centric coordinate 
 * frames of reference, non-vehicle centric coordinate frames of reference, offset is positive to the East (X) 
 * and to the North (Y) directions. The most negative value shall be used to indicate an unknown value.
 * ASN.1 Representation:
 *    Offset-B12 ::= INTEGER (-2048..2047) -- a range of +- 20.47 meters
 *
 */
public class OssNode_XYTest {
    
    /**
     * Test that an X,Y input pair with value (-2047,-2047) returns (-20.47, -20.47)
     */
    @Test
    public void shouldReturnMinimumOffsetPair() {
        
        Integer testInput = -2047;
        BigDecimal expectedValue = BigDecimal.valueOf(-20.47);
        
        Offset_B12 testOffsetX = new Offset_B12(testInput);
        Offset_B12 testOffsetY = new Offset_B12(testInput);
        
        Node_XY_24b testNode = new Node_XY_24b(testOffsetX, testOffsetY);
        
        J2735Node_XY actualNode = OssNode_XY.genericNode_XY(testNode);
        
        assertEquals(expectedValue, actualNode.getX());
        assertEquals(expectedValue, actualNode.getY());
        
    }
    
    /**
     * Test that an X,Y input pair with value (-2046, -2046) returns (-20.46, -20.46)
     */
    @Test
    public void shouldReturnCornerCaseMinimumOffsetPair() {
        
        Integer testInput = -2046;
        BigDecimal expectedValue = BigDecimal.valueOf(-20.46);
        
        Offset_B12 testOffsetX = new Offset_B12(testInput);
        Offset_B12 testOffsetY = new Offset_B12(testInput);
        
        Node_XY_24b testNode = new Node_XY_24b(testOffsetX, testOffsetY);
        
        J2735Node_XY actualNode = OssNode_XY.genericNode_XY(testNode);
        
        assertEquals(expectedValue, actualNode.getX());
        assertEquals(expectedValue, actualNode.getY());
        
    }
    
    /**
     * Test that an X,Y input pair with value (0,0) returns (0.00, 0.00)
     */
    @Test
    public void shouldReturnZeroOffsetPair() {
        
        Integer testInput = 0;
        BigDecimal expectedValue = BigDecimal.ZERO.setScale(2);
        
        Offset_B12 testOffsetX = new Offset_B12(testInput);
        Offset_B12 testOffsetY = new Offset_B12(testInput);
        
        Node_XY_24b testNode = new Node_XY_24b(testOffsetX, testOffsetY);
        
        J2735Node_XY actualNode = OssNode_XY.genericNode_XY(testNode);
        
        assertEquals(expectedValue, actualNode.getX());
        assertEquals(expectedValue, actualNode.getY());
        
    }
    
    /**
     * Test that an X,Y input pair with value (2046,2046) returns (20.46, 20.46)
     */
    @Test
    public void shouldReturnCornerCaseMaximumOffsetPair() {

        Integer testInput = 2046;
        BigDecimal expectedValue = BigDecimal.valueOf(20.46);
        
        Offset_B12 testOffsetX = new Offset_B12(testInput);
        Offset_B12 testOffsetY = new Offset_B12(testInput);
        
        Node_XY_24b testNode = new Node_XY_24b(testOffsetX, testOffsetY);
        
        J2735Node_XY actualNode = OssNode_XY.genericNode_XY(testNode);
        
        assertEquals(expectedValue, actualNode.getX());
        assertEquals(expectedValue, actualNode.getY());
    }
    
    /**
     * Test that an X,Y input pair with value (2047,2047) returns (20.47, 20.47)
     */
    @Test
    public void shouldReturnMaximumOffsetPair() {

        Integer testInput = 2047;
        BigDecimal expectedValue = BigDecimal.valueOf(20.47);
        
        Offset_B12 testOffsetX = new Offset_B12(testInput);
        Offset_B12 testOffsetY = new Offset_B12(testInput);
        
        Node_XY_24b testNode = new Node_XY_24b(testOffsetX, testOffsetY);
        
        J2735Node_XY actualNode = OssNode_XY.genericNode_XY(testNode);
        
        assertEquals(expectedValue, actualNode.getX());
        assertEquals(expectedValue, actualNode.getY());
        
    }
    
    /**
     * Test that an X,Y input pair with flag values (-2048,-2048) returns (null, null)
     */
    @Test
    public void shouldReturnUndefinedOffsetPair() {

        Integer testInput = -2048;
        BigDecimal expectedValue = null;
        
        Offset_B12 testOffsetX = new Offset_B12(testInput);
        Offset_B12 testOffsetY = new Offset_B12(testInput);
        
        Node_XY_24b testNode = new Node_XY_24b(testOffsetX, testOffsetY);
        
        J2735Node_XY actualNode = OssNode_XY.genericNode_XY(testNode);
        
        assertEquals(expectedValue, actualNode.getX());
        assertEquals(expectedValue, actualNode.getY());
        
    }
    
    /**
     * Test that an X offset value (-2049) below the lower bound (-2048) throws IllegalArgumentException
     */
    @Test
    public void shouldThrowExceptionXOffsetBelowLowerBound() {

        Integer testInput = -2049;
        
        Offset_B12 testOffsetX = new Offset_B12(testInput);
        Offset_B12 testOffsetY = new Offset_B12(0);
        
        Node_XY_24b testNode = new Node_XY_24b(testOffsetX, testOffsetY);
        
        try {
            OssNode_XY.genericNode_XY(testNode);
            fail("Expected IllegalArgumentException");
        } catch (RuntimeException e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }
        
    }
    
    /**
     * Test that an X offset value (2048) above the upper bound (2047) throws IllegalArgumentException
     */
    @Test
    public void shouldThrowExceptionXOffsetAboveUpperBound() {

        Integer testInput = 2048;
        
        Offset_B12 testOffsetX = new Offset_B12(testInput);
        Offset_B12 testOffsetY = new Offset_B12(0);
        
        Node_XY_24b testNode = new Node_XY_24b(testOffsetX, testOffsetY);
        
        try {
            OssNode_XY.genericNode_XY(testNode);
            fail("Expected IllegalArgumentException");
        } catch (RuntimeException e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }
    }
    
    /**
     * Test that a Y offset value (-2049) below the lower bound (-2048) throws IllegalArgumentException
     */
    @Test
    public void shouldThrowExceptionYOffsetBelowLowerBound() {

        Integer testInput = -2049;
        
        Offset_B12 testOffsetX = new Offset_B12(0);
        Offset_B12 testOffsetY = new Offset_B12(testInput);
        
        Node_XY_24b testNode = new Node_XY_24b(testOffsetX, testOffsetY);
        
        try {
            OssNode_XY.genericNode_XY(testNode);
            fail("Expected IllegalArgumentException");
        } catch (RuntimeException e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }
    }
    
    /**
     * Test that a Y offset value (2048) above the upper bound (2047) throws IllegalArgumentException
     */
    @Test
    public void shouldThrowExceptionYOffsetAboveUpperBound() {

        Integer testInput = 2048;
        
        Offset_B12 testOffsetX = new Offset_B12(0);
        Offset_B12 testOffsetY = new Offset_B12(testInput);
        
        Node_XY_24b testNode = new Node_XY_24b(testOffsetX, testOffsetY);
        
        try {
            OssNode_XY.genericNode_XY(testNode);
            fail("Expected IllegalArgumentException");
        } catch (RuntimeException e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }
    }


}
