package us.dot.its.jpo.ode.plugin.j2735.builders;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import org.junit.Test;

import com.fasterxml.jackson.databind.node.ObjectNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735BrakeSystemStatus;
import us.dot.its.jpo.ode.util.JsonUtils;

public class BrakeSystemStatusBuilderTest {

   @Test
   public void testWheelBrakesLeftFront() {

      String expectedTraction = "engaged";
      String expectedAbs = "on";
      String expectedScs = "off";
      String expectedBrakeBoost = "unavailable";
      String expectedAuxBrakes = "off";

      ObjectNode testInput = JsonUtils.newNode();
      testInput.put("wheelBrakes", "01000");
      testInput.set("traction", JsonUtils.newNode().put(expectedTraction, true));
      testInput.set("abs", JsonUtils.newNode().put(expectedAbs, true));
      testInput.set("scs", JsonUtils.newNode().put(expectedScs, true));
      testInput.set("brakeBoost", JsonUtils.newNode().put(expectedBrakeBoost, true));
      testInput.set("auxBrakes", JsonUtils.newNode().put(expectedAuxBrakes, true));

      J2735BrakeSystemStatus actualValue = BrakeSystemStatusBuilder.genericBrakeSystemStatus(testInput);

      assertFalse(actualValue.getWheelBrakes().get("unavailable"));
      assertTrue(actualValue.getWheelBrakes().get("leftFront"));
      assertFalse(actualValue.getWheelBrakes().get("leftRear"));
      assertFalse(actualValue.getWheelBrakes().get("rightFront"));
      assertFalse(actualValue.getWheelBrakes().get("rightRear"));

      assertEquals(expectedTraction, actualValue.getTraction());
      assertEquals(expectedAbs, actualValue.getAbs());
      assertEquals(expectedScs, actualValue.getScs());
      assertEquals(expectedBrakeBoost, actualValue.getBrakeBoost());
      assertEquals(expectedAuxBrakes, actualValue.getAuxBrakes());
   }

   @Test
   public void testConstructorIsPrivate()
         throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
      Constructor<BrakeSystemStatusBuilder> constructor = BrakeSystemStatusBuilder.class.getDeclaredConstructor();
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
