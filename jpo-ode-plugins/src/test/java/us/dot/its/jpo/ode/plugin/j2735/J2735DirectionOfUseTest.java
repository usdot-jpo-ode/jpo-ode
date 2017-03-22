package us.dot.its.jpo.ode.plugin.j2735;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class J2735DirectionOfUseTest {
   @Test
   public void checkForward() {
      assertNotNull(J2735DirectionOfUse.FORWARD);
   }

   @Test
   public void checkReverse() {
      assertNotNull(J2735DirectionOfUse.REVERSE);
   }

   @Test
   public void checkBoth() {
      assertNotNull(J2735DirectionOfUse.BOTH);
   }
}
