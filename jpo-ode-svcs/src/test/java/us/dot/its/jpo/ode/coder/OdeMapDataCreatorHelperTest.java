package us.dot.its.jpo.ode.coder;

import static org.junit.Assert.assertNotNull;

import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.Test;
import us.dot.its.jpo.ode.model.OdeMapData;

/**
 * Unit tests for the OdeMapDataCreatorHelper class.
 */
public class OdeMapDataCreatorHelperTest {

  /**
   * Test the constructor of the OdeMapDataCreatorHelper class.
   */
  @Test
  public void testConstructor() {
    OdeMapDataCreatorHelper helper = new OdeMapDataCreatorHelper();
    assertNotNull(helper);
  }

  /**
   * Test the createOdeMapData method of the OdeMapDataCreatorHelper class.
   */
  @Test
  public void testCreateOdeMapData() throws Exception {
    String xmlData = Files.readString(
        Paths.get("src/test/resources/us.dot.its.jpo.ode.coder/OdeMapDataCreatorHelper_MAP.xml"));
    String consumedData = new String(xmlData);

    OdeMapData mapData = OdeMapDataCreatorHelper.createOdeMapData(consumedData);
    assertNotNull(mapData);
  }
}
