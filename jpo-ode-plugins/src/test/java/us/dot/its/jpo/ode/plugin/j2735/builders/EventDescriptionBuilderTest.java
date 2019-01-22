/*******************************************************************************
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import org.junit.Test;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import mockit.Capturing;
import us.dot.its.jpo.ode.plugin.j2735.J2735BitString;
import us.dot.its.jpo.ode.plugin.j2735.J2735Extent;
import us.dot.its.jpo.ode.util.CodecUtils;
import us.dot.its.jpo.ode.util.JsonUtils;

public class EventDescriptionBuilderTest {

   @Test
   public void testTypeEvent() {

      ObjectNode testNode = JsonUtils.newNode();

      testNode.put("typeEvent", 3);
      assertEquals(Integer.valueOf(3), EventDescriptionBuilder.genericEventDescription(testNode).getTypeEvent());
   }

   @Test
   public void testDescription() {

      ArrayNode descriptions = JsonUtils.newArrayNode();
      descriptions.add(5);

      ObjectNode testNode = JsonUtils.newNode();
      testNode.put("typeEvent", 3);
      testNode.set("description", descriptions);

      assertEquals(Integer.valueOf(5),
            EventDescriptionBuilder.genericEventDescription(testNode).getDescription().get(0));
   }

   @Test
   public void testPriority() {

      String expectedPriority = "unitTestsAreHighPriority";

      ObjectNode testNode = JsonUtils.newNode();
      testNode.put("typeEvent", 3);
      testNode.put("priority", expectedPriority);

      assertEquals(expectedPriority, EventDescriptionBuilder.genericEventDescription(testNode).getPriority());
   }

   @Test
   public void testHeadingSlice() {

      // Set bits: from022-5to045-0degrees (1)
      // from202-5to225-0degrees (9),
      // from337-5to360-0degrees (15)

      ObjectNode testNode = JsonUtils.newNode();
      testNode.put("typeEvent", 3);
      testNode.put("heading", "0100000001000001");

      J2735BitString actualValue = EventDescriptionBuilder.genericEventDescription(testNode).getHeading();

      // bit 0 = false
      assertFalse("Incorrect bit 0", actualValue.get("FROM000_0TO022_5DEGREES"));
      // bit 1 = true
      assertTrue("Incorrect bit 1", actualValue.get("FROM022_5TO045_0DEGREES"));
      // bit 8 = false
      assertFalse("Incorrect bit 8", actualValue.get("FROM180_0TO202_5DEGREES"));
      // bit 9 = true
      assertTrue("Incorrect bit 9", actualValue.get("FROM202_5TO225_0DEGREES"));
      // bit 14 = false
      assertFalse("Incorrect bit 14", actualValue.get("FROM315_0TO337_5DEGREES"));
      // bit 15 = true
      assertTrue("Incorrect bit 15", actualValue.get("FROM337_5TO360_0DEGREES"));
   }

   @Test
   public void testExtent() {

      ObjectNode testNode = JsonUtils.newNode();
      testNode.put("typeEvent", 3);
      testNode.put("extent", "useFor100000meters");

      assertEquals(J2735Extent.USEFOR100000METERS,
            EventDescriptionBuilder.genericEventDescription(testNode).getExtent());
   }

   @Test
   public void testEmptyRegional(@Capturing CodecUtils capturingCodecUtils) {

      ObjectNode testNode = JsonUtils.newNode();
      testNode.put("typeEvent", 3);
      testNode.set("regional", JsonUtils.newNode());

      assertEquals(0, EventDescriptionBuilder.genericEventDescription(testNode).getRegional().size());
   }

   @Test
   public void testRegional(@Capturing CodecUtils capturingCodecUtils) {

      ArrayNode regions = JsonUtils.newArrayNode();
      regions.add(JsonUtils.newNode().put("regionId", 5).put("regExtValue", "something"));

      ObjectNode testNode = JsonUtils.newNode();
      testNode.put("typeEvent", 3);
      testNode.set("regional", regions);

      assertEquals(Integer.valueOf(5),
            EventDescriptionBuilder.genericEventDescription(testNode).getRegional().get(0).getId());
   }

   @Test
   public void testConstructorIsPrivate()
         throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
      Constructor<EventDescriptionBuilder> constructor = EventDescriptionBuilder.class.getDeclaredConstructor();
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
