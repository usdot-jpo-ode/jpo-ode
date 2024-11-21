package us.dot.its.jpo.ode.plugin.j2735.builders;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735NodeLL;
import us.dot.its.jpo.ode.plugin.j2735.J2735NodeListLL;

public class NodeListLLBuilder {
    private NodeListLLBuilder() {
        throw new UnsupportedOperationException();
    }

    public static J2735NodeListLL genericNodeListLL(JsonNode nodeListNode) {
        J2735NodeListLL nodeList = new J2735NodeListLL();

        if (nodeListNode.get("nodes") != null) {
            JsonNode nodeLL = nodeListNode.get("nodes").get("NodeLL");
            if (nodeLL != null) {
                List<J2735NodeLL> nllList = new ArrayList<>();

                if (nodeLL.isArray()) {
                    Iterator<JsonNode> elements = nodeLL.elements();

                    while (elements.hasNext()) {
                        nllList.add(NodeLLBuilder.genericNodeLL(elements.next()));
                    }
                } else {
                    nllList.add(NodeLLBuilder.genericNodeLL(nodeLL));
                }

                nodeList.setNodes(nllList.toArray(new J2735NodeLL[0]));
            }
        }

        return nodeList;
    }
}
