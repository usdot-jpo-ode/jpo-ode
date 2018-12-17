package us.dot.its.jpo.ode.plugin.j2735.builders;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735BsmPart2Content;
import us.dot.its.jpo.ode.plugin.j2735.J2735SpecialVehicleExtensions;

public class SpecialVehicleExtensionsBuilder {

   private SpecialVehicleExtensionsBuilder() {
      throw new UnsupportedOperationException();
   }

   public static void evaluateSpecialVehicleExt(J2735BsmPart2Content part2Content, JsonNode specVehExt)
         throws BsmPart2ContentBuilder.BsmPart2ContentBuilderException {
      J2735SpecialVehicleExtensions specVeh = new J2735SpecialVehicleExtensions();
      part2Content.setValue(specVeh);

      JsonNode va = specVehExt.get("vehicleAlerts");
      if (va != null) {
         specVeh.setVehicleAlerts(EmergencyDetailsBuilder.genericEmergencyDetails(va));
      }
      JsonNode desc = specVehExt.get("description");
      if (desc != null) {
         specVeh.setDescription(EventDescriptionBuilder.genericEventDescription(desc));
      }
      JsonNode tr = specVehExt.get("trailers");
      if (tr != null) {
         specVeh.setTrailers(TrailerDataBuilder.genericTrailerData(tr));
      }
   }

}
