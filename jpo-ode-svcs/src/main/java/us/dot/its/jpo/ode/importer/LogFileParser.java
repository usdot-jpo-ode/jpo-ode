package us.dot.its.jpo.ode.importer;

public interface LogFileParser {

    public class LogFileParserException extends Exception {
      public LogFileParserException(String msg) {
         super(msg);
      }

      public LogFileParserException(String msg, Exception e) {
         super (msg, e);
      }

      private static final long serialVersionUID = 1L;

   }

   public enum ParserStatus {
        UNKNOWN, INIT, NA, PARTIAL, COMPLETE, EOF
    }
   
   public enum MessageType {
      RX, TX, TIM
   }
    

}
