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

import us.dot.its.jpo.ode.plugin.j2735.J2735GNSSstatus;

public class GNSSstatusBuilder {

   public enum GNSstatusNames {
      unavailable, isHealthy, isMonitored, baseStationType, aPDOPofUnder5, inViewOfUnder5, localCorrectionsPresent, networkCorrectionsPresent
   }

   private GNSSstatusBuilder() {
      throw new UnsupportedOperationException();
   }

   public static J2735GNSSstatus genericGNSSstatus(JsonNode gnssStatus) {
      J2735GNSSstatus status = new J2735GNSSstatus();

      char[] gnsStatusBits = gnssStatus.asText().toCharArray();

      for (int i = 0; i < gnsStatusBits.length; i++) {
         String statusName = GNSstatusNames.values()[i].name();
         Boolean statusValue = (gnsStatusBits[i] == '1');
         status.put(statusName, statusValue);

      }
      return status;
   }

}
