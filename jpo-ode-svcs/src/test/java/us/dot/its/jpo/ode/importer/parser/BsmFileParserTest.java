package us.dot.its.jpo.ode.importer.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;

import org.apache.tomcat.util.buf.HexUtils;
import org.junit.Test;

import mockit.Injectable;
import mockit.Tested;
import us.dot.its.jpo.ode.importer.BsmSource;
import us.dot.its.jpo.ode.importer.parser.FileParser.FileParserException;
import us.dot.its.jpo.ode.importer.parser.FileParser.ParserStatus;

public class BsmFileParserTest {

   @Tested
   BsmFileParser testBsmFileParser;
   @Injectable long bundleId;
   

   /**
    * Silly test for coverage
    */
   @Test
   public void testStepsAlreadyDone() {

      ParserStatus expectedStatus = ParserStatus.COMPLETE;

      BufferedInputStream testInputStream = new BufferedInputStream(new ByteArrayInputStream(new byte[0]));

      try {
         testBsmFileParser.setStep(7);
         assertEquals(expectedStatus, testBsmFileParser.parseFile(testInputStream, "testLogFile.bin"));
      } catch (FileParserException e) {
         fail("Unexpected exception: " + e);
      }

   }

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
         assertEquals(expectedStatus, testBsmFileParser.parseFile(testInputStream, "testLogFile.bin"));
         assertEquals(testFileName, testBsmFileParser.getFilename());
         assertEquals(expectedStep, testBsmFileParser.getStep());
      } catch (FileParserException e) {
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
         assertEquals(expectedStatus, testBsmFileParser.parseFile(testInputStream, "testLogFile.bin"));
         assertEquals(expectedDirection, testBsmFileParser.getDirection());
         assertEquals(expectedStep, testBsmFileParser.getStep());
      } catch (FileParserException e) {
         fail("Unexpected exception: " + e);
      }
   }

   /**
    * Step 2 test. Should extract UTC time, 4 byte value, then return EOF.
    */
   @Test
   public void testStep2() {
      ParserStatus expectedStatus = ParserStatus.EOF;
      long expectedUtcTime = 33752069L; // 5,4,3,2 backwards as bytes
      int expectedStep = 3;

      BufferedInputStream testInputStream = new BufferedInputStream(
            new ByteArrayInputStream(new byte[] { 0, 5, 4, 3, 2 }));

      try {
         assertEquals(expectedStatus, testBsmFileParser.parseFile(testInputStream, "testLogFile.bin"));
         assertEquals(expectedUtcTime, testBsmFileParser.getUtcTimeInSec());
         assertEquals(expectedStep, testBsmFileParser.getStep());
      } catch (FileParserException e) {
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
         assertEquals(expectedStatus, testBsmFileParser.parseFile(testInputStream, "testLogFile.bin"));
         assertEquals("Wrong step", expectedStep, testBsmFileParser.getStep());
      } catch (FileParserException e) {
         fail("Unexpected exception: " + e);
      }
   }

   /**
    * Step 3 test. Should extract millisecond offset
    */
   @Test
   public void testStep3() {
      ParserStatus expectedStatus = ParserStatus.EOF;
      short expectedMsec = 0x0C0F; // 15, 12 backwards as bytes
      int expectedStep = 4;

      BufferedInputStream testInputStream = new BufferedInputStream(
            new ByteArrayInputStream(new byte[] { 1, 5, 4, 3, 2, 0xF, 0xC }));

      try {
         assertEquals(expectedStatus, testBsmFileParser.parseFile(testInputStream, "testLogFile.bin"));
         assertEquals(expectedMsec, testBsmFileParser.getmSec());
         assertEquals("Wrong step", expectedStep, testBsmFileParser.getStep());
      } catch (FileParserException e) {
         fail("Unexpected exception: " + e);
      }
   }

   /**
    * Step 3 partial test
    */
   @Test
   public void testStep3Partial() {
      ParserStatus expectedStatus = ParserStatus.PARTIAL;
      int expectedStep = 3;

      BufferedInputStream testInputStream = new BufferedInputStream(
            new ByteArrayInputStream(new byte[] { 1, 5, 4, 3, 2, 0xF }));

      try {
         assertEquals(expectedStatus, testBsmFileParser.parseFile(testInputStream, "testLogFile.bin"));
         assertEquals("Wrong step", expectedStep, testBsmFileParser.getStep());
      } catch (FileParserException e) {
         fail("Unexpected exception: " + e);
      }
   }

   /**
    * Step 4 test. Test EV_TX defaults to valid signature.
    */
   @Test
   public void testStep4EV_TX() {

      BufferedInputStream testInputStream = new BufferedInputStream(
            new ByteArrayInputStream(new byte[] { 0, 5, 4, 3, 2, 15, 12, 1 }));

      try {
         testBsmFileParser.parseFile(testInputStream, "testLogFile.bin");
         assertTrue(testBsmFileParser.isValidSignature());
      } catch (FileParserException e) {
         fail("Unexpected exception: " + e);
      }
   }

   /**
    * Step 4 test. Test EV_RX contains valid signature
    */
   @Test
   public void testStep4RXValid() {
      ParserStatus expectedStatus = ParserStatus.EOF;

      BufferedInputStream testInputStream = new BufferedInputStream(
            new ByteArrayInputStream(new byte[] { 1, 5, 4, 3, 2, 15, 12, 1 }));

      try {
         assertEquals(expectedStatus, testBsmFileParser.parseFile(testInputStream, "testLogFile.bin"));
         assertTrue(testBsmFileParser.isValidSignature());
      } catch (FileParserException e) {
         fail("Unexpected exception: " + e);
      }
   }

   /**
    * Step 4 test. Test EV_RX contains invalid signature
    */
   @Test
   public void testStep4RXInvalid() {
      ParserStatus expectedStatus = ParserStatus.EOF;

      BufferedInputStream testInputStream = new BufferedInputStream(
            new ByteArrayInputStream(new byte[] { 1, 5, 4, 3, 2, 15, 12, 0 }));

      try {
         assertEquals(expectedStatus, testBsmFileParser.parseFile(testInputStream, "testLogFile.bin"));
         assertFalse(testBsmFileParser.isValidSignature());
      } catch (FileParserException e) {
         fail("Unexpected exception: " + e);
      }
   }

   /**
    * Step 5 test. Should extract length field
    */
   @Test
   public void testStep5() {

      ParserStatus expectedStatus = ParserStatus.EOF;
      short expectedLength = 0x0100;
      int expectedStep = 6;

      BufferedInputStream testInputStream = new BufferedInputStream(
            new ByteArrayInputStream(new byte[] { 0, 5, 4, 3, 2, 15, 12, 0x0, 0x1 }));

      try {
         assertEquals(expectedStatus, testBsmFileParser.parseFile(testInputStream, "testLogFile.bin"));
         assertEquals(expectedLength, testBsmFileParser.getLength());
         assertEquals("Wrong step", expectedStep, testBsmFileParser.getStep());
      } catch (FileParserException e) {
         fail("Unexpected exception: " + e.getCause());
      }
   }

   /**
    * Step 5 partial test.
    */
   @Test
   public void testStep5Partial() {
      ParserStatus expectedStatus = ParserStatus.PARTIAL;
      int expectedStep = 5;

      BufferedInputStream testInputStream = new BufferedInputStream(
            new ByteArrayInputStream(new byte[] { 0, 5, 4, 3, 2, 15, 12, 0x0 }));

      try {
         assertEquals(expectedStatus, testBsmFileParser.parseFile(testInputStream, "testLogFile.bin"));
         assertEquals("Wrong step", expectedStep, testBsmFileParser.getStep());
      } catch (FileParserException e) {
         fail("Unexpected exception: " + e.getCause());
      }
   }

   /**
    * Step 6 test. Should extract payload field
    */
   @Test
   public void testStep6() {

      ParserStatus expectedStatus = ParserStatus.COMPLETE;
      short expectedLength = 2;
      byte[] expectedPayload = new byte[] { 0xF, 0xA };
      int expectedStep = 0;

      BufferedInputStream testInputStream = new BufferedInputStream(
            new ByteArrayInputStream(new byte[] { 0, 5, 4, 3, 2, 15, 12, 2, 0, 0xF, 0xA }));

      try {
         assertEquals(expectedStatus, testBsmFileParser.parseFile(testInputStream, "testLogFile.bin"));
         assertEquals(expectedLength, testBsmFileParser.getLength());
         assertEquals(HexUtils.toHexString(expectedPayload), HexUtils.toHexString(testBsmFileParser.getPayload()));
         assertEquals("Wrong step", expectedStep, testBsmFileParser.getStep());
      } catch (FileParserException e) {
         fail("Unexpected exception: " + e.getCause());
      }
   }

   /**
    * Step 6 partial test
    */
   @Test
   public void testStep6Partial() {

      ParserStatus expectedStatus = ParserStatus.PARTIAL;

      BufferedInputStream testInputStream = new BufferedInputStream(
            new ByteArrayInputStream(new byte[] { 0, 5, 4, 3, 2, 15, 12, 2, 0, 0xF }));

      try {
         assertEquals(expectedStatus, testBsmFileParser.parseFile(testInputStream, "testLogFile.bin"));
      } catch (FileParserException e) {
         fail("Unexpected exception: " + e.getCause());
      }
   }
}
