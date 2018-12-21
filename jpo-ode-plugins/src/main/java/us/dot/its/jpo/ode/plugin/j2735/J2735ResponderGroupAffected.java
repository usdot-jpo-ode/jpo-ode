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
package us.dot.its.jpo.ode.plugin.j2735;

public enum J2735ResponderGroupAffected {
   emergency_vehicle_units, // (9729), __ Default, to be used when one of
   // __ the below does not fit better
   federal_law_enforcement_units, // (9730),
   state_police_units, // (9731),
   county_police_units, // (9732), __ Hint: also sheriff response units
   local_police_units, // (9733),
   ambulance_units, // (9734),
   rescue_units, // (9735),
   fire_units, // (9736),
   hAZMAT_units, // (9737),
   light_tow_unit, // (9738),
   heavy_tow_unit, // (9739),
   freeway_service_patrols, // (9740),
   transportation_response_units, // (9741),
   private_contractor_response_units // (9742),
}
