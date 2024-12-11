package us.dot.its.jpo.ode.plugin.j2735.travelerinformation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import us.dot.its.jpo.ode.plugin.j2735.common.Offset_B10;

/**
 * Test serializing and deserializing a NodeAttributeSetLL to and from ODE JSON.
 */
public class NodeAttributeSetLLTest {

  private static final ObjectMapper mapper = new ObjectMapper();

  @Test
  public void testDeserializeJson_dWidth_dElevation() throws JsonProcessingException {
    final NodeAttributeSetLL nasll = mapper.readValue(
        DWITDH_DELEVATION_ONLY_JSON, NodeAttributeSetLL.class);
    assertNotNull(nasll);
    final long dElevation = nasll.getDElevation().getValue();
    assertEquals(424, dElevation);
    final long dWidth = nasll.getDWidth().getValue();
    assertEquals(162, dWidth);
  }

  @Test
  public void testSerializeJson_dWidth_dElevation() throws JsonProcessingException {
    final var nasll = new NodeAttributeSetLL();
    nasll.setDElevation(new Offset_B10(424L));
    nasll.setDWidth(new Offset_B10(162L));
    final String jsonResult = mapper.writeValueAsString(nasll);
    assertEquals(DWITDH_DELEVATION_ONLY_JSON, jsonResult);
  }

  private static final String DWITDH_DELEVATION_ONLY_JSON = """
      {"dWidth":162,"dElevation":424}""";

}
