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
package us.dot.its.jpo.ode.plugin.j2735.timstorage;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;
import us.dot.its.jpo.ode.plugin.j2735.J2735MessageFrame;

@EqualsAndHashCode(callSuper = false)
@Data
public class MessageFrame extends Asn1Object {

   private static final long serialVersionUID = 3450586016818874906L;

   @JsonProperty("MessageFrame")
   private J2735MessageFrame MessageFrame;

   /**
    * Returns the message frame using the ASN.1 field name.
    *
    * @return message frame
    */
   @JsonProperty("MessageFrame")
   public J2735MessageFrame getMessageFrame() {
      return MessageFrame;
   }

   /**
    * Sets the message frame using the ASN.1 field name.
    *
    * @param messageFrame message frame
    */
   @JsonProperty("MessageFrame")
   public void setMessageFrame(J2735MessageFrame messageFrame) {
      MessageFrame = messageFrame;
   }
}
