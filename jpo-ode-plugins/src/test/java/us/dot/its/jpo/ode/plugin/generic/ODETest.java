package us.dot.its.jpo.ode.plugin.generic;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import us.dot.its.jpo.ode.plugin.ODE;

public class ODETest {
   @Test
   public void testGetterSetter() {
      ODE ode = new ODE();
      
      ode.setVersion(3);
      assertEquals(3,  ode.getVersion());
   }
}
