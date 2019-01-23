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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;

import org.junit.Test;

import com.fasterxml.jackson.databind.node.ObjectNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735AntennaOffsetSet;
import us.dot.its.jpo.ode.util.JsonUtils;

public class AntennaOffsetSetBuilderTest {

  public static ObjectNode buildTestJ2735AntennaOffsetSet() {
    int antOffsetX = 1234;
    int antOffsetY = 234;
    int antOffsetZ = 321;

    ObjectNode testInput = JsonUtils.newNode();
    testInput.put("antOffsetX", antOffsetX);
    testInput.put("antOffsetY", antOffsetY);
    testInput.put("antOffsetZ", antOffsetZ);

    return testInput;
  }

   @Test
   public void testPopulate3Elements() {

     BigDecimal expectedX = BigDecimal.valueOf(12.34);
     BigDecimal expectedY = BigDecimal.valueOf(2.34);
     BigDecimal expectedZ = BigDecimal.valueOf(3.21);

     ObjectNode testInput = buildTestJ2735AntennaOffsetSet();
     J2735AntennaOffsetSet actualValue = AntennaOffsetSetBuilder.genericAntennaOffsetSet(testInput);
     
     assertEquals(expectedX, actualValue.getAntOffsetX());
     assertEquals(expectedY, actualValue.getAntOffsetY());
     assertEquals(expectedZ, actualValue.getAntOffsetZ());
   }

   @Test
   public void testConstructorIsPrivate()
         throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
      Constructor<AntennaOffsetSetBuilder> constructor = AntennaOffsetSetBuilder.class.getDeclaredConstructor();
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
