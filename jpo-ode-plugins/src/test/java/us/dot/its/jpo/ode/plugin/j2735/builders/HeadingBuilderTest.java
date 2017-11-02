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
