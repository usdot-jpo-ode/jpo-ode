package us.dot.its.jpo.ode.archive;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static us.dot.its.jpo.ode.archive.ArchiveTestData.BSM_HEX;
import static us.dot.its.jpo.ode.archive.ArchiveTestData.compactLine;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.zip.GZIPOutputStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import us.dot.its.jpo.ode.config.SerializationConfig;
import us.dot.its.jpo.ode.kafka.OdeKafkaProperties;

class ArchiveImportProcessorTest {

  private static final String RECEIVED_AT = "2026-10-29T22:07:29.253Z";

  @TempDir
  Path root;

  private Path inbox;
  private Path backup;
  private Path failed;

  private final ObjectMapper mapper = new SerializationConfig().objectMapper();
  private final SimpleMeterRegistry meterRegistry = new SimpleMeterRegistry();
  private final List<String[]> sent = new ArrayList<>();
  private KafkaTemplate<String, String> kafkaTemplate;
  private ArchiveImporterProperties properties;
  private OdeKafkaProperties odeKafkaProperties;

  @BeforeEach
  @SuppressWarnings("unchecked")
  void setUp() throws IOException {
    inbox = Files.createDirectories(root.resolve("archive"));
    backup = Files.createDirectories(root.resolve("backup/archive"));
    failed = Files.createDirectories(root.resolve("failed/archive"));

    kafkaTemplate = mock(KafkaTemplate.class);
    when(kafkaTemplate.send(anyString(), anyString())).thenAnswer(invocation -> {
      sent.add(new String[] {invocation.getArgument(0), invocation.getArgument(1)});
      return CompletableFuture.completedFuture(mock(SendResult.class));
    });

    properties = new ArchiveImporterProperties();
    properties.setMinFileAgeSeconds(0);
    properties.setSendBatchSize(2);
    odeKafkaProperties = new OdeKafkaProperties();
    odeKafkaProperties.setDisabledTopics(Set.of());
  }

  private ArchiveImportProcessor processor() {
    return new ArchiveImportProcessor(kafkaTemplate, ArchiveTestData.topicResolver(), mapper,
        meterRegistry, properties, odeKafkaProperties);
  }

  private int process() {
    return processor().processDirectory(inbox, backup, failed);
  }

  @Test
  void compactGzipFileIsPublishedToRawEncodedTopicWithArchivedMetadata() throws Exception {
    writeGzip(inbox.resolve("topic.OdeBsmJson+0+0000000000.json.gz"),
        compactLine(RECEIVED_AT, "10.0.0.1", BSM_HEX),
        "",
        compactLine("2026-10-29T22:07:30.000Z", "", BSM_HEX));

    assertEquals(1, process());

    assertEquals(2, sent.size());
    assertEquals("topic.OdeRawEncodedBSMJson", sent.get(0)[0]);
    JsonNode first = mapper.readTree(sent.get(0)[1]);
    assertEquals(RECEIVED_AT, first.at("/metadata/odeReceivedAt").asText());
    assertEquals("10.0.0.1", first.at("/metadata/originIp").asText());
    assertEquals("", mapper.readTree(sent.get(1)[1]).at("/metadata/originIp").asText());
    assertTrue(Files.exists(backup.resolve("topic.OdeBsmJson+0+0000000000.json.gz")));
    assertEquals(2.0, meterRegistry.get("ode.archive.import.records")
        .tags("topic", "topic.OdeRawEncodedBSMJson", "format", "compact", "result", "published")
        .counter().count());
  }

  @Test
  void filesArePublishedInOffsetOrderAndKeepTheirSubdirectory() throws Exception {
    Path hour = Files.createDirectories(inbox.resolve("compact/topic.OdeBsmJson/hour=18"));
    writePlain(hour.resolve("topic.OdeBsmJson+0+0000000010.json"),
        compactLine("2026-10-29T18:00:10Z", "b", BSM_HEX));
    writePlain(hour.resolve("topic.OdeBsmJson+0+0000000000.json"),
        compactLine("2026-10-29T18:00:00Z", "a", BSM_HEX));

    assertEquals(2, process());

    assertEquals("a", mapper.readTree(sent.get(0)[1]).at("/metadata/originIp").asText());
    assertEquals("b", mapper.readTree(sent.get(1)[1]).at("/metadata/originIp").asText());
    assertTrue(Files.exists(backup.resolve(
        "compact/topic.OdeBsmJson/hour=18/topic.OdeBsmJson+0+0000000000.json")));
    assertFalse(Files.exists(inbox.resolve("compact")), "Empty inbox folders are removed");
  }

  @Test
  void rawEncodedRecordsArePublishedUnchanged() throws Exception {
    String raw = "{\"metadata\":{\"odeReceivedAt\":\"" + RECEIVED_AT + "\"},"
        + "\"payload\":{\"data\":{\"bytes\":\"" + BSM_HEX + "\"}}}";
    writeGzip(inbox.resolve("topic.OdeRawEncodedBSMJson+0+0000000000.json.gz"), raw);

    assertEquals(1, process());

    assertEquals("topic.OdeRawEncodedBSMJson", sent.get(0)[0]);
    assertEquals(raw, sent.get(0)[1]);
  }

  @Test
  void decodedRecordsAreRepublishedToTheirDecodedTopic() throws Exception {
    String decoded = decodedBsm();
    writeGzip(inbox.resolve("topic.OdeBsmJson+0+0000000000.json.gz"), decoded);

    assertEquals(1, process());

    assertEquals("topic.OdeBsmJson", sent.get(0)[0]);
    assertEquals(decoded, sent.get(0)[1]);
  }

  @Test
  void decodedRecordsAreDecodedAgainWhenConfigured() throws Exception {
    properties.setRedecodeFullRecords(true);
    writeGzip(inbox.resolve("topic.OdeBsmJson+0+0000000000.json.gz"), decodedBsm());

    assertEquals(1, process());

    assertEquals("topic.OdeRawEncodedBSMJson", sent.get(0)[0]);
    assertEquals(RECEIVED_AT,
        mapper.readTree(sent.get(0)[1]).at("/metadata/odeReceivedAt").asText());
  }

  @Test
  void compactFileWithoutArchiveNameDetectsTheMessageType() throws Exception {
    writeGzip(inbox.resolve("renamed.json.gz"), compactLine(RECEIVED_AT, "10.0.0.1", BSM_HEX));

    assertEquals(1, process());

    assertEquals("topic.OdeRawEncodedBSMJson", sent.get(0)[0]);
  }

  @Test
  void unsupportedTopicMovesFileToFailed() throws Exception {
    writeGzip(inbox.resolve("topic.ProcessedMap+0+0000000000.json.gz"), "{\"a\":1}");

    assertEquals(0, process());

    assertTrue(sent.isEmpty());
    assertTrue(Files.exists(failed.resolve("topic.ProcessedMap+0+0000000000.json.gz")));
  }

  @Test
  void badLinesAreSkippedWhileOtherLinesAreImported() throws Exception {
    writeGzip(inbox.resolve("topic.OdeBsmJson+0+0000000000.json.gz"),
        "not json",
        compactLine(RECEIVED_AT, "10.0.0.1", "ABCDEF"),
        compactLine(RECEIVED_AT, "10.0.0.1", BSM_HEX));

    var result = processor().importFile(inbox.resolve("topic.OdeBsmJson+0+0000000000.json.gz"));

    assertEquals(1, result.published());
    assertEquals(2, result.skipped());
  }

  @Test
  void fileWithOnlyBadLinesMovesToFailed() throws Exception {
    writeGzip(inbox.resolve("topic.OdeBsmJson+0+0000000000.json.gz"), "not json", "{}");

    assertEquals(0, process());

    assertTrue(sent.isEmpty());
    assertTrue(Files.exists(failed.resolve("topic.OdeBsmJson+0+0000000000.json.gz")));
  }

  @Test
  void kafkaFailureMovesFileToFailed() throws Exception {
    when(kafkaTemplate.send(anyString(), anyString()))
        .thenReturn(CompletableFuture.failedFuture(new IllegalStateException("broker down")));
    writeGzip(inbox.resolve("topic.OdeBsmJson+0+0000000000.json.gz"),
        compactLine(RECEIVED_AT, "10.0.0.1", BSM_HEX));

    assertEquals(0, process());

    assertTrue(Files.exists(failed.resolve("topic.OdeBsmJson+0+0000000000.json.gz")));
  }

  @Test
  void disabledTargetTopicMovesFileToFailedWithoutSending() throws Exception {
    odeKafkaProperties.setDisabledTopics(Set.of("topic.OdeRawEncodedBSMJson"));
    writeGzip(inbox.resolve("topic.OdeBsmJson+0+0000000000.json.gz"),
        compactLine(RECEIVED_AT, "10.0.0.1", BSM_HEX));

    assertEquals(0, process());

    verify(kafkaTemplate, never()).send(anyString(), anyString());
    assertTrue(Files.exists(failed.resolve("topic.OdeBsmJson+0+0000000000.json.gz")));
  }

  @Test
  void partialAndRecentFilesAreLeftInTheInbox() throws Exception {
    writeGzip(inbox.resolve("topic.OdeBsmJson+0+0000000000.json.gz.part"),
        compactLine(RECEIVED_AT, "10.0.0.1", BSM_HEX));
    writeGzip(inbox.resolve(".topic.OdeBsmJson+0+0000000001.json.gz"),
        compactLine(RECEIVED_AT, "10.0.0.1", BSM_HEX));
    assertEquals(0, process());

    properties.setMinFileAgeSeconds(3600);
    writeGzip(inbox.resolve("topic.OdeBsmJson+0+0000000002.json.gz"),
        compactLine(RECEIVED_AT, "10.0.0.1", BSM_HEX));
    assertEquals(0, process());

    assertTrue(sent.isEmpty());
    assertTrue(Files.exists(inbox.resolve("topic.OdeBsmJson+0+0000000002.json.gz")));
  }

  private static String decodedBsm() {
    return "{\"metadata\":{\"odeReceivedAt\":\"" + RECEIVED_AT + "\",\"originIp\":\"10.0.0.1\","
        + "\"recordType\":\"bsmTx\",\"asn1\":\"" + BSM_HEX + "\"},"
        + "\"payload\":{\"data\":{\"coreData\":{}}}}";
  }

  private static void writeGzip(Path file, String... lines) throws IOException {
    try (OutputStream out = new GZIPOutputStream(Files.newOutputStream(file))) {
      out.write((String.join("\n", lines) + "\n").getBytes(StandardCharsets.UTF_8));
    }
  }

  private static void writePlain(Path file, String... lines) throws IOException {
    Files.writeString(file, String.join("\n", lines) + "\n");
  }
}
