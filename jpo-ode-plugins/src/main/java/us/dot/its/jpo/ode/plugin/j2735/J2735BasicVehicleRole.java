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

public enum J2735BasicVehicleRole {
   basicVehicle, //, // (0), -- Light duty passenger vehicle type
   publicTransport, //, // (1), -- Used in EU for Transit us
   specialTransport, //, // (2), -- Used in EU, // (e.g. heavy load)
   dangerousGoods, //, // (3), -- Used in EU for any HAZMAT
   roadWork, //, // (4), -- Used in EU for State and Local DOT uses
   roadRescue, //, // (5), -- Used in EU and in the US to include tow trucks.
   emergency, // (6), -- Used in EU for Police, Fire and Ambulance units
   safetyCar, // (7), -- Used in EU for Escort vehicles
   //-- Begin US unique numbering
   none_unknown, // (8), -- added to follow current SAE style guidelines
   truck, // (9), -- Heavy trucks with additional BSM rights and obligations
   motorcycle, // (10), --
   roadSideSource, // (11), -- For infrastructure generated calls such as
   //-- fire house, rail infrastructure, roadwork site, etc.
   police, // (12), --
   fire, // (13), --
   ambulance, // (14), --, // (does not include private para-transit etc.)
   dot, // (15), -- all roadwork vehicles
   transit, // (16), -- all transit vehicles
   slowMoving, // (17), -- to also include oversize etc.
   stopNgo, // (18), -- to include trash trucks, school buses and others
   //-- that routinely disturb the free flow of traffic
   cyclist, // (19), --
   pedestrian, // (20), -- also includes those with mobility limitations
   nonMotorized, // (21), -- other, horse drawn, etc.
   military // (22),
}
