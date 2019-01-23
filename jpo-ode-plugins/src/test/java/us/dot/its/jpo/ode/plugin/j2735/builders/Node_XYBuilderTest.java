/*******************************************************************************
 * Copyright 2018 572682
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package us.dot.its.jpo.ode.plugin.j2735.builders;


import static org.junit.Assert.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;


import us.dot.its.jpo.ode.util.XmlUtils;
import us.dot.its.jpo.ode.util.XmlUtils.XmlUtilsException;
import us.dot.its.jpo.ode.plugin.j2735.J2735Node_XY;

/**
 * -- Summary --
 * JUnit test class for Node_XYBuilder
 * 
 * Verifies correct conversion from JsonNode to compliant J2735Node_XY
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
public class Node_XYBuilderTest {
    
    /**
     * Test that an X,Y input pair with value (-2047,-2047) returns (-20.47, -20.47)
     */
    @Test
    public void shouldReturnMinimumOffsetPair() {
        
        Integer testInput = -2047;
        BigDecimal expectedValue = BigDecimal.valueOf(-20.47);
        
        JsonNode testNode = null;
        try {
           testNode = (JsonNode) XmlUtils.fromXmlS("<positionOffset><x>" + testInput + "</x><y>" + testInput + "</y></positionOffset>", JsonNode.class);
        } catch (XmlUtilsException e) {
           fail("XML parsing error:" + e);
        }
        
        
        
        J2735Node_XY actualNode = Node_XYBuilder.genericNode_XY(testNode);
        
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
        
        JsonNode testNode = null;
        try {
           testNode = (JsonNode) XmlUtils.fromXmlS("<positionOffset><x>" + testInput + "</x><y>" + testInput + "</y></positionOffset>", JsonNode.class);
        } catch (XmlUtilsException e) {
           fail("XML parsing error:" + e);
        }
        
        J2735Node_XY actualNode = Node_XYBuilder.genericNode_XY(testNode);
        
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
        
        JsonNode testNode = null;
        try {
           testNode = (JsonNode) XmlUtils.fromXmlS("<positionOffset><x>" + testInput + "</x><y>" + testInput + "</y></positionOffset>", JsonNode.class);
        } catch (XmlUtilsException e) {
           fail("XML parsing error:" + e);
        }
        
        J2735Node_XY actualNode = Node_XYBuilder.genericNode_XY(testNode);
        
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
        
        JsonNode testNode = null;
        try {
           testNode = (JsonNode) XmlUtils.fromXmlS("<positionOffset><x>" + testInput + "</x><y>" + testInput + "</y></positionOffset>", JsonNode.class);
        } catch (XmlUtilsException e) {
           fail("XML parsing error:" + e);
        }
        
        J2735Node_XY actualNode = Node_XYBuilder.genericNode_XY(testNode);
        
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
        
        JsonNode testNode = null;
        try {
           testNode = (JsonNode) XmlUtils.fromXmlS("<positionOffset><x>" + testInput + "</x><y>" + testInput + "</y></positionOffset>", JsonNode.class);
        } catch (XmlUtilsException e) {
           fail("XML parsing error:" + e);
        }
        
        J2735Node_XY actualNode = Node_XYBuilder.genericNode_XY(testNode);
        
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
        
        JsonNode testNode = null;
        try {
           testNode = (JsonNode) XmlUtils.fromXmlS("<positionOffset><x>" + testInput + "</x><y>" + testInput + "</y></positionOffset>", JsonNode.class);
        } catch (XmlUtilsException e) {
           fail("XML parsing error:" + e);
        }
        
        J2735Node_XY actualNode = Node_XYBuilder.genericNode_XY(testNode);
        
        assertEquals(expectedValue, actualNode.getX());
        assertEquals(expectedValue, actualNode.getY());
        
    }
    
    /**
     * Test that an X offset value (-2049) below the lower bound (-2048) throws IllegalArgumentException
     */
    @Test
    public void shouldThrowExceptionXOffsetBelowLowerBound() {

        Integer testInput = -2049;
        
        JsonNode testNode = null;
        try {
           testNode = (JsonNode) XmlUtils.fromXmlS("<positionOffset><x>" + testInput + "</x><y>" + testInput + "</y></positionOffset>", JsonNode.class);
        } catch (XmlUtilsException e) {
           fail("XML parsing error:" + e);
        }
        
        try {
            Node_XYBuilder.genericNode_XY(testNode);
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
        
        JsonNode testNode = null;
        try {
           testNode = (JsonNode) XmlUtils.fromXmlS("<positionOffset><x>" + testInput + "</x><y>" + testInput + "</y></positionOffset>", JsonNode.class);
        } catch (XmlUtilsException e) {
           fail("XML parsing error:" + e);
        }
        
        try {
            Node_XYBuilder.genericNode_XY(testNode);
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
        
        JsonNode testNode = null;
        try {
           testNode = (JsonNode) XmlUtils.fromXmlS("<positionOffset><x>" + testInput + "</x><y>" + testInput + "</y></positionOffset>", JsonNode.class);
        } catch (XmlUtilsException e) {
           fail("XML parsing error:" + e);
        }
        
        try {
            Node_XYBuilder.genericNode_XY(testNode);
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
        
        JsonNode testNode = null;
        try {
           testNode = (JsonNode) XmlUtils.fromXmlS("<positionOffset><x>" + testInput + "</x><y>" + testInput + "</y></positionOffset>", JsonNode.class);
        } catch (XmlUtilsException e) {
           fail("XML parsing error:" + e);
        }
        
        try {
            Node_XYBuilder.genericNode_XY(testNode);
            fail("Expected IllegalArgumentException");
        } catch (RuntimeException e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }
    }
    
    @Test
    public void testConstructorIsPrivate() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
      Constructor<Node_XYBuilder> constructor = Node_XYBuilder.class.getDeclaredConstructor();
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
