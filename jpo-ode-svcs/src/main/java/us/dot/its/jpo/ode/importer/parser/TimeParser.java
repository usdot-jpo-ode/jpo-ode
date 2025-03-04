/*============================================================================
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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteOrder;
import java.time.ZonedDateTime;
import us.dot.its.jpo.ode.model.OdeLogMetadata;
import us.dot.its.jpo.ode.util.CodecUtils;
import us.dot.its.jpo.ode.util.DateTimeUtils;

/**
 * TimeParser is a specialized parser that extends {@link LogFileParser} to parse log entries
 * containing timestamp data. It reads two specific segments of information: the UTC time
 * in seconds and the milliseconds, which together form a precise timestamp. The parsed
 * data can be used in further processing or converted into a human-readable date and time
 * format.
 */
public class TimeParser extends LogFileParser {

  public static final int UTC_TIME_IN_SEC_LENGTH = 4;
  public static final int MSEC_LENGTH = 2;

  protected int utcTimeInSec;
  protected short milliSec;

  public TimeParser(OdeLogMetadata.RecordType recordType, String filename) {
    super(recordType, filename);
  }

  @Override
  public ParserStatus parseFile(BufferedInputStream bis) throws FileParserException {

    ParserStatus status = ParserStatus.INIT;
    try {
      // parse utcTimeInSec
      if (getStep() == 0) {
        status = parseStep(bis, UTC_TIME_IN_SEC_LENGTH);
        if (status != ParserStatus.ENTRY_PARSING_COMPLETE) {
          return status;
        }
        setUtcTimeInSec(CodecUtils.bytesToInt(readBuffer, 0, UTC_TIME_IN_SEC_LENGTH, ByteOrder.LITTLE_ENDIAN));
      }

      // parse mSec
      if (getStep() == 1) {
        status = parseStep(bis, MSEC_LENGTH);
        if (status != ParserStatus.ENTRY_PARSING_COMPLETE) {
          return status;
        }
        setmSec(CodecUtils.bytesToShort(readBuffer, 0, MSEC_LENGTH, ByteOrder.LITTLE_ENDIAN));
      }

      resetStep();
      status = ParserStatus.ENTRY_PARSING_COMPLETE;

    } catch (Exception e) {
      throw new FileParserException(String.format("Error parsing %s on step %d", getFilename(), getStep()), e);
    }

    return status;
  }

  public long getUtcTimeInSec() {
    return utcTimeInSec;
  }

  public LogFileParser setUtcTimeInSec(int utcTimeInSec) {
    this.utcTimeInSec = utcTimeInSec;
    return this;
  }

  public short getMilliSec() {
    return milliSec;
  }

  public LogFileParser setmSec(short milliSec) {
    this.milliSec = milliSec;
    return this;
  }

  public ZonedDateTime getGeneratedAt() {
    return DateTimeUtils.isoDateTime(getUtcTimeInSec() * 1000 + getMilliSec());
  }

  @Override
  public void writeTo(OutputStream os) throws IOException {
    os.write(CodecUtils.intToBytes(utcTimeInSec, ByteOrder.LITTLE_ENDIAN));
    os.write(CodecUtils.shortToBytes(milliSec, ByteOrder.LITTLE_ENDIAN));
  }

}
