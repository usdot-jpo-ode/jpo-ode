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

public class MutcdCode extends Asn1Object {
   private static final long serialVersionUID = 1L;
   
   public enum MutcdCodeEnum {
      none,             // (0), -- non-MUTCD information
      regulatory,       // (1), -- "R" Regulatory signs
      warning,          // (2), -- "W" warning signs
      maintenance,      // (3), -- "M" Maintenance and construction
      motoristService,  // (4), -- Motorist Services
      guide,            // (5), -- "G" Guide signs
      rec               // (6), -- Recreation and Cultural Interest
   }

   private String none; // (0), -- non-MUTCD information
   private String regulatory; // (1), -- "R" Regulatory signs
   private String warning; // (2), -- "W" warning signs
   private String maintenance; // (3), -- "M" Maintenance and construction
   private String motoristService; // (4), -- Motorist Services
   private String guide; // (5), -- "G" Guide signs
   private String rec; // 6

   public String getGuide() {
      return guide;
   }

   public void setGuide(String guide) {
      this.guide = guide;
   }

   public String getNone() {
      return none;
   }

   public void setNone(String none) {
      this.none = none;
   }

   public String getRegulatory() {
      return regulatory;
   }

   public void setRegulatory(String regulatory) {
      this.regulatory = regulatory;
   }

   public String getWarning() {
      return warning;
   }

   public void setWarning(String warning) {
      this.warning = warning;
   }

   public String getMaintenance() {
      return maintenance;
   }

   public void setMaintenance(String maintenance) {
      this.maintenance = maintenance;
   }

   public String getMotoristService() {
      return motoristService;
   }

   public void setMotoristService(String motoristService) {
      this.motoristService = motoristService;
   }

   public String getRec() {
      return rec;
   }

   public void setRec(String rec) {
      this.rec = rec;
   }
}
