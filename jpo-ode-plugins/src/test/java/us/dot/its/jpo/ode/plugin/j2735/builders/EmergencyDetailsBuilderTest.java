/*==============================================================================
 * Copyright 2018 572682
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/

package us.dot.its.jpo.ode.plugin.j2735.builders;

import static org.junit.Assert.assertThrows;

import com.fasterxml.jackson.databind.node.ObjectNode;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import us.dot.its.jpo.ode.plugin.j2735.J2735EmergencyDetails;
import us.dot.its.jpo.ode.plugin.j2735.J2735LightbarInUse;
import us.dot.its.jpo.ode.plugin.j2735.J2735MultiVehicleResponse;
import us.dot.its.jpo.ode.plugin.j2735.J2735ResponseType;
import us.dot.its.jpo.ode.plugin.j2735.J2735SirenInUse;
import us.dot.its.jpo.ode.util.JsonUtils;

class EmergencyDetailsBuilderTest {

  @Test
  void testRequiredFields() {

    Integer expectedSspRights = 5;
    J2735SirenInUse expectedSirenUse = J2735SirenInUse.NOTINUSE;
    J2735LightbarInUse expectedLightsUse = J2735LightbarInUse.ARROWSIGNSACTIVE;
    J2735MultiVehicleResponse expectedMulti = J2735MultiVehicleResponse.SINGLEVEHICLE;

    ObjectNode testInput = JsonUtils.newNode();
    testInput.put("doNotUse", expectedSspRights);
    testInput.set("sirenUse", JsonUtils.newNode().put("notInUse", true));
    testInput.set("lightsUse", JsonUtils.newNode().put("arrowSignsActive", true));
    testInput.set("multi", JsonUtils.newNode().put("singleVehicle", true));

    J2735EmergencyDetails actualValue = EmergencyDetailsBuilder.genericEmergencyDetails(testInput);

    Assertions.assertEquals(expectedSspRights, actualValue.getDoNotUse());
    Assertions.assertEquals(expectedSirenUse, actualValue.getSirenUse());
    Assertions.assertEquals(expectedLightsUse, actualValue.getLightsUse());
    Assertions.assertEquals(expectedMulti, actualValue.getMulti());
  }

  @Test
  void testOptionalFields() {

    Integer expectedSspRights = 5;

    ObjectNode testInput = JsonUtils.newNode();
    testInput.put("doNotUse", 8);
    testInput.set("sirenUse", JsonUtils.newNode().put("notInUse", true));
    testInput.set("lightsUse", JsonUtils.newNode().put("arrowSignsActive", true));
    testInput.set("multi", JsonUtils.newNode().put("singleVehicle", true));

    // optional fields
    testInput.set("events", JsonUtils.newNode().put("doNotUse", 5).put("event", "001000"));
    testInput.set("responseType", JsonUtils.newNode().put("slowMoving", true));

    J2735EmergencyDetails actualValue = EmergencyDetailsBuilder.genericEmergencyDetails(testInput);

    Assertions.assertEquals(expectedSspRights, actualValue.getEvents().getDoNotUse());
    Assertions.assertFalse(actualValue.getEvents().getEvent().get("peUnavailable"));
    Assertions.assertFalse(actualValue.getEvents().getEvent().get("peEmergencyResponse"));
    Assertions.assertTrue(actualValue.getEvents().getEvent().get("peEmergencyLightsActive"));
    Assertions.assertFalse(actualValue.getEvents().getEvent().get("peEmergencySoundActive"));
    Assertions.assertFalse(actualValue.getEvents().getEvent().get("peNonEmergencyLightsActive"));
    Assertions.assertFalse(actualValue.getEvents().getEvent().get("peNonEmergencySoundActive"));
    Assertions.assertEquals(J2735ResponseType.SLOWMOVING, actualValue.getResponseType());
  }

  @Test
  void testOptionalFieldsWithInvalidSSPRights() {
    assertThrows(IllegalArgumentException.class, () -> {
      ObjectNode testInput = JsonUtils.newNode();
      testInput.put("doNotUse", 8);
      testInput.set("sirenUse", JsonUtils.newNode().put("notInUse", true));
      testInput.set("lightsUse", JsonUtils.newNode().put("arrowSignsActive", true));
      testInput.set("multi", JsonUtils.newNode().put("singleVehicle", true));

      // optional fields
      testInput.set("events", JsonUtils.newNode().put("doNotUse", 32).put("event", "001000"));
      testInput.set("responseType", JsonUtils.newNode().put("slowMoving", true));

      EmergencyDetailsBuilder.genericEmergencyDetails(testInput);
    });
  }

  @Test
  void testConstructorIsPrivate()
      throws NoSuchMethodException, IllegalAccessException, InvocationTargetException,
      InstantiationException {
    Constructor<EmergencyDetailsBuilder> constructor =
        EmergencyDetailsBuilder.class.getDeclaredConstructor();
    Assertions.assertTrue(Modifier.isPrivate(constructor.getModifiers()));
    constructor.setAccessible(true);
    try {
      constructor.newInstance();
      Assertions.fail("Expected IllegalAccessException.class");
    } catch (Exception e) {
      Assertions.assertEquals(InvocationTargetException.class, e.getClass());
    }
  }

  @Test
  void testPrivilegedEventsBuilderConstructorIsPrivate()
      throws NoSuchMethodException, IllegalAccessException, InvocationTargetException,
      InstantiationException {
    Constructor<PrivilegedEventsBuilder> constructor =
        PrivilegedEventsBuilder.class.getDeclaredConstructor();
    Assertions.assertTrue(Modifier.isPrivate(constructor.getModifiers()));
    constructor.setAccessible(true);
    try {
      constructor.newInstance();
      Assertions.fail("Expected IllegalAccessException.class");
    } catch (Exception e) {
      Assertions.assertEquals(InvocationTargetException.class, e.getClass());
    }
  }
}
