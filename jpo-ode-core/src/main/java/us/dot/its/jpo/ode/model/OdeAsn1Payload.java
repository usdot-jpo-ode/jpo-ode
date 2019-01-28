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

public class OdeAsn1Payload extends OdeMsgPayload {
   private static final long serialVersionUID = -7540671640610460741L;

    public OdeAsn1Payload() {
      super();
   }

   public OdeAsn1Payload(OdeObject data) {
      super(data);
   }

   public OdeAsn1Payload(String dataType, OdeObject data) {
      super(dataType, data);
   }

   public OdeAsn1Payload(OdeHexByteArray bytes) {
        super(bytes);
        this.setData(bytes);
    }

    public OdeAsn1Payload(byte[] bytes) {
       this(new OdeHexByteArray(bytes));
   }

}
