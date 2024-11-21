package us.dot.its.jpo.ode.plugin.j2735.builders;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735Offset;
import us.dot.its.jpo.ode.plugin.j2735.J2735OffsetSystem;

public class OffsetSystemBuilder {
    private OffsetSystemBuilder() {
        throw new UnsupportedOperationException();
    }

    public static J2735OffsetSystem genericOffsetSystem(JsonNode offsetSystem) {
        J2735OffsetSystem genericOffsetSystem = new J2735OffsetSystem();

        JsonNode scale = offsetSystem.get("scale");
        if (scale != null) {
            genericOffsetSystem.setScale(scale.asInt());
        }

        JsonNode offset = offsetSystem.get("offset");
        if (offset != null) {
            J2735Offset offsetObj = new J2735Offset();

            JsonNode xy = offset.get("xy");
            JsonNode ll = offset.get("ll");

            if (xy != null) {
                offsetObj.setXY(NodeListXYBuilder.genericNodeListXY(xy));
            } else if (ll != null) {
                offsetObj.setLL(NodeListLLBuilder.genericNodeListLL(ll));
            }

            genericOffsetSystem.setOffset(offsetObj);
        }

        return genericOffsetSystem;
    }
}
