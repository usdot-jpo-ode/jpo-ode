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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.dot.its.jpo.ode.model.RxSource;

public class RxMsgFileParser extends LogFileParser {

   private static final Logger logger = LoggerFactory.getLogger(RxMsgFileParser.class);

   private static final int RX_SOURCE_LENGTH = 1;
   
   private RxSource rxSource;

   public RxMsgFileParser() {
      super();
      setLocationParser(new LocationParser());
      setTimeParser(new TimeParser());
      setSecResCodeParser(new SecurityResultCodeParser());
      setPayloadParser(new PayloadParser());
   }

   @Override
   public ParserStatus parseFile(BufferedInputStream bis, String fileName) throws FileParserException {

      ParserStatus status;
      try {
         status = super.parseFile(bis, fileName);
         if (status != ParserStatus.COMPLETE)
            return status;

         // parse rxSource
         if (getStep() == 1) {
            status = parseStep(bis, RX_SOURCE_LENGTH);
            if (status != ParserStatus.COMPLETE)
               return status;
            try {
              setRxSource(RxSource.values()[readBuffer[0]]);
            } catch (Exception e) {
              setRxSource(RxSource.UNKNOWN);
            }
         }

         if (getStep() == 2) {
            status = nextStep(bis, fileName, locationParser);
            if (status != ParserStatus.COMPLETE)
               return status;
         }
         
         if (getStep() == 3) {
            status = nextStep(bis, fileName, timeParser);
            if (status != ParserStatus.COMPLETE)
               return status;
         }

         if (getStep() == 4) {
            status = nextStep(bis, fileName, secResCodeParser);
            if (status != ParserStatus.COMPLETE)
               return status;
         }

         if (getStep() == 5) {
            status = nextStep(bis, fileName, payloadParser);
            if (status != ParserStatus.COMPLETE)
               return status;
         }

         resetStep();
         status = ParserStatus.COMPLETE;

      } catch (Exception e) {
         throw new FileParserException(String.format("Error parsing %s on step %d", fileName, getStep()), e);
      }

      return status;

   }

   public RxSource getRxSource() {
      return rxSource;
   }

   public void setRxSource(RxSource rxSource) {
      this.rxSource = rxSource;
   }

   public void setRxSource(int rxSourceOrdinal) {
      try {
         setRxSource(RxSource.values()[rxSourceOrdinal]);
      } catch (Exception e) {
         logger.error("Invalid RxSource: {}. Valid values are {}: ", 
            rxSourceOrdinal, RxSource.values());
         setRxSource(RxSource.UNKNOWN);
      }
   }

  @Override
  public void writeTo(OutputStream os) throws IOException {
    os.write((byte)rxSource.ordinal());
    super.writeTo(os);
  }
}
