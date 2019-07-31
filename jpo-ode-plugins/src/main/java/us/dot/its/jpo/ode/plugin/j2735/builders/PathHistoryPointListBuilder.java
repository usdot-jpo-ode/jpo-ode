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

import us.dot.its.jpo.ode.plugin.j2735.J2735PathHistoryPoint;

public class PathHistoryPointListBuilder {

   private PathHistoryPointListBuilder() {
      throw new UnsupportedOperationException();
   }

   public static List<J2735PathHistoryPoint> genericPathHistoryPointList(JsonNode crumbData) {

      List<J2735PathHistoryPoint> phpl = new ArrayList<>();

      JsonNode php = crumbData.get("PathHistoryPoint");
      if (php.isArray()) {
         Iterator<JsonNode> iter = php.elements();
   
         while (iter.hasNext() && phpl.size() < 23) {
            phpl.add(PathHistoryPointBuilder.genericPathHistoryPoint(iter.next()));
         }
      } else {
         phpl.add(PathHistoryPointBuilder.genericPathHistoryPoint(php));
      }
      return phpl;
   }
}
