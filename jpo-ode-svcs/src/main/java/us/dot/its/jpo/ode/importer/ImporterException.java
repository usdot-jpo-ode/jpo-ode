package us.dot.its.jpo.ode.importer;

public class ImporterException extends Exception {
   private static final long serialVersionUID = 1L;

   public ImporterException(String arg0, Throwable arg1) {
      super(arg0, arg1);
   }

   public ImporterException(String arg0) {
      super(arg0);
   }

}
