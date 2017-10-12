package us.dot.its.jpo.ode.plugin.j2735.builders;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;
import us.dot.its.jpo.ode.plugin.j2735.builders.BsmPart2ContentBuilder.BsmPart2ContentBuilderException;

public class BsmBuilder {
    
    private BsmBuilder() {
        throw new UnsupportedOperationException();
    }

    public static J2735Bsm genericBsm(JsonNode basicSafetyMessage) throws BsmPart2ContentBuilderException {
        J2735Bsm genericBsm = new J2735Bsm();
        JsonNode coreData = basicSafetyMessage.get("coreData");
        if (coreData != null) {
            genericBsm.setCoreData(BsmCoreDataBuilder.genericBsmCoreData(coreData));
        }

        JsonNode partII = basicSafetyMessage.get("partII");
        if (null != partII) {
           JsonNode part2Content = partII.get("PartIIcontent");
           if (null != part2Content) {
              BsmPart2ContentBuilder.genericPart2Content(part2Content);
           }
        }

        return genericBsm;
    }

}
