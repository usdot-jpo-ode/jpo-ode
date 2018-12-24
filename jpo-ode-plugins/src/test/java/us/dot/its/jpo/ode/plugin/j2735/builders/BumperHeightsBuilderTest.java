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
import us.dot.its.jpo.ode.plugin.j2735.J2735BumperHeights;
import us.dot.its.jpo.ode.util.XmlUtils;
import us.dot.its.jpo.ode.util.XmlUtils.XmlUtilsException;

/**
 * -- Summary -- JUnit test class for BumperHeightsBuilder
 * 
 * Verifies that BumperHeightsBuilder properly converts a generic JsonNode
 * object into a compliant J2735BumperHeights object per ASN.1 specifications.
 * 
 * -- Documentation --
 * 
 * Data Element: DE_BumperHeight Use: The DE_Bumper Height data element conveys
 * the height of one of the bumpers of the vehicle or object. In cases of
 * vehicles with complex bumper shapes, the center of the mass of the bumper
 * (where the bumper can best absorb an impact) should be used. ASN.1
 * Representation: BumperHeight ::= INTEGER (0..127) -- in units of 0.01 meters
 * from ground surface.
 */
public class BumperHeightsBuilderTest {

   /**
    * Test that minimum bumper height 0 returns (0.00)
    */
   @Test
   public void shouldReturnMinimumBumperHeights() {

      JsonNode testBumperHeights = null;

      int frontInput = 0;
      int rearInput = 0;
      try {
         testBumperHeights = (JsonNode) XmlUtils.fromXmlS(
               "<heights><front>" + frontInput + "</front><rear>" + rearInput + "</rear></heights>", JsonNode.class);
      } catch (XmlUtilsException e) {
         fail("XML parsing error:" + e);
      }

      BigDecimal expectedValue = BigDecimal.ZERO.setScale(2);

      J2735BumperHeights BH = BumperHeightsBuilder.genericBumperHeights(testBumperHeights);

      assertEquals("Minimum front bumper height 0 should return 0:", expectedValue, BH.getFront());
      assertEquals("Minimum rear bumper height 0 should return 0:", expectedValue, BH.getRear());

   }

   /**
    * Test that maximum bumper height 127 returns 1.27
    */
   @Test
   public void shouldReturnMaximumBumperHeights() {

      JsonNode testBumperHeights = null;

      int frontInput = 127;
      int rearInput = 127;
      try {
         testBumperHeights = (JsonNode) XmlUtils.fromXmlS(
               "<heights><front>" + frontInput + "</front><rear>" + rearInput + "</rear></heights>", JsonNode.class);
      } catch (XmlUtilsException e) {
         fail("XML parsing error:" + e);
      }

      BigDecimal expectedValue = BigDecimal.valueOf(1.27);

      J2735BumperHeights BH = BumperHeightsBuilder.genericBumperHeights(testBumperHeights);

      assertEquals("Maximum front bumper height 127 should return 1.27:", expectedValue, BH.getFront());
      assertEquals("Maximum rear bumper height 127 should return 1.27:", expectedValue, BH.getRear());

   }

   /**
    * Test that known bumper height value 85 returns 0.85
    */
   @Test
   public void shouldReturnKnownBumperHeights() {

      JsonNode testBumperHeights = null;

      int frontInput = 85;
      int rearInput = 85;
      try {
         testBumperHeights = (JsonNode) XmlUtils.fromXmlS(
               "<heights><front>" + frontInput + "</front><rear>" + rearInput + "</rear></heights>", JsonNode.class);
      } catch (XmlUtilsException e) {
         fail("XML parsing error:" + e);
      }

      BigDecimal expectedValue = BigDecimal.valueOf(0.85);

      J2735BumperHeights BH = BumperHeightsBuilder.genericBumperHeights(testBumperHeights);

      assertEquals("Maximum front bumper height 85 should return 0.85:", expectedValue, BH.getFront());
      assertEquals("Maximum rear bumper height 85 should return 0.85:", expectedValue, BH.getRear());

   }

   /**
    * Test that a front bumper height value below 0 throws IllegalArgumentException
    * Rear bumper height is the independent variable for this test and is set to 0
    */
   @Test
   public void shouldThrowExceptionBumperHeightsFrontBelowLowerBound() {

      JsonNode testBumperHeights = null;

      int frontInput = -1;
      int rearInput = 0;
      try {
         testBumperHeights = (JsonNode) XmlUtils.fromXmlS(
               "<heights><front>" + frontInput + "</front><rear>" + rearInput + "</rear></heights>", JsonNode.class);
      } catch (XmlUtilsException e) {
         fail("XML parsing error:" + e);
      }

      try {
         BumperHeightsBuilder.genericBumperHeights(testBumperHeights);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }

   }

   /**
    * Test that a rear bumper height value below 0 throws IllegalArgumentException
    * front bumper height is the independent variable for this test and is set to 0
    */
   @Test
   public void shouldThrowExceptionBumperHeightsRearBelowLowerBound() {

      JsonNode testBumperHeights = null;

      int frontInput = 0;
      int rearInput = -1;
      try {
         testBumperHeights = (JsonNode) XmlUtils.fromXmlS(
               "<heights><front>" + frontInput + "</front><rear>" + rearInput + "</rear></heights>", JsonNode.class);
      } catch (XmlUtilsException e) {
         fail("XML parsing error:" + e);
      }

      try {
         BumperHeightsBuilder.genericBumperHeights(testBumperHeights);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }

   }

   /**
    * Test that a front bumper height value above 127 throws
    * IllegalArgumentException rear bumper height is the independent variable for
    * this test and is set to 0
    */
   @Test
   public void shouldThrowExceptionBumperHeightsFrontAboveUpperBound() {

      JsonNode testBumperHeights = null;

      int frontInput = 128;
      int rearInput = 0;
      try {
         testBumperHeights = (JsonNode) XmlUtils.fromXmlS(
               "<heights><front>" + frontInput + "</front><rear>" + rearInput + "</rear></heights>", JsonNode.class);
      } catch (XmlUtilsException e) {
         fail("XML parsing error:" + e);
      }

      try {
         BumperHeightsBuilder.genericBumperHeights(testBumperHeights);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }

   }

   /**
    * Test that a rear bumper height value above 127 throws
    * IllegalArgumentException front bumper height is the independent variable for
    * this test and is set to 0
    */
   @Test
   public void shouldThrowExceptionBumperHeightsRearAboveUpperBound() {

      JsonNode testBumperHeights = null;

      int frontInput = 0;
      int rearInput = 128;
      try {
         testBumperHeights = (JsonNode) XmlUtils.fromXmlS(
               "<heights><front>" + frontInput + "</front><rear>" + rearInput + "</rear></heights>", JsonNode.class);
      } catch (XmlUtilsException e) {
         fail("XML parsing error:" + e);
      }

      try {
         BumperHeightsBuilder.genericBumperHeights(testBumperHeights);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }

   }

   @Test
   public void testConstructorIsPrivate()
         throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
      Constructor<BumperHeightsBuilder> constructor = BumperHeightsBuilder.class.getDeclaredConstructor();
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
