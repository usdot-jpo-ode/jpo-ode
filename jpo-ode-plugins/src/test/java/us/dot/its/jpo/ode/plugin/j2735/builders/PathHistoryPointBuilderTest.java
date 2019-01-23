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
import java.math.BigDecimal;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.util.XmlUtils;
import us.dot.its.jpo.ode.util.XmlUtils.XmlUtilsException;

/**
 * -- Summary -- JUnit test class for PathHistoryPointBuilder
 * 
 * Verifies correct conversion from generic PathHistoryPoint to
 * compliant-J2735PathHistoryPoint
 * 
 * Notes: - Independent variables are set to 0 - Time offset set to 1 when
 * independent - Positional accuracy tests handled by OssPositionalAccuracyTest
 * 
 * -- Documentation -- Data Frame: DF_PathHistoryPoint Use: The PathHistoryPoint
 * data frame is used to convey a single point in the path of an object
 * (typically a motor vehicle) described as a sequence of such position points.
 * The sequence and number of these points (defined in another data frame) is
 * selected to convey the desired level of accuracy and precision required by
 * the application. The lat-long offset units used in the PathHistoryPointType
 * data frame support units of 1/10th micro degrees of lat and long. The
 * elevation offset units are in 10cm units. The time is expressed in units of
 * 10 milliseconds. The PositionalAccuracy entry uses 3 elements to relate the
 * pseudorange noise measured in the system. The heading and speed are not
 * offset values, and follow the units defined in the ASN comments. All of these
 * items are defined further in the relevant data entries. ASN.1 Representation:
 * PathHistoryPoint ::= SEQUENCE { latOffset OffsetLL-B18, lonOffset
 * OffsetLL-B18, elevationOffset VertOffset-B12, timeOffset TimeOffset, --
 * Offset backwards in time speed Speed OPTIONAL, -- Speed over the reported
 * period posAccuracy PositionalAccuracy OPTIONAL, -- The accuracy of this value
 * heading CoarseHeading OPTIONAL, -- overall heading ... }
 * 
 * Data Element: DE_OffsetLL-B18 Use: An 18-bit delta offset in Lat or Long
 * direction from the last point. The offset is positive to the East and to the
 * North directions. In LSB units of 0.1 microdegrees (unless a zoom is
 * employed). The most negative value shall be used to indicate an unknown
 * value. It should be noted that while the precise range of the data element in
 * degrees is a constant value, the equivalent length in meters will vary with
 * the position on the earth that is used. The above methodology is used when
 * the offset is incorporated in data frames other than DF_PathHistoryPoint.
 * Refer to the Use paragraph of DF_PathHistory for the methodology to calculate
 * this data element for use in DF_PathHistoryPoint. ASN.1 Representation:
 * OffsetLL-B18 ::= INTEGER (-131072..131071) -- A range of +- 0.0131071 degrees
 * -- The value +131071 shall be used for values >= than +0.0131071 degrees --
 * The value -131071 shall be used for values <= than -0.0131071 degrees -- The
 * value -131072 shall be used unknown -- In LSB units of 0.1 microdegrees
 * (unless a zoom is employed)
 * 
 * Data Element: DE_VertOffset-B12 Use: A 12-bit vertical delta offset in the Z
 * direction from the last point. The most negative value shall be used to
 * indicate an unknown value. Unlike similar horizontal offsets, the LSB used is
 * 10 centimeters (not one centimeter). The above methodology is used when the
 * offset is incorporated in data frames other than DF_PathHistoryPoint. Refer
 * to the Use paragraph of DF_PathHistory for the methodology to calculate this
 * data element for use in DF_PathHistoryPoint. ASN.1 Representation:
 * VertOffset-B12 ::= INTEGER (-2048..2047) -- LSB units of of 10 cm -- with a
 * range of +- 204.7 meters vertical -- value 2047 to be used for 2047 or
 * greater -- value -2047 to be used for -2047 or greater -- value -2048 to be
 * unavailable
 * 
 * Data Element: DE_TimeOffset Use: The DE_TimeOffset data element is used to
 * convey an offset in time from a known point. It is typically used to relate a
 * set of measurements made in the recent past, such as a set of path points.
 * The above methodology is used when the offset is incorporated in data frames
 * other than DF_PathHistoryPoint. Refer to the Use paragraph of DF_PathHistory
 * for the methodology to calculate this data element for use in
 * DF_PathHistoryPoint. ASN.1 Representation: TimeOffset ::= INTEGER (1..65535)
 * -- LSB units of of 10 mSec, -- with a range of 0.01 seconds to 10 minutes and
 * 55.34 seconds -- a value of 65534 to be used for 655.34 seconds or greater --
 * a value of 65535 to be unavailable
 * 
 * Data Element: DE_CoarseHeading Use: The DE_CoarseHeading data element is used
 * to provide a coarser sense of heading than the DE_Heading provides. ASN.1
 * Representation: CoarseHeading ::= INTEGER (0..240) -- Where the LSB is in
 * units of 1.5 degrees -- over a range of 0~358.5 degrees -- the value 240
 * shall be used for unavailable
 */
public class PathHistoryPointBuilderTest {

   // latOffset tests
   /**
    * Test that the undefined flag lat offset value (-131072) returns (null)
    */
   @Test
   public void shouldCreateUndefinedLatOffset() {

      Integer testInput = -131072;
      BigDecimal expectedValue = null;

      long testLatOffset = testInput;
      long testLonOffset = 0;
      long testElevationOffset = 0;
      long testTimeOffset = 1;

      JsonNode testPathHistoryPoint = null;
      try {
         testPathHistoryPoint = (JsonNode) XmlUtils.fromXmlS("<PathHistor><latOffset>" + testLatOffset
               + "</latOffset><lonOffset>" + testLonOffset + "</lonOffset><elevationOffset>" + testElevationOffset
               + "</elevationOffset><timeOffset>" + testTimeOffset + "</timeOffset></PathHistor>", JsonNode.class);
      } catch (XmlUtilsException e) {
         fail("XML parsing error:" + e);
      }
      // testPathHistoryPoint.setSpeed(testSpeed);
      // testPathHistoryPoint.setPosAccuracy(testPositionalAccuracy);
      // testPathHistoryPoint.setHeading(testHeading);

      BigDecimal actualValue = PathHistoryPointBuilder.genericPathHistoryPoint(testPathHistoryPoint).getLatOffset();

      assertEquals(expectedValue, actualValue);
   }

   /**
    * Test that the minimum lat offset value (-131071) returns (-0.0131071)
    */
   @Test
   public void shouldCreateMinimumLatOffset() {

      Integer testInput = -131071;
      BigDecimal expectedValue = BigDecimal.valueOf(-0.0131071);

      long testLatOffset = testInput;
      long testLonOffset = 0;
      long testElevationOffset = 0;
      long testTimeOffset = 1;

      JsonNode testPathHistoryPoint = null;
      try {
         testPathHistoryPoint = (JsonNode) XmlUtils.fromXmlS("<PathHistor><latOffset>" + testLatOffset
               + "</latOffset><lonOffset>" + testLonOffset + "</lonOffset><elevationOffset>" + testElevationOffset
               + "</elevationOffset><timeOffset>" + testTimeOffset + "</timeOffset></PathHistor>", JsonNode.class);
      } catch (XmlUtilsException e) {
         fail("XML parsing error:" + e);
      }

      BigDecimal actualValue = PathHistoryPointBuilder.genericPathHistoryPoint(testPathHistoryPoint).getLatOffset();

      assertEquals(expectedValue, actualValue);
   }

   /**
    * Test that a corner case minimum lat offset value (-131070) returns
    * (-0.0131070)
    */
   @Test
   public void shouldCreateCornerCaseMinimumLatOffset() {

      Integer testInput = -131070;
      BigDecimal expectedValue = BigDecimal.valueOf(-0.0131070).setScale(7);

      long testLatOffset = testInput;
      long testLonOffset = 0;
      long testElevationOffset = 0;
      long testTimeOffset = 1;

      JsonNode testPathHistoryPoint = null;
      try {
         testPathHistoryPoint = (JsonNode) XmlUtils.fromXmlS("<PathHistor><latOffset>" + testLatOffset
               + "</latOffset><lonOffset>" + testLonOffset + "</lonOffset><elevationOffset>" + testElevationOffset
               + "</elevationOffset><timeOffset>" + testTimeOffset + "</timeOffset></PathHistor>", JsonNode.class);
      } catch (XmlUtilsException e) {
         fail("XML parsing error:" + e);
      }

      BigDecimal actualValue = PathHistoryPointBuilder.genericPathHistoryPoint(testPathHistoryPoint).getLatOffset();

      assertEquals(expectedValue, actualValue);
   }

   /**
    * Test that a lat offset value (1927) returns (0.0001927)
    */
   @Test
   public void shouldCreateMiddleLatOffset() {

      Integer testInput = 1927;
      BigDecimal expectedValue = BigDecimal.valueOf(0.0001927);

      long testLatOffset = testInput;
      long testLonOffset = 0;
      long testElevationOffset = 0;
      long testTimeOffset = 1;

      JsonNode testPathHistoryPoint = null;
      try {
         testPathHistoryPoint = (JsonNode) XmlUtils.fromXmlS("<PathHistor><latOffset>" + testLatOffset
               + "</latOffset><lonOffset>" + testLonOffset + "</lonOffset><elevationOffset>" + testElevationOffset
               + "</elevationOffset><timeOffset>" + testTimeOffset + "</timeOffset></PathHistor>", JsonNode.class);
      } catch (XmlUtilsException e) {
         fail("XML parsing error:" + e);
      }

      BigDecimal actualValue = PathHistoryPointBuilder.genericPathHistoryPoint(testPathHistoryPoint).getLatOffset();

      assertEquals(expectedValue, actualValue);
   }

   /**
    * Test that a corner case maximum lat offset value (131070) returns (0.0131070)
    */
   @Test
   public void shouldCreateCornerCaseMaximumLatOffset() {

      Integer testInput = 131070;
      BigDecimal expectedValue = BigDecimal.valueOf(0.0131070).setScale(7);

      long testLatOffset = testInput;
      long testLonOffset = 0;
      long testElevationOffset = 0;
      long testTimeOffset = 1;

      JsonNode testPathHistoryPoint = null;
      try {
         testPathHistoryPoint = (JsonNode) XmlUtils.fromXmlS("<PathHistor><latOffset>" + testLatOffset
               + "</latOffset><lonOffset>" + testLonOffset + "</lonOffset><elevationOffset>" + testElevationOffset
               + "</elevationOffset><timeOffset>" + testTimeOffset + "</timeOffset></PathHistor>", JsonNode.class);
      } catch (XmlUtilsException e) {
         fail("XML parsing error:" + e);
      }

      BigDecimal actualValue = PathHistoryPointBuilder.genericPathHistoryPoint(testPathHistoryPoint).getLatOffset();

      assertEquals(expectedValue, actualValue);
   }

   /**
    * Test that the maximum lat offset value (131071) returns (0.0131071)
    */
   @Test
   public void shouldCreateMaximumLatOffset() {
      Integer testInput = 131071;
      BigDecimal expectedValue = BigDecimal.valueOf(0.0131071);

      long testLatOffset = testInput;
      long testLonOffset = 0;
      long testElevationOffset = 0;
      long testTimeOffset = 1;

      JsonNode testPathHistoryPoint = null;
      try {
         testPathHistoryPoint = (JsonNode) XmlUtils.fromXmlS("<PathHistor><latOffset>" + testLatOffset
               + "</latOffset><lonOffset>" + testLonOffset + "</lonOffset><elevationOffset>" + testElevationOffset
               + "</elevationOffset><timeOffset>" + testTimeOffset + "</timeOffset></PathHistor>", JsonNode.class);
      } catch (XmlUtilsException e) {
         fail("XML parsing error:" + e);
      }

      BigDecimal actualValue = PathHistoryPointBuilder.genericPathHistoryPoint(testPathHistoryPoint).getLatOffset();

      assertEquals(expectedValue, actualValue);
   }

   /**
    * Test that a lat offset value (-131073) below the lower bound (-131071) is
    * increased to the lower bound
    */
   @Test
   public void shouldIncreaseLatOffsetBelowLowerBound() {

      Integer testInput = -131073;
      BigDecimal expectedValue = BigDecimal.valueOf(-0.0131071);

      long testLatOffset = testInput;
      long testLonOffset = 0;
      long testElevationOffset = 0;
      long testTimeOffset = 1;

      JsonNode testPathHistoryPoint = null;
      try {
         testPathHistoryPoint = (JsonNode) XmlUtils.fromXmlS("<PathHistor><latOffset>" + testLatOffset
               + "</latOffset><lonOffset>" + testLonOffset + "</lonOffset><elevationOffset>" + testElevationOffset
               + "</elevationOffset><timeOffset>" + testTimeOffset + "</timeOffset></PathHistor>", JsonNode.class);
      } catch (XmlUtilsException e) {
         fail("XML parsing error:" + e);
      }
      BigDecimal actualValue = PathHistoryPointBuilder.genericPathHistoryPoint(testPathHistoryPoint).getLatOffset();

      assertEquals(expectedValue, actualValue);
   }

   /**
    * Test that a lat offset value (131073) above the upper bound (131071) is
    * reduced to the upper bound
    */
   @Test
   public void shouldReduceLatOffsetAboveUpperBound() {

      Integer testInput = 131073;
      BigDecimal expectedValue = BigDecimal.valueOf(0.0131071);

      long testLatOffset = testInput;
      long testLonOffset = 0;
      long testElevationOffset = 0;
      long testTimeOffset = 1;

      JsonNode testPathHistoryPoint = null;
      try {
         testPathHistoryPoint = (JsonNode) XmlUtils.fromXmlS("<PathHistor><latOffset>" + testLatOffset
               + "</latOffset><lonOffset>" + testLonOffset + "</lonOffset><elevationOffset>" + testElevationOffset
               + "</elevationOffset><timeOffset>" + testTimeOffset + "</timeOffset></PathHistor>", JsonNode.class);
      } catch (XmlUtilsException e) {
         fail("XML parsing error:" + e);
      }
      BigDecimal actualValue = PathHistoryPointBuilder.genericPathHistoryPoint(testPathHistoryPoint).getLatOffset();

      assertEquals(expectedValue, actualValue);
   }

   // lonOffset tests
   /**
    * Test that the undefined flag lon offset value (-131072) returns (null)
    */
   @Test
   public void shouldCreateUndefinedLonOffset() {

      Integer testInput = -131072;
      BigDecimal expectedValue = null;

      long testLatOffset = 0;
      long testLonOffset = testInput;
      long testElevationOffset = 0;
      long testTimeOffset = 1;

      JsonNode testPathHistoryPoint = null;
      try {
         testPathHistoryPoint = (JsonNode) XmlUtils.fromXmlS("<PathHistor><latOffset>" + testLatOffset
               + "</latOffset><lonOffset>" + testLonOffset + "</lonOffset><elevationOffset>" + testElevationOffset
               + "</elevationOffset><timeOffset>" + testTimeOffset + "</timeOffset></PathHistor>", JsonNode.class);
      } catch (XmlUtilsException e) {
         fail("XML parsing error:" + e);
      }
      BigDecimal actualValue = PathHistoryPointBuilder.genericPathHistoryPoint(testPathHistoryPoint).getLonOffset();

      assertEquals(expectedValue, actualValue);
   }

   /**
    * Test that the minimum lon offset value (-131071) returns (-0.0131071)
    */
   @Test
   public void shouldCreateMinimumLonOffset() {

      Integer testInput = -131071;
      BigDecimal expectedValue = BigDecimal.valueOf(-0.0131071);

      long testLatOffset = 0;
      long testLonOffset = testInput;
      long testElevationOffset = 0;
      long testTimeOffset = 1;

      JsonNode testPathHistoryPoint = null;
      try {
         testPathHistoryPoint = (JsonNode) XmlUtils.fromXmlS("<PathHistor><latOffset>" + testLatOffset
               + "</latOffset><lonOffset>" + testLonOffset + "</lonOffset><elevationOffset>" + testElevationOffset
               + "</elevationOffset><timeOffset>" + testTimeOffset + "</timeOffset></PathHistor>", JsonNode.class);
      } catch (XmlUtilsException e) {
         fail("XML parsing error:" + e);
      }
      BigDecimal actualValue = PathHistoryPointBuilder.genericPathHistoryPoint(testPathHistoryPoint).getLonOffset();

      assertEquals(expectedValue, actualValue);
   }

   /**
    * Test that a corner case minimum lon offset value (-131070) returns
    * (-0.0131070)
    */
   @Test
   public void shouldCreateCornerCaseMinimumLonOffset() {

      Integer testInput = -131070;
      BigDecimal expectedValue = BigDecimal.valueOf(-0.0131070).setScale(7);

      long testLatOffset = 0;
      long testLonOffset = testInput;
      long testElevationOffset = 0;
      long testTimeOffset = 1;

      JsonNode testPathHistoryPoint = null;
      try {
         testPathHistoryPoint = (JsonNode) XmlUtils.fromXmlS("<PathHistor><latOffset>" + testLatOffset
               + "</latOffset><lonOffset>" + testLonOffset + "</lonOffset><elevationOffset>" + testElevationOffset
               + "</elevationOffset><timeOffset>" + testTimeOffset + "</timeOffset></PathHistor>", JsonNode.class);
      } catch (XmlUtilsException e) {
         fail("XML parsing error:" + e);
      }
      BigDecimal actualValue = PathHistoryPointBuilder.genericPathHistoryPoint(testPathHistoryPoint).getLonOffset();

      assertEquals(expectedValue, actualValue);
   }

   /**
    * Test that a lon offset value (-134) returns (-0.0000134)
    */
   @Test
   public void shouldCreateMiddleLonOffset() {

      Integer testInput = -134;
      BigDecimal expectedValue = BigDecimal.valueOf(-0.0000134);
      long testLatOffset = 0;
      long testLonOffset = testInput;
      long testElevationOffset = 0;
      long testTimeOffset = 1;

      JsonNode testPathHistoryPoint = null;
      try {
         testPathHistoryPoint = (JsonNode) XmlUtils.fromXmlS("<PathHistor><latOffset>" + testLatOffset
               + "</latOffset><lonOffset>" + testLonOffset + "</lonOffset><elevationOffset>" + testElevationOffset
               + "</elevationOffset><timeOffset>" + testTimeOffset + "</timeOffset></PathHistor>", JsonNode.class);
      } catch (XmlUtilsException e) {
         fail("XML parsing error:" + e);
      }
      BigDecimal actualValue = PathHistoryPointBuilder.genericPathHistoryPoint(testPathHistoryPoint).getLonOffset();

      assertEquals(expectedValue, actualValue);
   }

   /**
    * Test that a corner case maximum lon offset value (131070) returns (0.0131070)
    */
   @Test
   public void shouldCreateCornerCaseMaximumLonOffset() {

      Integer testInput = 131070;
      BigDecimal expectedValue = BigDecimal.valueOf(0.0131070).setScale(7);

      long testLatOffset = 0;
      long testLonOffset = testInput;
      long testElevationOffset = 0;
      long testTimeOffset = 1;

      JsonNode testPathHistoryPoint = null;
      try {
         testPathHistoryPoint = (JsonNode) XmlUtils.fromXmlS("<PathHistor><latOffset>" + testLatOffset
               + "</latOffset><lonOffset>" + testLonOffset + "</lonOffset><elevationOffset>" + testElevationOffset
               + "</elevationOffset><timeOffset>" + testTimeOffset + "</timeOffset></PathHistor>", JsonNode.class);
      } catch (XmlUtilsException e) {
         fail("XML parsing error:" + e);
      }
      BigDecimal actualValue = PathHistoryPointBuilder.genericPathHistoryPoint(testPathHistoryPoint).getLonOffset();

      assertEquals(expectedValue, actualValue);
   }

   /**
    * Test that the maximum lon offset value (131071) returns (0.0131071)
    */
   @Test
   public void shouldCreateMaximumLonOffset() {

      Integer testInput = 131071;
      BigDecimal expectedValue = BigDecimal.valueOf(0.0131071);

      long testLatOffset = 0;
      long testLonOffset = testInput;
      long testElevationOffset = 0;
      long testTimeOffset = 1;

      JsonNode testPathHistoryPoint = null;
      try {
         testPathHistoryPoint = (JsonNode) XmlUtils.fromXmlS("<PathHistor><latOffset>" + testLatOffset
               + "</latOffset><lonOffset>" + testLonOffset + "</lonOffset><elevationOffset>" + testElevationOffset
               + "</elevationOffset><timeOffset>" + testTimeOffset + "</timeOffset></PathHistor>", JsonNode.class);
      } catch (XmlUtilsException e) {
         fail("XML parsing error:" + e);
      }
      BigDecimal actualValue = PathHistoryPointBuilder.genericPathHistoryPoint(testPathHistoryPoint).getLonOffset();

      assertEquals(expectedValue, actualValue);
   }

   /**
    * Test that a lon offset value (-131073) below the lower bound (-131071) is
    * increased to the lower bound
    */
   @Test
   public void shouldIncreaseLonOffsetBelowLowerBound() {

      Integer testInput = -131073;
      BigDecimal expectedValue = BigDecimal.valueOf(-0.0131071);

      long testLatOffset = 0;
      long testLonOffset = testInput;
      long testElevationOffset = 0;
      long testTimeOffset = 1;

      JsonNode testPathHistoryPoint = null;
      try {
         testPathHistoryPoint = (JsonNode) XmlUtils.fromXmlS("<PathHistor><latOffset>" + testLatOffset
               + "</latOffset><lonOffset>" + testLonOffset + "</lonOffset><elevationOffset>" + testElevationOffset
               + "</elevationOffset><timeOffset>" + testTimeOffset + "</timeOffset></PathHistor>", JsonNode.class);
      } catch (XmlUtilsException e) {
         fail("XML parsing error:" + e);
      }
      BigDecimal actualValue = PathHistoryPointBuilder.genericPathHistoryPoint(testPathHistoryPoint).getLonOffset();

      assertEquals(expectedValue, actualValue);
   }

   /**
    * Test that a lon offset value (131073) above the upper bound (131071) is
    * reduced to the upper bound
    */
   @Test
   public void shouldReduceLonOffsetAboveUpperBound() {

      Integer testInput = 131073;
      BigDecimal expectedValue = BigDecimal.valueOf(0.0131071);

      long testLatOffset = 0;
      long testLonOffset = testInput;
      long testElevationOffset = 0;
      long testTimeOffset = 1;

      JsonNode testPathHistoryPoint = null;
      try {
         testPathHistoryPoint = (JsonNode) XmlUtils.fromXmlS("<PathHistor><latOffset>" + testLatOffset
               + "</latOffset><lonOffset>" + testLonOffset + "</lonOffset><elevationOffset>" + testElevationOffset
               + "</elevationOffset><timeOffset>" + testTimeOffset + "</timeOffset></PathHistor>", JsonNode.class);
      } catch (XmlUtilsException e) {
         fail("XML parsing error:" + e);
      }
      BigDecimal actualValue = PathHistoryPointBuilder.genericPathHistoryPoint(testPathHistoryPoint).getLonOffset();

      assertEquals(expectedValue, actualValue);
   }

   // elevationOffset tests
   /**
    * Test that the undefined elevation offset flag value (-2048) returns (null)
    */
   @Test
   public void shouldCreateUndefinedElevationOffset() {

      Integer testInput = -2048;
      BigDecimal expectedValue = null;

      long testLatOffset = 0;
      long testLonOffset = 0;
      long testElevationOffset = testInput;
      long testTimeOffset = 1;

      JsonNode testPathHistoryPoint = null;
      try {
         testPathHistoryPoint = (JsonNode) XmlUtils.fromXmlS("<PathHistor><latOffset>" + testLatOffset
               + "</latOffset><lonOffset>" + testLonOffset + "</lonOffset><elevationOffset>" + testElevationOffset
               + "</elevationOffset><timeOffset>" + testTimeOffset + "</timeOffset></PathHistor>", JsonNode.class);
      } catch (XmlUtilsException e) {
         fail("XML parsing error:" + e);
      }
      BigDecimal actualValue = PathHistoryPointBuilder.genericPathHistoryPoint(testPathHistoryPoint)
            .getElevationOffset();

      assertEquals(expectedValue, actualValue);
   }

   /**
    * Test minimum elevation offset value (-2047) returns (-204.7)
    */
   @Test
   public void shouldCreateMinimumElevationOffset() {

      Integer testInput = -2047;
      BigDecimal expectedValue = BigDecimal.valueOf(-204.7);

      long testLatOffset = 0;
      long testLonOffset = 0;
      long testElevationOffset = testInput;
      long testTimeOffset = 1;

      JsonNode testPathHistoryPoint = null;
      try {
         testPathHistoryPoint = (JsonNode) XmlUtils.fromXmlS("<PathHistor><latOffset>" + testLatOffset
               + "</latOffset><lonOffset>" + testLonOffset + "</lonOffset><elevationOffset>" + testElevationOffset
               + "</elevationOffset><timeOffset>" + testTimeOffset + "</timeOffset></PathHistor>", JsonNode.class);
      } catch (XmlUtilsException e) {
         fail("XML parsing error:" + e);
      }
      BigDecimal actualValue = PathHistoryPointBuilder.genericPathHistoryPoint(testPathHistoryPoint)
            .getElevationOffset();

      assertEquals(expectedValue, actualValue);
   }

   /**
    * Test corner case minimum elevation offset value (-2046) returns (-204.6)
    */
   @Test
   public void shouldCreateCornerCaseMinimumElevationOffset() {

      Integer testInput = -2046;
      BigDecimal expectedValue = BigDecimal.valueOf(-204.6);
      long testLatOffset = 0;
      long testLonOffset = 0;
      long testElevationOffset = testInput;
      long testTimeOffset = 1;

      JsonNode testPathHistoryPoint = null;
      try {
         testPathHistoryPoint = (JsonNode) XmlUtils.fromXmlS("<PathHistor><latOffset>" + testLatOffset
               + "</latOffset><lonOffset>" + testLonOffset + "</lonOffset><elevationOffset>" + testElevationOffset
               + "</elevationOffset><timeOffset>" + testTimeOffset + "</timeOffset></PathHistor>", JsonNode.class);
      } catch (XmlUtilsException e) {
         fail("XML parsing error:" + e);
      }
      BigDecimal actualValue = PathHistoryPointBuilder.genericPathHistoryPoint(testPathHistoryPoint)
            .getElevationOffset();

      assertEquals(expectedValue, actualValue);
   }

   /**
    * Test middle elevation offset value (35) returns (3.5)
    */
   @Test
   public void shouldCreateMiddleElevationOffset() {

      Integer testInput = 35;
      BigDecimal expectedValue = BigDecimal.valueOf(3.5);

      long testLatOffset = 0;
      long testLonOffset = 0;
      long testElevationOffset = testInput;
      long testTimeOffset = 1;

      JsonNode testPathHistoryPoint = null;
      try {
         testPathHistoryPoint = (JsonNode) XmlUtils.fromXmlS("<PathHistor><latOffset>" + testLatOffset
               + "</latOffset><lonOffset>" + testLonOffset + "</lonOffset><elevationOffset>" + testElevationOffset
               + "</elevationOffset><timeOffset>" + testTimeOffset + "</timeOffset></PathHistor>", JsonNode.class);
      } catch (XmlUtilsException e) {
         fail("XML parsing error:" + e);
      }

      BigDecimal actualValue = PathHistoryPointBuilder.genericPathHistoryPoint(testPathHistoryPoint)
            .getElevationOffset();

      assertEquals(expectedValue, actualValue);
   }

   /**
    * Test corner case maximum elevation offset value (2046) returns (204.6)
    */
   @Test
   public void shouldCreateCornerCaseMaximumElevationOffset() {

      Integer testInput = 2046;
      BigDecimal expectedValue = BigDecimal.valueOf(204.6);

      long testLatOffset = 0;
      long testLonOffset = 0;
      long testElevationOffset = testInput;
      long testTimeOffset = 1;

      JsonNode testPathHistoryPoint = null;
      try {
         testPathHistoryPoint = (JsonNode) XmlUtils.fromXmlS("<PathHistor><latOffset>" + testLatOffset
               + "</latOffset><lonOffset>" + testLonOffset + "</lonOffset><elevationOffset>" + testElevationOffset
               + "</elevationOffset><timeOffset>" + testTimeOffset + "</timeOffset></PathHistor>", JsonNode.class);
      } catch (XmlUtilsException e) {
         fail("XML parsing error:" + e);
      }

      BigDecimal actualValue = PathHistoryPointBuilder.genericPathHistoryPoint(testPathHistoryPoint)
            .getElevationOffset();

      assertEquals(expectedValue, actualValue);
   }

   /**
    * Test maximum elevation offset value (2047) returns (204.7)
    */
   @Test
   public void shouldCreateMaximumElevationOffset() {

      Integer testInput = 2047;
      BigDecimal expectedValue = BigDecimal.valueOf(204.7);

      long testLatOffset = 0;
      long testLonOffset = 0;
      long testElevationOffset = testInput;
      long testTimeOffset = 1;

      JsonNode testPathHistoryPoint = null;
      try {
         testPathHistoryPoint = (JsonNode) XmlUtils.fromXmlS("<PathHistor><latOffset>" + testLatOffset
               + "</latOffset><lonOffset>" + testLonOffset + "</lonOffset><elevationOffset>" + testElevationOffset
               + "</elevationOffset><timeOffset>" + testTimeOffset + "</timeOffset></PathHistor>", JsonNode.class);
      } catch (XmlUtilsException e) {
         fail("XML parsing error:" + e);
      }

      BigDecimal actualValue = PathHistoryPointBuilder.genericPathHistoryPoint(testPathHistoryPoint)
            .getElevationOffset();

      assertEquals(expectedValue, actualValue);
   }

   /**
    * Test that an elevation offset value (-2049) below the lower bound (-2048) is
    * increased and returned as (-204.7)
    */
   @Test
   public void shouldIncreaseElevationOffsetBelowLowerBound() {

      Integer testInput = -2049;
      BigDecimal expectedValue = BigDecimal.valueOf(-204.7);

      long testLatOffset = 0;
      long testLonOffset = 0;
      long testElevationOffset = testInput;
      long testTimeOffset = 1;

      JsonNode testPathHistoryPoint = null;
      try {
         testPathHistoryPoint = (JsonNode) XmlUtils.fromXmlS("<PathHistor><latOffset>" + testLatOffset
               + "</latOffset><lonOffset>" + testLonOffset + "</lonOffset><elevationOffset>" + testElevationOffset
               + "</elevationOffset><timeOffset>" + testTimeOffset + "</timeOffset></PathHistor>", JsonNode.class);
      } catch (XmlUtilsException e) {
         fail("XML parsing error:" + e);
      }

      BigDecimal actualValue = PathHistoryPointBuilder.genericPathHistoryPoint(testPathHistoryPoint)
            .getElevationOffset();

      assertEquals(expectedValue, actualValue);
   }

   /**
    * Test that an elevation offset value (2048) above the upper bound (2047) is
    * reduced and returned as (204.7)
    */
   @Test
   public void shouldReduceElevationOffsetAboveUpperBound() {

      Integer testInput = 2048;
      BigDecimal expectedValue = BigDecimal.valueOf(204.7);

      long testLatOffset = 0;
      long testLonOffset = 0;
      long testElevationOffset = testInput;
      long testTimeOffset = 1;

      JsonNode testPathHistoryPoint = null;
      try {
         testPathHistoryPoint = (JsonNode) XmlUtils.fromXmlS("<PathHistor><latOffset>" + testLatOffset
               + "</latOffset><lonOffset>" + testLonOffset + "</lonOffset><elevationOffset>" + testElevationOffset
               + "</elevationOffset><timeOffset>" + testTimeOffset + "</timeOffset></PathHistor>", JsonNode.class);
      } catch (XmlUtilsException e) {
         fail("XML parsing error:" + e);
      }
      BigDecimal actualValue = PathHistoryPointBuilder.genericPathHistoryPoint(testPathHistoryPoint)
            .getElevationOffset();

      assertEquals(expectedValue, actualValue);
   }

   // timeOffset tests

   /**
    * Test that undefined time offset flag value (65535) returns (null)
    */
   @Test
   public void shouldCreateUndefinedTimeOffset() {

      Integer testInput = 65535;
      BigDecimal expectedValue = null;

      long testLatOffset = 0;
      long testLonOffset = 0;
      long testElevationOffset = 0;
      long testTimeOffset = testInput;

      JsonNode testPathHistoryPoint = null;
      try {
         testPathHistoryPoint = (JsonNode) XmlUtils.fromXmlS("<PathHistor><latOffset>" + testLatOffset
               + "</latOffset><lonOffset>" + testLonOffset + "</lonOffset><elevationOffset>" + testElevationOffset
               + "</elevationOffset><timeOffset>" + testTimeOffset + "</timeOffset></PathHistor>", JsonNode.class);
      } catch (XmlUtilsException e) {
         fail("XML parsing error:" + e);
      }

      BigDecimal actualValue = PathHistoryPointBuilder.genericPathHistoryPoint(testPathHistoryPoint).getTimeOffset();

      assertEquals(expectedValue, actualValue);
   }

   /**
    * Test that the minimum time offset value (1) returns (0.01)
    */
   @Test
   public void shouldCreateMinimumTimeOffset() {

      Integer testInput = 1;
      BigDecimal expectedValue = BigDecimal.valueOf(0.01);

      long testLatOffset = 0;
      long testLonOffset = 0;
      long testElevationOffset = 0;
      long testTimeOffset = testInput;

      JsonNode testPathHistoryPoint = null;
      try {
         testPathHistoryPoint = (JsonNode) XmlUtils.fromXmlS("<PathHistor><latOffset>" + testLatOffset
               + "</latOffset><lonOffset>" + testLonOffset + "</lonOffset><elevationOffset>" + testElevationOffset
               + "</elevationOffset><timeOffset>" + testTimeOffset + "</timeOffset></PathHistor>", JsonNode.class);
      } catch (XmlUtilsException e) {
         fail("XML parsing error:" + e);
      }
      BigDecimal actualValue = PathHistoryPointBuilder.genericPathHistoryPoint(testPathHistoryPoint).getTimeOffset();

      assertEquals(expectedValue, actualValue);
   }

   /**
    * Test corner case minimum time offset value (2) returns (0.02)
    */
   @Test
   public void shouldCreateCornerCaseMinimumTimeOffset() {

      Integer testInput = 2;
      BigDecimal expectedValue = BigDecimal.valueOf(0.02);

      long testLatOffset = 0;
      long testLonOffset = 0;
      long testElevationOffset = 0;
      long testTimeOffset = testInput;

      JsonNode testPathHistoryPoint = null;
      try {
         testPathHistoryPoint = (JsonNode) XmlUtils.fromXmlS("<PathHistor><latOffset>" + testLatOffset
               + "</latOffset><lonOffset>" + testLonOffset + "</lonOffset><elevationOffset>" + testElevationOffset
               + "</elevationOffset><timeOffset>" + testTimeOffset + "</timeOffset></PathHistor>", JsonNode.class);
      } catch (XmlUtilsException e) {
         fail("XML parsing error:" + e);
      }
      BigDecimal actualValue = PathHistoryPointBuilder.genericPathHistoryPoint(testPathHistoryPoint).getTimeOffset();

      assertEquals(expectedValue, actualValue);
   }

   /**
    * Test known, middle time offset value (32001) returns (320.01)
    */
   @Test
   public void shouldCreateMiddleTimeOffset() {

      Integer testInput = 32001;
      BigDecimal expectedValue = BigDecimal.valueOf(320.01);

      long testLatOffset = 0;
      long testLonOffset = 0;
      long testElevationOffset = 0;
      long testTimeOffset = testInput;

      JsonNode testPathHistoryPoint = null;
      try {
         testPathHistoryPoint = (JsonNode) XmlUtils.fromXmlS("<PathHistor><latOffset>" + testLatOffset
               + "</latOffset><lonOffset>" + testLonOffset + "</lonOffset><elevationOffset>" + testElevationOffset
               + "</elevationOffset><timeOffset>" + testTimeOffset + "</timeOffset></PathHistor>", JsonNode.class);
      } catch (XmlUtilsException e) {
         fail("XML parsing error:" + e);
      }
      BigDecimal actualValue = PathHistoryPointBuilder.genericPathHistoryPoint(testPathHistoryPoint).getTimeOffset();

      assertEquals(expectedValue, actualValue);
   }
   
    /**
    * Test corner case maximum time offset value (65533) returns (655.33)
    */
    @Test
    public void shouldCreateCornerCaseMaximumTimeOffset() {
   
    Integer testInput = 65533;
    BigDecimal expectedValue = BigDecimal.valueOf(655.33);
   
    long testLatOffset = 0;
    long testLonOffset = 0;
    long testElevationOffset = 0;
    long testTimeOffset = testInput;

    JsonNode testPathHistoryPoint = null;
    try {
       testPathHistoryPoint = (JsonNode) XmlUtils.fromXmlS("<PathHistor><latOffset>" + testLatOffset
             + "</latOffset><lonOffset>" + testLonOffset + "</lonOffset><elevationOffset>" + testElevationOffset
             + "</elevationOffset><timeOffset>" + testTimeOffset + "</timeOffset></PathHistor>", JsonNode.class);
    } catch (XmlUtilsException e) {
       fail("XML parsing error:" + e);
    }
   
    BigDecimal actualValue = PathHistoryPointBuilder
    .genericPathHistoryPoint(testPathHistoryPoint)
    .getTimeOffset();
   
    assertEquals(expectedValue, actualValue);
    }
   
    /**
    * Test maximum time offset value (65534) returns (655.34)
    */
    @Test
    public void shouldCreateMaximumTimeOffset() {
   
    Integer testInput = 65534;
    BigDecimal expectedValue = BigDecimal.valueOf(655.34);
   
    long testLatOffset = 0;
    long testLonOffset = 0;
    long testElevationOffset = 0;
    long testTimeOffset = testInput;

    JsonNode testPathHistoryPoint = null;
    try {
       testPathHistoryPoint = (JsonNode) XmlUtils.fromXmlS("<PathHistor><latOffset>" + testLatOffset
             + "</latOffset><lonOffset>" + testLonOffset + "</lonOffset><elevationOffset>" + testElevationOffset
             + "</elevationOffset><timeOffset>" + testTimeOffset + "</timeOffset></PathHistor>", JsonNode.class);
    } catch (XmlUtilsException e) {
       fail("XML parsing error:" + e);
    }
   
    BigDecimal actualValue = PathHistoryPointBuilder
    .genericPathHistoryPoint(testPathHistoryPoint)
    .getTimeOffset();
   
    assertEquals(expectedValue, actualValue);
    }
   
    /**
    * Test that a time offset value (0) below the lower bound (1) throws
    IllegalArgumentException
    */
    @Test
    public void shouldThrowExceptionTimeOffsetBelowLowerBound() {
   
    Integer testInput = 0;
   
    long testLatOffset = 0;
    long testLonOffset = 0;
    long testElevationOffset = 0;
    long testTimeOffset = testInput;

    JsonNode testPathHistoryPoint = null;
    try {
       testPathHistoryPoint = (JsonNode) XmlUtils.fromXmlS("<PathHistor><latOffset>" + testLatOffset
             + "</latOffset><lonOffset>" + testLonOffset + "</lonOffset><elevationOffset>" + testElevationOffset
             + "</elevationOffset><timeOffset>" + testTimeOffset + "</timeOffset></PathHistor>", JsonNode.class);
    } catch (XmlUtilsException e) {
       fail("XML parsing error:" + e);
    }
    try {
    PathHistoryPointBuilder
    .genericPathHistoryPoint(testPathHistoryPoint)
    .getTimeOffset();
    fail("Expected IllegalArgumentException");
    } catch (RuntimeException e) {
    assertEquals(IllegalArgumentException.class, e.getClass());
    }
    }
   
    /**
    * Test that a time offset value (65536) above the upper bound (65535)
    * is reduced and returned as (655.34)
    */
    @Test
    public void shouldReduceTimeOffsetAboveUpperBound() {
   
    Integer testInput = 65536;
    BigDecimal expectedValue = BigDecimal.valueOf(655.34);
   
    long testLatOffset = 0;
    long testLonOffset = 0;
    long testElevationOffset = 0;
    long testTimeOffset = testInput;

    JsonNode testPathHistoryPoint = null;
    try {
       testPathHistoryPoint = (JsonNode) XmlUtils.fromXmlS("<PathHistor><latOffset>" + testLatOffset
             + "</latOffset><lonOffset>" + testLonOffset + "</lonOffset><elevationOffset>" + testElevationOffset
             + "</elevationOffset><timeOffset>" + testTimeOffset + "</timeOffset></PathHistor>", JsonNode.class);
       

    } catch (XmlUtilsException e) {
       fail("XML parsing error:" + e);
    }
   
    BigDecimal actualValue = PathHistoryPointBuilder
    .genericPathHistoryPoint(testPathHistoryPoint)
    .getTimeOffset();
   
    assertEquals(expectedValue, actualValue);
    }
   
    // speed tests
   
    /**
    * Test that undefined speed flag value (8191) returns (null)
    */
    @Test
    public void shouldCreateUndefinedSpeed() {
   
    Integer testInput = 8191;
    BigDecimal expectedValue = null;
  
    long testSpeed = testInput;  
    
    long testLatOffset = 0;
    long testLonOffset = 0;
    long testElevationOffset = 0;
    long testTimeOffset = 1;

    JsonNode testPathHistoryPoint = null;
    try {
       testPathHistoryPoint = (JsonNode) XmlUtils.fromXmlS("<PathHistor><latOffset>" + testLatOffset
             + "</latOffset><lonOffset>" + testLonOffset + "</lonOffset><elevationOffset>" + testElevationOffset
             + "</elevationOffset><timeOffset>" + testTimeOffset + "</timeOffset><speed>" + testSpeed + "</speed></PathHistor>", JsonNode.class);
       

    } catch (XmlUtilsException e) {
       fail("XML parsing error:" + e);
    }

   

   
    BigDecimal actualValue = PathHistoryPointBuilder
    .genericPathHistoryPoint(testPathHistoryPoint)
    .getSpeed();
   
    assertEquals(expectedValue, actualValue);
    }
   
    /**
    * Test that the minimum speed value (0) returns (0.00)
    */
    @Test
    public void shouldCreateMinimumSpeed() {
   
    Integer testInput = 0;
    BigDecimal expectedValue = BigDecimal.ZERO.setScale(2);
   
    long testLatOffset = 0;
    long testLonOffset = 0;
    long testElevationOffset = 0;
    long testTimeOffset = 1;
    long testSpeed = testInput; 

    JsonNode testPathHistoryPoint = null;
    try {
       testPathHistoryPoint = (JsonNode) XmlUtils.fromXmlS("<PathHistor><latOffset>" + testLatOffset
             + "</latOffset><lonOffset>" + testLonOffset + "</lonOffset><elevationOffset>" + testElevationOffset
             + "</elevationOffset><timeOffset>" + testTimeOffset + "</timeOffset><speed>" + testSpeed + "</speed></PathHistor>", JsonNode.class);
       

    } catch (XmlUtilsException e) {
       fail("XML parsing error:" + e);
    }
   
    BigDecimal actualValue = PathHistoryPointBuilder
    .genericPathHistoryPoint(testPathHistoryPoint)
    .getSpeed();
   
    assertEquals(expectedValue, actualValue);
    }
   
    /**
    * Test corner case minimum speed value (1) returns (0.02)
    */
    @Test
    public void shouldCreateCornerCaseMinimumSpeed() {
   
    Integer testInput = 1;
    BigDecimal expectedValue = BigDecimal.valueOf(0.02);
   
    long testLatOffset = 0;
    long testLonOffset = 0;
    long testElevationOffset = 0;
    long testTimeOffset = 1;
    long testSpeed = testInput; 

    JsonNode testPathHistoryPoint = null;
    try {
       testPathHistoryPoint = (JsonNode) XmlUtils.fromXmlS("<PathHistor><latOffset>" + testLatOffset
             + "</latOffset><lonOffset>" + testLonOffset + "</lonOffset><elevationOffset>" + testElevationOffset
             + "</elevationOffset><timeOffset>" + testTimeOffset + "</timeOffset><speed>" + testSpeed + "</speed></PathHistor>", JsonNode.class);
       

    } catch (XmlUtilsException e) {
       fail("XML parsing error:" + e);
    }
    BigDecimal actualValue = PathHistoryPointBuilder
    .genericPathHistoryPoint(testPathHistoryPoint)
    .getSpeed();
   
    assertEquals(expectedValue, actualValue);
    }
   
    /**
    * Test known, middle speed value 4567 returns (91.34)
    */
    @Test
    public void shouldCreateMiddleSpeed() {
   
    Integer testInput = 4567;
    BigDecimal expectedValue = BigDecimal.valueOf(91.34);
   
    long testLatOffset = 0;
    long testLonOffset = 0;
    long testElevationOffset = 0;
    long testTimeOffset = 1;
    long testSpeed = testInput; 

    JsonNode testPathHistoryPoint = null;
    try {
       testPathHistoryPoint = (JsonNode) XmlUtils.fromXmlS("<PathHistor><latOffset>" + testLatOffset
             + "</latOffset><lonOffset>" + testLonOffset + "</lonOffset><elevationOffset>" + testElevationOffset
             + "</elevationOffset><timeOffset>" + testTimeOffset + "</timeOffset><speed>" + testSpeed + "</speed></PathHistor>", JsonNode.class);
       

    } catch (XmlUtilsException e) {
       fail("XML parsing error:" + e);
    }
    BigDecimal actualValue = PathHistoryPointBuilder
    .genericPathHistoryPoint(testPathHistoryPoint)
    .getSpeed();
   
    assertEquals(expectedValue, actualValue);
    }
   
    /**
    * Test corner case maximum speed value (8189) returns (163.78)
    */
    @Test
    public void shouldCreateCornerCaseMaximumSpeed() {
   
    Integer testInput = 8189;
    BigDecimal expectedValue = BigDecimal.valueOf(163.78);
   
    long testLatOffset = 0;
    long testLonOffset = 0;
    long testElevationOffset = 0;
    long testTimeOffset = 1;
    long testSpeed = testInput; 

    JsonNode testPathHistoryPoint = null;
    try {
       testPathHistoryPoint = (JsonNode) XmlUtils.fromXmlS("<PathHistor><latOffset>" + testLatOffset
             + "</latOffset><lonOffset>" + testLonOffset + "</lonOffset><elevationOffset>" + testElevationOffset
             + "</elevationOffset><timeOffset>" + testTimeOffset + "</timeOffset><speed>" + testSpeed + "</speed></PathHistor>", JsonNode.class);
       

    } catch (XmlUtilsException e) {
       fail("XML parsing error:" + e);
    }
   
    BigDecimal actualValue = PathHistoryPointBuilder
    .genericPathHistoryPoint(testPathHistoryPoint)
    .getSpeed();
   
    assertEquals(expectedValue, actualValue);
    }
   
    /**
    * Test maximum speed value (8190) returns (163.80)
    */
    @Test
    public void shouldCreateMaximumSpeed() {
   
    Integer testInput = 8190;
    BigDecimal expectedValue = BigDecimal.valueOf(163.80).setScale(2);
   
    long testLatOffset = 0;
    long testLonOffset = 0;
    long testElevationOffset = 0;
    long testTimeOffset = 1;
    long testSpeed = testInput; 

    JsonNode testPathHistoryPoint = null;
    try {
       testPathHistoryPoint = (JsonNode) XmlUtils.fromXmlS("<PathHistor><latOffset>" + testLatOffset
             + "</latOffset><lonOffset>" + testLonOffset + "</lonOffset><elevationOffset>" + testElevationOffset
             + "</elevationOffset><timeOffset>" + testTimeOffset + "</timeOffset><speed>" + testSpeed + "</speed></PathHistor>", JsonNode.class);
    } catch (XmlUtilsException e) {
       fail("XML parsing error:" + e);
    }
    BigDecimal actualValue = PathHistoryPointBuilder
    .genericPathHistoryPoint(testPathHistoryPoint)
    .getSpeed();
   
    assertEquals(expectedValue, actualValue);
    }
   
    /**
    * Test that a speed value (-1) below the lower bound (0) throws
    IllegalArgumentException
    */
    @Test
    public void shouldThrowExceptionSpeedBelowLowerBound() {
   
    Integer testInput = -1;
   
    long testLatOffset = 0;
    long testLonOffset = 0;
    long testElevationOffset = 0;
    long testTimeOffset = 1;
    long testSpeed = testInput; 

    JsonNode testPathHistoryPoint = null;
    try {
       testPathHistoryPoint = (JsonNode) XmlUtils.fromXmlS("<PathHistor><latOffset>" + testLatOffset
             + "</latOffset><lonOffset>" + testLonOffset + "</lonOffset><elevationOffset>" + testElevationOffset
             + "</elevationOffset><timeOffset>" + testTimeOffset + "</timeOffset><speed>" + testSpeed + "</speed></PathHistor>", JsonNode.class);
       

    } catch (XmlUtilsException e) {
       fail("XML parsing error:" + e);
    }
    try {
    PathHistoryPointBuilder
    .genericPathHistoryPoint(testPathHistoryPoint)
    .getSpeed();
    fail("Expected IllegalArgumentException");
    } catch (RuntimeException e) {
    assertEquals(IllegalArgumentException.class, e.getClass());
    }
    }
   
    /**
    * Test that a speed value (8192) above the upper bound (8191) throws
    IllegalArgumentException
    */
    @Test
    public void shouldThrowExceptionSpeedAboveUpperBound() {
   
    Integer testInput = 8192;
   
    long testLatOffset = 0;
    long testLonOffset = 0;
    long testElevationOffset = 0;
    long testTimeOffset = 1;
    long testSpeed = testInput; 

    JsonNode testPathHistoryPoint = null;
    try {
       testPathHistoryPoint = (JsonNode) XmlUtils.fromXmlS("<PathHistor><latOffset>" + testLatOffset
             + "</latOffset><lonOffset>" + testLonOffset + "</lonOffset><elevationOffset>" + testElevationOffset
             + "</elevationOffset><timeOffset>" + testTimeOffset + "</timeOffset><speed>" + testSpeed + "</speed></PathHistor>", JsonNode.class);
       

    } catch (XmlUtilsException e) {
       fail("XML parsing error:" + e);
    }
    try {
    PathHistoryPointBuilder
    .genericPathHistoryPoint(testPathHistoryPoint)
    .getSpeed();
    fail("Expected IllegalArgumentException");
    } catch (RuntimeException e) {
    assertEquals(IllegalArgumentException.class, e.getClass());
    }
    }
   
    // heading tests
    /**
    * Test that heading undefined flag value (240) returns (null)
    */
    @Test
    public void shouldCreateUndefinedHeading() {
   
    Integer testInput = 240;
    BigDecimal expectedValue = null;
    
    
    long testHeading = testInput;
    long testLatOffset = 0;
    long testLonOffset = 0;
    long testElevationOffset = 0;
    long testTimeOffset = 1;


    JsonNode testPathHistoryPoint = null;
    try {
       testPathHistoryPoint = (JsonNode) XmlUtils.fromXmlS("<PathHistor><latOffset>" + testLatOffset
             + "</latOffset><lonOffset>" + testLonOffset + "</lonOffset><elevationOffset>" + testElevationOffset
             + "</elevationOffset><timeOffset>" + testTimeOffset + "</timeOffset><heading>" + testHeading + "</heading></PathHistor>", JsonNode.class);
       

    } catch (XmlUtilsException e) {
       fail("XML parsing error:" + e);
    }
    BigDecimal actualValue = PathHistoryPointBuilder
    .genericPathHistoryPoint(testPathHistoryPoint)
    .getHeading();
   
    assertEquals(expectedValue, actualValue);
    }
   
    /**
    * Test that minimum heading value (0) returns (0.0)
    */
    @Test
    public void shouldCreateMinimumHeading() {
   
    Integer testInput = 0;
    BigDecimal expectedValue = BigDecimal.ZERO.setScale(1);
   
    long testHeading = testInput;
    long testLatOffset = 0;
    long testLonOffset = 0;
    long testElevationOffset = 0;
    long testTimeOffset = 1;


    JsonNode testPathHistoryPoint = null;
    try {
       testPathHistoryPoint = (JsonNode) XmlUtils.fromXmlS("<PathHistor><latOffset>" + testLatOffset
             + "</latOffset><lonOffset>" + testLonOffset + "</lonOffset><elevationOffset>" + testElevationOffset
             + "</elevationOffset><timeOffset>" + testTimeOffset + "</timeOffset><heading>" + testHeading + "</heading></PathHistor>", JsonNode.class);
       

    } catch (XmlUtilsException e) {
       fail("XML parsing error:" + e);
    }
    BigDecimal actualValue = PathHistoryPointBuilder
    .genericPathHistoryPoint(testPathHistoryPoint)
    .getHeading();
   
    assertEquals(expectedValue, actualValue);
    }
   
    /**
    * Test corner case minimum heading value (1) returns (1.5)
    */
    @Test
    public void shouldCreateCornerCaseMinimumHeading() {
   
    Integer testInput = 1;
    BigDecimal expectedValue = BigDecimal.valueOf(1.5);
   
    long testHeading = testInput;
    long testLatOffset = 0;
    long testLonOffset = 0;
    long testElevationOffset = 0;
    long testTimeOffset = 1;


    JsonNode testPathHistoryPoint = null;
    try {
       testPathHistoryPoint = (JsonNode) XmlUtils.fromXmlS("<PathHistor><latOffset>" + testLatOffset
             + "</latOffset><lonOffset>" + testLonOffset + "</lonOffset><elevationOffset>" + testElevationOffset
             + "</elevationOffset><timeOffset>" + testTimeOffset + "</timeOffset><heading>" + testHeading + "</heading></PathHistor>", JsonNode.class);
       

    } catch (XmlUtilsException e) {
       fail("XML parsing error:" + e);
    }
    BigDecimal actualValue = PathHistoryPointBuilder
    .genericPathHistoryPoint(testPathHistoryPoint)
    .getHeading();
   
    assertEquals(expectedValue, actualValue);
    }
   
    /**
    * Test known, middle heading value (122) returns (183.0)
    */
    @Test
    public void shouldCreateMiddleHeading() {
   
    Integer testInput = 122;
    BigDecimal expectedValue = BigDecimal.valueOf(183.0).setScale(1);
   
    long testHeading = testInput;
    long testLatOffset = 0;
    long testLonOffset = 0;
    long testElevationOffset = 0;
    long testTimeOffset = 1;


    JsonNode testPathHistoryPoint = null;
    try {
       testPathHistoryPoint = (JsonNode) XmlUtils.fromXmlS("<PathHistor><latOffset>" + testLatOffset
             + "</latOffset><lonOffset>" + testLonOffset + "</lonOffset><elevationOffset>" + testElevationOffset
             + "</elevationOffset><timeOffset>" + testTimeOffset + "</timeOffset><heading>" + testHeading + "</heading></PathHistor>", JsonNode.class);
       

    } catch (XmlUtilsException e) {
       fail("XML parsing error:" + e);
    }
    BigDecimal actualValue = PathHistoryPointBuilder
    .genericPathHistoryPoint(testPathHistoryPoint)
    .getHeading();
   
    assertEquals(expectedValue, actualValue);
    }
   
    /**
    * Test corner case maximum heading value (238) returns (357.0)
    */
    @Test
    public void shouldCreateCornerCaseMaximumHeading() {
   
    Integer testInput = 238;
    BigDecimal expectedValue = BigDecimal.valueOf(357.0).setScale(1);
   
    long testHeading = testInput;
    long testLatOffset = 0;
    long testLonOffset = 0;
    long testElevationOffset = 0;
    long testTimeOffset = 1;


    JsonNode testPathHistoryPoint = null;
    try {
       testPathHistoryPoint = (JsonNode) XmlUtils.fromXmlS("<PathHistor><latOffset>" + testLatOffset
             + "</latOffset><lonOffset>" + testLonOffset + "</lonOffset><elevationOffset>" + testElevationOffset
             + "</elevationOffset><timeOffset>" + testTimeOffset + "</timeOffset><heading>" + testHeading + "</heading></PathHistor>", JsonNode.class);
       

    } catch (XmlUtilsException e) {
       fail("XML parsing error:" + e);
    }
    BigDecimal actualValue = PathHistoryPointBuilder
    .genericPathHistoryPoint(testPathHistoryPoint)
    .getHeading();
   
    assertEquals(expectedValue, actualValue);
    }
   
    /**
    * Test maximum heading value (239) returns (358.5)
    */
    @Test
    public void shouldCreateMaximumHeading() {
   
    Integer testInput = 239;
    BigDecimal expectedValue = BigDecimal.valueOf(358.5);
   
    long testHeading = testInput;
    long testLatOffset = 0;
    long testLonOffset = 0;
    long testElevationOffset = 0;
    long testTimeOffset = 1;


    JsonNode testPathHistoryPoint = null;
    try {
       testPathHistoryPoint = (JsonNode) XmlUtils.fromXmlS("<PathHistor><latOffset>" + testLatOffset
             + "</latOffset><lonOffset>" + testLonOffset + "</lonOffset><elevationOffset>" + testElevationOffset
             + "</elevationOffset><timeOffset>" + testTimeOffset + "</timeOffset><heading>" + testHeading + "</heading></PathHistor>", JsonNode.class);
       

    } catch (XmlUtilsException e) {
       fail("XML parsing error:" + e);
    }
    BigDecimal actualValue = PathHistoryPointBuilder
    .genericPathHistoryPoint(testPathHistoryPoint)
    .getHeading();
   
    assertEquals(expectedValue, actualValue);
    }
   
    /**
    * Test heading value (-1) below lower bound (0) throws
    IllegalArgumentException
    */
    @Test
    public void shouldThrowExceptionHeadingBelowLowerBound() {
   
    Integer testInput = -1;
   
    long testHeading = testInput;
    long testLatOffset = 0;
    long testLonOffset = 0;
    long testElevationOffset = 0;
    long testTimeOffset = 1;


    JsonNode testPathHistoryPoint = null;
    try {
       testPathHistoryPoint = (JsonNode) XmlUtils.fromXmlS("<PathHistor><latOffset>" + testLatOffset
             + "</latOffset><lonOffset>" + testLonOffset + "</lonOffset><elevationOffset>" + testElevationOffset
             + "</elevationOffset><timeOffset>" + testTimeOffset + "</timeOffset><heading>" + testHeading + "</heading></PathHistor>", JsonNode.class);
       

    } catch (XmlUtilsException e) {
       fail("XML parsing error:" + e);
    }
   
    try {
    PathHistoryPointBuilder
    .genericPathHistoryPoint(testPathHistoryPoint)
    .getHeading();
    fail("Expected IllegalArgumentException");
    } catch (RuntimeException e) {
    assertEquals(IllegalArgumentException.class, e.getClass());
    }
    }
   
    /**
    * Test heading value (241) above upper bound (240) throws
    IllegalArgumentException
    */
    @Test
    public void shouldThrowExceptionHeadingAboveUpperBound() {
   
    Integer testInput = 241;
   
    long testHeading = testInput;
    long testLatOffset = 0;
    long testLonOffset = 0;
    long testElevationOffset = 0;
    long testTimeOffset = 1;


    JsonNode testPathHistoryPoint = null;
    try {
       testPathHistoryPoint = (JsonNode) XmlUtils.fromXmlS("<PathHistor><latOffset>" + testLatOffset
             + "</latOffset><lonOffset>" + testLonOffset + "</lonOffset><elevationOffset>" + testElevationOffset
             + "</elevationOffset><timeOffset>" + testTimeOffset + "</timeOffset><heading>" + testHeading + "</heading></PathHistor>", JsonNode.class);
       

    } catch (XmlUtilsException e) {
       fail("XML parsing error:" + e);
    }
   
    try {
    PathHistoryPointBuilder
    .genericPathHistoryPoint(testPathHistoryPoint)
    .getHeading();
    fail("Expected IllegalArgumentException");
    } catch (RuntimeException e) {
    assertEquals(IllegalArgumentException.class, e.getClass());
    }
    }
   
    @Test
    public void testConstructorIsPrivate() throws NoSuchMethodException,
    IllegalAccessException, InvocationTargetException, InstantiationException {
    Constructor<PathHistoryPointBuilder > constructor =
    PathHistoryPointBuilder.class.getDeclaredConstructor();
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
