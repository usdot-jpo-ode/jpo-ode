package us.dot.its.jpo.ode.http;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class BadRequestException extends RuntimeException {
   private static final long serialVersionUID = 1L;

   public BadRequestException(String message) {
           super(message);
       }

  public BadRequestException(String message, Exception e) {
     super (message, e);
  }

  public BadRequestException(Exception e) {
     super(e);
  }
}
