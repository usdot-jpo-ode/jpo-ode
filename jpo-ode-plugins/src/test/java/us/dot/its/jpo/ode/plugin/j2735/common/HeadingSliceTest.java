package us.dot.its.jpo.ode.plugin.j2735.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

/**
 * Test serializing and deserializing a HeadingSlice bitstring to ODE JSON.
 */
public class HeadingSliceTest {

  private static final ObjectMapper mapper = new ObjectMapper();

  @Test
  public void testDeserializeJson() throws JsonProcessingException {
    HeadingSlice hs = mapper.readValue(EXPECTED_JSON, HeadingSlice.class);
    assertNotNull(hs);
    for (int i = 0; i < hs.size(); i++) {
      assertTrue(hs.get(i));
    }
  }

  @Test
  public void testSerializeJson() throws JsonProcessingException {
    var hs = new HeadingSlice();
    for (int i = 0; i < hs.size(); i++) {
      hs.set(i, true);
    }
    String json = mapper.writeValueAsString(hs);
    assertEquals(EXPECTED_JSON, json);
  }

  private static final String EXPECTED_JSON = """
      {"from000-0to022-5degrees":true,"from022-5to045-0degrees":true,"from045-0to067-5degrees":true,"from067-5to090-0degrees":true,"from090-0to112-5degrees":true,"from112-5to135-0degrees":true,"from135-0to157-5degrees":true,"from157-5to180-0degrees":true,"from180-0to202-5degrees":true,"from202-5to225-0degrees":true,"from225-0to247-5degrees":true,"from247-5to270-0degrees":true,"from270-0to292-5degrees":true,"from292-5to315-0degrees":true,"from315-0to337-5degrees":true,"from337-5to360-0degrees":true}""";
}
