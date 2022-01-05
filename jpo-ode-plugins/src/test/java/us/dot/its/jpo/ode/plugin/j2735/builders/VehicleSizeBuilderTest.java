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
import static org.junit.Assert.fail;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735VehicleSize;
import us.dot.its.jpo.ode.util.XmlUtils;
import us.dot.its.jpo.ode.util.XmlUtils.XmlUtilsException;

/**
 * -- Summary -- JUnit test class for VehicleSizeBuilder
 * 
 * Verifies correct conversion from JsonNode to compliant-J2735VehicleSize
 * 
 * -- Documentation -- Data Frame: DF_VehicleSize Use: The DF_VehicleSize is a
 * data frame representing the vehicle length and vehicle width in a single data
 * concept. ASN.1 Representation: VehicleSize ::= SEQUENCE { width VehicleWidth,
 * length VehicleLength }
 *
 * Data Element: DE_VehicleWidth Use: The width of the vehicle expressed in
 * centimeters, unsigned. The width shall be the widest point of the vehicle
 * with all factory installed equipment. The value zero shall be sent when data
 * is unavailable. ASN.1 Representation: VehicleWidth ::= INTEGER (0..1023) --
 * LSB units are 1 cm with a range of >10 meters
 * 
 * Data Element: DE_VehicleLength Use: The length of the vehicle measured from
 * the edge of the front bumper to the edge of the rear bumper expressed in
 * centimeters, unsigned. It should be noted that this value is often combined
 * with a vehicle width value to form a data frame. The value zero shall be sent
 * when data is unavailable. ASN.1 Representation: VehicleLength ::= INTEGER
 * (0.. 4095) -- LSB units of 1 cm with a range of >40 meters
 */
public class VehicleSizeBuilderTest {

   // VehicleWidth tests

   /**
    * Test that the undefined vehicle width value (0) returns 0
    */
   @Test
   public void shouldReturnZeroVehicleWidth() {

      Integer expectedValue = 0;

      Integer testWidth = 0;
      Integer testLength = 0;

      JsonNode testVehicleSize = null;
      try {
         testVehicleSize = (JsonNode) XmlUtils.fromXmlS(
               "<VehicleSize><width>" + testWidth + "</width><length>" + testLength + "</length></VehicleSize>",
               JsonNode.class);
      } catch (XmlUtilsException e) {
         fail("XML parsing error:" + e);
      }

      J2735VehicleSize actualVehicleSize = VehicleSizeBuilder.genericVehicleSize(testVehicleSize);
      Integer actualValue = actualVehicleSize.getWidth();

      assertEquals(expectedValue, actualValue);

   }

   /**
    * Test that the minimum vehicle width value (1) returns (1)
    */
   @Test
   public void shouldReturnMinimumVehicleWidth() {

      Integer expectedValue = 1;

      Integer testWidth = 1;
      Integer testLength = 0;

      JsonNode testVehicleSize = null;
      try {
         testVehicleSize = (JsonNode) XmlUtils.fromXmlS(
               "<VehicleSize><width>" + testWidth + "</width><length>" + testLength + "</length></VehicleSize>",
               JsonNode.class);
      } catch (XmlUtilsException e) {
         fail("XML parsing error:" + e);
      }

      J2735VehicleSize actualVehicleSize = VehicleSizeBuilder.genericVehicleSize(testVehicleSize);
      Integer actualValue = actualVehicleSize.getWidth();

      assertEquals(expectedValue, actualValue);

   }

   /**
    * Test a minimum corner case vehicle width value (2) returns (2)
    */
   @Test
   public void shouldReturnCornerCaseMinimumVehicleWidth() {

      Integer expectedValue = 2;

      Integer testWidth = 2;
      Integer testLength = 0;

      JsonNode testVehicleSize = null;
      try {
         testVehicleSize = (JsonNode) XmlUtils.fromXmlS(
               "<VehicleSize><width>" + testWidth + "</width><length>" + testLength + "</length></VehicleSize>",
               JsonNode.class);
      } catch (XmlUtilsException e) {
         fail("XML parsing error:" + e);
      }

      J2735VehicleSize actualVehicleSize = VehicleSizeBuilder.genericVehicleSize(testVehicleSize);
      Integer actualValue = actualVehicleSize.getWidth();

      assertEquals(expectedValue, actualValue);

   }

   /**
    * Test that a maximum corner case vehicle width value (1022) returns (1022)
    */
   @Test
   public void shouldReturnCornerCaseMaximumVehicleWidth() {

      Integer expectedValue = 1022;

      Integer testWidth = 1022;
      Integer testLength = 0;

      JsonNode testVehicleSize = null;
      try {
         testVehicleSize = (JsonNode) XmlUtils.fromXmlS(
               "<VehicleSize><width>" + testWidth + "</width><length>" + testLength + "</length></VehicleSize>",
               JsonNode.class);
      } catch (XmlUtilsException e) {
         fail("XML parsing error:" + e);
      }

      J2735VehicleSize actualVehicleSize = VehicleSizeBuilder.genericVehicleSize(testVehicleSize);
      Integer actualValue = actualVehicleSize.getWidth();

      assertEquals(expectedValue, actualValue);

   }

   /**
    * Test that the maximum vehicle width value (1023) returns (1023)
    */
   @Test
   public void shouldReturnMaximumVehicleWidth() {

      Integer expectedValue = 1023;

      Integer testWidth = 1023;
      Integer testLength = 0;

      JsonNode testVehicleSize = null;
      try {
         testVehicleSize = (JsonNode) XmlUtils.fromXmlS(
               "<VehicleSize><width>" + testWidth + "</width><length>" + testLength + "</length></VehicleSize>",
               JsonNode.class);
      } catch (XmlUtilsException e) {
         fail("XML parsing error:" + e);
      }

      J2735VehicleSize actualVehicleSize = VehicleSizeBuilder.genericVehicleSize(testVehicleSize);
      Integer actualValue = actualVehicleSize.getWidth();

      assertEquals(expectedValue, actualValue);

   }

   /**
    * Test that a vehicle width value (-1) below the lower bound (0) throws
    */
   @Test
   public void shouldThrowExceptionVehicleWidthBelowLowerBound() {

      Integer testWidth = -1;
      Integer testLength = 0;

      JsonNode testVehicleSize = null;
      try {
         testVehicleSize = (JsonNode) XmlUtils.fromXmlS(
               "<VehicleSize><width>" + testWidth + "</width><length>" + testLength + "</length></VehicleSize>",
               JsonNode.class);
      } catch (XmlUtilsException e) {
         fail("XML parsing error:" + e);
      }

      try {
        VehicleSizeBuilder.genericVehicleSize(testVehicleSize);
        fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }

   }

   /**
    * Test that a vehicle width value (-1) below the lower bound (0) throws
    */
   @Test
   public void shouldThrowExceptionVehicleWidthAboveUpperBound() {

      Integer testWidth = 1024;
      Integer testLength = 0;

      JsonNode testVehicleSize = null;
      try {
         testVehicleSize = (JsonNode) XmlUtils.fromXmlS(
               "<VehicleSize><width>" + testWidth + "</width><length>" + testLength + "</length></VehicleSize>",
               JsonNode.class);
      } catch (XmlUtilsException e) {
         fail("XML parsing error:" + e);
      }

      try {
        VehicleSizeBuilder.genericVehicleSize(testVehicleSize);
        fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }

   }

   // VehicleLength tests

   /**
    * Test that undefined vehicle length flag value (0) returns 0
    */
   @Test
   public void shouldReturnZeroVehicleLength() {

      Integer expectedValue = 0;

      Integer testWidth = 0;
      Integer testLength = 0;

      JsonNode testVehicleSize = null;
      try {
         testVehicleSize = (JsonNode) XmlUtils.fromXmlS(
               "<VehicleSize><width>" + testWidth + "</width><length>" + testLength + "</length></VehicleSize>",
               JsonNode.class);
      } catch (XmlUtilsException e) {
         fail("XML parsing error:" + e);
      }

      J2735VehicleSize actualVehicleSize = VehicleSizeBuilder.genericVehicleSize(testVehicleSize);
      Integer actualValue = actualVehicleSize.getLength();

      assertEquals(expectedValue, actualValue);

   }

   /**
    * Test that the minimum vehicle length value (1) returns (1)
    */
   @Test
   public void shouldReturnMinimumVehicleLength() {

      Integer expectedValue = 1;

      Integer testWidth = 0;
      Integer testLength = 1;

      JsonNode testVehicleSize = null;
      try {
         testVehicleSize = (JsonNode) XmlUtils.fromXmlS(
               "<VehicleSize><width>" + testWidth + "</width><length>" + testLength + "</length></VehicleSize>",
               JsonNode.class);
      } catch (XmlUtilsException e) {
         fail("XML parsing error:" + e);
      }

      J2735VehicleSize actualVehicleSize = VehicleSizeBuilder.genericVehicleSize(testVehicleSize);
      Integer actualValue = actualVehicleSize.getLength();

      assertEquals(expectedValue, actualValue);

   }

   /*
    * Test minimum vehicle length corner case value (2) returns (2)
    */
   @Test
   public void shouldReturnCornerCaseMinimumVehicleLength() {

      Integer expectedValue = 2;

      Integer testWidth = 0;
      Integer testLength = 2;

      JsonNode testVehicleSize = null;
      try {
         testVehicleSize = (JsonNode) XmlUtils.fromXmlS(
               "<VehicleSize><width>" + testWidth + "</width><length>" + testLength + "</length></VehicleSize>",
               JsonNode.class);
      } catch (XmlUtilsException e) {
         fail("XML parsing error:" + e);
      }

      J2735VehicleSize actualVehicleSize = VehicleSizeBuilder.genericVehicleSize(testVehicleSize);
      Integer actualValue = actualVehicleSize.getLength();

      assertEquals(expectedValue, actualValue);

   }

   /*
    * Test maximum vehicle length corner case value (4094) returns (4094)
    */
   @Test
   public void shouldReturnCornerCaseMaximumVehicleLength() {

      Integer expectedValue = 4094;

      Integer testWidth = 0;
      Integer testLength = 4094;

      JsonNode testVehicleSize = null;
      try {
         testVehicleSize = (JsonNode) XmlUtils.fromXmlS(
               "<VehicleSize><width>" + testWidth + "</width><length>" + testLength + "</length></VehicleSize>",
               JsonNode.class);
      } catch (XmlUtilsException e) {
         fail("XML parsing error:" + e);
      }

      J2735VehicleSize actualVehicleSize = VehicleSizeBuilder.genericVehicleSize(testVehicleSize);
      Integer actualValue = actualVehicleSize.getLength();

      assertEquals(expectedValue, actualValue);

   }

   /*
    * Test that the maximum vehicle length value (4095) returns (4095)
    */
   @Test
   public void shouldReturnMaximumVehicleLength() {

      Integer expectedValue = 4095;

      Integer testWidth = 0;
      Integer testLength = 4095;

      JsonNode testVehicleSize = null;
      try {
         testVehicleSize = (JsonNode) XmlUtils.fromXmlS(
               "<VehicleSize><width>" + testWidth + "</width><length>" + testLength + "</length></VehicleSize>",
               JsonNode.class);
      } catch (XmlUtilsException e) {
         fail("XML parsing error:" + e);
      }

      J2735VehicleSize actualVehicleSize = VehicleSizeBuilder.genericVehicleSize(testVehicleSize);
      Integer actualValue = actualVehicleSize.getLength();

      assertEquals(expectedValue, actualValue);

   }

   /*
    * Test that a vehicle length value (-1) below the lower bound (0) throws
    * IllegalArgumentException
    */
   @Test
   public void shouldThrowExceptionVehicleLengthBelowLowerBound() {

      Integer testWidth = 0;
      Integer testLength = -1;

      JsonNode testVehicleSize = null;
      try {
         testVehicleSize = (JsonNode) XmlUtils.fromXmlS(
               "<VehicleSize><width>" + testWidth + "</width><length>" + testLength + "</length></VehicleSize>",
               JsonNode.class);
      } catch (XmlUtilsException e) {
         fail("XML parsing error:" + e);
      }

      try {
         VehicleSizeBuilder.genericVehicleSize(testVehicleSize);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }

   }

   /*
    * Test that a vehicle length value (4096) above the upper bound (4095) throws
    * IllegalArgumentException
    */
   @Test
   public void shouldThrowExceptionVehicleLengthAboveUpperBound() {

      Integer testWidth = 0;
      Integer testLength = 4096;

      JsonNode testVehicleSize = null;
      try {
         testVehicleSize = (JsonNode) XmlUtils.fromXmlS(
               "<VehicleSize><width>" + testWidth + "</width><length>" + testLength + "</length></VehicleSize>",
               JsonNode.class);
      } catch (XmlUtilsException e) {
         fail("XML parsing error:" + e);
      }

      try {
         VehicleSizeBuilder.genericVehicleSize(testVehicleSize);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }

   }

}
