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

public class Ieee1609Dot2DataTag extends Asn1Object {

   private static final long serialVersionUID = 6855732310695479036L;

   private Ieee1609Dot2Data Ieee1609Dot2Data;

   public Ieee1609Dot2Data getIeee1609Dot2Data() {
      return Ieee1609Dot2Data;
   }

   public void setIeee1609Dot2Data(Ieee1609Dot2Data ieee1609Dot2Data) {
      Ieee1609Dot2Data = ieee1609Dot2Data;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((Ieee1609Dot2Data == null) ? 0 : Ieee1609Dot2Data.hashCode());
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
      Ieee1609Dot2DataTag other = (Ieee1609Dot2DataTag) obj;
      if (Ieee1609Dot2Data == null) {
         if (other.Ieee1609Dot2Data != null)
            return false;
      } else if (!Ieee1609Dot2Data.equals(other.Ieee1609Dot2Data))
         return false;
      return true;
   }

}
