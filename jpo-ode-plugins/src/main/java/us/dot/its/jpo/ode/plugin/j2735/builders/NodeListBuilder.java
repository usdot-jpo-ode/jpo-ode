package us.dot.its.jpo.ode.plugin.j2735.builders;

import java.util.Iterator;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735NodeListXY;
import us.dot.its.jpo.ode.plugin.j2735.J2735NodeSetXY;

public class NodeListBuilder {

	public static J2735NodeListXY genericNodeList(JsonNode nodeListNode) {

		J2735NodeListXY nodeList = new J2735NodeListXY();
		if (nodeListNode.get("nodes") != null) {
			JsonNode nodeSetXYNode = nodeListNode.get("nodes").get("NodeXY");
			J2735NodeSetXY nodeSet = new J2735NodeSetXY();
			if (nodeSetXYNode!= null && nodeSetXYNode.isArray()) {
				Iterator<JsonNode> elements = nodeSetXYNode.elements();
				while (elements.hasNext()) {
					nodeSet.getNodes().add(NodeBuilder.genericNode(elements.next()));
				}
			} else {
				nodeSet.getNodes().add(NodeBuilder.genericNode(nodeSetXYNode));

			}
			nodeList.setNodes(nodeSet);
		} else if (nodeListNode.get("computed") != null) {
			// TODO
		}

		return nodeList;
	}

}
