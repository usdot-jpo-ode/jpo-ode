package us.dot.its.jpo.ode.traveler;

import org.junit.Test;

public class TimMessageExceptionTest {
   @Test
   public void checkExceptions() {
      try {
         throw new TimMessageException("This is a TIM exception");
      } catch (TimMessageException e) {
      }

      try {
         throw new TimMessageException("This is a Tim exception", new Exception());
      } catch (TimMessageException e) {

      }

      try {
         throw new TimMessageException(new Exception());
      } catch (TimMessageException e) {

      }
   }
}
