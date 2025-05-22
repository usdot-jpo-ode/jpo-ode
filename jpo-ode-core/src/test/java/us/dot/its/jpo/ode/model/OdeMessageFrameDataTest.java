package us.dot.its.jpo.ode.model;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class OdeMessageFrameDataTest {

  private static final String SAMPLE_SDSM_FILE = "src/test/resources/json/sample-sdsm.json";
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Test
  public void testSdsmSerializationDeserialization() throws IOException {
    // Read the sample JSON file
    String jsonContent = new String(Files.readAllBytes(Paths.get(SAMPLE_SDSM_FILE)));

    // Deserialize JSON to OdeMessageFrameData
    OdeMessageFrameData messageFrame =
        objectMapper.readValue(jsonContent, OdeMessageFrameData.class);

    // Verify metadata
    assertNotNull(messageFrame.getMetadata());
    assertEquals("us.dot.its.jpo.ode.model.OdeMessageFramePayload",
        messageFrame.getMetadata().getPayloadType());

    // Verify payload
    assertNotNull(messageFrame.getPayload());
    assertNotNull(messageFrame.getPayload().getData());

    // Verify basic SDSM structure
    JsonNode data = objectMapper.valueToTree(messageFrame.getPayload().getData());
    assertNotNull(data.get("messageId"));
    assertEquals(41, data.get("messageId").asInt());

    JsonNode sdsm = data.get("value").get("SensorDataSharingMessage");
    assertNotNull(sdsm);
    assertEquals(10, sdsm.get("msgCnt").asInt());
    assertEquals("010C0C0A", sdsm.get("sourceID").asText());
  }

}
