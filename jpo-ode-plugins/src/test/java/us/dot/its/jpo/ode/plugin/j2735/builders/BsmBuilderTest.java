/*==============================================================================
 * Copyright 2018 572682
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/

package us.dot.its.jpo.ode.plugin.j2735.builders;

import com.fasterxml.jackson.databind.JsonNode;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;
import us.dot.its.jpo.ode.util.XmlUtils;
import us.dot.its.jpo.ode.util.XmlUtils.XmlUtilsException;

class BsmBuilderTest {

  @Test
  void shouldTranslateBsm() throws IOException {

    JsonNode jsonBsm = null;
    try {
      jsonBsm = XmlUtils.toObjectNode(loadResourceAsString(
          "src/test/resources/us/dot/its/jpo/ode/plugin/j2735/builders/BSM_XML_J2735_2024_ToTranslate.xml"));
    } catch (XmlUtilsException e) {
      Assertions.fail("XML parsing error:" + e);
    }

    J2735Bsm actualBsm = BsmBuilder.genericBsm(jsonBsm.findValue("BasicSafetyMessage"));

    Assertions.assertNotNull(actualBsm);
    String expected = loadResourceAsString(
        "src/test/resources/us/dot/its/jpo/ode/plugin/j2735/builders/BSM_JSON_J2735_2024_ExpectedTranslation.json");
    Assertions.assertEquals(expected, actualBsm.toString());

    // assert partII list is not empty
    Assertions.assertFalse(actualBsm.getPartII().isEmpty());
  }

  static String loadResourceAsString(String resourcePath) throws IOException {
    File file = new File(resourcePath);
    byte[] data = Files.readAllBytes(file.toPath());
    return new String(data);
  }
}
