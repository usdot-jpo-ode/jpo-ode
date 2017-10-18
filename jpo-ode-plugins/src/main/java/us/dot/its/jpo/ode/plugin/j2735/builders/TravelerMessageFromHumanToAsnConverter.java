package us.dot.its.jpo.ode.plugin.j2735.builders;

import java.util.Iterator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import us.dot.its.jpo.ode.util.JsonUtils;

public class TravelerMessageFromHumanToAsnConverter {

   public static JsonNode changeTravelerInformationToAsnValues(JsonNode timData) {

      // replace data frames
      // INPUT:
      // "dataframes": [{},{}]
      // OUTPUT:
      // "dataFrames": [
      // {"TravelerDataFrame" : {}},
      // {"TravelerDataFrame" : {}}
      // ]

      // First thing to do is cast to ObjectNode so we can edit values in place
      ObjectNode timDataObjectNode = (ObjectNode) timData;

      replaceDataFrames(timDataObjectNode.get("tim").get("dataframes"));

      return timDataObjectNode;

   }

   public static JsonNode replaceDataFrames(JsonNode dataFrames) {

      if (dataFrames == null) {
         return JsonUtils.newNode();
      }

      ArrayNode replacedDataFrames = JsonUtils.newNode().arrayNode();

      if (dataFrames.isArray()) {
         Iterator<JsonNode> dataFramesIter = dataFrames.elements();

         while (dataFramesIter.hasNext()) {
            JsonNode oldFrame = dataFramesIter.next();
            replacedDataFrames.add(replaceDataFrame(oldFrame));
         }
      } else {
         replacedDataFrames.add(replaceDataFrame(dataFrames));
      }

      return replacedDataFrames;
   }

   /**
    * Convert necessary fields within the dataframe. For now just pos3d.
    * 
    * @param dataFrame
    */
   public static ObjectNode replaceDataFrame(JsonNode dataFrame) {

      ObjectNode updatedNode = (ObjectNode) dataFrame;

      // replace the msgID and relevant fields
      replaceMsgId(updatedNode);

      // replace the geographical path regions
      replaceGeographicalPathRegions(dataFrame.get("regions"));

      return updatedNode;
   }

   public static ObjectNode replaceMsgId(JsonNode msgIDNode) {

      // <msgId>
      // <roadSignID>
      // <position>
      // <lat>416784730</lat>
      // <long>-1087827750</long>
      // <elevation>9171</elevation>
      // </position>
      // <viewAngle>0101010101010100</viewAngle>
      // <mutcdCode>
      // <guide />
      // </mutcdCode>
      // <crc>0000</crc>
      // </roadSignID>
      // </msgId>

      // replace the messageID
      // TODO WRONG SCHEMA STRUCTURE - postion3d here should be inside the
      // RoadSignID element

      ObjectNode updatedNode = (ObjectNode) msgIDNode;

      JsonNode msgID = updatedNode.get("msgID");
      if (msgID != null) {
         if (msgID.asText().equals("RoadSignID")) {

            ObjectNode roadSignID = JsonUtils.newNode();
            JsonUtils.addNode(roadSignID, "position", Position3DBuilder.position3D(updatedNode.get("position")));
            JsonUtils.addNode(roadSignID, "viewAngle", updatedNode.get("viewAngle").asText());
            JsonUtils.addNode(roadSignID, "mutcdCode", updatedNode.get("mutcd").asText());
            roadSignID.put("crc", updatedNode.get("crc").asText());
            // TODO - we can't do the following because .addNode calls as POJO
            // JsonUtils.addNode(roadSignID, "crc",
            // dataFrame.get("crc").asText());

            updatedNode.remove("msgID");
            updatedNode.remove("position");
            updatedNode.remove("viewAngle");
            updatedNode.remove("mutcd");
            updatedNode.remove("crc");

            ObjectNode msgId = JsonUtils.newNode();
            msgId.set("roadSignID", roadSignID);

            updatedNode.set("msgID", msgId);

         } else if (msgID.asText().equals("FurtherInfoID")) {

            // TODO - this may not be correct since msgID schema is inconsistent

            updatedNode.remove("msgID");
            updatedNode.remove("position");
            updatedNode.remove("viewAngle");
            updatedNode.remove("mutcd");
            updatedNode.remove("crc");

            ObjectNode msgId = JsonUtils.newNode();
            msgId.put("furtherInfoID", msgID.get("FurtherInfoID").asText());

            updatedNode.set("msgID", msgId);
         }
      }

      return updatedNode;
   }

   public static JsonNode replaceGeographicalPathRegions(JsonNode regions) {
      ArrayNode replacedRegions = JsonUtils.newNode().arrayNode();

      if (regions.isArray()) {
         Iterator<JsonNode> regionsIter = regions.elements();

         while (regionsIter.hasNext()) {
            JsonNode curRegion = regionsIter.next();
            replacedRegions.add(translateGeoGraphicalPathRegion(curRegion));
         }
      }

      return replacedRegions;
   }

   public static ObjectNode translateGeoGraphicalPathRegion(JsonNode region) {

      ObjectNode updatedNode = (ObjectNode) region;

      // Step 1 - Translate Position3D
      // replace "anchorPosition" with "anchor" and translate values
      updatedNode.set("anchor", Position3DBuilder.position3D(updatedNode.get("anchorPosition")));
      updatedNode.remove("anchorPosition");

      // Step 2 - Translate LaneWidth
      updatedNode.put("laneWidth", LaneWidthBuilder.laneWidth(updatedNode.get("laneWidth").asLong()));

      // Step 3 - translate regions
      if (updatedNode.get("description").get("geometry") != null) {
         updatedNode = replaceGeometry(updatedNode);
      }

      if (updatedNode.get("description").get("oldRegion") != null) {
         updatedNode = replaceOldRegion(updatedNode);
      }

      return updatedNode;

   }

   public static ObjectNode replaceGeometry(JsonNode geometry) {

      ObjectNode updatedNode = (ObjectNode) geometry;

      // replace lane width
      updatedNode.put("laneWidth", LaneWidthBuilder.laneWidth(updatedNode.get("laneWidth").asLong()));

      return updatedNode;
   }

   public static ObjectNode replaceOldRegion(JsonNode oldRegion) {

      // old region == ValidRegion
      // elements:
      // direction - no changes
      // extent - no changes
      // area - needs changes

      ObjectNode updatedNode = (ObjectNode) oldRegion;

      updatedNode.set("area", replaceArea(updatedNode.get("area")));

      return updatedNode;
   }

   public static ObjectNode replaceArea(JsonNode area) {

      // area contains one of:
      // shapePointSet
      // circle
      // regionPointSet

      ObjectNode updatedNode = (ObjectNode) area;

      if (updatedNode.get("shapePointSet") != null) {
         updatedNode.set("shapePointSet", replaceShapePointSet(updatedNode.get("shapePointSet")));

      } else if (updatedNode.get("circle") != null) {
         updatedNode.set("circle", replaceCircle(updatedNode.get("circle")));

      } else if (updatedNode.get("regionPointSet") != null) {
         updatedNode.set("regionPointSet", replaceRegionPointSet(updatedNode.get("regionPointSet")));
      }

      return updatedNode;
   }

   private static ObjectNode replaceRegionPointSet(JsonNode regionPointSet) {
      // regionPointSet contains:
      // anchor
      // zoom
      // nodeList (regionList)
      ObjectNode updatedNode = (ObjectNode) regionPointSet;

      // replace anchor (optional)
      if (updatedNode.get("anchorPosition") != null) {
         updatedNode.set("anchor", Position3DBuilder.position3D(updatedNode.get("anchorPosition")));
         updatedNode.remove("anchorPosition");
      }

      // zoom doesnt need replacement (also optional)

      // replace regionList (required)
      updatedNode.set("nodeList", replaceRegionList(updatedNode.get("nodeList")));

      return updatedNode;
   }

   private static JsonNode replaceRegionList(JsonNode regionList) {
      // TODO Auto-generated method stub
      ObjectNode updatedNode = (ObjectNode) regionList;
      return updatedNode;
   }

   public static ObjectNode replaceCircle(JsonNode circle) {
      
//      Circle ::= SEQUENCE {
//         center Position3D,
//         radius Radius-B12,
//         units DistanceUnits
//         }
      
      ObjectNode updatedNode = (ObjectNode) circle;
      // replace center
      updatedNode.set("center", Position3DBuilder.position3D(updatedNode.get("position")));
      updatedNode.remove("position");
      
      // radius does not need replacement
      
      // units do not need replacement
      
      return updatedNode;
   }

   public static ObjectNode replaceShapePointSet(JsonNode shapePointSet) {
      // shape point set contains:
      // anchor
      // lane width
      // directionality
      // node list

      ObjectNode updatedNode = (ObjectNode) shapePointSet;

      // replace anchor
      if (updatedNode.get("anchor") != null) {
         updatedNode = JsonUtils.setElement("anchor", updatedNode,
               Position3DBuilder.position3D(updatedNode.get("anchorPosition")));
         updatedNode = JsonUtils.removeElement("anchorPosition", updatedNode);
      }

      // replace lane width
      if (updatedNode.get("laneWidth") != null) {
         updatedNode.put("laneWidth", LaneWidthBuilder.laneWidth(updatedNode.get("laneWidth").asLong()));
      }

      // directionality does not need replacement

      // replace node list
      updatedNode.set("nodeList", updatedNode.get("nodeList"));

      return updatedNode;
   }

   public static ObjectNode replaceNodeListXY(JsonNode nodeList) {
      // nodeListXY contains either NodeSetXY or ComputedLane

      ObjectNode updatedNode = (ObjectNode) nodeList;

      if (updatedNode.get("nodes") != null) {
         updatedNode.set("nodes", replaceNodeSetXY(updatedNode.get("nodes")));
      } else if (updatedNode.get("computed") != null) {
         replaceComputedLane(updatedNode.get("computed") );
      }

      return updatedNode;
   }

   private static ObjectNode replaceComputedLane(JsonNode jsonNode) {
//      referenceLaneId LaneID
//      offsetXaxis CHOICE {
//      small DrivenLineOffsetSm,
//      large DrivenLineOffsetLg
//      }
//      offsetYaxis CHOICE {
//      small DrivenLineOffsetSm,
//      large DrivenLineOffsetLg
//      }
//      rotateXY Angle OPTIONAL
//      scaleXaxis Scale-B12 OPTIONAL
//      scaleYaxis Scale-B12 OPTIONAL
      
      
      
      // TODO REST schema here is very different than ASN schema
      // must verify correct structure
      
      
      // lane id does not need replacement
      
      // detect and remove the nodes
      ObjectNode updatedNode = (ObjectNode) jsonNode;
      
      if (updatedNode.get("offsetSmallX") != null) {
         ObjectNode small = JsonUtils.newObjectNode("small", DrivenLineOffsetSmBuilder.drivenLaneOffsetSm(updatedNode.get("offsetSmallX").decimalValue()));
         updatedNode.set("offsetXaxis", small);
         updatedNode.remove("offsetSmallX");
      }
      if (updatedNode.get("offsetLargeX") != null) {
         ObjectNode large = JsonUtils.newObjectNode("large", DrivenLineOffsetLgBuilder.drivenLineOffsetLg(updatedNode.get("offsetLargeX").decimalValue()));
         updatedNode.set("offsetXaxis", large);
         updatedNode.remove("offsetLargeX");
      }
      if (updatedNode.get("offsetSmallY") != null) {
         ObjectNode small = JsonUtils.newObjectNode("small", DrivenLineOffsetSmBuilder.drivenLaneOffsetSm(updatedNode.get("offsetSmallY").decimalValue()));
         updatedNode.set("offsetYaxis", small);
         updatedNode.remove("offsetSmallY");
      }
      if (updatedNode.get("offsetLargeY") != null) {
         ObjectNode large = JsonUtils.newObjectNode("large", DrivenLineOffsetLgBuilder.drivenLineOffsetLg(updatedNode.get("offsetLargeY").decimalValue()));
         updatedNode.set("offsetYaxis", large);
         updatedNode.remove("offsetLargeY");
      }
      if (updatedNode.get("angle") != null) {
         updatedNode.put("rotateXY", AngleBuilder.angle(updatedNode.get("angle").decimalValue()));
         updatedNode.remove("angle");
      }
      if (updatedNode.get("xScale") != null) {
         updatedNode.put("scaleXaxis", ScaleB12Builder.scaleB12(updatedNode.get("xScale").decimalValue()));
         updatedNode.remove("xScale");
      }
      if (updatedNode.get("yScale") != null) {
         updatedNode.put("scaleYaxis", ScaleB12Builder.scaleB12(updatedNode.get("yScale").decimalValue()));
         updatedNode.remove("yScale");
      }
      
      return updatedNode;
      
   }

   public static ObjectNode replaceNodeSetXY(JsonNode nodeSet) {

      ObjectNode updatedNode = (ObjectNode) nodeSet;

      ArrayNode replacedDataFrames = JsonUtils.newNode().arrayNode();

      if (updatedNode.isArray()) {
         Iterator<JsonNode> nodeSetXYIter = updatedNode.elements();

         while (nodeSetXYIter.hasNext()) {
            JsonNode oldNode = nodeSetXYIter.next();
            replacedDataFrames.add(replaceNodeXY(oldNode));
         }
      }

      updatedNode.set("nodes", replacedDataFrames);

      return updatedNode;
   }

   private static JsonNode replaceNodeXY(JsonNode oldNode) {

      // TODO
      // nodexy contains:
      // delta NodeOffsetPointXY
      // attributes NodeAttributeSetXY (optional)

      ObjectNode updatedNode = (ObjectNode) oldNode;

      replaceNodeOffsetPointXY(updatedNode.get("delta"));

      if (updatedNode.get("attributes") != null) {
         replaceNodeAttributeSetXY(updatedNode);
      }

      return null;
   }

   private static ObjectNode replaceNodeAttributeSetXY(JsonNode jsonNode) {
      // localNode NodeAttributeXYList OPTIONAL,
      // disabled SegmentAttributeXYList OPTIONAL,
      // enabled SegmentAttributeXYList OPTIONAL,
      // data LaneDataAttributeList OPTIONAL,
      // dWidth Offset-B10 OPTIONAL,
      // dElevation Offset-B10 OPTIONAL,

      ObjectNode updatedNode = (ObjectNode) jsonNode;

      // localNode NodeAttributeXYList does not need to be replaced

      // disabled SegmentAttributeXYList does not need to be replaced
      // enabled SegmentAttributeXYList does not need to be replaced

      if (updatedNode.get("data") != null) {
         replaceLaneDataAttributeList(updatedNode.get("data"));
      }
      if (updatedNode.get("dWidth") != null) {
         updatedNode.put("dWidth", OffsetB10Builder.offsetB10(updatedNode.get("dWidth").decimalValue()));
      }

      if (updatedNode.get("dElevation") != null) {
         updatedNode.put("dElevation", OffsetB10Builder.offsetB10(updatedNode.get("dElevation").decimalValue()));
      }

      return updatedNode;

   }

   private static ObjectNode replaceLaneDataAttributeList(JsonNode laneDataAttributeList) {

      // iterate and replace
      ObjectNode updatedNode = (ObjectNode) laneDataAttributeList;

      ArrayNode updatedLaneDataAttributeList = JsonUtils.newNode().arrayNode();

      if (laneDataAttributeList.isArray()) {
         Iterator<JsonNode> laneDataAttributeListIter = laneDataAttributeList.elements();

         while (laneDataAttributeListIter.hasNext()) {
            JsonNode oldNode = laneDataAttributeListIter.next();
            updatedLaneDataAttributeList.add(replaceLaneDataAttribute(oldNode));
         }
      }

      updatedNode.set("NodeSetXY", updatedLaneDataAttributeList);

      return updatedNode;
   }

   public static ObjectNode replaceLaneDataAttribute(JsonNode oldNode) {
      // choice between 1 of the following:
      // pathEndPointAngle DeltaAngle
      // laneCrownPointCenter RoadwayCrownAngle
      // laneCrownPointLeft RoadwayCrownAngle
      // laneCrownPointRight RoadwayCrownAngle
      // laneAngle MergeDivergeNodeAngle
      // speedLimits SpeedLimitList

      ObjectNode updatedNode = (ObjectNode) oldNode;

      // pathEndPointAngle DeltaAngle does not need to be replaced
      if (updatedNode.get("pathEndPointAngle") != null) {
         // do nothing
      } else if (updatedNode.get("laneCrownPointCenter") != null) {
         updatedNode.put("laneCrownPointCenter",
               RoadwayCrownAngleBuilder.roadwayCrownAngle(updatedNode.get("laneCrownPointCenter").decimalValue()));
      } else if (updatedNode.get("laneCrownPointLeft") != null) {
         updatedNode.put("laneCrownPointLeft",
               RoadwayCrownAngleBuilder.roadwayCrownAngle(updatedNode.get("laneCrownPointLeft").decimalValue()));
      } else if (updatedNode.get("laneCrownPointRight") != null) {
         updatedNode.put("laneCrownPointRight",
               RoadwayCrownAngleBuilder.roadwayCrownAngle(updatedNode.get("laneCrownPointRight").decimalValue()));
      } else if (updatedNode.get("laneAngle") != null) {
         updatedNode.put("laneAngle",
               MergeDivergeNodeAngleBuilder.mergeDivergeNodeAngle(updatedNode.get("laneAngle").decimalValue()));
      } else if (updatedNode.get("speedLimits") != null) {
         updatedNode.set("speedLimits", replaceSpeedLimitList(updatedNode.get("speedLimits")));
      }

      return null;
   }

   private static ArrayNode replaceSpeedLimitList(JsonNode speedLimitList) {

      // iterate and replace
      ArrayNode updatedLaneDataAttributeList = JsonUtils.newNode().arrayNode();

      if (speedLimitList.isArray()) {
         Iterator<JsonNode> speedLimitListIter = speedLimitList.elements();

         while (speedLimitListIter.hasNext()) {
            JsonNode oldNode = speedLimitListIter.next();
            updatedLaneDataAttributeList.add(replaceRegulatorySpeedLimit(oldNode));
         }
      }

      return updatedLaneDataAttributeList;
   }

   private static ObjectNode replaceRegulatorySpeedLimit(JsonNode regulatorySpeedLimitNode) {
      // contains:
      // type SpeedLimitType
      // speed Velocity

      ObjectNode updatedNode = (ObjectNode) regulatorySpeedLimitNode;
      // type does not need to be replaced
      
      // replace velocity
      updatedNode.put("speed", VelocityBuilder.velocity(updatedNode.get("speed").decimalValue()));

      return updatedNode;
   }

   public static ObjectNode replaceNodeOffsetPointXY(JsonNode delta) {

      // NodeOffsetPointXY contains one of:
      // node-XY1 Node-XY-20b, -- node is within 5.11m of last node
      // node-XY2 Node-XY-22b, -- node is within 10.23m of last node
      // node-XY3 Node-XY-24b, -- node is within 20.47m of last node
      // node-XY4 Node-XY-26b, -- node is within 40.96m of last node
      // node-XY5 Node-XY-28b, -- node is within 81.91m of last node
      // node-XY6 Node-XY-32b, -- node is within 327.67m of last node
      // node-LatLon Node-LLmD-64b, -- node is a full 32b Lat/Lon range

      ObjectNode updatedNode = (ObjectNode) delta;

      if (delta.get("node-XY1") != null) {
         updatedNode.set("node-XY1", replaceNode_XY1(updatedNode.get("node-XY1")));
      } else if (delta.get("node-XY2") != null) {
         updatedNode.set("node-XY2", replaceNode_XY2(updatedNode.get("node-XY2")));

      } else if (delta.get("node-XY3") != null) {
         updatedNode.set("node-XY3", replaceNode_XY3(updatedNode.get("node-XY3")));

      } else if (delta.get("node-XY4") != null) {
         updatedNode.set("node-XY4", replaceNode_XY4(updatedNode.get("node-XY4")));

      } else if (delta.get("node-XY5") != null) {
         updatedNode.set("node-XY5", replaceNode_XY5(updatedNode.get("node-XY5")));

      } else if (delta.get("node-XY6") != null) {
         updatedNode.set("node-XY6", replaceNode_XY6(updatedNode.get("node-XY6")));

      } else if (delta.get("node-LatLon") != null) {
         updatedNode.set("node-LatLon", replaceNode_LatLon(delta.get("node-LatLon")));
      }

      return updatedNode;
   }

   private static JsonNode replaceNode_XY1(JsonNode jsonNode) {
      // xy1 = Node-XY-20b = Offset-B10

      ObjectNode updatedNode = (ObjectNode) jsonNode;

      updatedNode.put("x", OffsetB10Builder.offsetB10(updatedNode.get("x").decimalValue()));
      updatedNode.put("y", OffsetB10Builder.offsetB10(updatedNode.get("y").decimalValue()));

      return updatedNode;
   }

   private static JsonNode replaceNode_XY2(JsonNode jsonNode) {
      // xy2 = Node-XY-22b = Offset-B11
      ObjectNode updatedNode = (ObjectNode) jsonNode;

      updatedNode.put("x", OffsetB11Builder.offsetB11(updatedNode.get("x").decimalValue()));
      updatedNode.put("y", OffsetB11Builder.offsetB11(updatedNode.get("y").decimalValue()));

      return updatedNode;
   }

   private static ObjectNode replaceNode_XY3(JsonNode jsonNode) {
      // XY3 = Node-XY-24b = Offset-B12
      ObjectNode updatedNode = (ObjectNode) jsonNode;

      updatedNode.put("x", OffsetB12Builder.offsetB12(updatedNode.get("x").decimalValue()));
      updatedNode.put("y", OffsetB12Builder.offsetB12(updatedNode.get("y").decimalValue()));

      return updatedNode;
   }

   private static ObjectNode replaceNode_XY4(JsonNode jsonNode) {
      // XY4 = Node-XY-26b = Offset-B13
      ObjectNode updatedNode = (ObjectNode) jsonNode;

      updatedNode.put("x", OffsetB13Builder.offsetB13(updatedNode.get("x").decimalValue()));
      updatedNode.put("y", OffsetB13Builder.offsetB13(updatedNode.get("y").decimalValue()));

      return updatedNode;
   }

   private static ObjectNode replaceNode_XY5(JsonNode jsonNode) {
      // XY5 = Node-XY-28b = Offset-B14
      ObjectNode updatedNode = (ObjectNode) jsonNode;

      updatedNode.put("x", OffsetB14Builder.offsetB14(updatedNode.get("x").decimalValue()));
      updatedNode.put("y", OffsetB14Builder.offsetB14(updatedNode.get("y").decimalValue()));

      return updatedNode;
   }

   private static ObjectNode replaceNode_XY6(JsonNode jsonNode) {
      // XY6 = Node-XY-32b = Offset-B16
      ObjectNode updatedNode = (ObjectNode) jsonNode;

      updatedNode.put("x", OffsetB16Builder.offsetB16(updatedNode.get("x").decimalValue()));
      updatedNode.put("y", OffsetB16Builder.offsetB16(updatedNode.get("y").decimalValue()));

      return updatedNode;
   }

   private static ObjectNode replaceNode_LatLon(JsonNode jsonNode) {
      // LatLon = Node-LLmD-64b
      // Node-LLmD-64b ::= SEQUENCE {
      // lon Longitude,
      // lat Latitude
      // }

      ObjectNode updatedNode = (ObjectNode) jsonNode;

      updatedNode.put("lon", LongitudeBuilder.longitude(updatedNode.get("lon").decimalValue()));
      updatedNode.put("lat", LatitudeBuilder.latitude(updatedNode.get("lat").decimalValue()));

      return null;
   }

}
