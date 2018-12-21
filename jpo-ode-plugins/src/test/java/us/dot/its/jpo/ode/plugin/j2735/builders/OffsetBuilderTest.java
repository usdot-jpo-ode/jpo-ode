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

import com.fasterxml.jackson.databind.node.ObjectNode;

import us.dot.its.jpo.ode.util.JsonUtils;

public class OffsetBuilderTest {

   @Test
   public void shouldReturnMinimumOffsetB12() {

       ObjectNode testInput = JsonUtils.newNode().put("offset", -2047);
       BigDecimal expectedValue = BigDecimal.valueOf(-20.47);

       BigDecimal actualValue = OffsetBuilder.genericOffset_B12(testInput.get("offset"));

       assertEquals(expectedValue, actualValue);

   }

   /**
    * Test that a maximum value of +2047 returns +20.47
    */
   @Test
   public void shouldReturnMaximumOffsetB12() {

       ObjectNode testInput = JsonUtils.newNode().put("offset", 2047);
       BigDecimal expectedValue = BigDecimal.valueOf(20.47);
       
       BigDecimal actualValue = OffsetBuilder.genericOffset_B12(testInput.get("offset"));

       assertEquals(expectedValue, actualValue);

   }

   /**
    * Test that a known value of 1234 returns 12.34
    */
   @Test
   public void shouldReturnKnownOffsetB12() {
      
       ObjectNode testInput = JsonUtils.newNode().put("offset", 1234);
       BigDecimal expectedValue = BigDecimal.valueOf(12.34);
       
       BigDecimal actualValue = OffsetBuilder.genericOffset_B12(testInput.get("offset"));

       assertEquals(expectedValue, actualValue);

   }

   /**
    * Test that undefined flag -2048 returns null
    */
   @Test
   public void shouldReturnUndefinedOffsetB12() {
       
       ObjectNode testInput = JsonUtils.newNode().put("offset", -2048);
       BigDecimal expectedValue = null;
       
       BigDecimal actualValue = OffsetBuilder.genericOffset_B12(testInput.get("offset"));

       assertEquals(expectedValue, actualValue);
   }

   /**
    * Test that a value (2048) above the upper limit value 2047 throws IllegalArgumentException
    */
   @Test
   public void shouldThrowExceptionOffsetB12AboveUpperBound() {

      ObjectNode testInput = JsonUtils.newNode().put("offset", 2048);
       
       try {
           OffsetBuilder.genericOffset_B12(testInput.get("offset"));
           fail("Expected IllegalArgumentException");
       } catch (RuntimeException e) {
           assertEquals(IllegalArgumentException.class, e.getClass());
       }

   }

   /**
    * Test that a value (-2049) below the lower limit value -2048 throws IllegalArgumentException
    */
   @Test
   public void shouldThrowExceptionOffsetB12BelowLowerBound() {

      ObjectNode testInput = JsonUtils.newNode().put("offset", -2049);

       try {
          OffsetBuilder.genericOffset_B12(testInput.get("offset"));
           fail("Expected IllegalArgumentException");
       } catch (Exception e) {
           assertEquals(IllegalArgumentException.class, e.getClass());
       }

   }
   
   // VertOffset-B07 tests

   /**
    * Test that a minimum vertical offset of -63 returns -6.3
    */
   @Test
   public void shouldReturnMinimumVertOffsetB07() {

      ObjectNode testInput = JsonUtils.newNode().put("offset", -63);
       BigDecimal expectedValue = BigDecimal.valueOf(-6.3);

       BigDecimal actualValue = OffsetBuilder.genericVertOffset_B07(testInput.get("offset"));

       assertEquals(expectedValue, actualValue);

   }

   /**
    * Test that a maximum vertical offset of 63 returns 6.3
    */
   @Test
   public void shouldReturnMaximumVertOffsetB07() {
       
       ObjectNode testInput = JsonUtils.newNode().put("offset", 63);
       BigDecimal expectedValue = BigDecimal.valueOf(6.3);

       BigDecimal actualValue = OffsetBuilder.genericVertOffset_B07(testInput.get("offset"));

       assertEquals(expectedValue, actualValue);
   }

   /**
    * Test that a known vertical offset of 25 returns 2.5
    */
   @Test
   public void shouldReturnKnownVertOffsetB07() {
       
       ObjectNode testInput = JsonUtils.newNode().put("offset", 25);
       BigDecimal expectedValue = BigDecimal.valueOf(2.5);

       BigDecimal actualValue = OffsetBuilder.genericVertOffset_B07(testInput.get("offset"));

       assertEquals(expectedValue, actualValue);

   }

   /**
    * Test that a flag value offset of -64 indicates undefined by returning null
    */
   @Test
   public void shouldReturnUndefinedVertOffsetB07() {
       
       ObjectNode testInput = JsonUtils.newNode().put("offset", -64);
       BigDecimal expectedValue = null;

       BigDecimal actualValue = OffsetBuilder.genericVertOffset_B07(testInput.get("offset"));

       assertEquals(expectedValue, actualValue);

   }

   /**
    * Test that an offset value (64) above the upper bound 63 is reduced to 63 and returned as 6.3
    */
   @Test
   public void shouldReduceVertOffsetB07AboveUpperBoundToUpperBound() {
       
       ObjectNode testInput = JsonUtils.newNode().put("offset", 64);
       BigDecimal expectedValue = BigDecimal.valueOf(6.3);

       BigDecimal actualValue = OffsetBuilder.genericVertOffset_B07(testInput.get("offset"));

       assertEquals(expectedValue, actualValue);

   }

   /**
    * Test that an offset below the lower bound -63 is reduced to -63 and returned as -6.3
    */
   @Test
   public void shouldIncreaseVertOffsetB07BelowLowerBoundToLowerBound() {
       
       ObjectNode testInput = JsonUtils.newNode().put("offset", -65);
       BigDecimal expectedValue = BigDecimal.valueOf(-6.3);

       BigDecimal actualValue = OffsetBuilder.genericVertOffset_B07(testInput.get("offset"));

       assertEquals(expectedValue, actualValue);

   }
   
   ///////////
   @Test
   public void shouldReturnMinimumOffsetB09() {

       ObjectNode testInput = JsonUtils.newNode().put("offset", -255);
       BigDecimal expectedValue = BigDecimal.valueOf(-2.55);

       BigDecimal actualValue = OffsetBuilder.genericOffset_B09(testInput.get("offset"));

       assertEquals(expectedValue, actualValue);

   }

   @Test
   public void shouldReturnMaximumOffsetB09() {

       ObjectNode testInput = JsonUtils.newNode().put("offset", 255);
       BigDecimal expectedValue = BigDecimal.valueOf(2.55);
       
       BigDecimal actualValue = OffsetBuilder.genericOffset_B09(testInput.get("offset"));

       assertEquals(expectedValue, actualValue);

   }

   @Test
   public void shouldReturnKnownOffsetB09() {
      
       ObjectNode testInput = JsonUtils.newNode().put("offset", 123);
       BigDecimal expectedValue = BigDecimal.valueOf(1.23);
       
       BigDecimal actualValue = OffsetBuilder.genericOffset_B09(testInput.get("offset"));

       assertEquals(expectedValue, actualValue);

   }

   @Test
   public void shouldReturnUndefinedOffsetB09() {
       
       ObjectNode testInput = JsonUtils.newNode().put("offset", -256);
       BigDecimal expectedValue = null;
       
       BigDecimal actualValue = OffsetBuilder.genericOffset_B09(testInput.get("offset"));

       assertEquals(expectedValue, actualValue);
   }

   @Test
   public void shouldThrowExceptionOffsetB09AboveUpperBound() {

      ObjectNode testInput = JsonUtils.newNode().put("offset", 256);
       
       try {
           OffsetBuilder.genericOffset_B09(testInput.get("offset"));
           fail("Expected IllegalArgumentException");
       } catch (RuntimeException e) {
           assertEquals(IllegalArgumentException.class, e.getClass());
       }

   }

   @Test
   public void shouldThrowExceptionOffsetB09BelowLowerBound() {

      ObjectNode testInput = JsonUtils.newNode().put("offset", -257);

       try {
          OffsetBuilder.genericOffset_B09(testInput.get("offset"));
           fail("Expected IllegalArgumentException");
       } catch (Exception e) {
           assertEquals(IllegalArgumentException.class, e.getClass());
       }

   }
   
   ///////////
   
   @Test
   public void shouldReturnMinimumOffsetB10() {

       ObjectNode testInput = JsonUtils.newNode().put("offset", -511);
       BigDecimal expectedValue = BigDecimal.valueOf(-5.11);

       BigDecimal actualValue = OffsetBuilder.genericOffset_B10(testInput.get("offset"));

       assertEquals(expectedValue, actualValue);

   }

   @Test
   public void shouldReturnMaximumOffsetB10() {

       ObjectNode testInput = JsonUtils.newNode().put("offset", 511);
       BigDecimal expectedValue = BigDecimal.valueOf(5.11);
       
       BigDecimal actualValue = OffsetBuilder.genericOffset_B10(testInput.get("offset"));

       assertEquals(expectedValue, actualValue);

   }

   @Test
   public void shouldReturnKnownOffsetB10() {
      
       ObjectNode testInput = JsonUtils.newNode().put("offset", 213);
       BigDecimal expectedValue = BigDecimal.valueOf(2.13);
       
       BigDecimal actualValue = OffsetBuilder.genericOffset_B10(testInput.get("offset"));

       assertEquals(expectedValue, actualValue);

   }

   @Test
   public void shouldReturnUndefinedOffsetB10() {
       
       ObjectNode testInput = JsonUtils.newNode().put("offset", -512);
       BigDecimal expectedValue = null;
       
       BigDecimal actualValue = OffsetBuilder.genericOffset_B10(testInput.get("offset"));

       assertEquals(expectedValue, actualValue);
   }

   @Test
   public void shouldThrowExceptionOffsetB10AboveUpperBound() {

      ObjectNode testInput = JsonUtils.newNode().put("offset", 512);
       
       try {
           OffsetBuilder.genericOffset_B10(testInput.get("offset"));
           fail("Expected IllegalArgumentException");
       } catch (RuntimeException e) {
           assertEquals(IllegalArgumentException.class, e.getClass());
       }

   }

   @Test
   public void shouldThrowExceptionOffsetB10BelowLowerBound() {

      ObjectNode testInput = JsonUtils.newNode().put("offset", -513);

       try {
          OffsetBuilder.genericOffset_B10(testInput.get("offset"));
           fail("Expected IllegalArgumentException");
       } catch (Exception e) {
           assertEquals(IllegalArgumentException.class, e.getClass());
       }

   }
   
   ///////////
   
   @Test
   public void testConstructorIsPrivate() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
     Constructor<OffsetBuilder > constructor = OffsetBuilder.class.getDeclaredConstructor();
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
