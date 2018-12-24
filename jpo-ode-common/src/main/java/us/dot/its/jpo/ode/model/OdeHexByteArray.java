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

import us.dot.its.jpo.ode.util.CodecUtils;

public class OdeHexByteArray extends OdeObject {

   private static final long serialVersionUID = 6106562581659367345L;
   
   private String bytes;

   public OdeHexByteArray() {
      super();
   }

   public OdeHexByteArray(String bytes) {
      super();
      this.bytes = bytes;
   }

   public OdeHexByteArray(byte[] bytes) {
      setBytes(bytes);
   }

   public String getBytes() {
      return bytes;
   }

   public void setBytes(String bytes) {
      this.bytes = bytes;
   }

   public void setBytes(byte[] bytes) {
      this.bytes = CodecUtils.toHex(bytes);
   }

   
}
