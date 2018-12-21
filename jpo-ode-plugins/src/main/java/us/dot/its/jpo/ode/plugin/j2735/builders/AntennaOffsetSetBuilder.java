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

import us.dot.its.jpo.ode.plugin.j2735.J2735AntennaOffsetSet;

public class AntennaOffsetSetBuilder {
    
    private AntennaOffsetSetBuilder() {
       throw new UnsupportedOperationException();
    }

    public static J2735AntennaOffsetSet genericAntennaOffsetSet(JsonNode antennaOffsetSet) {

        J2735AntennaOffsetSet genericAntennaOffsetSet = new J2735AntennaOffsetSet();

        genericAntennaOffsetSet.setAntOffsetX(OffsetBuilder.genericOffset_B12(antennaOffsetSet.get("antOffsetX")));
        genericAntennaOffsetSet.setAntOffsetY(OffsetBuilder.genericOffset_B09(antennaOffsetSet.get("antOffsetY")));
        genericAntennaOffsetSet.setAntOffsetZ(OffsetBuilder.genericOffset_B10(antennaOffsetSet.get("antOffsetZ")));

        return genericAntennaOffsetSet;
    }

}
