package us.dot.its.jpo.ode.plugin.generic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Tested;
import mockit.integration.junit4.JMockit;
import us.dot.its.jpo.ode.plugin.RoadSideUnit;
import us.dot.its.jpo.ode.plugin.RoadSideUnit.RSU;

@RunWith(JMockit.class)
public class RoadSideUnitTest {
   @Tested
   RSU testRSU;

   @Test
   public void testGettersAndSetters() {
      String rsuTarget = "target";
      testRSU.setRsuTarget(rsuTarget);
      assertEquals(rsuTarget, testRSU.getRsuTarget());
      String rsuUsername = "name";
      testRSU.setRsuUsername(rsuUsername);
      assertEquals(rsuUsername, testRSU.getRsuUsername());
      String rsuPassword = "password";
      testRSU.setRsuPassword(rsuPassword);
      assertEquals(rsuPassword, testRSU.getRsuPassword());
      int rsuRetries = 2;
      testRSU.setRsuRetries(rsuRetries);
      assertEquals(rsuRetries, testRSU.getRsuRetries());
      int rsuTimeout = 10000;
      testRSU.setRsuTimeout(rsuTimeout);
      assertEquals(rsuTimeout, testRSU.getRsuTimeout());
   }
    
   @Test
   public void testConstructorIsPrivate() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
     Constructor<RoadSideUnit > constructor = RoadSideUnit.class.getDeclaredConstructor();
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
