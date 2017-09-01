package us.dot.its.jpo.ode.importer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;

import org.junit.Test;

import mockit.Tested;
import us.dot.its.jpo.ode.importer.LogFileParser.LogFileParserException;
import us.dot.its.jpo.ode.importer.LogFileParser.ParserStatus;

public class BsmFileParserTest {

   @Tested
   BsmFileParser testBsmFileParser;

   @Test
   public void testEmptyInputStreamShouldReturnEOF() {
      try {
         assertEquals(ParserStatus.EOF, testBsmFileParser
               .parse(new BufferedInputStream(new ByteArrayInputStream(new byte[0])), "testLogFile.bin"));
      } catch (LogFileParserException e) {
         fail("Unexpected exception: " + e);
      }
   }

   @Test
   public void testSingleByteInputStreamShouldReturnPartial() {
      try {
         assertEquals(ParserStatus.PARTIAL, testBsmFileParser
               .parse(new BufferedInputStream(new ByteArrayInputStream(new byte[2])), "testLogFile.bin"));
      } catch (LogFileParserException e) {
         fail("Unexpected exception: " + e);
      }
   }

}
