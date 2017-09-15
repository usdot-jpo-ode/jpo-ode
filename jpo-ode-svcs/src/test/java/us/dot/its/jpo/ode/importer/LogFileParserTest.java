package us.dot.its.jpo.ode.importer;

import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Test;

import mockit.Mocked;
import us.dot.its.jpo.ode.importer.parser.LogFileParser;

public class LogFileParserTest {

   @Mocked
   IOException mockIOException;

   @Test
   public void testExceptions() {
      try {
         new LogFileParser.LogFileParserException("message");
         new LogFileParser.LogFileParserException("message", mockIOException);
      } catch (Exception e) {
         fail("Unexpected exception: " + e);
      }
   }

}
