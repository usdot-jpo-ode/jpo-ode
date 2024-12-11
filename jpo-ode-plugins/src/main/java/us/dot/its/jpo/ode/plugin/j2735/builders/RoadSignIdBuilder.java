package us.dot.its.jpo.ode.plugin.j2735.builders;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.DsrcPosition3D;
import us.dot.its.jpo.ode.plugin.j2735.J2735MutcdCode;
import us.dot.its.jpo.ode.plugin.j2735.J2735RoadSignId;

public class RoadSignIdBuilder {
    private RoadSignIdBuilder() {
        throw new UnsupportedOperationException();
    }

    public static J2735RoadSignId genericRoadSignId(JsonNode roadSignId) {
        J2735RoadSignId genericRoadSignId = new J2735RoadSignId();

        JsonNode position = roadSignId.get("position");
        if (position != null) {
            DsrcPosition3D dsrcPosition3d = Position3DBuilder.dsrcPosition3D(position);
            genericRoadSignId.setPosition(Position3DBuilder.odePosition3D(dsrcPosition3d));
        }

        JsonNode viewAngle = roadSignId.get("viewAngle");
        if (viewAngle != null) {
            genericRoadSignId.setViewAngle(viewAngle.asText());
        }

        JsonNode mutcdCode = roadSignId.get("mutcdCode");
        if (mutcdCode != null) {
            String mutcdCodeValue = mutcdCode.fields().next().getKey();
            genericRoadSignId.setMutcdCode(J2735MutcdCode.valueOf(mutcdCodeValue));
        }

        JsonNode crc = roadSignId.get("crc");
        if (crc != null) {
            genericRoadSignId.setCrc(crc.asText());
        }

        return genericRoadSignId;
    }
}
