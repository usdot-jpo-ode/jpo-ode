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
package us.dot.its.jpo.ode.plugin.j2735.builders;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735VehicleSize;

public class VehicleSizeBuilder {

   private static final Integer WIDTH_LOWER_BOUND = 0;
   private static final Integer WIDTH_UPPER_BOUND = 1023;
   private static final Integer LENGTH_LOWER_BOUND = 0;
   private static final Integer LENGTH_UPPER_BOUND = 4095;

   
  private VehicleSizeBuilder() {
    super();
  }

  public static J2735VehicleSize genericVehicleSize(JsonNode vehicleSize) {
       int size = vehicleSize.get("width").asInt();
       
        if (size < WIDTH_LOWER_BOUND || size > WIDTH_UPPER_BOUND) {
            throw new IllegalArgumentException("Vehicle width out of bounds [0..1023]");
        }

        int length = vehicleSize.get("length").asInt();
        if (length < LENGTH_LOWER_BOUND || length > LENGTH_UPPER_BOUND) {
            throw new IllegalArgumentException("Vehicle length out of bounds [0..4095]");
        }

        J2735VehicleSize genericVehicleSize = new J2735VehicleSize();
     
        genericVehicleSize.setLength(length);
        
        int width = vehicleSize.get("width").asInt();
        genericVehicleSize.setWidth(width);
        
        return genericVehicleSize;
    }

}
