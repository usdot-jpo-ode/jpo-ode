package us.dot.its.jpo.ode.plugin.j2735.oss;

import static org.junit.Assert.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;

import org.junit.Test;

public class OssOffsetLLB18Test {

   @Test
   public void testConversion() {
      int expectedValue = 1234;
      BigDecimal testInput = BigDecimal.valueOf(0.000123456);

      assertEquals(expectedValue, OssOffsetLLB18.offsetLLB18(testInput).intValue());
   }

   @Test
   public void testConstructorIsPrivate()
         throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
      Constructor<OssOffsetLLB18> constructor = OssOffsetLLB18.class.getDeclaredConstructor();
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
