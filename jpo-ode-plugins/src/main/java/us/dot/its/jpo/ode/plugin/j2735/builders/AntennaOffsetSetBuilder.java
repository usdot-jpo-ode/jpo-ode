package us.dot.its.jpo.ode.plugin.j2735.builders;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735AntennaOffsetSet;

public class AntennaOffsetSetBuilder {
    
    private AntennaOffsetSetBuilder() {
       throw new UnsupportedOperationException();
    }

    public static J2735AntennaOffsetSet genericAntennaOffsetSet(JsonNode antennaOffsetSet) {

        J2735AntennaOffsetSet genericAntennaOffsetSet = new J2735AntennaOffsetSet();

        genericAntennaOffsetSet.setAntOffsetX(OffsetBuilder.genericVertOffset_B07(antennaOffsetSet.get("antOffsetX")));
        genericAntennaOffsetSet.setAntOffsetY(OffsetBuilder.genericVertOffset_B07(antennaOffsetSet.get("antOffsetY")));
        genericAntennaOffsetSet.setAntOffsetZ(OffsetBuilder.genericVertOffset_B07(antennaOffsetSet.get("antOffsetZ")));

        return genericAntennaOffsetSet;
    }

}
