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

import us.dot.its.jpo.ode.j2735.dsrc.Longitude;

public class LongitudeBuilderTest {

   @Test
   public void testConversion() throws JsonProcessingException, IOException {
      ObjectMapper mapper = new ObjectMapper();
      JsonNode testInput = mapper.readTree("251234567");
      BigDecimal expectedValue = BigDecimal.valueOf(25.1234567);

      assertEquals(expectedValue, LongitudeBuilder.genericLongitude(testInput));
   }
   
   @Test
   public void testConversionDeticatedNullValue() throws JsonProcessingException, IOException {
      ObjectMapper mapper = new ObjectMapper();
      JsonNode testInput = mapper.readTree("1800000001");
      BigDecimal expectedValue = null;

      assertEquals(expectedValue, LongitudeBuilder.genericLongitude(testInput));
   }
   
   @Test
   public void testConversionNullEntry() throws JsonProcessingException, IOException {
      JsonNode testInput = null;
      BigDecimal expectedValue = null;

      assertEquals(expectedValue, LongitudeBuilder.genericLongitude(testInput));
   }
   
   @Test
   public void testBigDecimal() throws JsonProcessingException, IOException {
      
      BigDecimal testInput = new BigDecimal(1.0);
      
      Longitude expectedValue = new Longitude(10000000);
      
     

      assertEquals(expectedValue, LongitudeBuilder.longitude(testInput));
      
      
      
   }
   
   
   
   
   @Test
   public void testConstructorIsPrivate()
         throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
      Constructor<LongitudeBuilder> constructor = LongitudeBuilder.class.getDeclaredConstructor();
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
