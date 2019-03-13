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
import us.dot.its.jpo.ode.model.OdeLogMetadata.SecurityResultCode;
import us.dot.its.jpo.ode.util.CodecUtils;

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

      byte[] buf = new byte[] { 
             (byte)0x00,                                     //0. securityResultCode
             };
      BufferedInputStream testInputStream = new BufferedInputStream(new ByteArrayInputStream(buf));

      try {
         assertEquals(expectedStatus, secResultCodeParser.parseFile(testInputStream, "testLogFile.bin"));
         assertEquals(SecurityResultCode.success, secResultCodeParser.getSecurityResultCode());
         assertEquals(expectedStep, secResultCodeParser.getStep());
         
         ByteArrayOutputStream os = new ByteArrayOutputStream();
         secResultCodeParser.writeTo(os);
         assertEquals(CodecUtils.toHex(buf), CodecUtils.toHex(os.toByteArray()));
      } catch (FileParserException | IOException e) {
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
