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

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.RoundingMode;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import us.dot.its.jpo.ode.util.JsonUtils;

public class AngleBuilderTest {

   /**
    * Tests that the minimum input angle (0) returns the minimum decimal value
    * (0)
    * 
    * @throws IOException
    * @throws JsonProcessingException
    */
   @Test
   public void shouldReturnZeroAngle() throws JsonProcessingException, IOException {

      ObjectMapper mapper = new ObjectMapper();
      JsonNode testInput = mapper.readTree("0");

      BigDecimal expectedValue = BigDecimal.ZERO.setScale(4);

      BigDecimal actualValue = AngleBuilder.genericAngle(testInput);

      assertEquals(expectedValue, actualValue);

   }

   /**
    * Tests that the max input angle (28799) returns the max decimal value
    * (359.9875)
    * 
    * @throws IOException
    * @throws JsonProcessingException
    */
   @Test
   public void shouldReturnMaxAngle() throws JsonProcessingException, IOException {

      ObjectMapper mapper = new ObjectMapper();
      JsonNode testInput = mapper.readTree("28799");

      BigDecimal expectedValue = BigDecimal.valueOf(359.9875);

      BigDecimal actualValue = AngleBuilder.genericAngle(testInput);

      assertEquals(expectedValue, actualValue);

   }

   /**
    * Tests that known input angle (14400) returns the max decimal value (180.0)
    * 
    * @throws IOException
    * @throws JsonProcessingException
    */
   @Test
   public void shouldReturnKnownAngle() throws JsonProcessingException, IOException {

      ObjectMapper mapper = new ObjectMapper();
      JsonNode testInput = mapper.readTree("14400");

      BigDecimal expectedValue = BigDecimal.valueOf(180).setScale(4);

      BigDecimal actualValue = AngleBuilder.genericAngle(testInput);

      assertEquals(expectedValue, actualValue);

   }

   /**
    * Tests that input angle (28800) returns (null) per ASN.1 specification: "A
    * value of 28800 shall be used when Angle is unavailable"
    * 
    * @throws IOException
    * @throws JsonProcessingException
    */
   @Test
   public void shouldReturnNullAngle() throws JsonProcessingException, IOException {

      ObjectMapper mapper = new ObjectMapper();
      JsonNode testInput = mapper.readTree("28800");
      BigDecimal expectedValue = null;

      BigDecimal actualValue = AngleBuilder.genericAngle(testInput);

      assertEquals(expectedValue, actualValue);

   }

   /**
    * Test that an input angle greater than 28800 throws exception
    * 
    * @throws IOException
    * @throws JsonProcessingException
    */
   @Test
   public void shouldThrowExceptionAboveUpperBound() throws JsonProcessingException, IOException {

      ObjectMapper mapper = new ObjectMapper();
      JsonNode testInput = mapper.readTree("28801");

      try {
         AngleBuilder.genericAngle(testInput);
         fail("Expected IllegalArgumentException");
      } catch (Exception e) {
         assertTrue(e.getClass().equals(IllegalArgumentException.class));
      }
   }

   /**
    * Test that an input angle less than 0 throws exception
    * 
    * @throws IOException
    * @throws JsonProcessingException
    */
   @Test
   public void shouldThrowExceptionBelowLowerBound() throws JsonProcessingException, IOException {

      ObjectMapper mapper = new ObjectMapper();
      JsonNode testInput = mapper.readTree("-1");

      try {
         AngleBuilder.genericAngle(testInput);
         fail("Expected IllegalArgumentException");
      } catch (Exception e) {
         assertTrue(e.getClass().equals(IllegalArgumentException.class));
      }
   }

   @Test
   public void testConstructorIsPrivate()
         throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
      Constructor<AngleBuilder> constructor = AngleBuilder.class.getDeclaredConstructor();
      assertTrue(Modifier.isPrivate(constructor.getModifiers()));
      constructor.setAccessible(true);
      try {
         constructor.newInstance();
         fail("Expected IllegalAccessException.class");
      } catch (Exception e) {
         assertEquals(InvocationTargetException.class, e.getClass());
      }
   }
   
   @Test
   public void testJsonNode() throws JsonProcessingException, IOException {

      

      JsonNode expectedValue = JsonUtils.newObjectNode("angle", 
              BigDecimal.valueOf(25).divide(BigDecimal.valueOf(0.0125), 0, RoundingMode.HALF_UP).intValue());

      JsonNode actualValue = AngleBuilder.angle(25);

      assertEquals(expectedValue, actualValue);

   }
   
   @Test
   public void testlongToDecimalNull() throws JsonProcessingException, IOException {

      

	   BigDecimal expectedValue = null;

      BigDecimal actualValue = AngleBuilder.longToDecimal((long)28800);

      assertEquals(expectedValue, actualValue);

   }

}
