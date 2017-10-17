package us.dot.its.jpo.ode.plugin.j2735.builders;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735Position3D;
import us.dot.its.jpo.ode.util.JsonUtils;

public class TravelerMessageFromHumanToAsnConverterTest {

   @Test
   public void test() {
      
      ObjectMapper mapper = new ObjectMapper();
      
      // create pos3d (anchor)
      ObjectNode pos3d = JsonUtils.newNode();
      JsonUtils.addNode(pos3d, "lat", "890000001");
      JsonUtils.addNode(pos3d, "long", "1799999998");
      JsonUtils.addNode(pos3d, "elevation", "61433");
      
      // add anchor to geopath (region)
      ObjectNode geoPath = JsonUtils.newNode();
      JsonUtils.addNode(geoPath, "anchor", pos3d);
      
      
      
      // create list of regions and add the geopath/region to it
      List<JsonNode> regionList = new ArrayList<>();
      regionList.add(geoPath);
      
      // convert list of regions to json node
      ObjectNode regions = JsonUtils.newObjectNode("regions", new ArrayNode(JsonNodeFactory.instance, regionList));
      
      
      
      // create list of dataframes and add the list of geopaths/regions to it
      List<JsonNode> dataFrameList = new ArrayList<>();
      //dataFrameList.add(JsonUtils.newObjectNode("TravelerDataFrame", regions));
      dataFrameList.add(regions);
      
      // convert list of dataframes to json node
      ObjectNode dataFrames = JsonUtils.newObjectNode("dataframes", new ArrayNode(JsonNodeFactory.instance, dataFrameList));
      
      
      ObjectNode tim = JsonUtils.newNode();
      tim.set("tim", dataFrames);
      
      JsonNode deTranslatedTim = TravelerMessageFromHumanToAsnConverter.changeTravelerInformationToAsnValues(tim);
      
      assertEquals("string", deTranslatedTim);
   }
   
   @Test
   public void testRealJson() throws JsonProcessingException, IOException {
      ObjectNode testJson = JsonUtils.toObjectNode("{ \"tim\": { \"index\": \"13\", \"msgCnt\": \"1\", \"timeStamp\": \"2017-08-03T22:25:36.297Z\", \"urlB\": \"null\", \"dataframes\": [ { \"startDateTime\": \"2017-08-02T22:25:00.000Z\", \"durationTime\": 1, \"frameType\": \"1\", \"sspTimRights\": \"0\", \"msgID\": \"RoadSignID\", \"position\": { \"latitude\": \"41.678473\", \"longitude\": \"-108.782775\", \"elevation\": \"917.1432\" }, \"viewAngle\": \"1010101010101010\", \"mutcd\": \"5\", \"crc\": \"0000000000000000\", \"priority\": \"0\", \"sspLocationRights\": \"3\", \"regions\": [ { \"name\": \"Testing TIM\", \"regulatorID\": \"0\", \"segmentID\": \"33\", \"anchorPosition\": { \"latitude\": \"41.2500807\", \"longitude\": \"-111.0093847\", \"elevation\": \"2020.6969900289998\" }, \"laneWidth\": \"7\", \"directionality\": \"3\", \"closedPath\": \"false\", \"description\": \"path\", \"path\": { \"scale\": \"0\", \"type\": \"ll\", \"nodes\": [ { \"nodeLong\": \"0.0031024\", \"nodeLat\": \"0.0014506\", \"delta\": \"node-LL3\" }, { \"nodeLong\": \"0.0030974\", \"nodeLat\": \"0.0014568\", \"delta\": \"node-LL3\" }, { \"nodeLong\": \"0.0030983\", \"nodeLat\": \"0.0014559\", \"delta\": \"node-LL3\" }, { \"nodeLong\": \"0.0030980\", \"nodeLat\": \"0.0014563\", \"delta\": \"node-LL3\" }, { \"nodeLong\": \"0.0030982\", \"nodeLat\": \"0.0014562\", \"delta\": \"node-LL3\" } ] }, \"direction\": \"0000000000001010\" } ], \"sspMsgTypes\": \"2\", \"sspMsgContent\": \"3\", \"content\": \"Advisory\", \"items\": [ \"513\" ], \"url\": \"null\" } ] }, \"rsus\": [ { \"rsuTarget\": \"192.168.1.1\", \"rsuUsername\": \"user\", \"rsuPassword\": \"password\", \"rsuRetries\": \"1\", \"rsuTimeout\": \"2000\" } ], \"snmp\": { \"rsuid\": \"00000083\", \"msgid\": \"31\", \"mode\": \"1\", \"channel\": \"178\", \"interval\": \"2\", \"deliverystart\": \"2017-06-01T17:47:11-05:00\", \"deliverystop\": \"2018-01-01T17:47:11-05:15\", \"enable\": \"1\", \"status\": \"4\" } }");
   
      JsonNode deTranslatedTim = TravelerMessageFromHumanToAsnConverter.changeTravelerInformationToAsnValues(testJson);
      
      assertEquals("string", deTranslatedTim);
   
   }
   

}
