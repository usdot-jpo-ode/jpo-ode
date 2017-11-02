package us.dot.its.jpo.ode.traveler;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.fasterxml.jackson.databind.node.ObjectNode;

import us.dot.its.jpo.ode.model.TravelerInputData;
import us.dot.its.jpo.ode.plugin.j2735.builders.TravelerMessageFromHumanToAsnConverter;
import us.dot.its.jpo.ode.util.JsonUtils;

public class TimControllerHelperTest {

   @Test
   public void testRealJson() throws Exception {
      String jsonString = "{\"tim\":{\"msgCnt\":\"1\",\"index\":\"3\",\"timeStamp\":\"2017-10-27T18:04:43.045Z\",\"packetID\":\"3\",\"urlB\":\"null\",\"dataframes\":[{\"startDateTime\":\"2017-10-20T05:22:33.985Z\",\"durationTime\":100,\"frameType\":\"1\",\"sspTimRights\":\"1\",\"msgID\":\"RoadSignID\",\"position\":{\"latitude\":\"40.573068\",\"longitude\":\"-105.049016\",\"elevation\":\"1500.8999999999999\"},\"viewAngle\":\"1111111111111111\",\"mutcd\":\"2\",\"crc\":\"0000000000000000\",\"priority\":\"5\",\"sspLocationRights\":\"1\",\"regions\":[{\"name\":\"Testing TIM\",\"regulatorID\":\"0\",\"segmentID\":\"33\",\"anchorPosition\":{\"latitude\":\"40.573068\",\"longitude\":\"-105.049016\",\"elevation\":\"1500.8999999999999\"},\"laneWidth\":\"327\",\"directionality\":\"3\",\"closedPath\":\"false\",\"description\":\"path\",\"path\":{\"scale\":\"0\",\"type\":\"xy\",\"nodes\":[{\"nodeLong\":\"-105.047355\",\"nodeLat\":\"40.572429\",\"delta\":\"node-LatLon\"},{\"nodeLong\":\"-105.046844\",\"nodeLat\":\"40.572228\",\"delta\":\"node-LatLon\"},{\"nodeLong\":\"-105.04659\",\"nodeLat\":\"40.572113\",\"delta\":\"node-LatLon\"},{\"nodeLong\":\"-105.046243\",\"nodeLat\":\"40.57191\",\"delta\":\"node-LatLon\"},{\"nodeLong\":\"-105.045936\",\"nodeLat\":\"40.571675\",\"delta\":\"node-LatLon\"},{\"nodeLong\":\"-105.045674\",\"nodeLat\":\"40.571422\",\"delta\":\"node-LatLon\"},{\"nodeLong\":\"-105.04545\",\"nodeLat\":\"40.571131\",\"delta\":\"node-LatLon\"},{\"nodeLong\":\"-105.045235\",\"nodeLat\":\"40.570724\",\"delta\":\"node-LatLon\"},{\"nodeLong\":\"-105.045113\",\"nodeLat\":\"40.570293\",\"delta\":\"node-LatLon\"},{\"nodeLong\":\"-105.045087\",\"nodeLat\":\"40.569848\",\"delta\":\"node-LatLon\"}]},\"direction\":\"1111111111111111\"}],\"sspMsgTypes\":\"1\",\"sspMsgContent\":\"1\",\"content\":\"Advisory\",\"items\":[\"7425\"],\"url\":\"null\"}]},\"rsus\":[{\"rsuRetries\":\"3\",\"rsuTimeout\":\"5000\",\"rsuUsername\":\"v3user\",\"rsuPassword\":\"password\",\"rsuTarget\":\"192.168.0.145\",\"indicies\":[1,2]}],\"snmp\":{\"rsuid\":\"00000083\",\"msgid\":\"31\",\"mode\":\"1\",\"channel\":\"178\",\"interval\":\"2\",\"deliverystart\":\"2017-06-01T17:47:11-05:00\",\"deliverystop\":\"2018-01-01T17:47:11-05:15\",\"enable\":\"1\",\"status\":\"4\"},\"sdw\":{\"ttl\":\"oneday\",\"serviceRegion\":{\"nwCorner\":{\"latitude\":\"45.035685245316394\",\"longitude\":\"-110.95195770263672\"},\"seCorner\":{\"latitude\":\"40.96538194577477\",\"longitude\":\"-104.15382385253906\"}}}}";
      TravelerInputData testTravelerinputData = (TravelerInputData) JsonUtils.fromJson(jsonString,
            TravelerInputData.class);

      ObjectNode encodableTid = TravelerMessageFromHumanToAsnConverter
            .changeTravelerInformationToAsnValues(JsonUtils.toObjectNode(testTravelerinputData.toJson()));

      assertNotNull(TimControllerHelper.convertToXml(testTravelerinputData, encodableTid));
   }
}
