package us.dot.its.jpo.ode.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import junit.framework.TestCase;
import us.dot.its.jpo.ode.context.AppContext;

public class JsonUtilsTest extends TestCase {
   private static final String OVDF = 
         "{\"className\":\"com.bah.ode.model.OdeVehicleDataFlat\",\"serialId\":\"10817812-036b-4d7b-867b-ae0bc62a2b3e.0\",\"receivedAt\":\"2015-07-22T19:21:16.413+0000\",\"groupId\":\"4130008F\",\"accelLong\":0.34,\"accelVert\":0.00,\"accellYaw\":8.42,\"heading\":65.9500,\"speed\":8.12,\"sizeLength\":500,\"sizeWidth\":200,\"latitude\":42.3296667,\"longitude\":-83.0445390,\"elevation\":156.9,\"tempId\":\"C4290123\",\"year\":2015,\"month\":5,\"day\":13,\"hour\":15,\"minute\":52,\"second\":45.500,\"dateTime\":\"2015-06-13T19:52:45.500+0000\"}";
   private static final String ODM =
         "{\"metadata\":{\"payloadType\":\"veh\",\"version\":1},\"payload\":" + OVDF + "}";
   
   public void testFromToJson() {
//      OdeVehicleDataFlat ovdf = 
//            (OdeVehicleDataFlat) JsonUtils.fromJson(OVDF, OdeVehicleDataFlat.class);
//      assertEquals(OVDF, JsonUtils.toJson(ovdf));
   }

   @Test
   public void testNewJson() {
      String json = JsonUtils.newJson("key1", "value1");
      assertEquals("{\"key1\":value1}", json);
   }

   @Test
   public void testNewObjectNode() {
      ObjectNode objectNode = JsonUtils.newObjectNode("key1", "value1");
      assertEquals("{\"key1\":value1}", objectNode.toString());
   }

   @Test
   public void testAddNode() {
      ObjectNode tree = JsonUtils.newObjectNode("key1", "value1");
      ObjectNode tree2 = JsonUtils.addNode(tree, "key2", "value2");
      assertEquals("{\"key1\":value1,\"key2\":value2}", tree2.toString());
   }

   @Test
   public void testGetJson() {
      assertEquals("10817812-036b-4d7b-867b-ae0bc62a2b3e.0", JsonUtils.getJsonNode(OVDF, "serialId").textValue());
   }

   @Test
   public void testNewNode() {
      assertEquals("{}", JsonUtils.newNode().toString());
   }

   @Test
   public void testToObjectNode() throws JsonProcessingException, IOException {
      ObjectNode ovdf = JsonUtils.toObjectNode(OVDF);
      String expectedOvdf = "{\"className\":\"com.bah.ode.model.OdeVehicleDataFlat\",\"serialId\":\"10817812-036b-4d7b-867b-ae0bc62a2b3e.0\",\"receivedAt\":\"2015-07-22T19:21:16.413+0000\",\"groupId\":\"4130008F\",\"accelLong\":0.34,\"accelVert\":0.0,\"accellYaw\":8.42,\"heading\":65.95,\"speed\":8.12,\"sizeLength\":500,\"sizeWidth\":200,\"latitude\":42.3296667,\"longitude\":-83.044539,\"elevation\":156.9,\"tempId\":\"C4290123\",\"year\":2015,\"month\":5,\"day\":13,\"hour\":15,\"minute\":52,\"second\":45.5,\"dateTime\":\"2015-06-13T19:52:45.500+0000\"}";
      assertEquals(expectedOvdf, ovdf.toString());
      JsonUtils.addNode(ovdf, "avgSpeed", "2.22");
      assertEquals("{\"className\":\"com.bah.ode.model.OdeVehicleDataFlat\",\"serialId\":\"10817812-036b-4d7b-867b-ae0bc62a2b3e.0\",\"receivedAt\":\"2015-07-22T19:21:16.413+0000\",\"groupId\":\"4130008F\",\"accelLong\":0.34,\"accelVert\":0.0,\"accellYaw\":8.42,\"heading\":65.95,\"speed\":8.12,\"sizeLength\":500,\"sizeWidth\":200,\"latitude\":42.3296667,\"longitude\":-83.044539,\"elevation\":156.9,\"tempId\":\"C4290123\",\"year\":2015,\"month\":5,\"day\":13,\"hour\":15,\"minute\":52,\"second\":45.5,\"dateTime\":\"2015-06-13T19:52:45.500+0000\",\"avgSpeed\":2.22}", ovdf.toString());
   }

   @Test
   public void testPutObject() {
      ObjectNode dm = JsonUtils.newNode();
      dm.putObject("metadata");
      dm.putObject(AppContext.PAYLOAD_STRING).setAll(JsonUtils.newObjectNode("key1", "value1"));
      assertEquals("{\"metadata\":{},\"payload\":{\"key1\":value1}}", dm.toString());
   }
   
   @Test
   public void testJsonNodeToHashMap() {
      JsonNode jsonNode = JsonUtils.getJsonNode(ODM, AppContext.PAYLOAD_STRING);
      HashMap<String, JsonNode> hashMap = JsonUtils.jsonNodeToHashMap(jsonNode);
      Iterator<Entry<String, JsonNode>> fieldsIter = jsonNode.fields();
      
      while (fieldsIter.hasNext()) {
         Entry<String, JsonNode> field = fieldsIter.next();
         JsonNode node = hashMap.get(field.getKey());
         assertNotNull(node);
         assertEquals(node.asText(), field.getValue().asText());
      }
      
      for (Entry<String, JsonNode> entry : hashMap.entrySet()) {
         JsonNode node = jsonNode.get(entry.getKey());
         assertNotNull(node);
         assertEquals(node.asText(), entry.getValue().asText());
      }
   }
   
}
