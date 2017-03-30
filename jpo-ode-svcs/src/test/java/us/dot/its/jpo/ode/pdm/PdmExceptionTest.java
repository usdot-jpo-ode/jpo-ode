package us.dot.its.jpo.ode.pdm;

import org.junit.Test;

import us.dot.its.jpo.ode.pdm.PdmException;

public class PdmExceptionTest {
   @Test
   public void checkExceptions() {
      try {
         throw new PdmException("This is a PDM exception");
      } catch (PdmException e) {
      }

      try {
         throw new PdmException("This is a PDM exception", new Exception());
      } catch (PdmException e) {

      }

      try {
         throw new PdmException(new Exception());
      } catch (PdmException e) {

      }
   }
}
