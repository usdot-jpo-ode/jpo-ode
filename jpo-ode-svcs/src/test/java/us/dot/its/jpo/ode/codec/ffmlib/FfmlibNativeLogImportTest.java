package us.dot.its.jpo.ode.codec.ffmlib;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import j2735ffm.MessageFrameCodec;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.zip.GZIPInputStream;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.kafka.core.KafkaTemplate;
import us.dot.its.jpo.ode.coder.OdeMessageFrameDataCreatorHelper;
import us.dot.its.jpo.ode.coder.stream.LogFileToAsn1CodecPublisher;
import us.dot.its.jpo.ode.importer.ImporterFileType;
import us.dot.its.jpo.ode.importer.ImporterProcessor;
import us.dot.its.jpo.ode.kafka.OdeKafkaProperties;
import us.dot.its.jpo.ode.kafka.listeners.json.RawEncodedJsonService;
import us.dot.its.jpo.ode.kafka.topics.JsonTopics;
import us.dot.its.jpo.ode.kafka.topics.RawEncodedJsonTopics;
import us.dot.its.jpo.ode.model.OdeAsn1Data;
import us.dot.its.jpo.ode.model.OdeHexByteArray;
import us.dot.its.jpo.ode.model.OdeMessageFrameMetadata;
import us.dot.its.jpo.ode.uper.SupportedMessageType;
import us.dot.its.jpo.ode.util.CodecUtils;
import us.dot.its.jpo.ode.util.JsonUtils;

/** Exercises real log parsing and native decoding against the external route's XML mapping. */
class FfmlibNativeLogImportTest {

  @TempDir
  Path temporary;

  @ParameterizedTest
  @CsvSource({
      "rxMsg_commsignia_map.gz, MAP, topic.OdeMapJson, 103",
      "bsmTx_commsignia.gz, BSM, topic.OdeBsmJson, 33",
      "bsmTx.bin, BSM, topic.OdeBsmJson, 16",
      "rxMsg_commsignia_tim.gz, TIM, topic.OdeTimJson, 279"})
  void importedLogsMatchExternalPreparationAndMapping(String filename, SupportedMessageType type,
      String topic, int recordCount) throws Exception {
    Harness harness = new Harness();
    List<JsonNode> expected = new ArrayList<>();
    List<JsonNode> actual = new ArrayList<>();
    RawEncodedJsonService externalPreparation = new RawEncodedJsonService(harness.jsonMapper);
    XmlMapper xmlMapper = new XmlMapper();
    doAnswer(invocation -> {
      OdeAsn1Data input = invocation.getArgument(0);
      OdeAsn1Data external = externalPreparation.addEncodingAndMutateBytes(
          JsonUtils.toJson(input, false), type, OdeMessageFrameMetadata.class);
      byte[] uper = CodecUtils.fromHex(
          ((OdeHexByteArray) external.getPayload().getData()).getBytes());
      external.getMetadata().setEncodings(null);
      String xml = "<OdeAsn1Data>"
          + xmlMapper.writer().withRootName("metadata").writeValueAsString(external.getMetadata())
          + "<payload><data>" + harness.nativeCodec.uperToXer(uper)
          + "</data></payload></OdeAsn1Data>";
      expected.add(harness.jsonMapper.readTree(JsonUtils.toJson(
          OdeMessageFrameDataCreatorHelper.createOdeMessageFrameData(xml, xmlMapper), false)));
      return invocation.callRealMethod();
    }).when(harness.decoder).decode(any(OdeAsn1Data.class), isNull());
    when(harness.outputKafka.send(anyString(), isNull(), anyString())).thenAnswer(invocation -> {
      assertEquals(topic, invocation.getArgument(0));
      actual.add(harness.jsonMapper.readTree((String) invocation.getArgument(2)));
      return CompletableFuture.completedFuture(null);
    });

    Path imported = harness.inbox.resolve(filename);
    Files.copy(Path.of("..", "data", filename), imported);
    try {
      assertEquals(1, harness.processor.processDirectory(
          harness.inbox, harness.backup, harness.failed));
      assertEquals(recordCount, actual.size());
      assertEquals(recordCount, expected.size());
      for (int record = 0; record < recordCount; record++) {
        assertEquals(expected.get(record).path("metadata"), actual.get(record).path("metadata"),
            filename + " record " + record + " metadata");
        assertEquals(expected.get(record).path("payload"), actual.get(record).path("payload"),
            filename + " record " + record + " payload");
      }
      assertFalse(Files.exists(imported));
      assertEquals(1, fileCount(harness.backup));
      assertEquals(0, fileCount(harness.failed));
      assertEquals(recordCount, harness.meters.get("ode.ffmlib.decode.messages")
          .tag("source", "import").tag("type", type.name()).counter().count());
      assertTrue(harness.meters.find("ode.ffmlib.decode.failures").counters().isEmpty());
      verify(harness.externalKafka, never()).send(anyString(), anyString(), anyString());
    } finally {
      harness.decoder.shutdown();
    }
  }

  @ParameterizedTest
  @ValueSource(booleans = {false, true})
  void decodeAndPublicationFailuresKeepTheFileInTheFailedDirectory(boolean publicationFailure)
      throws Exception {
    Harness harness = new Harness();
    Path imported = harness.inbox.resolve("rxMsg_commsignia_map.gz");
    if (publicationFailure) {
      Files.copy(Path.of("..", "data", imported.getFileName().toString()), imported);
      when(harness.outputKafka.send(anyString(), isNull(), anyString()))
          .thenReturn(CompletableFuture.failedFuture(new IllegalStateException("Broker unavailable")));
    } else {
      // Keep a valid log header but truncate the ASN.1 content to just the MAP start flag.
      byte[] entry;
      try (InputStream input = new GZIPInputStream(Files.newInputStream(
          Path.of("..", "data", imported.getFileName().toString())))) {
        entry = Arrays.copyOf(input.readNBytes(26), 28);
      }
      entry[24] = 2;
      entry[25] = 0;
      entry[26] = 0;
      entry[27] = 0x12;
      imported = harness.inbox.resolve("rxMsg_invalid.bin");
      Files.write(imported, entry);
    }
    try {
      assertEquals(0, harness.processor.processDirectory(
          harness.inbox, harness.backup, harness.failed));
      assertFalse(Files.exists(imported));
      assertTrue(Files.exists(harness.failed.resolve(imported.getFileName())));
      assertEquals(0, fileCount(harness.backup));
      assertEquals(0, harness.meters.get("ode.ffmlib.decode.messages")
          .tag("source", "import").tag("type", "MAP").counter().count());
      if (!publicationFailure) {
        verify(harness.outputKafka, never()).send(anyString(), isNull(), anyString());
      }
    } finally {
      harness.decoder.shutdown();
    }
  }

  private static long fileCount(Path directory) throws Exception {
    try (var files = Files.list(directory)) {
      return files.count();
    }
  }

  private class Harness {
    final ObjectMapper jsonMapper = new ObjectMapper();
    final SimpleMeterRegistry meters = new SimpleMeterRegistry();
    final Path inbox = Files.createDirectory(temporary.resolve("inbox"));
    final Path backup = Files.createDirectory(temporary.resolve("backup"));
    final Path failed = Files.createDirectory(temporary.resolve("failed"));
    final KafkaTemplate<String, String> externalKafka;
    final KafkaTemplate<String, String> outputKafka;
    final MessageFrameCodec nativeCodec;
    final FfmlibDecodeService decoder;
    final ImporterProcessor processor;

    @SuppressWarnings("unchecked")
    Harness() throws Exception {
      Path library = FfmlibNativeTestSupport.requireLibraryOrSkip();
      FfmlibProperties properties = new FfmlibProperties();
      nativeCodec = new MessageFrameCodec(properties.getTextBufferSize(),
          properties.getUperBufferSize(), properties.getErrorBufferSize(), library);
      FfmlibMessageFrameCodec codec = new FfmlibMessageFrameCodec(nativeCodec, meters);
      ObjectProvider<FfmlibMessageFrameCodec> codecProvider = mock(ObjectProvider.class);
      when(codecProvider.getIfAvailable()).thenReturn(codec);
      externalKafka = mock(KafkaTemplate.class);
      outputKafka = mock(KafkaTemplate.class);
      FfmlibOutputPublisher output = new FfmlibOutputPublisher(
          outputKafka, new OdeKafkaProperties(), meters);
      ObjectProvider<FfmlibOutputPublisher> outputProvider = mock(ObjectProvider.class);
      when(outputProvider.getObject()).thenReturn(output);
      Asn1CodecModeProperties mode = new Asn1CodecModeProperties();
      mode.setCodecMode(Asn1CodecModeProperties.CodecMode.ffm);
      JsonTopics topics = new JsonTopics();
      topics.setMap("topic.OdeMapJson");
      topics.setBsm("topic.OdeBsmJson");
      topics.setTim("topic.OdeTimJson");
      decoder = spy(new FfmlibDecodeService(codecProvider, properties, mode, topics,
          externalKafka, outputProvider, jsonMapper, meters, "topic.Asn1DecoderInput"));
      processor = new ImporterProcessor(new LogFileToAsn1CodecPublisher(externalKafka,
          topics, new RawEncodedJsonTopics(), mode, decoder), ImporterFileType.LOG_FILE, 4096);
    }
  }
}
