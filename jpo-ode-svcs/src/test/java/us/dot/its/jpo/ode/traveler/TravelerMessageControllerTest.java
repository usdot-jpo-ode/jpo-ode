package us.dot.its.jpo.ode.traveler;

import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Mocked;
import mockit.integration.junit4.JMockit;
import us.dot.its.jpo.ode.OdeProperties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created by anthonychen on 2/10/17.
 */
@RunWith(JMockit.class)
public class TravelerMessageControllerTest {

   /**
    * Created by anthonychen on 2/10/17.
    */

   @Mocked
   private OdeProperties odeProperties;
   
   @Test
   public void shouldRefuseConnectionNullIp() {

      String jsonString = null;

      try {
         new TravelerMessageController(odeProperties).timMessage(jsonString);
         fail("Expected IllegalArgumentException");
      } catch (Exception e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

}
