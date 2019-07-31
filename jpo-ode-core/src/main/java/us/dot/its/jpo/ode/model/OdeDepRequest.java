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

public class OdeDepRequest extends OdeRequest {

   private static final long serialVersionUID = -6766743372518752149L;
   
   private String encodeType;
   private String data;

   
   public String getEncodeType() {
      return encodeType;
   }

   public void setEncodeType(String encodeType) {
      this.encodeType = encodeType;
   }

   public String getData() {
      return data;
   }

   public void setData(String data) {
      this.data = data;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((data == null) ? 0 : data.hashCode());
      result = prime * result
            + ((encodeType == null) ? 0 : encodeType.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (!super.equals(obj))
         return false;
      if (getClass() != obj.getClass())
         return false;
      OdeDepRequest other = (OdeDepRequest) obj;
      if (data == null) {
         if (other.data != null)
            return false;
      } else if (!data.equals(other.data))
         return false;
      if (encodeType == null) {
         if (other.encodeType != null)
            return false;
      } else if (!encodeType.equals(other.encodeType))
         return false;
      return true;
   }

   
}
