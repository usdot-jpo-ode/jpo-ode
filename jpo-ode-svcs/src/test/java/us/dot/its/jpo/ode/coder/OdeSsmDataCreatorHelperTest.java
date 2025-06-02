package us.dot.its.jpo.ode.coder;

import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.Test;
import us.dot.its.jpo.ode.model.OdeSsmData;

/**
 * Unit tests for the OdeSsmDataCreatorHelper class.
 */
public class OdeSsmDataCreatorHelperTest {

  /**
   * Test the constructor of the OdeSsmDataCreatorHelper class.
   */
  @Test
  public void testConstructor() {
    OdeSsmDataCreatorHelper helper = new OdeSsmDataCreatorHelper();
    assertNotNull(helper);
  }

  /**
   * Test the createOdeSsmData method of the OdeSsmDataCreatorHelper class.
   */
  @Test
  public void testCreateOdeSsmData() throws Exception {
    String xmlData = Files.readString(
        Paths.get("src/test/resources/us.dot.its.jpo.ode.coder/OdeSsmDataCreatorHelper_SSM.xml"));
    String consumedData = new String(xmlData);

    OdeSsmData ssmData = OdeSsmDataCreatorHelper.createOdeSsmData(consumedData);
    assertNotNull(ssmData);
    assertThat(ssmData.toJson(), jsonEquals(loadExpectedJson()));
  }

  private String loadExpectedJson() throws Exception {
    return Files.readString(
        Paths.get("src/test/resources/us.dot.its.jpo.ode.coder/OdeSsmDataCreatorHelper_SSM.json"));
  }
}
