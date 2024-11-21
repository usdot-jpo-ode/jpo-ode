package us.dot.its.jpo.ode.plugin.j2735.builders;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735NodeLLmD64b;
import us.dot.its.jpo.ode.plugin.j2735.J2735NodeOffsetPointXY;
import us.dot.its.jpo.ode.plugin.j2735.J2735NodeXY;
import us.dot.its.jpo.ode.plugin.j2735.J2735Node_XY;
import us.dot.its.jpo.ode.plugin.j2735.J2735SegmentAttribute;
import us.dot.its.jpo.ode.plugin.j2735.J2735LaneDataAttribute;
import us.dot.its.jpo.ode.plugin.j2735.J2735NodeAttribute;
import us.dot.its.jpo.ode.plugin.j2735.J2735NodeAttributeSet;


public class NodeXYBuilder {
	private NodeXYBuilder() {
		throw new UnsupportedOperationException();
	}

	public static J2735NodeXY genericNodeXY(JsonNode NodeJson) {
		J2735NodeXY nodeXY = new J2735NodeXY();
		if (NodeJson.get("delta") != null) {
			J2735NodeOffsetPointXY pointoffsetXY = new J2735NodeOffsetPointXY();
			JsonNode NodeOffsetNode = NodeJson.get("delta");
			if(NodeOffsetNode.get("node-XY1") != null)
			{
				BigDecimal x =BigDecimal.valueOf( NodeOffsetNode.get("node-XY1").get("x").asInt());
				BigDecimal y =BigDecimal.valueOf( NodeOffsetNode.get("node-XY1").get("y").asInt());
				J2735Node_XY point = new J2735Node_XY(x,y);
				pointoffsetXY.setNodeXY1(point);
			}
			if(NodeOffsetNode.get("node-XY2") != null)
			{
				BigDecimal x =BigDecimal.valueOf( NodeOffsetNode.get("node-XY2").get("x").asInt());
				BigDecimal y =BigDecimal.valueOf( NodeOffsetNode.get("node-XY2").get("y").asInt());
				J2735Node_XY point = new J2735Node_XY(x,y);
				pointoffsetXY.setNodeXY2(point);
			}
			if(NodeOffsetNode.get("node-XY3") != null)
			{
				BigDecimal x =BigDecimal.valueOf( NodeOffsetNode.get("node-XY3").get("x").asInt());
				BigDecimal y =BigDecimal.valueOf( NodeOffsetNode.get("node-XY3").get("y").asInt());
				J2735Node_XY point = new J2735Node_XY(x,y);
				pointoffsetXY.setNodeXY3(point);
			}
			if(NodeOffsetNode.get("node-XY4") != null)
			{
				BigDecimal x =BigDecimal.valueOf( NodeOffsetNode.get("node-XY4").get("x").asInt());
				BigDecimal y =BigDecimal.valueOf( NodeOffsetNode.get("node-XY4").get("y").asInt());
				J2735Node_XY point = new J2735Node_XY(x,y);
				pointoffsetXY.setNodeXY4(point);
			}
			if(NodeOffsetNode.get("node-XY5") != null)
			{
				BigDecimal x =BigDecimal.valueOf( NodeOffsetNode.get("node-XY5").get("x").asInt());
				BigDecimal y =BigDecimal.valueOf( NodeOffsetNode.get("node-XY5").get("y").asInt());
				J2735Node_XY point = new J2735Node_XY(x,y);
				pointoffsetXY.setNodeXY5(point);
			}
			if(NodeOffsetNode.get("node-XY6") != null)
			{
				BigDecimal x =BigDecimal.valueOf( NodeOffsetNode.get("node-XY6").get("x").asInt());
				BigDecimal y =BigDecimal.valueOf( NodeOffsetNode.get("node-XY6").get("y").asInt());
				J2735Node_XY point = new J2735Node_XY(x,y);
				pointoffsetXY.setNodeXY6(point);
			}
			if(NodeOffsetNode.get("node-LatLon") != null)
			{
				BigDecimal lon =BigDecimal.valueOf( NodeOffsetNode.get("node-LatLon").get("lon").asInt());
				BigDecimal lat =BigDecimal.valueOf( NodeOffsetNode.get("node-LatLon").get("lat").asInt());
				J2735NodeLLmD64b point = new J2735NodeLLmD64b(lon,lat);
				pointoffsetXY.setNodeLatLon(point);
			}
			nodeXY.setDelta(pointoffsetXY);
		}

		if (NodeJson.get("attributes") != null) {
			J2735NodeAttributeSet attributeSet = new J2735NodeAttributeSet();
			JsonNode attributes = NodeJson.get("attributes");

			JsonNode localNode = attributes.get("localNode");
            if (localNode != null) {
                JsonNode nodeAttributeXY = localNode.get("NodeAttributeXY");
                if (nodeAttributeXY != null) {
                    List<J2735NodeAttribute> naList = new ArrayList<>();

                    if (nodeAttributeXY.isArray()) {
                        Iterator<JsonNode> elements = nodeAttributeXY.elements();
        
                        while (elements.hasNext()) {
                            String nodeAttributeValue = elements.next().fields().next().getKey();
                            naList.add(J2735NodeAttribute.valueOf(nodeAttributeValue));
                        }
                    } else {
                        String nodeAttributeValue = nodeAttributeXY.fields().next().getKey();
                        naList.add(J2735NodeAttribute.valueOf(nodeAttributeValue));
                    }

                    attributeSet.setLocalNode(naList.toArray(new J2735NodeAttribute[0]));
                }
            }

            JsonNode disabled = attributes.get("disabled");
            if (disabled != null) {
                JsonNode segmentAttributeXY = disabled.get("SegmentAttributeXY");
                if (segmentAttributeXY != null) {
                    List<J2735SegmentAttribute> saList = new ArrayList<>();

                    if (segmentAttributeXY.isArray()) {
                        Iterator<JsonNode> elements = segmentAttributeXY.elements();
        
                        while (elements.hasNext()) {
                            String segmentAttributeValue = elements.next().fields().next().getKey();
                            saList.add(J2735SegmentAttribute.valueOf(segmentAttributeValue));
                        }
                    } else {
                        String segmentAttributeValue = segmentAttributeXY.fields().next().getKey();
                        saList.add(J2735SegmentAttribute.valueOf(segmentAttributeValue));
                    }

                    attributeSet.setDisabled(saList.toArray(new J2735SegmentAttribute[0]));
                }
            }

            JsonNode enabled = attributes.get("enabled");
            if (enabled != null) {
                JsonNode segmentAttributeXY = enabled.get("SegmentAttributeXY");
                if (segmentAttributeXY != null) {
                    List<J2735SegmentAttribute> saList = new ArrayList<>();

                    if (segmentAttributeXY.isArray()) {
                        Iterator<JsonNode> elements = segmentAttributeXY.elements();
        
                        while (elements.hasNext()) {
                            String segmentAttributeValue = elements.next().fields().next().getKey();
                            saList.add(J2735SegmentAttribute.valueOf(segmentAttributeValue));
                        }
                    } else {
                        String segmentAttributeValue = segmentAttributeXY.fields().next().getKey();
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

			nodeXY.setAttributes(attributeSet);
		}
		return nodeXY;
	}

}
