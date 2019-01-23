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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.dot.its.jpo.ode.model.OdeBsmMetadata.BsmSource;

public class BsmLogFileParser extends LogFileParser {
   private static final Logger logger = LoggerFactory.getLogger(BsmLogFileParser.class);

   private static final int DIRECTION_LENGTH = 1;

   private BsmSource bsmSource; // 0 for EV(Tx), 1 for RV(Rx)

   public BsmLogFileParser() {
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

         if (getStep() == 1) {
            status = parseStep(bis, DIRECTION_LENGTH);
            if (status != ParserStatus.COMPLETE)
               return status;
            setBsmSource(readBuffer);
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
         throw new FileParserException("Error parsing " + fileName, e);
      }

      return status;
   }

   public BsmSource getBsmSource() {
      return bsmSource;
   }

   public void setBsmSource(BsmSource bsmSource) {
      this.bsmSource = bsmSource;
   }

   public void setBsmSource(byte[] code) {
      try {
         setBsmSource(BsmSource.values()[code[0]]);
      } catch (Exception e) {
         logger.error("Invalid BsmSource: {}. Valid values are {}-{} inclusive", 
            code, 0, BsmSource.values());
         setBsmSource(BsmSource.unknown);
      }
   }
}
