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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;
import us.dot.its.jpo.ode.util.JsonUtils;
import us.dot.its.jpo.ode.util.JsonUtils.JsonUtilsException;

public class TravelerMessageFromHumanToAsnConverterTest {
  @Mocked
  private Logger logger;

  @Before
  public void setup() {
    new MockUp<LoggerFactory>() {
      @Mock
      public Logger getLogger(String value) {
        return logger;
      }
    };
  }

  @Test
  public void testAdvisoryNodeLL() throws JsonProcessingException, IOException, JsonUtilsException {

    ObjectNode inputTID = JsonUtils.toObjectNode(
        "{\"request\":{\"rsus\":[{\"rsuIndex\":\"10\",\"rsuTarget\":\"127.0.0.2\",\"rsuUsername\":\"v3user\",\"rsuPassword\":\"password\",\"rsuRetries\":\"1\",\"rsuTimeout\":\"1000\"}],\"snmp\":{\"rsuid\":\"00000083\",\"msgid\":\"31\",\"mode\":\"1\",\"channel\":\"178\",\"interval\":\"2\",\"deliverystart\":\"2017-06-01T17:47:11-05:00\",\"deliverystop\":\"2018-01-01T17:47:11-05:15\",\"enable\":\"1\",\"status\":\"4\"}},\"tim\":{\"msgCnt\":\"1\",\"timeStamp\":\"2017-08-03T22:25:36.297Z\",\"urlB\":\"null\",\"packetID\":\"EC9C236B0000000000\",\"dataframes\":[{\"startDateTime\":\"2017-08-02T22:25:00.000Z\",\"durationTime\":1,\"sspTimRights\":\"0\",\"frameType\":\"advisory\",\"msgId\":{\"roadSignID\":{\"position\":{\"latitude\":\"41.678473\",\"longitude\":\"-108.782775\",\"elevation\":\"917.1432\"},\"viewAngle\":\"1010101010101010\",\"mutcdCode\":\"warning\",\"crc\":\"0000\"}},\"priority\":\"0\",\"sspLocationRights\":\"3\",\"regions\":[{\"name\":\"Testing TIM\",\"regulatorID\":\"0\",\"segmentID\":\"33\",\"anchorPosition\":{\"latitude\":\"41.2500807\",\"longitude\":\"-111.0093847\",\"elevation\":\"2020.6969900289998\"},\"laneWidth\":\"7\",\"directionality\":\"3\",\"closedPath\":\"false\",\"description\":\"path\",\"path\":{\"scale\":\"0\",\"type\":\"ll\",\"nodes\":[{\"nodeLong\":\"0.0002047\",\"nodeLat\":\"-0.0002048\",\"delta\":\"node-LL\"},{\"nodeLong\":\"0.0008191\",\"nodeLat\":\"-0.0008192\",\"delta\":\"node-LL\"},{\"nodeLong\":\"0.0032767\",\"nodeLat\":\"-0.0032768\",\"delta\":\"node-LL\"},{\"nodeLong\":\"0.0131071\",\"nodeLat\":\"-0.0131072\",\"delta\":\"node-LL\"},{\"nodeLong\":\"0.2097151\",\"nodeLat\":\"-0.2097152\",\"delta\":\"node-LL\"},{\"nodeLong\":\"0.8388607\",\"nodeLat\":\"-0.8388608\",\"delta\":\"node-LL\"},{\"nodeLong\":\"0.0002047\",\"nodeLat\":\"-0.0002048\",\"delta\":\"node-LL1\"},{\"nodeLong\":\"0.0008191\",\"nodeLat\":\"-0.0008192\",\"delta\":\"node-LL2\"},{\"nodeLong\":\"0.0032767\",\"nodeLat\":\"-0.0032768\",\"delta\":\"node-LL3\"},{\"nodeLong\":\"0.0131071\",\"nodeLat\":\"-0.0131072\",\"delta\":\"node-LL4\"},{\"nodeLong\":\"0.2097151\",\"nodeLat\":\"-0.2097152\",\"delta\":\"node-LL5\"},{\"nodeLong\":\"0.8388607\",\"nodeLat\":\"-0.8388608\",\"delta\":\"node-LL6\"},{\"nodeLong\":\"-111.0093847\",\"nodeLat\":\"41.2500807\",\"delta\":\"node-LatLon\"}]},\"direction\":\"0000000000001010\"}],\"sspMsgTypes\":\"2\",\"sspMsgContent\":\"3\",\"content\":\"advisory\",\"items\":[\"125\",\"some text\",\"250\",\"'98765\"],\"url\":\"null\"}]}}");
    TravelerMessageFromHumanToAsnConverter.convertTravelerInputDataToEncodableTim(inputTID);

    ObjectNode expectedTID = JsonUtils.toObjectNode(
        "{\"request\":{\"rsus\":[{\"rsuIndex\":\"10\",\"rsuTarget\":\"127.0.0.2\",\"rsuUsername\":\"v3user\",\"rsuPassword\":\"password\",\"rsuRetries\":\"1\",\"rsuTimeout\":\"1000\"}],\"snmp\":{\"rsuid\":\"00000083\",\"msgid\":\"31\",\"mode\":\"1\",\"channel\":\"178\",\"interval\":\"2\",\"deliverystart\":\"2017-06-01T17:47:11-05:00\",\"deliverystop\":\"2018-01-01T17:47:11-05:15\",\"enable\":\"1\",\"status\":\"4\"}},\"tim\":{\"msgCnt\":\"1\",\"timeStamp\":309505,\"urlB\":\"null\",\"packetID\":\"EC9C236B0000000000\",\"dataFrames\":{\"TravelerDataFrame\":[{\"sspTimRights\":\"0\",\"frameType\":{\"advisory\":\"EMPTY_TAG\"},\"msgId\":{\"roadSignID\":{\"position\":{\"lat\":416784730,\"long\":-1087827750,\"elevation\":9171},\"viewAngle\":\"1010101010101010\",\"mutcdCode\":{\"warning\":\"EMPTY_TAG\"},\"crc\":\"0000\"}},\"priority\":\"0\",\"sspLocationRights\":\"3\",\"regions\":{\"GeographicalPath\":[{\"name\":\"Testing TIM\",\"laneWidth\":700,\"directionality\":{\"both\":\"EMPTY_TAG\"},\"closedPath\":\"BOOLEAN_OBJECT_FALSE\",\"description\":{\"path\":{\"scale\":\"0\",\"offset\":{\"ll\":{\"nodes\":{\"NodeLL\":[{\"delta\":{\"node-LL1\":{\"lat\":-2048,\"lon\":2047}}},{\"delta\":{\"node-LL2\":{\"lat\":-8192,\"lon\":8191}}},{\"delta\":{\"node-LL3\":{\"lat\":-32768,\"lon\":32767}}},{\"delta\":{\"node-LL4\":{\"lat\":-131072,\"lon\":131071}}},{\"delta\":{\"node-LL5\":{\"lat\":-2097152,\"lon\":2097151}}},{\"delta\":{\"node-LL6\":{\"lat\":-8388608,\"lon\":8388607}}},{\"delta\":{\"node-LL1\":{\"lat\":-2048,\"lon\":2047}}},{\"delta\":{\"node-LL2\":{\"lat\":-8192,\"lon\":8191}}},{\"delta\":{\"node-LL3\":{\"lat\":-32768,\"lon\":32767}}},{\"delta\":{\"node-LL4\":{\"lat\":-131072,\"lon\":131071}}},{\"delta\":{\"node-LL5\":{\"lat\":-2097152,\"lon\":2097151}}},{\"delta\":{\"node-LL6\":{\"lat\":-8388608,\"lon\":8388607}}},{\"delta\":{\"node-LatLon\":{\"lat\":412500807,\"lon\":-1110093847}}}]}}}}},\"direction\":\"0000000000001010\",\"id\":{\"region\":0,\"id\":33},\"anchor\":{\"lat\":412500807,\"long\":-1110093847,\"elevation\":20207}}]},\"url\":\"null\",\"sspMsgRights2\":3,\"sspMsgRights1\":2,\"duratonTime\":1,\"startYear\":2017,\"startTime\":308065,\"tcontent\":{\"advisory\":{\"SEQUENCE\":[{\"item\":{\"itis\":125}},{\"item\":{\"text\":\"some text\"}},{\"item\":{\"itis\":250}},{\"item\":{\"text\":\"98765\"}}]}}}]}}}");
    assertEquals(expectedTID.toString(), inputTID.toString());
    JSONObject timObject = new JSONObject();
    try {
      timObject.put(TravelerMessageFromHumanToAsnConverter.TRAVELER_INFORMATION,
          JsonUtils.toJSONObject(inputTID.toString()));
    } catch (JSONException e1) {
      e1.printStackTrace();
    } catch (JsonUtilsException e1) {
      e1.printStackTrace();
    }
    try {
      assertNotNull(XML.toString(timObject));
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testWorkzoneNodeXYWithStringLatLon() throws JsonUtilsException {

    ObjectNode inputTID = JsonUtils.toObjectNode(
        "{\"request\":{\"rsus\":[{\"rsuIndex\":\"10\",\"rsuTarget\":\"127.0.0.3\",\"rsuUsername\":\"v3user\",\"rsuPassword\":\"password\",\"rsuRetries\":\"1\",\"rsuTimeout\":\"1000\"}],\"snmp\":{\"rsuid\":\"00000083\",\"msgid\":\"31\",\"mode\":\"1\",\"channel\":\"178\",\"interval\":\"2\",\"deliverystart\":\"2017-06-01T17:47:11-05:00\",\"deliverystop\":\"2018-01-01T17:47:11-05:15\",\"enable\":\"1\",\"status\":\"4\"},\"sdw\":{\"ttl\":\"oneweek\",\"serviceRegion\":{\"nwCorner\":{\"latitude\":\"44.998459\",\"longitude\":\"-111.040817\"},\"seCorner\":{\"latitude\":\"41.104674\",\"longitude\":\"-104.111312\"}}}},\"tim\":{\"msgCnt\":\"1\",\"timeStamp\":\"2017-10-27T18:04:43.045Z\",\"packetID\":\"3\",\"urlB\":\"null\",\"dataframes\":[{\"startDateTime\":\"2017-10-20T05:22:33.985Z\",\"durationTime\":100,\"frameType\":\"1\",\"sspTimRights\":\"1\",\"msgId\":\"roadSignID\",\"position\":{\"latitude\":\"40.573068\",\"longitude\":\"-105.049016\",\"elevation\":\"1500.8999999999999\"},\"viewAngle\":\"1111111111111111\",\"mutcd\":\"2\",\"crc\":\"0000\",\"priority\":\"5\",\"sspLocationRights\":\"1\",\"regions\":[{\"name\":\"Testing TIM\",\"regulatorID\":\"0\",\"segmentID\":\"33\",\"anchorPosition\":{\"latitude\":\"40.573068\",\"longitude\":\"-105.049016\",\"elevation\":\"1500.8999999999999\"},\"laneWidth\":\"327\",\"directionality\":\"3\",\"closedPath\":\"false\",\"description\":\"path\",\"path\":{\"scale\":\"0\",\"type\":\"xy\",\"nodes\":[{\"nodeLong\":\"-105.047355\",\"nodeLat\":\"40.572429\",\"delta\":\"node-LatLon\",\"attributes\":{\"localNode\":[\"stopLine\",\"roundedCapStyleA\",\"roundedCapStyleB\",\"mergePoint\",\"divergePoint\",\"downstreamStopLine\",\"downstreamStartNode\",\"closedToTraffic\",\"safeIsland\",\"curbPresentAtStepOff\",\"hydrantPresent\",\"reserved\"],\"disabled\":[\"reserved\",\"doNotBlock\",\"whiteLine\",\"mergingLaneLeft\",\"mergingLaneRight\",\"curbOnLeft\",\"curbOnRight\",\"loadingzoneOnLeft\",\"loadingzoneOnRight\",\"turnOutPointOnLeft\",\"turnOutPointOnRight\"],\"enabled\":[\"adjacentParkingOnLeft\",\"adjacentParkingOnRight\",\"adjacentBikeLaneOnLeft\"],\"data\":[{\"pathEndPointAngle\":123},{\"laneCrownPointCenter\":12.3},{\"laneCrownPointLeft\":\"23.4\"},{\"laneCrownPointRight\":34.5},{\"laneAngle\":\"1.23\"},{\"speedLimits\":[{\"type\":\"2\",\"speed\":\"12.3\"},{\"type\":\"maxSpeedInSchoolZone\",\"speed\":\"23.4\"},{\"type\":3,\"speed\":12.3},{\"type\":\"vehicleMinSpeed\",\"speed\":23.4}]}],\"dWidth\":\"1.23\",\"dElevation\":\"2.34\"}},{\"nodeLong\":\"-105.046844\",\"nodeLat\":\"40.572228\",\"delta\":\"node-LatLon\"},{\"nodeLong\":\"-105.04659\",\"nodeLat\":\"40.572113\",\"delta\":\"node-LatLon\"},{\"nodeLong\":\"-105.046243\",\"nodeLat\":\"40.57191\",\"delta\":\"node-LatLon\"},{\"nodeLong\":\"-105.045936\",\"nodeLat\":\"40.571675\",\"delta\":\"node-LatLon\"},{\"nodeLong\":\"-105.045674\",\"nodeLat\":\"40.571422\",\"delta\":\"node-LatLon\"},{\"nodeLong\":\"-105.04545\",\"nodeLat\":\"40.571131\",\"delta\":\"node-LatLon\"},{\"nodeLong\":\"-105.045235\",\"nodeLat\":\"40.570724\",\"delta\":\"node-LatLon\"},{\"nodeLong\":\"-105.045113\",\"nodeLat\":\"40.570293\",\"delta\":\"node-LatLon\"},{\"nodeLong\":\"-105.045087\",\"nodeLat\":\"40.569848\",\"delta\":\"node-LatLon\"}]},\"direction\":\"1111111111111111\"}],\"sspMsgTypes\":\"1\",\"sspMsgContent\":\"1\",\"content\":\"workZone\",\"items\":[\"7425\"],\"url\":\"null\"}]}}");
    TravelerMessageFromHumanToAsnConverter.convertTravelerInputDataToEncodableTim(inputTID);

    ObjectNode expectedTID = JsonUtils.toObjectNode(
        "{\"request\":{\"rsus\":[{\"rsuIndex\":\"10\",\"rsuTarget\":\"127.0.0.3\",\"rsuUsername\":\"v3user\",\"rsuPassword\":\"password\",\"rsuRetries\":\"1\",\"rsuTimeout\":\"1000\"}],\"snmp\":{\"rsuid\":\"00000083\",\"msgid\":\"31\",\"mode\":\"1\",\"channel\":\"178\",\"interval\":\"2\",\"deliverystart\":\"2017-06-01T17:47:11-05:00\",\"deliverystop\":\"2018-01-01T17:47:11-05:15\",\"enable\":\"1\",\"status\":\"4\"},\"sdw\":{\"ttl\":\"oneweek\",\"serviceRegion\":{\"nwCorner\":{\"latitude\":\"44.998459\",\"longitude\":\"-111.040817\"},\"seCorner\":{\"latitude\":\"41.104674\",\"longitude\":\"-104.111312\"}}}},\"tim\":{\"msgCnt\":\"1\",\"timeStamp\":431644,\"packetID\":\"3\",\"urlB\":\"null\",\"dataFrames\":{\"TravelerDataFrame\":[{\"frameType\":{\"1\":\"EMPTY_TAG\"},\"sspTimRights\":\"1\",\"msgId\":\"roadSignID\",\"position\":{\"latitude\":\"40.573068\",\"longitude\":\"-105.049016\",\"elevation\":\"1500.8999999999999\"},\"viewAngle\":\"1111111111111111\",\"mutcd\":\"2\",\"crc\":\"0000\",\"priority\":\"5\",\"sspLocationRights\":\"1\",\"regions\":{\"GeographicalPath\":[{\"name\":\"Testing TIM\",\"laneWidth\":32700,\"directionality\":{\"both\":\"EMPTY_TAG\"},\"closedPath\":\"BOOLEAN_OBJECT_FALSE\",\"description\":{\"path\":{\"scale\":\"0\",\"offset\":{\"xy\":{\"nodes\":{\"NodeXY\":[{\"delta\":{\"node-LatLon\":{\"lon\":-1050473550,\"lat\":405724290}},\"attributes\":{\"data\":[{\"pathEndPointAngle\":123},{\"laneCrownPointCenter\":41},{\"laneCrownPointLeft\":78},{\"laneCrownPointRight\":115},{\"laneAngle\":1},{\"speedLimits\":[{\"type\":{\"maxSpeedInSchoolZoneWhenChildrenArePresent\":\"EMPTY_TAG\"},\"speed\":615},{\"type\":{\"maxSpeedInSchoolZone\":\"EMPTY_TAG\"},\"speed\":1170},{\"type\":{\"maxSpeedInConstructionZone\":\"EMPTY_TAG\"},\"speed\":615},{\"type\":{\"vehicleMinSpeed\":\"EMPTY_TAG\"},\"speed\":1170}]}],\"dWidth\":123,\"dElevation\":234}},{\"delta\":{\"node-LatLon\":{\"lon\":-1050468440,\"lat\":405722280}}},{\"delta\":{\"node-LatLon\":{\"lon\":-1050465900,\"lat\":405721130}}},{\"delta\":{\"node-LatLon\":{\"lon\":-1050462430,\"lat\":405719100}}},{\"delta\":{\"node-LatLon\":{\"lon\":-1050459360,\"lat\":405716750}}},{\"delta\":{\"node-LatLon\":{\"lon\":-1050456740,\"lat\":405714220}}},{\"delta\":{\"node-LatLon\":{\"lon\":-1050454500,\"lat\":405711310}}},{\"delta\":{\"node-LatLon\":{\"lon\":-1050452350,\"lat\":405707240}}},{\"delta\":{\"node-LatLon\":{\"lon\":-1050451130,\"lat\":405702930}}},{\"delta\":{\"node-LatLon\":{\"lon\":-1050450870,\"lat\":405698480}}}]}}}}},\"direction\":\"1111111111111111\",\"id\":{\"region\":0,\"id\":33},\"anchor\":{\"lat\":405730680,\"long\":-1050490160,\"elevation\":15009}}]},\"url\":\"null\",\"sspMsgRights2\":1,\"sspMsgRights1\":1,\"duratonTime\":100,\"startYear\":2017,\"startTime\":420802,\"tcontent\":{\"workZone\":{\"SEQUENCE\":[{\"item\":{\"itis\":7425}}]}}}]}}}");
    assertEquals(expectedTID.toString(), inputTID.toString());
    JSONObject timObject = new JSONObject();
    try {
      timObject.put(TravelerMessageFromHumanToAsnConverter.TRAVELER_INFORMATION,
          JsonUtils.toJSONObject(inputTID.toString()));
    } catch (JSONException e) {
      e.printStackTrace();
    } catch (JsonUtilsException e) {
      e.printStackTrace();
    }
    try {
      assertNotNull(XML.toString(timObject));
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testGenericSignNodeXYWithNumericLatLon() throws JsonUtilsException {

    ObjectNode inputTID = JsonUtils.toObjectNode(
        "{\"request\":{\"rsus\":[{\"rsuIndex\":\"10\",\"rsuTarget\":\"127.0.0.3\",\"rsuUsername\":\"v3user\",\"rsuPassword\":\"password\",\"rsuRetries\":\"1\",\"rsuTimeout\":\"1000\"}],\"snmp\":{\"rsuid\":\"00000083\",\"msgid\":\"31\",\"mode\":\"1\",\"channel\":\"178\",\"interval\":\"2\",\"deliverystart\":\"2017-06-01T17:47:11-05:00\",\"deliverystop\":\"2018-01-01T17:47:11-05:15\",\"enable\":\"1\",\"status\":\"4\"},\"sdw\":{\"ttl\":\"oneweek\",\"serviceRegion\":{\"nwCorner\":{\"latitude\":44.998459,\"longitude\":-111.040817},\"seCorner\":{\"latitude\":41.104674,\"longitude\":-104.111312}}}},\"tim\":{\"msgCnt\":\"1\",\"timeStamp\":\"2017-10-27T18:04:43.045Z\",\"packetID\":\"3\",\"urlB\":\"null\",\"dataframes\":[{\"startDateTime\":\"2017-10-20T05:22:33.985Z\",\"durationTime\":100,\"frameType\":\"advisory\",\"sspTimRights\":\"1\",\"msgId\":\"roadSignID\",\"position\":{\"latitude\":40.573068,\"longitude\":-105.049016,\"elevation\":1500.8999999999999},\"viewAngle\":\"1111111111111111\",\"mutcd\":\"2\",\"crc\":\"0000\",\"priority\":\"5\",\"sspLocationRights\":\"1\",\"regions\":[{\"name\":\"Testing TIM\",\"regulatorID\":\"0\",\"segmentID\":\"33\",\"anchorPosition\":{\"latitude\":40.573068,\"longitude\":-105.049016,\"elevation\":1500.8999999999999},\"laneWidth\":\"327\",\"directionality\":\"3\",\"closedPath\":\"false\",\"description\":\"path\",\"path\":{\"scale\":\"0\",\"type\":\"xy\",\"nodes\":[{\"nodeLong\":-105.047355,\"nodeLat\":40.572429,\"delta\":\"node-LatLon\",\"attributes\":{\"localNode\":[\"stopLine\",\"roundedCapStyleA\",\"roundedCapStyleB\",\"mergePoint\",\"divergePoint\",\"downstreamStopLine\",\"downstreamStartNode\",\"closedToTraffic\",\"safeIsland\",\"curbPresentAtStepOff\",\"hydrantPresent\",\"reserved\"],\"disabled\":[\"reserved\",\"doNotBlock\",\"whiteLine\",\"mergingLaneLeft\",\"mergingLaneRight\",\"curbOnLeft\",\"curbOnRight\",\"loadingzoneOnLeft\",\"loadingzoneOnRight\",\"turnOutPointOnLeft\",\"turnOutPointOnRight\"],\"enabled\":[\"adjacentParkingOnLeft\",\"adjacentParkingOnRight\",\"adjacentBikeLaneOnLeft\"],\"data\":[{\"pathEndPointAngle\":123},{\"laneCrownPointCenter\":12.3},{\"laneCrownPointLeft\":\"23.4\"},{\"laneCrownPointRight\":34.5},{\"laneAngle\":\"1.23\"},{\"speedLimits\":[{\"type\":\"2\",\"speed\":\"12.3\"},{\"type\":\"maxSpeedInSchoolZone\",\"speed\":\"23.4\"},{\"type\":3,\"speed\":12.3},{\"type\":\"vehicleMinSpeed\",\"speed\":23.4}]}],\"dWidth\":\"1.23\",\"dElevation\":\"2.34\"}},{\"nodeLong\":\"-105.046844\",\"nodeLat\":\"40.572228\",\"delta\":\"node-LatLon\"},{\"nodeLong\":\"-105.04659\",\"nodeLat\":\"40.572113\",\"delta\":\"node-LatLon\"},{\"nodeLong\":\"-105.046243\",\"nodeLat\":\"40.57191\",\"delta\":\"node-LatLon\"},{\"nodeLong\":\"-105.045936\",\"nodeLat\":\"40.571675\",\"delta\":\"node-LatLon\"},{\"nodeLong\":\"-105.045674\",\"nodeLat\":\"40.571422\",\"delta\":\"node-LatLon\"},{\"nodeLong\":\"-105.04545\",\"nodeLat\":\"40.571131\",\"delta\":\"node-LatLon\"},{\"nodeLong\":\"-105.045235\",\"nodeLat\":\"40.570724\",\"delta\":\"node-LatLon\"},{\"nodeLong\":\"-105.045113\",\"nodeLat\":\"40.570293\",\"delta\":\"node-LatLon\"},{\"nodeLong\":\"-105.045087\",\"nodeLat\":\"40.569848\",\"delta\":\"node-LatLon\"}]},\"direction\":\"1111111111111111\"}],\"sspMsgTypes\":\"1\",\"sspMsgContent\":\"1\",\"content\":\"genericSign\",\"items\":[\"7425\"],\"url\":\"null\"}]}}");
    TravelerMessageFromHumanToAsnConverter.convertTravelerInputDataToEncodableTim(inputTID);

    ObjectNode expectedTID = JsonUtils.toObjectNode(
        "{\"request\":{\"rsus\":[{\"rsuIndex\":\"10\",\"rsuTarget\":\"127.0.0.3\",\"rsuUsername\":\"v3user\",\"rsuPassword\":\"password\",\"rsuRetries\":\"1\",\"rsuTimeout\":\"1000\"}],\"snmp\":{\"rsuid\":\"00000083\",\"msgid\":\"31\",\"mode\":\"1\",\"channel\":\"178\",\"interval\":\"2\",\"deliverystart\":\"2017-06-01T17:47:11-05:00\",\"deliverystop\":\"2018-01-01T17:47:11-05:15\",\"enable\":\"1\",\"status\":\"4\"},\"sdw\":{\"ttl\":\"oneweek\",\"serviceRegion\":{\"nwCorner\":{\"latitude\":44.998459,\"longitude\":-111.040817},\"seCorner\":{\"latitude\":41.104674,\"longitude\":-104.111312}}}},\"tim\":{\"msgCnt\":\"1\",\"timeStamp\":431644,\"packetID\":\"3\",\"urlB\":\"null\",\"dataFrames\":{\"TravelerDataFrame\":[{\"frameType\":{\"advisory\":\"EMPTY_TAG\"},\"sspTimRights\":\"1\",\"msgId\":\"roadSignID\",\"position\":{\"latitude\":40.573068,\"longitude\":-105.049016,\"elevation\":1500.8999999999999},\"viewAngle\":\"1111111111111111\",\"mutcd\":\"2\",\"crc\":\"0000\",\"priority\":\"5\",\"sspLocationRights\":\"1\",\"regions\":{\"GeographicalPath\":[{\"name\":\"Testing TIM\",\"laneWidth\":32700,\"directionality\":{\"both\":\"EMPTY_TAG\"},\"closedPath\":\"BOOLEAN_OBJECT_FALSE\",\"description\":{\"path\":{\"scale\":\"0\",\"offset\":{\"xy\":{\"nodes\":{\"NodeXY\":[{\"delta\":{\"node-LatLon\":{\"lon\":-1050473550,\"lat\":405724290}},\"attributes\":{\"data\":[{\"pathEndPointAngle\":123},{\"laneCrownPointCenter\":41},{\"laneCrownPointLeft\":78},{\"laneCrownPointRight\":115},{\"laneAngle\":1},{\"speedLimits\":[{\"type\":{\"maxSpeedInSchoolZoneWhenChildrenArePresent\":\"EMPTY_TAG\"},\"speed\":615},{\"type\":{\"maxSpeedInSchoolZone\":\"EMPTY_TAG\"},\"speed\":1170},{\"type\":{\"maxSpeedInConstructionZone\":\"EMPTY_TAG\"},\"speed\":615},{\"type\":{\"vehicleMinSpeed\":\"EMPTY_TAG\"},\"speed\":1170}]}],\"dWidth\":123,\"dElevation\":234}},{\"delta\":{\"node-LatLon\":{\"lon\":-1050468440,\"lat\":405722280}}},{\"delta\":{\"node-LatLon\":{\"lon\":-1050465900,\"lat\":405721130}}},{\"delta\":{\"node-LatLon\":{\"lon\":-1050462430,\"lat\":405719100}}},{\"delta\":{\"node-LatLon\":{\"lon\":-1050459360,\"lat\":405716750}}},{\"delta\":{\"node-LatLon\":{\"lon\":-1050456740,\"lat\":405714220}}},{\"delta\":{\"node-LatLon\":{\"lon\":-1050454500,\"lat\":405711310}}},{\"delta\":{\"node-LatLon\":{\"lon\":-1050452350,\"lat\":405707240}}},{\"delta\":{\"node-LatLon\":{\"lon\":-1050451130,\"lat\":405702930}}},{\"delta\":{\"node-LatLon\":{\"lon\":-1050450870,\"lat\":405698480}}}]}}}}},\"direction\":\"1111111111111111\",\"id\":{\"region\":0,\"id\":33},\"anchor\":{\"lat\":405730680,\"long\":-1050490160,\"elevation\":15009}}]},\"url\":\"null\",\"sspMsgRights2\":1,\"sspMsgRights1\":1,\"duratonTime\":100,\"startYear\":2017,\"startTime\":420802,\"tcontent\":{\"genericSign\":{\"SEQUENCE\":[{\"item\":{\"itis\":7425}}]}}}]}}}");
    assertEquals(expectedTID.toString(), inputTID.toString());
    JSONObject timObject = new JSONObject();
    try {
      timObject.put(TravelerMessageFromHumanToAsnConverter.TRAVELER_INFORMATION,
          JsonUtils.toJSONObject(inputTID.toString()));
    } catch (JSONException e) {
      e.printStackTrace();
    } catch (JsonUtilsException e) {
      e.printStackTrace();
    }
    try {
      assertNotNull(XML.toString(timObject));
    } catch (JSONException e) {

      e.printStackTrace();
    }
  }

  @Test
  public void testGeometryUnavailable() throws JsonUtilsException {
    ObjectNode inputTID = JsonUtils.toObjectNode(
        "{\"request\":{\"rsus\":[{\"rsuIndex\":\"10\",\"rsuTarget\":\"127.0.0.3\",\"rsuUsername\":\"v3user\",\"rsuPassword\":\"password\",\"rsuRetries\":\"1\",\"rsuTimeout\":\"1000\"}],\"snmp\":{\"rsuid\":\"00000083\",\"msgid\":\"31\",\"mode\":\"1\",\"channel\":\"178\",\"interval\":\"2\",\"deliverystart\":\"2017-06-01T17:47:11-05:00\",\"deliverystop\":\"2018-01-01T17:47:11-05:15\",\"enable\":\"1\",\"status\":\"4\"},\"sdw\":{\"ttl\":\"oneweek\",\"serviceRegion\":{\"nwCorner\":{\"latitude\":\"44.998459\",\"longitude\":\"-111.040817\"},\"seCorner\":{\"latitude\":\"41.104674\",\"longitude\":\"-104.111312\"}}}}, \"tim\": { \"msgCnt\": \"13\", \"timeStamp\": \"2017-03-13T01:07:11-05:00\", \"packetID\": \"1\", \"urlB\": \"null\", \"dataframes\": [ { \"sspTimRights\": \"0\", \"frameType\": \"unknown\", \"msgId\": \"roadSignID\", \"position\": { \"latitude\": \"41.678473\", \"longitude\": \"-108.782775\", \"elevation\": \"917.1432\" }, \"viewAngle\": \"1010101010101010\", \"mutcd\": \"5\", \"crc\": \"0000\", \"startDateTime\": \"2017-12-01T17:47:11-05:00\", \"durationTime\": \"22\", \"priority\": \"0\", \"sspLocationRights\": \"3\", \"regions\": [ { \"name\": \"bob\", \"regulatorID\": \"23\", \"segmentID\": \"33\", \"anchorPosition\": { \"latitude\": \"41.678473\", \"longitude\": \"-108.782775\", \"elevation\": \"917.1432\" }, \"laneWidth\": \"7\", \"directionality\": \"0\", \"closedPath\": \"false\", \"direction\": \"1010101010101010\", \"description\": \"geometry\", \"geometry\": { \"direction\": \"1010101010101010\", \"extent\": \"1\", \"laneWidth\": \"33\", \"circle\": { \"position\": { \"latitude\": \"41.678473\", \"longitude\": \"-108.782775\", \"elevation\": \"917.1432\" }, \"radius\": \"15\", \"units\": \"7\" } } } ], \"sspMsgTypes\": \"2\", \"sspMsgContent\": \"3\", \"content\": \"speedLimit\", \"items\": [ \"250\" ], \"url\": \"null\" } ] }}");
    TravelerMessageFromHumanToAsnConverter.convertTravelerInputDataToEncodableTim(inputTID);

    ObjectNode expectedTID = JsonUtils.toObjectNode(
        "{\"request\":{\"rsus\":[{\"rsuIndex\":\"10\",\"rsuTarget\":\"127.0.0.3\",\"rsuUsername\":\"v3user\",\"rsuPassword\":\"password\",\"rsuRetries\":\"1\",\"rsuTimeout\":\"1000\"}],\"snmp\":{\"rsuid\":\"00000083\",\"msgid\":\"31\",\"mode\":\"1\",\"channel\":\"178\",\"interval\":\"2\",\"deliverystart\":\"2017-06-01T17:47:11-05:00\",\"deliverystop\":\"2018-01-01T17:47:11-05:15\",\"enable\":\"1\",\"status\":\"4\"},\"sdw\":{\"ttl\":\"oneweek\",\"serviceRegion\":{\"nwCorner\":{\"latitude\":\"44.998459\",\"longitude\":\"-111.040817\"},\"seCorner\":{\"latitude\":\"41.104674\",\"longitude\":\"-104.111312\"}}}},\"tim\":{\"msgCnt\":\"13\",\"timeStamp\":102607,\"packetID\":\"1\",\"urlB\":\"null\",\"dataFrames\":{\"TravelerDataFrame\":[{\"sspTimRights\":\"0\",\"frameType\":{\"unknown\":\"EMPTY_TAG\"},\"msgId\":\"roadSignID\",\"position\":{\"latitude\":\"41.678473\",\"longitude\":\"-108.782775\",\"elevation\":\"917.1432\"},\"viewAngle\":\"1010101010101010\",\"mutcd\":\"5\",\"crc\":\"0000\",\"priority\":\"0\",\"sspLocationRights\":\"3\",\"regions\":{\"GeographicalPath\":[{\"name\":\"bob\",\"laneWidth\":700,\"directionality\":{\"unavailable\":\"EMPTY_TAG\"},\"closedPath\":\"BOOLEAN_OBJECT_FALSE\",\"direction\":\"1010101010101010\",\"description\":{\"geometry\":{\"direction\":\"1010101010101010\",\"extent\":\"1\",\"laneWidth\":3300,\"circle\":{\"radius\":\"15\",\"units\":{\"mile\":\"EMPTY_TAG\"},\"center\":{\"lat\":416784730,\"long\":-1087827750,\"elevation\":9171}}}},\"id\":{\"region\":23,\"id\":33},\"anchor\":{\"lat\":416784730,\"long\":-1087827750,\"elevation\":9171}}]},\"url\":\"null\",\"sspMsgRights2\":3,\"sspMsgRights1\":2,\"duratonTime\":22,\"startYear\":2017,\"startTime\":482327,\"tcontent\":{\"speedLimit\":{\"SEQUENCE\":[{\"item\":{\"itis\":250}}]}}}]}}}");
    assertEquals(expectedTID.toString(), inputTID.toString());

    JSONObject timObject = new JSONObject();
    try {
      timObject.put(TravelerMessageFromHumanToAsnConverter.TRAVELER_INFORMATION,
          JsonUtils.toJSONObject(inputTID.toString()));
    } catch (JSONException e) {

      e.printStackTrace();
    } catch (JsonUtilsException e) {

      e.printStackTrace();
    }
    try {
      assertNotNull(XML.toString(timObject));
    } catch (JSONException e) {

      e.printStackTrace();
    }

  }

  @Test
  public void testGeometryExitServiceForward() throws JsonUtilsException {
    ObjectNode inputTID = JsonUtils.toObjectNode(
        "{\"request\":{\"rsus\":[{\"rsuIndex\":\"10\",\"rsuTarget\":\"127.0.0.3\",\"rsuUsername\":\"v3user\",\"rsuPassword\":\"password\",\"rsuRetries\":\"1\",\"rsuTimeout\":\"1000\"}],\"snmp\":{\"rsuid\":\"00000083\",\"msgid\":\"31\",\"mode\":\"1\",\"channel\":\"178\",\"interval\":\"2\",\"deliverystart\":\"2017-06-01T17:47:11-05:00\",\"deliverystop\":\"2018-01-01T17:47:11-05:15\",\"enable\":\"1\",\"status\":\"4\"},\"sdw\":{\"ttl\":\"oneweek\",\"serviceRegion\":{\"nwCorner\":{\"latitude\":\"44.998459\",\"longitude\":\"-111.040817\"},\"seCorner\":{\"latitude\":\"41.104674\",\"longitude\":\"-104.111312\"}}}}, \"tim\": { \"msgCnt\": \"13\", \"timeStamp\": \"2017-03-13T01:07:11-05:00\", \"packetID\": \"1\", \"urlB\": \"null\", \"dataframes\": [ { \"sspTimRights\": \"0\", \"frameType\": \"0\", \"msgId\": \"roadSignID\", \"position\": { \"latitude\": \"41.678473\", \"longitude\": \"-108.782775\", \"elevation\": \"917.1432\" }, \"viewAngle\": \"1010101010101010\", \"mutcd\": \"5\", \"crc\": \"0000\", \"startDateTime\": \"2017-12-01T17:47:11-05:00\", \"durationTime\": \"22\", \"priority\": \"0\", \"sspLocationRights\": \"3\", \"regions\": [ { \"name\": \"bob\", \"regulatorID\": \"23\", \"segmentID\": \"33\", \"anchorPosition\": { \"latitude\": \"41.678473\", \"longitude\": \"-108.782775\", \"elevation\": \"917.1432\" }, \"laneWidth\": \"7\", \"directionality\": \"1\", \"closedPath\": \"false\", \"direction\": \"1010101010101010\", \"description\": \"geometry\", \"geometry\": { \"direction\": \"1010101010101010\", \"extent\": \"1\", \"laneWidth\": \"33\", \"circle\": { \"position\": { \"latitude\": \"41.678473\", \"longitude\": \"-108.782775\", \"elevation\": \"917.1432\" }, \"radius\": \"15\", \"units\": \"7\" } } } ], \"sspMsgTypes\": \"2\", \"sspMsgContent\": \"3\", \"content\": \"exitService\", \"items\": [ \"250\" ], \"url\": \"null\" } ] }}");
    TravelerMessageFromHumanToAsnConverter.convertTravelerInputDataToEncodableTim(inputTID);

    ObjectNode expectedTID = JsonUtils.toObjectNode(
        "{\"request\":{\"rsus\":[{\"rsuIndex\":\"10\",\"rsuTarget\":\"127.0.0.3\",\"rsuUsername\":\"v3user\",\"rsuPassword\":\"password\",\"rsuRetries\":\"1\",\"rsuTimeout\":\"1000\"}],\"snmp\":{\"rsuid\":\"00000083\",\"msgid\":\"31\",\"mode\":\"1\",\"channel\":\"178\",\"interval\":\"2\",\"deliverystart\":\"2017-06-01T17:47:11-05:00\",\"deliverystop\":\"2018-01-01T17:47:11-05:15\",\"enable\":\"1\",\"status\":\"4\"},\"sdw\":{\"ttl\":\"oneweek\",\"serviceRegion\":{\"nwCorner\":{\"latitude\":\"44.998459\",\"longitude\":\"-111.040817\"},\"seCorner\":{\"latitude\":\"41.104674\",\"longitude\":\"-104.111312\"}}}},\"tim\":{\"msgCnt\":\"13\",\"timeStamp\":102607,\"packetID\":\"1\",\"urlB\":\"null\",\"dataFrames\":{\"TravelerDataFrame\":[{\"sspTimRights\":\"0\",\"frameType\":{\"0\":\"EMPTY_TAG\"},\"msgId\":\"roadSignID\",\"position\":{\"latitude\":\"41.678473\",\"longitude\":\"-108.782775\",\"elevation\":\"917.1432\"},\"viewAngle\":\"1010101010101010\",\"mutcd\":\"5\",\"crc\":\"0000\",\"priority\":\"0\",\"sspLocationRights\":\"3\",\"regions\":{\"GeographicalPath\":[{\"name\":\"bob\",\"laneWidth\":700,\"directionality\":{\"forward\":\"EMPTY_TAG\"},\"closedPath\":\"BOOLEAN_OBJECT_FALSE\",\"direction\":\"1010101010101010\",\"description\":{\"geometry\":{\"direction\":\"1010101010101010\",\"extent\":\"1\",\"laneWidth\":3300,\"circle\":{\"radius\":\"15\",\"units\":{\"mile\":\"EMPTY_TAG\"},\"center\":{\"lat\":416784730,\"long\":-1087827750,\"elevation\":9171}}}},\"id\":{\"region\":23,\"id\":33},\"anchor\":{\"lat\":416784730,\"long\":-1087827750,\"elevation\":9171}}]},\"url\":\"null\",\"sspMsgRights2\":3,\"sspMsgRights1\":2,\"duratonTime\":22,\"startYear\":2017,\"startTime\":482327,\"tcontent\":{\"exitService\":{\"SEQUENCE\":[{\"item\":{\"itis\":250}}]}}}]}}}");
    assertEquals(expectedTID.toString(), inputTID.toString());

    JSONObject timObject = new JSONObject();
    try {
      timObject.put(TravelerMessageFromHumanToAsnConverter.TRAVELER_INFORMATION,
          JsonUtils.toJSONObject(inputTID.toString()));
    } catch (JSONException e) {

      e.printStackTrace();
    } catch (JsonUtilsException e) {

      e.printStackTrace();
    }
    try {
      assertNotNull(XML.toString(timObject));
    } catch (JSONException e) {

      e.printStackTrace();
    }

  }

  @Test
  public void testGeometryAdvisoryReverse() throws JsonUtilsException {
    ObjectNode inputTID = JsonUtils.toObjectNode(
        "{\"request\":{\"rsus\":[{\"rsuIndex\":\"10\",\"rsuTarget\":\"127.0.0.3\",\"rsuUsername\":\"v3user\",\"rsuPassword\":\"password\",\"rsuRetries\":\"1\",\"rsuTimeout\":\"1000\"}],\"snmp\":{\"rsuid\":\"00000083\",\"msgid\":\"31\",\"mode\":\"1\",\"channel\":\"178\",\"interval\":\"2\",\"deliverystart\":\"2017-06-01T17:47:11-05:00\",\"deliverystop\":\"2018-01-01T17:47:11-05:15\",\"enable\":\"1\",\"status\":\"4\"},\"sdw\":{\"ttl\":\"oneweek\",\"serviceRegion\":{\"nwCorner\":{\"latitude\":\"44.998459\",\"longitude\":\"-111.040817\"},\"seCorner\":{\"latitude\":\"41.104674\",\"longitude\":\"-104.111312\"}}}}, \"tim\": { \"msgCnt\": \"13\", \"timeStamp\": \"2017-03-13T01:07:11-05:00\", \"packetID\": \"1\", \"urlB\": \"null\", \"dataframes\": [ { \"sspTimRights\": \"0\", \"frameType\": \"roadSignage\", \"msgId\": \"roadSignID\", \"position\": { \"latitude\": \"41.678473\", \"longitude\": \"-108.782775\", \"elevation\": \"917.1432\" }, \"viewAngle\": \"1010101010101010\", \"mutcd\": \"5\", \"crc\": \"0000\", \"startDateTime\": \"2017-12-01T17:47:11-05:00\", \"durationTime\": \"22\", \"priority\": \"0\", \"sspLocationRights\": \"3\", \"regions\": [ { \"name\": \"bob\", \"regulatorID\": \"23\", \"segmentID\": \"33\", \"anchorPosition\": { \"latitude\": \"41.678473\", \"longitude\": \"-108.782775\", \"elevation\": \"917.1432\" }, \"laneWidth\": \"7\", \"directionality\": \"2\", \"closedPath\": \"false\", \"direction\": \"1010101010101010\", \"description\": \"geometry\", \"geometry\": { \"direction\": \"1010101010101010\", \"extent\": \"1\", \"laneWidth\": \"33\", \"circle\": { \"position\": { \"latitude\": \"41.678473\", \"longitude\": \"-108.782775\", \"elevation\": \"917.1432\" }, \"radius\": \"15\", \"units\": \"7\" } } } ], \"sspMsgTypes\": \"2\", \"sspMsgContent\": \"3\", \"content\": \"advisory\", \"items\": [ \"250\" ], \"url\": \"null\" } ] }}");
    TravelerMessageFromHumanToAsnConverter.convertTravelerInputDataToEncodableTim(inputTID);

    ObjectNode expectedTID = JsonUtils.toObjectNode(
        "{\"request\":{\"rsus\":[{\"rsuIndex\":\"10\",\"rsuTarget\":\"127.0.0.3\",\"rsuUsername\":\"v3user\",\"rsuPassword\":\"password\",\"rsuRetries\":\"1\",\"rsuTimeout\":\"1000\"}],\"snmp\":{\"rsuid\":\"00000083\",\"msgid\":\"31\",\"mode\":\"1\",\"channel\":\"178\",\"interval\":\"2\",\"deliverystart\":\"2017-06-01T17:47:11-05:00\",\"deliverystop\":\"2018-01-01T17:47:11-05:15\",\"enable\":\"1\",\"status\":\"4\"},\"sdw\":{\"ttl\":\"oneweek\",\"serviceRegion\":{\"nwCorner\":{\"latitude\":\"44.998459\",\"longitude\":\"-111.040817\"},\"seCorner\":{\"latitude\":\"41.104674\",\"longitude\":\"-104.111312\"}}}},\"tim\":{\"msgCnt\":\"13\",\"timeStamp\":102607,\"packetID\":\"1\",\"urlB\":\"null\",\"dataFrames\":{\"TravelerDataFrame\":[{\"sspTimRights\":\"0\",\"frameType\":{\"roadSignage\":\"EMPTY_TAG\"},\"msgId\":\"roadSignID\",\"position\":{\"latitude\":\"41.678473\",\"longitude\":\"-108.782775\",\"elevation\":\"917.1432\"},\"viewAngle\":\"1010101010101010\",\"mutcd\":\"5\",\"crc\":\"0000\",\"priority\":\"0\",\"sspLocationRights\":\"3\",\"regions\":{\"GeographicalPath\":[{\"name\":\"bob\",\"laneWidth\":700,\"directionality\":{\"reverse\":\"EMPTY_TAG\"},\"closedPath\":\"BOOLEAN_OBJECT_FALSE\",\"direction\":\"1010101010101010\",\"description\":{\"geometry\":{\"direction\":\"1010101010101010\",\"extent\":\"1\",\"laneWidth\":3300,\"circle\":{\"radius\":\"15\",\"units\":{\"mile\":\"EMPTY_TAG\"},\"center\":{\"lat\":416784730,\"long\":-1087827750,\"elevation\":9171}}}},\"id\":{\"region\":23,\"id\":33},\"anchor\":{\"lat\":416784730,\"long\":-1087827750,\"elevation\":9171}}]},\"url\":\"null\",\"sspMsgRights2\":3,\"sspMsgRights1\":2,\"duratonTime\":22,\"startYear\":2017,\"startTime\":482327,\"tcontent\":{\"advisory\":{\"SEQUENCE\":[{\"item\":{\"itis\":250}}]}}}]}}}");
    assertEquals(expectedTID.toString(), inputTID.toString());

    JSONObject timObject = new JSONObject();
    try {
      timObject.put(TravelerMessageFromHumanToAsnConverter.TRAVELER_INFORMATION,
          JsonUtils.toJSONObject(inputTID.toString()));
    } catch (JSONException e) {

      e.printStackTrace();
    } catch (JsonUtilsException e) {

      e.printStackTrace();
    }
    try {
      assertNotNull(XML.toString(timObject));
    } catch (JSONException e) {

      e.printStackTrace();
    }

  }

  @Test
  public void testRoadSignIDWorkzone() throws JsonUtilsException {
    ObjectNode inputTID = JsonUtils.toObjectNode(
        "{\"request\":{\"rsus\":[{\"rsuIndex\":\"10\",\"rsuTarget\":\"127.0.0.3\",\"rsuUsername\":\"v3user\",\"rsuPassword\":\"password\",\"rsuRetries\":\"1\",\"rsuTimeout\":\"1000\"}],\"snmp\":{\"rsuid\":\"00000083\",\"msgid\":\"31\",\"mode\":\"1\",\"channel\":\"178\",\"interval\":\"2\",\"deliverystart\":\"2017-06-01T17:47:11-05:00\",\"deliverystop\":\"2018-01-01T17:47:11-05:15\",\"enable\":\"1\",\"status\":\"4\"},\"sdw\":{\"ttl\":\"oneweek\",\"serviceRegion\":{\"nwCorner\":{\"latitude\":\"44.998459\",\"longitude\":\"-111.040817\"},\"seCorner\":{\"latitude\":\"41.104674\",\"longitude\":\"-104.111312\"}}}},\"tim\":{\"msgCnt\": \"2\", \"timeStamp\": \"2017-08-03T22:25:36.297Z\", \"urlB\": \"null\", \"packetID\": \"EC9C236B0000000000\", \"dataframes\": [ { \"startDateTime\": \"2017-08-02T22:25:00.000Z\", \"durationTime\": 1, \"sspTimRights\": \"0\", \"frameType\": \"commercialSignage\", \"msgId\": { \"roadSignID\": { \"position\": { \"latitude\": \"41.678473\", \"longitude\": \"-108.782775\", \"elevation\": \"917.1432\" }, \"viewAngle\": \"1010101010101010\", \"mutcdCode\": \"warning\", \"crc\": \"0000\" } }, \"priority\": \"0\", \"sspLocationRights\": \"3\", \"regions\": [ { \"name\": \"Testing TIM\", \"regulatorID\": \"0\", \"segmentID\": \"33\", \"anchorPosition\": { \"latitude\": \"41.2500807\", \"longitude\": \"-111.0093847\", \"elevation\": \"2020.6969900289998\" }, \"laneWidth\": \"7\", \"directionality\": \"3\", \"closedPath\": \"false\", \"description\": \"path\", \"path\": { \"scale\": \"0\", \"type\": \"ll\", \"nodes\": [ {\"nodeLong\":\"0.0002047\",\"nodeLat\":\"-0.0002048\",\"delta\":\"node-LL\",\"attributes\":{\"localNode\":[\"stopLine\",\"roundedCapStyleA\",\"roundedCapStyleB\",\"mergePoint\",\"divergePoint\",\"downstreamStopLine\",\"downstreamStartNode\",\"closedToTraffic\",\"safeIsland\",\"curbPresentAtStepOff\",\"hydrantPresent\",\"reserved\"],\"disabled\":[\"reserved\",\"doNotBlock\",\"whiteLine\",\"mergingLaneLeft\",\"mergingLaneRight\",\"curbOnLeft\",\"curbOnRight\",\"loadingzoneOnLeft\",\"loadingzoneOnRight\",\"turnOutPointOnLeft\",\"turnOutPointOnRight\"],\"enabled\":[\"adjacentParkingOnLeft\",\"adjacentParkingOnRight\",\"adjacentBikeLaneOnLeft\"],\"data\":[{\"pathEndPointAngle\":123},{\"laneCrownPointCenter\":12.3},{\"laneCrownPointLeft\":\"23.4\"},{\"laneCrownPointRight\":34.5},{\"laneAngle\":\"1.23\"},{\"speedLimits\":[{\"type\":\"2\",\"speed\":\"12.3\"},{\"type\":\"maxSpeedInSchoolZone\",\"speed\":\"23.4\"},{\"type\":3,\"speed\":12.3},{\"type\":\"vehicleMinSpeed\",\"speed\":23.4}]}],\"dWidth\":\"1.23\",\"dElevation\":\"2.34\"}}, { \"nodeLong\": \"0.0030974\", \"nodeLat\": \"0.0014568\", \"delta\": \"node-LL3\" }, { \"nodeLong\": \"0.0030983\", \"nodeLat\": \"0.0014559\", \"delta\": \"node-LL3\" }, { \"nodeLong\": \"0.0030980\", \"nodeLat\": \"0.0014563\", \"delta\": \"node-LL3\" }, { \"nodeLong\": \"0.0030982\", \"nodeLat\": \"0.0014562\", \"delta\": \"node-LL3\" } ] }, \"direction\": \"0000000000001010\" } ], \"sspMsgTypes\": \"2\", \"sspMsgContent\": \"3\", \"content\": \"workZone\", \"items\": [ \"513\" ], \"url\": \"null\" } ] }}");
    TravelerMessageFromHumanToAsnConverter.convertTravelerInputDataToEncodableTim(inputTID);

    ObjectNode expectedTID = JsonUtils.toObjectNode(
        "{\"request\":{\"rsus\":[{\"rsuIndex\":\"10\",\"rsuTarget\":\"127.0.0.3\",\"rsuUsername\":\"v3user\",\"rsuPassword\":\"password\",\"rsuRetries\":\"1\",\"rsuTimeout\":\"1000\"}],\"snmp\":{\"rsuid\":\"00000083\",\"msgid\":\"31\",\"mode\":\"1\",\"channel\":\"178\",\"interval\":\"2\",\"deliverystart\":\"2017-06-01T17:47:11-05:00\",\"deliverystop\":\"2018-01-01T17:47:11-05:15\",\"enable\":\"1\",\"status\":\"4\"},\"sdw\":{\"ttl\":\"oneweek\",\"serviceRegion\":{\"nwCorner\":{\"latitude\":\"44.998459\",\"longitude\":\"-111.040817\"},\"seCorner\":{\"latitude\":\"41.104674\",\"longitude\":\"-104.111312\"}}}},\"tim\":{\"msgCnt\":\"2\",\"timeStamp\":309505,\"urlB\":\"null\",\"packetID\":\"EC9C236B0000000000\",\"dataFrames\":{\"TravelerDataFrame\":[{\"sspTimRights\":\"0\",\"frameType\":{\"commercialSignage\":\"EMPTY_TAG\"},\"msgId\":{\"roadSignID\":{\"position\":{\"lat\":416784730,\"long\":-1087827750,\"elevation\":9171},\"viewAngle\":\"1010101010101010\",\"mutcdCode\":{\"warning\":\"EMPTY_TAG\"},\"crc\":\"0000\"}},\"priority\":\"0\",\"sspLocationRights\":\"3\",\"regions\":{\"GeographicalPath\":[{\"name\":\"Testing TIM\",\"laneWidth\":700,\"directionality\":{\"both\":\"EMPTY_TAG\"},\"closedPath\":\"BOOLEAN_OBJECT_FALSE\",\"description\":{\"path\":{\"scale\":\"0\",\"offset\":{\"ll\":{\"nodes\":{\"NodeLL\":[{\"delta\":{\"node-LL1\":{\"lat\":-2048,\"lon\":2047}}},{\"delta\":{\"node-LL3\":{\"lat\":14568,\"lon\":30974}}},{\"delta\":{\"node-LL3\":{\"lat\":14559,\"lon\":30983}}},{\"delta\":{\"node-LL3\":{\"lat\":14563,\"lon\":30980}}},{\"delta\":{\"node-LL3\":{\"lat\":14562,\"lon\":30982}}}]}}}}},\"direction\":\"0000000000001010\",\"id\":{\"region\":0,\"id\":33},\"anchor\":{\"lat\":412500807,\"long\":-1110093847,\"elevation\":20207}}]},\"url\":\"null\",\"sspMsgRights2\":3,\"sspMsgRights1\":2,\"duratonTime\":1,\"startYear\":2017,\"startTime\":308065,\"tcontent\":{\"workZone\":{\"SEQUENCE\":[{\"item\":{\"itis\":513}}]}}}]}}}");
    assertEquals(expectedTID.toString(), inputTID.toString());
    JSONObject timObject = new JSONObject();
    try {
      timObject.put(TravelerMessageFromHumanToAsnConverter.TRAVELER_INFORMATION,
          JsonUtils.toJSONObject(inputTID.toString()));
    } catch (JSONException e) {

      e.printStackTrace();
    } catch (JsonUtilsException e) {

      e.printStackTrace();
    }
    try {
      assertNotNull(XML.toString(timObject));
    } catch (JSONException e) {

      e.printStackTrace();
    }
  }

  @Test
  public void testGeometryBothGenericSign() throws JsonUtilsException {
    ObjectNode inputTID = JsonUtils.toObjectNode(
        "{\"request\":{\"rsus\":[{\"rsuIndex\":\"10\",\"rsuTarget\":\"127.0.0.3\",\"rsuUsername\":\"v3user\",\"rsuPassword\":\"password\",\"rsuRetries\":\"1\",\"rsuTimeout\":\"1000\"}],\"snmp\":{\"rsuid\":\"00000083\",\"msgid\":\"31\",\"mode\":\"1\",\"channel\":\"178\",\"interval\":\"2\",\"deliverystart\":\"2017-06-01T17:47:11-05:00\",\"deliverystop\":\"2018-01-01T17:47:11-05:15\",\"enable\":\"1\",\"status\":\"4\"},\"sdw\":{\"ttl\":\"oneweek\",\"serviceRegion\":{\"nwCorner\":{\"latitude\":\"44.998459\",\"longitude\":\"-111.040817\"},\"seCorner\":{\"latitude\":\"41.104674\",\"longitude\":\"-104.111312\"}}}}, \"tim\": { \"msgCnt\": \"13\", \"timeStamp\": \"2017-03-13T01:07:11-05:00\", \"packetID\": \"1\", \"urlB\": \"null\", \"dataframes\": [ { \"sspTimRights\": \"0\", \"frameType\": \"0\", \"msgId\": \"roadSignID\", \"position\": { \"latitude\": \"41.678473\", \"longitude\": \"-108.782775\", \"elevation\": \"917.1432\" }, \"viewAngle\": \"1010101010101010\", \"mutcd\": \"5\", \"crc\": \"0000\", \"startDateTime\": \"2017-12-01T17:47:11-05:00\", \"durationTime\": \"22\", \"priority\": \"0\", \"sspLocationRights\": \"3\", \"regions\": [ { \"name\": \"bob\", \"regulatorID\": \"23\", \"segmentID\": \"33\", \"anchorPosition\": { \"latitude\": \"41.678473\", \"longitude\": \"-108.782775\", \"elevation\": \"917.1432\" }, \"laneWidth\": \"7\", \"directionality\": \"3\", \"closedPath\": \"false\", \"direction\": \"1010101010101010\", \"description\": \"geometry\", \"geometry\": { \"direction\": \"1010101010101010\", \"extent\": \"1\", \"laneWidth\": \"33\", \"circle\": { \"position\": { \"latitude\": \"41.678473\", \"longitude\": \"-108.782775\", \"elevation\": \"917.1432\" }, \"radius\": \"15\", \"units\": \"7\" } } } ], \"sspMsgTypes\": \"2\", \"sspMsgContent\": \"3\", \"content\": \"genericSign\", \"items\": [ \"250\" ], \"url\": \"null\" } ] }}");
    TravelerMessageFromHumanToAsnConverter.convertTravelerInputDataToEncodableTim(inputTID);

    ObjectNode expectedTID = JsonUtils.toObjectNode(
        "{\"request\":{\"rsus\":[{\"rsuIndex\":\"10\",\"rsuTarget\":\"127.0.0.3\",\"rsuUsername\":\"v3user\",\"rsuPassword\":\"password\",\"rsuRetries\":\"1\",\"rsuTimeout\":\"1000\"}],\"snmp\":{\"rsuid\":\"00000083\",\"msgid\":\"31\",\"mode\":\"1\",\"channel\":\"178\",\"interval\":\"2\",\"deliverystart\":\"2017-06-01T17:47:11-05:00\",\"deliverystop\":\"2018-01-01T17:47:11-05:15\",\"enable\":\"1\",\"status\":\"4\"},\"sdw\":{\"ttl\":\"oneweek\",\"serviceRegion\":{\"nwCorner\":{\"latitude\":\"44.998459\",\"longitude\":\"-111.040817\"},\"seCorner\":{\"latitude\":\"41.104674\",\"longitude\":\"-104.111312\"}}}},\"tim\":{\"msgCnt\":\"13\",\"timeStamp\":102607,\"packetID\":\"1\",\"urlB\":\"null\",\"dataFrames\":{\"TravelerDataFrame\":[{\"sspTimRights\":\"0\",\"frameType\":{\"0\":\"EMPTY_TAG\"},\"msgId\":\"roadSignID\",\"position\":{\"latitude\":\"41.678473\",\"longitude\":\"-108.782775\",\"elevation\":\"917.1432\"},\"viewAngle\":\"1010101010101010\",\"mutcd\":\"5\",\"crc\":\"0000\",\"priority\":\"0\",\"sspLocationRights\":\"3\",\"regions\":{\"GeographicalPath\":[{\"name\":\"bob\",\"laneWidth\":700,\"directionality\":{\"both\":\"EMPTY_TAG\"},\"closedPath\":\"BOOLEAN_OBJECT_FALSE\",\"direction\":\"1010101010101010\",\"description\":{\"geometry\":{\"direction\":\"1010101010101010\",\"extent\":\"1\",\"laneWidth\":3300,\"circle\":{\"radius\":\"15\",\"units\":{\"mile\":\"EMPTY_TAG\"},\"center\":{\"lat\":416784730,\"long\":-1087827750,\"elevation\":9171}}}},\"id\":{\"region\":23,\"id\":33},\"anchor\":{\"lat\":416784730,\"long\":-1087827750,\"elevation\":9171}}]},\"url\":\"null\",\"sspMsgRights2\":3,\"sspMsgRights1\":2,\"duratonTime\":22,\"startYear\":2017,\"startTime\":482327,\"tcontent\":{\"genericSign\":{\"SEQUENCE\":[{\"item\":{\"itis\":250}}]}}}]}}}");
    assertEquals(expectedTID.toString(), inputTID.toString());

    JSONObject timObject = new JSONObject();
    try {
      timObject.put(TravelerMessageFromHumanToAsnConverter.TRAVELER_INFORMATION,
          JsonUtils.toJSONObject(inputTID.toString()));
    } catch (JSONException e) {

      e.printStackTrace();
    } catch (JsonUtilsException e) {

      e.printStackTrace();
    }
    try {
      assertNotNull(XML.toString(timObject));
    } catch (JSONException e) {

      e.printStackTrace();
    }

  }

  @Test
  public void testPathSpeedLimit() throws JsonUtilsException {
    ObjectNode inputTID = JsonUtils.toObjectNode(
        "{\"request\":{\"rsus\":[{\"rsuIndex\":\"10\",\"rsuTarget\":\"127.0.0.3\",\"rsuUsername\":\"v3user\",\"rsuPassword\":\"password\",\"rsuRetries\":\"1\",\"rsuTimeout\":\"1000\"}],\"snmp\":{\"rsuid\":\"00000083\",\"msgid\":\"31\",\"mode\":\"1\",\"channel\":\"178\",\"interval\":\"2\",\"deliverystart\":\"2017-06-01T17:47:11-05:00\",\"deliverystop\":\"2018-01-01T17:47:11-05:15\",\"enable\":\"1\",\"status\":\"4\"},\"sdw\":{\"ttl\":\"oneweek\",\"serviceRegion\":{\"nwCorner\":{\"latitude\":\"44.998459\",\"longitude\":\"-111.040817\"},\"seCorner\":{\"latitude\":\"41.104674\",\"longitude\":\"-104.111312\"}}}},\"tim\":{\"msgCnt\": \"1\", \"timeStamp\": \"2017-08-03T22:25:36.297Z\", \"urlB\": \"null\", \"packetID\": \"EC9C236B0000000000\", \"dataframes\": [ { \"startDateTime\": \"2017-08-02T22:25:00.000Z\", \"durationTime\": 1, \"sspTimRights\": \"0\", \"frameType\": \"advisory\", \"msgId\": { \"roadSignID\": { \"position\": { \"latitude\": \"41.678473\", \"longitude\": \"-108.782775\", \"elevation\": \"917.1432\" }, \"viewAngle\": \"1010101010101010\", \"mutcdCode\": \"warning\", \"crc\": \"0000\" } }, \"priority\": \"0\", \"sspLocationRights\": \"3\", \"regions\": [ { \"name\": \"Testing TIM\", \"regulatorID\": \"0\", \"segmentID\": \"33\", \"anchorPosition\": { \"latitude\": \"41.2500807\", \"longitude\": \"-111.0093847\", \"elevation\": \"2020.6969900289998\" }, \"laneWidth\": \"7\", \"directionality\": \"3\", \"closedPath\": \"false\", \"description\": \"path\", \"path\": { \"scale\": \"0\", \"type\": \"ll\", \"nodes\": [ { \"nodeLong\": \"0.0031024\", \"nodeLat\": \"0.0014506\", \"delta\": \"node-LL3\",\"attributes\":{\"localNode\":[\"stopLine\",\"roundedCapStyleA\",\"roundedCapStyleB\",\"mergePoint\",\"divergePoint\",\"downstreamStopLine\",\"downstreamStartNode\",\"closedToTraffic\",\"safeIsland\",\"curbPresentAtStepOff\",\"hydrantPresent\",\"reserved\"],\"disabled\":[\"reserved\",\"doNotBlock\",\"whiteLine\",\"mergingLaneLeft\",\"mergingLaneRight\",\"curbOnLeft\",\"curbOnRight\",\"loadingzoneOnLeft\",\"loadingzoneOnRight\",\"turnOutPointOnLeft\",\"turnOutPointOnRight\"],\"enabled\":[\"adjacentParkingOnLeft\",\"adjacentParkingOnRight\",\"adjacentBikeLaneOnLeft\"],\"data\":[{\"pathEndPointAngle\":\"123\"},{\"laneCrownPointCenter\":\"111\"},{\"laneCrownPointLeft\":\"5.5\"}],\"dWidth\":\"33\",\"dElevation\":\"500\"} }, { \"nodeLong\": \"0.0030974\", \"nodeLat\": \"0.0014568\", \"delta\": \"node-LL3\" }, { \"nodeLong\": \"0.0030983\", \"nodeLat\": \"0.0014559\", \"delta\": \"node-LL3\" }, { \"nodeLong\": \"0.0030980\", \"nodeLat\": \"0.0014563\", \"delta\": \"node-LL3\" }, { \"nodeLong\": \"0.0030982\", \"nodeLat\": \"0.0014562\", \"delta\": \"node-LL3\" } ] }, \"direction\": \"0000000000001010\" } ], \"sspMsgTypes\": \"2\", \"sspMsgContent\": \"3\", \"content\": \"speedLimit\", \"items\": [ \"513\" ], \"url\": \"null\" } ] }}");
    TravelerMessageFromHumanToAsnConverter.convertTravelerInputDataToEncodableTim(inputTID);

    ObjectNode expectedTID = JsonUtils.toObjectNode(
        "{\"request\":{\"rsus\":[{\"rsuIndex\":\"10\",\"rsuTarget\":\"127.0.0.3\",\"rsuUsername\":\"v3user\",\"rsuPassword\":\"password\",\"rsuRetries\":\"1\",\"rsuTimeout\":\"1000\"}],\"snmp\":{\"rsuid\":\"00000083\",\"msgid\":\"31\",\"mode\":\"1\",\"channel\":\"178\",\"interval\":\"2\",\"deliverystart\":\"2017-06-01T17:47:11-05:00\",\"deliverystop\":\"2018-01-01T17:47:11-05:15\",\"enable\":\"1\",\"status\":\"4\"},\"sdw\":{\"ttl\":\"oneweek\",\"serviceRegion\":{\"nwCorner\":{\"latitude\":\"44.998459\",\"longitude\":\"-111.040817\"},\"seCorner\":{\"latitude\":\"41.104674\",\"longitude\":\"-104.111312\"}}}},\"tim\":{\"msgCnt\":\"1\",\"timeStamp\":309505,\"urlB\":\"null\",\"packetID\":\"EC9C236B0000000000\",\"dataFrames\":{\"TravelerDataFrame\":[{\"sspTimRights\":\"0\",\"frameType\":{\"advisory\":\"EMPTY_TAG\"},\"msgId\":{\"roadSignID\":{\"position\":{\"lat\":416784730,\"long\":-1087827750,\"elevation\":9171},\"viewAngle\":\"1010101010101010\",\"mutcdCode\":{\"warning\":\"EMPTY_TAG\"},\"crc\":\"0000\"}},\"priority\":\"0\",\"sspLocationRights\":\"3\",\"regions\":{\"GeographicalPath\":[{\"name\":\"Testing TIM\",\"laneWidth\":700,\"directionality\":{\"both\":\"EMPTY_TAG\"},\"closedPath\":\"BOOLEAN_OBJECT_FALSE\",\"description\":{\"path\":{\"scale\":\"0\",\"offset\":{\"ll\":{\"nodes\":{\"NodeLL\":[{\"delta\":{\"node-LL3\":{\"lat\":14506,\"lon\":31024}}},{\"delta\":{\"node-LL3\":{\"lat\":14568,\"lon\":30974}}},{\"delta\":{\"node-LL3\":{\"lat\":14559,\"lon\":30983}}},{\"delta\":{\"node-LL3\":{\"lat\":14563,\"lon\":30980}}},{\"delta\":{\"node-LL3\":{\"lat\":14562,\"lon\":30982}}}]}}}}},\"direction\":\"0000000000001010\",\"id\":{\"region\":0,\"id\":33},\"anchor\":{\"lat\":412500807,\"long\":-1110093847,\"elevation\":20207}}]},\"url\":\"null\",\"sspMsgRights2\":3,\"sspMsgRights1\":2,\"duratonTime\":1,\"startYear\":2017,\"startTime\":308065,\"tcontent\":{\"speedLimit\":{\"SEQUENCE\":[{\"item\":{\"itis\":513}}]}}}]}}}");
    assertEquals(expectedTID.toString(), inputTID.toString());
    JSONObject timObject = new JSONObject();
    try {
      timObject.put(TravelerMessageFromHumanToAsnConverter.TRAVELER_INFORMATION,
          JsonUtils.toJSONObject(inputTID.toString()));
    } catch (JSONException e) {

      e.printStackTrace();
    } catch (JsonUtilsException e) {

      e.printStackTrace();
    }
    try {
      assertNotNull(XML.toString(timObject));
    } catch (JSONException e) {

      e.printStackTrace();
    }
  }

  @Test
  public void testTranslateISOTimeStampToMinuteOfYear() {
    assertEquals(232800,
        TravelerMessageFromHumanToAsnConverter.translateISOTimeStampToMinuteOfYear("2018-06-11T16:00:00.000Z"));

    assertEquals(232800,
        TravelerMessageFromHumanToAsnConverter.translateISOTimeStampToMinuteOfYear("2018-06-11T10:00-06:00"));

    // Test for invalid timestamp
    assertEquals(527040,
        TravelerMessageFromHumanToAsnConverter.translateISOTimeStampToMinuteOfYear("2018-15-44T25:66:77.999Z"));
  }

  @Test
  public void testReplaceDataFrameTimestamp() {
    String fieldName = "startDateTime";
    ObjectNode startDateTime = JsonUtils.newNode().put(fieldName, "2018-06-11T16:00:00.000Z");
    TravelerMessageFromHumanToAsnConverter.replaceDataFrameTimestamp(startDateTime);
    assertNull(startDateTime.get("startDateTime"));
    assertEquals(2018, startDateTime.get("startYear").asInt());
    assertEquals(232800, startDateTime.get("startTime").asLong());

    startDateTime = JsonUtils.newNode().put(fieldName, "2018-06-11T10:00-06:00");
    TravelerMessageFromHumanToAsnConverter.replaceDataFrameTimestamp(startDateTime);
    assertNull(startDateTime.get("startDateTime"));
    assertEquals(2018, startDateTime.get("startYear").asInt());
    assertEquals(232800, startDateTime.get("startTime").asLong());

    // Test for invalid timestamp
    startDateTime = JsonUtils.newNode().put(fieldName, "2018-15-44T25:66:77.999Z");
    TravelerMessageFromHumanToAsnConverter.replaceDataFrameTimestamp(startDateTime);
    assertNull(startDateTime.get("startDateTime"));
    assertEquals(0, startDateTime.get("startYear").asInt());
    assertEquals(527040, startDateTime.get("startTime").asLong());
  }

  @Test
  public void testBuildItem() {

    String itisCode = "123";
    String itis = "itis";
    ObjectNode expectedItisNode = JsonUtils.newNode().put(itis, Integer.parseInt(itisCode));
    ObjectNode expecteditem = (ObjectNode) JsonUtils.newNode().set("item", expectedItisNode);

    // build ITIS code
    JsonNode actualItem = TravelerMessageFromHumanToAsnConverter.buildItem(itisCode);
    assertEquals(expecteditem, actualItem);

    // build number text
    expectedItisNode.remove(itis);
    expectedItisNode.put("text", itisCode);
    actualItem = TravelerMessageFromHumanToAsnConverter.buildItem("'123");
    assertEquals(expecteditem, actualItem);

    // build alphanumeric text
    String test123 = "test 123";
    expectedItisNode.put("text", test123);
    actualItem = TravelerMessageFromHumanToAsnConverter.buildItem(test123);
    assertEquals(expecteditem, actualItem);
  }

  @Test
  public void testOldRegionWithShapePointSetWithNodeList() throws JsonUtilsException {
    ObjectNode inputTID = JsonUtils.toObjectNode(
        "{\"request\":{\"rsus\":[{\"rsuIndex\":\"10\",\"rsuTarget\":\"127.0.0.3\",\"rsuUsername\":\"v3user\",\"rsuPassword\":\"password\",\"rsuRetries\":\"1\",\"rsuTimeout\":\"1000\"}],\"snmp\":{\"rsuid\":\"00000083\",\"msgid\":\"31\",\"mode\":\"1\",\"channel\":\"178\",\"interval\":\"2\",\"deliverystart\":\"2017-06-01T17:47:11-05:00\",\"deliverystop\":\"2018-01-01T17:47:11-05:15\",\"enable\":\"1\",\"status\":\"4\"},\"sdw\":{\"ttl\":\"oneweek\",\"serviceRegion\":{\"nwCorner\":{\"latitude\":\"44.998459\",\"longitude\":\"-111.040817\"},\"seCorner\":{\"latitude\":\"41.104674\",\"longitude\":\"-104.111312\"}}}},\"tim\":{\"msgCnt\":\"13\",\"timeStamp\":\"2017-03-13T01:07:11-05:00\",\"packetID\":\"EC9C236B0000000000\",\"urlB\":\"null\",\"dataframes\":[{\"sspTimRights\":\"0\",\"frameType\":\"advisory\",\"msgId\":{\"roadSignID\":{\"position\":{\"latitude\":\"41.678473\",\"longitude\":\"-108.782775\",\"elevation\":\"917.1432\"},\"viewAngle\":\"1010101010101010\",\"mutcdCode\":\"warning\",\"crc\":\"0000\"}},\"startDateTime\":\"2017-12-01T17:47:11-05:00\",\"durationTime\":\"22\",\"priority\":\"0\",\"sspLocationRights\":\"3\",\"regions\":[{\"name\":\"bob\",\"regulatorID\":\"23\",\"segmentID\":\"33\",\"anchorPosition\":{\"latitude\":\"41.678473\",\"longitude\":\"-108.782775\",\"elevation\":\"917.1432\"},\"laneWidth\":\"7\",\"directionality\":\"3\",\"closedPath\":\"false\",\"direction\":\"1010101010101010\",\"description\":\"oldRegion\",\"oldRegion\":{\"direction\":\"1010101010101010\",\"extent\":\"1\",\"area\":{\"shapePointSet\":{\"anchor\":{\"latitude\":\"41.678473\",\"longitude\":\"-108.782775\",\"elevation\":\"917.1432\"},\"laneWidth\":\"33\",\"directionality\":\"3\",\"nodeList\":{\"nodes\":[{\"x\":\"-5.12\",\"y\":\"5.11\",\"delta\":\"node-XY\"},{\"x\":\"-10.24\",\"y\":\"10.23\",\"delta\":\"node-XY\"},{\"x\":\"-20.48\",\"y\":\"20.47\",\"delta\":\"node-XY\"},{\"x\":\"-40.96\",\"y\":\"40.95\",\"delta\":\"node-XY\"},{\"x\":\"-81.92\",\"y\":\"81.91\",\"delta\":\"node-XY\"},{\"x\":\"-327.68\",\"y\":\"327.67\",\"delta\":\"node-XY\"},{\"nodeLong\":\"-105.045087\",\"nodeLat\":\"40.569848\",\"delta\":\"node-LatLon\"}]}}}}}],\"sspMsgTypes\":\"2\",\"sspMsgContent\":\"3\",\"content\":\"exitService\",\"items\":[\"125\",\"some text\",\"250\",\"'98765\"],\"url\":\"null\"}]}}");
    TravelerMessageFromHumanToAsnConverter.convertTravelerInputDataToEncodableTim(inputTID);

    ObjectNode expectedTID = JsonUtils.toObjectNode(
        "{\"request\":{\"rsus\":[{\"rsuIndex\":\"10\",\"rsuTarget\":\"127.0.0.3\",\"rsuUsername\":\"v3user\",\"rsuPassword\":\"password\",\"rsuRetries\":\"1\",\"rsuTimeout\":\"1000\"}],\"snmp\":{\"rsuid\":\"00000083\",\"msgid\":\"31\",\"mode\":\"1\",\"channel\":\"178\",\"interval\":\"2\",\"deliverystart\":\"2017-06-01T17:47:11-05:00\",\"deliverystop\":\"2018-01-01T17:47:11-05:15\",\"enable\":\"1\",\"status\":\"4\"},\"sdw\":{\"ttl\":\"oneweek\",\"serviceRegion\":{\"nwCorner\":{\"latitude\":\"44.998459\",\"longitude\":\"-111.040817\"},\"seCorner\":{\"latitude\":\"41.104674\",\"longitude\":\"-104.111312\"}}}},\"tim\":{\"msgCnt\":\"13\",\"timeStamp\":102607,\"packetID\":\"EC9C236B0000000000\",\"urlB\":\"null\",\"dataFrames\":{\"TravelerDataFrame\":[{\"sspTimRights\":\"0\",\"frameType\":{\"advisory\":\"EMPTY_TAG\"},\"msgId\":{\"roadSignID\":{\"position\":{\"lat\":416784730,\"long\":-1087827750,\"elevation\":9171},\"viewAngle\":\"1010101010101010\",\"mutcdCode\":{\"warning\":\"EMPTY_TAG\"},\"crc\":\"0000\"}},\"priority\":\"0\",\"sspLocationRights\":\"3\",\"regions\":{\"GeographicalPath\":[{\"name\":\"bob\",\"laneWidth\":700,\"directionality\":{\"both\":\"EMPTY_TAG\"},\"closedPath\":\"BOOLEAN_OBJECT_FALSE\",\"direction\":\"1010101010101010\",\"description\":{\"oldRegion\":{\"direction\":\"1010101010101010\",\"extent\":{\"useFor3meters\":\"EMPTY_TAG\"},\"area\":{\"shapePointSet\":{\"anchor\":{\"lat\":416784730,\"long\":-1087827750,\"elevation\":9171},\"laneWidth\":3300,\"directionality\":{\"both\":\"EMPTY_TAG\"},\"nodeList\":{\"nodes\":[{\"delta\":{\"node-XY1\":{\"x\":-512,\"y\":511}}},{\"delta\":{\"node-XY2\":{\"x\":-1024,\"y\":1023}}},{\"delta\":{\"node-XY3\":{\"x\":-2048,\"y\":2047}}},{\"delta\":{\"node-XY4\":{\"x\":-4096,\"y\":4095}}},{\"delta\":{\"node-XY5\":{\"x\":-8192,\"y\":8191}}},{\"delta\":{\"node-XY6\":{\"x\":-32768,\"y\":32767}}},{\"delta\":{\"node-LatLon\":{\"lon\":-1050450870,\"lat\":405698480}}}]}}}}},\"id\":{\"region\":23,\"id\":33},\"anchor\":{\"lat\":416784730,\"long\":-1087827750,\"elevation\":9171}}]},\"url\":\"null\",\"sspMsgRights2\":3,\"sspMsgRights1\":2,\"duratonTime\":22,\"startYear\":2017,\"startTime\":482327,\"tcontent\":{\"exitService\":{\"SEQUENCE\":[{\"item\":{\"itis\":125}},{\"item\":{\"text\":\"some text\"}},{\"item\":{\"itis\":250}},{\"item\":{\"text\":\"98765\"}}]}}}]}}}");
    assertEquals(expectedTID.toString(), inputTID.toString());

    JSONObject timObject = new JSONObject();
    try {
      timObject.put(TravelerMessageFromHumanToAsnConverter.TRAVELER_INFORMATION,
          JsonUtils.toJSONObject(inputTID.toString()));
    } catch (JSONException e) {

      e.printStackTrace();
    } catch (JsonUtilsException e) {

      e.printStackTrace();
    }
    try {
      assertNotNull(XML.toString(timObject));
    } catch (JSONException e) {

      e.printStackTrace();
    }

  }

  @Test
  public void testOldRegionWithShapePointSetWithComputedLanesSmall() throws JsonUtilsException {
    ObjectNode inputTID = JsonUtils.toObjectNode(
        "{\"request\":{\"rsus\":[{\"rsuIndex\":\"10\",\"rsuTarget\":\"127.0.0.3\",\"rsuUsername\":\"v3user\",\"rsuPassword\":\"password\",\"rsuRetries\":\"1\",\"rsuTimeout\":\"1000\"}],\"snmp\":{\"rsuid\":\"00000083\",\"msgid\":\"31\",\"mode\":\"1\",\"channel\":\"178\",\"interval\":\"2\",\"deliverystart\":\"2017-06-01T17:47:11-05:00\",\"deliverystop\":\"2018-01-01T17:47:11-05:15\",\"enable\":\"1\",\"status\":\"4\"},\"sdw\":{\"ttl\":\"oneweek\",\"serviceRegion\":{\"nwCorner\":{\"latitude\":\"44.998459\",\"longitude\":\"-111.040817\"},\"seCorner\":{\"latitude\":\"41.104674\",\"longitude\":\"-104.111312\"}}}},\"tim\":{\"msgCnt\":\"13\",\"timeStamp\":\"2017-03-13T01:07:11-05:00\",\"packetID\":\"EC9C236B0000000000\",\"urlB\":\"null\",\"dataframes\":[{\"sspTimRights\":\"0\",\"frameType\":\"advisory\",\"msgId\":{\"roadSignID\":{\"position\":{\"latitude\":\"41.678473\",\"longitude\":\"-108.782775\",\"elevation\":\"917.1432\"},\"viewAngle\":\"1010101010101010\",\"mutcdCode\":\"warning\",\"crc\":\"0000\"}},\"startDateTime\":\"2017-12-01T17:47:11-05:00\",\"durationTime\":\"22\",\"priority\":\"0\",\"sspLocationRights\":\"3\",\"regions\":[{\"name\":\"bob\",\"regulatorID\":\"23\",\"segmentID\":\"33\",\"anchorPosition\":{\"latitude\":\"41.678473\",\"longitude\":\"-108.782775\",\"elevation\":\"917.1432\"},\"laneWidth\":\"7\",\"directionality\":\"3\",\"closedPath\":\"false\",\"direction\":\"1010101010101010\",\"description\":\"oldRegion\",\"oldRegion\":{\"direction\":\"1010101010101010\",\"extent\":\"1\",\"area\":{\"shapePointSet\":{\"anchor\":{\"latitude\":\"41.678473\",\"longitude\":\"-108.782775\",\"elevation\":\"917.1432\"},\"laneWidth\":\"33\",\"directionality\":\"3\",\"nodeList\":{\"computed\":{\"referenceLaneId\":\"123\",\"offsetXaxis\":\"111\",\"offsetYaxis\":\"111\",\"rotateXY\":\"123.45\",\"scaleXaxis\":\"123.45\",\"scaleYaxis\":\"123.45\"}}}}}}],\"sspMsgTypes\":\"2\",\"sspMsgContent\":\"3\",\"content\":\"exitService\",\"items\":[\"125\",\"some text\",\"250\",\"'98765\"],\"url\":\"null\"}]}}");
    TravelerMessageFromHumanToAsnConverter.convertTravelerInputDataToEncodableTim(inputTID);

    ObjectNode expectedTID = JsonUtils.toObjectNode(
        "{\"request\":{\"rsus\":[{\"rsuIndex\":\"10\",\"rsuTarget\":\"127.0.0.3\",\"rsuUsername\":\"v3user\",\"rsuPassword\":\"password\",\"rsuRetries\":\"1\",\"rsuTimeout\":\"1000\"}],\"snmp\":{\"rsuid\":\"00000083\",\"msgid\":\"31\",\"mode\":\"1\",\"channel\":\"178\",\"interval\":\"2\",\"deliverystart\":\"2017-06-01T17:47:11-05:00\",\"deliverystop\":\"2018-01-01T17:47:11-05:15\",\"enable\":\"1\",\"status\":\"4\"},\"sdw\":{\"ttl\":\"oneweek\",\"serviceRegion\":{\"nwCorner\":{\"latitude\":\"44.998459\",\"longitude\":\"-111.040817\"},\"seCorner\":{\"latitude\":\"41.104674\",\"longitude\":\"-104.111312\"}}}},\"tim\":{\"msgCnt\":\"13\",\"timeStamp\":102607,\"packetID\":\"EC9C236B0000000000\",\"urlB\":\"null\",\"dataFrames\":{\"TravelerDataFrame\":[{\"sspTimRights\":\"0\",\"frameType\":{\"advisory\":\"EMPTY_TAG\"},\"msgId\":{\"roadSignID\":{\"position\":{\"lat\":416784730,\"long\":-1087827750,\"elevation\":9171},\"viewAngle\":\"1010101010101010\",\"mutcdCode\":{\"warning\":\"EMPTY_TAG\"},\"crc\":\"0000\"}},\"priority\":\"0\",\"sspLocationRights\":\"3\",\"regions\":{\"GeographicalPath\":[{\"name\":\"bob\",\"laneWidth\":700,\"directionality\":{\"both\":\"EMPTY_TAG\"},\"closedPath\":\"BOOLEAN_OBJECT_FALSE\",\"direction\":\"1010101010101010\",\"description\":{\"oldRegion\":{\"direction\":\"1010101010101010\",\"extent\":{\"useFor3meters\":\"EMPTY_TAG\"},\"area\":{\"shapePointSet\":{\"anchor\":{\"lat\":416784730,\"long\":-1087827750,\"elevation\":9171},\"laneWidth\":3300,\"directionality\":{\"both\":\"EMPTY_TAG\"},\"nodeList\":{\"computed\":{\"referenceLaneId\":\"123\",\"offsetXaxis\":{\"small\":111},\"offsetYaxis\":{\"small\":111},\"rotateXY\":9876,\"scaleXaxis\":469,\"scaleYaxis\":469}}}}}},\"id\":{\"region\":23,\"id\":33},\"anchor\":{\"lat\":416784730,\"long\":-1087827750,\"elevation\":9171}}]},\"url\":\"null\",\"sspMsgRights2\":3,\"sspMsgRights1\":2,\"duratonTime\":22,\"startYear\":2017,\"startTime\":482327,\"tcontent\":{\"exitService\":{\"SEQUENCE\":[{\"item\":{\"itis\":125}},{\"item\":{\"text\":\"some text\"}},{\"item\":{\"itis\":250}},{\"item\":{\"text\":\"98765\"}}]}}}]}}}");
    assertEquals(expectedTID.toString(), inputTID.toString());

    JSONObject timObject = new JSONObject();
    try {
      timObject.put(TravelerMessageFromHumanToAsnConverter.TRAVELER_INFORMATION,
          JsonUtils.toJSONObject(inputTID.toString()));
    } catch (JSONException e) {

      e.printStackTrace();
    } catch (JsonUtilsException e) {

      e.printStackTrace();
    }
    try {
      assertNotNull(XML.toString(timObject));
    } catch (JSONException e) {

      e.printStackTrace();
    }

  }

  @Test
  public void testOldRegionWithShapePointSetWithComputedLanesLarge() throws JsonUtilsException {
    ObjectNode inputTID = JsonUtils.toObjectNode(
        "{\"request\":{\"rsus\":[{\"rsuIndex\":\"10\",\"rsuTarget\":\"127.0.0.3\",\"rsuUsername\":\"v3user\",\"rsuPassword\":\"password\",\"rsuRetries\":\"1\",\"rsuTimeout\":\"1000\"}],\"snmp\":{\"rsuid\":\"00000083\",\"msgid\":\"31\",\"mode\":\"1\",\"channel\":\"178\",\"interval\":\"2\",\"deliverystart\":\"2017-06-01T17:47:11-05:00\",\"deliverystop\":\"2018-01-01T17:47:11-05:15\",\"enable\":\"1\",\"status\":\"4\"},\"sdw\":{\"ttl\":\"oneweek\",\"serviceRegion\":{\"nwCorner\":{\"latitude\":\"44.998459\",\"longitude\":\"-111.040817\"},\"seCorner\":{\"latitude\":\"41.104674\",\"longitude\":\"-104.111312\"}}}},\"tim\":{\"msgCnt\":\"13\",\"timeStamp\":\"2017-03-13T01:07:11-05:00\",\"packetID\":\"EC9C236B0000000000\",\"urlB\":\"null\",\"dataframes\":[{\"sspTimRights\":\"0\",\"frameType\":\"advisory\",\"msgId\":{\"roadSignID\":{\"position\":{\"latitude\":\"41.678473\",\"longitude\":\"-108.782775\",\"elevation\":\"917.1432\"},\"viewAngle\":\"1010101010101010\",\"mutcdCode\":\"warning\",\"crc\":\"0000\"}},\"startDateTime\":\"2017-12-01T17:47:11-05:00\",\"durationTime\":\"22\",\"priority\":\"0\",\"sspLocationRights\":\"3\",\"regions\":[{\"name\":\"bob\",\"regulatorID\":\"23\",\"segmentID\":\"33\",\"anchorPosition\":{\"latitude\":\"41.678473\",\"longitude\":\"-108.782775\",\"elevation\":\"917.1432\"},\"laneWidth\":\"7\",\"directionality\":\"3\",\"closedPath\":\"false\",\"direction\":\"1010101010101010\",\"description\":\"oldRegion\",\"oldRegion\":{\"direction\":\"1010101010101010\",\"extent\":\"1\",\"area\":{\"shapePointSet\":{\"anchor\":{\"latitude\":\"41.678473\",\"longitude\":\"-108.782775\",\"elevation\":\"917.1432\"},\"laneWidth\":\"33\",\"directionality\":\"3\",\"nodeList\":{\"computed\":{\"referenceLaneId\":\"123\",\"offsetXaxis\":\"11111\",\"offsetYaxis\":\"11111\",\"rotateXY\":\"123.45\",\"scaleXaxis\":\"123.45\",\"scaleYaxis\":\"123.45\"}}}}}}],\"sspMsgTypes\":\"2\",\"sspMsgContent\":\"3\",\"content\":\"exitService\",\"items\":[\"125\",\"some text\",\"250\",\"'98765\"],\"url\":\"null\"}]}}");
    TravelerMessageFromHumanToAsnConverter.convertTravelerInputDataToEncodableTim(inputTID);

    ObjectNode expectedTID = JsonUtils.toObjectNode(
        "{\"request\":{\"rsus\":[{\"rsuIndex\":\"10\",\"rsuTarget\":\"127.0.0.3\",\"rsuUsername\":\"v3user\",\"rsuPassword\":\"password\",\"rsuRetries\":\"1\",\"rsuTimeout\":\"1000\"}],\"snmp\":{\"rsuid\":\"00000083\",\"msgid\":\"31\",\"mode\":\"1\",\"channel\":\"178\",\"interval\":\"2\",\"deliverystart\":\"2017-06-01T17:47:11-05:00\",\"deliverystop\":\"2018-01-01T17:47:11-05:15\",\"enable\":\"1\",\"status\":\"4\"},\"sdw\":{\"ttl\":\"oneweek\",\"serviceRegion\":{\"nwCorner\":{\"latitude\":\"44.998459\",\"longitude\":\"-111.040817\"},\"seCorner\":{\"latitude\":\"41.104674\",\"longitude\":\"-104.111312\"}}}},\"tim\":{\"msgCnt\":\"13\",\"timeStamp\":102607,\"packetID\":\"EC9C236B0000000000\",\"urlB\":\"null\",\"dataFrames\":{\"TravelerDataFrame\":[{\"sspTimRights\":\"0\",\"frameType\":{\"advisory\":\"EMPTY_TAG\"},\"msgId\":{\"roadSignID\":{\"position\":{\"lat\":416784730,\"long\":-1087827750,\"elevation\":9171},\"viewAngle\":\"1010101010101010\",\"mutcdCode\":{\"warning\":\"EMPTY_TAG\"},\"crc\":\"0000\"}},\"priority\":\"0\",\"sspLocationRights\":\"3\",\"regions\":{\"GeographicalPath\":[{\"name\":\"bob\",\"laneWidth\":700,\"directionality\":{\"both\":\"EMPTY_TAG\"},\"closedPath\":\"BOOLEAN_OBJECT_FALSE\",\"direction\":\"1010101010101010\",\"description\":{\"oldRegion\":{\"direction\":\"1010101010101010\",\"extent\":{\"useFor3meters\":\"EMPTY_TAG\"},\"area\":{\"shapePointSet\":{\"anchor\":{\"lat\":416784730,\"long\":-1087827750,\"elevation\":9171},\"laneWidth\":3300,\"directionality\":{\"both\":\"EMPTY_TAG\"},\"nodeList\":{\"computed\":{\"referenceLaneId\":\"123\",\"offsetXaxis\":{\"large\":11111},\"offsetYaxis\":{\"large\":11111},\"rotateXY\":9876,\"scaleXaxis\":469,\"scaleYaxis\":469}}}}}},\"id\":{\"region\":23,\"id\":33},\"anchor\":{\"lat\":416784730,\"long\":-1087827750,\"elevation\":9171}}]},\"url\":\"null\",\"sspMsgRights2\":3,\"sspMsgRights1\":2,\"duratonTime\":22,\"startYear\":2017,\"startTime\":482327,\"tcontent\":{\"exitService\":{\"SEQUENCE\":[{\"item\":{\"itis\":125}},{\"item\":{\"text\":\"some text\"}},{\"item\":{\"itis\":250}},{\"item\":{\"text\":\"98765\"}}]}}}]}}}");
    assertEquals(expectedTID.toString(), inputTID.toString());

    JSONObject timObject = new JSONObject();
    try {
      timObject.put(TravelerMessageFromHumanToAsnConverter.TRAVELER_INFORMATION,
          JsonUtils.toJSONObject(inputTID.toString()));
    } catch (JSONException e) {

      e.printStackTrace();
    } catch (JsonUtilsException e) {

      e.printStackTrace();
    }
    try {
      assertNotNull(XML.toString(timObject));
    } catch (JSONException e) {

      e.printStackTrace();
    }

  }

  @Test
  public void testOldRegionWithCircle() throws JsonUtilsException {
    ObjectNode inputTID = JsonUtils.toObjectNode(
        "{\"request\":{\"rsus\":[{\"rsuIndex\":\"10\",\"rsuTarget\":\"127.0.0.3\",\"rsuUsername\":\"v3user\",\"rsuPassword\":\"password\",\"rsuRetries\":\"1\",\"rsuTimeout\":\"1000\"}],\"snmp\":{\"rsuid\":\"00000083\",\"msgid\":\"31\",\"mode\":\"1\",\"channel\":\"178\",\"interval\":\"2\",\"deliverystart\":\"2017-06-01T17:47:11-05:00\",\"deliverystop\":\"2018-01-01T17:47:11-05:15\",\"enable\":\"1\",\"status\":\"4\"},\"sdw\":{\"ttl\":\"oneweek\",\"serviceRegion\":{\"nwCorner\":{\"latitude\":\"44.998459\",\"longitude\":\"-111.040817\"},\"seCorner\":{\"latitude\":\"41.104674\",\"longitude\":\"-104.111312\"}}}},\"tim\":{\"msgCnt\":\"13\",\"timeStamp\":\"2017-03-13T01:07:11-05:00\",\"packetID\":\"EC9C236B0000000000\",\"urlB\":\"null\",\"dataframes\":[{\"sspTimRights\":\"0\",\"frameType\":\"advisory\",\"msgId\":{\"roadSignID\":{\"position\":{\"latitude\":\"41.678473\",\"longitude\":\"-108.782775\",\"elevation\":\"917.1432\"},\"viewAngle\":\"1010101010101010\",\"mutcdCode\":\"warning\",\"crc\":\"0000\"}},\"startDateTime\":\"2017-12-01T17:47:11-05:00\",\"durationTime\":\"22\",\"priority\":\"0\",\"sspLocationRights\":\"3\",\"regions\":[{\"name\":\"bob\",\"regulatorID\":\"23\",\"segmentID\":\"33\",\"anchorPosition\":{\"latitude\":\"41.678473\",\"longitude\":\"-108.782775\",\"elevation\":\"917.1432\"},\"laneWidth\":\"7\",\"directionality\":\"3\",\"closedPath\":\"false\",\"direction\":\"1010101010101010\",\"description\":\"oldRegion\",\"oldRegion\":{\"direction\":\"1010101010101010\",\"extent\":\"1\",\"area\":{\"circle\":{\"center\":{\"latitude\":\"41.678473\",\"longitude\":\"-108.782775\",\"elevation\":\"917.1432\"},\"radius\":\"2048\",\"units\":\"centimeter\"}}}}],\"sspMsgTypes\":\"2\",\"sspMsgContent\":\"3\",\"content\":\"advisory\",\"items\":[\"125\",\"some text\",\"250\",\"'98765\"],\"url\":\"null\"}]}}");
    TravelerMessageFromHumanToAsnConverter.convertTravelerInputDataToEncodableTim(inputTID);

    ObjectNode expectedTID = JsonUtils.toObjectNode(
        "{\"request\":{\"rsus\":[{\"rsuIndex\":\"10\",\"rsuTarget\":\"127.0.0.3\",\"rsuUsername\":\"v3user\",\"rsuPassword\":\"password\",\"rsuRetries\":\"1\",\"rsuTimeout\":\"1000\"}],\"snmp\":{\"rsuid\":\"00000083\",\"msgid\":\"31\",\"mode\":\"1\",\"channel\":\"178\",\"interval\":\"2\",\"deliverystart\":\"2017-06-01T17:47:11-05:00\",\"deliverystop\":\"2018-01-01T17:47:11-05:15\",\"enable\":\"1\",\"status\":\"4\"},\"sdw\":{\"ttl\":\"oneweek\",\"serviceRegion\":{\"nwCorner\":{\"latitude\":\"44.998459\",\"longitude\":\"-111.040817\"},\"seCorner\":{\"latitude\":\"41.104674\",\"longitude\":\"-104.111312\"}}}},\"tim\":{\"msgCnt\":\"13\",\"timeStamp\":102607,\"packetID\":\"EC9C236B0000000000\",\"urlB\":\"null\",\"dataFrames\":{\"TravelerDataFrame\":[{\"sspTimRights\":\"0\",\"frameType\":{\"advisory\":\"EMPTY_TAG\"},\"msgId\":{\"roadSignID\":{\"position\":{\"lat\":416784730,\"long\":-1087827750,\"elevation\":9171},\"viewAngle\":\"1010101010101010\",\"mutcdCode\":{\"warning\":\"EMPTY_TAG\"},\"crc\":\"0000\"}},\"priority\":\"0\",\"sspLocationRights\":\"3\",\"regions\":{\"GeographicalPath\":[{\"name\":\"bob\",\"laneWidth\":700,\"directionality\":{\"both\":\"EMPTY_TAG\"},\"closedPath\":\"BOOLEAN_OBJECT_FALSE\",\"direction\":\"1010101010101010\",\"description\":{\"oldRegion\":{\"direction\":\"1010101010101010\",\"extent\":{\"useFor3meters\":\"EMPTY_TAG\"},\"area\":{\"circle\":{\"center\":{\"lat\":416784730,\"long\":-1087827750,\"elevation\":9171},\"radius\":\"2048\",\"units\":{\"centimeter\":\"EMPTY_TAG\"}}}}},\"id\":{\"region\":23,\"id\":33},\"anchor\":{\"lat\":416784730,\"long\":-1087827750,\"elevation\":9171}}]},\"url\":\"null\",\"sspMsgRights2\":3,\"sspMsgRights1\":2,\"duratonTime\":22,\"startYear\":2017,\"startTime\":482327,\"tcontent\":{\"advisory\":{\"SEQUENCE\":[{\"item\":{\"itis\":125}},{\"item\":{\"text\":\"some text\"}},{\"item\":{\"itis\":250}},{\"item\":{\"text\":\"98765\"}}]}}}]}}}");
    assertEquals(expectedTID.toString(), inputTID.toString());

    JSONObject timObject = new JSONObject();
    try {
      timObject.put(TravelerMessageFromHumanToAsnConverter.TRAVELER_INFORMATION,
          JsonUtils.toJSONObject(inputTID.toString()));
    } catch (JSONException e) {

      e.printStackTrace();
    } catch (JsonUtilsException e) {

      e.printStackTrace();
    }
    try {
      assertNotNull(XML.toString(timObject));
    } catch (JSONException e) {

      e.printStackTrace();
    }

  }

  @Test
  public void testOldRegionWithRegionPointSet() throws JsonUtilsException {
    ObjectNode inputTID = JsonUtils.toObjectNode(
        "{\"request\":{\"rsus\":[{\"rsuIndex\":\"10\",\"rsuTarget\":\"127.0.0.3\",\"rsuUsername\":\"v3user\",\"rsuPassword\":\"password\",\"rsuRetries\":\"1\",\"rsuTimeout\":\"1000\"}],\"snmp\":{\"rsuid\":\"00000083\",\"msgid\":\"31\",\"mode\":\"1\",\"channel\":\"178\",\"interval\":\"2\",\"deliverystart\":\"2017-06-01T17:47:11-05:00\",\"deliverystop\":\"2018-01-01T17:47:11-05:15\",\"enable\":\"1\",\"status\":\"4\"},\"sdw\":{\"ttl\":\"oneweek\",\"serviceRegion\":{\"nwCorner\":{\"latitude\":\"44.998459\",\"longitude\":\"-111.040817\"},\"seCorner\":{\"latitude\":\"41.104674\",\"longitude\":\"-104.111312\"}}}},\"tim\":{\"msgCnt\":\"13\",\"timeStamp\":\"2017-03-13T01:07:11-05:00\",\"packetID\":\"EC9C236B0000000000\",\"urlB\":\"null\",\"dataframes\":[{\"sspTimRights\":\"0\",\"frameType\":\"advisory\",\"msgId\":{\"roadSignID\":{\"position\":{\"latitude\":\"41.678473\",\"longitude\":\"-108.782775\",\"elevation\":\"917.1432\"},\"viewAngle\":\"1010101010101010\",\"mutcdCode\":\"warning\",\"crc\":\"0000\"}},\"startDateTime\":\"2017-12-01T17:47:11-05:00\",\"durationTime\":\"22\",\"priority\":\"0\",\"sspLocationRights\":\"3\",\"regions\":[{\"name\":\"bob\",\"regulatorID\":\"23\",\"segmentID\":\"33\",\"anchorPosition\":{\"latitude\":\"41.678473\",\"longitude\":\"-108.782775\",\"elevation\":\"917.1432\"},\"laneWidth\":\"7\",\"directionality\":\"3\",\"closedPath\":\"false\",\"direction\":\"1010101010101010\",\"description\":\"oldRegion\",\"oldRegion\":{\"direction\":\"1010101010101010\",\"extent\":\"1\",\"area\":{\"regionPointSet\":{\"anchor\":{\"latitude\":\"41.678473\",\"longitude\":\"-108.782775\",\"elevation\":\"917.1432\"},\"scale\":\"0\",\"nodeList\":[{\"xOffset\":\"-0.003\",\"yOffset\":\"0.003\",\"zOffset\":\"0\"},{\"xOffset\":\"-0.002\",\"yOffset\":\"0.002\",\"zOffset\":\"0\"},{\"xOffset\":\"-0.001\",\"yOffset\":\"0.001\",\"zOffset\":\"0\"}]}}}}],\"sspMsgTypes\":\"2\",\"sspMsgContent\":\"3\",\"content\":\"advisory\",\"items\":[\"125\",\"some text\",\"250\",\"'98765\"],\"url\":\"null\"}]}}");
    TravelerMessageFromHumanToAsnConverter.convertTravelerInputDataToEncodableTim(inputTID);

    ObjectNode expectedTID = JsonUtils.toObjectNode(
        "{\"request\":{\"rsus\":[{\"rsuIndex\":\"10\",\"rsuTarget\":\"127.0.0.3\",\"rsuUsername\":\"v3user\",\"rsuPassword\":\"password\",\"rsuRetries\":\"1\",\"rsuTimeout\":\"1000\"}],\"snmp\":{\"rsuid\":\"00000083\",\"msgid\":\"31\",\"mode\":\"1\",\"channel\":\"178\",\"interval\":\"2\",\"deliverystart\":\"2017-06-01T17:47:11-05:00\",\"deliverystop\":\"2018-01-01T17:47:11-05:15\",\"enable\":\"1\",\"status\":\"4\"},\"sdw\":{\"ttl\":\"oneweek\",\"serviceRegion\":{\"nwCorner\":{\"latitude\":\"44.998459\",\"longitude\":\"-111.040817\"},\"seCorner\":{\"latitude\":\"41.104674\",\"longitude\":\"-104.111312\"}}}},\"tim\":{\"msgCnt\":\"13\",\"timeStamp\":102607,\"packetID\":\"EC9C236B0000000000\",\"urlB\":\"null\",\"dataFrames\":{\"TravelerDataFrame\":[{\"sspTimRights\":\"0\",\"frameType\":{\"advisory\":\"EMPTY_TAG\"},\"msgId\":{\"roadSignID\":{\"position\":{\"lat\":416784730,\"long\":-1087827750,\"elevation\":9171},\"viewAngle\":\"1010101010101010\",\"mutcdCode\":{\"warning\":\"EMPTY_TAG\"},\"crc\":\"0000\"}},\"priority\":\"0\",\"sspLocationRights\":\"3\",\"regions\":{\"GeographicalPath\":[{\"name\":\"bob\",\"laneWidth\":700,\"directionality\":{\"both\":\"EMPTY_TAG\"},\"closedPath\":\"BOOLEAN_OBJECT_FALSE\",\"direction\":\"1010101010101010\",\"description\":{\"oldRegion\":{\"direction\":\"1010101010101010\",\"extent\":{\"useFor3meters\":\"EMPTY_TAG\"},\"area\":{\"regionPointSet\":{\"anchor\":{\"latitude\":\"41.678473\",\"longitude\":\"-108.782775\",\"elevation\":\"917.1432\"},\"scale\":\"0\",\"nodeList\":[{\"xOffset\":\"-0.003\",\"yOffset\":\"0.003\",\"zOffset\":\"0\"},{\"xOffset\":\"-0.002\",\"yOffset\":\"0.002\",\"zOffset\":\"0\"},{\"xOffset\":\"-0.001\",\"yOffset\":\"0.001\",\"zOffset\":\"0\"}]}}}},\"id\":{\"region\":23,\"id\":33},\"anchor\":{\"lat\":416784730,\"long\":-1087827750,\"elevation\":9171}}]},\"url\":\"null\",\"sspMsgRights2\":3,\"sspMsgRights1\":2,\"duratonTime\":22,\"startYear\":2017,\"startTime\":482327,\"tcontent\":{\"advisory\":{\"SEQUENCE\":[{\"item\":{\"itis\":125}},{\"item\":{\"text\":\"some text\"}},{\"item\":{\"itis\":250}},{\"item\":{\"text\":\"98765\"}}]}}}]}}}");
    assertEquals(expectedTID.toString(), inputTID.toString());

    JSONObject timObject = new JSONObject();
    try {
      timObject.put(TravelerMessageFromHumanToAsnConverter.TRAVELER_INFORMATION,
          JsonUtils.toJSONObject(inputTID.toString()));
    } catch (JSONException e) {

      e.printStackTrace();
    } catch (JsonUtilsException e) {

      e.printStackTrace();
    }
    try {
      assertNotNull(XML.toString(timObject));
    } catch (JSONException e) {

      e.printStackTrace();
    }

  }
}
