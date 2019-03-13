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

public class LocationParserTest {
   
   @Tested
   LocationParser locationParser;
   @Injectable long bundleId;

   /**
    * Step 1 test. Should extract the "location->latitude" value, length 4
    * bytes, then return EOF.
    */
   @Test
   public void testAll() {

      ParserStatus expectedStatus = ParserStatus.COMPLETE;
      int expectedStep = 0;

      byte[] buf = new byte[] { 
             (byte)0x6f, (byte)0x75, (byte)0x4d, (byte)0x19, //0 latitude
             (byte)0xa4, (byte)0xa1, (byte)0x5c, (byte)0xce, //1 longitude
             (byte)0x67, (byte)0x06, (byte)0x00, (byte)0x00, //2 elevation
             (byte)0x04, (byte)0x00,                         //3 speed
             (byte)0x09, (byte)0x27,                         //4 heading
             };
      BufferedInputStream testInputStream = new BufferedInputStream(new ByteArrayInputStream(buf));

      try {
         assertEquals(expectedStatus, locationParser.parseFile(testInputStream, "testLogFile.bin"));
         assertEquals(424506735L, locationParser.getLocation().getLatitude());
         assertEquals(-832790108L, locationParser.getLocation().getLongitude());
         assertEquals(1639L, locationParser.getLocation().getElevation());
         assertEquals(4, locationParser.getLocation().getSpeed());
         assertEquals(9993, locationParser.getLocation().getHeading());
         assertEquals(expectedStep, locationParser.getStep());
         
         ByteArrayOutputStream os = new ByteArrayOutputStream();
         locationParser.writeTo(os);
         assertEquals(CodecUtils.toHex(buf), CodecUtils.toHex(os.toByteArray()));
      } catch (FileParserException | IOException e) {
         fail("Unexpected exception: " + e);
      }
   }

   /**
    * Step 1 test without enough bytes. Should return PARTIAL.
    */
   @Test
   public void testStep0Partial() {

      ParserStatus expectedStatus = ParserStatus.PARTIAL;
      int expectedStep = 0;

      BufferedInputStream testInputStream = new BufferedInputStream(
         new ByteArrayInputStream(new byte[] { 
               (byte)0x6f, (byte)0x75//, (byte)0x4d, (byte)0x19, //0 latitude
               //(byte)0xa4, (byte)0xa1, (byte)0x5c, (byte)0xce, //1 longitude
               //(byte)0x67, (byte)0x06, (byte)0x00, (byte)0x00, //2 elevation
               //(byte)0x04, (byte)0x00,                         //3 speed
               //(byte)0x09, (byte)0x27,                         //4 heading
               }));

      try {
         assertEquals(expectedStatus, locationParser.parseFile(testInputStream, "testLogFile.bin"));
         assertEquals(expectedStep, locationParser.getStep());
      } catch (FileParserException e) {
         fail("Unexpected exception: " + e);
      }
   }

   /**
    * Step 2 test without enough bytes. Should return PARTIAL.
    */
   @Test
   public void testStep1Partial() {

      ParserStatus expectedStatus = ParserStatus.PARTIAL;
      int expectedStep = 1;

      BufferedInputStream testInputStream = new BufferedInputStream(
         new ByteArrayInputStream(new byte[] { 
               (byte)0x6f, (byte)0x75, (byte)0x4d, (byte)0x19, //0 latitude
               (byte)0xa4, (byte)0xa1//, (byte)0x5c, (byte)0xce, //1 longitude
               //(byte)0x67, (byte)0x06, (byte)0x00, (byte)0x00, //2 elevation
               //(byte)0x04, (byte)0x00,                         //3 speed
               //(byte)0x09, (byte)0x27,                         //4 heading
               }));

      try {
         assertEquals(expectedStatus, locationParser.parseFile(testInputStream, "testLogFile.bin"));
         assertEquals(expectedStep, locationParser.getStep());
      } catch (FileParserException e) {
         fail("Unexpected exception: " + e);
      }
   }

   /**
    * Step 3 test without enough bytes. Should return PARTIAL.
    */
   @Test
   public void testStep2Partial() {

      ParserStatus expectedStatus = ParserStatus.PARTIAL;
      int expectedStep = 2;

      BufferedInputStream testInputStream = new BufferedInputStream(
         new ByteArrayInputStream(new byte[] { 
               (byte)0x6f, (byte)0x75, (byte)0x4d, (byte)0x19, //0 latitude
               (byte)0xa4, (byte)0xa1, (byte)0x5c, (byte)0xce, //1 longitude
               (byte)0x67, (byte)0x06//, (byte)0x00, (byte)0x00, //2 elevation
               //(byte)0x04, (byte)0x00,                         //3 speed
               //(byte)0x09, (byte)0x27,                         //4 heading
               }));

      try {
         assertEquals(expectedStatus, locationParser.parseFile(testInputStream, "testLogFile.bin"));
         assertEquals(expectedStep, locationParser.getStep());
      } catch (FileParserException e) {
         fail("Unexpected exception: " + e);
      }
   }

   /**
    * Step 4 test without enough bytes. Should return PARTIAL.
    */
   @Test
   public void testStep3Partial() {

      ParserStatus expectedStatus = ParserStatus.PARTIAL;
      int expectedStep = 3;

      BufferedInputStream testInputStream = new BufferedInputStream(
         new ByteArrayInputStream(new byte[] { 
               (byte)0x6f, (byte)0x75, (byte)0x4d, (byte)0x19, //0 latitude
               (byte)0xa4, (byte)0xa1, (byte)0x5c, (byte)0xce, //1 longitude
               (byte)0x67, (byte)0x06, (byte)0x00, (byte)0x00, //2 elevation
               (byte)0x04//, (byte)0x00,                         //3 speed
               //(byte)0x09, (byte)0x27,                         //4 heading
               }));

      try {
         assertEquals(expectedStatus, locationParser.parseFile(testInputStream, "testLogFile.bin"));
         assertEquals(expectedStep, locationParser.getStep());
      } catch (FileParserException e) {
         fail("Unexpected exception: " + e);
      }
   }

   /**
    * Step 5 test without enough bytes. Should return PARTIAL.
    */
   @Test
   public void testStep4Partial() {

      ParserStatus expectedStatus = ParserStatus.PARTIAL;
      int expectedStep = 4;

      BufferedInputStream testInputStream = new BufferedInputStream(
         new ByteArrayInputStream(new byte[] { 
               (byte)0x6f, (byte)0x75, (byte)0x4d, (byte)0x19, //0 latitude
               (byte)0xa4, (byte)0xa1, (byte)0x5c, (byte)0xce, //1 longitude
               (byte)0x67, (byte)0x06, (byte)0x00, (byte)0x00, //2 elevation
               (byte)0x04, (byte)0x00,                         //3 speed
               (byte)0x09//, (byte)0x27,                         //4 heading
               }));

      try {
         assertEquals(expectedStatus, locationParser.parseFile(testInputStream, "testLogFile.bin"));
         assertEquals(expectedStep, locationParser.getStep());
      } catch (FileParserException e) {
         fail("Unexpected exception: " + e);
      }
   }


}
