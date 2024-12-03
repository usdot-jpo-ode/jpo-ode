package us.dot.its.jpo.ode.plugin.j2735.builders;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735ComputedLane;
import us.dot.its.jpo.ode.plugin.j2735.J2735NodeListXY;
import us.dot.its.jpo.ode.plugin.j2735.J2735NodeXY;

public class NodeListXYBuilder {
	private NodeListXYBuilder() {
		throw new UnsupportedOperationException();
	}

	public static J2735NodeListXY genericNodeListXY(JsonNode nodeListNode) {
		J2735NodeListXY nodeList = new J2735NodeListXY();

		if (nodeListNode.get("nodes") != null) {
			JsonNode nodeXY = nodeListNode.get("nodes").get("NodeXY");
            if (nodeXY != null) {
                List<J2735NodeXY> nxyList = new ArrayList<>();

                if (nodeXY.isArray()) {
                    Iterator<JsonNode> elements = nodeXY.elements();
    
                    while (elements.hasNext()) {
                        nxyList.add(NodeXYBuilder.genericNodeXY(elements.next()));
                    }
                } else {
                    nxyList.add(NodeXYBuilder.genericNodeXY(nodeXY));
                }

                nodeList.setNodes(nxyList.toArray(new J2735NodeXY[0]));
            }
		} else if (nodeListNode.get("computed") != null) {
            JsonNode computedLane = nodeListNode.get("computed");
            if (computedLane != null) {
                nodeList.setComputed(ComputedLaneBuilder.genericComputedLane(computedLane));
            }
		}

		return nodeList;
	}

}
