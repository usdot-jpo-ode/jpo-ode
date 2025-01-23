/*******************************************************************************
 * Copyright 2018 572682.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at</p>
 *
 *   <p>http://www.apache.org/licenses/LICENSE-2.0</p>
 *
 * <p>Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.</p>
 ******************************************************************************/

package us.dot.its.jpo.ode.plugin.j2735.builders;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.dot.its.jpo.ode.plugin.j2735.DsrcPosition3D;
import us.dot.its.jpo.ode.plugin.j2735.timstorage.DirectionOfUse.DirectionOfUseEnum;
import us.dot.its.jpo.ode.plugin.j2735.timstorage.DistanceUnits.DistanceUnitsEnum;
import us.dot.its.jpo.ode.plugin.j2735.timstorage.Extent;
import us.dot.its.jpo.ode.plugin.j2735.timstorage.SpeedLimitType.SpeedLimitTypeEnum;
import us.dot.its.jpo.ode.util.CommonUtils;
import us.dot.its.jpo.ode.util.DateTimeUtils;
import us.dot.its.jpo.ode.util.JsonUtils;
import us.dot.its.jpo.ode.util.JsonUtils.JsonUtilsException;

/**
 * This class is used to convert a JsonNode representing an OdeTravelerInputData object to a format
 * that can be encoded by the asn1_codec submodule.
 */
public class TravelerMessageFromHumanToAsnConverter {

  private static final String SPEED = "speed";
  private static final String TYPE = "type";
  private static final String ATTRIBUTES = "attributes";
  public static final String TRAVELER_INFORMATION = "TravelerInformation";
  private static final String EXTENT = "extent";
  private static final String UNITS = "units";
  private static final String DIRECTIONALITY = "directionality";
  private static final String CENTER = "center";
  private static final String NODE_LAT = "nodeLat";
  private static final String NODE_LONG = "nodeLong";
  private static final String Y = "y";
  private static final String X = "x";
  private static final String NODE_LAT_LON = "node-LatLon";
  private static final String LON = "lon";
  private static final String LAT = "lat";
  private static final String NODE_XY = "node-XY";
  private static final String NODE_XY2 = "NodeXY";
  private static final String COMPUTED = "computed";
  private static final String SPEED_LIMITS = "speedLimits";
  private static final String LANE_ANGLE = "laneAngle";
  private static final String LANE_CROWN_POINT_RIGHT = "laneCrownPointRight";
  private static final String LANE_CROWN_POINT_LEFT = "laneCrownPointLeft";
  private static final String LANE_CROWN_POINT_CENTER = "laneCrownPointCenter";
  private static final String D_ELEVATION = "dElevation";
  private static final String D_WIDTH = "dWidth";
  private static final String DATA = "data";
  private static final String OFFSET_X_AXIS = "offsetXaxis";
  private static final String OFFSET_Y_AXIS = "offsetYaxis";
  private static final String ROTATE_XY = "rotateXY";
  private static final String SCALE_X_AXIS = "scaleXaxis";
  private static final String SCALE_Y_AXIS = "scaleYaxis";
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
  private static final String DURATON_TIME_MISSPELLED = "duratonTime"; // J2735 2016 Misspelling
  private static final String SSP_TIM_RIGHTS = "sspTimRights"; // used in J2735 2016
  private static final String NOT_USED = "notUsed"; // used in J2735 2020
  private static final String SSP_LOCATION_RIGHTS = "sspLocationRights"; // used in J2735 2016
  private static final String NOT_USED_1 = "notUsed1"; // used in J2735 2020
  private static final String SSP_MSG_TYPES = "sspMsgTypes"; // used previously
  private static final String SSP_MSG_RIGHTS_1 = "sspMsgRights1"; // used in J2735 2016
  private static final String NOT_USED_2 = "notUsed2"; // used in J2735 2020
  private static final String SSP_MSG_CONTENT = "sspMsgContent"; // used previously
  private static final String SSP_MSG_RIGHTS_2 = "sspMsgRights2"; // used in J2735 2016
  private static final String NOT_USED_3 = "notUsed3"; // used in J2735 2020
  private static final String DATAFRAMES = "dataframes";
  private static final String TIME_STAMP = "timeStamp";
  public static final String GEOGRAPHICAL_PATH_STRING = "GeographicalPath";
  public static final String REGIONS_STRING = "regions";
  public static final String TRAVELER_DATA_FRAME_STRING = "TravelerDataFrame";
  public static final String DATA_FRAMES_STRING = "dataFrames";
  public static final String SEQUENCE_STRING = "SEQUENCE";
  public static final String TCONTENT_STRING = "tcontent";
  // JSON cannot have empty fields like XML, so the XML must be modified by
  // removing all flag field values
  public static final String EMPTY_FIELD_FLAG = "EMPTY_TAG";
  public static final String BOOLEAN_OBJECT_TRUE = "BOOLEAN_OBJECT_TRUE";
  public static final String BOOLEAN_OBJECT_FALSE = "BOOLEAN_OBJECT_FALSE";

  private static final Logger logger =
      LoggerFactory.getLogger(TravelerMessageFromHumanToAsnConverter.class);

  private TravelerMessageFromHumanToAsnConverter() {
    super();
  }

  /**
   * Converts a JsonNode representing an OdeTravelerInputData object to a format
   * that can be encoded by the asn1_codec submodule.
   *
   * @param tid TravelerInputData object serialized as a JsonNode
   * @throws JsonUtilsException       if there is an issue converting the JsonNode
   * @throws IllegalArgumentException if the JsonNode contains old fields that are no longer used
   */
  public static void convertTravelerInputDataToEncodableTim(JsonNode tid)
      throws JsonUtilsException, NoncompliantFieldsException {
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
    timDataObjectNode.set(DATA_FRAMES_STRING,
        transformDataFrames(timDataObjectNode.get(DATAFRAMES)));
    timDataObjectNode.remove(DATAFRAMES);
  }

  /**
   * Transforms the dataFrames field.
   *
   * @param dataFrames JsonNode representing the dataFrames field
   * @return ObjectNode representing the transformed dataFrames field
   * @throws JsonUtilsException          if there is an issue converting the JsonNode
   * @throws NoncompliantFieldsException if the JsonNode contains old fields that are no longer used
   */
  public static ObjectNode transformDataFrames(JsonNode dataFrames)
      throws JsonUtilsException, NoncompliantFieldsException {

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

  /**
   * Replaces a data frame.
   *
   * @param dataFrame ObjectNode representing the data frame
   * @throws JsonUtilsException          if there is an issue converting the JsonNode
   * @throws NoncompliantFieldsException if the JsonNode contains old fields that are no longer used
   */
  public static void replaceDataFrame(ObjectNode dataFrame)
      throws JsonUtilsException, NoncompliantFieldsException {

    // INPUT
    //////
    // "dataframes": [
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
    // "content": "advisory",
    // "items": [
    // "513"
    // ],
    // "url": "null"
    // ]

    /// OUTPUT:
    //////
    // <dataFrames>
    // <TravelerDataFrame>
    // <startYear>2017</startYear>
    // <startTime>308065</startTime>
    // </TravelerDataFrame>
    // </dataFrames>

    // set frameType value
    dataFrame.set("frameType",
        JsonUtils.newNode().put(dataFrame.get("frameType").asText(), EMPTY_FIELD_FLAG));

    ensureComplianceWithJ2735Revision2024(dataFrame);

    // priority does not need replacement

    // url does not need replacement

    replaceDataFrameTimestamp(dataFrame);

    // replace the geographical path regions
    dataFrame.set(REGIONS_STRING, transformRegions(dataFrame.get(REGIONS_STRING)));
    // replace content
    replaceContent(dataFrame);

    // replace the msgID and relevant fields
    replaceMsgId(dataFrame);
  }

  /**
   * Translates ISO timestamp to minute of year.
   *
   * @param isoTime ISO timestamp
   * @return minute of year
   */
  public static long translateISOTimeStampToMinuteOfYear(String isoTime) {
    int startYear = 0;
    int startMinute = 527040;
    try {
      ZonedDateTime zonedDateTime = DateTimeUtils.isoDateTime(isoTime);
      startYear = zonedDateTime.getYear();
      startMinute =
          (int) Duration.between(DateTimeUtils.isoDateTime(startYear, 1, 1, 0,
                  0, 0, 0), zonedDateTime)
              .toMinutes();
    } catch (Exception e) { // NOSONAR
      logger.error("Failed to parse datetime {}, defaulting to unknown value {}", isoTime,
          startMinute);
    }

    return startMinute;
  }

  /**
   * Replaces the data frame timestamp.
   *
   * @param dataFrame ObjectNode representing the data frame
   */
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
      ZonedDateTime zonedDateTime = DateTimeUtils.isoDateTime(startDateTime);
      startYear = zonedDateTime.getYear();
      startMinute =
          (int) ChronoUnit.MINUTES.between(DateTimeUtils.isoDateTime(startYear, 1, 1, 0, 0, 0, 0),
              zonedDateTime);
    } catch (Exception e) {
      logger.error("Failed to startDateTime {}, defaulting to unknown value {}.", startDateTime,
          startMinute);
    }

    dataFrame.put("startYear", startYear);
    dataFrame.put("startTime", startMinute);
    dataFrame.remove(START_DATE_TIME);
  }

  /**
   * Replaces content.
   *
   * @param dataFrame ObjectNode representing the data frame
   */
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
    // "content": "advisory",
    // "items":["513", "Text you need to send", "'1234567'", "255"]},

    // step 1, reformat item list
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

    dataFrame.remove("items");

    // step 2, set the content CHOICE
    String replacedContentName = dataFrame.get("content").asText();
    if (replacedContentName.equals("Advisory")) {
      replacedContentName = "advisory";
    }

    // The following field is called "content" but this results in a
    // failed conversion to XML
    // see @us.dot.its.jpo.ode.traveler.TimController.publish
    dataFrame.set(TCONTENT_STRING, JsonUtils.newNode().set(replacedContentName, sequence));
    dataFrame.remove("content");
  }

  /**
   * Builds an item.
   *
   * @param itemStr String representing the item
   * @return JsonNode representing the item
   */
  public static JsonNode buildItem(String itemStr) {
    JsonNode item = null;
    // check to see if it is an itis code or text
    try {
      item = JsonUtils.newNode().set(ITEM, JsonUtils.newNode().put(ITIS, Integer.valueOf(itemStr)));
      // it's a number, so create "itis" code
    } catch (NumberFormatException e) {
      // it's not a number, so create "text"
      if (itemStr.startsWith("'")) {
        item = JsonUtils.newNode().set(ITEM, JsonUtils.newNode().put(TEXT, itemStr.substring(1)));
      } else {
        item = JsonUtils.newNode().set(ITEM, JsonUtils.newNode().put(TEXT, itemStr));
      }
    }

    return item;
  }

  /**
   * Replaces msg id.
   *
   * @param dataFrame ObjectNode representing the data frame
   */
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

        DsrcPosition3D position = Position3DBuilder
            .dsrcPosition3D(Position3DBuilder.odePosition3D(roadSignID.get(POSITION)));

        roadSignID.putPOJO(POSITION, position);

        // mutcdCode is optional
        JsonNode mutcdNode = roadSignID.get("mutcdCode");
        if (mutcdNode != null) {
          roadSignID.set("mutcdCode",
              JsonUtils.newNode().put(mutcdNode.asText(), EMPTY_FIELD_FLAG));
        }
      }
    }
  }

  /**
   * Transforms regions.
   *
   * @param regions JsonNode representing the regions
   * @return ObjectNode representing the transformed regions
   * @throws JsonUtilsException if there is an issue converting the JsonNode
   */
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

  /**
   * Replaces a region.
   *
   * @param region ObjectNode representing the region
   * @throws JsonUtilsException if there is an issue converting the JsonNode
   */
  public static void replaceRegion(ObjectNode region) throws JsonUtilsException {

    //// EXPECTED INPUT:
    // "name": "Testing TIM",
    // "regulatorID": "0",
    // "segmentID": "33",
    // "anchorPosition":
    // "latitude": "41.2500807",
    // "longitude": "-111.0093847",
    // "elevation": "2020.6969900289998"
    // ,
    // "laneWidth": "7",
    // "directionality": "3",
    // "closedPath": "false",
    // "description": "path",
    // "path": {},
    // "direction": "0000000000001010"

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
    ObjectNode id = JsonUtils.newNode().put(REGION, region.get(REGULATOR_ID).asInt()).put(ID,
        region.get(SEGMENT_ID).asInt());

    region.set(ID, id);
    region.remove(REGULATOR_ID);
    region.remove(SEGMENT_ID);

    // anchorPosition --> anchor (optional)
    JsonNode anchorPos = region.get(ANCHOR_POSITION);
    if (anchorPos != null) {
      region.set(ANCHOR, JsonUtils.toObjectNode(Position3DBuilder
          .dsrcPosition3D(Position3DBuilder.odePosition3D(region.get(ANCHOR_POSITION))).toJson()));
      region.remove(ANCHOR_POSITION);
    }

    // lane width (optional)
    JsonNode laneWidth = region.get(LANE_WIDTH);
    if (laneWidth != null) {
      region.put(LANE_WIDTH, LaneWidthBuilder.laneWidth(JsonUtils.decimalValue(laneWidth)));
    }

    // directionality (optional)
    if (region.has(DIRECTIONALITY)) {
      JsonNode directionality = region.get(DIRECTIONALITY);
      String enumString =
          CommonUtils.enumToString(DirectionOfUseEnum.class, directionality.asText());
      if (enumString != null) {
        region.set(DIRECTIONALITY, JsonUtils.newNode().put(enumString, EMPTY_FIELD_FLAG));
      }
    }

    // closed path (optional)
    JsonNode closedPath = region.get(CLOSED_PATH);
    if (closedPath != null) {
      region.put(CLOSED_PATH,
          (closedPath.asBoolean() ? BOOLEAN_OBJECT_TRUE : BOOLEAN_OBJECT_FALSE));
    }

    // description (optional)
    JsonNode descriptionNode = region.get(DESCRIPTION);
    if (descriptionNode != null) {
      String descriptionType = descriptionNode.asText();
      if (PATH.equals(descriptionType)) {
        ObjectNode pathNode = (ObjectNode) region.get(PATH);
        replacePath(pathNode);
        region.set(DESCRIPTION, JsonUtils.newNode().set(PATH, pathNode));
      } else if (GEOMETRY.equals(descriptionType)) {
        ObjectNode newGeometry = (ObjectNode) region.get(GEOMETRY);
        replaceGeometry(newGeometry);
        region.set(DESCRIPTION, JsonUtils.newNode().set(GEOMETRY, newGeometry));
      } else if (OLD_REGION.equals(descriptionType)) {
        ObjectNode newOldRegion = (ObjectNode) region.get(OLD_REGION);
        replaceOldRegion(newOldRegion);
        region.set(DESCRIPTION, JsonUtils.newNode().set(OLD_REGION, newOldRegion));
      }
      region.remove(descriptionType);
    }
  }

  private static void replacePath(ObjectNode pathNode) {

    //// EXPECTED INPUT:
    // "path":
    // "scale": "0",
    // "type": "ll",
    // "nodes": []

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
    String nodeType = pathNode.get(TYPE).asText();
    JsonNode nodes = pathNode.get(NODES);
    JsonNode nodeList;
    if (LL.equals(nodeType)) {
      nodeList = JsonUtils.newNode().set("NodeLL", transformNodeSetLL(nodes));
      pathNode.set(OFFSET, JsonUtils.newNode().set(LL, JsonUtils.newNode().set(NODES, nodeList)));
    } else if (XY.equals(nodeType)) {
      nodeList = JsonUtils.newNode().set(NODE_XY2, transformNodeSetXY(nodes));
      pathNode.set(OFFSET, JsonUtils.newNode().set(XY, JsonUtils.newNode().set(NODES, nodeList)));
    }
    pathNode.remove(NODES);
    pathNode.remove(TYPE);

  }

  private static ArrayNode transformNodeSetLL(JsonNode nodes) {

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

    if (nodes.isArray()) {
      Iterator<JsonNode> nodeListIter = nodes.elements();

      while (nodeListIter.hasNext()) {
        JsonNode inputNode = nodeListIter.next();
        outputNodeList.add(transformNodeLL(inputNode));
      }
    }

    return outputNodeList;
  }

  private static ObjectNode transformNodeLL(JsonNode oldNode) {

    //// EXPECTED INPUT:

    // "nodeLong": "0.0031024",
    // "nodeLat": "0.0014506",
    // "delta": "node-LL3"

    //// EXPECTED OUTPUT:
    // <NodeLL>
    // .<delta>
    // ..<node-LL3>
    // ...<lon>14506</lon>
    // ...<lat>31024</lat>
    // ..</node-LL3>
    // .</delta>
    // </NodeLL>

    BigDecimal latOffset = JsonUtils.decimalValue(oldNode.get(NODE_LAT));
    BigDecimal longOffset = JsonUtils.decimalValue(oldNode.get(NODE_LONG));
    JsonNode delta = oldNode.get(DELTA);
    Long transformedLat = null;
    Long transformedLong = null;

    ObjectNode innerNode = JsonUtils.newNode();
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
    ObjectNode deltaNode = JsonUtils.newNode().set(DELTA, innerNode);
    return deltaNode;
  }

  // -- Nodes with LL content Span at the equator when using a zoom of one:
  // node-LL1 Node-LL-24B, -- within +- 22.634554 meters of last node
  // node-LL2 Node-LL-28B, -- within +- 90.571389 meters of last node
  // node-LL3 Node-LL-32B, -- within +- 362.31873 meters of last node
  // node-LL4 Node-LL-36B, -- within +- 01.449308 Kmeters of last node
  // node-LL5 Node-LL-44B, -- within +- 23.189096 Kmeters of last node
  // node-LL6 Node-LL-48B, -- within +- 92.756481 Kmeters of last node
  // node-LatLon Node-LLmD-64b, -- node is a full 32b Lat/Lon range
  private static String nodeOffsetPointLL(long transformedLat, long transformedLon) {
    long transformedLatabs = Math.abs(transformedLat);
    long transformedLonabs = Math.abs(transformedLon);
    if (((transformedLatabs & (-1 << 11)) == 0
        || (transformedLat < 0 && (transformedLatabs ^ (1 << 11)) == 0))
        && (transformedLonabs & (-1 << 11)) == 0
        || (transformedLon < 0 && ((transformedLonabs ^ (1 << 11)) == 0))) {
      // 11 bit value
      return "node-LL1";
    } else if (((transformedLatabs & (-1 << 13)) == 0
        || (transformedLat < 0 && (transformedLatabs ^ (1 << 13)) == 0))
        && (transformedLonabs & (-1 << 13)) == 0
        || (transformedLon < 0 && ((transformedLonabs ^ (1 << 13)) == 0))) {
      // 13 bit value
      return "node-LL2";
    } else if (((transformedLatabs & (-1 << 15)) == 0
        || (transformedLat < 0 && (transformedLatabs ^ (1 << 15)) == 0))
        && (transformedLonabs & (-1 << 15)) == 0
        || (transformedLon < 0 && ((transformedLonabs ^ (1 << 15)) == 0))) {
      // 15 bit value
      return "node-LL3";
    } else if (((transformedLatabs & (-1 << 17)) == 0
        || (transformedLat < 0 && (transformedLatabs ^ (1 << 17)) == 0))
        && (transformedLonabs & (-1 << 17)) == 0
        || (transformedLon < 0 && ((transformedLonabs ^ (1 << 17)) == 0))) {
      // 17 bit value
      return "node-LL4";
    } else if (((transformedLatabs & (-1 << 21)) == 0
        || (transformedLat < 0 && (transformedLatabs ^ (1 << 21)) == 0))
        && (transformedLonabs & (-1 << 21)) == 0
        || (transformedLon < 0 && ((transformedLonabs ^ (1 << 21)) == 0))) {
      // 21 bit value
      return "node-LL5";
    } else if (((transformedLatabs & (-1 << 23)) == 0
        || (transformedLat < 0 && (transformedLatabs ^ (1 << 23)) == 0))
        && (transformedLonabs & (-1 << 23)) == 0
        || (transformedLon < 0 && ((transformedLonabs ^ (1 << 23)) == 0))) {
      // 23 bit value
      return "node-LL6";
    } else {
      throw new IllegalArgumentException(
          "Invalid node lat/long offset: " + transformedLat + "/" + transformedLon
              + ". Values must be between a range of -0.8388608/+0.8388607 degrees.");
    }

  }

  /**
   * Replaces geometry.
   *
   * @param geometry ObjectNode representing the geometry
   */
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
      geometry.put(LANE_WIDTH, LaneWidthBuilder.laneWidth(JsonUtils.decimalValue(laneWidth)));
    }

    // replace circle
    replaceCircle(geometry.get(CIRCLE));
  }

  /**
   * Replaces old region.
   *
   * @param oldRegion ObjectNode representing the old region
   */
  public static void replaceOldRegion(ObjectNode oldRegion) {

    // old region == ValidRegion
    // elements:
    // direction - no changes

    // extent - no changes
    JsonNode extentNode = oldRegion.get(EXTENT);
    String extent = CommonUtils.enumToString(Extent.ExtentEnum.class, extentNode.asText());
    oldRegion.set(EXTENT, JsonUtils.newNode().put(extent, EMPTY_FIELD_FLAG));

    // area - needs changes
    replaceArea(oldRegion.get("area"));
  }

  /**
   * Replaces area.
   *
   * @param area JsonNode representing the area
   */
  public static void replaceArea(JsonNode area) {

    // area contains one of:
    // shapePointSet
    // circle
    // regionPointSet

    ObjectNode updatedNode = (ObjectNode) area;

    if (updatedNode.has(SHAPE_POINT_SET)) {
      JsonNode shapePointSet = updatedNode.get(SHAPE_POINT_SET);
      replaceShapePointSet(shapePointSet);
      updatedNode.set(SHAPE_POINT_SET, shapePointSet);
    } else if (updatedNode.has(CIRCLE)) {
      replaceCircle(updatedNode.get(CIRCLE));
    } else if (updatedNode.has(REGION_POINT_SET)) {
      replaceRegionPointSet(updatedNode.get(REGION_POINT_SET));
    }
  }

  private static void replaceRegionPointSet(JsonNode regionPointSet) {
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
  }

  /**
   * Replaces circle.
   *
   * @param circle JsonNode representing the circle
   */
  public static void replaceCircle(JsonNode circle) {

    // Circle ::= SEQUENCE
    // center Position3D,
    // radius Radius-B12,
    // units DistanceUnits

    ObjectNode updatedNode = (ObjectNode) circle;

    JsonNode centerPosition = null;
    if (updatedNode.has(POSITION)) {
      centerPosition = updatedNode.get(POSITION);
      updatedNode.remove(POSITION);
    } else {
      centerPosition = updatedNode.get(CENTER);
    }

    // replace center
    JsonUtils.addNode(updatedNode, CENTER,
        Position3DBuilder.dsrcPosition3D(Position3DBuilder.odePosition3D(centerPosition)));

    // radius does not need replacement

    // replace units
    if (updatedNode.has(UNITS)) {
      JsonNode units = updatedNode.get(UNITS);
      String enumString = CommonUtils.enumToString(DistanceUnitsEnum.class, units.asText());
      if (enumString != null) {
        updatedNode.set(UNITS, JsonUtils.newNode().put(enumString, EMPTY_FIELD_FLAG));
      }
    }
  }

  /**
   * Replaces shape point set.
   *
   * @param shapePointSet JsonNode representing the shape point set
   */
  public static void replaceShapePointSet(JsonNode shapePointSet) {
    // shape point set contains:
    // anchor
    // lane width
    // directionality
    // node list

    ObjectNode updatedNode = (ObjectNode) shapePointSet;

    // replace anchor
    if (updatedNode.has(ANCHOR)) {
      JsonUtils.addNode(updatedNode, ANCHOR,
          Position3DBuilder.dsrcPosition3D(
              Position3DBuilder.odePosition3D(updatedNode.get(ANCHOR))));
    }

    // replace lane width
    if (updatedNode.has(LANE_WIDTH)) {
      updatedNode.put(LANE_WIDTH,
          LaneWidthBuilder.laneWidth(JsonUtils.decimalValue(updatedNode.get(LANE_WIDTH))));
    }

    // replace directionality
    if (updatedNode.has(DIRECTIONALITY)) {
      JsonNode directionality = updatedNode.get(DIRECTIONALITY);
      String enumString =
          CommonUtils.enumToString(DirectionOfUseEnum.class, directionality.asText());
      if (enumString != null) {
        updatedNode.set(DIRECTIONALITY, JsonUtils.newNode().put(enumString, EMPTY_FIELD_FLAG));
      }
    }

    // replace node list
    if (updatedNode.has(NODE_LIST)) {
      ObjectNode nodeList = (ObjectNode) updatedNode.get(NODE_LIST);
      if (nodeList.has(NODES)) {
        ArrayNode nodes = transformNodeSetXY(nodeList.get(NODES));
        nodeList.set(NODES, nodes);
      } else if (nodeList.has(COMPUTED)) {
        JsonNode computedLane = nodeList.get(COMPUTED);
        replaceComputedLane(computedLane);
      }
    }
  }

  /**
   * Replaces computed lane.
   *
   * @param jsonNode JsonNode representing the computed lane
   */
  public static void replaceComputedLane(JsonNode jsonNode) {
    ObjectNode updatedNode = (ObjectNode) jsonNode;

    // Nothing to do for referenceLaneId LaneID

    // offsetXaxis CHOICE
    // small DrivenLineOffsetSm,
    // large DrivenLineOffsetLg

    replaceScale(updatedNode, OFFSET_X_AXIS);

    // offsetYaxis CHOICE
    // small DrivenLineOffsetSm,
    // large DrivenLineOffsetLg

    replaceScale(updatedNode, OFFSET_Y_AXIS);

    // rotateXY Angle OPTIONAL
    if (updatedNode.has(ROTATE_XY)) {
      updatedNode.put(ROTATE_XY,
          AngleBuilder.angle(JsonUtils.decimalValue(updatedNode.get(ROTATE_XY))));
    }

    // scaleXaxis Scale-B12 OPTIONAL
    if (updatedNode.has(SCALE_X_AXIS)) {
      updatedNode.put(SCALE_X_AXIS,
          ScaleB12Builder.scaleB12(JsonUtils.decimalValue(updatedNode.get(SCALE_X_AXIS))));
    }

    // scaleYaxis Scale-B12 OPTIONAL
    if (updatedNode.has(SCALE_Y_AXIS)) {
      updatedNode.put(SCALE_Y_AXIS,
          ScaleB12Builder.scaleB12(JsonUtils.decimalValue(updatedNode.get(SCALE_Y_AXIS))));
    }
  }

  /**
   * Replaces scale.
   *
   * @param updatedNode ObjectNode representing the updated node
   * @param scale       String representing the scale
   */
  public static void replaceScale(ObjectNode updatedNode, String scale) {
    if (updatedNode.has(scale)) {
      int scaleX = updatedNode.get(scale).asInt();
      String key = "large";
      if (-2048 <= scaleX && scaleX <= 2047) {
        key = "small";
      }

      ObjectNode node = JsonUtils.newObjectNode(key, scaleX);
      updatedNode.set(scale, node);
    }
  }

  /**
   * Transforms node set XY.
   *
   * @param inputNodeList JsonNode representing the input node list
   * @return ArrayNode representing the transformed node list
   */
  public static ArrayNode transformNodeSetXY(JsonNode inputNodeList) {

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
        outputNodeList.add(transformNodeXY(inputNode));
      }
    }

    return outputNodeList;
  }

  /**
   * Transformed a NodeXY.
   *
   * @param oldNode JsonNode representing the old node
   * @return ObjectNode representing the transformed node
   */
  public static JsonNode transformNodeXY(JsonNode oldNode) {

    // nodexy contains:
    // delta NodeOffsetPointXY
    // attributes NodeAttributeSetXY (optional)

    ObjectNode nodexy = transformNodeOffsetPointXY(oldNode);

    if (oldNode.has(ATTRIBUTES)) {
      nodexy.set(ATTRIBUTES, transformNodeAttributeSetXY(oldNode.get(ATTRIBUTES)));
    }

    return nodexy;
  }

  private static ObjectNode transformNodeAttributeSetXY(JsonNode jsonNode) {
    // localNode NodeAttributeXYList OPTIONAL,
    // disabled SegmentAttributeXYList OPTIONAL,
    // enabled SegmentAttributeXYList OPTIONAL,
    // data LaneDataAttributeList OPTIONAL,
    // dWidth Offset-B10 OPTIONAL,
    // dElevation Offset-B10 OPTIONAL,

    ObjectNode updatedNode = JsonUtils.newNode();

    // localNode NodeAttributeXYList does not need to be replaced

    // disabled SegmentAttributeXYList does not need to be replaced
    // enabled SegmentAttributeXYList does not need to be replaced

    if (jsonNode.has(DATA)) {
      updatedNode.set(DATA, transformLaneDataAttributeList(jsonNode.get(DATA)));
    }
    if (jsonNode.has(D_WIDTH)) {
      updatedNode.put(D_WIDTH,
          OffsetXyBuilder.offsetXy(JsonUtils.decimalValue(jsonNode.get(D_WIDTH))));
    }

    if (jsonNode.has(D_ELEVATION)) {
      updatedNode.put(D_ELEVATION,
          OffsetXyBuilder.offsetXy(JsonUtils.decimalValue(jsonNode.get(D_ELEVATION))));
    }
    return updatedNode;
  }

  private static ArrayNode transformLaneDataAttributeList(JsonNode laneDataAttribute) {

    ArrayNode updatedLaneDataAttributeList = JsonUtils.newNode().arrayNode();

    if (laneDataAttribute.isArray()) {
      Iterator<JsonNode> laneDataAttributeListIter = laneDataAttribute.elements();

      while (laneDataAttributeListIter.hasNext()) {
        JsonNode oldNode = laneDataAttributeListIter.next();
        replaceLaneDataAttribute(oldNode);
        updatedLaneDataAttributeList.add(oldNode);
      }
    }
    return updatedLaneDataAttributeList;
  }

  /**
   * Replaces lane data attribute.
   *
   * @param oldNode JsonNode representing the old node
   */
  public static void replaceLaneDataAttribute(JsonNode oldNode) {
    // choice between 1 of the following:
    // pathEndPointAngle DeltaAngle
    // laneCrownPointCenter RoadwayCrownAngle
    // laneCrownPointLeft RoadwayCrownAngle
    // laneCrownPointRight RoadwayCrownAngle
    // laneAngle MergeDivergeNodeAngle
    // speedLimits SpeedLimitList

    ObjectNode updatedNode = (ObjectNode) oldNode;

    // pathEndPointAngle DeltaAngle does not need to be replaced
    if (oldNode.has("pathEndPointAngle")) {
      // do nothing
    } else if (oldNode.has(LANE_CROWN_POINT_CENTER)) {
      updatedNode.put(LANE_CROWN_POINT_CENTER, RoadwayCrownAngleBuilder
          .roadwayCrownAngle(JsonUtils.decimalValue(updatedNode.get(LANE_CROWN_POINT_CENTER))));
    } else if (oldNode.has(LANE_CROWN_POINT_LEFT)) {
      updatedNode.put(LANE_CROWN_POINT_LEFT, RoadwayCrownAngleBuilder
          .roadwayCrownAngle(JsonUtils.decimalValue(updatedNode.get(LANE_CROWN_POINT_LEFT))));
    } else if (oldNode.has(LANE_CROWN_POINT_RIGHT)) {
      updatedNode.put(LANE_CROWN_POINT_RIGHT, RoadwayCrownAngleBuilder
          .roadwayCrownAngle(JsonUtils.decimalValue(updatedNode.get(LANE_CROWN_POINT_RIGHT))));
    } else if (oldNode.has(LANE_ANGLE)) {
      updatedNode.put(LANE_ANGLE,
          MergeDivergeNodeAngleBuilder.mergeDivergeNodeAngle(
              JsonUtils.decimalValue(updatedNode.get(LANE_ANGLE))));
    } else if (oldNode.has(SPEED_LIMITS)) {
      replaceSpeedLimitList(updatedNode.get(SPEED_LIMITS));
    }
  }

  private static void replaceSpeedLimitList(JsonNode speedLimitList) {

    if (speedLimitList.isArray()) {
      Iterator<JsonNode> speedLimitListIter = speedLimitList.elements();

      while (speedLimitListIter.hasNext()) {
        JsonNode oldNode = speedLimitListIter.next();
        replaceRegulatorySpeedLimit(oldNode);
      }
    }
  }

  private static void replaceRegulatorySpeedLimit(JsonNode regulatorySpeedLimitNode) {
    // contains:
    // type SpeedLimitType
    // speed Velocity

    ObjectNode updatedNode = (ObjectNode) regulatorySpeedLimitNode;

    // type
    JsonNode typeNode = regulatorySpeedLimitNode.get(TYPE);
    String type = CommonUtils.enumToString(SpeedLimitTypeEnum.class, typeNode.asText());
    if (type != null) {
      updatedNode.set(TYPE, JsonUtils.newNode().put(type, EMPTY_FIELD_FLAG));
    }

    // replace velocity
    updatedNode.put(SPEED,
        VelocityBuilder.velocity(JsonUtils.decimalValue(updatedNode.get(SPEED))));

  }

  /**
   * Transformed node offset point XY.
   *
   * @param oldNode JsonNode representing the old node
   * @return ObjectNode representing the transformed node
   */
  public static ObjectNode transformNodeOffsetPointXY(JsonNode oldNode) {
    //// EXPECTED INPUT:

    // "nodeLong": "0.0031024",
    // "nodeLat": "0.0014506",
    // "delta": "node-LL3"

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
      BigDecimal offsetX = JsonUtils.decimalValue(oldNode.get(X));
      BigDecimal offsetY = JsonUtils.decimalValue(oldNode.get(Y));
      Long transformedX = OffsetXyBuilder.offsetXy(offsetX);
      Long transformedY = OffsetXyBuilder.offsetXy(offsetY);
      ObjectNode xy = JsonUtils.newNode().put(X, transformedX).put(Y, transformedY);
      if (deltaText.equals(NODE_XY)) {
        innerNode.set(nodeOffsetPointXY(transformedX, transformedY), xy);
      } else {
        innerNode.set(deltaText, xy);
      }
    } else if (deltaText.startsWith(NODE_LAT_LON)) {
      BigDecimal lonOffset = JsonUtils.decimalValue(oldNode.get(NODE_LONG));
      BigDecimal latOffset = JsonUtils.decimalValue(oldNode.get(NODE_LAT));
      Long transformedLon = LatitudeBuilder.j2735Latitude(lonOffset);
      Long transformedLat = LongitudeBuilder.j2735Longitude(latOffset);
      ObjectNode latLong = JsonUtils.newNode().put(LON, transformedLon).put(LAT, transformedLat);
      if (deltaText.equals(NODE_XY)) {
        innerNode.set(nodeOffsetPointLL(transformedLat, transformedLon), latLong);
      } else {
        innerNode.set(deltaText, latLong);
      }
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
      throw new IllegalArgumentException(
          "Invalid node X/Y offset: " + transformedX + "/" + transformedY
              + ". Values must be between a range of -327.68/+327.67 meters.");
    }
  }

  /**
   * Ensures compliance with the J2735 2024 standard by checking
   * for old fields in the given data frame.
   *
   * @param dataFrame the JSON object representing the data frame to be checked
   * @throws NoncompliantFieldsException if any old fields are found
   */
  public static void ensureComplianceWithJ2735Revision2024(ObjectNode dataFrame)
      throws NoncompliantFieldsException {
    // Check and throw exception if old fields are found
    Set<String> nonCompliantFields = Set.of(
        SSP_MSG_CONTENT,
        SSP_MSG_TYPES,
        SSP_LOCATION_RIGHTS,
        SSP_TIM_RIGHTS,
        SSP_MSG_RIGHTS_1,
        SSP_MSG_RIGHTS_2,
        NOT_USED,
        NOT_USED_1,
        NOT_USED_2,
        NOT_USED_3,
        DURATON_TIME_MISSPELLED
    );
    ArrayList<String> violations = new ArrayList<>();
    for (String violationName : nonCompliantFields) {
      if (dataFrame.has(violationName)) {
        violations.add(violationName);
      }
    }
    if (!violations.isEmpty()) {
      throw new NoncompliantFieldsException(
          String.format(
              "Data frame contains the following old fields that are not compliant with "
                  + "J2735 2024: [%s]. Deserialization should prevent this.",
              violations));
    }
  }

  /**
   * Exception thrown when noncompliant fields are found in the data frame.
   */
  public static class NoncompliantFieldsException extends Exception {
    public NoncompliantFieldsException(String message) {
      super(message);
    }
  }

}
