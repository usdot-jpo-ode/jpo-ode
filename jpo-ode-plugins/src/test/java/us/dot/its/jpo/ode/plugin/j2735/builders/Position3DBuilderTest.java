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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.node.ObjectNode;

import mockit.Capturing;
import mockit.Expectations;
import us.dot.its.jpo.ode.plugin.j2735.DsrcPosition3D;
import us.dot.its.jpo.ode.plugin.j2735.OdePosition3D;
import us.dot.its.jpo.ode.util.JsonUtils;

public class Position3DBuilderTest {

   @Test
   public void testJsonNodeToDsrcPosition3D() {
      ObjectNode node = JsonUtils.newNode();
      node.put("lat", 604739946);
      node.put("long", -1009691905);
      node.put("elevation", 14843);

      DsrcPosition3D result = Position3DBuilder.dsrcPosition3D(node);
      assertEquals(Long.valueOf(604739946), result.getLatitude());
      assertEquals(Long.valueOf(-1009691905), result.getLongitude());
      assertEquals(Long.valueOf(14843), result.getElevation());
   }

   @Test
   public void testNullDsrcPosition3D() {
      DsrcPosition3D input = new DsrcPosition3D();
      OdePosition3D result = Position3DBuilder.odePosition3D(input);

      assertNull(result.getLatitude());
      assertNull(result.getLongitude());
      assertNull(result.getElevation());
   }

   @Test
   public void testOdePosition3DPopulation() {
      OdePosition3D result = Position3DBuilder
            .odePosition3D(new DsrcPosition3D(Long.valueOf(54), Long.valueOf(65), Long.valueOf(76)));

      assertEquals(BigDecimal.valueOf(0.0000054), result.getLatitude());
      assertEquals(BigDecimal.valueOf(0.0000065), result.getLongitude());
      assertEquals(BigDecimal.valueOf(7.6), result.getElevation());
   }

   @Test
   public void testJsonNodeToOdePosition3D() {
      ObjectNode node = JsonUtils.newNode();
      node.put("latitude", 604739946);
      node.put("longitude", -1009691905);
      node.put("elevation", 14843);

      OdePosition3D result = Position3DBuilder.odePosition3D(node);

      assertEquals(BigDecimal.valueOf(604739946), result.getLatitude());
      assertEquals(BigDecimal.valueOf(-1009691905), result.getLongitude());
      assertEquals(BigDecimal.valueOf(14843), result.getElevation());
   }

   @Test
   public void testEmptyJsonNodeToOdePosition3D() {
      ObjectNode node = JsonUtils.newNode();

      OdePosition3D result = Position3DBuilder.odePosition3D(node);

      assertNull(result.getLatitude());
      assertNull(result.getLongitude());
      assertNull(result.getElevation());
   }

   @Test
   public void testOdePosition3DToDsrcPosition3D(@Capturing LatitudeBuilder capturingLatitudeBuilder,
         @Capturing LongitudeBuilder capturingLongitudeBuilder, @Capturing ElevationBuilder capturingElevationBuilder) {
      new Expectations() {
         {
            LatitudeBuilder.j2735Latitude((BigDecimal) any);
            result = Long.valueOf(5);
            LongitudeBuilder.j2735Longitude((BigDecimal) any);
            result = Long.valueOf(6);
            ElevationBuilder.j2735Elevation((BigDecimal) any);
            result = Long.valueOf(7);
         }
      };

      DsrcPosition3D result = Position3DBuilder
            .dsrcPosition3D(new OdePosition3D(BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE));

      assertEquals(Long.valueOf(5), result.getLatitude());
      assertEquals(Long.valueOf(6), result.getLongitude());
      assertEquals(Long.valueOf(7), result.getElevation());
   }

   @Test
   public void testNullOdePosition3DToDsrcPosition3D() {
      DsrcPosition3D result = Position3DBuilder.dsrcPosition3D(new OdePosition3D());

      assertNull(result.getElevation());
      assertNull(result.getLatitude());
      assertNull(result.getLongitude());
   }

   @Test
   public void testConstructorIsPrivate()
         throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
      Constructor<Position3DBuilder> constructor = Position3DBuilder.class.getDeclaredConstructor();
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
