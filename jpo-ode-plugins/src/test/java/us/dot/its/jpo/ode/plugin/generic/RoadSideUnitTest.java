package us.dot.its.jpo.ode.plugin.generic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Constructor;
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
      testRSU.setrsuTarget(rsuTarget);
      assertEquals(rsuTarget, testRSU.getrsuTarget());
      String rsuUsername = "name";
      testRSU.setrsuUsername(rsuUsername);
      assertEquals(rsuUsername, testRSU.getrsuUsername());
      String rsuPassword = "password";
      testRSU.setrsuPassword(rsuPassword);
      assertEquals(rsuPassword, testRSU.getrsuPassword());
      int rsuRetries = 2;
      testRSU.setrsuRetries(rsuRetries);
      assertEquals(rsuRetries, testRSU.getrsuRetries());
      int rsuTimeout = 10000;
      testRSU.setTimeout(rsuTimeout);
      assertEquals(rsuTimeout, testRSU.getrsuTimeout());
      Constructor<RoadSideUnit> constructor;
      try {
         constructor = RoadSideUnit.class.getDeclaredConstructor();
         assertTrue(Modifier.isPrivate(constructor.getModifiers()));
      }
      catch (NoSuchMethodException e) {
         fail("Unexpected Exception");
      }
   }
}
