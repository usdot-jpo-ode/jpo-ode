package us.dot.its.jpo.ode.codec.ffmlib;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.junit.jupiter.api.Test;
import us.dot.its.jpo.asn.j2735.r2024.TravelerInformation.OffsetLL_B16;
import us.dot.its.jpo.asn.j2735.r2024.TravelerInformation.RegionOffsets;
import us.dot.its.jpo.ode.model.OdeMessageFrameMetadata;

/** Protects the historical Jackson field names emitted by generated ASN.1 POJOs and ODE metadata. */
class SerializationContractTest {

  private final ObjectMapper jsonMapper = new ObjectMapper();
  private final XmlMapper xmlMapper = new XmlMapper();

  @Test
  void regionOffsetsKeepTheHistoricalJsonFieldNames() throws Exception {
    RegionOffsets offsets = regionOffsets();

    JsonNode json = jsonMapper.valueToTree(offsets);

    assertEquals(2, json.size());
    assertEquals(12, json.path("xOffset").longValue());
    assertEquals(-34, json.path("yOffset").longValue());
    assertFalse(json.has("xoffset"));
    assertFalse(json.has("yoffset"));
  }

  @Test
  void regionOffsetsKeepTheHistoricalXmlElementNames() throws Exception {
    String xml = xmlMapper.writeValueAsString(regionOffsets());

    assertTrue(xml.contains("<xOffset>12</xOffset>"), xml);
    assertTrue(xml.contains("<yOffset>-34</yOffset>"), xml);
    assertEquals(1, occurrences(xml, "<xOffset>"), xml);
    assertEquals(1, occurrences(xml, "<yOffset>"), xml);
    assertFalse(xml.contains("<xoffset>"), xml);
    assertFalse(xml.contains("<yoffset>"), xml);
  }

  @Test
  void messageFrameMetadataKeepsItsHistoricalCertificateFieldName() throws Exception {
    OdeMessageFrameMetadata metadata = new OdeMessageFrameMetadata(
        OdeMessageFrameMetadata.Source.RSU);
    metadata.setCertPresent(true);

    JsonNode json = jsonMapper.valueToTree(metadata);
    String xml = xmlMapper.writeValueAsString(metadata);

    assertTrue(json.path("isCertPresent").booleanValue());
    assertFalse(json.has("certPresent"));
    assertEquals(1, occurrences(xml, "<isCertPresent>"), xml);
    assertFalse(xml.contains("<certPresent>"), xml);
  }

  private static RegionOffsets regionOffsets() {
    RegionOffsets offsets = new RegionOffsets();
    offsets.setXOffset(new OffsetLL_B16(12));
    offsets.setYOffset(new OffsetLL_B16(-34));
    return offsets;
  }

  private static int occurrences(String text, String needle) {
    int count = 0;
    int index = 0;
    while ((index = text.indexOf(needle, index)) >= 0) {
      count++;
      index += needle.length();
    }
    return count;
  }
}
