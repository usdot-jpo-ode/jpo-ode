package us.dot.its.jpo.ode.codec.ffmlib;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import j2735ffm.MessageFrameCodec;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.zip.GZIPInputStream;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import us.dot.its.jpo.ode.kafka.OdeKafkaProperties;
import us.dot.its.jpo.ode.kafka.listeners.json.RawEncodedJsonService;
import us.dot.its.jpo.ode.kafka.topics.JsonTopics;
import us.dot.its.jpo.ode.kafka.topics.RawEncodedJsonTopics;
import us.dot.its.jpo.ode.model.OdeMessageFrameMetadata;
import us.dot.its.jpo.ode.uper.SupportedMessageType;
import us.dot.its.jpo.ode.uper.UperUtil;

/** Routes native capacity failures to infrastructure handling rather than raw-input quarantine. */
class FfmlibNativeFailureRoutingTest {

  private static final String RAW_TOPIC = "topic.OdeRawEncodedBSMJson";
  private static final String RAW_JSON = "{\"payload\":{\"data\":{\"bytes\":\"0014\"}}}";

  @Test
  @SuppressWarnings("unchecked")
  void undersizedNativeBufferAtHaltThresholdLeavesRawRecordUnacknowledged() throws Exception {
    Scenario scenario = scenario(1);

    FfmlibDecodeService.InfrastructureFailure failure = assertThrows(
        FfmlibDecodeService.InfrastructureFailure.class,
        () -> scenario.listener().bsm(scenario.record(), scenario.acknowledgment()));

    assertNotNull(failure.getCause());
    verify(scenario.quarantineKafka(), never()).send(any(ProducerRecord.class));
    verify(scenario.outputKafka(), never()).send(anyString(), anyString(), anyString());
    verify(scenario.acknowledgment(), never()).acknowledge();
    assertEquals("DOWN", scenario.listener().health().getStatus().getCode());
    assertEquals(1.0, scenario.meters().get("ode.ffmlib.decode.failures")
        .tag("type", "BSM").tag("source", "udp").tag("reason", "infrastructure")
        .counter().count());
  }

  @Test
  @SuppressWarnings("unchecked")
  void undersizedNativeBufferBelowHaltThresholdIsQuarantined() throws Exception {
    Scenario scenario = scenario(3);
    when(scenario.quarantineKafka().send(any(ProducerRecord.class)))
        .thenReturn(CompletableFuture.completedFuture(null));

    scenario.listener().bsm(scenario.record(), scenario.acknowledgment());

    var captor = ArgumentCaptor.forClass(ProducerRecord.class);
    verify(scenario.quarantineKafka()).send(captor.capture());
    assertEquals("infrastructure", new String(
        captor.getValue().headers().lastHeader("failure-category").value(),
        StandardCharsets.UTF_8));
    verify(scenario.outputKafka(), never()).send(anyString(), anyString(), anyString());
    verify(scenario.acknowledgment()).acknowledge();
    assertEquals("UP", scenario.listener().health().getStatus().getCode());
    assertEquals(1.0, scenario.meters().get("ode.ffmlib.decode.failures")
        .tag("type", "BSM").tag("source", "udp").tag("reason", "infrastructure")
        .counter().count());
  }

  @SuppressWarnings("unchecked")
  private Scenario scenario(int haltThreshold) throws Exception {
    Path library = FfmlibNativeTestSupport.requireLibraryOrSkip();
    byte[] validBsm = validMessageFrameFromFirstBsmLogEntry();
    assertEquals("0014", us.dot.its.jpo.ode.util.CodecUtils.toHex(validBsm).substring(0, 4));

    SimpleMeterRegistry meters = new SimpleMeterRegistry();
    FfmlibProperties properties = new FfmlibProperties();
    properties.setTextBufferSize(1);
    properties.setInfrastructureHaltThreshold(haltThreshold);
    MessageFrameCodec nativeCodec = new MessageFrameCodec(properties.getTextBufferSize(),
        properties.getUperBufferSize(), properties.getErrorBufferSize(), library);
    FfmlibMessageFrameCodec codec = new FfmlibMessageFrameCodec(nativeCodec, meters);
    ObjectProvider<FfmlibMessageFrameCodec> codecProvider = mock(ObjectProvider.class);
    when(codecProvider.getIfAvailable()).thenReturn(codec);

    KafkaTemplate<String, String> outputKafka = mock(KafkaTemplate.class);
    KafkaTemplate<String, String> quarantineKafka = mock(KafkaTemplate.class);
    FfmlibOutputPublisher output = new FfmlibOutputPublisher(outputKafka,
        new OdeKafkaProperties(), meters);
    ObjectProvider<FfmlibOutputPublisher> outputProvider = mock(ObjectProvider.class);
    when(outputProvider.getObject()).thenReturn(output);
    Asn1CodecModeProperties mode = new Asn1CodecModeProperties();
    mode.setCodecMode(Asn1CodecModeProperties.CodecMode.ffm);
    JsonTopics jsonTopics = new JsonTopics();
    jsonTopics.setBsm("topic.OdeBsmJson");
    FfmlibDecodeService decoder = new FfmlibDecodeService(codecProvider, properties, mode,
        jsonTopics, mock(KafkaTemplate.class), outputProvider, new ObjectMapper(), meters,
        "topic.Asn1DecoderInput");

    RawEncodedJsonService rawService = mock(RawEncodedJsonService.class);
    OdeMessageFrameMetadata metadata = new OdeMessageFrameMetadata();
    metadata.setSchemaVersion(9);
    when(rawService.parseFfmDecodeInput(eq(RAW_JSON), eq(SupportedMessageType.BSM)))
        .thenReturn(new RawEncodedJsonService.FfmDecodeInput(metadata, validBsm));
    RawEncodedJsonTopics rawTopics = new RawEncodedJsonTopics();
    rawTopics.setBsm(RAW_TOPIC);
    FfmlibRawJsonListener listener = new FfmlibRawJsonListener(rawService, decoder, output,
        quarantineKafka, rawTopics, new FfmlibCommitTracker(meters), meters, properties);
    listener.markStartupComplete();
    ConsumerRecord<String, String> record = new ConsumerRecord<>(RAW_TOPIC, 0, 1, "key",
        RAW_JSON);
    Acknowledgment acknowledgment = mock(Acknowledgment.class);
    return new Scenario(listener, record, acknowledgment, outputKafka, quarantineKafka, meters);
  }

  private record Scenario(FfmlibRawJsonListener listener, ConsumerRecord<String, String> record,
      Acknowledgment acknowledgment, KafkaTemplate<String, String> outputKafka,
      KafkaTemplate<String, String> quarantineKafka, SimpleMeterRegistry meters) {
  }

  private static byte[] validMessageFrameFromFirstBsmLogEntry() throws Exception {
    Path compressedLog = Path.of("..", "data", "bsmTx_commsignia.gz");
    try (InputStream input = new GZIPInputStream(Files.newInputStream(compressedLog))) {
      byte[] header = input.readNBytes(26);
      if (header.length != 26) {
        throw new IllegalStateException("The BSM fixture does not contain a complete log header");
      }
      int payloadLength = Byte.toUnsignedInt(header[24])
          | (Byte.toUnsignedInt(header[25]) << 8);
      byte[] packet = input.readNBytes(payloadLength);
      if (packet.length != payloadLength) {
        throw new IllegalStateException("The BSM fixture does not contain a complete payload");
      }
      return UperUtil.stripDot2Header(packet, SupportedMessageType.BSM.getStartFlagBytes());
    }
  }
}
