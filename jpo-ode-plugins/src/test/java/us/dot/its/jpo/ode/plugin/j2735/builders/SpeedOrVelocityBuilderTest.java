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
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SpeedOrVelocityBuilderTest {

   
   @Test
   public void testInZeroPositive() throws JsonProcessingException, IOException {
      ObjectMapper mapper = new ObjectMapper();
      JsonNode testInput = mapper.readTree("1234");
      
      
      BigDecimal expectedValue = BigDecimal.valueOf(24.68);

      assertEquals(expectedValue, SpeedOrVelocityBuilder.genericVelocity(testInput));
   }
   
   @Test
   public void testOutOfBoundAbove() throws JsonProcessingException, IOException {
      ObjectMapper mapper = new ObjectMapper();
      JsonNode testInput = mapper.readTree("8192");

      try {
         SpeedOrVelocityBuilder.genericSpeed(testInput);
         fail("Expected IllegalArgumentException");
      } catch (Exception e) {
         assertTrue(e instanceof IllegalArgumentException);
      }
   }
   
   @Test
   public void testOutOfBoundBelow() throws JsonProcessingException, IOException {
      ObjectMapper mapper = new ObjectMapper();
      JsonNode testInput = mapper.readTree("-1");

      try {
         SpeedOrVelocityBuilder.genericSpeed(testInput);
         fail("Expected IllegalArgumentException");
      } catch (Exception e) {
         assertTrue(e instanceof IllegalArgumentException);
      }
   }
   @Test
   public void testEightOneNineOne() throws JsonProcessingException, IOException {
      ObjectMapper mapper = new ObjectMapper();
      JsonNode testInput = mapper.readTree("8191");

      BigDecimal expectedValue = null;

      assertEquals(expectedValue, SpeedOrVelocityBuilder.genericSpeed(testInput));
   }
}

