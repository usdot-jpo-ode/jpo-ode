package us.dot.its.jpo.ode.coder;

import static org.junit.Assert.assertNotNull;

import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.Test;
import us.dot.its.jpo.ode.model.OdePsmData;

/**
 * Unit tests for the OdePsmDataCreatorHelper class.
 */
public class OdePsmDataCreatorHelperTest {

  /**
   * Tests the constructor creates a valid instance.
   */
  @Test
  public void testConstructor() {
    OdePsmDataCreatorHelper helper = new OdePsmDataCreatorHelper();
    assertNotNull(helper);
  }

  /**
   * Tests PSM data creation from XML string input.
   */
  @Test
  public void testCreateOdePsmData() throws Exception {
    String xmlData = Files.readString(
        Paths.get("src/test/resources/us.dot.its.jpo.ode.coder/OdePsmDataCreatorHelper_PSM.xml"));
    String consumedData = new String(xmlData);

    OdePsmData psmData = OdePsmDataCreatorHelper.createOdePsmData(consumedData);
    assertNotNull(psmData);
  }
}
