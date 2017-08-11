package us.dot.its.jpo.ode.http;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class InternalServerErrorException extends RuntimeException {
   private static final long serialVersionUID = 1L;

   public InternalServerErrorException(String message) {
           super(message);
       }

  public InternalServerErrorException(String message, Exception e) {
     super (message, e);
  }

  public InternalServerErrorException(Exception e) {
     super(e);
  }
}
