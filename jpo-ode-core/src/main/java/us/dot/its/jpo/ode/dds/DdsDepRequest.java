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
package us.dot.its.jpo.ode.dds;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * "DEPOSIT: { \"systemDepositName\": \"%s\", \"encodeType\": \"%s\",
 * \"encodedMsg\": \"%s\" }"
 *
 */
@JsonPropertyOrder({ "systemDepositName", "encodeType", "encodedMsg" })
public class DdsDepRequest extends DdsRequest {

   private static final long serialVersionUID = 6066887685895268828L;

   private String systemDepositName;
   private String encodeType;
   private String encodedMsg;

   public String getSystemDepositName() {
      return systemDepositName;
   }

   public DdsDepRequest setSystemDepositName(String systemDepositName) {
      this.systemDepositName = systemDepositName;
      return this;
   }

   public String getEncodeType() {
      return encodeType;
   }

   public DdsDepRequest setEncodeType(String encodeType) {
      this.encodeType = encodeType;
      return this;
   }

   public String getEncodedMsg() {
      return encodedMsg;
   }

   public DdsDepRequest setEncodedMsg(String encodedMsg) {
      this.encodedMsg = encodedMsg;
      return this;
   }

   @Override
   public String toString() {
      return "DEPOSIT:" + this.toJson(false);
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result
            + ((encodeType == null) ? 0 : encodeType.hashCode());
      result = prime * result
            + ((encodedMsg == null) ? 0 : encodedMsg.hashCode());
      result = prime * result
            + ((systemDepositName == null) ? 0 : systemDepositName.hashCode());
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
      DdsDepRequest other = (DdsDepRequest) obj;
      if (encodeType == null) {
         if (other.encodeType != null)
            return false;
      } else if (!encodeType.equals(other.encodeType))
         return false;
      if (encodedMsg == null) {
         if (other.encodedMsg != null)
            return false;
      } else if (!encodedMsg.equals(other.encodedMsg))
         return false;
      if (systemDepositName == null) {
         if (other.systemDepositName != null)
            return false;
      } else if (!systemDepositName.equals(other.systemDepositName))
         return false;
      return true;
   }

}
