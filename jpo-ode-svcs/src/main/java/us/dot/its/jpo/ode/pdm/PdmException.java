package us.dot.its.jpo.ode.pdm;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class PdmException extends RuntimeException {
   private static final long serialVersionUID = 1L;

   public PdmException(String message) {
           super(message);
       }

  public PdmException(String message, Exception e) {
     super (message, e);
  }

  public PdmException(Exception e) {
     super(e);
  }
}
