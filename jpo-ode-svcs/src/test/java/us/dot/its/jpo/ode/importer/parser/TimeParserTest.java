/*===========================================================================
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import us.dot.its.jpo.ode.importer.parser.FileParser.FileParserException;
import us.dot.its.jpo.ode.importer.parser.FileParser.ParserStatus;
import us.dot.its.jpo.ode.model.OdeLogMetadata;
import us.dot.its.jpo.ode.util.CodecUtils;

class TimeParserTest {

  TimeParser timeParser;

  TimeParserTest() {
    timeParser = new TimeParser(OdeLogMetadata.RecordType.dnMsg, OdeLogMetadata.RecordType.dnMsg.name());
  }

  /**
   * Should extract all time fields and return ParserStatus.COMPLETE.
   */
  @Test
  void testAll() {

    ParserStatus expectedStatus = ParserStatus.COMPLETE;
    int expectedStep = 0;

    byte[] buf = new byte[] {
        (byte) 0xa9, (byte) 0x2c, (byte) 0xe2, (byte) 0x5a, //0. utcTimeInSec
        (byte) 0x8f, (byte) 0x01,                         //1. mSec
    };
    BufferedInputStream testInputStream = new BufferedInputStream(new ByteArrayInputStream(buf));

    try {
      assertEquals(expectedStatus, timeParser.parseFile(testInputStream));
      assertEquals(1524772009, timeParser.getUtcTimeInSec());
      assertEquals(399, timeParser.getMilliSec());
      assertEquals(expectedStep, timeParser.getStep());

      ByteArrayOutputStream os = new ByteArrayOutputStream();
      timeParser.writeTo(os);
      assertEquals(CodecUtils.toHex(buf), CodecUtils.toHex(os.toByteArray()));
    } catch (FileParserException | IOException e) {
      fail("Unexpected exception: " + e);
    }
  }

  /**
   * test PARTIAL utcTimeInSec.
   */
  @Test
  void testPartialUtcTimeInSec() {

    ParserStatus expectedStatus = ParserStatus.PARTIAL;
    int expectedStep = 0;

    BufferedInputStream testInputStream = new BufferedInputStream(
        new ByteArrayInputStream(new byte[] {
            (byte) 0xa9, (byte) 0x2c//, (byte)0xe2, (byte)0x5a, //0. utcTimeInSec
            //(byte)0x8f, (byte)0x01,                         //1. mSec
        }));

    try {
      assertEquals(expectedStatus, timeParser.parseFile(testInputStream));
      assertEquals(expectedStep, timeParser.getStep());
    } catch (FileParserException e) {
      fail("Unexpected exception: " + e);
    }
  }

  /**
   * test PARTIAL mSec field.
   */
  @Test
  void testPartialMSec() {

    ParserStatus expectedStatus = ParserStatus.PARTIAL;
    int expectedStep = 1;

    BufferedInputStream testInputStream = new BufferedInputStream(
        new ByteArrayInputStream(new byte[] {
            (byte) 0xa9, (byte) 0x2c, (byte) 0xe2, (byte) 0x5a, //0. utcTimeInSec
            (byte) 0x8f//, (byte)0x01,                         //1. mSec
        }));

    try {
      assertEquals(expectedStatus, timeParser.parseFile(testInputStream));
      assertEquals(expectedStep, timeParser.getStep());
    } catch (FileParserException e) {
      fail("Unexpected exception: " + e);
    }
  }

}
