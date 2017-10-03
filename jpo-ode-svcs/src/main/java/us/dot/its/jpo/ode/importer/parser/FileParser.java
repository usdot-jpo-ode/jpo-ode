package us.dot.its.jpo.ode.importer.parser;

import java.io.BufferedInputStream;

public interface FileParser {

   public static class FileParserException extends Exception {
      public FileParserException(String msg) {
         super(msg);
      }

      public FileParserException(String msg, Exception e) {
         super(msg, e);
      }

      private static final long serialVersionUID = 1L;

   }

   public enum ParserStatus {
      UNKNOWN, INIT, NA, PARTIAL, COMPLETE, EOF, ERROR
   }

   public ParserStatus parseFile(BufferedInputStream bis, String fileName) throws FileParserException;
}
