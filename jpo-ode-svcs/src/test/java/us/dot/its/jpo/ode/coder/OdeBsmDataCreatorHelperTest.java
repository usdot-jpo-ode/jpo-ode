package us.dot.its.jpo.ode.coder;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
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
  void testCreateOdeBsmData() throws XmlUtils.XmlUtilsException, IOException, URISyntaxException {
    String xmlFilePath =
        "src/test/resources/us.dot.its.jpo.ode.coder/OdeBsmDataCreatorHelper_BSM_XER_J2735_2024.xml";
    File xmlFile = new File(xmlFilePath);
    byte[] xmlData = Files.readAllBytes(xmlFile.toPath());
    String consumedData = new String(xmlData);
    XmlUtils.toObjectNode(consumedData);

    OdeBsmData bsmData;
    bsmData = OdeBsmDataCreatorHelper.createOdeBsmData(consumedData);
    Assertions.assertNotNull(bsmData);
    J2735BsmPart2ExtensionBase part2Ext = ((OdeBsmPayload) bsmData.getPayload()).getBsm().getPartII().get(0).getValue();
    BigDecimal timeOffset = ((J2735VehicleSafetyExtensions) part2Ext).getPathHistory().getCrumbData().get(9).getTimeOffset();
    assertEquals(Integer.toString(65535), timeOffset.toString());


    JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7);
    var schema = factory.getSchema(Objects.requireNonNull(getClass().getClassLoader().getResource("schemas/schema-bsm.json")).toURI());

    // convert bsmData to JSON
    var objectMapper = new ObjectMapper();
    var serializedBsmData = objectMapper.writeValueAsString(bsmData);
    var validationResults = schema.validate(objectMapper.readTree(serializedBsmData));

    // validate bsmData JSON with schema validator
    assertEquals(0, validationResults.size(), "Outputted BSM failed schema validation: " + validationResults);
  }
}
