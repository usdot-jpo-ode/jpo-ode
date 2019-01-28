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

import java.util.Iterator;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;

public class BsmBuilder {
    
    private BsmBuilder() {
        throw new UnsupportedOperationException();
    }

    public static J2735Bsm genericBsm(JsonNode basicSafetyMessage) {
        J2735Bsm genericBsm = new J2735Bsm();
        JsonNode coreData = basicSafetyMessage.get("coreData");
        if (coreData != null) {
            genericBsm.setCoreData(BsmCoreDataBuilder.genericBsmCoreData(coreData));
        }

        JsonNode partII = basicSafetyMessage.get("partII");
        if (null != partII) {
           JsonNode part2Content = partII.get("PartIIcontent");
           if (null != part2Content) {
              if (part2Content.isArray()) {
                 Iterator<JsonNode> elements = part2Content.elements();
                 while (elements.hasNext()) {
                    genericBsm.getPartII().add(BsmPart2ContentBuilder.genericPart2Content(elements.next()));
                 }
              } else {
                 genericBsm.getPartII().add(BsmPart2ContentBuilder.genericPart2Content(part2Content));
              }
           }
        }

        return genericBsm;
    }

}
