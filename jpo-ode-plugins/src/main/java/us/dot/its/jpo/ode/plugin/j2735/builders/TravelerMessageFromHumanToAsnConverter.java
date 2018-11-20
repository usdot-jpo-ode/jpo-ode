package us.dot.its.jpo.ode.plugin.j2735.builders;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import us.dot.its.jpo.ode.plugin.j2735.DsrcPosition3D;
import us.dot.its.jpo.ode.util.DateTimeUtils;
import us.dot.its.jpo.ode.util.JsonUtils;
import us.dot.its.jpo.ode.util.JsonUtils.JsonUtilsException;

public class TravelerMessageFromHumanToAsnConverter {

   private static final Logger logger = LoggerFactory.getLogger(TravelerMessageFromHumanToAsnConverter.class);

   // JSON cannot have empty fields like XML, so the XML must be modified by
   // removing all flag field values
   public static final String EMPTY_FIELD_FLAG = "EMPTY_TAG";
   public static final String BOOLEAN_OBJECT_TRUE = "BOOLEAN_OBJECT_TRUE";
   public static final String BOOLEAN_OBJECT_FALSE = "BOOLEAN_OBJECT_FALSE";

   public static void convertTravelerInputDataToEncodableTim(JsonNode tid) throws JsonUtilsException {
      // msgCnt MsgCount,
      // timeStamp MinuteOfTheYear OPTIONAL
      // packetID UniqueMSGID OPTIONAL
      // urlB URL-Base OPTIONAL
      // dataFrames TravelerDataFrameList

      // Cast to ObjectNode to allow manipulation in place
      ObjectNode timDataObjectNode = (ObjectNode) tid.get("tim");
      
      // timeStamp is optional
      if (timDataObjectNode.get("timeStamp") != null) {
         timDataObjectNode.put("timeStamp",
               translateISOTimeStampToMinuteOfYear(timDataObjectNode.get("timeStamp").asText()));
      }

      // urlB is optional but does not need replacement

      // dataFrames are required
      timDataObjectNode.set("dataFrames", transformDataFrames(timDataObjectNode.get("dataframes")));
      timDataObjectNode.remove("dataframes");
   }
      
   public static JsonNode transformDataFrames(JsonNode dataFrames) throws JsonUtilsException {

      if (dataFrames == null) {
         return JsonUtils.newNode();
   }

      ArrayNode replacedDataFrames = JsonUtils.newNode().arrayNode();

      if (dataFrames.isArray()) {
         Iterator<JsonNode> dataFramesIter = dataFrames.elements();

         while (dataFramesIter.hasNext()) {
            ObjectNode oldFrame = (ObjectNode) dataFramesIter.next();
            replaceDataFrame(oldFrame);
            // wrap each data frame inside a TravelerDataFrame
            replacedDataFrames.add(JsonUtils.newObjectNode("TravelerDataFrame", oldFrame));
         }
      }

      return replacedDataFrames;
   }

   public static void replaceDataFrame(ObjectNode dataFrame) throws JsonUtilsException {

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

      // replace sspMsgContent with sspMsgRights2
      dataFrame.put("sspMsgRights2", dataFrame.get("sspMsgContent").asInt());
      dataFrame.remove("sspMsgContent");

      // replace sspMsgTypes with sspMsgRights1
      dataFrame.put("sspMsgRights1", dataFrame.get("sspMsgTypes").asInt());
      dataFrame.remove("sspMsgTypes");

      dataFrame.put("sspTimRights", dataFrame.get("sspTimRights").asText());

      // priority does not need replacement

      // replace durationTime with duratonTime - j2735 schema misspelling
      dataFrame.put("duratonTime", dataFrame.get("durationTime").asInt());
      dataFrame.remove("durationTime");

      // url does not need replacement

      replaceDataFrameTimestamp(dataFrame);

      // replace the geographical path regions
      dataFrame.set("regions", transformRegions(dataFrame.get("regions")));
      // replace content
      replaceContent(dataFrame);

      // replace the msgID and relevant fields
      replaceMsgId(dataFrame);
   }

   public static long translateISOTimeStampToMinuteOfYear(String isoTime) {
      int startYear = 0;
      int startMinute = 527040;
      try {
         ZonedDateTime zDateTime = DateTimeUtils.isoDateTime(isoTime);
         startYear = zDateTime.getYear();
         startMinute = (int) Duration.between(DateTimeUtils.isoDateTime(startYear, 1, 1, 0, 0, 0, 0), zDateTime).toMinutes();
      } catch (Exception e) { // NOSONAR
         logger.warn("Failed to parse datetime {}, defaulting to unknown value {}", isoTime, startMinute);
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
      String startDateTime = dataFrame.get("startDateTime").asText();
      try {
         ZonedDateTime zDateTime = DateTimeUtils.isoDateTime(startDateTime);
         startYear = zDateTime.getYear();
         startMinute = (int)ChronoUnit.MINUTES.between(DateTimeUtils.isoDateTime(startYear, 1, 1, 0, 0, 0, 0), zDateTime);
      } catch (Exception e) {
         logger.warn("Failed to startDateTime {}, defaulting to unknown value {}.", startDateTime, startMinute);
      }

      dataFrame.put("startYear", startYear);
      dataFrame.put("startTime", startMinute);
      dataFrame.remove("startDateTime");
   }

   public static void replaceContent(ObjectNode dataFrame) {

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
      // "items":["513", "Text you need to send", "'1234567'", "255"]},
      
      // step 1, figure out the name of the content
      String contentName = dataFrame.get("content").asText();
      String replacedContentName;
      if ("Work Zone".equalsIgnoreCase(contentName) || "workZone".equalsIgnoreCase(contentName)) {
         replacedContentName = "workZone";
      } else if ("Speed Limit".equalsIgnoreCase(contentName) || "speedLimit".equalsIgnoreCase(contentName)) {
         replacedContentName = "speedLimit";
      } else if ("Exit Service".equalsIgnoreCase(contentName) || "exitService".equalsIgnoreCase(contentName)) {
         replacedContentName = "exitService";
      } else if ("Generic Signage".equalsIgnoreCase(contentName) || "genericSign".equalsIgnoreCase(contentName)) {
         replacedContentName = "genericSign";
      } else {
         // default
         replacedContentName = "advisory";
      }
      dataFrame.remove("content");
//      updatedNode.set("frameType", replaceFrameType(updatedNode.get("frameType")));
      dataFrame.set("frameType", 
         JsonUtils.newNode().put(dataFrame.get("frameType").asText(), EMPTY_FIELD_FLAG));
      
      // step 2, reformat item list
      ArrayNode items = (ArrayNode) dataFrame.get("items");
      ArrayNode newItems = JsonUtils.newNode().arrayNode();
      if (items.isArray()) {
         // take the array of ITIScodesAndText items and transform it into
         // schema-appropriate array

         Iterator<JsonNode> itemsIter = items.elements();

         while (itemsIter.hasNext()) {
            JsonNode curItem = itemsIter.next();
            newItems.add(buildItem(curItem.asText()));
         }
      }

      // final step, transform into correct format
      JsonNode sequence = JsonUtils.newNode().set("SEQUENCE", newItems);

      // TODO the following field is called "content" but this results in a
      // failed conversion to XML
      // see @us.dot.its.jpo.ode.traveler.TimController.publish
      dataFrame.set("tcontent", JsonUtils.newNode().set(replacedContentName, sequence));
      dataFrame.remove("items");
   }

   private static JsonNode buildItem(String itemStr) {
      JsonNode item = null;
      // check to see if it is a itis code or text
      try {
         item = JsonUtils.newNode().set("item", JsonUtils.newNode().put("itis", Integer.valueOf(itemStr)));
         // it's a number, so create "itis" code
      } catch (NumberFormatException e) {
         // it's a number, so create "text"
         if (itemStr.startsWith("'")) {
            item = JsonUtils.newNode().set("item", JsonUtils.newNode().put("text", itemStr.substring(1)));
         } else {
            item = JsonUtils.newNode().set("item", JsonUtils.newNode().put("text", itemStr));
         }
      }

      return item;
   }

//   public static ObjectNode replaceFrameType(JsonNode oldFrameType) {
//
//      String frameType;
//      switch (oldFrameType.asInt()) {
//      case 1:
//         frameType = "advisory";
//         break;
//      case 2:
//         frameType = "roadSignage";
//         break;
//      case 3:
//         frameType = "commercialSignage";
//         break;
//      default:
//         frameType = "unknown";
//      }
//
//      return JsonUtils.newNode().put(frameType, EMPTY_FIELD_FLAG);
//   }

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

      JsonNode msgId = dataFrame.get("msgId");
      if (msgId != null) {
         ObjectNode roadSignID = (ObjectNode) msgId.get("roadSignID");
         if (roadSignID != null) {
            
            DsrcPosition3D position = Position3DBuilder.dsrcPosition3D(
                  Position3DBuilder.odePosition3D(roadSignID.get("position")));
            
            roadSignID.putPOJO("position", position);
            
            // mutcdCode is optional
            JsonNode mutcdNode = roadSignID.get("mutcdCode");
            if (mutcdNode != null) {
               roadSignID.set("mutcdCode", JsonUtils.newNode().put(mutcdNode.asText(), EMPTY_FIELD_FLAG));
            }

            // crc is optional
            JsonNode crcNode = roadSignID.get("crc");
            if (crcNode != null) {
               roadSignID.put("crc", String.format("%04X", crcNode.asInt()));
            }
         }
      }
   }

   public static JsonNode transformRegions(JsonNode regions) throws JsonUtilsException {
      ArrayNode replacedRegions = JsonUtils.newNode().arrayNode();

      if (regions.isArray()) {
         Iterator<JsonNode> regionsIter = regions.elements();

         while (regionsIter.hasNext()) {
            JsonNode curRegion = regionsIter.next();
            replaceRegion((ObjectNode) curRegion);
            replacedRegions.add(JsonUtils.newNode().set("GeographicalPath", curRegion));
         }
      } else {
         replacedRegions.add(JsonUtils.newNode().put("GeographicalPath", EMPTY_FIELD_FLAG));
      }

      return replacedRegions;
   }

   public static void replaceRegion(ObjectNode region) throws JsonUtilsException {

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

      // name does not need to be replaced

      // id optional, consists of segmentID (required)
      // and regulatorID (optional)
      JsonNode segmentID = region.get("segmentID");
      if (segmentID != null) {
         ObjectNode id = JsonUtils.newNode().put("id", segmentID.asInt());
         JsonNode regulatorID = region.get("regulatorID");
         if (regulatorID != null) {
            id.put("region", regulatorID.asInt());
         }
         region.set("id", id);
      }
      // replace regulatorID and segmentID with id
      ObjectNode id = JsonUtils.newNode()
            .put("region",region.get("regulatorID").asInt())
            .put("id", region.get("segmentID").asInt());
      
      region.set("id", id);
      region.remove("regulatorID");
      region.remove("segmentID");

      // anchorPosition --> anchor (optional)
      JsonNode anchorPos = region.get("anchorPosition");
      if (anchorPos != null) {
         region.set("anchor", JsonUtils.toObjectNode(Position3DBuilder.dsrcPosition3D(
            Position3DBuilder.odePosition3D(region.get("anchorPosition"))).toJson()));
         region.remove("anchorPosition");
      }

      // lane width (optional)
      JsonNode laneWidth = region.get("laneWidth");
      if (laneWidth != null) {
         region.put("laneWidth", LaneWidthBuilder.laneWidth(laneWidth.decimalValue()));
      }

      // directionality (optional)
      JsonNode directionality = region.get("directionality");
      if (directionality != null) {
         String directionName;
         switch (directionality.asInt()) {
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
         region.set("directionality", JsonUtils.newNode().put(directionName, EMPTY_FIELD_FLAG));
      }

      // closed path (optional)
      JsonNode closedPath = region.get("closedPath");
      if (closedPath != null) {
         region.put("closedPath", (closedPath.asBoolean() ? BOOLEAN_OBJECT_TRUE : BOOLEAN_OBJECT_FALSE));
      }

      // description (optional)
      JsonNode descriptionNode = region.get("description");
      if (descriptionNode != null) {
         String descriptionType = descriptionNode.asText();
         if ("path".equals(descriptionType)) {
            ObjectNode pathNode = (ObjectNode) region.get("path");
            replacePath(pathNode);
            region.remove("path");
            region.set("description", JsonUtils.newNode().set("path", pathNode));
         } else if ("geometry".equals(descriptionType)) {
            ObjectNode newGeometry = (ObjectNode) region.get("geometry");
            replaceGeometry(newGeometry);
            region.remove("geometry");
            region.set("description", JsonUtils.newNode().set("geometry", newGeometry));
         } else if ("oldRegion".equals(descriptionType)) {
            ObjectNode newOldRegion = (ObjectNode) region.get("oldRegion");
            replaceOldRegion(newOldRegion);
            region.remove("oldRegion");
            region.set("description", JsonUtils.newNode().set("oldRegion", newOldRegion));
         }
      }
   }

   private static void replacePath(ObjectNode pathNode) {

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

      // zoom does not need to be replaced
      String nodeType = pathNode.get("type").asText();
      if ("ll".equals(nodeType)) {
         JsonNode nodeList = JsonUtils.newNode().set("NodeLL", transformNodeListLL(pathNode.get("nodes")));
         pathNode.set("offset", JsonUtils.newNode().set("ll", JsonUtils.newNode().set("nodes", nodeList)));
         pathNode.remove("nodes");
      } else if ("xy".equals(nodeType)) {
         JsonNode nodeList = JsonUtils.newNode().set("NodeXY", replaceNodeListXY(pathNode.get("nodes")));
         pathNode.set("offset", JsonUtils.newNode().set("xy", JsonUtils.newNode().set("nodes", nodeList)));
         pathNode.remove("nodes");
      }

      pathNode.remove("type");

   }

   private static ArrayNode transformNodeListLL(JsonNode jsonNode) {

      // technically this can have more options in the future

      return transformNodeSetLL((ArrayNode) jsonNode);
   }

   private static ArrayNode transformNodeSetLL(ArrayNode inputNodeList) {

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
            outputNodeList.add(transformNodeLL(inputNode));
         }
      }

      return outputNodeList;
   }

   private static ObjectNode transformNodeLL(JsonNode oldNode) {

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
      Long transformedLat = null;
      Long transformedLong = null;

      ObjectNode innerNode = (ObjectNode) JsonUtils.newNode();
      ObjectNode deltaNode = (ObjectNode) JsonUtils.newNode().set("delta", innerNode);
      ObjectNode latLong = JsonUtils.newNode();
      String deltaText = delta.asText();
      if (deltaText.startsWith("node-LL")) {
         transformedLat = OffsetLLBuilder.offsetLL(latOffset);
         transformedLong = OffsetLLBuilder.offsetLL(longOffset);
         if (deltaText.equals("node-LL")) {
            deltaText = nodeOffsetPointLL(transformedLat, transformedLong);
         }
      } else if ("node-LatLon".equals(deltaText)) {
         transformedLat = LatitudeBuilder.j2735Latitude(latOffset);
         transformedLong = LongitudeBuilder.j2735Longitude(longOffset);
      }

      innerNode.set(deltaText, latLong);
      latLong.put("lat", transformedLat).put("lon", transformedLong);

      return deltaNode;
   }

//   -- Nodes with LL content Span at the equator when using a zoom of one:
//      node-LL1 Node-LL-24B, -- within +- 22.634554 meters of last node
//      node-LL2 Node-LL-28B, -- within +- 90.571389 meters of last node
//      node-LL3 Node-LL-32B, -- within +- 362.31873 meters of last node
//      node-LL4 Node-LL-36B, -- within +- 01.449308 Kmeters of last node
//      node-LL5 Node-LL-44B, -- within +- 23.189096 Kmeters of last node
//      node-LL6 Node-LL-48B, -- within +- 92.756481 Kmeters of last node
//      node-LatLon Node-LLmD-64b, -- node is a full 32b Lat/Lon range
   private static String nodeOffsetPointLL(long transformedLat, long transformedLon) {
      long transformed = Math.abs(transformedLat) | Math.abs(transformedLon);
      if ((transformed & (-1 << 12)) == 0) {
         // 12 bit value
         return "node-LL1";
      } else if ((transformed & (-1 << 14)) == 0) {
         // 14 bit value
         return "node-LL2";
      } else if ((transformed & (-1 << 16)) == 0) {
         // 16 bit value
         return "node-LL3";
      } else if ((transformed & (-1 << 18)) == 0) {
         // 18 bit value
         return "node-LL4";
      } else if ((transformed & (-1 << 22)) == 0) {
         // 22 bit value
         return "node-LL5";
      } else if ((transformed & (-1 << 24)) == 0) {
         // 24 bit value
         return "node-LL6";
      } else {
         throw new IllegalArgumentException("Invalid node lat/long offset: " + transformedLat + "/" + transformedLon
               + ". Values must be between a range of -0.8388608/+0.8388607 degrees.");
      }
   }

   public static void replaceGeometry(ObjectNode geometry) {

      // direction HeadingSlice
      // extent Extent OPTIONAL
      // laneWidth LaneWidth OPTIONAL
      // circle Circle

      // direction does not need to be replaced
      
      // extent does not need to be replaced (optional)

      // replace lane width
      JsonNode laneWidth = geometry.get("laneWidth");
      if (laneWidth != null) {
         geometry.put("laneWidth", LaneWidthBuilder.laneWidth(laneWidth.decimalValue()));
      }

      // replace circle
      replaceCircle(geometry.get("circle"));
   }

   public static void replaceOldRegion(ObjectNode oldRegion) {

      // old region == ValidRegion
      // elements:
      // direction - no changes
      // extent - no changes
      // area - needs changes

      oldRegion.set("area", replaceArea(oldRegion.get("area")));
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
         JsonUtils.addNode(updatedNode, "anchor", 
            Position3DBuilder.dsrcPosition3D(
               Position3DBuilder.odePosition3D(updatedNode.get("anchorPosition"))));
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
      JsonUtils.addNode(updatedNode, "center", 
         Position3DBuilder.dsrcPosition3D(
            Position3DBuilder.odePosition3D(updatedNode.get("position"))));
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
         JsonUtils.addNode(updatedNode, "anchor", 
            Position3DBuilder.dsrcPosition3D(
               Position3DBuilder.odePosition3D(updatedNode.get("anchorPosition"))));
         updatedNode.remove("anchorPosition");
      }

      // replace lane width
      if (updatedNode.get("laneWidth") != null) {
         updatedNode.put("laneWidth", LaneWidthBuilder.laneWidth(updatedNode.get("laneWidth").decimalValue()));
      }

      // directionality does not need replacement

      // replace node list
      updatedNode.set("nodeList", updatedNode.get("nodeList"));

      return updatedNode;
   }

   public static JsonNode replaceNodeListXY(JsonNode nodeList) {
      
      
      // nodeListXY contains either NodeSetXY or ComputedLane

      // technically this can have more options in the future

      return replaceNodeSetXY(nodeList);
//      if (nodeList.get("nodes") != null) {
//         return replaceNodeSetXY(nodeList);
//      } else if (nodeList.get("computed") != null) {
//         return replaceComputedLane(nodeList);
//      }
//      
//      return null;
   }

   public static ObjectNode replaceComputedLane(JsonNode jsonNode) {
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
               updatedNode.get("offsetSmallX").asInt());
         updatedNode.set("offsetXaxis", small);
         updatedNode.remove("offsetSmallX");
      }
      if (updatedNode.get("offsetLargeX") != null) {
         ObjectNode large = JsonUtils.newObjectNode("large", updatedNode.get("offsetLargeX").asInt());
         updatedNode.set("offsetXaxis", large);
         updatedNode.remove("offsetLargeX");
      }
      if (updatedNode.get("offsetSmallY") != null) {
         ObjectNode small = JsonUtils.newObjectNode("small", updatedNode.get("offsetSmallY").asInt());
         updatedNode.set("offsetYaxis", small);
         updatedNode.remove("offsetSmallY");
      }
      if (updatedNode.get("offsetLargeY") != null) {
         ObjectNode large = JsonUtils.newObjectNode("large", updatedNode.get("offsetLargeY").asInt());
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

   public static ArrayNode replaceNodeSetXY(JsonNode inputNodeList) {
      
      //// EXPECTED INPUT:
      // "nodes": []

      // EXPECTED OUTPUT:
      // <nodes>
      // .<NodeXY>
      // .</NodeXY>
      // .<NodeXY>
      // .</NodeXY>
      // </nodes>

      ArrayNode outputNodeList = JsonUtils.newNode().arrayNode();

      if (inputNodeList.isArray()) {
         Iterator<JsonNode> nodeListIter = inputNodeList.elements();

         while (nodeListIter.hasNext()) {
            JsonNode inputNode = nodeListIter.next();
            outputNodeList.add(replaceNodeXY(inputNode));
         }
      }

      return outputNodeList;
   }

   public static JsonNode replaceNodeXY(JsonNode oldNode) {

      // TODO
      // nodexy contains:
      // delta NodeOffsetPointXY
      // attributes NodeAttributeSetXY (optional)

      ObjectNode updatedNode = replaceNodeOffsetPointXY(oldNode);

      if (oldNode.get("attributes") != null) {
         replaceNodeAttributeSetXY(updatedNode);
      }
      
      return updatedNode;
   }

   private static void replaceNodeAttributeSetXY(JsonNode jsonNode) {
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
         updatedNode.put("dWidth", OffsetXyBuilder.offsetXy(updatedNode.get("dWidth").decimalValue()));
      }

      if (updatedNode.get("dElevation") != null) {
         updatedNode.put("dElevation", OffsetXyBuilder.offsetXy(updatedNode.get("dElevation").decimalValue()));
      }
   }

   private static void replaceLaneDataAttributeList(JsonNode laneDataAttributeList) {

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

   public static ObjectNode replaceNodeOffsetPointXY(JsonNode oldNode) {
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

      JsonNode delta = oldNode.get("delta");

      ObjectNode innerNode = (ObjectNode) JsonUtils.newNode();
      ObjectNode deltaNode = (ObjectNode) JsonUtils.newNode();
      String deltaText = delta.asText();
      if (deltaText.startsWith("node-XY")) {
         BigDecimal xOffset = oldNode.get("x").decimalValue();
         BigDecimal yOffset = oldNode.get("y").decimalValue();
         Long transformedX = OffsetXyBuilder.offsetXy(xOffset);
         Long transformedY = OffsetXyBuilder.offsetXy(yOffset);
         ObjectNode xy = JsonUtils.newNode().put("x", transformedX).put("y", transformedY);
         if (deltaText.equals("node-XY")) {
            innerNode.set(nodeOffsetPointXY(transformedX, transformedY), xy);
         } else {
            innerNode.set(deltaText, xy);
         }
      } else if ("node-LatLon".equals(deltaText)) {
         BigDecimal lonOffset = oldNode.get("nodeLong").decimalValue();
         BigDecimal latOffset = oldNode.get("nodeLat").decimalValue();
         Long transformedLon = LatitudeBuilder.j2735Latitude(lonOffset);
         Long transformedLat = LongitudeBuilder.j2735Longitude(latOffset);
         ObjectNode latLong = JsonUtils.newNode().put("lon", transformedLon).put("lat", transformedLat);
         innerNode.set(deltaText, latLong);
      }

      deltaNode.set("delta", innerNode);

      return deltaNode;

   }

   // NodeOffsetPointXY contains one of:
   // node-XY1 Node-XY-20b, -- node is within 5.11m of last node
   // node-XY2 Node-XY-22b, -- node is within 10.23m of last node
   // node-XY3 Node-XY-24b, -- node is within 20.47m of last node
   // node-XY4 Node-XY-26b, -- node is within 40.96m of last node
   // node-XY5 Node-XY-28b, -- node is within 81.91m of last node
   // node-XY6 Node-XY-32b, -- node is within 327.67m of last node
   // node-LatLon Node-LLmD-64b, -- node is a full 32b Lat/Lon range
   private static String nodeOffsetPointXY(long transformedX, long transformedY) {
      long transformed = Math.abs(transformedX) | Math.abs(transformedY);
      if ((transformed & (-1 << 10)) == 0) {
         return "node-XY1";
      } else if ((transformed & (-1 << 11)) == 0) {
         return "node-XY2";
      } else if ((transformed & (-1 << 12)) == 0) {
         return "node-XY3";
      } else if ((transformed & (-1 << 13)) == 0) {
         return "node-XY4";
      } else if ((transformed & (-1 << 14)) == 0) {
         return "node-XY5";
      } else if ((transformed & (-1 << 16)) == 0) {
         return "node-XY6";
      } else {
         throw new IllegalArgumentException("Invalid node X/Y offset: " + transformedX + "/" + transformedY
               + ". Values must be between a range of -327.68/+327.67 meters.");
      }
   }

}
