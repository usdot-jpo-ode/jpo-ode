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

   /**
    * Step 0 test. Test an empty stream should immediately return EOF, but still
    * set the filename.
    */
   @Test
   public void testStep0() {

      String testFileName = "testLogFile.bin";
      ParserStatus expectedStatus = ParserStatus.EOF;
      int expectedStep = 1;

      BufferedInputStream testInputStream = new BufferedInputStream(new ByteArrayInputStream(new byte[0]));

      try {
         assertEquals(expectedStatus, testBsmFileParser.parse(testInputStream, "testLogFile.bin"));
         assertEquals(testFileName, testBsmFileParser.getFilename());
         assertEquals(expectedStep, testBsmFileParser.getStep());
      } catch (LogFileParserException e) {
         fail("Unexpected exception: " + e);
      }
   }

   /**
    * Step 1 test. Should extract the "direction" value, length 1 byte, then
    * return EOF.
    */
   @Test
   public void testStep1() {

      int testDirection = 1;
      ParserStatus expectedStatus = ParserStatus.EOF;
      BsmSource expectedDirection = BsmSource.RV_RX;
      int expectedStep = 2;

      BufferedInputStream testInputStream = new BufferedInputStream(
            new ByteArrayInputStream(new byte[] { (byte) testDirection }));

      try {
         assertEquals(expectedStatus, testBsmFileParser.parse(testInputStream, "testLogFile.bin"));
         assertEquals(expectedDirection, testBsmFileParser.getDirection());
         assertEquals(expectedStep, testBsmFileParser.getStep());
      } catch (LogFileParserException e) {
         fail("Unexpected exception: " + e);
      }
   }
  
   /**
    * Step 2 test. Should extract UTC time, 4 byte value, then return EOF.
    */
   @Test
   public void testtStep2() {
      ParserStatus expectedStatus = ParserStatus.EOF;
      long expectedUtcTime = 33752069L; // 5,4,3,2 backwards as bytes
      int expectedStep = 3;

      BufferedInputStream testInputStream = new BufferedInputStream(
            new ByteArrayInputStream(new byte[] { 0, 5, 4, 3, 2 }));

      try {
         assertEquals(expectedStatus, testBsmFileParser.parse(testInputStream, "testLogFile.bin"));
         assertEquals(expectedUtcTime, testBsmFileParser.getUtctimeInSec());
         assertEquals(expectedStep, testBsmFileParser.getStep());
      } catch (LogFileParserException e) {
         fail("Unexpected exception: " + e);
      }
   }
   
   /**
    * Step 2 test without enough bytes, should return PARTIAL
    */
   @Test
   public void testStep2Partial() {
      ParserStatus expectedStatus = ParserStatus.PARTIAL;
      int expectedStep = 2;

      BufferedInputStream testInputStream = new BufferedInputStream(
            new ByteArrayInputStream(new byte[] { 0, 5, 4, 3 }));

      try {
         assertEquals(expectedStatus, testBsmFileParser.parse(testInputStream, "testLogFile.bin"));
         assertEquals("Wrong step", expectedStep, testBsmFileParser.getStep());
      } catch (LogFileParserException e) {
         fail("Unexpected exception: " + e);
      }
   }
   
   /**
    * Step 3 test. Should extract millisecond offset
    */
   @Test
   public void tetstStep3() {
      ParserStatus expectedStatus = ParserStatus.EOF;
      short expectedMsec = 0x0C0F; // 15, 12 backwards as bytes
      int expectedStep = 4;

      BufferedInputStream testInputStream = new BufferedInputStream(
            new ByteArrayInputStream(new byte[] { 1, 5, 4, 3, 2, 0xF, 0xC }));

      try {
         assertEquals(expectedStatus, testBsmFileParser.parse(testInputStream, "testLogFile.bin"));
         assertEquals(expectedMsec, testBsmFileParser.getmSec());
         assertEquals("Wrong step", expectedStep, testBsmFileParser.getStep());
      } catch (LogFileParserException e) {
         fail("Unexpected exception: " + e);
      }
   }
   
   /**
    * Step 3 partial test
    */
   @Test
   public void tetstStep3Partial() {
      ParserStatus expectedStatus = ParserStatus.PARTIAL;
      int expectedStep = 3;

      BufferedInputStream testInputStream = new BufferedInputStream(
            new ByteArrayInputStream(new byte[] { 1, 5, 4, 3, 2, 0xF }));

      try {
         assertEquals(expectedStatus, testBsmFileParser.parse(testInputStream, "testLogFile.bin"));
         assertEquals("Wrong step", expectedStep, testBsmFileParser.getStep());
      } catch (LogFileParserException e) {
         fail("Unexpected exception: " + e);
      }
   }
}
