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

import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * -- Summary -- Test class for TimeOffsetBuilder
 * 
 * Verifies correct conversion from JsonNode to compliant-J2735TimeOffSett
 * 
 * -- Documentation -- Data Element: DE_TimeOffset Use: The DE_TimeOffset data
 * element is used to convey an offset in time from a known point. It is
 * typically used to relate a set of measurements made in the recent past, such
 * as a set of path points. The above methodology is used when the offset is
 * incorporated in data frames other than DF_PathHistoryPoint. Refer to the Use
 * paragraph of DF_PathHistory for the methodology to calculate this data
 * element for use in DF_PathHistoryPoint. ASN.1 Representation: TimeOffset ::=
 * INTEGER (1..65535) -- LSB units of of 10 mSec, -- with a range of 0.01
 * seconds to 10 minutes and 55.34 seconds -- a value of 65534 to be used for
 * 655.34 seconds or greater -- a value of 65535 to be unavailable
 */
public class TimeOffsetBuilderTest {

   /**
    * Test that an undefined time offset flag value 65535 returns null
    */
   @Test
   public void shouldReturnUndefinedTimeOffset() throws JsonProcessingException, IOException {

      BigDecimal expectedValue = null;

      ObjectMapper mapper = new ObjectMapper();
      JsonNode testTimeOffset = mapper.readTree("65535");

      BigDecimal actualValue = TimeOffsetBuilder.genericTimeOffset(testTimeOffset);

      assertEquals(expectedValue, actualValue);

   }

   /**
    * Test that a minimum time offset of (1) returns (0.01)
    */
   @Test
   public void shouldReturnMinimumTimeOffset() throws JsonProcessingException, IOException {

      BigDecimal expectedValue = BigDecimal.valueOf(0.01);
      ObjectMapper mapper = new ObjectMapper();
      JsonNode testTimeOffset = mapper.readTree("1");

      BigDecimal actualValue = TimeOffsetBuilder.genericTimeOffset(testTimeOffset);

      assertEquals(expectedValue, actualValue);

   }

   /**
    * Test that a maximum time offset of (65534) returns (655.34)
    */
   @Test
   public void shouldReturnMaximumTimeOffset() throws JsonProcessingException, IOException {

      ObjectMapper mapper = new ObjectMapper();
      JsonNode testTimeOffset = mapper.readTree("65534");
      BigDecimal expectedValue = BigDecimal.valueOf(655.34);

      BigDecimal actualValue = TimeOffsetBuilder.genericTimeOffset(testTimeOffset);

      assertEquals(expectedValue, actualValue);
   }

   //
   /**
    * Test that a known time offset of (15234) returns (152.34)
    */
   @Test
   public void shouldReturnKnownTimeOffset() throws JsonProcessingException, IOException {

      ObjectMapper mapper = new ObjectMapper();
      BigDecimal expectedValue = BigDecimal.valueOf(152.34);
      JsonNode testTimeOffset = mapper.readTree("15234");

      BigDecimal actualValue = TimeOffsetBuilder.genericTimeOffset(testTimeOffset);

      assertEquals(expectedValue, actualValue);
   }

   /**
    * Test that a time offset value below the lower bound (1) throws
    * IllegalArgumentException
    */
   @Test
   public void shouldThrowExceptionTimeOffsetBelowLowerBound() throws JsonProcessingException, IOException {

      ObjectMapper mapper = new ObjectMapper();
      JsonNode testTimeOffset = mapper.readTree("0");

      try {
         TimeOffsetBuilder.genericTimeOffset(testTimeOffset);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }

   }

   /**
    * Test that a time offset value (65536) above the upper bound (65535) is
    * reduced and returned as (655.34)
    */
   @Test
   public void shouldReduceTimeOffsetAboveUpperBound() throws JsonProcessingException, IOException {

      ObjectMapper mapper = new ObjectMapper();
      BigDecimal expectedValue = BigDecimal.valueOf(655.34);
      JsonNode testTimeOffset = mapper.readTree("65536");

      BigDecimal actualValue = TimeOffsetBuilder.genericTimeOffset(testTimeOffset);

      assertEquals(expectedValue, actualValue);

   }

   @Test
   public void testConstructorIsPrivate()
         throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
      Constructor<TimeOffsetBuilder> constructor = TimeOffsetBuilder.class.getDeclaredConstructor();
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
