package us.dot.its.jpo.ode.importer.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;

import org.junit.Test;

import mockit.Injectable;
import mockit.Tested;
import us.dot.its.jpo.ode.importer.parser.FileParser.FileParserException;
import us.dot.its.jpo.ode.importer.parser.FileParser.ParserStatus;

public class TimeParserTest {
   
   @Tested
   TimeParser timeParser;
   @Injectable long bundleId;

   /**
    * Should extract all time fields and return ParserStatus.COMPLETE
    */
   @Test
   public void testAll() {

      ParserStatus expectedStatus = ParserStatus.COMPLETE;
      int expectedStep = 0;

      BufferedInputStream testInputStream = new BufferedInputStream(
         new ByteArrayInputStream(new byte[] { 
               (byte)0xa9, (byte)0x2c, (byte)0xe2, (byte)0x5a, //0. utcTimeInSec
               (byte)0x8f, (byte)0x01,                         //1. mSec
               }));

      try {
         assertEquals(expectedStatus, timeParser.parseFile(testInputStream, "testLogFile.bin"));
         assertEquals(1524772009, timeParser.getUtcTimeInSec());
         assertEquals(399, timeParser.getmSec());
         assertEquals(expectedStep, timeParser.getStep());
      } catch (FileParserException e) {
         fail("Unexpected exception: " + e);
      }
   }

   /**
    * test PARTIAL utcTimeInSec.
    */
   @Test
   public void testPartialUtcTimeInSec() {

      ParserStatus expectedStatus = ParserStatus.PARTIAL;
      int expectedStep = 0;

      BufferedInputStream testInputStream = new BufferedInputStream(
         new ByteArrayInputStream(new byte[] { 
               (byte)0xa9, (byte)0x2c//, (byte)0xe2, (byte)0x5a, //0. utcTimeInSec
               //(byte)0x8f, (byte)0x01,                         //1. mSec
               }));

      try {
         assertEquals(expectedStatus, timeParser.parseFile(testInputStream, "testLogFile.bin"));
         assertEquals(expectedStep, timeParser.getStep());
      } catch (FileParserException e) {
         fail("Unexpected exception: " + e);
      }
   }

   /**
    * test PARTIAL mSec field.
    */
   @Test
   public void testPartialMSec() {

      ParserStatus expectedStatus = ParserStatus.PARTIAL;
      int expectedStep = 1;

      BufferedInputStream testInputStream = new BufferedInputStream(
         new ByteArrayInputStream(new byte[] { 
               (byte)0xa9, (byte)0x2c, (byte)0xe2, (byte)0x5a, //0. utcTimeInSec
               (byte)0x8f//, (byte)0x01,                         //1. mSec
               }));

      try {
         assertEquals(expectedStatus, timeParser.parseFile(testInputStream, "testLogFile.bin"));
         assertEquals(expectedStep, timeParser.getStep());
      } catch (FileParserException e) {
         fail("Unexpected exception: " + e);
      }
   }

}
