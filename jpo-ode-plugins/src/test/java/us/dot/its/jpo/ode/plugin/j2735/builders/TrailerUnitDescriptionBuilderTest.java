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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735TrailerUnitDescription;
import us.dot.its.jpo.ode.util.XmlUtils;
import us.dot.its.jpo.ode.util.XmlUtils.XmlUtilsException;

/**
 * -- Summary --
 * JUnit test class for TrailerUnitDescriptionBuilder
 * 
 * Verifies correct conversion from a JsonNode to compliant-J2735TrailerUnitDescription
 * 
 * Notes:
 * - Tested elements: isDolly, width, length, crumbData
 * - Required element Front pivot is tested by OssPivotPointDescriptionTest
 * - Required element Position offset is tested by OssNode_XYTest
 * - The rest of the elements of this class are optional and tested by other test classes
 * 
 * -- Documentation --
 * Data Frame: DF_TrailerUnitDescription
 * Use: The DF_TrailerUnitDescription data frame provides a physical description for one trailer or a dolly 
 * element (called a unit), including details of how it connects with other elements fore and aft.
 * ASN.1 Representation:
 *    TrailerUnitDescription ::= SEQUENCE {
 *       isDolly IsDolly, -- if false this is a trailer
 *       width VehicleWidth,
 *       length VehicleLength,
 *       height VehicleHeight OPTIONAL,
 *       mass TrailerMass OPTIONAL,
 *       bumperHeights BumperHeights OPTIONAL,
 *       centerOfGravity VehicleHeight OPTIONAL, 
 *       -- The front pivot point of the unit
 *       frontPivot PivotPointDescription, 
 *       -- The rear pivot point connecting to the next element, 
 *       -- if present and used (implies another unit is connected)
 *       rearPivot PivotPointDescription OPTIONAL,
 *       -- Rear wheel pivot point center-line offset 
 *       -- measured from the rear of the above length
 *       rearWheelOffset Offset-B12 OPTIONAL,
 *       -- the effective center-line of the wheel set
 *       -- Current Position relative to the hauling Vehicle
 *       positionOffset Node-XY-24b,
 *       elevationOffset VertOffset-B07 OPTIONAL,
 *       -- Past Position history relative to the hauling Vehicle
 *       crumbData TrailerHistoryPointList OPTIONAL,
 *       ...
 *       }
 * 
 * Data Element: DE_IsDolly
 * Use: A DE_IsDolly data element is a flag which is set to true to indicate that the described element is a 
 * dolly type rather than a trailer type of object. It should be noted that dollies (like trailers) may or may 
 * not pivot at the front and back connection points, and that they do not carry cargo or placards. Dollies do 
 * have an outline and connection point offsets like a trailer. Dollies have some form of draw bar to connect 
 * to the power unit (the vehicle or trailer in front of it). The term "bogie" is also used for dolly in some 
 * markets. In this standard, there is no differentiation between a dolly for a full trailer and a semi-trailer 
 * or a converter dolly. The only difference between an A-dolly (single coupling point) and a C-dolly (a dolly
 * with two coupling points arranged side by side) is the way in which the pivoting flag is set. (As a rule a 
 * C-dolly does not pivot.)
 * ASN.1 Representation:
 *    IsDolly ::= BOOLEAN -- When false indicates a trailer unit
 * 
 * Data Element: DE_VehicleWidth
 * Use: The width of the vehicle expressed in centimeters, unsigned. The width shall be the widest point of 
 * the vehicle with all factory installed equipment. The value zero shall be sent when data is unavailable.
 * ASN.1 Representation:
 *    VehicleWidth ::= INTEGER (0..1023) -- LSB units are 1 cm with a range of >10 meters
 * 
 * Data Element: DE_VehicleLength
 * Use: The length of the vehicle measured from the edge of the front bumper to the edge of the rear bumper 
 * expressed in centimeters, unsigned. It should be noted that this value is often combined with a vehicle 
 * width value to form a data frame. The value zero shall be sent when data is unavailable.
 * ASN.1 Representation:
 *    VehicleLength ::= INTEGER (0.. 4095) -- LSB units of 1 cm with a range of >40 meters
 *
 */
public class TrailerUnitDescriptionBuilderTest {
    
    // isDolly tests
    /**
     * Test isDolly value (true) returns (true)
     */
    @Test
    public void shouldReturnIsDollyTrue() {
        

        Boolean expectedValue = true;

        
        Boolean isDolly = true;
        int width = 0;
        int length = 0;
        String frontPivot = "<pivotOffset>" + 0 + "</pivotOffset><pivotAngle>" + 0 + "</pivotAngle><pivots>" + true + "</pivots>";
        String positionOffset = "<x>" + 0 + "</x><y>" + 0 + "</y>";
        int height = 0;
        int mass = 0;
        String bumperHeights = "<front>" + 2 + "</front><rear>" + 2 + "</rear>";
        int centerOfGravity = 0;
        String rearPivot = "<pivotOffset>" + 0 + "</pivotOffset><pivotAngle>" + 0 + "</pivotAngle><pivots>" + true + "</pivots>";
        int rearWheelOffset = 0;
        int elevationOffset = 0;
        String crumbData = "<thp><pivotAngle>" + 0 + "</pivotAngle><timeOffset>"
              + 1 + "</timeOffset><positionOffset>" + positionOffset + "</positionOffset><elevationOffset>" + 0
              + "</elevationOffset><heading>" + 0 + "</heading></thp>";

        
        
        
        JsonNode testtud = null;
        try {
           testtud = (JsonNode) XmlUtils.fromXmlS("<tud>" 
                 + "<isDolly>" + isDolly + "</isDolly>"
                 + "<width>" + width + "</width>"
                 + "<length>" + length + "</length>"                 
                 + "<frontPivot>" + frontPivot + "</frontPivot>"
                 + "<positionOffset>" + positionOffset + "</positionOffset>"
                 + "<height>" + height + "</height>"
                 + "<mass>" + mass + "</mass>"
                 + "<bumperHeights>" + bumperHeights + "</bumperHeights>"
                 + "<centerOfGravity>" + centerOfGravity + "</centerOfGravity>"
                 + "<rearPivot>" + rearPivot + "</rearPivot>"
                 + "<rearWheelOffset>" + rearWheelOffset + "</rearWheelOffset>"
                 + "<elevationOffset>" + elevationOffset + "</elevationOffset>" 
                 + "<crumbData>" + crumbData + "</crumbData>" 
                 + "</tud>", JsonNode.class);
        } catch (XmlUtilsException e) {
           fail("XML parsing error:" + e);
        }
        
        Boolean actualValue = TrailerUnitDescriptionBuilder.genericTrailerUnitDescription(testtud).getIsDolly();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test isDolly value (false) returns (false)
     */
    @Test
    public void shouldReturnIsDollyFalse() {
        

        Boolean expectedValue = false;

        
        Boolean isDolly = false;
        int width = 0;
        int length = 0;
        String frontPivot = "<pivotOffset>" + 0 + "</pivotOffset><pivotAngle>" + 0 + "</pivotAngle><pivots>" + true + "</pivots>";
        String positionOffset = "<x>" + 0 + "</x><y>" + 0 + "</y>";
        int height = 0;
        int mass = 0;
        String bumperHeights = "<front>" + 2 + "</front><rear>" + 2 + "</rear>";
        int centerOfGravity = 0;
        String rearPivot = "<pivotOffset>" + 0 + "</pivotOffset><pivotAngle>" + 0 + "</pivotAngle><pivots>" + true + "</pivots>";
        int rearWheelOffset = 0;
        int elevationOffset = 0;
        //String crumbData = "<pivotAngle>" + 0 + "</pivotAngle><timeOffset>" + 0 + "</timeOffset><positionOffset>" + "<x>" + 0 + "</x><y>" + 0 + "</y>" + "</positionOffset>";

        
        
        
        JsonNode testtud = null;
        try {
           testtud = (JsonNode) XmlUtils.fromXmlS("<tud>" 
                 + "<isDolly>" + isDolly + "</isDolly>"
                 + "<width>" + width + "</width>"
                 + "<length>" + length + "</length>"                 
                 + "<frontPivot>" + frontPivot + "</frontPivot>"
                 + "<positionOffset>" + positionOffset + "</positionOffset>"
                 + "<height>" + height + "</height>"
                 + "<mass>" + mass + "</mass>"
                 + "<bumperHeights>" + bumperHeights + "</bumperHeights>"
                 + "<centerOfGravity>" + centerOfGravity + "</centerOfGravity>"
                 + "<rearPivot>" + rearPivot + "</rearPivot>"
                 + "<rearWheelOffset>" + rearWheelOffset + "</rearWheelOffset>"
                 + "<elevationOffset>" + elevationOffset + "</elevationOffset>" 
                 //+ "<crumbData>" + crumbData + "</crumbData>" 
                 + "</tud>", JsonNode.class);
        } catch (XmlUtilsException e) {
           fail("XML parsing error:" + e);
        }
        
        
        Boolean actualValue = TrailerUnitDescriptionBuilder.genericTrailerUnitDescription(testtud).getIsDolly();
        
        assertEquals(expectedValue, actualValue);
    }
    
    // width tests
    /**
     * Test width undefined flag value (0) returns (null)
     */
    @Test
    public void shouldReturnUndefinedVehicleWidth() {
        

        Integer expectedValue = null;

        
        Boolean isDolly = false;
        int width = 0;
        int length = 0;
        String frontPivot = "<pivotOffset>" + 0 + "</pivotOffset><pivotAngle>" + 0 + "</pivotAngle><pivots>" + true + "</pivots>";
        String positionOffset = "<x>" + 0 + "</x><y>" + 0 + "</y>";
        int height = 0;
        int mass = 0;
        String bumperHeights = "<front>" + 2 + "</front><rear>" + 2 + "</rear>";
        int centerOfGravity = 0;
        String rearPivot = "<pivotOffset>" + 0 + "</pivotOffset><pivotAngle>" + 0 + "</pivotAngle><pivots>" + true + "</pivots>";
        int rearWheelOffset = 0;
        int elevationOffset = 0;
        //String crumbData = "<pivotAngle>" + 0 + "</pivotAngle><timeOffset>" + 0 + "</timeOffset><positionOffset>" + "<x>" + 0 + "</x><y>" + 0 + "</y>" + "</positionOffset>";

        
        
        
        JsonNode testtud = null;
        try {
           testtud = (JsonNode) XmlUtils.fromXmlS("<tud>" 
                 + "<isDolly>" + isDolly + "</isDolly>"
                 + "<width>" + width + "</width>"
                 + "<length>" + length + "</length>"                 
                 + "<frontPivot>" + frontPivot + "</frontPivot>"
                 + "<positionOffset>" + positionOffset + "</positionOffset>"
                 + "<height>" + height + "</height>"
                 + "<mass>" + mass + "</mass>"
                 + "<bumperHeights>" + bumperHeights + "</bumperHeights>"
                 + "<centerOfGravity>" + centerOfGravity + "</centerOfGravity>"
                 + "<rearPivot>" + rearPivot + "</rearPivot>"
                 + "<rearWheelOffset>" + rearWheelOffset + "</rearWheelOffset>"
                 + "<elevationOffset>" + elevationOffset + "</elevationOffset>" 
                 //+ "<crumbData>" + crumbData + "</crumbData>" 
                 + "</tud>", JsonNode.class);
        } catch (XmlUtilsException e) {
           fail("XML parsing error:" + e);
        }
        
        Integer actualValue = TrailerUnitDescriptionBuilder.genericTrailerUnitDescription(testtud).getWidth();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test minimum vehicle width value (1) returns (1)
     */
    @Test
    public void shouldReturnMinimumVehicleWidth() {
        

        Integer expectedValue = 1;

        
        
        Boolean isDolly = false;
        int width = 1;
        int length = 0;
        String frontPivot = "<pivotOffset>" + 0 + "</pivotOffset><pivotAngle>" + 0 + "</pivotAngle><pivots>" + true + "</pivots>";
        String positionOffset = "<x>" + 0 + "</x><y>" + 0 + "</y>";
        int height = 0;
        int mass = 0;
        String bumperHeights = "<front>" + 2 + "</front><rear>" + 2 + "</rear>";
        int centerOfGravity = 0;
        String rearPivot = "<pivotOffset>" + 0 + "</pivotOffset><pivotAngle>" + 0 + "</pivotAngle><pivots>" + true + "</pivots>";
        int rearWheelOffset = 0;
        int elevationOffset = 0;
        //String crumbData = "<pivotAngle>" + 0 + "</pivotAngle><timeOffset>" + 0 + "</timeOffset><positionOffset>" + "<x>" + 0 + "</x><y>" + 0 + "</y>" + "</positionOffset>";

        
        
        
        JsonNode testtud = null;
        try {
           testtud = (JsonNode) XmlUtils.fromXmlS("<tud>" 
                 + "<isDolly>" + isDolly + "</isDolly>"
                 + "<width>" + width + "</width>"
                 + "<length>" + length + "</length>"                 
                 + "<frontPivot>" + frontPivot + "</frontPivot>"
                 + "<positionOffset>" + positionOffset + "</positionOffset>"
                 + "<height>" + height + "</height>"
                 + "<mass>" + mass + "</mass>"
                 + "<bumperHeights>" + bumperHeights + "</bumperHeights>"
                 + "<centerOfGravity>" + centerOfGravity + "</centerOfGravity>"
                 + "<rearPivot>" + rearPivot + "</rearPivot>"
                 + "<rearWheelOffset>" + rearWheelOffset + "</rearWheelOffset>"
                 + "<elevationOffset>" + elevationOffset + "</elevationOffset>" 
                 //+ "<crumbData>" + crumbData + "</crumbData>" 
                 + "</tud>", JsonNode.class);
        } catch (XmlUtilsException e) {
           fail("XML parsing error:" + e);
        }
        
        
        Integer actualValue = TrailerUnitDescriptionBuilder.genericTrailerUnitDescription(testtud).getWidth();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test corner case minimum vehicle width value (2) returns (2)
     */
    @Test
    public void shouldReturnCornerCaseMinimumVehicleWidth() {
        

        Integer expectedValue = 2;
           
   
        Boolean isDolly = false;
        int width = 2;
        int length = 0;
        String frontPivot = "<pivotOffset>" + 0 + "</pivotOffset><pivotAngle>" + 0 + "</pivotAngle><pivots>" + true + "</pivots>";
        String positionOffset = "<x>" + 0 + "</x><y>" + 0 + "</y>";
        int height = 0;
        int mass = 0;
        String bumperHeights = "<front>" + 2 + "</front><rear>" + 2 + "</rear>";
        int centerOfGravity = 0;
        String rearPivot = "<pivotOffset>" + 0 + "</pivotOffset><pivotAngle>" + 0 + "</pivotAngle><pivots>" + true + "</pivots>";
        int rearWheelOffset = 0;
        int elevationOffset = 0;
        //String crumbData = "<pivotAngle>" + 0 + "</pivotAngle><timeOffset>" + 0 + "</timeOffset><positionOffset>" + "<x>" + 0 + "</x><y>" + 0 + "</y>" + "</positionOffset>";

        
        
        
        JsonNode testtud = null;
        try {
           testtud = (JsonNode) XmlUtils.fromXmlS("<tud>" 
                 + "<isDolly>" + isDolly + "</isDolly>"
                 + "<width>" + width + "</width>"
                 + "<length>" + length + "</length>"                 
                 + "<frontPivot>" + frontPivot + "</frontPivot>"
                 + "<positionOffset>" + positionOffset + "</positionOffset>"
                 + "<height>" + height + "</height>"
                 + "<mass>" + mass + "</mass>"
                 + "<bumperHeights>" + bumperHeights + "</bumperHeights>"
                 + "<centerOfGravity>" + centerOfGravity + "</centerOfGravity>"
                 + "<rearPivot>" + rearPivot + "</rearPivot>"
                 + "<rearWheelOffset>" + rearWheelOffset + "</rearWheelOffset>"
                 + "<elevationOffset>" + elevationOffset + "</elevationOffset>" 
                 //+ "<crumbData>" + crumbData + "</crumbData>" 
                 + "</tud>", JsonNode.class);
        } catch (XmlUtilsException e) {
           fail("XML parsing error:" + e);
        }
        
        
        
        Integer actualValue = TrailerUnitDescriptionBuilder.genericTrailerUnitDescription(testtud).getWidth();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test known, middle vehicle width value (437) returns (437)
     */
    @Test
    public void shouldReturnMiddleVehicleWidth() {
        

        Integer expectedValue = 437;
       
        
        Boolean isDolly = false;
        int width = 437;
        int length = 0;
        String frontPivot = "<pivotOffset>" + 0 + "</pivotOffset><pivotAngle>" + 0 + "</pivotAngle><pivots>" + true + "</pivots>";
        String positionOffset = "<x>" + 0 + "</x><y>" + 0 + "</y>";
        int height = 0;
        int mass = 0;
        String bumperHeights = "<front>" + 2 + "</front><rear>" + 2 + "</rear>";
        int centerOfGravity = 0;
        String rearPivot = "<pivotOffset>" + 0 + "</pivotOffset><pivotAngle>" + 0 + "</pivotAngle><pivots>" + true + "</pivots>";
        int rearWheelOffset = 0;
        int elevationOffset = 0;
        //String crumbData = "<pivotAngle>" + 0 + "</pivotAngle><timeOffset>" + 0 + "</timeOffset><positionOffset>" + "<x>" + 0 + "</x><y>" + 0 + "</y>" + "</positionOffset>";

        
        
        
        JsonNode testtud = null;
        try {
           testtud = (JsonNode) XmlUtils.fromXmlS("<tud>" 
                 + "<isDolly>" + isDolly + "</isDolly>"
                 + "<width>" + width + "</width>"
                 + "<length>" + length + "</length>"                 
                 + "<frontPivot>" + frontPivot + "</frontPivot>"
                 + "<positionOffset>" + positionOffset + "</positionOffset>"
                 + "<height>" + height + "</height>"
                 + "<mass>" + mass + "</mass>"
                 + "<bumperHeights>" + bumperHeights + "</bumperHeights>"
                 + "<centerOfGravity>" + centerOfGravity + "</centerOfGravity>"
                 + "<rearPivot>" + rearPivot + "</rearPivot>"
                 + "<rearWheelOffset>" + rearWheelOffset + "</rearWheelOffset>"
                 + "<elevationOffset>" + elevationOffset + "</elevationOffset>" 
                 //+ "<crumbData>" + crumbData + "</crumbData>" 
                 + "</tud>", JsonNode.class);
        } catch (XmlUtilsException e) {
           fail("XML parsing error:" + e);
        }
        
        
        Integer actualValue = TrailerUnitDescriptionBuilder.genericTrailerUnitDescription(testtud).getWidth();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test corner case maximum vehicle width value (1022) returns (1022)
     */
    @Test
    public void shouldReturnCornerCaseMaximumVehicleWidth() {
        
     
        Integer expectedValue = 1022;
        

        
        Boolean isDolly = false;
        int width = 1022;
        int length = 0;
        String frontPivot = "<pivotOffset>" + 0 + "</pivotOffset><pivotAngle>" + 0 + "</pivotAngle><pivots>" + true + "</pivots>";
        String positionOffset = "<x>" + 0 + "</x><y>" + 0 + "</y>";
        int height = 0;
        int mass = 0;
        String bumperHeights = "<front>" + 2 + "</front><rear>" + 2 + "</rear>";
        int centerOfGravity = 0;
        String rearPivot = "<pivotOffset>" + 0 + "</pivotOffset><pivotAngle>" + 0 + "</pivotAngle><pivots>" + true + "</pivots>";
        int rearWheelOffset = 0;
        int elevationOffset = 0;
        //String crumbData = "<TrailerHistoryPoint><pivotAngle>" + 0 + "</pivotAngle><timeOffset>" + 0 + "</timeOffset><positionOffset>" + "<x>" + 0 + "</x><y>" + 0 + "</y>" + "</positionOffset></TrailerHistoryPoint>";

        
        
        
        JsonNode testtud = null;
        try {
           testtud = (JsonNode) XmlUtils.fromXmlS("<tud>" 
                 + "<isDolly>" + isDolly + "</isDolly>"
                 + "<width>" + width + "</width>"
                 + "<length>" + length + "</length>"                 
                 + "<frontPivot>" + frontPivot + "</frontPivot>"
                 + "<positionOffset>" + positionOffset + "</positionOffset>"
                 + "<height>" + height + "</height>"
                 + "<mass>" + mass + "</mass>"
                 + "<bumperHeights>" + bumperHeights + "</bumperHeights>"
                 + "<centerOfGravity>" + centerOfGravity + "</centerOfGravity>"
                 + "<rearPivot>" + rearPivot + "</rearPivot>"
                 + "<rearWheelOffset>" + rearWheelOffset + "</rearWheelOffset>"
                 + "<elevationOffset>" + elevationOffset + "</elevationOffset>" 
                 //+ "<crumbData>" + crumbData + "</crumbData>" 
                 + "</tud>", JsonNode.class);
        } catch (XmlUtilsException e) {
           fail("XML parsing error:" + e);
        }
        
        
        Integer actualValue = TrailerUnitDescriptionBuilder.genericTrailerUnitDescription(testtud).getWidth();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test maximum vehicle width value (1023) returns (1023)
     */
    @Test
    public void shouldReturnMaximumVehicleWidth() {
        
      
        Integer expectedValue = 1023;
        

        
        
        Boolean isDolly = false;
        int width = 1023;
        int length = 0;
        String frontPivot = "<pivotOffset>" + 0 + "</pivotOffset><pivotAngle>" + 0 + "</pivotAngle><pivots>" + true + "</pivots>";
        String positionOffset = "<x>" + 0 + "</x><y>" + 0 + "</y>";
        int height = 0;
        int mass = 0;
        String bumperHeights = "<front>" + 2 + "</front><rear>" + 2 + "</rear>";
        int centerOfGravity = 0;
        String rearPivot = "<pivotOffset>" + 0 + "</pivotOffset><pivotAngle>" + 0 + "</pivotAngle><pivots>" + true + "</pivots>";
        int rearWheelOffset = 0;
        int elevationOffset = 0;
        //String crumbData = "<TrailerHistoryPoint><pivotAngle>" + 0 + "</pivotAngle><timeOffset>" + 0 + "</timeOffset><positionOffset>" + "<x>" + 0 + "</x><y>" + 0 + "</y>" + "</positionOffset></TrailerHistoryPoint>";

        
        
        
        JsonNode testtud = null;
        try {
           testtud = (JsonNode) XmlUtils.fromXmlS("<tud>" 
                 + "<isDolly>" + isDolly + "</isDolly>"
                 + "<width>" + width + "</width>"
                 + "<length>" + length + "</length>"                 
                 + "<frontPivot>" + frontPivot + "</frontPivot>"
                 + "<positionOffset>" + positionOffset + "</positionOffset>"
                 + "<height>" + height + "</height>"
                 + "<mass>" + mass + "</mass>"
                 + "<bumperHeights>" + bumperHeights + "</bumperHeights>"
                 + "<centerOfGravity>" + centerOfGravity + "</centerOfGravity>"
                 + "<rearPivot>" + rearPivot + "</rearPivot>"
                 + "<rearWheelOffset>" + rearWheelOffset + "</rearWheelOffset>"
                 + "<elevationOffset>" + elevationOffset + "</elevationOffset>" 
                 //+ "<crumbData>" + crumbData + "</crumbData>" 
                 + "</tud>", JsonNode.class);
        } catch (XmlUtilsException e) {
           fail("XML parsing error:" + e);
        }
        Integer actualValue = TrailerUnitDescriptionBuilder.genericTrailerUnitDescription(testtud).getWidth();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test vehicle width value (-1) below lower bound (0) throws IllegalArgumentException
     */
    @Test
    public void shouldThrowExceptionVehicleWidthBelowLowerBound() {
        
       
        
        Boolean isDolly = false;
        int width = -1;
        int length = 0;
        String frontPivot = "<pivotOffset>" + 0 + "</pivotOffset><pivotAngle>" + 0 + "</pivotAngle><pivots>" + true + "</pivots>";
        String positionOffset = "<x>" + 0 + "</x><y>" + 0 + "</y>";
        int height = 0;
        int mass = 0;
        String bumperHeights = "<front>" + 2 + "</front><rear>" + 2 + "</rear>";
        int centerOfGravity = 0;
        String rearPivot = "<pivotOffset>" + 0 + "</pivotOffset><pivotAngle>" + 0 + "</pivotAngle><pivots>" + true + "</pivots>";
        int rearWheelOffset = 0;
        int elevationOffset = 0;
        //String crumbData = "<TrailerHistoryPoint><pivotAngle>" + 0 + "</pivotAngle><timeOffset>" + 0 + "</timeOffset><positionOffset>" + "<x>" + 0 + "</x><y>" + 0 + "</y>" + "</positionOffset></TrailerHistoryPoint>";

        
        
        
        JsonNode testtud = null;
        try {
           testtud = (JsonNode) XmlUtils.fromXmlS("<tud>" 
                 + "<isDolly>" + isDolly + "</isDolly>"
                 + "<width>" + width + "</width>"
                 + "<length>" + length + "</length>"                 
                 + "<frontPivot>" + frontPivot + "</frontPivot>"
                 + "<positionOffset>" + positionOffset + "</positionOffset>"
                 + "<height>" + height + "</height>"
                 + "<mass>" + mass + "</mass>"
                 + "<bumperHeights>" + bumperHeights + "</bumperHeights>"
                 + "<centerOfGravity>" + centerOfGravity + "</centerOfGravity>"
                 + "<rearPivot>" + rearPivot + "</rearPivot>"
                 + "<rearWheelOffset>" + rearWheelOffset + "</rearWheelOffset>"
                 + "<elevationOffset>" + elevationOffset + "</elevationOffset>" 
                 //+ "<crumbData>" + crumbData + "</crumbData>" 
                 + "</tud>", JsonNode.class);
        } catch (XmlUtilsException e) {
           fail("XML parsing error:" + e);
        }
        
        try {
           TrailerUnitDescriptionBuilder.genericTrailerUnitDescription(testtud).getWidth();
           fail("Expected IllegalArgumentException");
        } catch (RuntimeException e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }
    }
    
    /**
     * Test vehicle width value (1024) above upper bound (1023) throws IllegalArgumentException
     */
    @Test
    public void shouldThrowExceptionVehicleWidthAboveUpperBound() {
        

        
        
        Boolean isDolly = false;
        int width = 1024;
        int length = 0;
        String frontPivot = "<pivotOffset>" + 0 + "</pivotOffset><pivotAngle>" + 0 + "</pivotAngle><pivots>" + true + "</pivots>";
        String positionOffset = "<x>" + 0 + "</x><y>" + 0 + "</y>";
        int height = 0;
        int mass = 0;
        String bumperHeights = "<front>" + 2 + "</front><rear>" + 2 + "</rear>";
        int centerOfGravity = 0;
        String rearPivot = "<pivotOffset>" + 0 + "</pivotOffset><pivotAngle>" + 0 + "</pivotAngle><pivots>" + true + "</pivots>";
        int rearWheelOffset = 0;
        int elevationOffset = 0;
        //String crumbData = "<TrailerHistoryPoint><pivotAngle>" + 0 + "</pivotAngle><timeOffset>" + 0 + "</timeOffset><positionOffset>" + "<x>" + 0 + "</x><y>" + 0 + "</y>" + "</positionOffset></TrailerHistoryPoint>";

        
        
        
        JsonNode testtud = null;
        try {
           testtud = (JsonNode) XmlUtils.fromXmlS("<tud>" 
                 + "<isDolly>" + isDolly + "</isDolly>"
                 + "<width>" + width + "</width>"
                 + "<length>" + length + "</length>"                 
                 + "<frontPivot>" + frontPivot + "</frontPivot>"
                 + "<positionOffset>" + positionOffset + "</positionOffset>"
                 + "<height>" + height + "</height>"
                 + "<mass>" + mass + "</mass>"
                 + "<bumperHeights>" + bumperHeights + "</bumperHeights>"
                 + "<centerOfGravity>" + centerOfGravity + "</centerOfGravity>"
                 + "<rearPivot>" + rearPivot + "</rearPivot>"
                 + "<rearWheelOffset>" + rearWheelOffset + "</rearWheelOffset>"
                 + "<elevationOffset>" + elevationOffset + "</elevationOffset>" 
                 //+ "<crumbData>" + crumbData + "</crumbData>" 
                 + "</tud>", JsonNode.class);
        } catch (XmlUtilsException e) {
           fail("XML parsing error:" + e);
        }
        
        try {
           TrailerUnitDescriptionBuilder.genericTrailerUnitDescription(testtud).getWidth();
           fail("Expected IllegalArgumentException");
        } catch (RuntimeException e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }
    }
    
    // length tests
    /**
     * Test vehicle length undefined flag value (0) returns (null)
     */
    @Test
    public void shouldReturnUndefinedVehicleLength() {
        
        Integer testInput = 0;
        Integer expectedValue = null;
        

        
        
        Boolean isDolly = false;
        int width = 0;
        int length = testInput;
        String frontPivot = "<pivotOffset>" + 0 + "</pivotOffset><pivotAngle>" + 0 + "</pivotAngle><pivots>" + true + "</pivots>";
        String positionOffset = "<x>" + 0 + "</x><y>" + 0 + "</y>";
        int height = 0;
        int mass = 0;
        String bumperHeights = "<front>" + 2 + "</front><rear>" + 2 + "</rear>";
        int centerOfGravity = 0;
        String rearPivot = "<pivotOffset>" + 0 + "</pivotOffset><pivotAngle>" + 0 + "</pivotAngle><pivots>" + true + "</pivots>";
        int rearWheelOffset = 0;
        int elevationOffset = 0;
        //String crumbData = "<TrailerHistoryPoint><pivotAngle>" + 0 + "</pivotAngle><timeOffset>" + 0 + "</timeOffset><positionOffset>" + "<x>" + 0 + "</x><y>" + 0 + "</y>" + "</positionOffset></TrailerHistoryPoint>";

        
        
        
        JsonNode testtud = null;
        try {
           testtud = (JsonNode) XmlUtils.fromXmlS("<tud>" 
                 + "<isDolly>" + isDolly + "</isDolly>"
                 + "<width>" + width + "</width>"
                 + "<length>" + length + "</length>"                 
                 + "<frontPivot>" + frontPivot + "</frontPivot>"
                 + "<positionOffset>" + positionOffset + "</positionOffset>"
                 + "<height>" + height + "</height>"
                 + "<mass>" + mass + "</mass>"
                 + "<bumperHeights>" + bumperHeights + "</bumperHeights>"
                 + "<centerOfGravity>" + centerOfGravity + "</centerOfGravity>"
                 + "<rearPivot>" + rearPivot + "</rearPivot>"
                 + "<rearWheelOffset>" + rearWheelOffset + "</rearWheelOffset>"
                 + "<elevationOffset>" + elevationOffset + "</elevationOffset>" 
                 //+ "<crumbData>" + crumbData + "</crumbData>" 
                 + "</tud>", JsonNode.class);
        } catch (XmlUtilsException e) {
           fail("XML parsing error:" + e);
        }
        
        
        Integer actualValue = TrailerUnitDescriptionBuilder.genericTrailerUnitDescription(testtud).getLength();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test minimum vehicle length value (1) returns (1)
     */
    @Test
    public void shouldReturnMinimumVehicleLength() {
        
        Integer testInput = 1;
        Integer expectedValue = 1;
        
        Boolean isDolly = false;
        int width = 0;
        int length = testInput;
        String frontPivot = "<pivotOffset>" + 0 + "</pivotOffset><pivotAngle>" + 0 + "</pivotAngle><pivots>" + true + "</pivots>";
        String positionOffset = "<x>" + 0 + "</x><y>" + 0 + "</y>";
        int height = 0;
        int mass = 0;
        String bumperHeights = "<front>" + 2 + "</front><rear>" + 2 + "</rear>";
        int centerOfGravity = 0;
        String rearPivot = "<pivotOffset>" + 0 + "</pivotOffset><pivotAngle>" + 0 + "</pivotAngle><pivots>" + true + "</pivots>";
        int rearWheelOffset = 0;
        int elevationOffset = 0;
        //String crumbData = "<TrailerHistoryPoint><pivotAngle>" + 0 + "</pivotAngle><timeOffset>" + 0 + "</timeOffset><positionOffset>" + "<x>" + 0 + "</x><y>" + 0 + "</y>" + "</positionOffset></TrailerHistoryPoint>";

        
        
        
        JsonNode testtud = null;
        try {
           testtud = (JsonNode) XmlUtils.fromXmlS("<tud>" 
                 + "<isDolly>" + isDolly + "</isDolly>"
                 + "<width>" + width + "</width>"
                 + "<length>" + length + "</length>"                 
                 + "<frontPivot>" + frontPivot + "</frontPivot>"
                 + "<positionOffset>" + positionOffset + "</positionOffset>"
                 + "<height>" + height + "</height>"
                 + "<mass>" + mass + "</mass>"
                 + "<bumperHeights>" + bumperHeights + "</bumperHeights>"
                 + "<centerOfGravity>" + centerOfGravity + "</centerOfGravity>"
                 + "<rearPivot>" + rearPivot + "</rearPivot>"
                 + "<rearWheelOffset>" + rearWheelOffset + "</rearWheelOffset>"
                 + "<elevationOffset>" + elevationOffset + "</elevationOffset>" 
                 //+ "<crumbData>" + crumbData + "</crumbData>" 
                 + "</tud>", JsonNode.class);
        } catch (XmlUtilsException e) {
           fail("XML parsing error:" + e);
        }
        Integer actualValue = TrailerUnitDescriptionBuilder.genericTrailerUnitDescription(testtud).getLength();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test corner case minimum vehicle length value (2) returns (2)
     */
    @Test
    public void shouldReturnCornerCaseMinimumVehicleLength() {
        
        Integer testInput = 2;
        Integer expectedValue = 2;
        
        Boolean isDolly = false;
        int width = 0;
        int length = testInput;
        String frontPivot = "<pivotOffset>" + 0 + "</pivotOffset><pivotAngle>" + 0 + "</pivotAngle><pivots>" + true + "</pivots>";
        String positionOffset = "<x>" + 0 + "</x><y>" + 0 + "</y>";
        int height = 0;
        int mass = 0;
        String bumperHeights = "<front>" + 2 + "</front><rear>" + 2 + "</rear>";
        int centerOfGravity = 0;
        String rearPivot = "<pivotOffset>" + 0 + "</pivotOffset><pivotAngle>" + 0 + "</pivotAngle><pivots>" + true + "</pivots>";
        int rearWheelOffset = 0;
        int elevationOffset = 0;
        //String crumbData = "<TrailerHistoryPoint><pivotAngle>" + 0 + "</pivotAngle><timeOffset>" + 0 + "</timeOffset><positionOffset>" + "<x>" + 0 + "</x><y>" + 0 + "</y>" + "</positionOffset></TrailerHistoryPoint>";

        
        
        
        JsonNode testtud = null;
        try {
           testtud = (JsonNode) XmlUtils.fromXmlS("<tud>" 
                 + "<isDolly>" + isDolly + "</isDolly>"
                 + "<width>" + width + "</width>"
                 + "<length>" + length + "</length>"                 
                 + "<frontPivot>" + frontPivot + "</frontPivot>"
                 + "<positionOffset>" + positionOffset + "</positionOffset>"
                 + "<height>" + height + "</height>"
                 + "<mass>" + mass + "</mass>"
                 + "<bumperHeights>" + bumperHeights + "</bumperHeights>"
                 + "<centerOfGravity>" + centerOfGravity + "</centerOfGravity>"
                 + "<rearPivot>" + rearPivot + "</rearPivot>"
                 + "<rearWheelOffset>" + rearWheelOffset + "</rearWheelOffset>"
                 + "<elevationOffset>" + elevationOffset + "</elevationOffset>" 
                 //+ "<crumbData>" + crumbData + "</crumbData>" 
                 + "</tud>", JsonNode.class);
        } catch (XmlUtilsException e) {
           fail("XML parsing error:" + e);
        }
        
        Integer actualValue = TrailerUnitDescriptionBuilder.genericTrailerUnitDescription(testtud).getLength();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test known, middle vehicle length value (2213) returns (2213)
     */
    @Test
    public void shouldReturnMiddleVehicleLength() {
        
        Integer testInput = 2213;
        Integer expectedValue = 2213;
        
        Boolean isDolly = false;
        int width = 0;
        int length = testInput;
        String frontPivot = "<pivotOffset>" + 0 + "</pivotOffset><pivotAngle>" + 0 + "</pivotAngle><pivots>" + true + "</pivots>";
        String positionOffset = "<x>" + 0 + "</x><y>" + 0 + "</y>";
        int height = 0;
        int mass = 0;
        String bumperHeights = "<front>" + 2 + "</front><rear>" + 2 + "</rear>";
        int centerOfGravity = 0;
        String rearPivot = "<pivotOffset>" + 0 + "</pivotOffset><pivotAngle>" + 0 + "</pivotAngle><pivots>" + true + "</pivots>";
        int rearWheelOffset = 0;
        int elevationOffset = 0;
        //String crumbData = "<TrailerHistoryPoint><pivotAngle>" + 0 + "</pivotAngle><timeOffset>" + 0 + "</timeOffset><positionOffset>" + "<x>" + 0 + "</x><y>" + 0 + "</y>" + "</positionOffset></TrailerHistoryPoint>";

        
        
        
        JsonNode testtud = null;
        try {
           testtud = (JsonNode) XmlUtils.fromXmlS("<tud>" 
                 + "<isDolly>" + isDolly + "</isDolly>"
                 + "<width>" + width + "</width>"
                 + "<length>" + length + "</length>"                 
                 + "<frontPivot>" + frontPivot + "</frontPivot>"
                 + "<positionOffset>" + positionOffset + "</positionOffset>"
                 + "<height>" + height + "</height>"
                 + "<mass>" + mass + "</mass>"
                 + "<bumperHeights>" + bumperHeights + "</bumperHeights>"
                 + "<centerOfGravity>" + centerOfGravity + "</centerOfGravity>"
                 + "<rearPivot>" + rearPivot + "</rearPivot>"
                 + "<rearWheelOffset>" + rearWheelOffset + "</rearWheelOffset>"
                 + "<elevationOffset>" + elevationOffset + "</elevationOffset>" 
                 //+ "<crumbData>" + crumbData + "</crumbData>" 
                 + "</tud>", JsonNode.class);
        } catch (XmlUtilsException e) {
           fail("XML parsing error:" + e);
        }
        
        Integer actualValue = TrailerUnitDescriptionBuilder.genericTrailerUnitDescription(testtud).getLength();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test corner case maximum vehicle length value (4094) returns (4094)
     */
    @Test
    public void shouldReturnCornerCaseMaximumVehicleLength() {
        
        Integer testInput = 4094;
        Integer expectedValue = 4094;
        
        Boolean isDolly = false;
        int width = 0;
        int length = testInput;
        String frontPivot = "<pivotOffset>" + 0 + "</pivotOffset><pivotAngle>" + 0 + "</pivotAngle><pivots>" + true + "</pivots>";
        String positionOffset = "<x>" + 0 + "</x><y>" + 0 + "</y>";
        int height = 0;
        int mass = 0;
        String bumperHeights = "<front>" + 2 + "</front><rear>" + 2 + "</rear>";
        int centerOfGravity = 0;
        String rearPivot = "<pivotOffset>" + 0 + "</pivotOffset><pivotAngle>" + 0 + "</pivotAngle><pivots>" + true + "</pivots>";
        int rearWheelOffset = 0;
        int elevationOffset = 0;
        //String crumbData = "<TrailerHistoryPoint><pivotAngle>" + 0 + "</pivotAngle><timeOffset>" + 0 + "</timeOffset><positionOffset>" + "<x>" + 0 + "</x><y>" + 0 + "</y>" + "</positionOffset></TrailerHistoryPoint>";

        
        
        
        JsonNode testtud = null;
        try {
           testtud = (JsonNode) XmlUtils.fromXmlS("<tud>" 
                 + "<isDolly>" + isDolly + "</isDolly>"
                 + "<width>" + width + "</width>"
                 + "<length>" + length + "</length>"                 
                 + "<frontPivot>" + frontPivot + "</frontPivot>"
                 + "<positionOffset>" + positionOffset + "</positionOffset>"
                 + "<height>" + height + "</height>"
                 + "<mass>" + mass + "</mass>"
                 + "<bumperHeights>" + bumperHeights + "</bumperHeights>"
                 + "<centerOfGravity>" + centerOfGravity + "</centerOfGravity>"
                 + "<rearPivot>" + rearPivot + "</rearPivot>"
                 + "<rearWheelOffset>" + rearWheelOffset + "</rearWheelOffset>"
                 + "<elevationOffset>" + elevationOffset + "</elevationOffset>" 
                 //+ "<crumbData>" + crumbData + "</crumbData>" 
                 + "</tud>", JsonNode.class);
        } catch (XmlUtilsException e) {
           fail("XML parsing error:" + e);
        }
        
        Integer actualValue = TrailerUnitDescriptionBuilder.genericTrailerUnitDescription(testtud).getLength();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test maximum vehicle length value (4095) returns (4095)
     */
    @Test
    public void shouldReturnMaximumVehicleLength() {
        
        Integer testInput = 4095;
        Integer expectedValue = 4095;
        
        Boolean isDolly = false;
        int width = 0;
        int length = testInput;
        String frontPivot = "<pivotOffset>" + 0 + "</pivotOffset><pivotAngle>" + 0 + "</pivotAngle><pivots>" + true + "</pivots>";
        String positionOffset = "<x>" + 0 + "</x><y>" + 0 + "</y>";
        int height = 0;
        int mass = 0;
        String bumperHeights = "<front>" + 2 + "</front><rear>" + 2 + "</rear>";
        int centerOfGravity = 0;
        String rearPivot = "<pivotOffset>" + 0 + "</pivotOffset><pivotAngle>" + 0 + "</pivotAngle><pivots>" + true + "</pivots>";
        int rearWheelOffset = 0;
        int elevationOffset = 0;
        //String crumbData = "<TrailerHistoryPoint><pivotAngle>" + 0 + "</pivotAngle><timeOffset>" + 0 + "</timeOffset><positionOffset>" + "<x>" + 0 + "</x><y>" + 0 + "</y>" + "</positionOffset></TrailerHistoryPoint>";

        
        
        
        JsonNode testtud = null;
        try {
           testtud = (JsonNode) XmlUtils.fromXmlS("<tud>" 
                 + "<isDolly>" + isDolly + "</isDolly>"
                 + "<width>" + width + "</width>"
                 + "<length>" + length + "</length>"                 
                 + "<frontPivot>" + frontPivot + "</frontPivot>"
                 + "<positionOffset>" + positionOffset + "</positionOffset>"
                 + "<height>" + height + "</height>"
                 + "<mass>" + mass + "</mass>"
                 + "<bumperHeights>" + bumperHeights + "</bumperHeights>"
                 + "<centerOfGravity>" + centerOfGravity + "</centerOfGravity>"
                 + "<rearPivot>" + rearPivot + "</rearPivot>"
                 + "<rearWheelOffset>" + rearWheelOffset + "</rearWheelOffset>"
                 + "<elevationOffset>" + elevationOffset + "</elevationOffset>" 
                 //+ "<crumbData>" + crumbData + "</crumbData>" 
                 + "</tud>", JsonNode.class);
        } catch (XmlUtilsException e) {
           fail("XML parsing error:" + e);
        }
        
        Integer actualValue = TrailerUnitDescriptionBuilder.genericTrailerUnitDescription(testtud).getLength();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test vehicle length value (-1) below lower bound (0) throws IllegalArgumentException
     */
    @Test
    public void shouldThrowExceptionVehicleLengthBelowLowerBound() {
        
        Integer testInput = -1;
        
        Boolean isDolly = false;
        int width = 0;
        int length = testInput;
        String frontPivot = "<pivotOffset>" + 0 + "</pivotOffset><pivotAngle>" + 0 + "</pivotAngle><pivots>" + true + "</pivots>";
        String positionOffset = "<x>" + 0 + "</x><y>" + 0 + "</y>";
        int height = 0;
        int mass = 0;
        String bumperHeights = "<front>" + 2 + "</front><rear>" + 2 + "</rear>";
        int centerOfGravity = 0;
        String rearPivot = "<pivotOffset>" + 0 + "</pivotOffset><pivotAngle>" + 0 + "</pivotAngle><pivots>" + true + "</pivots>";
        int rearWheelOffset = 0;
        int elevationOffset = 0;
        //String crumbData = "<TrailerHistoryPoint><pivotAngle>" + 0 + "</pivotAngle><timeOffset>" + 0 + "</timeOffset><positionOffset>" + "<x>" + 0 + "</x><y>" + 0 + "</y>" + "</positionOffset></TrailerHistoryPoint>";

        
        
        
        JsonNode testtud = null;
        try {
           testtud = (JsonNode) XmlUtils.fromXmlS("<tud>" 
                 + "<isDolly>" + isDolly + "</isDolly>"
                 + "<width>" + width + "</width>"
                 + "<length>" + length + "</length>"                 
                 + "<frontPivot>" + frontPivot + "</frontPivot>"
                 + "<positionOffset>" + positionOffset + "</positionOffset>"
                 + "<height>" + height + "</height>"
                 + "<mass>" + mass + "</mass>"
                 + "<bumperHeights>" + bumperHeights + "</bumperHeights>"
                 + "<centerOfGravity>" + centerOfGravity + "</centerOfGravity>"
                 + "<rearPivot>" + rearPivot + "</rearPivot>"
                 + "<rearWheelOffset>" + rearWheelOffset + "</rearWheelOffset>"
                 + "<elevationOffset>" + elevationOffset + "</elevationOffset>" 
                 //+ "<crumbData>" + crumbData + "</crumbData>" 
                 + "</tud>", JsonNode.class);
        } catch (XmlUtilsException e) {
           fail("XML parsing error:" + e);
        }
        
        try {
            TrailerUnitDescriptionBuilder.genericTrailerUnitDescription(testtud).getLength();
            fail("Expected IllegalArgumentException");
        } catch (RuntimeException e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }
    }
    
    /**
     * Test vehicle length value (4096) above upper bound (4095) throws IllegalArgumentException
     */
    @Test
    public void shouldThrowExceptionVehicleLengthAboveUpperBound() {
        
        Integer testInput = 4096;
        
        Boolean isDolly = false;
        int width = 0;
        int length = testInput;
        String frontPivot = "<pivotOffset>" + 0 + "</pivotOffset><pivotAngle>" + 0 + "</pivotAngle><pivots>" + true + "</pivots>";
        String positionOffset = "<x>" + 0 + "</x><y>" + 0 + "</y>";
        int height = 0;
        int mass = 0;
        String bumperHeights = "<front>" + 2 + "</front><rear>" + 2 + "</rear>";
        int centerOfGravity = 0;
        String rearPivot = "<pivotOffset>" + 0 + "</pivotOffset><pivotAngle>" + 0 + "</pivotAngle><pivots>" + true + "</pivots>";
        int rearWheelOffset = 0;
        int elevationOffset = 0;
        //String crumbData = "<TrailerHistoryPoint><pivotAngle>" + 0 + "</pivotAngle><timeOffset>" + 0 + "</timeOffset><positionOffset>" + "<x>" + 0 + "</x><y>" + 0 + "</y>" + "</positionOffset></TrailerHistoryPoint>";

        
        
        
        JsonNode testtud = null;
        try {
           testtud = (JsonNode) XmlUtils.fromXmlS("<tud>" 
                 + "<isDolly>" + isDolly + "</isDolly>"
                 + "<width>" + width + "</width>"
                 + "<length>" + length + "</length>"                 
                 + "<frontPivot>" + frontPivot + "</frontPivot>"
                 + "<positionOffset>" + positionOffset + "</positionOffset>"
                 + "<height>" + height + "</height>"
                 + "<mass>" + mass + "</mass>"
                 + "<bumperHeights>" + bumperHeights + "</bumperHeights>"
                 + "<centerOfGravity>" + centerOfGravity + "</centerOfGravity>"
                 + "<rearPivot>" + rearPivot + "</rearPivot>"
                 + "<rearWheelOffset>" + rearWheelOffset + "</rearWheelOffset>"
                 + "<elevationOffset>" + elevationOffset + "</elevationOffset>" 
                 //+ "<crumbData>" + crumbData + "</crumbData>" 
                 + "</tud>", JsonNode.class);
        } catch (XmlUtilsException e) {
           fail("XML parsing error:" + e);
        }
        try {
            TrailerUnitDescriptionBuilder.genericTrailerUnitDescription(testtud).getLength();
            fail("Expected IllegalArgumentException");
        } catch (RuntimeException e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }
    }
    
    /**
     * Test corner case minimum vehicle width value (2) returns (2)
     */
    @Test
    public void shouldTestNulls() {
        

        Integer expectedValue = 2;
           
   
        Boolean isDolly = false;
        int width = 2;
        int length = 0;
        String frontPivot = "<pivotOffset>" + 0 + "</pivotOffset><pivotAngle>" + 0 + "</pivotAngle><pivots>" + true + "</pivots>";
        String positionOffset = "<x>" + 0 + "</x><y>" + 0 + "</y>";
       
        
        JsonNode testtud = null;
        try {
           testtud = (JsonNode) XmlUtils.fromXmlS("<tud>" 
                 + "<isDolly>" + isDolly + "</isDolly>"
                 + "<width>" + width + "</width>"
                 + "<length>" + length + "</length>"                 
                 + "<frontPivot>" + frontPivot + "</frontPivot>"
                 + "<positionOffset>" + positionOffset + "</positionOffset>"
                 + "</tud>", JsonNode.class);
        } catch (XmlUtilsException e) {
           fail("XML parsing error:" + e);
        }
        
        
        
        Integer actualValue = TrailerUnitDescriptionBuilder.genericTrailerUnitDescription(testtud).getWidth();
        
        assertEquals(expectedValue, actualValue);
    }
    
    // crumbData tests
    /**
     * Test that a mock crumb data object can be added to a trailer unit description object
     * and that its contents can be correctly extracted
     */
    @Test
    public void shouldCreateMockCrumbData() {
        Boolean isDolly = false;
        int width = 0;
        int length = 0;
        String frontPivot = "<pivotOffset>" + 0 + "</pivotOffset><pivotAngle>" + 0 + "</pivotAngle><pivots>" + true + "</pivots>";
        String positionOffset = "<x>" + 0 + "</x><y>" + 0 + "</y>";
        int height = 0;
        int mass = 0;
        String bumperHeights = "<front>" + 2 + "</front><rear>" + 2 + "</rear>";
        int centerOfGravity = 0;
        String rearPivot = "<pivotOffset>" + 0 + "</pivotOffset><pivotAngle>" + 0 + "</pivotAngle><pivots>" + true + "</pivots>";
        int rearWheelOffset = 0;
        int elevationOffset = 0;
        String crumbData = "<thp><pivotAngle>" + 0 + "</pivotAngle><timeOffset>"
              + 1 + "</timeOffset><positionOffset>" + positionOffset + "</positionOffset><elevationOffset>" + 0
              + "</elevationOffset><heading>" + 15 + "</heading></thp>";

        
        
        
        JsonNode testtud = null;
        try {
           testtud = (JsonNode) XmlUtils.fromXmlS("<tud>" 
                 + "<isDolly>" + isDolly + "</isDolly>"
                 + "<width>" + width + "</width>"
                 + "<length>" + length + "</length>"                 
                 + "<frontPivot>" + frontPivot + "</frontPivot>"
                 + "<positionOffset>" + positionOffset + "</positionOffset>"
                 + "<height>" + height + "</height>"
                 + "<mass>" + mass + "</mass>"
                 + "<bumperHeights>" + bumperHeights + "</bumperHeights>"
                 + "<centerOfGravity>" + centerOfGravity + "</centerOfGravity>"
                 + "<rearPivot>" + rearPivot + "</rearPivot>"
                 + "<rearWheelOffset>" + rearWheelOffset + "</rearWheelOffset>"
                 + "<elevationOffset>" + elevationOffset + "</elevationOffset>" 
                 + "<crumbData>" + crumbData + "</crumbData>" 
                 + "</tud>", JsonNode.class);
        } catch (XmlUtilsException e) {
           fail("XML parsing error:" + e);
        }
        
        // Perform conversion
        J2735TrailerUnitDescription actualtud = TrailerUnitDescriptionBuilder.genericTrailerUnitDescription(testtud);
        
        assertEquals(1, actualtud.getCrumbData().size());
        //assertEquals(expectedHeading, actualtud.getCrumbData().get(0).getHeading());
    }

    @Test
    public void testConstructorIsPrivate() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
      Constructor<TrailerUnitDescriptionBuilder> constructor = TrailerUnitDescriptionBuilder.class.getDeclaredConstructor();
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
