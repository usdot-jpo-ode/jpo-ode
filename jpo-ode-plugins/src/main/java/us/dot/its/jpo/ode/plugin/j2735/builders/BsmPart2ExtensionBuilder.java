package us.dot.its.jpo.ode.plugin.j2735.builders;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735BsmPart2Extension;

public class BsmPart2ExtensionBuilder {
    
    private BsmPart2ExtensionBuilder() {
       throw new UnsupportedOperationException();
    }

    public static J2735BsmPart2Extension genericSupplementalVehicleExtensions(
       JsonNode sve) {
        return SupplementalVehicleExtensionsBuilder.genericSupplementalVehicleExtensions(sve);
    }

}
