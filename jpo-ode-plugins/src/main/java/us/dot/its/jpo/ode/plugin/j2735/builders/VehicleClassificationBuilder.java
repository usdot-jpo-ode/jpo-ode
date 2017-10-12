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
      if (vc.get("fuelType") != null) {

         int fuelType = vc.get("fueltype").asInt();

         if (fuelType < FUEL_TYPE_LOWER_BOUND || FUEL_TYPE_UPPER_BOUND < fuelType) {
            throw new IllegalArgumentException(String.format("Fuel type value out of bounds [%d..%d]",
                  FUEL_TYPE_LOWER_BOUND, FUEL_TYPE_UPPER_BOUND));
         }

         gvc.setFuelType(J2735FuelType.values()[fuelType]);
      }
      if (vc.get("hpmsType") != null) {
         gvc.setHpmsType(J2735VehicleType.values()[vc.get("hpmsType").asInt()]);
      }
      if (vc.get("iso3883") != null) {
         gvc.setIso3883(vc.get("iso3883").asInt());
      }
      if (vc.get("keyType") != null) {

         int keyType = vc.get("keyType").asInt();

         if (keyType < VEH_CLASS_LOWER_BOUND || VEH_CLASS_UPPER_BOUND < keyType) {
            throw new IllegalArgumentException(String.format("Basic vehicle classification out of bounds [%d..%d]",
                  VEH_CLASS_LOWER_BOUND, VEH_CLASS_UPPER_BOUND));
         }

         gvc.setKeyType(keyType);
      }
      if (vc.get("responderType") != null) {
         gvc.setResponderType(J2735ResponderGroupAffected.values()[vc.get("responderType").asInt()]);
      }
      if (vc.get("responseEquip") != null) {
         gvc.setResponseEquip(NamedNumberBuilder.genericIncidentResponseEquipment(vc.get("responseEquip")));
      }
      if (vc.get("role") != null) {
         gvc.setRole(J2735BasicVehicleRole.values()[vc.get("role").asInt()]);
      }
      if (vc.get("vehicleType") != null) {
         gvc.setVehicleType(NamedNumberBuilder.genericVehicleGroupAffected(vc.get("vehicleType")));
      }
      if (vc.get("regional") != null) {
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

         if (vc.get("regional").isArray()) {
            Iterator<JsonNode> elements = vc.get("regional").elements();

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
