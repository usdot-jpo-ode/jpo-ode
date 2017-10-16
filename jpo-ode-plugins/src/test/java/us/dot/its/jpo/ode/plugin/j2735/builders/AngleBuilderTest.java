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

}
