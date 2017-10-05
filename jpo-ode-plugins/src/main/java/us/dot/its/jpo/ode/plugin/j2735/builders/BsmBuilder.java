package us.dot.its.jpo.ode.plugin.j2735.builders;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;
import us.dot.its.jpo.ode.plugin.j2735.J2735BsmCoreData;

public class BsmBuilder {
    
    private BsmBuilder() {
        throw new UnsupportedOperationException();
    }

    public static J2735Bsm genericBsm(JsonNode basicSafetyMessage) {
        J2735Bsm genericBsm = new J2735Bsm();
        JsonNode coreData = basicSafetyMessage.get("coreData");
        if (coreData != null) {
            genericBsm.setCoreData(BsmCoreDataBuilder.genericBsmCoreData(coreData));
        }

        JsonNode partII = basicSafetyMessage.get("partII");
        if (partII != null) {
           //TODO
           //BsmPart2ContentBuilder.buildGenericPart2(partII, genericBsm.getPartII());
        }

        return genericBsm;
    }

}
