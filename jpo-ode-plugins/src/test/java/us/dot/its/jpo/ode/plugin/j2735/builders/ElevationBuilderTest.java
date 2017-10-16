package us.dot.its.jpo.ode.plugin.j2735.builders;

import static org.junit.Assert.*;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ElevationBuilderTest {

   @Test
   public void testConversion() throws JsonProcessingException, IOException {
      ObjectMapper mapper = new ObjectMapper();
      JsonNode testInput = mapper.readTree("32834");
      BigDecimal expectedValue = BigDecimal.valueOf(3283.4);

      assertEquals(expectedValue, ElevationBuilder.genericElevation(testInput));
   }

   @Test
   public void testConstructorIsPrivate()
         throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
      Constructor<ElevationBuilder> constructor = ElevationBuilder.class.getDeclaredConstructor();
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
   public void testConversionReturnNull() throws JsonProcessingException, IOException {
      ObjectMapper mapper = new ObjectMapper();
      JsonNode testInput = mapper.readTree("-4096");
      BigDecimal expectedValue = null;

      assertEquals(expectedValue, ElevationBuilder.genericElevation(testInput));
   }
   
   @Test
   public void testConversionWithinBounds() throws JsonProcessingException, IOException {
      ObjectMapper mapper = new ObjectMapper();
      JsonNode testInput = mapper.readTree("-4095");
      BigDecimal expectedValue = BigDecimal.valueOf(-409.5);

      assertEquals(expectedValue, ElevationBuilder.genericElevation(testInput));
   }
   @Test
   public void testConversionOutOfBoundsLower() throws JsonProcessingException, IOException {
      ObjectMapper mapper = new ObjectMapper();
      JsonNode testInput = mapper.readTree("-4097");
      BigDecimal expectedValue = BigDecimal.valueOf(-409.5);

      assertEquals(expectedValue, ElevationBuilder.genericElevation(testInput));
   }
   @Test
   public void testConversionOutOfBoundsLowerTwo() throws JsonProcessingException, IOException {
      ObjectMapper mapper = new ObjectMapper();
      JsonNode testInput = mapper.readTree("-5097");
      BigDecimal expectedValue = BigDecimal.valueOf(-409.5);

      assertEquals(expectedValue, ElevationBuilder.genericElevation(testInput));
   }
   
   
   @Test
   public void testConversionOutOfBoundsUpper() throws JsonProcessingException, IOException {
      ObjectMapper mapper = new ObjectMapper();
      JsonNode testInput = mapper.readTree("61440");
      BigDecimal expectedValue = BigDecimal.valueOf(6143.9);

      assertEquals(expectedValue, ElevationBuilder.genericElevation(testInput));
   }
   
   
   
   
}
