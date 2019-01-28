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

public class FrameType extends Asn1Object {

   private static final long serialVersionUID = 1L;

   public enum TravelerInfoType {
      unknown,
      advisory,
      roadSignage,
      commercialSignage
   }

   private String advisory;

   private String commercialSignage;

   private String roadSignage;

   private String unknown;

   public String getAdvisory() {
      return advisory;
   }

   public void setAdvisory(String advisory) {
      this.advisory = advisory;
   }

   public String getUnknown() {
      return unknown;
   }

   public void setUnknown(String unknown) {
      this.unknown = unknown;
   }

   public String getRoadSignage() {
      return roadSignage;
   }

   public void setRoadSignage(String roadSignage) {
      this.roadSignage = roadSignage;
   }

   public String getCommercialSignage() {
      return commercialSignage;
   }

   public void setCommercialSignage(String commercialSignage) {
      this.commercialSignage = commercialSignage;
   }
}
