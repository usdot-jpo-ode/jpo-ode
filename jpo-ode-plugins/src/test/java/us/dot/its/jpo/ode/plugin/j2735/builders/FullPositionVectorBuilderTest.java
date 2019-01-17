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

import us.dot.its.jpo.ode.plugin.j2735.J2735TimeConfidence;
import us.dot.its.jpo.ode.util.XmlUtils;
import us.dot.its.jpo.ode.util.XmlUtils.XmlUtilsException;

/**
 * -- Summary -- JUnit test class for FullPositionVectorBuilder
 * 
 * Verifies correct conversion from generic FullPositionVector to
 * compliant-J2735FullPositionVector
 * 
 * Tested elements: - Longitude - Latitude - Elevation - TimeConfidence
 * 
 * The rest of the elements are tested by other Oss*Test classes
 * 
 * -- Documentation -- Data Frame: DF_FullPositionVector Use: A complete report
 * of the vehicle's position, speed, and heading at an instant in time. Used in
 * the probe vehicle message (and elsewhere) as the initial position
 * information. Often followed by other data frames that may provide offset path
 * data. ASN.1 Representation: FullPositionVector ::= SEQUENCE { utcTime
 * DDateTime OPTIONAL, -- time with mSec precision long Longitude, -- 1/10th
 * microdegree lat Latitude, -- 1/10th microdegree elevation Elevation OPTIONAL,
 * -- units of 0.1 m heading Heading OPTIONAL, speed TransmissionAndSpeed
 * OPTIONAL, posAccuracy PositionalAccuracy OPTIONAL, timeConfidence
 * TimeConfidence OPTIONAL, posConfidence PositionConfidenceSet OPTIONAL,
 * speedConfidence SpeedandHeadingandThrottleConfidence OPTIONAL, ... }
 * 
 * Data Element: DE_Longitude Use: The geographic longitude of an object,
 * expressed in 1/10th integer microdegrees, as a 32-bit value, and with
 * reference to the horizontal datum then in use. The value 1800000001 shall be
 * used when unavailable. ASN.1 Representation: Longitude ::= INTEGER
 * (-1799999999..1800000001) -- LSB = 1/10 micro degree -- Providing a range of
 * plus-minus 180 degrees
 * 
 * Data Element: DE_Latitude Use: The geographic latitude of an object,
 * expressed in 1/10th integer microdegrees, as a 31 bit value, and with
 * reference to the horizontal datum then in use. The value 900000001 shall be
 * used when unavailable. ASN.1 Representation: Latitude ::= INTEGER
 * (-900000000..900000001) -- LSB = 1/10 micro degree -- Providing a range of
 * plus-minus 90 degrees
 * 
 * Data Element: DE_Elevation Use: The DE_Elevation data element represents the
 * geographic position above or below the reference ellipsoid (typically
 * WGS-84). The number has a resolution of 1 decimeter and represents an
 * asymmetric range of positive and negative values. Any elevation higher than
 * +6143.9 meters is represented as +61439. Any elevation lower than -409.5
 * meters is represented as -4095. If the sending device does not know its
 * elevation, it shall encode the Elevation data element with -4096. ASN.1
 * Representation: Elevation ::= INTEGER (-4096..61439) -- In units of 10 cm
 * steps above or below the reference ellipsoid -- Providing a range of -409.5
 * to + 6143.9 meters -- The value -4096 shall be used when Unknown is to be
 * sent
 * 
 * Data Element: DE_TimeConfidence Use: The DE_TimeConfidence data element is
 * used to provide the 95% confidence level for the currently reported value of
 * time, taking into account the current calibration and precision of the
 * sensor(s) used to measure and/or calculate the value. This data element is
 * only to provide information on the limitations of the sensing system, not to
 * support any type of automatic error correction or to imply a guaranteed
 * maximum error. This data element should not be used for fault detection or
 * diagnosis, but if a vehicle is able to detect a fault, the confidence
 * interval should be increased accordingly. ASN.1 Representation:
 * TimeConfidence ::= ENUMERATED { unavailable (0), -- Not Equipped or
 * unavailable time-100-000 (1), -- Better than 100 Seconds time-050-000 (2), --
 * Better than 50 Seconds ... time-000-000-000-000-05 (37), -- Better than
 * 0.000,000,000,05 Seconds time-000-000-000-000-02 (38), -- Better than
 * 0.000,000,000,02 Seconds time-000-000-000-000-01 (39) -- Better than
 * 0.000,000,000,01 Seconds }
 */
public class FullPositionVectorBuilderTest {

   // Longitude tests

   /**
    * Test that the undefined longitude flag value (1800000001) returns (null)
    */
   @Test
   public void shouldReturnUndefinedLongitude() {

//      Integer testInput = 1800000001;
      BigDecimal expectedValue = null;
//
//      Longitude testLong = new Longitude(testInput);
//      Latitude testLat = new Latitude(0);
//      Elevation testElevation = new Elevation(0);
//      TimeConfidence testTimeConfidence = new TimeConfidence(0);
//
//      FullPositionVector testFPV = new FullPositionVector();
//      testFPV.set_long(testLong);
//      testFPV.setLat(testLat);
//      testFPV.setElevation(testElevation);
//      testFPV.setTimeConfidence(testTimeConfidence);
//      
      
      
      JsonNode testFPV = null;
      try {
         testFPV = (JsonNode) XmlUtils.fromXmlS(
               "<initialPosition>"
               + "<long>1800000001</long>"
               + "<lat>0</lat>"
               + "<elevation>0</elevation>"
               + "<heading>0</heading>"
               + "<posAccuracy><semiMajor>0</semiMajor><semiMinor>0</semiMinor><orientation>0</orientation></posAccuracy>"
               + "<posConfidence><pos>unavailable</pos><elevation>unavailable</elevation></posConfidence>"
               + "<speed><transmission>forwardGears</transmission><speed>0</speed></speed>"
               + "<speedConfidence><heading>unavailable</heading><speed>unavailable</speed><throttle>unavailable</throttle></speedConfidence>"
               + "<timeConfidence>UNAVAILABLE</timeConfidence>"
               + "<utcTime><year>0</year><month>0</month><day>0</day><hour>31</hour><minute>60</minute><second>65535</second><offset>0</offset></utcTime>"
               + "</initialPosition>", JsonNode.class);
      } catch (XmlUtilsException e) {
         fail("XML parsing error:" + e);
      }


      BigDecimal actualValue = FullPositionVectorBuilder.genericFullPositionVector(testFPV).getPosition().getLongitude();

      assertEquals(expectedValue, actualValue);
   }

   /**
    * Test that the minimum longitude value (-1799999999) returns (-179.9999999)
    */
   @Test
   public void shouldReturnMinimumLongitude() {

 
      BigDecimal expectedValue = BigDecimal.valueOf(-179.9999999);


      
      JsonNode testFPV = null;
      try {
         testFPV = (JsonNode) XmlUtils.fromXmlS(
               "<initialPosition>"
               + "<long>-1799999999</long>"
               + "<lat>0</lat>"
               + "<elevation></elevation>"
              // + "<heading></heading>"
              // + "<posAccuracy><semiMajor>0</semiMajor><semiMinor>0</semiMinor><orientation>0</orientation></posAccuracy>"
              // + "<posConfidence><pos>unavailable</pos><elevation>unavailable</elevation></posConfidence>"
             //  + "<speed><transmission>forwardGears</transmission><speed>0</speed></speed>"
             //  + "<speedConfidence><heading>unavailable</heading><speed>unavailable</speed><throttle>unavailable</throttle></speedConfidence>"
               + "<timeConfidence>UNAVAILABLE</timeConfidence>"
              // + "<utcTime></utcTime>"
               + "</initialPosition>", JsonNode.class);
      } catch (XmlUtilsException e) {
         fail("XML parsing error:" + e);
      }

      BigDecimal actualValue = FullPositionVectorBuilder.genericFullPositionVector(testFPV).getPosition().getLongitude();

      assertEquals(expectedValue, actualValue);

   }

   /**
    * Test that the corner case minimum longitude value (-1799999998) returns
    * (-179.9999998)
    */
   @Test
   public void shouldReturnCornerCaseMinimumLongitude() {

      BigDecimal expectedValue = BigDecimal.valueOf(-179.9999998);
 
      JsonNode testFPV = null;
      try {
         testFPV = (JsonNode) XmlUtils.fromXmlS(
               "<initialPosition>"
               + "<long>-1799999998</long>"
               + "<lat>0</lat>"
               + "<elevation>0</elevation>"
               + "<heading>0</heading>"
               + "<posAccuracy><semiMajor>0</semiMajor><semiMinor>0</semiMinor><orientation>0</orientation></posAccuracy>"
               + "<posConfidence><pos>unavailable</pos><elevation>unavailable</elevation></posConfidence>"
               + "<speed><transmission>forwardGears</transmission><speed>0</speed></speed>"
               + "<speedConfidence><heading>unavailable</heading><speed>unavailable</speed><throttle>unavailable</throttle></speedConfidence>"
               + "<timeConfidence>UNAVAILABLE</timeConfidence>"
               + "<utcTime><year>0</year><month>0</month><day>0</day><hour>31</hour><minute>60</minute><second>65535</second><offset>0</offset></utcTime>"
               + "</initialPosition>", JsonNode.class);
      } catch (XmlUtilsException e) {
         fail("XML parsing error:" + e);
      }

      BigDecimal actualValue = FullPositionVectorBuilder.genericFullPositionVector(testFPV).getPosition().getLongitude();

      assertEquals(expectedValue, actualValue);

   }

   /**
    * Test that a zero longitude value (0) returns (0)
    */
   @Test
   public void shouldReturnZeroLongitude() {

      BigDecimal expectedValue = BigDecimal.ZERO.setScale(7);

      
      JsonNode testFPV = null;
      try {
         testFPV = (JsonNode) XmlUtils.fromXmlS(
               "<initialPosition>"
               + "<long>0</long>"
               + "<lat>0</lat>"
               + "<elevation>0</elevation>"
               + "<heading>0</heading>"
               + "<posAccuracy><semiMajor>0</semiMajor><semiMinor>0</semiMinor><orientation>0</orientation></posAccuracy>"
               + "<posConfidence><pos>unavailable</pos><elevation>unavailable</elevation></posConfidence>"
               + "<speed><transmission>forwardGears</transmission><speed>0</speed></speed>"
               + "<speedConfidence><heading>unavailable</heading><speed>unavailable</speed><throttle>unavailable</throttle></speedConfidence>"
               + "<timeConfidence>UNAVAILABLE</timeConfidence>"
               + "<utcTime><year>0</year><month>0</month><day>0</day><hour>31</hour><minute>60</minute><second>65535</second><offset>0</offset></utcTime>"
               + "</initialPosition>", JsonNode.class);
      } catch (XmlUtilsException e) {
         fail("XML parsing error:" + e);
      }
      
      BigDecimal actualValue = FullPositionVectorBuilder.genericFullPositionVector(testFPV).getPosition().getLongitude();

      assertEquals(expectedValue, actualValue);

   }

   /**
    * Test that a corner case maximum longitude value () returns ()
    */
   @Test
   public void shouldReturnCornerCaseMaximumLongitude() {

      BigDecimal expectedValue = BigDecimal.valueOf(179.9999999);


      JsonNode testFPV = null;
      try {
         testFPV = (JsonNode) XmlUtils.fromXmlS(
               "<initialPosition>"
               + "<long>1799999999</long>"
               + "<lat>0</lat>"
               + "<elevation>0</elevation>"
               + "<heading>0</heading>"
               + "<posAccuracy><semiMajor>0</semiMajor><semiMinor>0</semiMinor><orientation>0</orientation></posAccuracy>"
               + "<posConfidence><pos>unavailable</pos><elevation>unavailable</elevation></posConfidence>"
               + "<speed><transmission>forwardGears</transmission><speed>0</speed></speed>"
               + "<speedConfidence><heading>unavailable</heading><speed>unavailable</speed><throttle>unavailable</throttle></speedConfidence>"
               + "<timeConfidence>UNAVAILABLE</timeConfidence>"
               + "<utcTime><year>0</year><month>0</month><day>0</day><hour>31</hour><minute>60</minute><second>65535</second><offset>0</offset></utcTime>"
               + "</initialPosition>", JsonNode.class);
      } catch (XmlUtilsException e) {
         fail("XML parsing error:" + e);
      }
      
      BigDecimal actualValue = FullPositionVectorBuilder.genericFullPositionVector(testFPV).getPosition().getLongitude();

      assertEquals(expectedValue, actualValue);

   }

   /**
    * Test that the maximum longitude value () returns ()
    */
   @Test
   public void shouldReturnMaximumLongitude() {


      BigDecimal expectedValue = BigDecimal.valueOf(180.0000000).setScale(7);

      JsonNode testFPV = null;
      try {
         testFPV = (JsonNode) XmlUtils.fromXmlS(
               "<initialPosition>"
               + "<long>1800000000</long>"
               + "<lat>0</lat>"
               + "<elevation>0</elevation>"
               + "<heading>0</heading>"
               + "<posAccuracy><semiMajor>0</semiMajor><semiMinor>0</semiMinor><orientation>0</orientation></posAccuracy>"
               + "<posConfidence><pos>unavailable</pos><elevation>unavailable</elevation></posConfidence>"
               + "<speed><transmission>forwardGears</transmission><speed>0</speed></speed>"
               + "<speedConfidence><heading>unavailable</heading><speed>unavailable</speed><throttle>unavailable</throttle></speedConfidence>"
               + "<timeConfidence>UNAVAILABLE</timeConfidence>"
               + "<utcTime><year>0</year><month>0</month><day>0</day><hour>31</hour><minute>60</minute><second>65535</second><offset>0</offset></utcTime>"
               + "</initialPosition>", JsonNode.class);
      } catch (XmlUtilsException e) {
         fail("XML parsing error:" + e);
      }
      
      BigDecimal actualValue = FullPositionVectorBuilder.genericFullPositionVector(testFPV).getPosition().getLongitude();

      assertEquals(expectedValue, actualValue);

   }

   /**
    * Test that a longitude value (-1800000000) below the lower bound
    * (-1799999999) throws IllegalArgumentException
    */
   @Test
   public void shouldThrowExceptionLongitudeBelowLowerBound() {


      JsonNode testFPV = null;
      try {
         testFPV = (JsonNode) XmlUtils.fromXmlS(
               "<initialPosition>"
               + "<long>-1800000000</long>"
               + "<lat>0</lat>"
               + "<elevation>0</elevation>"
               + "<heading>0</heading>"
               + "<posAccuracy><semiMajor>0</semiMajor><semiMinor>0</semiMinor><orientation>0</orientation></posAccuracy>"
               + "<posConfidence><pos>unavailable</pos><elevation>unavailable</elevation></posConfidence>"
               + "<speed><transmission>forwardGears</transmission><speed>0</speed></speed>"
               + "<speedConfidence><heading>unavailable</heading><speed>unavailable</speed><throttle>unavailable</throttle></speedConfidence>"
               + "<timeConfidence>UNAVAILABLE</timeConfidence>"
               + "<utcTime><year>0</year><month>0</month><day>0</day><hour>31</hour><minute>60</minute><second>65535</second><offset>0</offset></utcTime>"
               + "</initialPosition>", JsonNode.class);
      } catch (XmlUtilsException e) {
         fail("XML parsing error:" + e);
      }
      
      
      try {
         FullPositionVectorBuilder.genericFullPositionVector(testFPV).getPosition().getLongitude();
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }

   }

   /**
    * Test that a longitude value (1800000002) above the upper bound
    * (1800000001) throws IllegalArgumentException
    */
   @Test
   public void shouldThrowExceptionLongitudeAboveUpperBound() {



      JsonNode testFPV = null;
      try {
         testFPV = (JsonNode) XmlUtils.fromXmlS(
               "<initialPosition>"
               + "<long>-1800000000</long>"
               + "<lat>0</lat>"
               + "<elevation>0</elevation>"
               + "<heading>0</heading>"
               + "<posAccuracy><semiMajor>0</semiMajor><semiMinor>0</semiMinor><orientation>0</orientation></posAccuracy>"
               + "<posConfidence><pos>unavailable</pos><elevation>unavailable</elevation></posConfidence>"
               + "<speed><transmission>forwardGears</transmission><speed>0</speed></speed>"
               + "<speedConfidence><heading>unavailable</heading><speed>unavailable</speed><throttle>unavailable</throttle></speedConfidence>"
               + "<timeConfidence>UNAVAILABLE</timeConfidence>"
               + "<utcTime><year>0</year><month>0</month><day>0</day><hour>31</hour><minute>60</minute><second>65535</second><offset>0</offset></utcTime>"
               + "</initialPosition>", JsonNode.class);
      } catch (XmlUtilsException e) {
         fail("XML parsing error:" + e);
      }
      
      try {
         FullPositionVectorBuilder.genericFullPositionVector(testFPV).getPosition().getLongitude();
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }

   }

   // Latitude tests

   /**
    * Test that an undefined latitude flag value (900000001) returns (null)
    */
   @Test
   public void shouldReturnUndefinedLatitude() {

      BigDecimal expectedValue = null;

      
      JsonNode testFPV = null;
      try {
         testFPV = (JsonNode) XmlUtils.fromXmlS(
               "<initialPosition>"
               + "<long>0</long>"
               + "<lat>900000001</lat>"
               + "<elevation>0</elevation>"
               + "<heading>0</heading>"
               + "<posAccuracy><semiMajor>0</semiMajor><semiMinor>0</semiMinor><orientation>0</orientation></posAccuracy>"
               + "<posConfidence><pos>unavailable</pos><elevation>unavailable</elevation></posConfidence>"
               + "<speed><transmission>forwardGears</transmission><speed>0</speed></speed>"
               + "<speedConfidence><heading>unavailable</heading><speed>unavailable</speed><throttle>unavailable</throttle></speedConfidence>"
               + "<timeConfidence>UNAVAILABLE</timeConfidence>"
               + "<utcTime><year>0</year><month>0</month><day>0</day><hour>31</hour><minute>60</minute><second>65535</second><offset>0</offset></utcTime>"
               + "</initialPosition>", JsonNode.class);
      } catch (XmlUtilsException e) {
         fail("XML parsing error:" + e);
      }

      BigDecimal actualValue = FullPositionVectorBuilder.genericFullPositionVector(testFPV).getPosition().getLatitude();

      assertEquals(expectedValue, actualValue);

   }

   /**
    * Test that the minimum latitude value (-900000000) returns (-90.0000000)
    */
   @Test
   public void shouldReturnMinimumLatitude() {

      BigDecimal expectedValue = BigDecimal.valueOf(-90.0000000).setScale(7);



      JsonNode testFPV = null;
      try {
         testFPV = (JsonNode) XmlUtils.fromXmlS(
               "<initialPosition>"
               + "<long>0</long>"
               + "<lat>-900000000</lat>"
               + "<elevation>0</elevation>"
               + "<heading>0</heading>"
               + "<posAccuracy><semiMajor>0</semiMajor><semiMinor>0</semiMinor><orientation>0</orientation></posAccuracy>"
               + "<posConfidence><pos>unavailable</pos><elevation>unavailable</elevation></posConfidence>"
               + "<speed><transmission>forwardGears</transmission><speed>0</speed></speed>"
               + "<speedConfidence><heading>unavailable</heading><speed>unavailable</speed><throttle>unavailable</throttle></speedConfidence>"
               + "<timeConfidence>UNAVAILABLE</timeConfidence>"
               + "<utcTime><year>0</year><month>0</month><day>0</day><hour>31</hour><minute>60</minute><second>65535</second><offset>0</offset></utcTime>"
               + "</initialPosition>", JsonNode.class);
      } catch (XmlUtilsException e) {
         fail("XML parsing error:" + e);
      }
      
      BigDecimal actualValue = FullPositionVectorBuilder.genericFullPositionVector(testFPV).getPosition().getLatitude();

      assertEquals(expectedValue, actualValue);
   }

   /**
    * Test that a corner case minimum latitude value (-899999999) returns
    * (-89.9999999)
    */
   @Test
   public void shouldReturnCornerCaseMinimumLatitude() {

     
      BigDecimal expectedValue = BigDecimal.valueOf(-89.9999999);


      JsonNode testFPV = null;
      try {
         testFPV = (JsonNode) XmlUtils.fromXmlS(
               "<initialPosition>"
               + "<long>0</long>"
               + "<lat>-899999999</lat>"
               + "<elevation>0</elevation>"
               + "<heading>0</heading>"
               + "<posAccuracy><semiMajor>0</semiMajor><semiMinor>0</semiMinor><orientation>0</orientation></posAccuracy>"
               + "<posConfidence><pos>unavailable</pos><elevation>unavailable</elevation></posConfidence>"
               + "<speed><transmission>forwardGears</transmission><speed>0</speed></speed>"
               + "<speedConfidence><heading>unavailable</heading><speed>unavailable</speed><throttle>unavailable</throttle></speedConfidence>"
               + "<timeConfidence>UNAVAILABLE</timeConfidence>"
               + "<utcTime><year>0</year><month>0</month><day>0</day><hour>31</hour><minute>60</minute><second>65535</second><offset>0</offset></utcTime>"
               + "</initialPosition>", JsonNode.class);
      } catch (XmlUtilsException e) {
         fail("XML parsing error:" + e);
      }
      
      BigDecimal actualValue = FullPositionVectorBuilder.genericFullPositionVector(testFPV).getPosition().getLatitude();

      assertEquals(expectedValue, actualValue);

   }

   /**
    * Test that a zero latitude value (0) returns (0.0000000)
    */
   @Test
   public void shouldReturnZeroLatitude() {

  
      BigDecimal expectedValue = BigDecimal.ZERO.setScale(7);

      JsonNode testFPV = null;
      try {
         testFPV = (JsonNode) XmlUtils.fromXmlS(
               "<initialPosition>"
               + "<long>0</long>"
               + "<lat>0</lat>"
               + "<elevation>0</elevation>"
               + "<heading>0</heading>"
               + "<posAccuracy><semiMajor>0</semiMajor><semiMinor>0</semiMinor><orientation>0</orientation></posAccuracy>"
               + "<posConfidence><pos>unavailable</pos><elevation>unavailable</elevation></posConfidence>"
               + "<speed><transmission>forwardGears</transmission><speed>0</speed></speed>"
               + "<speedConfidence><heading>unavailable</heading><speed>unavailable</speed><throttle>unavailable</throttle></speedConfidence>"
               + "<timeConfidence>UNAVAILABLE</timeConfidence>"
               + "<utcTime><year>0</year><month>0</month><day>0</day><hour>31</hour><minute>60</minute><second>65535</second><offset>0</offset></utcTime>"
               + "</initialPosition>", JsonNode.class);
      } catch (XmlUtilsException e) {
         fail("XML parsing error:" + e);
      }
      
      BigDecimal actualValue = FullPositionVectorBuilder.genericFullPositionVector(testFPV).getPosition().getLatitude();

      assertEquals(expectedValue, actualValue);

   }

   /**
    * Test that a corner case maximum latitude value (899999999) returns
    * (89.9999999)
    */
   @Test
   public void shouldReturnCornerCaseMaximumLatitude() {

     
      BigDecimal expectedValue = BigDecimal.valueOf(89.9999999);


      JsonNode testFPV = null;
      try {
         testFPV = (JsonNode) XmlUtils.fromXmlS(
               "<initialPosition>"
               + "<long>0</long>"
               + "<lat>899999999</lat>"
               + "<elevation>0</elevation>"
               + "<heading>0</heading>"
               + "<posAccuracy><semiMajor>0</semiMajor><semiMinor>0</semiMinor><orientation>0</orientation></posAccuracy>"
               + "<posConfidence><pos>unavailable</pos><elevation>unavailable</elevation></posConfidence>"
               + "<speed><transmission>forwardGears</transmission><speed>0</speed></speed>"
               + "<speedConfidence><heading>unavailable</heading><speed>unavailable</speed><throttle>unavailable</throttle></speedConfidence>"
               + "<timeConfidence>UNAVAILABLE</timeConfidence>"
               + "<utcTime><year>0</year><month>0</month><day>0</day><hour>31</hour><minute>60</minute><second>65535</second><offset>0</offset></utcTime>"
               + "</initialPosition>", JsonNode.class);
      } catch (XmlUtilsException e) {
         fail("XML parsing error:" + e);
      }
      
      BigDecimal actualValue = FullPositionVectorBuilder.genericFullPositionVector(testFPV).getPosition().getLatitude();

      assertEquals(expectedValue, actualValue);

   }

   /**
    * Test that the maximum latitude value (900000000) returns (90.0000000)
    */
   @Test
   public void shouldReturnMaximumLatitude() {

      
      BigDecimal expectedValue = BigDecimal.valueOf(90.0000000).setScale(7);


      JsonNode testFPV = null;
      try {
         testFPV = (JsonNode) XmlUtils.fromXmlS(
               "<initialPosition>"
               + "<long>0</long>"
               + "<lat>900000000</lat>"
               + "<elevation>0</elevation>"
               + "<heading>0</heading>"
               + "<posAccuracy><semiMajor>0</semiMajor><semiMinor>0</semiMinor><orientation>0</orientation></posAccuracy>"
               + "<posConfidence><pos>unavailable</pos><elevation>unavailable</elevation></posConfidence>"
               + "<speed><transmission>forwardGears</transmission><speed>0</speed></speed>"
               + "<speedConfidence><heading>unavailable</heading><speed>unavailable</speed><throttle>unavailable</throttle></speedConfidence>"
               + "<timeConfidence>UNAVAILABLE</timeConfidence>"
               + "<utcTime><year>0</year><month>0</month><day>0</day><hour>31</hour><minute>60</minute><second>65535</second><offset>0</offset></utcTime>"
               + "</initialPosition>", JsonNode.class);
      } catch (XmlUtilsException e) {
         fail("XML parsing error:" + e);
      }
      
      BigDecimal actualValue = FullPositionVectorBuilder.genericFullPositionVector(testFPV).getPosition().getLatitude();

      assertEquals(expectedValue, actualValue);

   }

   /**
    * Test that a latitude value (-900000001) below the lower bound (-900000000)
    * throws IllegalArgumentException
    */
   @Test
   public void shouldThrowExceptionLatitudeBelowLowerBound() {



      JsonNode testFPV = null;
      try {
         testFPV = (JsonNode) XmlUtils.fromXmlS(
               "<initialPosition>"
               + "<long>0</long>"
               + "<lat>-900000001</lat>"
               + "<elevation>0</elevation>"
               + "<heading>0</heading>"
               + "<posAccuracy><semiMajor>0</semiMajor><semiMinor>0</semiMinor><orientation>0</orientation></posAccuracy>"
               + "<posConfidence><pos>unavailable</pos><elevation>unavailable</elevation></posConfidence>"
               + "<speed><transmission>forwardGears</transmission><speed>0</speed></speed>"
               + "<speedConfidence><heading>unavailable</heading><speed>unavailable</speed><throttle>unavailable</throttle></speedConfidence>"
               + "<timeConfidence>UNAVAILABLE</timeConfidence>"
               + "<utcTime><year>0</year><month>0</month><day>0</day><hour>31</hour><minute>60</minute><second>65535</second><offset>0</offset></utcTime>"
               + "</initialPosition>", JsonNode.class);
      } catch (XmlUtilsException e) {
         fail("XML parsing error:" + e);
      }
      
      try {
         FullPositionVectorBuilder.genericFullPositionVector(testFPV).getPosition().getLatitude();
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }

   }

   /**
    * Test that a latitude value (900000002) above the upper bound (900000001)
    * throws IllegalArgumentException
    */
   @Test
   public void shouldThrowExceptionLatitudeAboveUpperBound() {



      JsonNode testFPV = null;
      try {
         testFPV = (JsonNode) XmlUtils.fromXmlS(
               "<initialPosition>"
               + "<long>0</long>"
               + "<lat>900000002</lat>"
               + "<elevation>0</elevation>"
               + "<heading>0</heading>"
               + "<posAccuracy><semiMajor>0</semiMajor><semiMinor>0</semiMinor><orientation>0</orientation></posAccuracy>"
               + "<posConfidence><pos>unavailable</pos><elevation>unavailable</elevation></posConfidence>"
               + "<speed><transmission>forwardGears</transmission><speed>0</speed></speed>"
               + "<speedConfidence><heading>unavailable</heading><speed>unavailable</speed><throttle>unavailable</throttle></speedConfidence>"
               + "<timeConfidence>UNAVAILABLE</timeConfidence>"
               + "<utcTime><year>0</year><month>0</month><day>0</day><hour>31</hour><minute>60</minute><second>65535</second><offset>0</offset></utcTime>"
               + "</initialPosition>", JsonNode.class);
      } catch (XmlUtilsException e) {
         fail("XML parsing error:" + e);
      }
      
      try {
         FullPositionVectorBuilder.genericFullPositionVector(testFPV).getPosition().getLatitude();
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }

   }

   // Elevation tests

   /**
    * Test that undefined elevation flag value (-4096) returns (null)
    */
   @Test
   public void shouldReturnUndefinedElevation() {

  
      BigDecimal expectedValue = null;


      JsonNode testFPV = null;
      try {
         testFPV = (JsonNode) XmlUtils.fromXmlS(
               "<initialPosition>"
               + "<long>0</long>"
               + "<lat>0</lat>"
               + "<elevation>-4096</elevation>"
               + "<heading>0</heading>"
               + "<posAccuracy><semiMajor>0</semiMajor><semiMinor>0</semiMinor><orientation>0</orientation></posAccuracy>"
               + "<posConfidence><pos>unavailable</pos><elevation>unavailable</elevation></posConfidence>"
               + "<speed><transmission>forwardGears</transmission><speed>0</speed></speed>"
               + "<speedConfidence><heading>unavailable</heading><speed>unavailable</speed><throttle>unavailable</throttle></speedConfidence>"
               + "<timeConfidence>UNAVAILABLE</timeConfidence>"
               + "<utcTime><year>0</year><month>0</month><day>0</day><hour>31</hour><minute>60</minute><second>65535</second><offset>0</offset></utcTime>"
               + "</initialPosition>", JsonNode.class);
      } catch (XmlUtilsException e) {
         fail("XML parsing error:" + e);
      }
      
      BigDecimal actualValue = FullPositionVectorBuilder.genericFullPositionVector(testFPV).getPosition().getElevation();

      assertEquals(expectedValue, actualValue);
   }

   /**
    * Test that minimum elevation value (-4095) returns (-409.5)
    */
   @Test
   public void shouldReturnMinimumElevation() {

   
      BigDecimal expectedValue = BigDecimal.valueOf(-409.5);


      JsonNode testFPV = null;
      try {
         testFPV = (JsonNode) XmlUtils.fromXmlS(
               "<initialPosition>"
               + "<long>0</long>"
               + "<lat>0</lat>"
               + "<elevation>-4095</elevation>"
               + "<heading>0</heading>"
               + "<posAccuracy><semiMajor>0</semiMajor><semiMinor>0</semiMinor><orientation>0</orientation></posAccuracy>"
               + "<posConfidence><pos>unavailable</pos><elevation>unavailable</elevation></posConfidence>"
               + "<speed><transmission>forwardGears</transmission><speed>0</speed></speed>"
               + "<speedConfidence><heading>unavailable</heading><speed>unavailable</speed><throttle>unavailable</throttle></speedConfidence>"
               + "<timeConfidence>UNAVAILABLE</timeConfidence>"
               + "<utcTime><year>0</year><month>0</month><day>0</day><hour>31</hour><minute>60</minute><second>65535</second><offset>0</offset></utcTime>"
               + "</initialPosition>", JsonNode.class);
      } catch (XmlUtilsException e) {
         fail("XML parsing error:" + e);
      }
      
      BigDecimal actualValue = FullPositionVectorBuilder.genericFullPositionVector(testFPV).getPosition().getElevation();

      assertEquals(expectedValue, actualValue);
   }

   /**
    * Test that a corner case minimum elevation value (-4094) returns (-409.4)
    */
   @Test
   public void shouldReturnCornerCaseMinimumElevation() {

    
      BigDecimal expectedValue = BigDecimal.valueOf(-409.4);


      JsonNode testFPV = null;
      try {
         testFPV = (JsonNode) XmlUtils.fromXmlS(
               "<initialPosition>"
               + "<long>0</long>"
               + "<lat>0</lat>"
               + "<elevation>-4094</elevation>"
               + "<heading>0</heading>"
               + "<posAccuracy><semiMajor>0</semiMajor><semiMinor>0</semiMinor><orientation>0</orientation></posAccuracy>"
               + "<posConfidence><pos>unavailable</pos><elevation>unavailable</elevation></posConfidence>"
               + "<speed><transmission>forwardGears</transmission><speed>0</speed></speed>"
               + "<speedConfidence><heading>unavailable</heading><speed>unavailable</speed><throttle>unavailable</throttle></speedConfidence>"
               + "<timeConfidence>UNAVAILABLE</timeConfidence>"
               + "<utcTime><year>0</year><month>0</month><day>0</day><hour>31</hour><minute>60</minute><second>65535</second><offset>0</offset></utcTime>"
               + "</initialPosition>", JsonNode.class);
      } catch (XmlUtilsException e) {
         fail("XML parsing error:" + e);
      }
      
      BigDecimal actualValue = FullPositionVectorBuilder.genericFullPositionVector(testFPV).getPosition().getElevation();

      assertEquals(expectedValue, actualValue);
   }

   /**
    * Test that a zero elevation value (0) returns (0.0)
    */
   @Test
   public void shouldReturnZeroElevation() {

 
      BigDecimal expectedValue = BigDecimal.valueOf(0).setScale(1);


      JsonNode testFPV = null;
      try {
         testFPV = (JsonNode) XmlUtils.fromXmlS(
               "<initialPosition>"
               + "<long>0</long>"
               + "<lat>0</lat>"
               + "<elevation>0</elevation>"
               + "<heading>0</heading>"
               + "<posAccuracy><semiMajor>0</semiMajor><semiMinor>0</semiMinor><orientation>0</orientation></posAccuracy>"
               + "<posConfidence><pos>unavailable</pos><elevation>unavailable</elevation></posConfidence>"
               + "<speed><transmission>forwardGears</transmission><speed>0</speed></speed>"
               + "<speedConfidence><heading>unavailable</heading><speed>unavailable</speed><throttle>unavailable</throttle></speedConfidence>"
               + "<timeConfidence>UNAVAILABLE</timeConfidence>"
               + "<utcTime><year>0</year><month>0</month><day>0</day><hour>31</hour><minute>60</minute><second>65535</second><offset>0</offset></utcTime>"
               + "</initialPosition>", JsonNode.class);
      } catch (XmlUtilsException e) {
         fail("XML parsing error:" + e);
      }
      
      BigDecimal actualValue = FullPositionVectorBuilder.genericFullPositionVector(testFPV).getPosition().getElevation();

      assertEquals(expectedValue, actualValue);
   }

   /**
    * Test that a corner case maximum elevation value (61438) returns (6143.8)
    */
   @Test
   public void shouldReturnCornerCaseMaximumElevation() {

  
      BigDecimal expectedValue = BigDecimal.valueOf(6143.8);


      JsonNode testFPV = null;
      try {
         testFPV = (JsonNode) XmlUtils.fromXmlS(
               "<initialPosition>"
               + "<long>0</long>"
               + "<lat>0</lat>"
               + "<elevation>61438</elevation>"
               + "<heading>0</heading>"
               + "<posAccuracy><semiMajor>0</semiMajor><semiMinor>0</semiMinor><orientation>0</orientation></posAccuracy>"
               + "<posConfidence><pos>unavailable</pos><elevation>unavailable</elevation></posConfidence>"
               + "<speed><transmission>forwardGears</transmission><speed>0</speed></speed>"
               + "<speedConfidence><heading>unavailable</heading><speed>unavailable</speed><throttle>unavailable</throttle></speedConfidence>"
               + "<timeConfidence>UNAVAILABLE</timeConfidence>"
               + "<utcTime><year>0</year><month>0</month><day>0</day><hour>31</hour><minute>60</minute><second>65535</second><offset>0</offset></utcTime>"
               + "</initialPosition>", JsonNode.class);
      } catch (XmlUtilsException e) {
         fail("XML parsing error:" + e);
      }
      
      BigDecimal actualValue = FullPositionVectorBuilder.genericFullPositionVector(testFPV).getPosition().getElevation();

      assertEquals(expectedValue, actualValue);
   }

   /**
    * Test that the maximum elevation value (61439) returns (6143.9)
    */
   @Test
   public void shouldReturnMaximumElevation() {

      
      BigDecimal expectedValue = BigDecimal.valueOf(6143.9);


      JsonNode testFPV = null;
      try {
         testFPV = (JsonNode) XmlUtils.fromXmlS(
               "<initialPosition>"
               + "<long>0</long>"
               + "<lat>0</lat>"
               + "<elevation>61439</elevation>"
               + "<heading>0</heading>"
               + "<posAccuracy><semiMajor>0</semiMajor><semiMinor>0</semiMinor><orientation>0</orientation></posAccuracy>"
               + "<posConfidence><pos>unavailable</pos><elevation>unavailable</elevation></posConfidence>"
               + "<speed><transmission>forwardGears</transmission><speed>0</speed></speed>"
               + "<speedConfidence><heading>unavailable</heading><speed>unavailable</speed><throttle>unavailable</throttle></speedConfidence>"
               + "<timeConfidence>UNAVAILABLE</timeConfidence>"
               + "<utcTime><year>0</year><month>0</month><day>0</day><hour>31</hour><minute>60</minute><second>65535</second><offset>0</offset></utcTime>"
               + "</initialPosition>", JsonNode.class);
      } catch (XmlUtilsException e) {
         fail("XML parsing error:" + e);
      }
      
      BigDecimal actualValue = FullPositionVectorBuilder.genericFullPositionVector(testFPV).getPosition().getElevation();

      assertEquals(expectedValue, actualValue);
   }

   /**
    * Test that an elevation value (-4097) below the lower bound (-4096) throws
    * IllegalArgumentException
    */
   @Test
   public void shouldThrowExceptionElevationBelowLowerBound() {

 

      JsonNode testFPV = null;
      try {
         testFPV = (JsonNode) XmlUtils.fromXmlS(
               "<initialPosition>"
               + "<long>0</long>"
               + "<lat>0</lat>"
               + "<elevation>-4097</elevation>"
               + "<heading>0</heading>"
               + "<posAccuracy><semiMajor>0</semiMajor><semiMinor>0</semiMinor><orientation>0</orientation></posAccuracy>"
               + "<posConfidence><pos>unavailable</pos><elevation>unavailable</elevation></posConfidence>"
               + "<speed><transmission>forwardGears</transmission><speed>0</speed></speed>"
               + "<speedConfidence><heading>unavailable</heading><speed>unavailable</speed><throttle>unavailable</throttle></speedConfidence>"
               + "<timeConfidence>UNAVAILABLE</timeConfidence>"
               + "<utcTime><year>0</year><month>0</month><day>0</day><hour>31</hour><minute>60</minute><second>65535</second><offset>0</offset></utcTime>"
               + "</initialPosition>", JsonNode.class);
      } catch (XmlUtilsException e) {
         fail("XML parsing error:" + e);
      }
      
      try {
         FullPositionVectorBuilder.genericFullPositionVector(testFPV).getPosition().getElevation();
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }

   }

   /**
    * Test that an elevation value (61440) above the upper bound (61439) throws
    * IllegalArgumentException
    */
   @Test
   public void shouldThrowExceptionElevationAboveUpperBound() {


      JsonNode testFPV = null;
      try {
         testFPV = (JsonNode) XmlUtils.fromXmlS(
               "<initialPosition>"
               + "<long>0</long>"
               + "<lat>0</lat>"
               + "<elevation>61440</elevation>"
               + "<heading>0</heading>"
               + "<posAccuracy><semiMajor>0</semiMajor><semiMinor>0</semiMinor><orientation>0</orientation></posAccuracy>"
               + "<posConfidence><pos>unavailable</pos><elevation>unavailable</elevation></posConfidence>"
               + "<speed><transmission>forwardGears</transmission><speed>0</speed></speed>"
               + "<speedConfidence><heading>unavailable</heading><speed>unavailable</speed><throttle>unavailable</throttle></speedConfidence>"
               + "<timeConfidence>UNAVAILABLE</timeConfidence>"
               + "<utcTime><year>0</year><month>0</month><day>0</day><hour>31</hour><minute>60</minute><second>65535</second><offset>0</offset></utcTime>"
               + "</initialPosition>", JsonNode.class);
      } catch (XmlUtilsException e) {
         fail("XML parsing error:" + e);
      }
      
      try {
         FullPositionVectorBuilder.genericFullPositionVector(testFPV).getPosition().getElevation();
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   // TimeConfidence tests

   /**
    * Test that a time confidence undefined flag value (0) returns (unavailable)
    */
   @Test
   public void shouldReturnUnavailableTimeConfidence() {

      
      J2735TimeConfidence expectedValue = J2735TimeConfidence.UNAVAILABLE;
      
      
      JsonNode testFPV = null;
      try {
         testFPV = (JsonNode) XmlUtils.fromXmlS(
               "<initialPosition>"
               + "<long>0</long>"
               + "<lat>0</lat>"
               + "<elevation>0</elevation>"
               + "<heading>0</heading>"
               + "<posAccuracy><semiMajor>0</semiMajor><semiMinor>0</semiMinor><orientation>0</orientation></posAccuracy>"
               + "<posConfidence><pos>unavailable</pos><elevation>unavailable</elevation></posConfidence>"
               + "<speed><transmission>forwardGears</transmission><speed>0</speed></speed>"
               + "<speedConfidence><heading>unavailable</heading><speed>unavailable</speed><throttle>unavailable</throttle></speedConfidence>"
               + "<timeConfidence>unavailable</timeConfidence>"
               + "<utcTime><year>0</year><month>0</month><day>0</day><hour>31</hour><minute>60</minute><second>65535</second><offset>0</offset></utcTime>"
               + "</initialPosition>", JsonNode.class);
      } catch (XmlUtilsException e) {
         fail("XML parsing error:" + e);
      }
      
      J2735TimeConfidence actualValue = FullPositionVectorBuilder.genericFullPositionVector(testFPV).getTimeConfidence();

      assertEquals(expectedValue, actualValue);

   }

   /**
    * Test that a minimum time confidence value (1) returns (time-100-000)
    */
   @Test
   public void shouldReturnMinimumTimeConfidence() {

   
      J2735TimeConfidence expectedValue = J2735TimeConfidence.TIME_100_000;


      JsonNode testFPV = null;
      try {
         testFPV = (JsonNode) XmlUtils.fromXmlS(
               "<initialPosition>"
               + "<long>0</long>"
               + "<lat>0</lat>"
               + "<elevation>0</elevation>"
               + "<heading>0</heading>"
               + "<posAccuracy><semiMajor>0</semiMajor><semiMinor>0</semiMinor><orientation>0</orientation></posAccuracy>"
               + "<posConfidence><pos>unavailable</pos><elevation>unavailable</elevation></posConfidence>"
               + "<speed><transmission>forwardGears</transmission><speed>0</speed></speed>"
               + "<speedConfidence><heading>unavailable</heading><speed>unavailable</speed><throttle>unavailable</throttle></speedConfidence>"
               + "<timeConfidence>TIME_100_000</timeConfidence>"
               + "<utcTime><year>0</year><month>0</month><day>0</day><hour>31</hour><minute>60</minute><second>65535</second><offset>0</offset></utcTime>"
               + "</initialPosition>", JsonNode.class);
      } catch (XmlUtilsException e) {
         fail("XML parsing error:" + e);
      }
      
      J2735TimeConfidence actualValue = FullPositionVectorBuilder.genericFullPositionVector(testFPV).getTimeConfidence();

      assertEquals(expectedValue, actualValue);

   }

   /**
    * Test that a corner case minimum time confidence value (2) returns
    * (time-050-000)
    */
   @Test
   public void shouldReturnCornerCaseMinimumTimeConfidence() {

     
      J2735TimeConfidence expectedValue = J2735TimeConfidence.TIME_050_000;



 
      JsonNode testFPV = null;
      try {
         testFPV = (JsonNode) XmlUtils.fromXmlS(
               "<initialPosition>"
               + "<long>0</long>"
               + "<lat>0</lat>"
               + "<elevation>0</elevation>"
               + "<heading>0</heading>"
               + "<posAccuracy><semiMajor>0</semiMajor><semiMinor>0</semiMinor><orientation>0</orientation></posAccuracy>"
               + "<posConfidence><pos>unavailable</pos><elevation>unavailable</elevation></posConfidence>"
               + "<speed><transmission>forwardGears</transmission><speed>0</speed></speed>"
               + "<speedConfidence><heading>unavailable</heading><speed>unavailable</speed><throttle>unavailable</throttle></speedConfidence>"
               + "<timeConfidence>time_050_000</timeConfidence>"
               + "<utcTime><year>0</year><month>0</month><day>0</day><hour>31</hour><minute>60</minute><second>65535</second><offset>0</offset></utcTime>"
               + "</initialPosition>", JsonNode.class);
      } catch (XmlUtilsException e) {
         fail("XML parsing error:" + e);
      }
      
      J2735TimeConfidence actualValue = FullPositionVectorBuilder.genericFullPositionVector(testFPV).getTimeConfidence();

      assertEquals(expectedValue, actualValue);

   }

   /**
    * Test that a corner case maximum time confidence value (38) returns
    * (time-000-000-000-000-02)
    */
   @Test
   public void shouldReturnCornerCaseMaximumTimeConfidence() {

     
      J2735TimeConfidence expectedValue = J2735TimeConfidence.TIME_000_000_000_000_02;


      JsonNode testFPV = null;
      try {
         testFPV = (JsonNode) XmlUtils.fromXmlS(
               "<initialPosition>"
               + "<long>0</long>"
               + "<lat>0</lat>"
               + "<elevation>0</elevation>"
               + "<heading>0</heading>"
               + "<posAccuracy><semiMajor>0</semiMajor><semiMinor>0</semiMinor><orientation>0</orientation></posAccuracy>"
               + "<posConfidence><pos>unavailable</pos><elevation>unavailable</elevation></posConfidence>"
               + "<speed><transmission>forwardGears</transmission><speed>0</speed></speed>"
               + "<speedConfidence><heading>unavailable</heading><speed>unavailable</speed><throttle>unavailable</throttle></speedConfidence>"
               + "<timeConfidence>TIME_000_000_000_000_02</timeConfidence>"
               + "<utcTime><year>0</year><month>0</month><day>0</day><hour>31</hour><minute>60</minute><second>65535</second><offset>0</offset></utcTime>"
               + "</initialPosition>", JsonNode.class);
      } catch (XmlUtilsException e) {
         fail("XML parsing error:" + e);
      }
      
      J2735TimeConfidence actualValue = FullPositionVectorBuilder.genericFullPositionVector(testFPV).getTimeConfidence();

      assertEquals(expectedValue, actualValue);

   }

   /**
    * Test that the maximum time confidence value (39) returns
    * (time-000-000-000-000-01)
    */
   @Test
   public void shouldReturnMaximumTimeConfidence() {

    
      J2735TimeConfidence expectedValue = J2735TimeConfidence.TIME_000_000_000_000_01;


   
      JsonNode testFPV = null;
      try {
         testFPV = (JsonNode) XmlUtils.fromXmlS(
               "<initialPosition>"
               + "<long>0</long>"
               + "<lat>0</lat>"
               + "<elevation>0</elevation>"
               + "<heading>0</heading>"
               + "<posAccuracy><semiMajor>0</semiMajor><semiMinor>0</semiMinor><orientation>0</orientation></posAccuracy>"
               + "<posConfidence><pos>unavailable</pos><elevation>unavailable</elevation></posConfidence>"
               + "<speed><transmission>forwardGears</transmission><speed>0</speed></speed>"
               + "<speedConfidence><heading>unavailable</heading><speed>unavailable</speed><throttle>unavailable</throttle></speedConfidence>"
               + "<timeConfidence>TIME_000_000_000_000_01</timeConfidence>"
               + "<utcTime><year>0</year><month>0</month><day>0</day><hour>31</hour><minute>60</minute><second>65535</second><offset>0</offset></utcTime>"
               + "</initialPosition>", JsonNode.class);
      } catch (XmlUtilsException e) {
         fail("XML parsing error:" + e);
      }
      
      J2735TimeConfidence actualValue = FullPositionVectorBuilder.genericFullPositionVector(testFPV).getTimeConfidence();

      assertEquals(expectedValue, actualValue);

   }

   /**
    * Test that a time confidence value (-1) below the lower bound (0) throws
    * IllegalArgumentException
    */
   @Test
   public void shouldThrowExceptionTimeConfidenceBelowLowerBound() {



      JsonNode testFPV = null;
      try {
         testFPV = (JsonNode) XmlUtils.fromXmlS(
               "<initialPosition>"
               + "<long>0</long>"
               + "<lat>0</lat>"
               + "<elevation>0</elevation>"
               + "<heading>0</heading>"
               + "<posAccuracy><semiMajor>0</semiMajor><semiMinor>0</semiMinor><orientation>0</orientation></posAccuracy>"
               + "<posConfidence><pos>unavailable</pos><elevation>unavailable</elevation></posConfidence>"
               + "<speed><transmission>forwardGears</transmission><speed>0</speed></speed>"
               + "<speedConfidence><heading>unavailable</heading><speed>unavailable</speed><throttle>unavailable</throttle></speedConfidence>"
               + "<timeConfidence>TIME_000_000</timeConfidence>"
               + "<utcTime><year>0</year><month>0</month><day>0</day><hour>31</hour><minute>60</minute><second>65535</second><offset>0</offset></utcTime>"
               + "</initialPosition>", JsonNode.class);
      } catch (XmlUtilsException e) {
         fail("XML parsing error:" + e);
      }
      
      try {
         FullPositionVectorBuilder.genericFullPositionVector(testFPV).getTimeConfidence();
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }

   }

   /**
    * Test that a time confidence value (40) above the upper bound (39) throws
    * IllegalArgumentException
    */
   @Test
   public void shouldThrowExceptionTimeConfidenceAboveUpperBound() {



      JsonNode testFPV = null;
      try {
         testFPV = (JsonNode) XmlUtils.fromXmlS(
               "<initialPosition>"
               + "<long>0</long>"
               + "<lat>0</lat>"
               + "<elevation>0</elevation>"
               + "<heading>0</heading>"
               + "<posAccuracy><semiMajor>0</semiMajor><semiMinor>0</semiMinor><orientation>0</orientation></posAccuracy>"
               + "<posConfidence><pos>unavailable</pos><elevation>unavailable</elevation></posConfidence>"
               + "<speed><transmission>forwardGears</transmission><speed>0</speed></speed>"
               + "<speedConfidence><heading>unavailable</heading><speed>unavailable</speed><throttle>unavailable</throttle></speedConfidence>"
               + "<timeConfidence>IME_000_000_000_000_44</timeConfidence>"
               + "<utcTime><year>0</year><month>0</month><day>0</day><hour>31</hour><minute>60</minute><second>65535</second><offset>0</offset></utcTime>"
               + "</initialPosition>", JsonNode.class);
      } catch (XmlUtilsException e) {
         fail("XML parsing error:" + e);
      }
      
      try {
         FullPositionVectorBuilder.genericFullPositionVector(testFPV).getTimeConfidence();
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }

   }
   

   /**
    * Null elevation test
    * IllegalArgumentException
    */
   @Test
   public void shouldThrowExceptionNullElivation() {



      JsonNode testFPV = null;
      try {
         testFPV = (JsonNode) XmlUtils.fromXmlS(
               "<initialPosition>"
               + "<long>0</long>"
               + "<lat>0</lat>"
               //+ "<elevation>0</elevation>"
               + "<heading>0</heading>"
               + "<posAccuracy><semiMajor>0</semiMajor><semiMinor>0</semiMinor><orientation>0</orientation></posAccuracy>"
               + "<posConfidence><pos>unavailable</pos><elevation>unavailable</elevation></posConfidence>"
               + "<speed><transmission>forwardGears</transmission><speed>0</speed></speed>"
               + "<speedConfidence><heading>unavailable</heading><speed>unavailable</speed><throttle>unavailable</throttle></speedConfidence>"
               + "<timeConfidence>IME_000_000_000_000_44</timeConfidence>"
               + "<utcTime><year>0</year><month>0</month><day>0</day><hour>31</hour><minute>60</minute><second>65535</second><offset>0</offset></utcTime>"
               + "</initialPosition>", JsonNode.class);
      } catch (XmlUtilsException e) {
         fail("XML parsing error:" + e);
      }
      
      try {
         FullPositionVectorBuilder.genericFullPositionVector(testFPV).getPosition().getElevation();
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }

   }
   
   /**
    * Null timeConfidence test
    * IllegalArgumentException
    */
   @Test
   public void shouldThrowExceptionNullTimeConfidence() {



      JsonNode testFPV = null;
      try {
         testFPV = (JsonNode) XmlUtils.fromXmlS(
               "<initialPosition>"
               + "<long>0</long>"
               + "<lat>0</lat>"
               + "<elevation>0</elevation>"
               + "<heading>0</heading>"
               + "<posAccuracy><semiMajor>0</semiMajor><semiMinor>0</semiMinor><orientation>0</orientation></posAccuracy>"
               + "<posConfidence><pos>unavailable</pos><elevation>unavailable</elevation></posConfidence>"
               + "<speed><transmission>forwardGears</transmission><speed>0</speed></speed>"
               + "<speedConfidence><heading>unavailable</heading><speed>unavailable</speed><throttle>unavailable</throttle></speedConfidence>"
               // + "<timeConfidence>UNAVAILABLE</timeConfidence>"
               + "<utcTime><year>0</year><month>0</month><day>0</day><hour>31</hour><minute>60</minute><second>65535</second><offset>0</offset></utcTime>"
               + "</initialPosition>", JsonNode.class);
      } catch (XmlUtilsException e) {
         fail("XML parsing error:" + e);
      }
      
      try {
         FullPositionVectorBuilder.genericFullPositionVector(testFPV).getTimeConfidence();
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }

   }
   
   @Test
   public void testConstructorIsPrivate() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
     Constructor<FullPositionVectorBuilder > constructor = FullPositionVectorBuilder.class.getDeclaredConstructor();
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
