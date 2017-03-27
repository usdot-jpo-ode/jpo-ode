package us.dot.its.jpo.ode.plugin.generic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Tested;
import mockit.integration.junit4.JMockit;
import us.dot.its.jpo.ode.plugin.SituationDataWarehouse;
import us.dot.its.jpo.ode.plugin.SituationDataWarehouse.SDW;
import us.dot.its.jpo.ode.plugin.SituationDataWarehouse.SDW.TimeToLive;
import us.dot.its.jpo.ode.plugin.j2735.J2735GeoRegion;

@RunWith(JMockit.class)
public class SituationDataWarehouseTest {
   @Tested
   SDW testSDW;

   @Test
   public void testGettersAndSetters() {
      J2735GeoRegion serviceRegion = new J2735GeoRegion();
      testSDW.setServiceRegion(serviceRegion);
      assertEquals(serviceRegion, testSDW.getServiceRegion());
      TimeToLive ttl = TimeToLive.THIRTYMINUTES;
      testSDW.setTtl(ttl);
      assertEquals(ttl, testSDW.getTtl());
      Constructor<SituationDataWarehouse> constructor;
      try {
         constructor = SituationDataWarehouse.class.getDeclaredConstructor();
         assertTrue(Modifier.isPrivate(constructor.getModifiers()));
      } catch (NoSuchMethodException | UnsupportedOperationException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void testEnumerations() {
      assertNotNull(TimeToLive.ONEMINUTE);
      assertNotNull(TimeToLive.THIRTYMINUTES);
      assertNotNull(TimeToLive.ONEDAY);
      assertNotNull(TimeToLive.ONEWEEK);
      assertNotNull(TimeToLive.ONEMONTH);
      assertNotNull(TimeToLive.ONEYEAR);
   }
}
