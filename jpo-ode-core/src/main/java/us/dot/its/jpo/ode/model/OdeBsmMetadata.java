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
package us.dot.its.jpo.ode.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "bsmSource", "logFileName", "recordType", "securityResultCode", "receivedMessageDetails",
      "encodings", "payloadType", "serialId", "odeReceivedAt", "schemaVersion", "maxDurationTime", "recordGeneratedAt",
      "recordGeneratedBy", "sanitized" })
public class OdeBsmMetadata extends OdeLogMetadata {

   private static final long serialVersionUID = -8601265839394150140L;

   private String originIp;

   public enum BsmSource {
      EV, RV, unknown
   }

   private BsmSource bsmSource;

   public OdeBsmMetadata() {
      super();
   }

   public OdeBsmMetadata(OdeMsgPayload payload) {
      super(payload);
   }

   public OdeBsmMetadata(OdeMsgPayload payload, SerialId serialId, String receivedAt) {

   }

   public BsmSource getBsmSource() {
      return bsmSource;
   }

   public void setBsmSource(BsmSource bsmSource) {
      this.bsmSource = bsmSource;
   }

   public String getOriginIp() {
      return originIp;
   }

   public void setOriginIp(String originIp) {
      this.originIp = originIp;
   }
}
