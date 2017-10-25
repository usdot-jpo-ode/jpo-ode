package us.dot.its.jpo.ode.plugin.j2735;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotEquals;

import java.math.BigDecimal;

import org.junit.Test;

public class OdePosition3DTest {
   @Test
   public void checkEqualsAndHashCode() {
      OdePosition3D po = new OdePosition3D();
      OdePosition3D pos = new OdePosition3D();
      assertTrue(po.equals(pos));
      assertEquals(po.hashCode(), pos.hashCode());
   }

   @Test
   public void checkEqualsAndHashCodeValues() {
      OdePosition3D po = new OdePosition3D(BigDecimal.valueOf(1.1d), BigDecimal.valueOf(2.2d), BigDecimal.valueOf(3.3d));
      OdePosition3D pos = new OdePosition3D(BigDecimal.valueOf(1.1d), BigDecimal.valueOf(2.2d), BigDecimal.valueOf(3.3d));
      assertTrue(po.equals(pos));
      assertEquals(po.hashCode(), pos.hashCode());
   }

   @Test
   public void checkHashCode() {
      OdePosition3D po = new OdePosition3D();
      OdePosition3D pos = new OdePosition3D();
      assertEquals(po.hashCode(), pos.hashCode());
      po.setLatitude(BigDecimal.valueOf(1));
      assertNotEquals(po.hashCode(), pos.hashCode());
      pos.setLatitude(BigDecimal.valueOf(1));
      assertEquals(po.hashCode(), pos.hashCode());
      po.setLongitude(BigDecimal.valueOf(1));
      assertNotEquals(po.hashCode(), pos.hashCode());
      pos.setLongitude(BigDecimal.valueOf(1));
      assertEquals(po.hashCode(), pos.hashCode());
      po.setElevation(BigDecimal.valueOf(1));
      assertNotEquals(po.hashCode(), pos.hashCode());
      pos.setElevation(BigDecimal.valueOf(1));
      assertEquals(po.hashCode(), pos.hashCode());
   }
}
