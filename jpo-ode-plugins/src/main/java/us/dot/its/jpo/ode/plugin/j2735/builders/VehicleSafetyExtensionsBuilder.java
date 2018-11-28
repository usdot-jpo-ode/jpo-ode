package us.dot.its.jpo.ode.plugin.j2735.builders;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735BsmPart2Content;
import us.dot.its.jpo.ode.plugin.j2735.J2735ExteriorLights;
import us.dot.its.jpo.ode.plugin.j2735.J2735VehicleEventFlags;
import us.dot.its.jpo.ode.plugin.j2735.J2735VehicleSafetyExtensions;

public class VehicleSafetyExtensionsBuilder {
   
   private VehicleSafetyExtensionsBuilder() {
      throw new UnsupportedOperationException();
   }

   public static void evaluateVehicleSafetyExt(J2735BsmPart2Content part2Content, JsonNode vehSafetyExt)
         throws BsmPart2ContentBuilder.BsmPart2ContentBuilderException {
      J2735VehicleSafetyExtensions vehSafety = new J2735VehicleSafetyExtensions();
      part2Content.setValue(vehSafety);
   
      JsonNode events = vehSafetyExt.get("events");
      if (events != null) {
   
         char[] eventBits = events.asText().toCharArray();
   
         J2735VehicleEventFlags eventFlags = new J2735VehicleEventFlags();
   
         for (char i = 0; i < eventBits.length; i++) {
            String eventName = BsmPart2ContentBuilder.VehicleEventFlagsNames.values()[i].name();
            Boolean eventStatus = (eventBits[i] == '1' ? true : false);
            eventFlags.put(eventName, eventStatus);
         }
   
         vehSafety.setEvents(eventFlags);
      }
   
      JsonNode lights = vehSafetyExt.get("lights");
      if (lights != null) {
   
         char[] lightsBits = lights.asText().toCharArray();
   
         J2735ExteriorLights exteriorLights = new J2735ExteriorLights();
   
         for (char i = 0; i < lightsBits.length; i++) {
            String eventName = BsmPart2ContentBuilder.ExteriorLightsNames.values()[i].name();
            Boolean eventStatus = (lightsBits[i] == '1' ? true : false);
            exteriorLights.put(eventName, eventStatus);
         }
   
         vehSafety.setLights(exteriorLights);
      }
   
      JsonNode pathHistory = vehSafetyExt.get("pathHistory");
      if (pathHistory != null) {
         vehSafety.setPathHistory(PathHistoryBuilder.genericPathHistory(pathHistory));
      }
      JsonNode pathPrediction = vehSafetyExt.get("pathPrediction");
      if (pathPrediction != null) {
         vehSafety.setPathPrediction(PathPredictionBuilder.genericPathPrediction(pathPrediction));
      }
   
   }

}