package us.dot.its.jpo.ode.plugin.generic;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import us.dot.its.jpo.ode.plugin.ServiceRequest;

public class ODETest {
   @Test
   public void testGetterSetter() {
      ServiceRequest.OdeInternal ode = new ServiceRequest.OdeInternal();
      
      ode.setVersion(3);
      assertEquals(3,  ode.getVersion());
   }
}
