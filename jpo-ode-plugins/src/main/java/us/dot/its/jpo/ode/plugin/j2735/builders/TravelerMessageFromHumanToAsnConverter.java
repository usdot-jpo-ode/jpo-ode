/*******************************************************************************
 * Copyright 2018 572682
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
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

   private static final String NODE_LAT = "nodeLat";

  private static final String NODE_LONG = "nodeLong";

  private static final String Y = "y";

  private static final String X = "x";

  private static final String NODE_LAT_LON = "node-LatLon";

  private static final String LON = "lon";

  private static final String LAT = "lat";

  private static final String NODE_XY = "node-XY";

  private static final String SPEED_LIMITS = "speedLimits";

  private static final String LANE_ANGLE = "laneAngle";

  private static final String LANE_CROWN_POINT_RIGHT = "laneCrownPointRight";

  private static final String LANE_CROWN_POINT_LEFT = "laneCrownPointLeft";

  private static final String LANE_CROWN_POINT_CENTER = "laneCrownPointCenter";

  private static final String D_ELEVATION = "dElevation";

  private static final String D_WIDTH = "dWidth";

  private static final String DATA = "data";

  private static final String Y_SCALE = "yScale";

  private static final String X_SCALE = "xScale";

  private static final String ANGLE = "angle";

  private static final String OFFSET_LARGE_Y = "offsetLargeY";

  private static final String OFFSET_SMALL_Y = "offsetSmallY";

  private static final String OFFSET_LARGE_X = "offsetLargeX";

  private static final String OFFSET_SMALL_X = "offsetSmallX";

  private static final String NODE_LIST = "nodeList";

  private static final String REGION_POINT_SET = "regionPointSet";

  private static final String CIRCLE = "circle";

  private static final String SHAPE_POINT_SET = "shapePointSet";

  private static final String DELTA = "delta";

  private static final String OFFSET = "offset";

  private static final String NODES = "nodes";

  private static final String XY = "xy";

  private static final String LL = "ll";

  private static final String OLD_REGION = "oldRegion";

  private static final String GEOMETRY = "geometry";

  private static final String PATH = "path";

  private static final String DESCRIPTION = "description";

  private static final String CLOSED_PATH = "closedPath";

  private static final String ANCHOR = "anchor";

  private static final String ID = "id";

  private static final String REGION = "region";

  private static final String LANE_WIDTH = "laneWidth";

  private static final String ANCHOR_POSITION = "anchorPosition";

  private static final String REGULATOR_ID = "regulatorID";

  private static final String SEGMENT_ID = "segmentID";

  private static final String POSITION = "position";

  private static final String TEXT = "text";

  private static final String ITIS = "itis";

  private static final String ITEM = "item";

  private static final String START_DATE_TIME = "startDateTime";

  private static final String DURATION_TIME = "durationTime";

    // I know, it's misspelled and it has to stay that way. J2735 spec misspelled it
    private static final String DURATON_TIME_MISSPELLED = "duratonTime";
  
    private static final String SSP_TIM_RIGHTS = "sspTimRights";
  
    private static final String SSP_MSG_TYPES = "sspMsgTypes";
  
    private static final String SSP_MSG_CONTENT = "sspMsgContent";
  
    private static final String DATAFRAMES = "dataframes";
  
    private static final String TIME_STAMP = "timeStamp";
  
    public static final String GEOGRAPHICAL_PATH_STRING = "GeographicalPath";

   public static final String REGIONS_STRING = "regions";

   public static final String TRAVELER_DATA_FRAME_STRING = "TravelerDataFrame";

   public static final String DATA_FRAMES_STRING = "dataFrames";

   public static final String SEQUENCE_STRING = "SEQUENCE";
   public static final String TCONTENT_STRING = "tcontent";

   private static final Logger logger = LoggerFactory.getLogger(TravelerMessageFromHumanToAsnConverter.class);

   // JSON cannot have empty fields like XML, so the XML must be modified by
   // removing all flag field values
   public static final String EMPTY_FIELD_FLAG = "EMPTY_TAG";
   public static final String BOOLEAN_OBJECT_TRUE = "BOOLEAN_OBJECT_TRUE";
   public static final String BOOLEAN_OBJECT_FALSE = "BOOLEAN_OBJECT_FALSE";

   
  private TravelerMessageFromHumanToAsnConverter() {
      super();
  }

  public static void convertTravelerInputDataToEncodableTim(JsonNode tid) throws JsonUtilsException {
      // msgCnt MsgCount,
      // timeStamp MinuteOfTheYear OPTIONAL
      // packetID UniqueMSGID OPTIONAL
      // urlB URL-Base OPTIONAL
      // dataFrames TravelerDataFrameList

      // Cast to ObjectNode to allow manipulation in place
      ObjectNode timDataObjectNode = (ObjectNode) tid.get("tim");
      
      // timeStamp is optional
      if (timDataObjectNode.get(TIME_STAMP) != null) {
         timDataObjectNode.put(TIME_STAMP,
               translateISOTimeStampToMinuteOfYear(timDataObjectNode.get(TIME_STAMP).asText()));
      }

      // urlB is optional but does not need replacement

      // dataFrames are required
      timDataObjectNode.set(DATA_FRAMES_STRING, transformDataFrames(timDataObjectNode.get(DATAFRAMES)));
      timDataObjectNode.remove(DATAFRAMES);
   }
      
   public static ObjectNode transformDataFrames(JsonNode dataFrames) throws JsonUtilsException {

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
            replacedDataFrames.add(oldFrame);
         }
      }

      return JsonUtils.newObjectNode(TRAVELER_DATA_FRAME_STRING, replacedDataFrames);
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
      dataFrame.put("sspMsgRights2", dataFrame.get(SSP_MSG_CONTENT).asInt());
      dataFrame.remove(SSP_MSG_CONTENT);

      // replace sspMsgTypes with sspMsgRights1
      dataFrame.put("sspMsgRights1", dataFrame.get(SSP_MSG_TYPES).asInt());
      dataFrame.remove(SSP_MSG_TYPES);

      dataFrame.put(SSP_TIM_RIGHTS, dataFrame.get(SSP_TIM_RIGHTS).asText());

      // priority does not need replacement

      // replace durationTime with duratonTime - j2735 schema misspelling
      dataFrame.put(DURATON_TIME_MISSPELLED, dataFrame.get(DURATION_TIME).asInt());
      dataFrame.remove(DURATION_TIME);

      // url does not need replacement

      replaceDataFrameTimestamp(dataFrame);

      // replace the geographical path regions
      dataFrame.set(REGIONS_STRING, transformRegions(dataFrame.get(REGIONS_STRING)));
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
      String startDateTime = dataFrame.get(START_DATE_TIME).asText();
      try {
         ZonedDateTime zDateTime = DateTimeUtils.isoDateTime(startDateTime);
         startYear = zDateTime.getYear();
         startMinute = (int)ChronoUnit.MINUTES.between(DateTimeUtils.isoDateTime(startYear, 1, 1, 0, 0, 0, 0), zDateTime);
      } catch (Exception e) {
         logger.warn("Failed to startDateTime {}, defaulting to unknown value {}.", startDateTime, startMinute);
      }

      dataFrame.put("startYear", startYear);
      dataFrame.put("startTime", startMinute);
      dataFrame.remove(START_DATE_TIME);
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

      JsonNode sequence = JsonUtils.newNode().set(SEQUENCE_STRING, newItems);

      // The following field is called "content" but this results in a
      // failed conversion to XML
      // see @us.dot.its.jpo.ode.traveler.TimController.publish
      dataFrame.set(TCONTENT_STRING, JsonUtils.newNode().set(replacedContentName, sequence));
      dataFrame.remove("items");
   }

   private static JsonNode buildItem(String itemStr) {
      JsonNode item = null;
      // check to see if it is a itis code or text
      try {
         item = JsonUtils.newNode().set(ITEM, JsonUtils.newNode().put(ITIS, Integer.valueOf(itemStr)));
         // it's a number, so create "itis" code
      } catch (NumberFormatException e) {
         // it's a number, so create "text"
         if (itemStr.startsWith("'")) {
            item = JsonUtils.newNode().set(ITEM, JsonUtils.newNode().put(TEXT, itemStr.substring(1)));
         } else {
            item = JsonUtils.newNode().set(ITEM, JsonUtils.newNode().put(TEXT, itemStr));
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
                  Position3DBuilder.odePosition3D(roadSignID.get(POSITION)));
            
            roadSignID.putPOJO(POSITION, position);
            
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

   public static ObjectNode transformRegions(JsonNode regions) throws JsonUtilsException {
      ArrayNode replacedRegions = JsonUtils.newNode().arrayNode();

      if (regions.isArray()) {
         Iterator<JsonNode> regionsIter = regions.elements();

         while (regionsIter.hasNext()) {
            JsonNode curRegion = regionsIter.next();
            replaceRegion((ObjectNode) curRegion);
            replacedRegions.add(curRegion);
         }
      }

      return JsonUtils.newObjectNode(GEOGRAPHICAL_PATH_STRING, replacedRegions);
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
      JsonNode segmentID = region.get(SEGMENT_ID);
      if (segmentID != null) {
         ObjectNode id = JsonUtils.newNode().put(ID, segmentID.asInt());
         JsonNode regulatorID = region.get(REGULATOR_ID);
         if (regulatorID != null) {
            id.put(REGION, regulatorID.asInt());
         }
         region.set(ID, id);
      }
      // replace regulatorID and segmentID with id
      ObjectNode id = JsonUtils.newNode()
            .put(REGION,region.get(REGULATOR_ID).asInt())
            .put(ID, region.get(SEGMENT_ID).asInt());
      
      region.set(ID, id);
      region.remove(REGULATOR_ID);
      region.remove(SEGMENT_ID);

      // anchorPosition --> anchor (optional)
      JsonNode anchorPos = region.get(ANCHOR_POSITION);
      if (anchorPos != null) {
         region.set(ANCHOR, JsonUtils.toObjectNode(Position3DBuilder.dsrcPosition3D(
            Position3DBuilder.odePosition3D(region.get(ANCHOR_POSITION))).toJson()));
         region.remove(ANCHOR_POSITION);
      }

      // lane width (optional)
      JsonNode laneWidth = region.get(LANE_WIDTH);
      if (laneWidth != null) {
         region.put(LANE_WIDTH, LaneWidthBuilder.laneWidth(laneWidth.decimalValue()));
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
      JsonNode closedPath = region.get(CLOSED_PATH);
      if (closedPath != null) {
         region.put(CLOSED_PATH, (closedPath.asBoolean() ? BOOLEAN_OBJECT_TRUE : BOOLEAN_OBJECT_FALSE));
      }

      // description (optional)
      JsonNode descriptionNode = region.get(DESCRIPTION);
      if (descriptionNode != null) {
         String descriptionType = descriptionNode.asText();
         if (PATH.equals(descriptionType)) {
            ObjectNode pathNode = (ObjectNode) region.get(PATH);
            replacePath(pathNode);
            region.remove(PATH);
            region.set(DESCRIPTION, JsonUtils.newNode().set(PATH, pathNode));
         } else if (GEOMETRY.equals(descriptionType)) {
            ObjectNode newGeometry = (ObjectNode) region.get(GEOMETRY);
            replaceGeometry(newGeometry);
            region.remove(GEOMETRY);
            region.set(DESCRIPTION, JsonUtils.newNode().set(GEOMETRY, newGeometry));
         } else if (OLD_REGION.equals(descriptionType)) {
            ObjectNode newOldRegion = (ObjectNode) region.get(OLD_REGION);
            replaceOldRegion(newOldRegion);
            region.remove(OLD_REGION);
            region.set(DESCRIPTION, JsonUtils.newNode().set(OLD_REGION, newOldRegion));
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
      if (LL.equals(nodeType)) {
         JsonNode nodeList = JsonUtils.newNode().set("NodeLL", transformNodeListLL(pathNode.get(NODES)));
         pathNode.set(OFFSET, JsonUtils.newNode().set(LL, JsonUtils.newNode().set(NODES, nodeList)));
         pathNode.remove(NODES);
      } else if (XY.equals(nodeType)) {
         JsonNode nodeList = JsonUtils.newNode().set("NodeXY", replaceNodeListXY(pathNode.get(NODES)));
         pathNode.set(OFFSET, JsonUtils.newNode().set(XY, JsonUtils.newNode().set(NODES, nodeList)));
         pathNode.remove(NODES);
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

      BigDecimal latOffset = oldNode.get(NODE_LAT).decimalValue();
      BigDecimal longOffset = oldNode.get(NODE_LONG).decimalValue();
      JsonNode delta = oldNode.get(DELTA);
      Long transformedLat = null;
      Long transformedLong = null;

      ObjectNode innerNode = JsonUtils.newNode();
      ObjectNode deltaNode = (ObjectNode) JsonUtils.newNode().set(DELTA, innerNode);
      ObjectNode latLong = JsonUtils.newNode();
      String deltaText = delta.asText();
      if (deltaText.startsWith("node-LL")) {
         transformedLat = OffsetLLBuilder.offsetLL(latOffset);
         transformedLong = OffsetLLBuilder.offsetLL(longOffset);
         if (deltaText.equals("node-LL")) {
            deltaText = nodeOffsetPointLL(transformedLat, transformedLong);
         }
      } else if (NODE_LAT_LON.equals(deltaText)) {
         transformedLat = LatitudeBuilder.j2735Latitude(latOffset);
         transformedLong = LongitudeBuilder.j2735Longitude(longOffset);
      }

      innerNode.set(deltaText, latLong);
      latLong.put(LAT, transformedLat).put(LON, transformedLong);

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
      JsonNode laneWidth = geometry.get(LANE_WIDTH);
      if (laneWidth != null) {
         geometry.put(LANE_WIDTH, LaneWidthBuilder.laneWidth(laneWidth.decimalValue()));
      }

      // replace circle
      replaceCircle(geometry.get(CIRCLE));
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

      if (updatedNode.get(SHAPE_POINT_SET) != null) {
         updatedNode.set(SHAPE_POINT_SET, replaceShapePointSet(updatedNode.get(SHAPE_POINT_SET)));

      } else if (updatedNode.get(CIRCLE) != null) {
         updatedNode.set(CIRCLE, replaceCircle(updatedNode.get(CIRCLE)));

      } else if (updatedNode.get(REGION_POINT_SET) != null) {
         updatedNode.set(REGION_POINT_SET, replaceRegionPointSet(updatedNode.get(REGION_POINT_SET)));
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
      if (updatedNode.get(ANCHOR_POSITION) != null) {
         JsonUtils.addNode(updatedNode, ANCHOR, 
            Position3DBuilder.dsrcPosition3D(
               Position3DBuilder.odePosition3D(updatedNode.get(ANCHOR_POSITION))));
         updatedNode.remove(ANCHOR_POSITION);
      }

      // zoom doesn't need replacement (also optional)

      // regionList is good as is and does not need replacement (required)

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
            Position3DBuilder.odePosition3D(updatedNode.get(POSITION))));
      updatedNode.remove(POSITION);

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
      if (updatedNode.get(ANCHOR) != null) {
         JsonUtils.addNode(updatedNode, ANCHOR, 
            Position3DBuilder.dsrcPosition3D(
               Position3DBuilder.odePosition3D(updatedNode.get(ANCHOR_POSITION))));
         updatedNode.remove(ANCHOR_POSITION);
      }

      // replace lane width
      if (updatedNode.get(LANE_WIDTH) != null) {
         updatedNode.put(LANE_WIDTH, LaneWidthBuilder.laneWidth(updatedNode.get(LANE_WIDTH).decimalValue()));
      }

      // directionality does not need replacement

      // replace node list
      updatedNode.set(NODE_LIST, updatedNode.get(NODE_LIST));

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

      // lane id does not need replacement

      // detect and remove the nodes
      ObjectNode updatedNode = (ObjectNode) jsonNode;

      if (updatedNode.get(OFFSET_SMALL_X) != null) {
         ObjectNode small = JsonUtils.newObjectNode("small",
               updatedNode.get(OFFSET_SMALL_X).asInt());
         updatedNode.set("offsetXaxis", small);
         updatedNode.remove(OFFSET_SMALL_X);
      }
      if (updatedNode.get(OFFSET_LARGE_X) != null) {
         ObjectNode large = JsonUtils.newObjectNode("large", updatedNode.get(OFFSET_LARGE_X).asInt());
         updatedNode.set("offsetXaxis", large);
         updatedNode.remove(OFFSET_LARGE_X);
      }
      if (updatedNode.get(OFFSET_SMALL_Y) != null) {
         ObjectNode small = JsonUtils.newObjectNode("small", updatedNode.get(OFFSET_SMALL_Y).asInt());
         updatedNode.set("offsetYaxis", small);
         updatedNode.remove(OFFSET_SMALL_Y);
      }
      if (updatedNode.get(OFFSET_LARGE_Y) != null) {
         ObjectNode large = JsonUtils.newObjectNode("large", updatedNode.get(OFFSET_LARGE_Y).asInt());
         updatedNode.set("offsetYaxis", large);
         updatedNode.remove(OFFSET_LARGE_Y);
      }
      if (updatedNode.get(ANGLE) != null) {
         updatedNode.put("rotateXY", AngleBuilder.angle(updatedNode.get(ANGLE).decimalValue()));
         updatedNode.remove(ANGLE);
      }
      if (updatedNode.get(X_SCALE) != null) {
         updatedNode.put("scaleXaxis", ScaleB12Builder.scaleB12(updatedNode.get(X_SCALE).decimalValue()));
         updatedNode.remove(X_SCALE);
      }
      if (updatedNode.get(Y_SCALE) != null) {
         updatedNode.put("scaleYaxis", ScaleB12Builder.scaleB12(updatedNode.get(Y_SCALE).decimalValue()));
         updatedNode.remove(Y_SCALE);
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

      if (updatedNode.get(DATA) != null) {
         replaceLaneDataAttributeList(updatedNode.get(DATA));
      }
      if (updatedNode.get(D_WIDTH) != null) {
         updatedNode.put(D_WIDTH, OffsetXyBuilder.offsetXy(updatedNode.get(D_WIDTH).decimalValue()));
      }

      if (updatedNode.get(D_ELEVATION) != null) {
         updatedNode.put(D_ELEVATION, OffsetXyBuilder.offsetXy(updatedNode.get(D_ELEVATION).decimalValue()));
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
      } else if (updatedNode.get(LANE_CROWN_POINT_CENTER) != null) {
         updatedNode.put(LANE_CROWN_POINT_CENTER,
               RoadwayCrownAngleBuilder.roadwayCrownAngle(updatedNode.get(LANE_CROWN_POINT_CENTER).decimalValue()));
      } else if (updatedNode.get(LANE_CROWN_POINT_LEFT) != null) {
         updatedNode.put(LANE_CROWN_POINT_LEFT,
               RoadwayCrownAngleBuilder.roadwayCrownAngle(updatedNode.get(LANE_CROWN_POINT_LEFT).decimalValue()));
      } else if (updatedNode.get(LANE_CROWN_POINT_RIGHT) != null) {
         updatedNode.put(LANE_CROWN_POINT_RIGHT,
               RoadwayCrownAngleBuilder.roadwayCrownAngle(updatedNode.get(LANE_CROWN_POINT_RIGHT).decimalValue()));
      } else if (updatedNode.get(LANE_ANGLE) != null) {
         updatedNode.put(LANE_ANGLE,
               MergeDivergeNodeAngleBuilder.mergeDivergeNodeAngle(updatedNode.get(LANE_ANGLE).decimalValue()));
      } else if (updatedNode.get(SPEED_LIMITS) != null) {
         updatedNode.set(SPEED_LIMITS, replaceSpeedLimitList(updatedNode.get(SPEED_LIMITS)));
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

      JsonNode delta = oldNode.get(DELTA);

      ObjectNode innerNode = JsonUtils.newNode();
      ObjectNode deltaNode = JsonUtils.newNode();
      String deltaText = delta.asText();
      if (deltaText.startsWith(NODE_XY)) {
         BigDecimal xOffset = oldNode.get(X).decimalValue();
         BigDecimal yOffset = oldNode.get(Y).decimalValue();
         Long transformedX = OffsetXyBuilder.offsetXy(xOffset);
         Long transformedY = OffsetXyBuilder.offsetXy(yOffset);
         ObjectNode xy = JsonUtils.newNode().put(X, transformedX).put(Y, transformedY);
         if (deltaText.equals(NODE_XY)) {
            innerNode.set(nodeOffsetPointXY(transformedX, transformedY), xy);
         } else {
            innerNode.set(deltaText, xy);
         }
      } else if (NODE_LAT_LON.equals(deltaText)) {
         BigDecimal lonOffset = oldNode.get(NODE_LONG).decimalValue();
         BigDecimal latOffset = oldNode.get(NODE_LAT).decimalValue();
         Long transformedLon = LatitudeBuilder.j2735Latitude(lonOffset);
         Long transformedLat = LongitudeBuilder.j2735Longitude(latOffset);
         ObjectNode latLong = JsonUtils.newNode().put(LON, transformedLon).put(LAT, transformedLat);
         innerNode.set(deltaText, latLong);
      }

      deltaNode.set(DELTA, innerNode);

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
