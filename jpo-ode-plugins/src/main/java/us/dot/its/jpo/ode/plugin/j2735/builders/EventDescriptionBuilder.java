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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735BitString;
import us.dot.its.jpo.ode.plugin.j2735.J2735EventDescription;
import us.dot.its.jpo.ode.plugin.j2735.J2735Extent;
import us.dot.its.jpo.ode.plugin.j2735.J2735RegionalContent;
import us.dot.its.jpo.ode.util.CodecUtils;

public class EventDescriptionBuilder {

   public enum J2735HeadingSliceNames {
      FROM000_0TO022_5DEGREES, FROM022_5TO045_0DEGREES, FROM045_0TO067_5DEGREES, FROM067_5TO090_0DEGREES, FROM090_0TO112_5DEGREES, FROM112_5TO135_0DEGREES, FROM135_0TO157_5DEGREES, FROM157_5TO180_0DEGREES, FROM180_0TO202_5DEGREES, FROM202_5TO225_0DEGREES, FROM225_0TO247_5DEGREES, FROM247_5TO270_0DEGREES, FROM270_0TO292_5DEGREES, FROM292_5TO315_0DEGREES, FROM315_0TO337_5DEGREES, FROM337_5TO360_0DEGREES
   }

   private EventDescriptionBuilder() {
      throw new UnsupportedOperationException();
   }

   public static J2735EventDescription genericEventDescription(JsonNode description) {

      J2735EventDescription desc = new J2735EventDescription();

      // Required element
      desc.setTypeEvent(description.get("typeEvent").asInt());

      // Optional elements
      JsonNode d = description.get("description");
      if (d != null) {
         desc.setDescription(buildDescription(d));
      }
      
      JsonNode p = description.get("priority");
      if (p != null) {
         desc.setPriority(p.asText());
      }
      
      JsonNode h = description.get("heading");
      if (h != null) {
         J2735BitString headingSlice = BitStringBuilder.genericBitString(h, J2735HeadingSliceNames.values());
         desc.setHeading(headingSlice);
      }
      
      JsonNode e = description.get("extent");
      if (e != null) {
         desc.setExtent(J2735Extent.valueOf(e.asText().replaceAll("-", "_").toUpperCase()));
      }

      JsonNode regional = description.get("regional");
      if (regional != null && regional.isArray()) {
          Iterator<JsonNode> elements = regional.elements();

          while (elements.hasNext()) {
             JsonNode element = elements.next();

             desc.getRegional().add(new J2735RegionalContent().setId(element.get("regionId").asInt())
                   .setValue(CodecUtils.fromHex(element.get("regExtValue").asText())));
          }
      }

      return desc;
   }

   private static List<Integer> buildDescription(JsonNode description) {

      List<Integer> desc = new ArrayList<>();

      if (description.isArray()) {

         Iterator<JsonNode> iter = description.elements();

         while (iter.hasNext()) {
            desc.add(iter.next().asInt());
         }
      }

      return desc;
   }

}
