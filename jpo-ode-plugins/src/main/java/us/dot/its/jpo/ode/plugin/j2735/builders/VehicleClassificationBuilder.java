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

import us.dot.its.jpo.ode.plugin.j2735.J2735BasicVehicleRole;
import us.dot.its.jpo.ode.plugin.j2735.J2735FuelType;
import us.dot.its.jpo.ode.plugin.j2735.J2735ResponderGroupAffected;
import us.dot.its.jpo.ode.plugin.j2735.J2735VehicleClassification;
import us.dot.its.jpo.ode.plugin.j2735.J2735VehicleType;

public class VehicleClassificationBuilder {

   private static final int FUEL_TYPE_LOWER_BOUND = 0;
   private static final int FUEL_TYPE_UPPER_BOUND = 9;
   private static final int VEH_CLASS_LOWER_BOUND = 0;
   private static final int VEH_CLASS_UPPER_BOUND = 255;

   private VehicleClassificationBuilder() {
      throw new UnsupportedOperationException();
   }

   public static J2735VehicleClassification genericVehicleClassification(JsonNode vc) {
      J2735VehicleClassification gvc = new J2735VehicleClassification();

      // All elements of this class are optional
      JsonNode kt = vc.get("keyType");
      if (kt != null) {

         int keyType = kt.asInt();

         if (keyType < VEH_CLASS_LOWER_BOUND || VEH_CLASS_UPPER_BOUND < keyType) {
            throw new IllegalArgumentException(String.format("Basic vehicle classification out of bounds [%d..%d]",
                  VEH_CLASS_LOWER_BOUND, VEH_CLASS_UPPER_BOUND));
         }

         gvc.setKeyType(keyType);
      }

      JsonNode role = vc.get("role");
      if (role != null) {
         gvc.setRole(J2735BasicVehicleRole.valueOf(role.fieldNames().next()));
      }

      JsonNode iso3883 = vc.get("iso3883");
      if (iso3883 != null) {
         gvc.setIso3883(iso3883.asInt());
      }

      JsonNode hpmsType = vc.get("hpmsType");
      if (hpmsType != null) {
         gvc.setHpmsType(J2735VehicleType.valueOf(hpmsType.fieldNames().next()));
      }

      JsonNode vehicleType = vc.get("vehicleType");
      if (vehicleType != null) {
         gvc.setVehicleType(NamedNumberBuilder.genericVehicleGroupAffected(vehicleType));
      }

      JsonNode responseEquip = vc.get("responseEquip");
      if (responseEquip != null) {
         gvc.setResponseEquip(NamedNumberBuilder.genericIncidentResponseEquipment(responseEquip));
      }

      JsonNode responderType = vc.get("responderType");
      if (responderType != null) {
         gvc.setResponderType(J2735ResponderGroupAffected
               .valueOf(responderType.fieldNames().next().toLowerCase().replaceAll("-", "_")));
      }

      JsonNode ft = vc.get("fuelType");
      if (ft != null) {

         int fuelType = ft.asInt();

         if (fuelType < FUEL_TYPE_LOWER_BOUND || FUEL_TYPE_UPPER_BOUND < fuelType) {
            throw new IllegalArgumentException(String.format("Fuel type value out of bounds [%d..%d]",
                  FUEL_TYPE_LOWER_BOUND, FUEL_TYPE_UPPER_BOUND));
         }

         gvc.setFuelType(J2735FuelType.values()[fuelType]);
      }

      return gvc;
   }

}
