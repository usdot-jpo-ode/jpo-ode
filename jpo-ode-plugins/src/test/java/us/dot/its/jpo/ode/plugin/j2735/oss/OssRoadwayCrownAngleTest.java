package us.dot.its.jpo.ode.plugin.j2735.oss;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;

import org.junit.Test;

public class OssRoadwayCrownAngleTest {

   @Test
   public void testConversion() {
      int expectedValue = 54;
      BigDecimal testInput = BigDecimal.valueOf(16.2);

      assertEquals(expectedValue, OssRoadwayCrownAngle.roadwayCrownAngle(testInput).intValue());
   }

   @Test
   public void testConstructorIsPrivate()
         throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
      Constructor<OssRoadwayCrownAngle> constructor = OssRoadwayCrownAngle.class.getDeclaredConstructor();
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
