package us.dot.its.jpo.ode.plugin.j2735.builders;

import java.util.Iterator;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735NodeListXY;
import us.dot.its.jpo.ode.plugin.j2735.J2735NodeSetXY;

public class NodeListBuilder {

	public static J2735NodeListXY genericNodeList(JsonNode nodeListNode) {

		J2735NodeListXY nodeList = new J2735NodeListXY();

		if (nodeListNode.get("nodes") != null) {
			J2735NodeSetXY nodeSet = new J2735NodeSetXY();

			JsonNode nodeSetXYNode = nodeListNode.get("nodes");
			if (nodeSetXYNode!= null && nodeSetXYNode.isArray()) {
				Iterator<JsonNode> elements = nodeSetXYNode.elements();
				while (elements.hasNext()) {
					nodeSet.getNodes().add(NodeBuilder.genericNode(elements.next().get("NodeXY")));
				}
			} else {
				nodeSet.getNodes().add(NodeBuilder.genericNode(nodeSetXYNode.get("NodeXY")));
			}

			nodeList.setNodes(nodeSet);
		} else if (nodeListNode.get("computed") != null) {
			// TODO
		}

		return nodeList;
	}

}
