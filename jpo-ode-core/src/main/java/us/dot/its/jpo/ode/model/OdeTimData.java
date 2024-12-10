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

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.*;

public class OdeTimData extends OdeData {
   
   private static final long serialVersionUID = 2057040404896561615L;


   public OdeTimData() {
       super();
   }


   public OdeTimData(OdeMsgMetadata metadata, OdeMsgPayload payload) {
       super(metadata, payload);
   }

   @Override
   @JsonTypeInfo(use = Id.CLASS, include = As.EXISTING_PROPERTY, defaultImpl = OdeTimMetadata.class)
   public void setMetadata(OdeMsgMetadata metadata) {
      super.setMetadata(metadata);
   }

   @Override
   @JsonTypeInfo(use = Id.CLASS, include = As.EXISTING_PROPERTY, defaultImpl = OdeTimPayload.class)
   public void setPayload(OdeMsgPayload payload) {
      super.setPayload(payload);
   }

}
