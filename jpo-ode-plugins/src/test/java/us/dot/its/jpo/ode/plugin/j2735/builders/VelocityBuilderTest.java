package us.dot.its.jpo.ode.plugin.j2735.builders;

import static org.junit.Assert.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;

import org.junit.Test;

public class VelocityBuilderTest {

   @Test
   public void testLowerBound() {
      
      BigDecimal testInput = BigDecimal.ZERO;
      int expectedValue = 0;
      assertEquals(expectedValue, VelocityBuilder.velocity(testInput));
   }
   
   @Test
   public void testLowerBoundPlus1() {
      
      BigDecimal testInput = BigDecimal.valueOf(0.02);
      int expectedValue = 1;
      assertEquals(expectedValue, VelocityBuilder.velocity(testInput));
   }
   
   @Test
   public void testUpperBoundMinus1() {
      BigDecimal testInput = BigDecimal.valueOf(163.78);
      int expectedValue = 8189;
      assertEquals(expectedValue, VelocityBuilder.velocity(testInput));
   }
   
   @Test
   public void testUpperBound() {
      BigDecimal testInput = BigDecimal.valueOf(163.80);
      int expectedValue = 8190;
      assertEquals(expectedValue, VelocityBuilder.velocity(testInput));
   }
   
   @Test
   public void testNullFlagValue() {
      BigDecimal testInput = null;
      int expectedValue = 8191;
      assertEquals(expectedValue, VelocityBuilder.velocity(testInput));
   }
   
   @Test
   public void testConstructorIsPrivate()
         throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
      Constructor<VelocityBuilder> constructor = VelocityBuilder.class.getDeclaredConstructor();
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
