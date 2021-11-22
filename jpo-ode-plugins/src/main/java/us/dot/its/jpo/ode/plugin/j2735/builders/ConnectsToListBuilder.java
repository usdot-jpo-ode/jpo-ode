package us.dot.its.jpo.ode.plugin.j2735.builders;

import java.util.Iterator;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735ConnectsToList;

public class ConnectsToListBuilder {
    private ConnectsToListBuilder() {
		throw new UnsupportedOperationException();
	}
	
	public static J2735ConnectsToList genericConnectsToList(JsonNode connectsToNode) {
		J2735ConnectsToList connectsToList = new J2735ConnectsToList();

		JsonNode connection = connectsToNode.get("Connection");
		if (connection != null && connection.isArray()) {
			Iterator<JsonNode> elements = connection.elements();

			while (elements.hasNext()) {
				connectsToList.getConnectsTo()
                    .add(ConnectionBuilder.genericConnection(elements.next()));
			}
		} else if (connection != null) {
			connectsToList.getConnectsTo()
				.add(ConnectionBuilder.genericConnection(connection));
		}

		return connectsToList;
	}
}
