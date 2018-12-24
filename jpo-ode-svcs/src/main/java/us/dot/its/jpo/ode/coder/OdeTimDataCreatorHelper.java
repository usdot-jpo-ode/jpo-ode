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
package us.dot.its.jpo.ode.coder;

import org.json.JSONObject;

import us.dot.its.jpo.ode.context.AppContext;
import us.dot.its.jpo.ode.model.OdeTimPayload;

public class OdeTimDataCreatorHelper {

   private OdeTimDataCreatorHelper() {
   }

   public static JSONObject createOdeTimData(JSONObject timData) { 

      JSONObject metadata = timData.getJSONObject(AppContext.METADATA_STRING);
      metadata.put("payloadType", OdeTimPayload.class.getName());
      metadata.remove(AppContext.ENCODINGS_STRING);
      
      JSONObject payload = timData.getJSONObject(AppContext.PAYLOAD_STRING);
      payload.put(AppContext.DATA_TYPE_STRING, "TravelerInformation");
      return timData;
   }
}
