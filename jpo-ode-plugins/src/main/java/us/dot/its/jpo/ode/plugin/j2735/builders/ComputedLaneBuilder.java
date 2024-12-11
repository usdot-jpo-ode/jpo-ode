package us.dot.its.jpo.ode.plugin.j2735.builders;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735ComputedLane;

public class ComputedLaneBuilder {
    private ComputedLaneBuilder() {
        throw new UnsupportedOperationException();
    }

    public static J2735ComputedLane genericComputedLane(JsonNode computed) {
        J2735ComputedLane computedLane = new J2735ComputedLane();

        JsonNode referenceLaneId = computed.get("referenceLaneId");
        if (referenceLaneId != null) {
            computedLane.setReferenceLaneId(referenceLaneId.asInt());
        }

        JsonNode offsetXaxis = computed.get("offsetXaxis");
        if (offsetXaxis != null) {
            if (offsetXaxis.get("small") != null) {
                computedLane.setOffsetXaxis(offsetXaxis.get("small").asInt());
            } else if (offsetXaxis.get("large") != null) {
                computedLane.setOffsetXaxis(offsetXaxis.get("large").asInt());
            }
        }

        JsonNode offsetYaxis = computed.get("offsetYaxis");
        if (offsetYaxis != null) {
            if (offsetYaxis.get("small") != null) {
                computedLane.setOffsetYaxis(offsetYaxis.get("small").asInt());
            } else if (offsetYaxis.get("large") != null) {
                computedLane.setOffsetYaxis(offsetYaxis.get("large").asInt());
            }
        }

        JsonNode rotateXY = computed.get("rotateXY");
        if (rotateXY != null) {
            computedLane.setRotateXY(rotateXY.asInt());
        }

        JsonNode scaleXaxis = computed.get("scaleXaxis");
        if (scaleXaxis != null) {
            computedLane.setScaleXaxis(scaleXaxis.asInt());
        }

        JsonNode scaleYaxis = computed.get("scaleYaxis");
        if (scaleYaxis != null) {
            computedLane.setScaleYaxis(scaleYaxis.asInt());
        }

        return computedLane;
    }
}
