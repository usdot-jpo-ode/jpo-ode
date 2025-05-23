package us.dot.its.jpo.ode.coder;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;
import us.dot.its.jpo.ode.model.OdeTimData;
import us.dot.its.jpo.ode.util.XmlUtils;

/**
 * Unit tests for the OdeTimDataCreatorHelper class.
 */
class OdeTimDataCreatorHelperTest {

  /**
   * Test TIM data creation from 2024 XML string input.
   */
  @Test
  public void testCreateOdeTimDataFromDecoded2024() throws Exception {
    String xmlString = Files.readString(Paths.get(
        "src/test/resources/us.dot.its.jpo.ode.coder/OdeTimDataCreatorHelper_TIM_XER_J2735-2024.xml"));

    XmlUtils.toObjectNode(xmlString);

    String expectedJsonString = Files.readString(Paths.get(
        "src/test/resources/us.dot.its.jpo.ode.coder/OdeTimDataCreatorHelper_TIM_JSON_J2735-2024.json"));
    OdeTimData timData;

    timData = OdeTimDataCreatorHelper.createOdeTimDataFromDecoded(xmlString);

    assertEquals(expectedJsonString, timData.toString());

  }

  /**
   * Test TIM data creation from 2020 XML string input.
   */
  @Test
  public void testCreateOdeTimDataFromDecoded2020() throws Exception {
    String xmlString = Files.readString(Paths.get(
        "src/test/resources/us.dot.its.jpo.ode.coder/OdeTimDataCreatorHelper_TIM_XER_J2735-2020.xml"));

    XmlUtils.toObjectNode(xmlString);

    String expectedJsonString = Files.readString(Paths.get(
        "src/test/resources/us.dot.its.jpo.ode.coder/OdeTimDataCreatorHelper_TIM_JSON_J2735-2024.json"));
    OdeTimData timData;

    timData = OdeTimDataCreatorHelper.createOdeTimDataFromDecoded(xmlString);

    assertEquals(expectedJsonString, timData.toString());

  }

  /**
   * Test TIM data creation from 2016 XML string input.
   */
  @Test
  public void testCreateOdeTimDataFromDecoded2016() throws Exception {
    String xmlString = Files.readString(Paths.get(
        "src/test/resources/us.dot.its.jpo.ode.coder/OdeTimDataCreatorHelper_TIM_XER_J2735-2016.xml"));

    XmlUtils.toObjectNode(xmlString);

    String expectedJsonString = Files.readString(Paths.get(
        "src/test/resources/us.dot.its.jpo.ode.coder/OdeTimDataCreatorHelper_TIM_JSON_J2735-2024.json"));
    OdeTimData timData;

    timData = OdeTimDataCreatorHelper.createOdeTimDataFromDecoded(xmlString);

    assertEquals(expectedJsonString, timData.toString());

  }
}
