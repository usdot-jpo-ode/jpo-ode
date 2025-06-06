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
import java.util.Iterator;
import us.dot.its.jpo.ode.plugin.j2735.J2735TrailerData;

public class TrailerDataBuilder {

    private TrailerDataBuilder() {
       throw new UnsupportedOperationException();
    }

    public static J2735TrailerData genericTrailerData(JsonNode trailers) {
        J2735TrailerData td = new J2735TrailerData();

        td.setConnection(PivotPointDescriptionBuilder.genericPivotPointDescription(trailers.get("connection")));
        td.setDoNotUse(trailers.get("doNotUse").asInt());

        Iterator<JsonNode> iter = trailers.get("units").elements();

        while (iter.hasNext()) {
            td.getUnits().add(TrailerUnitDescriptionBuilder.genericTrailerUnitDescription(iter.next()));
        }

        return td;
    }

}
