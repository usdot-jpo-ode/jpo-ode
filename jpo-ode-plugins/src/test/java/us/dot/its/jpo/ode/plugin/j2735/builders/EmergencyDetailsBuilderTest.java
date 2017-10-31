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

import us.dot.its.jpo.ode.plugin.j2735.J2735EmergencyDetails;
import us.dot.its.jpo.ode.plugin.j2735.J2735LightbarInUse;
import us.dot.its.jpo.ode.plugin.j2735.J2735MultiVehicleResponse;
import us.dot.its.jpo.ode.plugin.j2735.J2735ResponseType;
import us.dot.its.jpo.ode.plugin.j2735.J2735SirenInUse;
import us.dot.its.jpo.ode.util.JsonUtils;

public class EmergencyDetailsBuilderTest {

   @Test
   public void testRequiredFields() {
      
      Integer expectedSspRights = 5;
      J2735SirenInUse expectedSirenUse = J2735SirenInUse.NOTINUSE;
      J2735LightbarInUse expectedLightsUse = J2735LightbarInUse.ARROWSIGNSACTIVE;
      J2735MultiVehicleResponse expectedMulti = J2735MultiVehicleResponse.SINGLEVEHICLE;

      ObjectNode testInput = JsonUtils.newNode();
      testInput.put("sspRights", expectedSspRights);
      testInput.set("sirenUse", JsonUtils.newNode().put("notInUse", true));
      testInput.set("lightsUse", JsonUtils.newNode().put("arrowSignsActive", true));
      testInput.set("multi", JsonUtils.newNode().put("singleVehicle", true));

      J2735EmergencyDetails actualValue = EmergencyDetailsBuilder.genericEmergencyDetails(testInput);

      assertEquals(expectedSspRights, actualValue.getSspRights());
      assertEquals(expectedSirenUse, actualValue.getSirenUse());
      assertEquals(expectedLightsUse, actualValue.getLightsUse());
      assertEquals(expectedMulti, actualValue.getMulti());
   }
   
   @Test
   public void testOptionalFields() {
      
      Integer expectedSspRights = 5;

      ObjectNode testInput = JsonUtils.newNode();
      testInput.put("sspRights", 8);
      testInput.set("sirenUse", JsonUtils.newNode().put("notInUse", true));
      testInput.set("lightsUse", JsonUtils.newNode().put("arrowSignsActive", true));
      testInput.set("multi", JsonUtils.newNode().put("singleVehicle", true));
      
      // optional fields
      testInput.set("events", JsonUtils.newNode().put("sspRights", 5).put("event", "001000"));
      testInput.set("responseType", JsonUtils.newNode().put("slowMoving", true));

      J2735EmergencyDetails actualValue = EmergencyDetailsBuilder.genericEmergencyDetails(testInput);

      assertEquals(expectedSspRights, actualValue.getEvents().getSspRights());
      assertFalse(actualValue.getEvents().getEvent().get("peUnavailable"));
      assertFalse(actualValue.getEvents().getEvent().get("peEmergencyResponse"));
      assertTrue(actualValue.getEvents().getEvent().get("peEmergencyLightsActive"));
      assertFalse(actualValue.getEvents().getEvent().get("peEmergencySoundActive"));
      assertFalse(actualValue.getEvents().getEvent().get("peNonEmergencyLightsActive"));
      assertFalse(actualValue.getEvents().getEvent().get("peNonEmergencySoundActive"));
      assertEquals(J2735ResponseType.SLOWMOVING, actualValue.getResponseType());
   }

   @Test
   public void testConstructorIsPrivate()
         throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
      Constructor<EmergencyDetailsBuilder> constructor = EmergencyDetailsBuilder.class.getDeclaredConstructor();
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
