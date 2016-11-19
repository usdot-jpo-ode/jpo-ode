package us.dot.its.jpo.ode.wrapper;

import java.io.IOException;
import java.text.ParseException;
import java.util.concurrent.Future;

import us.dot.its.jpo.ode.model.OdeDataMessage;
import us.dot.its.jpo.ode.model.OdeException;

public interface DataProcessor<D, R> {
   Future<R> process(D data) throws DataProcessorException;
   void filterAndSend(OdeDataMessage dataMsg) throws IOException, ParseException;

   public static class DataProcessorException extends OdeException {

      private static final long serialVersionUID = -3319078097438578006L;

      public DataProcessorException(Exception e) {
         super(e);
      }

      public DataProcessorException(String message, Exception cause) {
         super(message, cause);
      }

      public DataProcessorException(String message) {
         super(message);
      }

   }

}
