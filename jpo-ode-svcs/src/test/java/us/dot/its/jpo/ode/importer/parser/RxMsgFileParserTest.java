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
import us.dot.its.jpo.ode.model.RxSource;

public class RxMsgFileParserTest {

   @Tested
   RxMsgFileParser testRxMsgFileParser;
   @Injectable long bundleId;

   /**
    * Silly test for coverage
    */
   @Test
   public void testStepsAlreadyDone() {

      ParserStatus expectedStatus = ParserStatus.COMPLETE;

      BufferedInputStream testInputStream = new BufferedInputStream(new ByteArrayInputStream(new byte[0]));

      try {
        testRxMsgFileParser.setStep(6);
        assertEquals(expectedStatus, testRxMsgFileParser.parseFile(testInputStream, "testLogFile.bin"));
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
             (byte)0x02,                                     //1. RxSource = RV 
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

      try {
         assertEquals(expectedStatus, testRxMsgFileParser.parseFile(testInputStream, "testLogFile.bin"));
         assertEquals(RxSource.RV, testRxMsgFileParser.getRxSource());
         assertEquals(424506735L, testRxMsgFileParser.getLocationParser().getLocation().getLatitude());
         assertEquals(-832790108L, testRxMsgFileParser.getLocationParser().getLocation().getLongitude());
         assertEquals(1639L, testRxMsgFileParser.getLocationParser().getLocation().getElevation());
         assertEquals(4, testRxMsgFileParser.getLocationParser().getLocation().getSpeed());
         assertEquals(9993, testRxMsgFileParser.getLocationParser().getLocation().getHeading());
         assertEquals(1524772009, testRxMsgFileParser.getTimeParser().getUtcTimeInSec());
         assertEquals(399, testRxMsgFileParser.getTimeParser().getmSec());
         assertEquals(SecurityResultCode.success, testRxMsgFileParser.getSecResCodeParser().getSecurityResultCode());
         assertEquals(6, testRxMsgFileParser.getPayloadParser().getPayloadLength());
         assertEquals(HexUtils.toHexString(expectedPayload), HexUtils.toHexString(testRxMsgFileParser.getPayloadParser().getPayload()));
         assertEquals(expectedStep, testRxMsgFileParser.getStep());
         
         ByteArrayOutputStream os = new ByteArrayOutputStream();
         testRxMsgFileParser.writeTo(os);
         assertEquals(CodecUtils.toHex(buf), CodecUtils.toHex(os.toByteArray()));
      } catch (FileParserException | IOException e) {
         fail("Unexpected exception: " + e);
      }
   }

   /**
    * Step 0 test. Test an empty stream should immediately return EOF, but still
    * set the filename.
    */
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
         assertEquals(expectedStatus, testRxMsgFileParser.parseFile(testInputStream, "testLogFile.bin"));
         assertEquals(testFileName, testRxMsgFileParser.getFilename());
         assertEquals(expectedStep, testRxMsgFileParser.getStep());
      } catch (FileParserException e) {
         fail("Unexpected exception: " + e);
      }
   }

   @Test
   public void testSetRxSource() {
     RxMsgFileParser parser = new RxMsgFileParser();
     
     parser.setRxSource(0);
     assertEquals(RxSource.RSU, parser.getRxSource());
     parser.setRxSource(1);
     assertEquals(RxSource.SAT, parser.getRxSource());
     parser.setRxSource(2);
     assertEquals(RxSource.RV, parser.getRxSource());
     parser.setRxSource(3);
     assertEquals(RxSource.SNMP, parser.getRxSource());
     parser.setRxSource(4);
     assertEquals(RxSource.NA, parser.getRxSource());
     parser.setRxSource(5);
     assertEquals(RxSource.UNKNOWN, parser.getRxSource());
     parser.setRxSource(6);
     assertEquals(RxSource.UNKNOWN, parser.getRxSource());
   }
}
