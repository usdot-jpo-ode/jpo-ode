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

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class J2735SupplementalVehicleExtensions extends J2735BsmPart2ExtensionBase {
  private static final long serialVersionUID = 1L;

  private Integer classification;
  private J2735VehicleClassification classDetails;
  private J2735VehicleData vehicleData;
  private J2735WeatherReport doNotUse1;
  private J2735WeatherProbe doNotUse2;
  private J2735ObstacleDetection doNotUse3;
  private J2735DisabledVehicle status;
  private J2735SpeedProfile doNotUse4;
  private J2735RTCMPackage doNotUse5;
}
