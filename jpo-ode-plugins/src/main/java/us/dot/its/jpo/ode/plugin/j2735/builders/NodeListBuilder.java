package us.dot.its.jpo.ode.plugin.j2735.builders;

import java.util.Iterator;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735NodeListXY;
import us.dot.its.jpo.ode.plugin.j2735.J2735NodeSetXY;

public class NodeListBuilder {
	private NodeListBuilder() {
		throw new UnsupportedOperationException();
	}

	public static J2735NodeListXY genericNodeList(JsonNode nodeListNode) {
		J2735NodeListXY nodeList = new J2735NodeListXY();

		if (nodeListNode.get("nodes") != null) {
			J2735NodeSetXY nodeSet = new J2735NodeSetXY();

			JsonNode nodeXY = nodeListNode.get("nodes").get("NodeXY");
			if (nodeXY != null && nodeXY.isArray()) {
				Iterator<JsonNode> elements = nodeXY.elements();

				while (elements.hasNext()) {
					nodeSet.getNodes().add(NodeBuilder.genericNode(elements.next()));
				}
			} else if (nodeXY != null) {
				nodeSet.getNodes().add(NodeBuilder.genericNode(nodeXY));
			}

			nodeList.setNodes(nodeSet);
		} else if (nodeListNode.get("computed") != null) {
			// TODO
		}

		return nodeList;
	}

}
