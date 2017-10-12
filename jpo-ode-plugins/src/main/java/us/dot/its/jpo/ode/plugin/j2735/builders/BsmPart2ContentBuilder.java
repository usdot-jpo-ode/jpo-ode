package us.dot.its.jpo.ode.plugin.j2735.builders;

import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735BsmPart2Content;
import us.dot.its.jpo.ode.plugin.j2735.J2735ExteriorLights;
import us.dot.its.jpo.ode.plugin.j2735.J2735SpecialVehicleExtensions;
import us.dot.its.jpo.ode.plugin.j2735.J2735SupplementalVehicleExtensions;
import us.dot.its.jpo.ode.plugin.j2735.J2735VehicleEventFlags;
import us.dot.its.jpo.ode.plugin.j2735.J2735VehicleSafetyExtensions;

public class BsmPart2ContentBuilder {

   // public static final int eventHazardLights = 0;
   // public static final int eventStopLineViolation = 1;
   // public static final int eventABSactivated = 2;
   // public static final int eventTractionControlLoss = 3;
   // public static final int eventStabilityControlactivated = 4;
   // public static final int eventHazardousMaterials = 5;
   // public static final int eventReserved1 = 6;
   // public static final int eventHardBraking = 7;
   // public static final int eventLightsChanged = 8;
   // public static final int eventWipersChanged = 9;
   // public static final int eventFlatTire = 10;
   // public static final int eventDisabledVehicle = 11;
   // public static final int eventAirBagDeployment = 12;
   //
   // public enum VehicleEventFlags {
   // eventHazardLights(0),
   // eventStopLineViolation(1),
   // eventABSactivated(2),
   // eventTractionControlLoss(3),
   // eventStabilityControlactivated(4),
   // eventHazardousMaterials(5),
   // eventReserved1(6),
   // eventHardBraking(7),
   // eventLightsChanged(8),
   // eventWipersChanged(9)
   // eventFlatTire(10),
   // eventDisabledVehicle(11),
   // eventAirBagDeployment(12)
   // }

//   private static final String DECODING_ERROR = "Error decoding OpenType value";
//   private static final String NO_OPEN_TYPE = "No OpenType value";

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

   public static J2735BsmPart2Content genericPart2Content(JsonNode part2Content) throws BsmPart2ContentBuilderException {

      JsonNode partII_Id = part2Content.get("partII-Id");

      JsonNode part2value = part2Content.get("partII-Value");
      
      if (null != partII_Id && null != part2value) {
         return buildContent(
            J2735BsmPart2Content.J2735BsmPart2Id.values()[partII_Id.asInt()], 
            part2value);
      } else {
         return null;
      }
   }

   private static J2735BsmPart2Content buildContent(
      J2735BsmPart2Content.J2735BsmPart2Id id, JsonNode openType)
         throws BsmPart2ContentBuilderException {

      J2735BsmPart2Content part2Content = new J2735BsmPart2Content();
      part2Content.setId(id);

      switch (id) {
      case VehicleSafetyExtensions:
         evaluateVehicleSafetyExt(part2Content, openType);
         break;
      case SpecialVehicleExtensions:
         evaluateSpecialVehicleExt(part2Content, openType);
         break;
      case SupplementalVehicleExtensions:
         evaluateSupplementalVehicleExt(part2Content, openType);
         break;
      }
      return part2Content;
   }

   public enum VehicleEventFlagsNames {
      eventWipersCeventAirBagDeploymenthanged, eventDisabledVehicle, eventFlatTire, eventWipersChanged, eventLightsChanged, eventHardBraking, eventReserved1, eventHazardousMaterials, eventStabilityControlactivated, eventTractionControlLoss, eventABSactivated, eventStopLineViolation, eventHazardLights
   }

   public enum ExteriorLightsNames {
      lowBeamHeadlightsOn, highBeamHeadlightsOn, leftTurnSignalOn, rightTurnSignalOn, hazardSignalOn, automaticLightControlOn, daytimeRunningLightsOn, fogLightOn, parkingLightsOn
   }

   private static void evaluateVehicleSafetyExt(J2735BsmPart2Content part2Content, JsonNode openType)
         throws BsmPart2ContentBuilderException {
      J2735VehicleSafetyExtensions vehSafety = new J2735VehicleSafetyExtensions();
      part2Content.setValue(vehSafety);

      JsonNode events = openType.get("events");
      if (events != null) {

         char[] eventBits = events.asText().toCharArray();

         J2735VehicleEventFlags eventFlags = new J2735VehicleEventFlags();

         for (char i = 0; i < eventBits.length; i++) {
            String eventName = VehicleEventFlagsNames.values()[i].name();
            Boolean eventStatus = (eventBits[i] == '1' ? true : false);
            eventFlags.put(eventName, eventStatus);
         }

         vehSafety.setEvents(eventFlags);
      }

      JsonNode lights = openType.get("lights");
      if (lights != null) {

         char[] lightsBits = lights.asText().toCharArray();

         J2735ExteriorLights exteriorLights = new J2735ExteriorLights();

         for (char i = 0; i < lightsBits.length; i++) {
            String eventName = ExteriorLightsNames.values()[i].name();
            Boolean eventStatus = (lightsBits[i] == '1' ? true : false);
            exteriorLights.put(eventName, eventStatus);
         }

         vehSafety.setLights(exteriorLights);
      }

      JsonNode pathHistory = openType.get("pathHistory");
      if (pathHistory != null) {
         vehSafety.setPathHistory(PathHistoryBuilder.genericPathHistory(pathHistory));
      }
      JsonNode pathPrediction = openType.get("pathPrediction");
      if (pathPrediction != null) {
         vehSafety.setPathPrediction(PathPredictionBuilder.genericPathPrediction(pathPrediction));
      }

   }

   private static void evaluateSpecialVehicleExt(J2735BsmPart2Content part2Content, JsonNode openType)
         throws BsmPart2ContentBuilderException {
      J2735SpecialVehicleExtensions specVeh = new J2735SpecialVehicleExtensions();
      part2Content.setValue(specVeh);

      // SpecialVehicleExtensions sp;
      // if (openType.getDecodedValue() != null) {
      // sp = (SpecialVehicleExtensions) openType.getDecodedValue();
      // } else if (openType.getEncodedValueAsStream() != null) {
      // sp = new SpecialVehicleExtensions();
      // try {
      // coder.decode(openType.getEncodedValueAsStream(), sp);
      // } catch (DecodeFailedException | DecodeNotSupportedException e) {
      // throw new BsmPart2ContentBuilderException(DECODING_ERROR, e);
      // }
      // } else {
      // throw new BsmPart2ContentBuilderException(NO_OPEN_TYPE);
      // }

      JsonNode va = openType.get("vehicleAlerts");
      if (va != null) {
         specVeh.setVehicleAlerts(EmergencyDetailsBuilder.genericEmergencyDetails(va));
      }
      JsonNode desc = openType.get("description");
      if (desc != null) {
         specVeh.setDescription(EventDescriptionBuilder.genericEventDescription(desc));
      }
      JsonNode tr = openType.get("trailers");
      if (tr != null) {
         specVeh.setTrailers(TrailerDataBuilder.genericTrailerData(tr));
      }
   }

   private static void evaluateSupplementalVehicleExt(J2735BsmPart2Content part2Content, JsonNode openType)
         throws BsmPart2ContentBuilderException {
      J2735SupplementalVehicleExtensions supVeh = new J2735SupplementalVehicleExtensions();
      part2Content.setValue(supVeh);

      // SupplementalVehicleExtensions sve;
      // if (openType.getDecodedValue() != null) {
      // sve = (SupplementalVehicleExtensions) openType.getDecodedValue();
      // } else if (openType.getEncodedValueAsStream() != null) {
      // sve = new SupplementalVehicleExtensions();
      // try {
      // coder.decode(openType.getEncodedValueAsStream(), sve);
      // } catch (DecodeFailedException | DecodeNotSupportedException e) {
      // throw new BsmPart2ContentBuilderException(DECODING_ERROR, e);
      // }
      // } else {
      // throw new BsmPart2ContentBuilderException(NO_OPEN_TYPE);
      // }
      JsonNode sve = openType.get("SupplementalVehicleExtensions");
      if (null != sve)
         part2Content.setValue(SupplementalVehicleExtensionsBuilder
               .genericSupplementalVehicleExtensions(sve));

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
