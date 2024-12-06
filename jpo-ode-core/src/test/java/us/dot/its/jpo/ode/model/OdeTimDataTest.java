package us.dot.its.jpo.ode.model;

import static org.junit.Assert.assertEquals;

import com.fasterxml.jackson.databind.JsonNode;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import java.io.File;
import java.nio.file.Files;
import java.util.Set;
import org.junit.jupiter.api.Test;
import us.dot.its.jpo.ode.util.JsonUtils;

/**
 * Tests for verifying the TIM schema is functional with the TIM JSON output.
 */
public class OdeTimDataTest {
  @Test
  public void shouldValidateJson() throws Exception {
    // Load test JSON
    String jsonFilePath = 
        "src/test/resources/CVMessages/TIM_test.json";
    File jsonFile = new File(jsonFilePath);
    byte[] jsonData = Files.readAllBytes(jsonFile.toPath());
    String json = new String(jsonData);

    // Load JSON schema from resource
    JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V202012);
    final JsonSchema schema = factory
        .getSchema(getClass().getClassLoader().getResource("schemas/schema-tim.json").toURI());
    final JsonNode node = (JsonNode) JsonUtils.fromJson(json, JsonNode.class);
    Set<ValidationMessage> validationMessages = schema.validate(node);
    assertEquals(
        String.format("Json validation errors: %s", validationMessages), 
        0, 
        validationMessages.size());
  }
}
