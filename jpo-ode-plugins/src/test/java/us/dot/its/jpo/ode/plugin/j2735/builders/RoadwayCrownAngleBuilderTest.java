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
import java.math.BigDecimal;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;

public class RoadwayCrownAngleBuilderTest {

   @Test
   public void testInZeroNegitive() throws JsonProcessingException, IOException {
      BigDecimal testInput = BigDecimal.valueOf(-0.14);
      int expectedValue = 0;

      assertEquals(expectedValue, RoadwayCrownAngleBuilder.roadwayCrownAngle(testInput));
   }

   @Test
   public void testInZeroPositive() throws JsonProcessingException, IOException {
      BigDecimal testInput = BigDecimal.valueOf(0.14);
      int expectedValue = 0;

      assertEquals(expectedValue, RoadwayCrownAngleBuilder.roadwayCrownAngle(testInput));
   }

   @Test
   public void testNull() throws JsonProcessingException, IOException {
      BigDecimal testInput = null;
      int expectedValue = 128;

      assertEquals(expectedValue, RoadwayCrownAngleBuilder.roadwayCrownAngle(testInput));
   }

   @Test
   public void testInBoundUpper() throws JsonProcessingException, IOException {
      BigDecimal testInput = BigDecimal.valueOf(38.1);
      int expectedValue = 127;

      assertEquals(expectedValue, RoadwayCrownAngleBuilder.roadwayCrownAngle(testInput));
   }

   @Test
   public void testInBoundLower() throws JsonProcessingException, IOException {
      BigDecimal testInput = BigDecimal.valueOf(-38.1);
      int expectedValue = -127;

      assertEquals(expectedValue, RoadwayCrownAngleBuilder.roadwayCrownAngle(testInput));
   }

   @Test
   public void testOutOfBoundAbove() throws JsonProcessingException, IOException {
      BigDecimal testInput = BigDecimal.valueOf(39.1);

      try {
         RoadwayCrownAngleBuilder.roadwayCrownAngle(testInput);
         fail("Expected IllegalArgumentException");
      } catch (Exception e) {
         assertTrue(e instanceof IllegalArgumentException);
      }
   }

   @Test
   public void testOutOfBoundBelow() throws JsonProcessingException, IOException {
      BigDecimal testInput = BigDecimal.valueOf(-39.1);

      try {
         RoadwayCrownAngleBuilder.roadwayCrownAngle(testInput);
         fail("Expected IllegalArgumentException");
      } catch (Exception e) {
         assertTrue(e instanceof IllegalArgumentException);
      }
   }

}
