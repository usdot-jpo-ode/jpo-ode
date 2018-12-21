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
package us.dot.its.jpo.ode.plugin.generic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Tested;
import mockit.integration.junit4.JMockit;
import us.dot.its.jpo.ode.plugin.SituationDataWarehouse;
import us.dot.its.jpo.ode.plugin.SituationDataWarehouse.SDW;
import us.dot.its.jpo.ode.plugin.SituationDataWarehouse.SDW.TimeToLive;
import us.dot.its.jpo.ode.plugin.j2735.OdeGeoRegion;

@RunWith(JMockit.class)
public class SituationDataWarehouseTest {
   @Tested
   SDW testSDW;

   @Test
   public void testGettersAndSetters() {
      OdeGeoRegion serviceRegion = new OdeGeoRegion();
      testSDW.setServiceRegion(serviceRegion);
      assertEquals(serviceRegion, testSDW.getServiceRegion());
      TimeToLive ttl = TimeToLive.thirtyminutes;
      testSDW.setTtl(ttl);
      assertEquals(ttl, testSDW.getTtl());
   }
   
   @Test
   public void testTheConstructor() throws NoSuchMethodException, IllegalAccessException {
      Constructor<SituationDataWarehouse> sdw;
      try {
         sdw = SituationDataWarehouse.class.getDeclaredConstructor();
         assertNotNull(sdw);
      }
      catch (NoSuchMethodException e) {
         fail("unexpected Exception");
      }
   }
   
   @Test
   public void testConstructorIsPrivate() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
     Constructor<SituationDataWarehouse > constructor = SituationDataWarehouse.class.getDeclaredConstructor();
     assertTrue(Modifier.isPrivate(constructor.getModifiers()));
     constructor.setAccessible(true);
     try {
       constructor.newInstance();
       fail("Expected IllegalAccessException.class");
     } catch (Exception e) {
       assertEquals(InvocationTargetException.class, e.getClass());
     }
   }

   @Test
   public void testEnumerations() {
      assertNotNull(TimeToLive.oneminute);
      assertNotNull(TimeToLive.thirtyminutes);
      assertNotNull(TimeToLive.oneday);
      assertNotNull(TimeToLive.oneweek);
      assertNotNull(TimeToLive.onemonth);
      assertNotNull(TimeToLive.oneyear);
   }
}
