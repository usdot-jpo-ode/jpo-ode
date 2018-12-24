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
package us.dot.its.jpo.ode.plugin.j2735.builders;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.OdeTravelerInformationMessage;
import us.dot.its.jpo.ode.util.DateTimeUtils;

public class TravelerInformationFromAsnToHumanConverter {

   private TravelerInformationFromAsnToHumanConverter() {
      throw new UnsupportedOperationException();
   }

   public static OdeTravelerInformationMessage genericTim(JsonNode asnTim) {
      OdeTravelerInformationMessage genericTim = new OdeTravelerInformationMessage();

      genericTim.setMsgCnt(asnTim.get("msgCnt").asInt());

      // TODO - Pure J2735 TIMs only contain time offset from an unknown year
      // Instead, time must be extracted from log file metadata
      genericTim.setTimeStamp(DateTimeUtils.now());

      genericTim.setPacketID(asnTim.get("packetID").asText());

      if (asnTim.get("urlB") != null) {
         genericTim.setUrlB(asnTim.get("urlB").asText());
      }

      // TODO - the rest of the message translation
      genericTim.setAsnDataFrames(asnTim.get("dataFrames"));

      return genericTim;
   }

}
