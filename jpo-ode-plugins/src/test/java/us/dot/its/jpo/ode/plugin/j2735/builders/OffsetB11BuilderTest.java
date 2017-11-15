package us.dot.its.jpo.ode.plugin.j2735.builders;

import static org.junit.Assert.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;

import org.junit.Test;

public class OffsetB11BuilderTest {

   @Test
   public void testConversion() {
      int expectedValue = 312;
      BigDecimal testInput = BigDecimal.valueOf(3.124);

      assertEquals(expectedValue,OffsetB11Builder.offsetB11(testInput));
   }

   @Test
   public void testConstructorIsPrivate()
         throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
      Constructor<OffsetB11Builder> constructor = OffsetB11Builder.class.getDeclaredConstructor();
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
