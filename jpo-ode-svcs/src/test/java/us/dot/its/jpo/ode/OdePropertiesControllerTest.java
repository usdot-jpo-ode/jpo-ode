package us.dot.its.jpo.ode;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;

public class OdePropertiesControllerTest {

   @Tested
   OdePropertiesController testOdePropertiesController;

   @Injectable
   OdeProperties injectableOdeProperties;

   @Test
   public void shouldReturnVersionFromOdeProperties1() {
      new Expectations() {
         {
            injectableOdeProperties.getVersion();
            result = "5";
         }
      };

      assertEquals("{\"version\":\"5\"}", testOdePropertiesController.getVersion().getBody());
   }

   @Test
   public void shouldReturnVersionFromOdeProperties2() {
      new Expectations() {
         {
            injectableOdeProperties.getVersion();
            result = "testStringNotARealVersion";
         }
      };

      assertEquals("{\"version\":\"testStringNotARealVersion\"}", testOdePropertiesController.getVersion().getBody());
   }

}
