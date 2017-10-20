package us.dot.its.jpo.ode.plugin.j2735.builders;

import java.math.BigDecimal;
import java.text.ParseException;
import java.time.ZonedDateTime;
import java.util.Iterator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import us.dot.its.jpo.ode.util.DateTimeUtils;
import us.dot.its.jpo.ode.util.JsonUtils;

public class TravelerMessageFromHumanToAsnConverter {

   public static ObjectNode changeTravelerInformationToAsnValues(JsonNode timData) {

      // Cast to ObjectNode to allow manipulation in place
      ObjectNode replacedTim = (ObjectNode) timData;
      ObjectNode timDataObjectNode = (ObjectNode) replacedTim.get("tim");
      
      replacedTim.remove("index");
      
      
      // TODO packetID is optional
      timDataObjectNode.put("packetID", String.format("%018X", timDataObjectNode.get("packetID").asInt()));

      timDataObjectNode.put("timeStamp",
            translateISOTimeStampToMinuteOfYear(timDataObjectNode.get("timeStamp").asText()));
      timDataObjectNode.set("dataFrames", replaceDataFrames(timDataObjectNode.get("dataframes")));
      timDataObjectNode.remove("dataframes");

      return replacedTim;

   }

   public static JsonNode replaceDataFrames(JsonNode dataFrames) {

      if (dataFrames == null) {
         return JsonUtils.newNode();
      }

      ArrayNode replacedDataFrames = JsonUtils.newNode().arrayNode();

      if (dataFrames.isArray()) {
         Iterator<JsonNode> dataFramesIter = dataFrames.elements();

         while (dataFramesIter.hasNext()) {
            ObjectNode oldFrame = (ObjectNode) dataFramesIter.next();
            replacedDataFrames.add(JsonUtils.newObjectNode("TravelerDataFrame", replaceDataFrame(oldFrame)));
         }
      }

      return replacedDataFrames;
   }

   /**
    * Convert necessary fields within the dataframe. For now just pos3d.
    * 
    * @param dataFrame
    */
   public static ObjectNode replaceDataFrame(ObjectNode dataFrame) {

      // INPUT
      //////
      // "dataframes": [
      // {
      // "startDateTime": "2017-08-02T22:25:00.000Z",
      // "durationTime": 1,
      // "frameType": "1",
      // "sspTimRights": "0",
      // "msgID": "RoadSignID",
      // "position": {
      // "latitude": "41.678473",
      // "longitude": "-108.782775",
      // "elevation": "917.1432"
      // },
      // "viewAngle": "1010101010101010",
      // "mutcd": "5",
      // "crc": "0000000000000000",
      // "priority": "0",
      // "sspLocationRights": "3",
      // "regions": []
      // "sspMsgTypes": "2",
      // "sspMsgContent": "3",
      // "content": "Advisory",
      // "items": [
      // "513"
      // ],
      // "url": "null"
      // }
      // ]

      /// OUTPUT:
      //////
      // <dataFrames>
      // <TravelerDataFrame>
      // <startYear>2017</startYear>
      // <startTime>308065</startTime>
      // </TravelerDataFrame>
      // </dataFrames>

      // sspTimRights does not need replacement
      // sspMsgRights1 does not need replacement
      // sspMsgRights2 does not need replacement
      // priority does not need replacement
      // durationTime does not need replacement
      // url does not need replacement
      
      

      replaceDataFrameTimestamp(dataFrame);

      // replace content
      dataFrame = replaceContent(dataFrame);

      // replace frameType
      replaceFrameType(dataFrame);

      // replace the msgID and relevant fields
      replaceMsgId(dataFrame);

      // replace the geographical path regions
      dataFrame.set("regions", transformRegions(dataFrame.get("regions")));

      return dataFrame;
   }

   public static long translateISOTimeStampToMinuteOfYear(String isoTime) {
      int startYear = 0;
      int startMinute = 527040;
      try {
         ZonedDateTime zDateTime = DateTimeUtils.isoDateTime(isoTime);
         startYear = zDateTime.getYear();
         startMinute = (int) DateTimeUtils.difference(DateTimeUtils.isoDateTime(startYear, 1, 1, 0, 0, 0, 0), zDateTime)
               / 60000;
      } catch (ParseException e) {
         // failed to parse datetime, default back to unknown values
      }

      return startMinute;
   }

   public static void replaceDataFrameTimestamp(ObjectNode dataFrame) {

      // EXPECTED INPUT:
      // "timeStamp": "2017-08-03T22:25:36.297Z"

      // EXPECTED OUTPUT:
      // <startYear>2017</startYear>
      // <startTime>308065</startTime>

      // unknown year value = 0
      // unknown minuteofyear = 527040
      int startYear = 0;
      int startMinute = 527040;
      try {
         ZonedDateTime zDateTime = DateTimeUtils.isoDateTime(dataFrame.get("startDateTime").asText());
         startYear = zDateTime.getYear();
         startMinute = (int) DateTimeUtils.difference(DateTimeUtils.isoDateTime(startYear, 1, 1, 0, 0, 0, 0), zDateTime)
               / 60000;
      } catch (ParseException e) {
         // failed to parse datetime, default back to unknown values
      }

      dataFrame.put("startYear", startYear);
      dataFrame.put("startTime", startMinute);
      dataFrame.remove("startDateTime");
   }

   public static ObjectNode replaceContent(JsonNode dataFrame) {

      // EXPECTED OUTPUT:
      ///////
      // <content>
      // <advisory>
      // <SEQUENCE>
      // <item>
      // <itis>513</itis>
      // </item>
      // </SEQUENCE>
      // </advisory>
      // </content>

      // EXPECTED INPUT:
      ////////
      // "content": "Advisory",
      // "items": [
      // "513"
      // ],
      
      ObjectNode updatedNode = (ObjectNode) dataFrame;

      // step 1, figure out the name of the content
      String contentName = updatedNode.get("content").asText();
      String replacedContentName;
      if ("Work Zone".equals(contentName)) {
         replacedContentName = "workZone";
      } else if ("Speed Limit".equals(contentName)) {
         replacedContentName = "speedLimit";
      } else if ("Exit Service".equals(contentName)) {
         replacedContentName = "exitService";
      } else if ("Generic Signage".equals(contentName)) {
         replacedContentName = "genericSign";
      } else {
         // default
         replacedContentName = "advisory";
      }
      updatedNode.remove("content");
      updatedNode.put("frameType", replacedContentName);

      // step 2, reformat item list
      ArrayNode items = (ArrayNode) updatedNode.get("items");
      ArrayNode newItems = JsonUtils.newNode().arrayNode();
      if (items.isArray()) {
         // take the array of ITIScodesAndText items and transform it into
         // schema-appropriate array

         Iterator<JsonNode> itemsIter = items.elements();

         while (itemsIter.hasNext()) {
            JsonNode curItem = itemsIter.next();
            // check to see if it is a number or text
            if (curItem.asText().matches("^[0-9]")) {
               // it's a number, so create "itis"
               newItems.add(JsonUtils.newNode().set("item", JsonUtils.newNode().put("text", curItem.asText())));
            } else {
               newItems.add(JsonUtils.newNode().set("item", JsonUtils.newNode().put("itis", curItem.asInt())));
            }
         }
      }

      // final step, transform into correct format
      JsonNode sequence = JsonUtils.newNode().set("SEQUENCE", newItems);
      
      // TODO the following field is called "content" but this results in an failed conversion to XML
      // see @us.dot.its.jpo.ode.traveler.TimController.publish
      updatedNode.set("tcontent", JsonUtils.newNode().set(replacedContentName, sequence));
      updatedNode.remove("items");
      
      return updatedNode;
   }

   public static void replaceFrameType(ObjectNode dataFrame) {
      String frameType;
      switch (dataFrame.get("frameType").asInt()) {
      case 1:
         frameType = "advisory";
         break;
      case 2:
         frameType = "roadSignage";
         break;
      case 3:
         frameType = "commercialSignage";
         break;
      default:
         frameType = "unknown";
      }
      dataFrame.set("frameType", JsonUtils.newObjectNode(frameType, "EMPTY_TAG"));
   }

   public static void replaceMsgId(ObjectNode dataFrame) {

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

      JsonNode msgID = dataFrame.get("msgID");
      if (msgID != null) {
         if (msgID.asText().equals("RoadSignID")) {

            ObjectNode roadSignID = JsonUtils.newNode();
            roadSignID.set("position", Position3DBuilder.position3D(dataFrame.get("position")));
            roadSignID.put("viewAngle", dataFrame.get("viewAngle").asText());

            // transform mutcdCode
            String mutcdName;
            switch (dataFrame.get("mutcd").asInt()) {
            case 1:
               mutcdName = "regulatory";
               break;
            case 2:
               mutcdName = "warning";
               break;
            case 3:
               mutcdName = "maintenance";
               break;
            case 4:
               mutcdName = "motoristService";
               break;
            case 5:
               mutcdName = "guide";
               break;
            case 6:
               mutcdName = "rec";
               break;
            default:
               mutcdName = "none";
            }
            roadSignID.set("mutcdCode", JsonUtils.newNode().put(mutcdName, "EMPTY_TAG"));
            roadSignID.put("crc", String.format("%04X", dataFrame.get("crc").asInt()));

            dataFrame.remove("msgID");
            dataFrame.remove("position");
            dataFrame.remove("viewAngle");
            dataFrame.remove("mutcd");
            dataFrame.remove("crc");

            ObjectNode msgId = JsonUtils.newNode();
            msgId.set("roadSignID", roadSignID);

            dataFrame.set("msgID", msgId);

         } else if (msgID.asText().equals("FurtherInfoID")) {

            dataFrame.remove("msgID");
            dataFrame.remove("position");
            dataFrame.remove("viewAngle");
            dataFrame.remove("mutcd");
            dataFrame.remove("crc");

            ObjectNode msgId = JsonUtils.newNode();
            msgId.put("furtherInfoID", msgID.get("FurtherInfoID").asText());

            dataFrame.set("msgID", msgId);
         }
      }
   }

   public static JsonNode transformRegions(JsonNode regions) {
      ArrayNode replacedRegions = JsonUtils.newNode().arrayNode();

      if (regions.isArray()) {
         Iterator<JsonNode> regionsIter = regions.elements();

         while (regionsIter.hasNext()) {
            JsonNode curRegion = regionsIter.next();
            replacedRegions.add(JsonUtils.newNode().set("GeographicalPath", transformRegion(curRegion)));
         }
      }

      return replacedRegions;
   }

   public static ObjectNode transformRegion(JsonNode region) {

      //// EXPECTED INPUT:
      // "name": "Testing TIM",
      // "regulatorID": "0",
      // "segmentID": "33",
      // "anchorPosition": {
      // "latitude": "41.2500807",
      // "longitude": "-111.0093847",
      // "elevation": "2020.6969900289998"
      // },
      // "laneWidth": "7",
      // "directionality": "3",
      // "closedPath": "false",
      // "description": "path",
      // "path": {},
      // "direction": "0000000000001010"
      // }

      //// EXPECTED OUTPUT:
      // <GeographicalPath>
      // .<name>Testing TIM</name>
      // .<id>
      // ..<region>0</region>
      // ..<id>33</id>
      // .</id>
      // .<anchor>
      // ..<lat>412500807</lat>
      // ..<long>-1110093847</long>
      // ..<elevation>20206</elevation>
      // .</anchor>
      // .<laneWidth>700</laneWidth>
      // .<directionality>
      // ..<both />
      // .</directionality>
      // .<closedPath>
      // ..<false />
      // .</closedPath>
      // .<direction>0000000000010100</direction>
      // .<description>
      // .</description>
      // </GeographicalPath>

      ObjectNode updatedNode = (ObjectNode) region;

      // name does not need to be replaced

      // replace regulatorID and segmentID with id
      ObjectNode id = JsonUtils.newNode().put("region", updatedNode.get("regulatorID").asInt()).put("id",
            updatedNode.get("segmentID").asInt());
      updatedNode.set("id", id);
      updatedNode.remove("regulatorID");
      updatedNode.remove("segmentID");

      // replace "anchorPosition" with "anchor" and translate values
      updatedNode.set("anchor", Position3DBuilder.position3D(updatedNode.get("anchorPosition")));
      updatedNode.remove("anchorPosition");

      // replace LaneWidth
      updatedNode.put("laneWidth", LaneWidthBuilder.laneWidth(updatedNode.get("laneWidth").asLong()));

      // replace directionality
      String directionName;
      switch (updatedNode.get("directionality").asInt()) {
      case 1:
         directionName = "forward";
         break;
      case 2:
         directionName = "reverse";
         break;
      case 3:
         directionName = "both";
         break;
      default:
         directionName = "unavailable";
      }
      updatedNode.set("directionality", JsonUtils.newNode().put(directionName, "EMPTY_TAG"));

      // replace closed path
      String closedPathBoolean = updatedNode.get("closedPath").asText();
      updatedNode.set("closedPath", JsonUtils.newNode().put(closedPathBoolean, "EMPTY_TAG"));

      // transform regions
      String description = updatedNode.get("description").asText();
      if ("path".equals(description)) {
         ObjectNode newPath = replacePath(updatedNode.get("path"));
         updatedNode.remove("path");
         updatedNode.set("description", JsonUtils.newNode().set("path", newPath));
      } else if ("geometry".equals(description)) {
         ObjectNode newGeometry = replaceGeometry(updatedNode.get("geometry"));
         updatedNode.remove("geometry");
         updatedNode.set("description", JsonUtils.newNode().set("geometry", newGeometry));
      } else if ("oldRegion".equals(description)) {
         ObjectNode newOldRegion = replaceOldRegion(updatedNode.get("oldRegion"));
         updatedNode.remove("oldRegion");
         updatedNode.set("description", JsonUtils.newNode().set("oldRegion", newOldRegion));
      }

      return updatedNode;

   }

   private static ObjectNode replacePath(JsonNode pathNode) {

      //// EXPECTED INPUT:
      // "path": {
      // "scale": "0",
      // "type": "ll",
      // "nodes": []
      // }

      //// EXPECTED OUTPUT:
      // <path>
      // .<scale>0</scale>
      // .<offset>
      // ..<ll>
      // ...<nodes>
      // ...</nodes>
      // ..</ll>
      // .</offset>
      // </path>

      ObjectNode updatedNode = (ObjectNode) pathNode;

      // zoom does not need to be replaced
      String nodeType = updatedNode.get("type").asText();
      if ("ll".equals(nodeType)) {
         JsonNode nodeList = JsonUtils.newNode().set("NodeLL",  replaceNodeListLL(updatedNode.get("nodes")));
         updatedNode.set("offset", JsonUtils.newNode().set("ll", JsonUtils.newNode().set("nodes", nodeList)));
         updatedNode.remove("nodes");
      } else if ("xy".equals(nodeType)) {
         //ObjectNode nodeList = replaceNodeListXY(updatedNode.get("nodes"));
         JsonNode nodeList = JsonUtils.newNode().set("NodeXY",  replaceNodeListXY(updatedNode.get("nodes")));
         //ObjectNode xy = JsonUtils.newObjectNode("xy", nodeList);
         updatedNode.set("offset", JsonUtils.newNode().set("xy", JsonUtils.newNode().set("nodes", nodeList)));
         //updatedNode.set("offset", xy);
         updatedNode.remove("nodes");
      }

      updatedNode.remove("type");
      return updatedNode;
   }

   private static ArrayNode replaceNodeListLL(JsonNode jsonNode) {

      // technically this can have more options in the future

      return replaceNodeSetLL((ArrayNode) jsonNode);
   }

   private static ArrayNode replaceNodeSetLL(ArrayNode inputNodeList) {

      //// EXPECTED INPUT:
      // "nodes": []

      // EXPECTED OUTPUT:
      // <nodes>
      // .<NodeLL>
      // .</NodeLL>
      // .<NodeLL>
      // .</NodeLL>
      // </nodes>

      ArrayNode outputNodeList = JsonUtils.newNode().arrayNode();

      if (inputNodeList.isArray()) {
         Iterator<JsonNode> nodeListIter = inputNodeList.elements();

         while (nodeListIter.hasNext()) {
            JsonNode inputNode = nodeListIter.next();
            outputNodeList.add(replaceNodeLL(inputNode));
         }
      }

      return outputNodeList;
   }

   private static ObjectNode replaceNodeLL(JsonNode oldNode) {

      //// EXPECTED INPUT:
      // {
      // "nodeLong": "0.0031024",
      // "nodeLat": "0.0014506",
      // "delta": "node-LL3"
      // }

      //// EXPECTED OUTPUT:
      // <NodeLL>
      // .<delta>
      // ..<node-LL3>
      // ...<lon>14506</lon>
      // ...<lat>31024</lat>
      // ..</node-LL3>
      // .</delta>
      // </NodeLL>

      BigDecimal latOffset = oldNode.get("nodeLat").decimalValue();
      BigDecimal longOffset = oldNode.get("nodeLong").decimalValue();
      JsonNode delta = oldNode.get("delta");
      Integer transformedLat = null;
      Integer transformedLong = null;

      if ("node-LL1".equals(delta.asText())) {
         transformedLat = OffsetLLB12Builder.offsetLLB12(latOffset);
         transformedLong = OffsetLLB12Builder.offsetLLB12(longOffset);
      } else if ("node-LL2".equals(delta.asText())) {
         transformedLat = OffsetLLB14Builder.offsetLLB14(latOffset);
         transformedLong = OffsetLLB14Builder.offsetLLB14(longOffset);
      } else if ("node-LL3".equals(delta.asText())) {
         transformedLat = OffsetLLB16Builder.offsetLLB16(latOffset);
         transformedLong = OffsetLLB16Builder.offsetLLB16(longOffset);
      } else if ("node-LL4".equals(delta.asText())) {
         transformedLat = OffsetLLB18Builder.offsetLLB18(latOffset);
         transformedLong = OffsetLLB18Builder.offsetLLB18(longOffset);
      } else if ("node-LL5".equals(delta.asText())) {
         transformedLat = OffsetLLB22Builder.offsetLLB22(latOffset);
         transformedLong = OffsetLLB22Builder.offsetLLB22(longOffset);
      } else if ("node-LL6".equals(delta.asText())) {
         transformedLat = OffsetLLB24Builder.offsetLLB24(latOffset);
         transformedLong = OffsetLLB24Builder.offsetLLB24(longOffset);
      } else if ("node-LatLon".equals(delta.asText())) {
         transformedLat = LatitudeBuilder.latitude(latOffset);
         transformedLong = LongitudeBuilder.longitude(longOffset);
      }

      ObjectNode latLong = JsonUtils.newNode().put("lat", transformedLat).put("lon", transformedLong);

      ObjectNode innerNode = (ObjectNode) JsonUtils.newNode().set(delta.asText(), latLong);
      ObjectNode deltaNode = (ObjectNode) JsonUtils.newNode().set("delta", innerNode);
      //ObjectNode outerNode = (ObjectNode) JsonUtils.newNode().set("NodeLL", deltaNode);

      return deltaNode;
   }

   public static ObjectNode replaceGeometry(JsonNode geometry) {

      // direction HeadingSlice
      // extent Extent OPTIONAL
      // laneWidth LaneWidth OPTIONAL
      // circle Circle

      ObjectNode updatedNode = (ObjectNode) geometry;

      // direction does not need to be replaced
      // extend does not need to be replaced

      // replace lane width
      if (updatedNode.get("laneWidth") != null) {
         updatedNode.put("laneWidth", LaneWidthBuilder.laneWidth(updatedNode.get("laneWidth").asLong()));
      }

      // replace circle
      replaceCircle(updatedNode.get("circle"));

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

      // Circle ::= SEQUENCE {
      // center Position3D,
      // radius Radius-B12,
      // units DistanceUnits
      // }

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
         updatedNode.set("anchor", Position3DBuilder.position3D(updatedNode.get("anchorPosition")));
         updatedNode.remove("anchorPosition");
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
         replaceComputedLane(updatedNode.get("computed"));
      }

      return updatedNode;
   }

   private static ObjectNode replaceComputedLane(JsonNode jsonNode) {
      // referenceLaneId LaneID
      // offsetXaxis CHOICE {
      // small DrivenLineOffsetSm,
      // large DrivenLineOffsetLg
      // }
      // offsetYaxis CHOICE {
      // small DrivenLineOffsetSm,
      // large DrivenLineOffsetLg
      // }
      // rotateXY Angle OPTIONAL
      // scaleXaxis Scale-B12 OPTIONAL
      // scaleYaxis Scale-B12 OPTIONAL

      // TODO REST schema here is very different than ASN schema
      // must verify correct structure

      // lane id does not need replacement

      // detect and remove the nodes
      ObjectNode updatedNode = (ObjectNode) jsonNode;

      if (updatedNode.get("offsetSmallX") != null) {
         ObjectNode small = JsonUtils.newObjectNode("small",
               DrivenLineOffsetSmBuilder.drivenLaneOffsetSm(updatedNode.get("offsetSmallX").decimalValue()));
         updatedNode.set("offsetXaxis", small);
         updatedNode.remove("offsetSmallX");
      }
      if (updatedNode.get("offsetLargeX") != null) {
         ObjectNode large = JsonUtils.newObjectNode("large",
               DrivenLineOffsetLgBuilder.drivenLineOffsetLg(updatedNode.get("offsetLargeX").decimalValue()));
         updatedNode.set("offsetXaxis", large);
         updatedNode.remove("offsetLargeX");
      }
      if (updatedNode.get("offsetSmallY") != null) {
         ObjectNode small = JsonUtils.newObjectNode("small",
               DrivenLineOffsetSmBuilder.drivenLaneOffsetSm(updatedNode.get("offsetSmallY").decimalValue()));
         updatedNode.set("offsetYaxis", small);
         updatedNode.remove("offsetSmallY");
      }
      if (updatedNode.get("offsetLargeY") != null) {
         ObjectNode large = JsonUtils.newObjectNode("large",
               DrivenLineOffsetLgBuilder.drivenLineOffsetLg(updatedNode.get("offsetLargeY").decimalValue()));
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

      ArrayNode newNodeSet = JsonUtils.newNode().arrayNode();

      if (updatedNode.isArray()) {
         Iterator<JsonNode> nodeSetXYIter = updatedNode.elements();

         while (nodeSetXYIter.hasNext()) {
            JsonNode oldNode = nodeSetXYIter.next();
            newNodeSet.add(replaceNodeXY(oldNode));
         }
      }

      updatedNode.set("nodes", newNodeSet);

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

      return updatedNode;
   }

}
