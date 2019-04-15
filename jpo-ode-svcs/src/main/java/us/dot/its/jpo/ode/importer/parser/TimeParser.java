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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteOrder;
import java.time.ZonedDateTime;

import us.dot.its.jpo.ode.util.CodecUtils;
import us.dot.its.jpo.ode.util.DateTimeUtils;

public class TimeParser extends LogFileParser {

   public static final int UTC_TIME_IN_SEC_LENGTH = 4;
   public static final int MSEC_LENGTH = 2;

   protected int utcTimeInSec;
   protected short mSec;

   public TimeParser() {
      super();
   }

   @Override
   public ParserStatus parseFile(BufferedInputStream bis, String fileName) throws FileParserException {

      ParserStatus status = ParserStatus.INIT;
      try {
         // parse utcTimeInSec
         if (getStep() == 0) {
            status = parseStep(bis, UTC_TIME_IN_SEC_LENGTH);
            if (status != ParserStatus.COMPLETE)
               return status;
            setUtcTimeInSec(CodecUtils.bytesToInt(readBuffer, 0, UTC_TIME_IN_SEC_LENGTH, ByteOrder.LITTLE_ENDIAN));
         }

         // parse mSec
         if (getStep() == 1) {
            status = parseStep(bis, MSEC_LENGTH);
            if (status != ParserStatus.COMPLETE)
               return status;
            setmSec(CodecUtils.bytesToShort(readBuffer, 0, MSEC_LENGTH, ByteOrder.LITTLE_ENDIAN));
         }

         resetStep();
         status = ParserStatus.COMPLETE;

      } catch (Exception e) {
         throw new FileParserException(String.format("Error parsing %s on step %d", fileName, getStep()), e);
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

   public short getmSec() {
      return mSec;
   }

   public LogFileParser setmSec(short mSec) {
      this.mSec = mSec;
      return this;
   }

   public ZonedDateTime getGeneratedAt() {
      return DateTimeUtils.isoDateTime(getUtcTimeInSec() * 1000 + getmSec());
   }

  @Override
  public void writeTo(OutputStream os) throws IOException {
    os.write(CodecUtils.intToBytes(utcTimeInSec, ByteOrder.LITTLE_ENDIAN));
    os.write(CodecUtils.shortToBytes(mSec, ByteOrder.LITTLE_ENDIAN));
  }

}
