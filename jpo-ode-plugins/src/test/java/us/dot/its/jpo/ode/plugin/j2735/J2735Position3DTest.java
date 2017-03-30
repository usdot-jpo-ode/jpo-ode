package us.dot.its.jpo.ode.plugin.j2735;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
   public void checkHashCodeDifferentValues() {
      J2735Position3D po = new J2735Position3D((long) 1, (long) 2, (long) 3);
      J2735Position3D pos = new J2735Position3D((long) 2, (long) 3, (long) 4);
      assertFalse(po.hashCode() == pos.hashCode());
   }
}
