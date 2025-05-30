package us.dot.its.jpo.ode.coder;

import static org.junit.Assert.assertNotNull;

import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.Test;
import us.dot.its.jpo.ode.model.OdeSrmData;

/**
 * Unit tests for the OdeSrmDataCreatorHelper class.
 */
public class OdeSrmDataCreatorHelperTest {

  /**
   * Test the constructor of the OdeSrmDataCreatorHelper class.
   */
  @Test
  public void testConstructor() {
    OdeSrmDataCreatorHelper helper = new OdeSrmDataCreatorHelper();
    assertNotNull(helper);
  }

  /**
   * Test the createOdeSrmData method of the OdeSrmDataCreatorHelper class.
   */
  @Test
  public void testCreateOdeSrmData() throws Exception {
    String xmlData = Files.readString(
        Paths.get("src/test/resources/us.dot.its.jpo.ode.coder/OdeSrmDataCreatorHelper_SRM.xml"));
    String consumedData = new String(xmlData);

    OdeSrmData srmData = OdeSrmDataCreatorHelper.createOdeSrmData(consumedData);
    assertNotNull(srmData);
  }
}
