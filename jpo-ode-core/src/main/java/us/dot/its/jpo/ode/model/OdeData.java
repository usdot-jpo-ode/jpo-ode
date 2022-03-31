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

@JsonPropertyOrder({ "metadata", "payload" })
public class OdeData extends OdeObject implements OdeFilterable {
    private static final long serialVersionUID = -7711340868799607662L;

    private OdeMsgMetadata metadata;
    private OdeMsgPayload payload;

    public OdeData() {
        super();
    }

    public OdeData(OdeMsgMetadata metadata, OdeMsgPayload payload) {
        super();
        this.metadata = metadata;
        this.payload = payload;
        this.metadata.setPayloadType(payload.getClass().getName());
    }

    public OdeMsgMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(OdeMsgMetadata metadata) {
        this.metadata = metadata;
    }

    public OdeMsgPayload getPayload() {
        return payload;
    }

    public void setPayload(OdeMsgPayload payload) {
        this.payload = payload;
    }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((metadata == null) ? 0 : metadata.hashCode());
      result = prime * result + ((payload == null) ? 0 : payload.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      OdeData other = (OdeData) obj;
      if (metadata == null) {
         if (other.metadata != null)
            return false;
      } else if (!metadata.equals(other.metadata))
         return false;
      if (payload == null) {
         if (other.payload != null)
            return false;
      } else if (!payload.equals(other.payload))
         return false;
      return true;
   }


}
