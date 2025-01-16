package us.dot.its.jpo.ode.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.databind.JsonNode;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import org.junit.Test;
import us.dot.its.jpo.ode.util.JsonUtils;

/**
 * Unit test class for OdePsmData.
 */
public class OdePsmDataTest {
  private String loadTestJson() throws IOException {
    return new String(getClass().getClassLoader()
        .getResourceAsStream("json/sample-psm.json")
        .readAllBytes(), StandardCharsets.UTF_8);
  }

  @Test
  public void shouldDeserializeJson() throws IOException {
    final var deserialized = (OdePsmData) JsonUtils.fromJson(loadTestJson(), OdePsmData.class);
    assertNotNull(deserialized);
    assertTrue(deserialized.getMetadata() instanceof OdePsmMetadata);
    assertTrue(deserialized.getPayload() instanceof OdePsmPayload);

  }

  @Test
  public void serializationShouldNotAddClassProperty() throws IOException  {
    final var deserialized = (OdePsmData) JsonUtils.fromJson(loadTestJson(), OdePsmData.class);
    final String serialized = deserialized.toJson(false);
    assertFalse(serialized.contains("@class"));
  }

  @Test
  public void shouldValidateJson() throws Exception {
    final var deserialized = (OdePsmData) JsonUtils.fromJson(loadTestJson(), OdePsmData.class);
    final String serialized = deserialized.toJson(false);

    // Load json schema from resource
    JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V4);
    final JsonSchema schema =
        factory.getSchema(getClass().getClassLoader().getResource("schemas/schema-psm.json").toURI());
    final JsonNode node = (JsonNode) JsonUtils.fromJson(serialized, JsonNode.class);
    Set<ValidationMessage> validationMessages = schema.validate(node);
    assertEquals(String.format("Json validation errors: %s", validationMessages), 0, validationMessages.size());
  }

}
