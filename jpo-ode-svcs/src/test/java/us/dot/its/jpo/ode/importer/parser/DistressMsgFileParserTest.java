package us.dot.its.jpo.ode.importer.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;

import org.apache.tomcat.util.buf.HexUtils;
import org.junit.Test;

import mockit.Injectable;
import mockit.Tested;
import us.dot.its.jpo.ode.importer.parser.FileParser.FileParserException;
import us.dot.its.jpo.ode.importer.parser.FileParser.ParserStatus;

public class DistressMsgFileParserTest {
   
   @Tested
   DistressMsgFileParser testDistressMsgFileParser;
   @Injectable long bundleId;

   /**
    * Silly test for coverage
    */
   @Test
   public void testStepsAlreadyDone() {

      ParserStatus expectedStatus = ParserStatus.COMPLETE;
      int expectedStep = 0;

      BufferedInputStream testInputStream = new BufferedInputStream(new ByteArrayInputStream(new byte[0]));

      try {
         testDistressMsgFileParser.setStep(12);
         assertEquals(expectedStatus, testDistressMsgFileParser.parseFile(testInputStream, "testLogFile.bin"));
         assertEquals(expectedStep, testDistressMsgFileParser.getStep());
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
         assertEquals(expectedStatus, testDistressMsgFileParser.parseFile(testInputStream, "testLogFile.bin"));
         assertEquals(testFileName, testDistressMsgFileParser.getFilename());
         assertEquals(expectedStep, testDistressMsgFileParser.getStep());
      } catch (FileParserException e) {
         fail("Unexpected exception: " + e);
      }
   }

   /**
    * Step 1 test. Should extract the "location->latitude" value, length 4
    * bytes, then return EOF.
    */
   @Test
   public void testStep1() {

      ParserStatus expectedStatus = ParserStatus.EOF;
      long expectedLatitude = 33752069L; // 5,4,3,2 backwards as bytes
      int expectedStep = 2;

      BufferedInputStream testInputStream = new BufferedInputStream(
            new ByteArrayInputStream(new byte[] { 5, 4, 3, 2 }));

      try {
         assertEquals(expectedStatus, testDistressMsgFileParser.parseFile(testInputStream, "testLogFile.bin"));
         assertEquals(expectedLatitude, testDistressMsgFileParser.getLocation().getLatitude());
         assertEquals(expectedStep, testDistressMsgFileParser.getStep());
      } catch (FileParserException e) {
         fail("Unexpected exception: " + e);
      }
   }

   /**
    * Step 1 test without enough bytes. Should return PARTIAL.
    */
   @Test
   public void testStep1Partial() {

      ParserStatus expectedStatus = ParserStatus.PARTIAL;
      int expectedStep = 1;

      BufferedInputStream testInputStream = new BufferedInputStream(new ByteArrayInputStream(new byte[] { 5, 4, 3 }));

      try {
         assertEquals(expectedStatus, testDistressMsgFileParser.parseFile(testInputStream, "testLogFile.bin"));
         assertEquals(expectedStep, testDistressMsgFileParser.getStep());
      } catch (FileParserException e) {
         fail("Unexpected exception: " + e);
      }
   }

   /**
    * Step 2 test. Should extract the "location->longitude" value, length 4
    * bytes, then return EOF.
    */
   @Test
   public void testStep2() {

      ParserStatus expectedStatus = ParserStatus.EOF;
      long expectedLongitude = 84148994L; // 2,3,4,5 backwards as bytes
      int expectedStep = 3;

      BufferedInputStream testInputStream = new BufferedInputStream(
            new ByteArrayInputStream(new byte[] { 5, 4, 3, 2, 2, 3, 4, 5 }));

      try {
         assertEquals(expectedStatus, testDistressMsgFileParser.parseFile(testInputStream, "testLogFile.bin"));
         assertEquals(expectedLongitude, testDistressMsgFileParser.getLocation().getLongitude());
         assertEquals(expectedStep, testDistressMsgFileParser.getStep());
      } catch (FileParserException e) {
         fail("Unexpected exception: " + e);
      }
   }

   /**
    * Step 2 test without enough bytes. Should return PARTIAL.
    */
   @Test
   public void testStep2Partial() {

      ParserStatus expectedStatus = ParserStatus.PARTIAL;
      int expectedStep = 2;

      BufferedInputStream testInputStream = new BufferedInputStream(
            new ByteArrayInputStream(new byte[] { 5, 4, 3, 2, 2, 3, 4 }));

      try {
         assertEquals(expectedStatus, testDistressMsgFileParser.parseFile(testInputStream, "testLogFile.bin"));
         assertEquals(expectedStep, testDistressMsgFileParser.getStep());
      } catch (FileParserException e) {
         fail("Unexpected exception: " + e);
      }
   }

   /**
    * Step 3 test. Should extract the "location->elevation" value, length 4
    * bytes, then return EOF.
    */
   @Test
   public void testStep3() {

      ParserStatus expectedStatus = ParserStatus.EOF;
      long expectedElevation = 151521030L; // 6,7,8,9 backwards as bytes
      int expectedStep = 4;

      BufferedInputStream testInputStream = new BufferedInputStream(
            new ByteArrayInputStream(new byte[] { 5, 4, 3, 2, 2, 3, 4, 5, 6, 7, 8, 9 }));

      try {
         assertEquals(expectedStatus, testDistressMsgFileParser.parseFile(testInputStream, "testLogFile.bin"));
         assertEquals(expectedElevation, testDistressMsgFileParser.getLocation().getElevation());
         assertEquals(expectedStep, testDistressMsgFileParser.getStep());
      } catch (FileParserException e) {
         fail("Unexpected exception: " + e);
      }
   }

   /**
    * Step 3 test without enough bytes. Should return PARTIAL.
    */
   @Test
   public void testStep3Partial() {

      ParserStatus expectedStatus = ParserStatus.PARTIAL;
      int expectedStep = 3;

      BufferedInputStream testInputStream = new BufferedInputStream(
            new ByteArrayInputStream(new byte[] { 5, 4, 3, 2, 2, 3, 4, 5, 6, 7, 8 }));

      try {
         assertEquals(expectedStatus, testDistressMsgFileParser.parseFile(testInputStream, "testLogFile.bin"));
         assertEquals(expectedStep, testDistressMsgFileParser.getStep());
      } catch (FileParserException e) {
         fail("Unexpected exception: " + e);
      }
   }

   /**
    * Step 4 test. Should extract the "location->speed" value, length 2 bytes,
    * then return EOF.
    */
   @Test
   public void testStep4() {

      ParserStatus expectedStatus = ParserStatus.EOF;
      long expectedSpeed = 3339L; // B,D backwards as bytes
      int expectedStep = 5;

      BufferedInputStream testInputStream = new BufferedInputStream(
            new ByteArrayInputStream(new byte[] { 5, 4, 3, 2, 2, 3, 4, 5, 6, 7, 8, 9, 0xB, 0xD }));

      try {
         assertEquals(expectedStatus, testDistressMsgFileParser.parseFile(testInputStream, "testLogFile.bin"));
         assertEquals(expectedSpeed, testDistressMsgFileParser.getLocation().getSpeed());
         assertEquals(expectedStep, testDistressMsgFileParser.getStep());
      } catch (FileParserException e) {
         fail("Unexpected exception: " + e);
      }
   }

   /**
    * Step 4 test without enough bytes. Should return PARTIAL.
    */
   @Test
   public void testStep4Partial() {

      ParserStatus expectedStatus = ParserStatus.PARTIAL;
      int expectedStep = 4;

      BufferedInputStream testInputStream = new BufferedInputStream(
            new ByteArrayInputStream(new byte[] { 5, 4, 3, 2, 2, 3, 4, 5, 6, 7, 8, 9, 0xB }));

      try {
         assertEquals(expectedStatus, testDistressMsgFileParser.parseFile(testInputStream, "testLogFile.bin"));
         assertEquals(expectedStep, testDistressMsgFileParser.getStep());
      } catch (FileParserException e) {
         fail("Unexpected exception: " + e);
      }
   }

   /**
    * Step 5 test. Should extract the "location->heading" value, length 2 bytes,
    * then return EOF.
    */
   @Test
   public void testStep5() {

      ParserStatus expectedStatus = ParserStatus.EOF;
      long expectedHeading = 3082L; // A,C backwards as bytes
      int expectedStep = 6;

      BufferedInputStream testInputStream = new BufferedInputStream(
            new ByteArrayInputStream(new byte[] { 5, 4, 3, 2, 2, 3, 4, 5, 6, 7, 8, 9, 0xB, 0xD, 0xA, 0xC }));

      try {
         assertEquals(expectedStatus, testDistressMsgFileParser.parseFile(testInputStream, "testLogFile.bin"));
         assertEquals(expectedHeading, testDistressMsgFileParser.getLocation().getHeading());
         assertEquals(expectedStep, testDistressMsgFileParser.getStep());
      } catch (FileParserException e) {
         fail("Unexpected exception: " + e);
      }
   }

   /**
    * Step 5 test without enough bytes. Should return PARTIAL.
    */
   @Test
   public void testStep5Partial() {

      ParserStatus expectedStatus = ParserStatus.PARTIAL;
      int expectedStep = 5;

      BufferedInputStream testInputStream = new BufferedInputStream(
            new ByteArrayInputStream(new byte[] { 5, 4, 3, 2, 2, 3, 4, 5, 6, 7, 8, 9, 0xB, 0xD, 0xA }));

      try {
         assertEquals(expectedStatus, testDistressMsgFileParser.parseFile(testInputStream, "testLogFile.bin"));
         assertEquals(expectedStep, testDistressMsgFileParser.getStep());
      } catch (FileParserException e) {
         fail("Unexpected exception: " + e);
      }
   }

   /**
    * Step 6 test. Should extract the "utc time" value, length 4 bytes, then
    * return EOF.
    */
   @Test
   public void testStep6() {

      ParserStatus expectedStatus = ParserStatus.EOF;
      long expectedUtcTime = 67305985L; // 1,2,3,4 backwards as bytes
      int expectedStep = 7;

      BufferedInputStream testInputStream = new BufferedInputStream(new ByteArrayInputStream(
            new byte[] { 5, 4, 3, 2, 2, 3, 4, 5, 6, 7, 8, 9, 0xB, 0xD, 0xA, 0xC, 1, 2, 3, 4 }));

      try {
         assertEquals(expectedStatus, testDistressMsgFileParser.parseFile(testInputStream, "testLogFile.bin"));
         assertEquals(expectedUtcTime, testDistressMsgFileParser.getUtcTimeInSec());
         assertEquals(expectedStep, testDistressMsgFileParser.getStep());
      } catch (FileParserException e) {
         fail("Unexpected exception: " + e);
      }
   }

   /**
    * Step 6 test without enough bytes. Should return PARTIAL.
    */
   @Test
   public void testStep6Partial() {

      ParserStatus expectedStatus = ParserStatus.PARTIAL;
      int expectedStep = 6;

      BufferedInputStream testInputStream = new BufferedInputStream(
            new ByteArrayInputStream(new byte[] { 5, 4, 3, 2, 2, 3, 4, 5, 6, 7, 8, 9, 0xB, 0xD, 0xA, 0xC, 1, 2 }));

      try {
         assertEquals(expectedStatus, testDistressMsgFileParser.parseFile(testInputStream, "testLogFile.bin"));
         assertEquals(expectedStep, testDistressMsgFileParser.getStep());
      } catch (FileParserException e) {
         fail("Unexpected exception: " + e);
      }
   }

   /**
    * Step 7 test. Should extract the "msec time" value, length 2 bytes, then
    * return EOF.
    */
   @Test
   public void testStep7() {

      ParserStatus expectedStatus = ParserStatus.EOF;
      long expectedMsec = 266L; // A,1 backwards as bytes
      int expectedStep = 8;

      BufferedInputStream testInputStream = new BufferedInputStream(new ByteArrayInputStream(
            new byte[] { 5, 4, 3, 2, 2, 3, 4, 5, 6, 7, 8, 9, 0xB, 0xD, 0xA, 0xC, 1, 2, 3, 4, 0xA, 1 }));

      try {
         assertEquals(expectedStatus, testDistressMsgFileParser.parseFile(testInputStream, "testLogFile.bin"));
         assertEquals(expectedMsec, testDistressMsgFileParser.getmSec());
         assertEquals(expectedStep, testDistressMsgFileParser.getStep());
      } catch (FileParserException e) {
         fail("Unexpected exception: " + e);
      }
   }

   /**
    * Step 7 test without enough bytes. Should return PARTIAL.
    */
   @Test
   public void testStep7Partial() {

      ParserStatus expectedStatus = ParserStatus.PARTIAL;
      int expectedStep = 7;

      BufferedInputStream testInputStream = new BufferedInputStream(new ByteArrayInputStream(
            new byte[] { 5, 4, 3, 2, 2, 3, 4, 5, 6, 7, 8, 9, 0xB, 0xD, 0xA, 0xC, 1, 2, 3, 4, 0xA }));

      try {
         assertEquals(expectedStatus, testDistressMsgFileParser.parseFile(testInputStream, "testLogFile.bin"));
         assertEquals(expectedStep, testDistressMsgFileParser.getStep());
      } catch (FileParserException e) {
         fail("Unexpected exception: " + e);
      }
   }

   /**
    * Step 8 test. Should extract the "verification status" value, length 1
    * bytes, then return EOF.
    */
   @Test
   public void testStep8True() {

      ParserStatus expectedStatus = ParserStatus.EOF;
      boolean expectedVerificationStatus = true; // byte value 1 should be true
      int expectedStep = 9;

      BufferedInputStream testInputStream = new BufferedInputStream(new ByteArrayInputStream(
            new byte[] { 5, 4, 3, 2, 2, 3, 4, 5, 6, 7, 8, 9, 0xB, 0xD, 0xA, 0xC, 1, 2, 3, 4, 0xA, 1, 1 }));

      try {
         assertEquals(expectedStatus, testDistressMsgFileParser.parseFile(testInputStream, "testLogFile.bin"));
         assertEquals(expectedVerificationStatus, testDistressMsgFileParser.isValidSignature());
         assertEquals(expectedStep, testDistressMsgFileParser.getStep());
      } catch (FileParserException e) {
         fail("Unexpected exception: " + e);
      }
   }

   /**
    * Step 8 test. Should extract the "verification status" value, length 1
    * bytes, then return EOF.
    */
   @Test
   public void testStep8False() {

      ParserStatus expectedStatus = ParserStatus.EOF;
      boolean expectedVerificationStatus = false; // byte value 0 should be
                                                  // false
      int expectedStep = 9;

      BufferedInputStream testInputStream = new BufferedInputStream(new ByteArrayInputStream(
            new byte[] { 5, 4, 3, 2, 2, 3, 4, 5, 6, 7, 8, 9, 0xB, 0xD, 0xA, 0xC, 1, 2, 3, 4, 0xA, 1, 0 }));

      try {
         assertEquals(expectedStatus, testDistressMsgFileParser.parseFile(testInputStream, "testLogFile.bin"));
         assertEquals(expectedVerificationStatus, testDistressMsgFileParser.isValidSignature());
         assertEquals(expectedStep, testDistressMsgFileParser.getStep());
      } catch (FileParserException e) {
         fail("Unexpected exception: " + e);
      }
   }

   /**
    * Step 9 test. Should extract the "payload length" value, length 2 bytes,
    * then return EOF.
    */
   @Test
   public void testStep9() {

      ParserStatus expectedStatus = ParserStatus.EOF;
      long expectedLength = 2317L; // D,9 backwards as bytes
      int expectedStep = 10;

      BufferedInputStream testInputStream = new BufferedInputStream(new ByteArrayInputStream(new byte[] { 5, 4, 3, 2, 2,
            3, 4, 5, 6, 7, 8, 9, 0xB, 0xD, 0xA, 0xC, 1, 2, 3, 4, 0xA, 1, 0, 0xD, 9 }));

      try {
         assertEquals(expectedStatus, testDistressMsgFileParser.parseFile(testInputStream, "testLogFile.bin"));
         assertEquals(expectedLength, testDistressMsgFileParser.getLength());
         assertEquals(expectedStep, testDistressMsgFileParser.getStep());
      } catch (FileParserException e) {
         fail("Unexpected exception: " + e);
      }
   }

   /**
    * Step 9 test without enough bytes. Should return PARTIAL.
    */
   @Test
   public void testStep9Partial() {

      ParserStatus expectedStatus = ParserStatus.PARTIAL;
      int expectedStep = 9;

      BufferedInputStream testInputStream = new BufferedInputStream(new ByteArrayInputStream(new byte[] { 5, 4, 3, 2, 2,
            3, 4, 5, 6, 7, 8, 9, 0xB, 0xD, 0xA, 0xC, 1, 2, 3, 4, 0xA, 1, 0, 0xD }));

      try {
         assertEquals(expectedStatus, testDistressMsgFileParser.parseFile(testInputStream, "testLogFile.bin"));
         assertEquals(expectedStep, testDistressMsgFileParser.getStep());
      } catch (FileParserException e) {
         fail("Unexpected exception: " + e);
      }
   }

   /**
    * Step 10 test. Should extract the "payload" value, length ${length} bytes,
    * then return COMPLETE. Should also reset step to 0.
    */
   @Test
   public void testStep10() {

      ParserStatus expectedStatus = ParserStatus.COMPLETE;
      long expectedLength = 5L;
      byte[] expectedPayload = new byte[] { 1, 0xD, 5, 0xA, 0xC }; // 5 bytes
      int expectedStep = 0;

      BufferedInputStream testInputStream = new BufferedInputStream(new ByteArrayInputStream(new byte[] { 5, 4, 3, 2, 2,
            3, 4, 5, 6, 7, 8, 9, 0xB, 0xD, 0xA, 0xC, 1, 2, 3, 4, 0xA, 1, 0, 5, 0, 1, 0xD, 5, 0xA, 0xC }));

      try {
         assertEquals(expectedStatus, testDistressMsgFileParser.parseFile(testInputStream, "testLogFile.bin"));
         assertEquals(expectedLength, testDistressMsgFileParser.getLength());
         assertEquals(HexUtils.toHexString(expectedPayload), HexUtils.toHexString(testDistressMsgFileParser.getPayload()));
         assertEquals(expectedStep, testDistressMsgFileParser.getStep());
      } catch (FileParserException e) {
         fail("Unexpected exception: " + e);
      }
   }

   /**
    * Step 10 test without enough bytes. Should return PARTIAL.
    */
   @Test
   public void testStep10Partial() {

      ParserStatus expectedStatus = ParserStatus.PARTIAL;
      int expectedStep = 10;

      BufferedInputStream testInputStream = new BufferedInputStream(new ByteArrayInputStream(new byte[] { 5, 4, 3, 2, 2,
            3, 4, 5, 6, 7, 8, 9, 0xB, 0xD, 0xA, 0xC, 1, 2, 3, 4, 0xA, 1, 0, 5, 0, 1, 0xD, 5, 0xA }));

      try {
         assertEquals(expectedStatus, testDistressMsgFileParser.parseFile(testInputStream, "testLogFile.bin"));
         assertEquals(expectedStep, testDistressMsgFileParser.getStep());
      } catch (FileParserException e) {
         fail("Unexpected exception: " + e);
      }
   }

}
