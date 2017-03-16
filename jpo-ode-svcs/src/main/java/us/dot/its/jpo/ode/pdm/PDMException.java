package us.dot.its.jpo.ode.pdm;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class PDMException extends RuntimeException {
   private static final long serialVersionUID = 1L;

   public PDMException(String message) {
           super(message);
       }

  public PDMException(String message, Exception e) {
     super (message, e);
  }

  public PDMException(Exception e) {
     super(e);
  }
}
