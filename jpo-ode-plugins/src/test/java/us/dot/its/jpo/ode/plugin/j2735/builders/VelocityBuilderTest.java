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
