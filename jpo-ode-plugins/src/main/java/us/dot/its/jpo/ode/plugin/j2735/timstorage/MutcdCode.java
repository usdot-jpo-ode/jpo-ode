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

import lombok.Data;
import lombok.EqualsAndHashCode;
import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

@EqualsAndHashCode(callSuper = false)
@Data
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
}
