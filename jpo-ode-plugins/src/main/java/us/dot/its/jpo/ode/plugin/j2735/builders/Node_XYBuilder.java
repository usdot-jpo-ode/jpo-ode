package us.dot.its.jpo.ode.plugin.j2735.builders;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735Node_XY;

public class Node_XYBuilder {

    private Node_XYBuilder() {
       throw new UnsupportedOperationException();
    }

    public static J2735Node_XY genericNode_XY(JsonNode node) {
        return new J2735Node_XY(OffsetBuilder.genericOffset_B12(node.get("x")), OffsetBuilder.genericOffset_B12(node.get("y")));
    }

}
