package us.dot.its.jpo.ode.plugin.j2735.builders;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

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
      dataFrameList.add(regions);
      
      // convert list of dataframes to json node
      ObjectNode dataFrames = JsonUtils.newObjectNode("dataFrames", new ArrayNode(JsonNodeFactory.instance, dataFrameList));
      
      
      ObjectNode tim = JsonUtils.newObjectNode("TravelerInformation", dataFrames);
      
      JsonNode deTranslatedTim = TravelerMessageFromHumanToAsnConverter.changeTravelerInformationToAsnValues(tim);
      
      assertEquals("string", deTranslatedTim.toString());
   }
   

}
