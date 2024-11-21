package us.dot.its.jpo.ode.plugin.j2735.builders;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735LaneDataAttribute;
import us.dot.its.jpo.ode.plugin.j2735.J2735NodeAttribute;
import us.dot.its.jpo.ode.plugin.j2735.J2735NodeAttributeSet;
import us.dot.its.jpo.ode.plugin.j2735.J2735NodeLL;
import us.dot.its.jpo.ode.plugin.j2735.J2735NodeLLmD64b;
import us.dot.its.jpo.ode.plugin.j2735.J2735NodeOffsetPointLL;
import us.dot.its.jpo.ode.plugin.j2735.J2735Node_LL;
import us.dot.its.jpo.ode.plugin.j2735.J2735SegmentAttribute;

public class NodeLLBuilder {
    private NodeLLBuilder() {
		throw new UnsupportedOperationException();
	}

    public static J2735NodeLL genericNodeLL(JsonNode NodeJson) {
		J2735NodeLL nodeLL = new J2735NodeLL();
		if (NodeJson.get("delta") != null) {
			J2735NodeOffsetPointLL pointoffsetLL = new J2735NodeOffsetPointLL();
			JsonNode NodeOffsetNode = NodeJson.get("delta");
			if(NodeOffsetNode.get("node-LL1") != null)
			{
				BigDecimal lon =BigDecimal.valueOf( NodeOffsetNode.get("node-LL1").get("lon").asInt());
				BigDecimal lat =BigDecimal.valueOf( NodeOffsetNode.get("node-LL1").get("lat").asInt());
				J2735Node_LL point = new J2735Node_LL(lon,lat);
				pointoffsetLL.setNodeLL1(point);
			}
			if(NodeOffsetNode.get("node-LL2") != null)
			{
				BigDecimal lon =BigDecimal.valueOf( NodeOffsetNode.get("node-LL2").get("lon").asInt());
				BigDecimal lat =BigDecimal.valueOf( NodeOffsetNode.get("node-LL2").get("lat").asInt());
				J2735Node_LL point = new J2735Node_LL(lon,lat);
				pointoffsetLL.setNodeLL2(point);
			}
			if(NodeOffsetNode.get("node-LL3") != null)
			{
				BigDecimal lon =BigDecimal.valueOf( NodeOffsetNode.get("node-LL3").get("lon").asInt());
				BigDecimal lat =BigDecimal.valueOf( NodeOffsetNode.get("node-LL3").get("lat").asInt());
				J2735Node_LL point = new J2735Node_LL(lon,lat);
				pointoffsetLL.setNodeLL3(point);
			}
			if(NodeOffsetNode.get("node-LL4") != null)
			{
				BigDecimal lon =BigDecimal.valueOf( NodeOffsetNode.get("node-LL4").get("lon").asInt());
				BigDecimal lat =BigDecimal.valueOf( NodeOffsetNode.get("node-LL4").get("lat").asInt());
				J2735Node_LL point = new J2735Node_LL(lon,lat);
				pointoffsetLL.setNodeLL4(point);
			}
			if(NodeOffsetNode.get("node-LL5") != null)
			{
				BigDecimal lon =BigDecimal.valueOf( NodeOffsetNode.get("node-LL5").get("lon").asInt());
				BigDecimal lat =BigDecimal.valueOf( NodeOffsetNode.get("node-LL5").get("lat").asInt());
				J2735Node_LL point = new J2735Node_LL(lon,lat);
				pointoffsetLL.setNodeLL5(point);
			}
			if(NodeOffsetNode.get("node-LL6") != null)
			{
				BigDecimal lon =BigDecimal.valueOf( NodeOffsetNode.get("node-LL6").get("lon").asInt());
				BigDecimal lat =BigDecimal.valueOf( NodeOffsetNode.get("node-LL6").get("lat").asInt());
				J2735Node_LL point = new J2735Node_LL(lon,lat);
				pointoffsetLL.setNodeLL6(point);
			}
			if(NodeOffsetNode.get("node-LatLon") != null)
			{
				BigDecimal lon =BigDecimal.valueOf( NodeOffsetNode.get("node-LatLon").get("lon").asInt());
				BigDecimal lat =BigDecimal.valueOf( NodeOffsetNode.get("node-LatLon").get("lat").asInt());
				J2735NodeLLmD64b point = new J2735NodeLLmD64b(lon,lat);
				pointoffsetLL.setNodeLatLon(point);
			}
			nodeLL.setDelta(pointoffsetLL);
		}

		if (NodeJson.get("attributes") != null) {
			J2735NodeAttributeSet attributeSet = new J2735NodeAttributeSet();
			JsonNode attributes = NodeJson.get("attributes");

			JsonNode localNode = attributes.get("localNode");
            if (localNode != null) {
                JsonNode nodeAttributeLL = localNode.get("NodeAttributeLL");
                if (nodeAttributeLL != null) {
                    List<J2735NodeAttribute> naList = new ArrayList<>();

                    if (nodeAttributeLL.isArray()) {
                        Iterator<JsonNode> elements = nodeAttributeLL.elements();
        
                        while (elements.hasNext()) {
                            String nodeAttributeValue = elements.next().fields().next().getKey();
                            naList.add(J2735NodeAttribute.valueOf(nodeAttributeValue));
                        }
                    } else {
                        String nodeAttributeValue = nodeAttributeLL.fields().next().getKey();
                        naList.add(J2735NodeAttribute.valueOf(nodeAttributeValue));
                    }

                    attributeSet.setLocalNode(naList.toArray(new J2735NodeAttribute[0]));
                }
            }

            JsonNode disabled = attributes.get("disabled");
            if (disabled != null) {
                JsonNode segmentAttributeLL = disabled.get("SegmentAttributeLL");
                if (segmentAttributeLL != null) {
                    List<J2735SegmentAttribute> saList = new ArrayList<>();

                    if (segmentAttributeLL.isArray()) {
                        Iterator<JsonNode> elements = segmentAttributeLL.elements();
        
                        while (elements.hasNext()) {
                            String segmentAttributeValue = elements.next().fields().next().getKey();
                            saList.add(J2735SegmentAttribute.valueOf(segmentAttributeValue));
                        }
                    } else {
                        String segmentAttributeValue = segmentAttributeLL.fields().next().getKey();
                        saList.add(J2735SegmentAttribute.valueOf(segmentAttributeValue));
                    }

                    attributeSet.setDisabled(saList.toArray(new J2735SegmentAttribute[0]));
                }
            }

            JsonNode enabled = attributes.get("enabled");
            if (enabled != null) {
                JsonNode segmentAttributeLL = enabled.get("SegmentAttributeLL");
                if (segmentAttributeLL != null) {
                    List<J2735SegmentAttribute> saList = new ArrayList<>();

                    if (segmentAttributeLL.isArray()) {
                        Iterator<JsonNode> elements = segmentAttributeLL.elements();
        
                        while (elements.hasNext()) {
                            String segmentAttributeValue = elements.next().fields().next().getKey();
                            saList.add(J2735SegmentAttribute.valueOf(segmentAttributeValue));
                        }
                    } else {
                        String segmentAttributeValue = segmentAttributeLL.fields().next().getKey();
                        saList.add(J2735SegmentAttribute.valueOf(segmentAttributeValue));
                    }

                    attributeSet.setEnabled(saList.toArray(new J2735SegmentAttribute[0]));
                }
            }

            JsonNode data = attributes.get("data");
            if (data != null) {
                JsonNode laneDataAttribute = data.get("LaneDataAttribute");
                if (laneDataAttribute != null) {
                    List<J2735LaneDataAttribute> ldaList = new ArrayList<>();

                    if (laneDataAttribute.isArray()) {
                        Iterator<JsonNode> elements = laneDataAttribute.elements();
        
                        while (elements.hasNext()) {
                            ldaList.add(LaneDataAttributeBuilder.genericLaneDataAttribute(elements.next()));
                        }
                    } else {
                        ldaList.add(LaneDataAttributeBuilder.genericLaneDataAttribute(laneDataAttribute));
                    }

                    attributeSet.setData(ldaList.toArray(new J2735LaneDataAttribute[0]));
                }
            }

            if(attributes.get("dWidth") != null)
			{
				attributeSet.setdWidth(attributes.get("dWidth").asInt());
			}

			if(attributes.get("dElevation") != null)
			{
				attributeSet.setdElevation(attributes.get("dElevation").asInt());
			}

			nodeLL.setAttributes(attributeSet);
		}
		return nodeLL;
	}
}
