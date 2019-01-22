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

import java.io.IOException;
import java.math.BigDecimal;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import us.dot.its.jpo.ode.util.JsonUtils;

public class HeadingBuilderTest {
   
   // CoarseHeading ::= INTEGER (0..240)
   // -- Where the LSB is in units of 1.5 degrees
   // -- over a range of 0~358.5 degrees
   // -- the value 240 shall be used for unavailable

   /**
    * Test that minimum coarse heading (0) returns correct heading angle (0.0)
    */
   @Test
   public void shouldReturnCoarseHeadingMin() throws JsonProcessingException, IOException{
      
       BigDecimal expectedValue = BigDecimal.ZERO.setScale(1);

       ObjectNode testHeading = JsonUtils.newNode().put("angle", "0");
       BigDecimal actualValue = HeadingBuilder.genericCoarseHeading(testHeading.get("angle"));

       assertEquals(expectedValue, actualValue);

   }
   /**
    * Test that maximum coarse heading (239) returns correct heading angle (358.5)
    */
   @Test
   public void shouldReturnCoarseHeadingMax() throws JsonProcessingException, IOException{
       ObjectMapper mapper = new ObjectMapper();
       
       BigDecimal expectedValue = BigDecimal.valueOf(358.5);

       JsonNode testHeading = mapper.readTree("239");
       BigDecimal actualValue = HeadingBuilder.genericCoarseHeading(testHeading);

       assertEquals(expectedValue, actualValue);
   }
   
   /**
    * Test that undefined coarse heading flag (240) returns (null)
    */
   @Test
   public void shouldReturnCoarseHeadingUndefined() throws JsonProcessingException, IOException{
       ObjectMapper mapper = new ObjectMapper();
       BigDecimal expectedValue = null;

       JsonNode testHeading = mapper.readTree("240");
       BigDecimal actualValue = HeadingBuilder.genericCoarseHeading(testHeading);

       assertEquals(expectedValue, actualValue);
       
   }
   
   /**
    * Test that known coarse heading (11) returns (16.5)
    */
   @Test
   public void shouldReturnCoarseHeadingKnown() throws JsonProcessingException, IOException{
      ObjectMapper mapper = new ObjectMapper();
       
       BigDecimal expectedValue = BigDecimal.valueOf(16.5);
       
       JsonNode testHeading = mapper.readTree("11");
       BigDecimal actualValue = HeadingBuilder.genericCoarseHeading(testHeading);
       
       assertEquals(expectedValue, actualValue);
       
   }

   /**
    * Test that a coarse heading greater than 240 throws exception
    */
   @Test
   public void shouldThrowExceptionHeadingOutOfBoundsHigh() throws JsonProcessingException, IOException{
      ObjectMapper mapper = new ObjectMapper();
      
       JsonNode testHeading = mapper.readTree("241");

       try {
          HeadingBuilder.genericCoarseHeading(testHeading);
           fail("Expected IllegalArgumentException");
       } catch (RuntimeException e) {
           assertEquals(IllegalArgumentException.class, e.getClass());
       }
       
   }

   /**
    * Test that a coarse heading less than 0 throws exception
    */
   @Test
   public void shouldThrowExceptionHeadingOutOfBoundsLow() throws JsonProcessingException, IOException{
      ObjectMapper mapper = new ObjectMapper();
      
       JsonNode testHeading = mapper.readTree("-1");
       
       try {
          HeadingBuilder.genericCoarseHeading(testHeading);
           fail("Expected IllegalArgumentException");
       } catch (RuntimeException e) {
           assertEquals(IllegalArgumentException.class, e.getClass());
       }
       
   }
   

   
}
