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

import org.junit.Test;

import mockit.Injectable;
import mockit.Tested;
import us.dot.its.jpo.ode.importer.parser.FileParser.FileParserException;
import us.dot.its.jpo.ode.importer.parser.FileParser.ParserStatus;
import us.dot.its.jpo.ode.util.CodecUtils;

public class DriverAlertFileParserTest {

   @Tested
   DriverAlertFileParser testDriverAlertFileParser;
   
   @Injectable
   long bundleid = 0;

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
         assertEquals(expectedStatus, testDriverAlertFileParser.parseFile(testInputStream, "testLogFile.bin"));
         assertEquals(testFileName, testDriverAlertFileParser.getFilename());
         assertEquals(expectedStep, testDriverAlertFileParser.getStep());
      } catch (FileParserException e) {
         fail("Unexpected exception: " + e);
      }
   }

   @Test
   public void testAll() {

      ParserStatus expectedStatus = ParserStatus.COMPLETE;
      String expectedPayload = "Test Driver Alert";
      int expectedStep = 0;

      byte[] buf = new byte[] { 
             (byte)0x6f, (byte)0x75, (byte)0x4d, (byte)0x19, //1.0 latitude
             (byte)0xa4, (byte)0xa1, (byte)0x5c, (byte)0xce, //1.1 longitude
             (byte)0x67, (byte)0x06, (byte)0x00, (byte)0x00, //1.2 elevation
             (byte)0x04, (byte)0x00,                         //1.3 speed
             (byte)0x09, (byte)0x27,                         //1.4 heading
             (byte)0xa9, (byte)0x2c, (byte)0xe2, (byte)0x5a, //2. utcTimeInSec
             (byte)0x8f, (byte)0x01,                         //3. mSec
             (byte)0x11, (byte)0x00,                         //4.0 payloadLength
                                                             //4.1 payload
             'T', 'e', 's', 't', ' ', 'D', 'r', 'i', 'v', 'e', 'r', ' ', 'A', 'l', 'e', 'r', 't' 
             };
      BufferedInputStream testInputStream = new BufferedInputStream(new ByteArrayInputStream(buf));

      try {
         assertEquals(expectedStatus, testDriverAlertFileParser.parseFile(testInputStream, "testLogFile.bin"));
         assertEquals(424506735L, testDriverAlertFileParser.getLocationParser().getLocation().getLatitude());
         assertEquals(-832790108L, testDriverAlertFileParser.getLocationParser().getLocation().getLongitude());
         assertEquals(1639L, testDriverAlertFileParser.getLocationParser().getLocation().getElevation());
         assertEquals(4, testDriverAlertFileParser.getLocationParser().getLocation().getSpeed());
         assertEquals(9993, testDriverAlertFileParser.getLocationParser().getLocation().getHeading());
         assertEquals(1524772009, testDriverAlertFileParser.getTimeParser().getUtcTimeInSec());
         assertEquals(399, testDriverAlertFileParser.getTimeParser().getmSec());
         assertEquals(17, testDriverAlertFileParser.getPayloadParser().getPayloadLength());
         assertEquals(expectedPayload, testDriverAlertFileParser.getAlert());
         assertEquals(expectedStep, testDriverAlertFileParser.getStep());
         
         ByteArrayOutputStream os = new ByteArrayOutputStream();
         testDriverAlertFileParser.writeTo(os);
         assertEquals(CodecUtils.toHex(buf), CodecUtils.toHex(os.toByteArray()));
      } catch (FileParserException | IOException e) {
         fail("Unexpected exception: " + e);
      }
   }

}
