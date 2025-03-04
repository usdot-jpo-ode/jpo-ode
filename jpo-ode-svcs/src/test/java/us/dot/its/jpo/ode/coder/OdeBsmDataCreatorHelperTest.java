package us.dot.its.jpo.ode.coder;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.Objects;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import us.dot.its.jpo.ode.model.OdeBsmData;
import us.dot.its.jpo.ode.model.OdeBsmPayload;
import us.dot.its.jpo.ode.plugin.j2735.J2735BsmPart2ExtensionBase;
import us.dot.its.jpo.ode.plugin.j2735.J2735VehicleSafetyExtensions;
import us.dot.its.jpo.ode.util.XmlUtils;

class OdeBsmDataCreatorHelperTest {
  @Test
  void testCreateOdeBsmData_OnePartIIExtension()
      throws XmlUtils.XmlUtilsException, IOException, URISyntaxException {
    String xmlFilePath =
        "src/test/resources/us.dot.its.jpo.ode.coder/OdeBsmDataCreatorHelper_BSM_XER_J2735_2024_OneExtension.xml";
    String consumedData = loadResourceAsString(xmlFilePath);
    XmlUtils.toObjectNode(consumedData);

    OdeBsmData bsmData = OdeBsmDataCreatorHelper.createOdeBsmData(consumedData);
    Assertions.assertNotNull(bsmData);
    J2735BsmPart2ExtensionBase part2Ext =
        ((OdeBsmPayload) bsmData.getPayload()).getBsm().getPartII().getFirst().getValue();
    BigDecimal timeOffset =
        ((J2735VehicleSafetyExtensions) part2Ext).getPathHistory().getCrumbData().get(9)
            .getTimeOffset();
    assertEquals(Integer.toString(65535), timeOffset.toString());

    // validate against schema
    JsonSchema schema = loadBsmSchema();
    var objectMapper = new ObjectMapper();
    var serializedBsmData = objectMapper.writeValueAsString(bsmData);
    var validationResults = schema.validate(objectMapper.readTree(serializedBsmData));
    assertEquals(0, validationResults.size(),
        "Outputted BSM failed schema validation: " + validationResults);
  }

  @Test
  void testCreateOdeBsmData_TwoPartIIExtensions()
      throws XmlUtils.XmlUtilsException, IOException, URISyntaxException {
    String xmlFilePath =
        "src/test/resources/us.dot.its.jpo.ode.coder/OdeBsmDataCreatorHelper_BSM_XER_J2735_2024_TwoExtensions.xml";
    String consumedData = loadResourceAsString(xmlFilePath);
    XmlUtils.toObjectNode(consumedData);

    OdeBsmData bsmData = OdeBsmDataCreatorHelper.createOdeBsmData(consumedData);
    Assertions.assertNotNull(bsmData);
    J2735BsmPart2ExtensionBase part2Ext =
        ((OdeBsmPayload) bsmData.getPayload()).getBsm().getPartII().getFirst().getValue();
    Assertions.assertNotNull(part2Ext);

    // validate against schema
    JsonSchema schema = loadBsmSchema();
    var objectMapper = new ObjectMapper();
    var serializedBsmData = objectMapper.writeValueAsString(bsmData);
    var validationResults = schema.validate(objectMapper.readTree(serializedBsmData));
    assertEquals(0, validationResults.size(),
        "Outputted BSM failed schema validation: " + validationResults);
  }

  static String loadResourceAsString(String resourcePath) throws IOException {
    File file = new File(resourcePath);
    byte[] data = Files.readAllBytes(file.toPath());
    return new String(data);
  }

  static JsonSchema loadBsmSchema() throws URISyntaxException {
    JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7);
    return factory.getSchema(Objects.requireNonNull(
            OdeBsmDataCreatorHelperTest.class.getClassLoader().getResource("schemas/schema-bsm.json"))
        .toURI());
  }
}
