package us.dot.its.jpo.ode.coder;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.nio.file.Files;
import org.junit.jupiter.api.Test;
import us.dot.its.jpo.ode.model.OdeTimData;
import us.dot.its.jpo.ode.util.XmlUtils;

class OdeTimDataCreatorHelperTest {

  @Test
  public void testCreateOdeTimDataFromDecoded2024() throws Exception {
    String xmlFilePath = 
        "src/test/resources/us.dot.its.jpo.ode.coder/OdeTimDataCreatorHelper_TIM_XER_J2735-2024.xml";
    File xmlFile = new File(xmlFilePath);
    byte[] xmlData = Files.readAllBytes(xmlFile.toPath());
    String xmlString = new String(xmlData);

    XmlUtils.toObjectNode(xmlString);

    String jsonFilePath = 
        "src/test/resources/us.dot.its.jpo.ode.coder/OdeTimDataCreatorHelper_TIM_JSON_J2735-2024.json";
    File jsonFile = new File(jsonFilePath);
    byte[] jsonData = Files.readAllBytes(jsonFile.toPath());
    String expectedJsonString = new String(jsonData);
    OdeTimData timData;
    
    timData = OdeTimDataCreatorHelper.createOdeTimDataFromDecoded(xmlString);

    assertEquals(expectedJsonString, timData.toString());

  }


  @Test
  public void testCreateOdeTimDataFromDecoded2020() throws Exception {
    String xmlFilePath =
        "src/test/resources/us.dot.its.jpo.ode.coder/OdeTimDataCreatorHelper_TIM_XER_J2735-2020.xml";
    File xmlFile = new File(xmlFilePath);
    byte[] xmlData = Files.readAllBytes(xmlFile.toPath());
    String xmlString = new String(xmlData);

    XmlUtils.toObjectNode(xmlString);

    String jsonFilePath =
        "src/test/resources/us.dot.its.jpo.ode.coder/OdeTimDataCreatorHelper_TIM_JSON_J2735-2024.json";
    File jsonFile = new File(jsonFilePath);
    byte[] jsonData = Files.readAllBytes(jsonFile.toPath());
    String expectedJsonString = new String(jsonData);
    OdeTimData timData;

    timData = OdeTimDataCreatorHelper.createOdeTimDataFromDecoded(xmlString);

    assertEquals(expectedJsonString, timData.toString());

  }

  @Test
  public void testCreateOdeTimDataFromDecoded2016() throws Exception {
    String xmlFilePath =
        "src/test/resources/us.dot.its.jpo.ode.coder/OdeTimDataCreatorHelper_TIM_XER_J2735-2016.xml";
    File xmlFile = new File(xmlFilePath);
    byte[] xmlData = Files.readAllBytes(xmlFile.toPath());
    String xmlString = new String(xmlData);

    XmlUtils.toObjectNode(xmlString);

    String jsonFilePath =
        "src/test/resources/us.dot.its.jpo.ode.coder/OdeTimDataCreatorHelper_TIM_JSON_J2735-2024.json";
    File jsonFile = new File(jsonFilePath);
    byte[] jsonData = Files.readAllBytes(jsonFile.toPath());
    String expectedJsonString = new String(jsonData);
    OdeTimData timData;

    timData = OdeTimDataCreatorHelper.createOdeTimDataFromDecoded(xmlString);

    assertEquals(expectedJsonString, timData.toString());

  }
}
