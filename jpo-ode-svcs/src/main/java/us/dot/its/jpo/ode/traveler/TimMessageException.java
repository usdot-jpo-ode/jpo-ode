package us.dot.its.jpo.ode.traveler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class TimMessageException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public TimMessageException(String message) {
            super(message);
        }

   public TimMessageException(String message, Exception e) {
      super (message, e);
   }

   public TimMessageException(Exception e) {
      super(e);
   }

}
