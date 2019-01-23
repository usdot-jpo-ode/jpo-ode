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
package us.dot.its.jpo.ode.plugin.ieee1609dot2;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class Ieee1609Dot2Data extends Asn1Object {
   private static final long serialVersionUID = -228377851758092505L;

   private Byte protocolVersion;
   private Ieee1609Dot2Content content;
   
   
   public Ieee1609Dot2Data() {
      super();
      protocolVersion = 3;
   }
   
   public Byte getProtocolVersion() {
      return protocolVersion;
   }
   public void setProtocolVersion(Byte protocolVersion) {
      this.protocolVersion = protocolVersion;
   }
   public Ieee1609Dot2Content getContent() {
      return content;
   }
   public void setContent(Ieee1609Dot2Content content) {
      this.content = content;
   }
   
   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((content == null) ? 0 : content.hashCode());
      result = prime * result + ((protocolVersion == null) ? 0 : protocolVersion.hashCode());
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
      Ieee1609Dot2Data other = (Ieee1609Dot2Data) obj;
      if (content == null) {
         if (other.content != null)
            return false;
      } else if (!content.equals(other.content))
         return false;
      if (protocolVersion == null) {
         if (other.protocolVersion != null)
            return false;
      } else if (!protocolVersion.equals(other.protocolVersion))
         return false;
      return true;
   }

}
