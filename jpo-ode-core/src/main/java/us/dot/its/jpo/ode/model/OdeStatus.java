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


public class OdeStatus extends OdeMessage {
   private static final long serialVersionUID = -8787345244306039604L;

   public static enum Code {
      SUCCESS, FAILURE, 
      SOURCE_CONNECTION_ERROR, INVALID_REQUEST_TYPE_ERROR, 
      INVALID_DATA_TYPE_ERROR, COMMENT, DATA_TYPE_NOT_SUPPORTED
   }

   Code code;
   String message;
   String requestId;

   public OdeStatus() {
      super();
   }

   public OdeStatus(Code code, String message) {
      super();
      this.code = code;
      this.message = message;
   }

   public Code getCode() {
      return code;
   }

   public OdeStatus setCode(Code code) {
      this.code = code;
      return this;
   }

   public String getMessage() {
      return message;
   }

   public OdeStatus setMessage(String message) {
      this.message = message;
      return this;
   }

   public String getRequestId() {
      return requestId;
   }

   public OdeStatus setRequestId(String requestId) {
      this.requestId = requestId;
      return this;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((code == null) ? 0 : code.hashCode());
      result = prime * result + ((message == null) ? 0 : message.hashCode());
      result = prime * result
            + ((requestId == null) ? 0 : requestId.hashCode());
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
      OdeStatus other = (OdeStatus) obj;
      if (code != other.code)
         return false;
      if (message == null) {
         if (other.message != null)
            return false;
      } else if (!message.equals(other.message))
         return false;
      if (requestId == null) {
         if (other.requestId != null)
            return false;
      } else if (!requestId.equals(other.requestId))
         return false;
      return true;
   }

}
