package us.dot.its.jpo.ode.coder;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.junit.jupiter.api.Test;
import us.dot.its.jpo.ode.model.OdeTimData;
import us.dot.its.jpo.ode.util.XmlUtils;
import us.dot.its.jpo.ode.util.XmlUtils.XmlUtilsException;

/**
 * Tests for the OdeTimDataCreatorHelper class.
 */
public class OdeTimDataCreatorHelperTest {
  @Test
  public void testCreateOdeTimDataFromDecoded() throws IOException {
    String xmlFilePath = 
        "src/test/resources/us.dot.its.jpo.ode.coder/OdeTimDataCreatorHelper_TIM_XER.xml";
    File xmlFile = new File(xmlFilePath);
    byte[] xmlData = Files.readAllBytes(xmlFile.toPath());
    String xmlString = new String(xmlData);
    try {
      XmlUtils.toObjectNode(xmlString);
    } catch (XmlUtilsException e) {
      fail("XML parsing error:" + e);
    }

    String jsonFilePath = 
        "src/test/resources/us.dot.its.jpo.ode.coder/OdeTimDataCreatorHelper_TIM_JSON.json";
    File jsonFile = new File(jsonFilePath);
    byte[] jsonData = Files.readAllBytes(jsonFile.toPath());
    String expectedJsonString = new String(jsonData);
    OdeTimData timData;
    try {
      timData = OdeTimDataCreatorHelper.createOdeTimDataFromDecoded(xmlString);
      System.out.println(timData.toString());
      assertEquals(expectedJsonString, timData.toString());
    } catch (XmlUtilsException e) {
      e.printStackTrace();
    }

  }
}
