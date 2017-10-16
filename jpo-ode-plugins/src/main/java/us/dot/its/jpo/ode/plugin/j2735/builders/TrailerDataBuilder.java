package us.dot.its.jpo.ode.plugin.j2735.builders;

import java.util.Iterator;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735TrailerData;

public class TrailerDataBuilder {

    private TrailerDataBuilder() {
       throw new UnsupportedOperationException();
    }

    public static J2735TrailerData genericTrailerData(JsonNode trailers) {
        J2735TrailerData td = new J2735TrailerData();

        td.setConnection(PivotPointDescriptionBuilder.genericPivotPointDescription(trailers.get("connection")));
        td.setSspRights(trailers.get("sspRights").asInt());

        Iterator<JsonNode> iter = trailers.get("units").elements();

        while (iter.hasNext()) {
            td.getUnits().add(TrailerUnitDescriptionBuilder.genericTrailerUnitDescription(iter.next()));
        }

        return td;
    }

}
