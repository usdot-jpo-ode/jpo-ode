package us.dot.its.jpo.ode.plugin.j2735;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotEquals;

import java.math.BigDecimal;

import org.junit.Test;

public class J2735Position3DTest {
   @Test
   public void checkEqualsAndHashCode() {
      J2735Position3D po = new J2735Position3D();
      J2735Position3D pos = new J2735Position3D();
      assertTrue(po.equals(pos));
      assertEquals(po.hashCode(), pos.hashCode());
   }

   @Test
   public void checkEqualsAndHashCodeValues() {
      J2735Position3D po = new J2735Position3D((long) 1, (long) 2, (long) 3);
      J2735Position3D pos = new J2735Position3D((long) 1, (long) 2, (long) 3);
      assertTrue(po.equals(pos));
      assertEquals(po.hashCode(), pos.hashCode());
   }

   @Test
   public void checkHashCode() {
      J2735Position3D po = new J2735Position3D();
      J2735Position3D pos = new J2735Position3D();
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
