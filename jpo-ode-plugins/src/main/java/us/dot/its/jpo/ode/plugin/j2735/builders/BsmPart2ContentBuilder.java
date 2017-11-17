package us.dot.its.jpo.ode.plugin.j2735.builders;

import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735BsmPart2Content;

public class BsmPart2ContentBuilder {

   private BsmPart2ContentBuilder() {
      throw new UnsupportedOperationException();
   }

   public static class BsmPart2ContentBuilderException extends Exception {

      private static final long serialVersionUID = 7318127023245642955L;

      public BsmPart2ContentBuilderException(String msg) {
         super(msg);
      }

      public BsmPart2ContentBuilderException(String msg, Exception e) {
         super(msg, e);
      }

   }

   public static J2735BsmPart2Content genericPart2Content(JsonNode part2Content)
         throws BsmPart2ContentBuilderException {

      JsonNode partII_Id = part2Content.get("partII-Id");

      JsonNode part2value = part2Content.get("partII-Value");

      if (null != partII_Id && null != part2value) {
         return buildContent(J2735BsmPart2Content.J2735BsmPart2Id.values()[partII_Id.asInt()], part2value);
      } else {
         return null;
      }
   }

   private static J2735BsmPart2Content buildContent(J2735BsmPart2Content.J2735BsmPart2Id id, JsonNode openType)
         throws BsmPart2ContentBuilderException {

      J2735BsmPart2Content part2Content = new J2735BsmPart2Content();
      part2Content.setId(id);

      switch (id) {
      case VehicleSafetyExtensions:
         VehicleSafetyExtensionsBuilder.evaluateVehicleSafetyExt(part2Content, openType.get(id.name()));
         break;
      case SpecialVehicleExtensions:
         SpecialVehicleExtensionsBuilder.evaluateSpecialVehicleExt(part2Content, openType.get(id.name()));
         break;
      case SupplementalVehicleExtensions:
         SupplementalVehicleExtensionsBuilder.genericSupplementalVehicleExtensions(part2Content,
               openType.get(id.name()));
         break;
      }
      return part2Content;
   }

   // TODO - this is backwards?
   // public enum VehicleEventFlagsNames {
   // eventWipersCeventAirBagDeploymenthanged, eventDisabledVehicle,
   // eventFlatTire, eventWipersChanged, eventLightsChanged, eventHardBraking,
   // eventReserved1, eventHazardousMaterials, eventStabilityControlactivated,
   // eventTractionControlLoss, eventABSactivated, eventStopLineViolation,
   // eventHazardLights
   // }

   public enum VehicleEventFlagsNames {
      eventHazardLights, eventStopLineViolation, eventABSactivated, eventTractionControlLoss, eventStabilityControlactivated, eventHazardousMaterials, eventReserved1, eventHardBraking, eventLightsChanged, eventWipersChanged, eventFlatTire, eventDisabledVehicle, eventAirBagDeployment
   }

   public enum ExteriorLightsNames {
      lowBeamHeadlightsOn, highBeamHeadlightsOn, leftTurnSignalOn, rightTurnSignalOn, hazardSignalOn, automaticLightControlOn, daytimeRunningLightsOn, fogLightOn, parkingLightsOn
   }

   public static void buildGenericPart2(List<JsonNode> elements, List<J2735BsmPart2Content> partII)
         throws BsmPart2ContentBuilderException {
      if (elements != null) {
         Iterator<JsonNode> iter = elements.iterator();

         while (iter.hasNext()) {
            partII.add(BsmPart2ContentBuilder.genericPart2Content(iter.next()));
         }
      }
   }
}
