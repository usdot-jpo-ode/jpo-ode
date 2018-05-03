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
import us.dot.its.jpo.ode.model.OdeLogMetadata.SecurityResultCode;

public class SecResultCodeParserTest {
   
   @Tested
   SecurityResultCodeParser secResultCodeParser;
   @Injectable long bundleId;

   /**
    * Should extract securityResultCode and return ParserStatus.COMPLETE
    */
   @Test
   public void testAll() {

      ParserStatus expectedStatus = ParserStatus.COMPLETE;
      int expectedStep = 0;

      BufferedInputStream testInputStream = new BufferedInputStream(
         new ByteArrayInputStream(new byte[] { 
               (byte)0x00,                                     //0. securityResultCode
               }));

      try {
         assertEquals(expectedStatus, secResultCodeParser.parseFile(testInputStream, "testLogFile.bin"));
         assertEquals(SecurityResultCode.success, secResultCodeParser.getSecurityResultCode());
         assertEquals(expectedStep, secResultCodeParser.getStep());
      } catch (FileParserException e) {
         fail("Unexpected exception: " + e);
      }
   }

   /**
    * Test securityResultCode = unknown
    */
   @Test
   public void testSecurityResultCodeUnknown() {

      ParserStatus expectedStatus = ParserStatus.COMPLETE;
      int expectedStep = 0;

      BufferedInputStream testInputStream = new BufferedInputStream(
         new ByteArrayInputStream(new byte[] { 
               (byte)0x01,                                     //0. securityResultCode
               }));

      try {
         assertEquals(expectedStatus, secResultCodeParser.parseFile(testInputStream, "testLogFile.bin"));
         assertEquals(SecurityResultCode.unknown, secResultCodeParser.getSecurityResultCode());
         assertEquals(expectedStep, secResultCodeParser.getStep());
      } catch (FileParserException e) {
         fail("Unexpected exception: " + e);
      }
   }

   /**
    * Test securityResultCode failure
    */
   @Test
   public void testSecurityResultCodeFailure() {

      ParserStatus expectedStatus = ParserStatus.COMPLETE;
      int expectedStep = 0;

      BufferedInputStream testInputStream = new BufferedInputStream(
         new ByteArrayInputStream(new byte[] { 
               (byte)0x02,                                     //0. securityResultCode
               }));

      try {
         assertEquals(expectedStatus, secResultCodeParser.parseFile(testInputStream, "testLogFile.bin"));
         assertEquals(SecurityResultCode.inconsistentInputParameters, secResultCodeParser.getSecurityResultCode());
         assertEquals(expectedStep, secResultCodeParser.getStep());
      } catch (FileParserException e) {
         fail("Unexpected exception: " + e);
      }
   }

}
