/*******************************************************************************
 * Copyright 2018 572682
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package us.dot.its.jpo.ode.importer.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.tomcat.util.buf.HexUtils;
import org.junit.Test;

import mockit.Injectable;
import mockit.Tested;
import us.dot.its.jpo.ode.importer.parser.FileParser.FileParserException;
import us.dot.its.jpo.ode.importer.parser.FileParser.ParserStatus;
import us.dot.its.jpo.ode.model.OdeBsmMetadata.BsmSource;
import us.dot.its.jpo.ode.model.OdeLogMetadata.SecurityResultCode;
import us.dot.its.jpo.ode.util.CodecUtils;

public class BsmFileParserTest {

   @Tested
   BsmLogFileParser bsmFileParser;
   @Injectable long bundleId;
   

   /**
    * Silly test for coverage
    */
   @Test
   public void testStepsAlreadyDone() {

      ParserStatus expectedStatus = ParserStatus.COMPLETE;

      BufferedInputStream testInputStream = new BufferedInputStream(new ByteArrayInputStream(new byte[0]));

      try {
         bsmFileParser.setStep(7);
         assertEquals(expectedStatus, bsmFileParser.parseFile(testInputStream, "testLogFile.bin"));
      } catch (FileParserException e) {
         fail("Unexpected exception: " + e);
      }

   }

   /**
    * Step 1 test. Should extract the "location->latitude" value, length 4
    * bytes, then return EOF.
    */
   @Test
   public void testAll() {

      try {
        ParserStatus expectedStatus = ParserStatus.COMPLETE;
        byte[] expectedPayload = new byte[] { (byte)0x03, (byte)0x81, (byte)0x00, (byte)0x40, (byte)0x03, (byte)0x80 };
        int expectedStep = 0;

        byte[] buf = new byte[] { 
               (byte)0x00,                                     //1. direction 
               (byte)0x6f, (byte)0x75, (byte)0x4d, (byte)0x19, //2.0 latitude
               (byte)0xa4, (byte)0xa1, (byte)0x5c, (byte)0xce, //2.1 longitude
               (byte)0x67, (byte)0x06, (byte)0x00, (byte)0x00, //2.3 elevation
               (byte)0x04, (byte)0x00,                         //2.3 speed
               (byte)0x09, (byte)0x27,                         //2.4 heading
               (byte)0xa9, (byte)0x2c, (byte)0xe2, (byte)0x5a, //3. utcTimeInSec
               (byte)0x8f, (byte)0x01,                         //4. mSec
               (byte)0x00,                                     //5. securityResultCode
               (byte)0x06, (byte)0x00,                         //6.0 payloadLength
                                                               //6.1 payload
               (byte)0x03, (byte)0x81, (byte)0x00, (byte)0x40, (byte)0x03, (byte)0x80 
               };
        BufferedInputStream testInputStream = new BufferedInputStream(new ByteArrayInputStream(buf));

        assertEquals(expectedStatus, bsmFileParser.parseFile(testInputStream, "testLogFile.bin"));
        assertEquals(BsmSource.EV, bsmFileParser.getBsmSource());
        assertEquals(424506735L, bsmFileParser.getLocationParser().getLocation().getLatitude());
        assertEquals(-832790108L, bsmFileParser.getLocationParser().getLocation().getLongitude());
        assertEquals(1639L, bsmFileParser.getLocationParser().getLocation().getElevation());
        assertEquals(4, bsmFileParser.getLocationParser().getLocation().getSpeed());
        assertEquals(9993, bsmFileParser.getLocationParser().getLocation().getHeading());
        assertEquals(1524772009, bsmFileParser.getTimeParser().getUtcTimeInSec());
        assertEquals(399, bsmFileParser.getTimeParser().getmSec());
        assertEquals(SecurityResultCode.success, bsmFileParser.getSecResCodeParser().getSecurityResultCode());
        assertEquals(6, bsmFileParser.getPayloadParser().getPayloadLength());
        assertEquals(HexUtils.toHexString(expectedPayload), HexUtils.toHexString(bsmFileParser.getPayloadParser().getPayload()));
        assertEquals(expectedStep, bsmFileParser.getStep());
         
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        bsmFileParser.writeTo(os);
        assertEquals(CodecUtils.toHex(buf), CodecUtils.toHex(os.toByteArray()));
      } catch (FileParserException | IOException e) {
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
         assertEquals(expectedStatus, bsmFileParser.parseFile(testInputStream, "testLogFile.bin"));
         assertEquals(testFileName, bsmFileParser.getFilename());
         assertEquals(expectedStep, bsmFileParser.getStep());
      } catch (FileParserException e) {
         fail("Unexpected exception: " + e);
      }
   }

   /**
    * Step 1 test. Should extract BsmSource.RV and return EOF.
    */
   @Test
   public void testDirectionEV() {

      int testDirection = 0;
      ParserStatus expectedStatus = ParserStatus.EOF;
      BsmSource expectedDirection = BsmSource.EV;
      int expectedStep = 2;

      BufferedInputStream testInputStream = new BufferedInputStream(
            new ByteArrayInputStream(new byte[] { (byte) testDirection }));

      try {
         assertEquals(expectedStatus, bsmFileParser.parseFile(testInputStream, "testLogFile.bin"));
         assertEquals(expectedDirection, bsmFileParser.getBsmSource());
         assertEquals(expectedStep, bsmFileParser.getStep());
      } catch (FileParserException e) {
         fail("Unexpected exception: " + e);
      }
   }

   /**
    * Step 1 test. Should extract BsmSource.RV and return EOF.
    */
   @Test
   public void testDirectionRV() {

      int testDirection = 1;
      ParserStatus expectedStatus = ParserStatus.EOF;
      BsmSource expectedDirection = BsmSource.RV;
      int expectedStep = 2;

      BufferedInputStream testInputStream = new BufferedInputStream(
            new ByteArrayInputStream(new byte[] { (byte) testDirection }));

      try {
         assertEquals(expectedStatus, bsmFileParser.parseFile(testInputStream, "testLogFile.bin"));
         assertEquals(expectedDirection, bsmFileParser.getBsmSource());
         assertEquals(expectedStep, bsmFileParser.getStep());
      } catch (FileParserException e) {
         fail("Unexpected exception: " + e);
      }
   }

   /**
    * Step 1 test. Should extract BsmSource.unknown and return EOF.
    */
   @Test
   public void testDirectionUnknown() {

      int testDirection = 111;
      ParserStatus expectedStatus = ParserStatus.EOF;
      BsmSource expectedDirection = BsmSource.unknown;
      int expectedStep = 2;

      BufferedInputStream testInputStream = new BufferedInputStream(
            new ByteArrayInputStream(new byte[] { (byte) testDirection }));

      try {
         assertEquals(expectedStatus, bsmFileParser.parseFile(testInputStream, "testLogFile.bin"));
         assertEquals(expectedDirection, bsmFileParser.getBsmSource());
         assertEquals(expectedStep, bsmFileParser.getStep());
      } catch (FileParserException e) {
         fail("Unexpected exception: " + e);
      }
   }

}
