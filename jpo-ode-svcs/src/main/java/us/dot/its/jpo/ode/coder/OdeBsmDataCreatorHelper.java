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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import us.dot.its.jpo.ode.context.AppContext;
import us.dot.its.jpo.ode.model.OdeBsmData;
import us.dot.its.jpo.ode.model.OdeBsmMetadata;
import us.dot.its.jpo.ode.model.OdeBsmPayload;
import us.dot.its.jpo.ode.plugin.j2735.builders.BsmBuilder;
import us.dot.its.jpo.ode.util.JsonUtils;
import us.dot.its.jpo.ode.util.XmlUtils;
import us.dot.its.jpo.ode.util.XmlUtils.XmlUtilsException;

public class OdeBsmDataCreatorHelper {

   private OdeBsmDataCreatorHelper() {
   }


   public static OdeBsmData createOdeBsmData(String consumedData) throws XmlUtilsException 
          {
      ObjectNode consumed = XmlUtils.toObjectNode(consumedData);

      JsonNode metadataNode = consumed.findValue(AppContext.METADATA_STRING);
      if (metadataNode instanceof ObjectNode) {
         ObjectNode object = (ObjectNode) metadataNode;
         object.remove(AppContext.ENCODINGS_STRING);
      }
      
      OdeBsmMetadata metadata = (OdeBsmMetadata) JsonUtils.fromJson(
         metadataNode.toString(), OdeBsmMetadata.class);

      /*
       *  ODE-755 and ODE-765 Starting with schemaVersion=5 receivedMessageDetails 
       *  will be present in BSM metadata. None should be present in prior versions.
       */
      if (metadata.getSchemaVersion() <= 4) {
         metadata.setReceivedMessageDetails(null);
      }
      
      OdeBsmPayload payload = new OdeBsmPayload(
         BsmBuilder.genericBsm(consumed.findValue("BasicSafetyMessage")));
      return new OdeBsmData(metadata, payload );
   }
}
