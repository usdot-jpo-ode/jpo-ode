package us.dot.its.jpo.ode.traveler;

import org.junit.Test;

import us.dot.its.jpo.ode.http.BadRequestException;

public class BadRequestExceptionTest {
   @Test
   public void checkExceptions() {
      try {
         throw new BadRequestException("This is a TIM exception");
      } catch (BadRequestException e) {
      }

      try {
         throw new BadRequestException("This is a Tim exception", new Exception());
      } catch (BadRequestException e) {

      }

      try {
         throw new BadRequestException(new Exception());
      } catch (BadRequestException e) {

      }
   }
}
