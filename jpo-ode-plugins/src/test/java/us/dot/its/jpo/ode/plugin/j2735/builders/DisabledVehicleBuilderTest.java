package us.dot.its.jpo.ode.plugin.j2735.builders;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735DisabledVehicle;
import us.dot.its.jpo.ode.util.JsonUtils;

public class DisabledVehicleBuilderTest {

   @Test
   public void testPopulatesOnlyStatusDetailsLowerBound() {
      Integer expectedValue = 523;
      JsonNode testInput = JsonUtils.newNode().put("statusDetails", expectedValue);

      J2735DisabledVehicle result = DisabledVehicleBuilder.genericDisabledVehicle(testInput);

      assertEquals(expectedValue, result.getStatusDetails());
      assertNull(result.getLocationDetails());
   }

   @Test
   public void testPopulatesBothDetails() {
      Integer expectedStatus = 541;
      String expectedLocation = "in-tunnels";
      ObjectNode testInput = JsonUtils.newNode();
      testInput.put("statusDetails", expectedStatus);
      testInput.put("locationDetails", expectedLocation);

      J2735DisabledVehicle result = DisabledVehicleBuilder.genericDisabledVehicle(testInput);

      assertEquals(expectedStatus, result.getStatusDetails());
      assertEquals(expectedLocation, result.getLocationDetails().getName());
   }

   @Test
   public void testOutOfBoundsLower() {
      try {
         DisabledVehicleBuilder.genericDisabledVehicle(JsonUtils.newNode().put("statusDetails", 522));
         fail("Expected IllegalArgumentException");
      } catch (Exception e) {
         assertTrue(e instanceof IllegalArgumentException);
      }
   }

   @Test
   public void testOutOfBoundsUpper() {
      try {
         DisabledVehicleBuilder.genericDisabledVehicle(JsonUtils.newNode().put("statusDetails", 542));
         fail("Expected IllegalArgumentException");
      } catch (Exception e) {
         assertTrue(e instanceof IllegalArgumentException);
      }
   }

   @Test
   public void testConstructorIsPrivate()
         throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
      Constructor<DisabledVehicleBuilder> constructor = DisabledVehicleBuilder.class.getDeclaredConstructor();
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
