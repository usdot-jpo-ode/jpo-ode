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

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class MsgId extends Asn1Object {
   private static final long serialVersionUID = 1L;
   private RoadSignID roadSignID;
   private String furtherInfoID;

   public RoadSignID getRoadSignID() {
      return roadSignID;
   }

   public void setRoadSignID(RoadSignID roadSignID) {
      this.roadSignID = roadSignID;
   }

   public String getFurtherInfoID() {
      return furtherInfoID;
   }

   public void setFurtherInfoID(String furtherInfoID) {
      this.furtherInfoID = furtherInfoID;
   }
}
