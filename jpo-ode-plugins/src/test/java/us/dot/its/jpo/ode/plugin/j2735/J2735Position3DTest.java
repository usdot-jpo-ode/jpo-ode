package us.dot.its.jpo.ode.plugin.j2735;

import static org.junit.Assert.assertEquals;
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
}
