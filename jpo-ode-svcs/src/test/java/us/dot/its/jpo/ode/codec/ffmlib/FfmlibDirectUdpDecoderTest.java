package us.dot.its.jpo.ode.codec.ffmlib;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.health.contributor.Status;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import us.dot.its.jpo.ode.codec.ffmlib.FfmlibDecodeService.PreparedDecodedMessage;
import us.dot.its.jpo.ode.codec.ffmlib.FfmlibOutputPublisher.PublicationOutcome;
import us.dot.its.jpo.ode.model.OdeMessageFrameMetadata;
import us.dot.its.jpo.ode.udp.UdpHexDecoder.UdpDecodeInput;
import us.dot.its.jpo.ode.uper.SupportedMessageType;

class FfmlibDirectUdpDecoderTest {

  private static final String RAW_TOPIC = "topic.OdeRawEncodedBSMJson";
  private static final String DLQ_TOPIC = "dlq.OdeRawEncodedBSMJson";
  private static final String RAW_JSON = "{\"metadata\":{},\"payload\":{}}";

  private FfmlibDecodeService decoder;
  private FfmlibOutputPublisher output;
  private KafkaTemplate<String, String> quarantineProducer;
  private SimpleMeterRegistry meters;
  private FfmlibDirectUdpDecoder direct;
  private UdpDecodeInput input;
  private PreparedDecodedMessage prepared;

  @BeforeEach
  @SuppressWarnings("unchecked")
  void setUp() {
    decoder = mock(FfmlibDecodeService.class);
    output = mock(FfmlibOutputPublisher.class);
    quarantineProducer = mock(KafkaTemplate.class);
    when(quarantineProducer.send(any(ProducerRecord.class)))
        .thenReturn(new CompletableFuture<SendResult<String, String>>());
    meters = new SimpleMeterRegistry();
    FfmlibProperties properties = new FfmlibProperties();
    properties.setInfrastructureHaltThreshold(2);
    direct = new FfmlibDirectUdpDecoder(decoder, output, quarantineProducer, properties, meters);
    OdeMessageFrameMetadata metadata = new OdeMessageFrameMetadata();
    input = new UdpDecodeInput(metadata, new byte[] {0, 20});
    prepared = new PreparedDecodedMessage("topic.OdeBsmJson", null, "{}", metadata, "BSM", "udp",
        1L, 1L);
  }

  @Test
  void publishesWithoutWaitingAndRecordsConfirmationLater() {
    when(decoder.prepareRaw(eq(input.metadata()), any(byte[].class), isNull(),
        eq(SupportedMessageType.BSM))).thenReturn(prepared);
    CompletableFuture<PublicationOutcome> confirmation = new CompletableFuture<>();
    when(output.publish(prepared)).thenReturn(confirmation);

    direct.decodeAndPublish(input, SupportedMessageType.BSM, RAW_TOPIC, () -> RAW_JSON);

    verify(output).publish(prepared);
    verify(decoder, never()).recordRawConfirmed(any());
    confirmation.complete(PublicationOutcome.PUBLISHED);
    verify(decoder).recordRawConfirmed(prepared);
  }

  @Test
  void disabledJsonTopicIsNeitherConfirmedNorAFailure() {
    when(decoder.prepareRaw(any(), any(), any(), any())).thenReturn(prepared);
    when(output.publish(prepared))
        .thenReturn(CompletableFuture.completedFuture(PublicationOutcome.SKIPPED_DISABLED));

    direct.decodeAndPublish(input, SupportedMessageType.BSM, RAW_TOPIC, () -> RAW_JSON);

    verify(decoder, never()).recordRawConfirmed(any());
    verify(decoder, never()).recordRawPublishFailure(any());
  }

  @Test
  void publicationFailureIsCountedOnceWithoutRetryOrQuarantine() {
    when(decoder.prepareRaw(any(), any(), any(), any())).thenReturn(prepared);
    when(output.publish(prepared)).thenReturn(
        CompletableFuture.failedFuture(new IllegalStateException("broker unavailable")));

    direct.decodeAndPublish(input, SupportedMessageType.BSM, RAW_TOPIC, () -> RAW_JSON);

    verify(output).publish(prepared);
    verify(decoder).recordRawPublishFailure(prepared);
    verify(quarantineProducer, never()).send(any(ProducerRecord.class));
    assertEquals(1.0, meters.counter("ode.ffmlib.direct.publish.failures", "topic",
        "topic.OdeBsmJson").count());
  }

  @Test
  @SuppressWarnings("unchecked")
  void decodeFailureIsQuarantinedWithDirectRouteHeaders() {
    when(decoder.prepareRaw(any(), any(), any(), any()))
        .thenThrow(new IllegalArgumentException("Error decoding PDU"));

    direct.decodeAndPublish(input, SupportedMessageType.BSM, RAW_TOPIC, () -> RAW_JSON);

    ArgumentCaptor<ProducerRecord<String, String>> deadLetter =
        ArgumentCaptor.forClass(ProducerRecord.class);
    verify(quarantineProducer).send(deadLetter.capture());
    verify(output, never()).publish(any());
    ProducerRecord<String, String> record = deadLetter.getValue();
    assertEquals(DLQ_TOPIC, record.topic());
    assertEquals(RAW_JSON, record.value());
    assertEquals("malformed_payload", header(record, "failure-category"));
    assertEquals("direct", header(record, "ingest-route"));
    assertEquals(RAW_TOPIC, header(record, "source-topic"));
    assertNull(record.headers().lastHeader("source-offset"));
    assertEquals(Status.UP, direct.health().getStatus());
  }

  @Test
  void unsupportedSignedPayloadKeepsItsCategory() {
    when(decoder.prepareRaw(any(), any(), any(), any()))
        .thenThrow(new UnsupportedOperationException("signed"));

    direct.decodeAndPublish(input, SupportedMessageType.BSM, RAW_TOPIC, () -> RAW_JSON);

    assertEquals("unsupported_signed", header(lastDeadLetter(), "failure-category"));
  }

  @Test
  void consecutiveInfrastructureFailuresMarkHealthDownUntilTheNextSuccess() {
    FfmlibDecodeService.InfrastructureFailure failure =
        new FfmlibDecodeService.InfrastructureFailure("native capacity", null);
    when(decoder.prepareRaw(any(), any(), any(), any())).thenThrow(failure);

    direct.decodeAndPublish(input, SupportedMessageType.BSM, RAW_TOPIC, () -> RAW_JSON);
    assertEquals(Status.UP, direct.health().getStatus());
    direct.decodeAndPublish(input, SupportedMessageType.BSM, RAW_TOPIC, () -> RAW_JSON);

    assertEquals(Status.DOWN, direct.health().getStatus());
    assertEquals("infrastructure", header(lastDeadLetter(), "failure-category"));
    assertEquals(2.0, meters.counter("ode.ffmlib.infrastructure.quarantined", "type", "BSM")
        .count());

    org.mockito.Mockito.doReturn(prepared).when(decoder).prepareRaw(any(), any(), any(), any());
    when(output.publish(prepared)).thenReturn(new CompletableFuture<>());
    direct.decodeAndPublish(input, SupportedMessageType.BSM, RAW_TOPIC, () -> RAW_JSON);

    assertEquals(Status.UP, direct.health().getStatus());
  }

  @Test
  void quarantineSubmissionFailureIsCounted() {
    when(decoder.prepareRaw(any(), any(), any(), any()))
        .thenThrow(new IllegalArgumentException("Error decoding PDU"));
    when(quarantineProducer.send(any(ProducerRecord.class)))
        .thenThrow(new IllegalStateException("producer closed"));

    direct.decodeAndPublish(input, SupportedMessageType.BSM, RAW_TOPIC, () -> RAW_JSON);

    assertEquals(1.0, meters.counter("ode.ffmlib.direct.quarantine.failures", "topic", DLQ_TOPIC)
        .count());
  }

  @SuppressWarnings("unchecked")
  private ProducerRecord<String, String> lastDeadLetter() {
    ArgumentCaptor<ProducerRecord<String, String>> deadLetter =
        ArgumentCaptor.forClass(ProducerRecord.class);
    verify(quarantineProducer, org.mockito.Mockito.atLeastOnce()).send(deadLetter.capture());
    return deadLetter.getValue();
  }

  private static String header(ProducerRecord<String, String> record, String name) {
    return new String(record.headers().lastHeader(name).value(), StandardCharsets.UTF_8);
  }
}
