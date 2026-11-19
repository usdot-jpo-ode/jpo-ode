package us.dot.its.jpo.ode.archive;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

class ArchiveFileNameTest {

  @Test
  void parsesGzipArchiveObjectName() {
    ArchiveFileName name =
        ArchiveFileName.parse("topic.OdeBsmJson+3+0000002500.json.gz").orElseThrow();

    assertEquals("topic.OdeBsmJson", name.topic());
    assertEquals(3, name.partition());
    assertEquals(2500L, name.startOffset());
  }

  @Test
  void parsesUncompressedArchiveObjectName() {
    ArchiveFileName name =
        ArchiveFileName.parse("topic.OdeRawEncodedMAPJson+0+0000000000.json").orElseThrow();

    assertEquals("topic.OdeRawEncodedMAPJson", name.topic());
    assertEquals(0L, name.startOffset());
  }

  @ParameterizedTest
  @NullSource
  @ValueSource(strings = {"bsmTx_1.gz", "topic.OdeBsmJson.json.gz", "topic.OdeBsmJson+0.json.gz",
      "topic.OdeBsmJson+a+0.json.gz", "topic.OdeBsmJson+0+0.txt", "+0+0.json.gz",
      "1712345678-topic.OdeBsmJson+0+0000000000.json.gz.part"})
  void rejectsOtherNames(String fileName) {
    assertTrue(ArchiveFileName.parse(fileName).isEmpty());
  }
}
