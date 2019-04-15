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
import us.dot.its.jpo.ode.model.OdeLogMetadata.SecurityResultCode;
import us.dot.its.jpo.ode.util.CodecUtils;

public class DistressMsgFileParserTest {
   
   @Tested
   DistressMsgFileParser testDistressMsgFileParser;
   @Injectable long bundleId;

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
   public void testAll() {

      ParserStatus expectedStatus = ParserStatus.COMPLETE;
      byte[] expectedPayload = new byte[] { (byte)0x03, (byte)0x81, (byte)0x00, (byte)0x40, (byte)0x03, (byte)0x80 };
      int expectedStep = 0;

      byte[] buf = new byte[] { 
             (byte)0x6f, (byte)0x75, (byte)0x4d, (byte)0x19, //1.1 latitude
             (byte)0xa4, (byte)0xa1, (byte)0x5c, (byte)0xce, //1.2 longitude
             (byte)0x67, (byte)0x06, (byte)0x00, (byte)0x00, //1.3 elevation
             (byte)0x04, (byte)0x00,                         //1.4 speed
             (byte)0x09, (byte)0x27,                         //1.5 heading
             (byte)0xa9, (byte)0x2c, (byte)0xe2, (byte)0x5a, //2. utcTimeInSec
             (byte)0x8f, (byte)0x01,                         //3. mSec
             (byte)0x00,                                     //4. securityResultCode
             (byte)0x06, (byte)0x00,                         //5.1 payloadLength
                                                             //5.2 payload
             (byte)0x03, (byte)0x81, (byte)0x00, (byte)0x40, (byte)0x03, (byte)0x80 
             };
      BufferedInputStream testInputStream = new BufferedInputStream(new ByteArrayInputStream(buf));

      try {
         assertEquals(expectedStatus, testDistressMsgFileParser.parseFile(testInputStream, "testLogFile.bin"));
         assertEquals(424506735L, testDistressMsgFileParser.getLocationParser().getLocation().getLatitude());
         assertEquals(-832790108L, testDistressMsgFileParser.getLocationParser().getLocation().getLongitude());
         assertEquals(1639L, testDistressMsgFileParser.getLocationParser().getLocation().getElevation());
         assertEquals(4, testDistressMsgFileParser.getLocationParser().getLocation().getSpeed());
         assertEquals(9993, testDistressMsgFileParser.getLocationParser().getLocation().getHeading());
         assertEquals(1524772009, testDistressMsgFileParser.getTimeParser().getUtcTimeInSec());
         assertEquals(399, testDistressMsgFileParser.getTimeParser().getmSec());
         assertEquals(SecurityResultCode.success, testDistressMsgFileParser.getSecResCodeParser().getSecurityResultCode());
         assertEquals(6, testDistressMsgFileParser.getPayloadParser().getPayloadLength());
         assertEquals(HexUtils.toHexString(expectedPayload), HexUtils.toHexString(testDistressMsgFileParser.getPayloadParser().getPayload()));
         assertEquals(expectedStep, testDistressMsgFileParser.getStep());
         
         ByteArrayOutputStream os = new ByteArrayOutputStream();
         testDistressMsgFileParser.writeTo(os);
         assertEquals(CodecUtils.toHex(buf), CodecUtils.toHex(os.toByteArray()));
      } catch (FileParserException | IOException e) {
         fail("Unexpected exception: " + e);
      }
   }

   /**
    * Step 1 test without enough bytes. Should return PARTIAL.
    */
   @Test
   public void testStep1_1Partial() {

      ParserStatus expectedStatus = ParserStatus.PARTIAL;
      int expectedStep = 1;

      BufferedInputStream testInputStream = new BufferedInputStream(
         new ByteArrayInputStream(new byte[] { 
               (byte)0x6f, (byte)0x75//, (byte)0x4d, (byte)0x19, //1.1 latitude
               //(byte)0xa4, (byte)0xa1, (byte)0x5c, (byte)0xce, //1.2 longitude
               //(byte)0x67, (byte)0x06, (byte)0x00, (byte)0x00, //1.3 elevation
               //(byte)0x04, (byte)0x00,                         //1.4 speed
               //(byte)0x09, (byte)0x27,                         //1.5 heading
               //(byte)0xa9, (byte)0x2c, (byte)0xe2, (byte)0x5a, //2. utcTimeInSec
               //(byte)0x8f, (byte)0x01,                         //3. mSec
               //(byte)0x00,                                     //4. securityResultCode
               //(byte)0x06, (byte)0x00,                         //5.1 payloadLength
                                                               //5.2 payload
               //(byte)0x03, (byte)0x81, (byte)0x00, (byte)0x40, (byte)0x03, (byte)0x80 
               }));

      try {
         assertEquals(expectedStatus, testDistressMsgFileParser.parseFile(testInputStream, "testLogFile.bin"));
         assertEquals(expectedStep, testDistressMsgFileParser.getStep());
      } catch (FileParserException e) {
         fail("Unexpected exception: " + e);
      }
   }

   /**
    * Step 2 test without enough bytes. Should return PARTIAL.
    */
   @Test
   public void testStep1_2Partial() {

      ParserStatus expectedStatus = ParserStatus.PARTIAL;
      int expectedStep = 1;

      BufferedInputStream testInputStream = new BufferedInputStream(
         new ByteArrayInputStream(new byte[] { 
               (byte)0x6f, (byte)0x75, (byte)0x4d, (byte)0x19, //1.1 latitude
               (byte)0xa4, (byte)0xa1//, (byte)0x5c, (byte)0xce, //1.2 longitude
               //(byte)0x67, (byte)0x06, (byte)0x00, (byte)0x00, //1.3 elevation
               //(byte)0x04, (byte)0x00,                         //1.4 speed
               //(byte)0x09, (byte)0x27,                         //1.5 heading
               //(byte)0xa9, (byte)0x2c, //(byte)0xe2, (byte)0x5a, //2. utcTimeInSec
               //(byte)0x8f, (byte)0x01,                         //3. mSec
               //(byte)0x00,                                     //4. securityResultCode
               //(byte)0x06, (byte)0x00,                         //5.1 payloadLength
                                                               //5.2 payload
               //(byte)0x03, (byte)0x81, (byte)0x00, (byte)0x40, (byte)0x03, (byte)0x80 
               }));

      try {
         assertEquals(expectedStatus, testDistressMsgFileParser.parseFile(testInputStream, "testLogFile.bin"));
         assertEquals(expectedStep, testDistressMsgFileParser.getStep());
      } catch (FileParserException e) {
         fail("Unexpected exception: " + e);
      }
   }

   /**
    * Step 3 test without enough bytes. Should return PARTIAL.
    */
   @Test
   public void testStep1_3Partial() {

      ParserStatus expectedStatus = ParserStatus.PARTIAL;
      int expectedStep = 1;

      BufferedInputStream testInputStream = new BufferedInputStream(
         new ByteArrayInputStream(new byte[] { 
               (byte)0x6f, (byte)0x75, (byte)0x4d, (byte)0x19, //1.1 latitude
               (byte)0xa4, (byte)0xa1, (byte)0x5c, (byte)0xce, //1.2 longitude
               (byte)0x67, (byte)0x06//, (byte)0x00, (byte)0x00, //1.3 elevation
               //(byte)0x04, (byte)0x00,                         //1.4 speed
               //(byte)0x09, (byte)0x27,                         //1.5 heading
               //(byte)0xa9, (byte)0x2c, (byte)0xe2, (byte)0x5a, //2. utcTimeInSec
               //(byte)0x8f, (byte)0x01,                         //3. mSec
               //(byte)0x00,                                     //4. securityResultCode
               //(byte)0x06, (byte)0x00,                         //5.1 payloadLength
                                                               //5.2 payload
               //(byte)0x03, (byte)0x81, (byte)0x00, (byte)0x40, (byte)0x03, (byte)0x80 
               }));

      try {
         assertEquals(expectedStatus, testDistressMsgFileParser.parseFile(testInputStream, "testLogFile.bin"));
         assertEquals(expectedStep, testDistressMsgFileParser.getStep());
      } catch (FileParserException e) {
         fail("Unexpected exception: " + e);
      }
   }

   /**
    * Step 4 test without enough bytes. Should return PARTIAL.
    */
   @Test
   public void testStep1_4Partial() {

      ParserStatus expectedStatus = ParserStatus.PARTIAL;
      int expectedStep = 1;

      BufferedInputStream testInputStream = new BufferedInputStream(
         new ByteArrayInputStream(new byte[] { 
               (byte)0x6f, (byte)0x75, (byte)0x4d, (byte)0x19, //1.1 latitude
               (byte)0xa4, (byte)0xa1, (byte)0x5c, (byte)0xce, //1.2 longitude
               (byte)0x67, (byte)0x06, (byte)0x00, (byte)0x00, //1.3 elevation
               (byte)0x04//, (byte)0x00,                         //1.4 speed
               //(byte)0x09, (byte)0x27,                         //1.5 heading
               //(byte)0xa9, (byte)0x2c, (byte)0xe2, (byte)0x5a, //2. utcTimeInSec
               //(byte)0x8f, (byte)0x01,                         //3. mSec
               //(byte)0x00,                                     //4. securityResultCode
               //(byte)0x06, (byte)0x00,                         //5.1 payloadLength
                                                               //5.2 payload
               //(byte)0x03, (byte)0x81, (byte)0x00, (byte)0x40, (byte)0x03, (byte)0x80 
               }));

      try {
         assertEquals(expectedStatus, testDistressMsgFileParser.parseFile(testInputStream, "testLogFile.bin"));
         assertEquals(expectedStep, testDistressMsgFileParser.getStep());
      } catch (FileParserException e) {
         fail("Unexpected exception: " + e);
      }
   }

   /**
    * Step 5 test without enough bytes. Should return PARTIAL.
    */
   @Test
   public void testStep1_5Partial() {

      ParserStatus expectedStatus = ParserStatus.PARTIAL;
      int expectedStep = 1;

      BufferedInputStream testInputStream = new BufferedInputStream(
         new ByteArrayInputStream(new byte[] { 
               (byte)0x6f, (byte)0x75, (byte)0x4d, (byte)0x19, //1.1 latitude
               (byte)0xa4, (byte)0xa1, (byte)0x5c, (byte)0xce, //1.2 longitude
               (byte)0x67, (byte)0x06, (byte)0x00, (byte)0x00, //1.3 elevation
               (byte)0x04, (byte)0x00,                         //1.4 speed
               (byte)0x09//, (byte)0x27,                         //1.5 heading
               //(byte)0xa9, (byte)0x2c, (byte)0xe2, (byte)0x5a, //2. utcTimeInSec
               //(byte)0x8f, (byte)0x01,                         //3. mSec
               //(byte)0x00,                                     //4. securityResultCode
               //(byte)0x06, (byte)0x00,                         //5.1 payloadLength
                                                               //5.2 payload
               //(byte)0x03, (byte)0x81//, (byte)0x00, (byte)0x40, (byte)0x03, (byte)0x80 
               }));

      try {
         assertEquals(expectedStatus, testDistressMsgFileParser.parseFile(testInputStream, "testLogFile.bin"));
         assertEquals(expectedStep, testDistressMsgFileParser.getStep());
      } catch (FileParserException e) {
         fail("Unexpected exception: " + e);
      }
   }

   /**
    * Step 7 test without enough bytes. Should return PARTIAL.
    */
   @Test
   public void testStep2Partial() {

      ParserStatus expectedStatus = ParserStatus.PARTIAL;
      int expectedStep = 2;

      BufferedInputStream testInputStream = new BufferedInputStream(
         new ByteArrayInputStream(new byte[] { 
               (byte)0x6f, (byte)0x75, (byte)0x4d, (byte)0x19, //1.1 latitude
               (byte)0xa4, (byte)0xa1, (byte)0x5c, (byte)0xce, //1.2 longitude
               (byte)0x67, (byte)0x06, (byte)0x00, (byte)0x00, //1.3 elevation
               (byte)0x04, (byte)0x00,                         //1.4 speed
               (byte)0x09, (byte)0x27,                         //1.5heading
               (byte)0xa9, (byte)0x2c//, (byte)0xe2, (byte)0x5a, //2. utcTimeInSec
               //(byte)0x8f, (byte)0x01,                         //3. mSec
               //(byte)0x00,                                     //4. securityResultCode
               //(byte)0x06, (byte)0x00,                         //5.1 payloadLength
                                                               //5.2 payload
               //(byte)0x03, (byte)0x81, (byte)0x00, (byte)0x40, (byte)0x03, (byte)0x80 
               }));

      try {
         assertEquals(expectedStatus, testDistressMsgFileParser.parseFile(testInputStream, "testLogFile.bin"));
         assertEquals(expectedStep, testDistressMsgFileParser.getStep());
      } catch (FileParserException e) {
         fail("Unexpected exception: " + e);
      }
   }

   /**
    * Step 7 test without enough bytes. Should return PARTIAL.
    */
   @Test
   public void testStep3Partial() {

      ParserStatus expectedStatus = ParserStatus.PARTIAL;
      int expectedStep = 2;

      BufferedInputStream testInputStream = new BufferedInputStream(
         new ByteArrayInputStream(new byte[] { 
               (byte)0x6f, (byte)0x75, (byte)0x4d, (byte)0x19, //1.1 latitude
               (byte)0xa4, (byte)0xa1, (byte)0x5c, (byte)0xce, //1.2 longitude
               (byte)0x67, (byte)0x06, (byte)0x00, (byte)0x00, //1.3 elevation
               (byte)0x04, (byte)0x00,                         //1.4 speed
               (byte)0x09, (byte)0x27,                         //1.5heading
               (byte)0xa9, (byte)0x2c, (byte)0xe2, (byte)0x5a, //2. utcTimeInSec
               (byte)0x8f//, (byte)0x01,                         //3. mSec
               //(byte)0x00,                                     //4. securityResultCode
               //(byte)0x06, (byte)0x00,                         //5.1 payloadLength
                                                               //5.2 payload
               //(byte)0x03, (byte)0x81, (byte)0x00, (byte)0x40, (byte)0x03, (byte)0x80 
               }));

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
   public void testStep4Unknown() {

      ParserStatus expectedStatus = ParserStatus.COMPLETE;
      int expectedStep = 0;

      BufferedInputStream testInputStream = new BufferedInputStream(
         new ByteArrayInputStream(new byte[] { 
               (byte)0x6f, (byte)0x75, (byte)0x4d, (byte)0x19, //1.1 latitude
               (byte)0xa4, (byte)0xa1, (byte)0x5c, (byte)0xce, //1.2 longitude
               (byte)0x67, (byte)0x06, (byte)0x00, (byte)0x00, //1.3 elevation
               (byte)0x04, (byte)0x00,                         //1.4 speed
               (byte)0x09, (byte)0x27,                         //1.5heading
               (byte)0xa9, (byte)0x2c, (byte)0xe2, (byte)0x5a, //2. utcTimeInSec
               (byte)0x8f, (byte)0x01,                         //3. mSec
               (byte)0x01,                                     //4. securityResultCode
               (byte)0x06, (byte)0x00,                         //5.1 payloadLength
                                                               //5.2 payload
               (byte)0x03, (byte)0x81, (byte)0x00, (byte)0x40, (byte)0x03, (byte)0x80 
               }));

      try {
         assertEquals(expectedStatus, testDistressMsgFileParser.parseFile(testInputStream, "testLogFile.bin"));
         assertEquals(SecurityResultCode.unknown, testDistressMsgFileParser.getSecResCodeParser().getSecurityResultCode());
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
   public void testStep4Failure() {

      ParserStatus expectedStatus = ParserStatus.COMPLETE;
      int expectedStep = 0;

      BufferedInputStream testInputStream = new BufferedInputStream(
         new ByteArrayInputStream(new byte[] { 
               (byte)0x6f, (byte)0x75, (byte)0x4d, (byte)0x19, //1.1 latitude
               (byte)0xa4, (byte)0xa1, (byte)0x5c, (byte)0xce, //1.2 longitude
               (byte)0x67, (byte)0x06, (byte)0x00, (byte)0x00, //1.3 elevation
               (byte)0x04, (byte)0x00,                         //1.4 speed
               (byte)0x09, (byte)0x27,                         //1.5heading
               (byte)0xa9, (byte)0x2c, (byte)0xe2, (byte)0x5a, //2. utcTimeInSec
               (byte)0x8f, (byte)0x01,                         //3. mSec
               (byte)0x02,                                     //4. securityResultCode
               (byte)0x06, (byte)0x00,                         //5.1 payloadLength
                                                               //5.2 payload
               (byte)0x03, (byte)0x81, (byte)0x00, (byte)0x40, (byte)0x03, (byte)0x80 
               }));

      try {
         assertEquals(expectedStatus, testDistressMsgFileParser.parseFile(testInputStream, "testLogFile.bin"));
         assertEquals(SecurityResultCode.inconsistentInputParameters, testDistressMsgFileParser.getSecResCodeParser().getSecurityResultCode());
         assertEquals(expectedStep, testDistressMsgFileParser.getStep());
      } catch (FileParserException e) {
         fail("Unexpected exception: " + e);
      }
   }

   /**
    * Step 10 test without enough bytes. Should return PARTIAL.
    */
   @Test
   public void testPayloadPartial() {

      ParserStatus expectedStatus = ParserStatus.PARTIAL;
      int expectedStep = 4;

      BufferedInputStream testInputStream = new BufferedInputStream(
         new ByteArrayInputStream(new byte[] { 
               (byte)0x6f, (byte)0x75, (byte)0x4d, (byte)0x19, //1.1 latitude
               (byte)0xa4, (byte)0xa1, (byte)0x5c, (byte)0xce, //1.2 longitude
               (byte)0x67, (byte)0x06, (byte)0x00, (byte)0x00, //1.3 elevation
               (byte)0x04, (byte)0x00,                         //1.4 speed
               (byte)0x09, (byte)0x27,                         //1.5heading
               (byte)0xa9, (byte)0x2c, (byte)0xe2, (byte)0x5a, //2. utcTimeInSec
               (byte)0x8f, (byte)0x01,                         //3. mSec
               (byte)0x00,                                     //4. securityResultCode
               (byte)0x06, (byte)0x00,                         //5.1 payloadLength
                                                               //5.2 payload
               (byte)0x03, (byte)0x81//, (byte)0x00, (byte)0x40, (byte)0x03, (byte)0x80 
               }));

      try {
         assertEquals(expectedStatus, testDistressMsgFileParser.parseFile(testInputStream, "testLogFile.bin"));
         assertEquals(expectedStep, testDistressMsgFileParser.getStep());
      } catch (FileParserException e) {
         fail("Unexpected exception: " + e);
      }
   }

}
