package us.dot.its.jpo.ode.plugin.j2735.builders;

import java.math.BigDecimal;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735NodeLLmD64b;
import us.dot.its.jpo.ode.plugin.j2735.J2735NodeOffsetPointXY;
import us.dot.its.jpo.ode.plugin.j2735.J2735NodeXY;
import us.dot.its.jpo.ode.plugin.j2735.J2735Node_XY;
import us.dot.its.jpo.ode.plugin.j2735.J2735NodeAttributeSetXY;


public class NodeBuilder {

	public static J2735NodeXY genericNode(JsonNode NodeJson) {
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
			J2735NodeAttributeSetXY attributeSetXY = new J2735NodeAttributeSetXY();
			JsonNode attributes = NodeJson.get("attributes");

			// TODO: finish attributes with all of the lists

			if(attributes.get("dElevation") != null)
			{
				attributeSetXY.setdElevation(attributes.get("dElevation").asInt());
			}

			nodeXY.setAttributes(attributeSetXY);
		}
		return nodeXY;
	}

}
