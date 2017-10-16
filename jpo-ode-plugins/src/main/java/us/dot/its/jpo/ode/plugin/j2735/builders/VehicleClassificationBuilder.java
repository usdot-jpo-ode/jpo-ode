package us.dot.its.jpo.ode.plugin.j2735.builders;

import java.util.Iterator;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735BasicVehicleRole;
import us.dot.its.jpo.ode.plugin.j2735.J2735FuelType;
import us.dot.its.jpo.ode.plugin.j2735.J2735RegionalContent;
import us.dot.its.jpo.ode.plugin.j2735.J2735ResponderGroupAffected;
import us.dot.its.jpo.ode.plugin.j2735.J2735VehicleClassification;
import us.dot.its.jpo.ode.plugin.j2735.J2735VehicleType;
import us.dot.its.jpo.ode.util.CodecUtils;

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
      JsonNode ft = vc.get("fuelType");
      if (ft != null) {

         int fuelType = ft.asInt();

         if (fuelType < FUEL_TYPE_LOWER_BOUND || FUEL_TYPE_UPPER_BOUND < fuelType) {
            throw new IllegalArgumentException(String.format("Fuel type value out of bounds [%d..%d]",
                  FUEL_TYPE_LOWER_BOUND, FUEL_TYPE_UPPER_BOUND));
         }

         gvc.setFuelType(J2735FuelType.values()[fuelType]);
      }
      JsonNode hpmsType = vc.get("hpmsType");
      if (hpmsType != null) {
         gvc.setHpmsType(J2735VehicleType.valueOf(hpmsType.fieldNames().next()));
      }
      JsonNode iso3883 = vc.get("iso3883");
      if (iso3883 != null) {
         gvc.setIso3883(iso3883.asInt());
      }
      JsonNode kt = vc.get("keyType");
      if (kt != null) {

         int keyType = kt.asInt();

         if (keyType < VEH_CLASS_LOWER_BOUND || VEH_CLASS_UPPER_BOUND < keyType) {
            throw new IllegalArgumentException(String.format("Basic vehicle classification out of bounds [%d..%d]",
                  VEH_CLASS_LOWER_BOUND, VEH_CLASS_UPPER_BOUND));
         }

         gvc.setKeyType(keyType);
      }
      JsonNode responderType = vc.get("responderType");
      if (responderType != null) {
         gvc.setResponderType(J2735ResponderGroupAffected.valueOf(responderType.fieldNames().next()));
      }
      JsonNode responseEquip = vc.get("responseEquip");
      if (responseEquip != null) {
         gvc.setResponseEquip(NamedNumberBuilder.genericIncidentResponseEquipment(responseEquip));
      }
      JsonNode role = vc.get("role");
      if (role != null) {
         gvc.setRole(J2735BasicVehicleRole.valueOf(role.fieldNames().next()));
      }
      JsonNode vehicleType = vc.get("vehicleType");
      if (vehicleType != null) {
         gvc.setVehicleType(NamedNumberBuilder.genericVehicleGroupAffected(vehicleType));
      }
      JsonNode regional = vc.get("regional");
      if (regional != null) {
         // while (vc.regional.elements().hasMoreElements()) {
         // us.dot.its.jpo.ode.j2735.dsrc.VehicleClassification.Regional.Sequence_
         // element =
         // (us.dot.its.jpo.ode.j2735.dsrc.VehicleClassification.Regional.Sequence_)
         // vc.regional
         // .elements().nextElement();
         // gvc.getRegional().add(new
         // J2735RegionalContent().setId(element.regionId.asInt())
         // .setValue(element.regExtValue.getEncodedValue()));
         // }

         if (regional.isArray()) {
            Iterator<JsonNode> elements = regional.elements();

            while (elements.hasNext()) {
               JsonNode element = elements.next();

               gvc.getRegional().add(new J2735RegionalContent().setId(element.get("regionId").asInt())
                     .setValue(CodecUtils.fromHex(element.get("regExtValue").asText())));
            }
         }
      }

      return gvc;
   }

}
