package us.dot.its.jpo.ode.codec.ffmlib;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import us.dot.its.jpo.ode.codec.ffmlib.FfmlibDecodeService.PreparedDecodedMessage;
import us.dot.its.jpo.ode.kafka.listeners.json.RawEncodedJsonService;
import us.dot.its.jpo.ode.kafka.topics.JsonTopics;
import us.dot.its.jpo.ode.kafka.topics.RawEncodedJsonTopics;
import us.dot.its.jpo.ode.model.OdeLogMetadata.SecurityResultCode;
import us.dot.its.jpo.ode.model.OdeMessageFrameMetadata;
import us.dot.its.jpo.ode.uper.SupportedMessageType;

class FfmlibRawJsonListenerTest {

  private static final String RAW_TOPIC = "topic.OdeRawEncodedBSMJson";
  private static final String RAW_JSON = "{\"payload\":{\"data\":{\"bytes\":\"0014\"}}}";

  private RawEncodedJsonService rawService;
  private FfmlibDecodeService decoder;
  private FfmlibOutputPublisher output;
  private KafkaTemplate<String, String> quarantineProducer;
  private RawEncodedJsonTopics topics;
  private FfmlibCommitTracker commitTracker;
  private FfmlibRawJsonListener listener;
  private ConsumerRecord<String, String> record;
  private Acknowledgment acknowledgment;
  private OdeMessageFrameMetadata metadata;
  private PreparedDecodedMessage prepared;

  @BeforeEach
  void setUp() throws Exception {
    rawService = mock(RawEncodedJsonService.class);
    decoder = mock(FfmlibDecodeService.class);
    output = mock(FfmlibOutputPublisher.class);
    quarantineProducer = mock(KafkaTemplate.class);
    topics = mock(RawEncodedJsonTopics.class);
    commitTracker = mock(FfmlibCommitTracker.class);
    when(topics.getBsm()).thenReturn(RAW_TOPIC);
    listener = new FfmlibRawJsonListener(rawService, decoder, output, quarantineProducer, topics,
        commitTracker, new SimpleMeterRegistry());
    listener.markStartupComplete();
    metadata = new OdeMessageFrameMetadata();
    metadata.setAsn1("0014");
    var rawData = new RawEncodedJsonService.FfmDecodeInput(metadata, new byte[] {0, 20});
    when(rawService.parseFfmDecodeInput(eq(RAW_JSON), eq(SupportedMessageType.BSM)))
        .thenReturn(rawData);
    prepared = new PreparedDecodedMessage("topic.OdeBsmJson", "packet-key", "{}", metadata,
        "BSM", "udp", 1L, 1L);
    when(decoder.prepareRaw(eq(metadata), any(byte[].class), eq("packet-key"),
        eq(SupportedMessageType.BSM))).thenReturn(prepared);
    record = new ConsumerRecord<>(RAW_TOPIC, 0, 7, "packet-key", RAW_JSON);
    acknowledgment = mock(Acknowledgment.class);
  }

  @Test
  void confirmsJsonBeforeAcknowledgingRawInput() throws Exception {
    CompletableFuture<FfmlibOutputPublisher.PublicationOutcome> confirmation =
        new CompletableFuture<>();
    when(output.publish(prepared)).thenReturn(confirmation);
    AtomicReference<Throwable> listenerFailure = new AtomicReference<>();
    Thread worker = new Thread(() -> invokeListener(listenerFailure));
    worker.start();

    verify(output, timeout(1000)).publish(prepared);
    verify(acknowledgment, never()).acknowledge();
    confirmation.complete(FfmlibOutputPublisher.PublicationOutcome.PUBLISHED);
    worker.join(1000);

    assertFalse(worker.isAlive());
    assertEquals(null, listenerFailure.get());
    verify(decoder).recordRawConfirmed(prepared);
    verify(acknowledgment).acknowledge();
  }

  @Test
  void retriesTransientPublicationTwiceThenAcknowledges() {
    when(output.publish(prepared)).thenReturn(
        CompletableFuture.failedFuture(new IllegalStateException("first")),
        CompletableFuture.failedFuture(new IllegalStateException("second")),
        CompletableFuture.completedFuture(FfmlibOutputPublisher.PublicationOutcome.PUBLISHED));

    listener.bsm(record, acknowledgment);

    verify(output, times(3)).publish(prepared);
    verify(decoder).recordRawConfirmed(prepared);
    verify(acknowledgment).acknowledge();
  }

  @Test
  void exhaustedOutputRetriesLeaveOffsetUncommittedAndListenerUnhealthy() {
    when(output.publish(prepared)).thenReturn(
        CompletableFuture.failedFuture(new IllegalStateException("one")),
        CompletableFuture.failedFuture(new IllegalStateException("two")),
        CompletableFuture.failedFuture(new IllegalStateException("three")));

    assertThrows(RuntimeException.class, () -> listener.bsm(record, acknowledgment));

    verify(output, times(3)).publish(prepared);
    verify(decoder).recordRawPublishFailure(prepared);
    verify(acknowledgment, never()).acknowledge();
    assertEquals("DOWN", listener.health().getStatus().getCode());
  }

  @Test
  @SuppressWarnings("unchecked")
  void signedStrippedUperIsPublishedAndConfirmedBeforeAcknowledgement() throws Exception {
    String signedJson = "{\"payload\":{\"data\":{\"bytes\":\"03810014\"}}}";
    byte[] strippedBytes = {0x00, 0x14};
    metadata.setAsn1((String) null);
    metadata.setCertPresent(true);
    metadata.setSecurityResultCode(SecurityResultCode.spduCryptoVerificationFailure);
    var rawData = new RawEncodedJsonService.FfmDecodeInput(metadata, strippedBytes);
    when(rawService.parseFfmDecodeInput(eq(signedJson), eq(SupportedMessageType.BSM)))
        .thenReturn(rawData);
    CompletableFuture<FfmlibOutputPublisher.PublicationOutcome> confirmation =
        new CompletableFuture<>();
    when(output.publish(prepared)).thenReturn(confirmation);
    ConsumerRecord<String, String> signedRecord = new ConsumerRecord<>(RAW_TOPIC, 0, 8,
        "packet-key", signedJson);
    AtomicReference<Throwable> listenerFailure = new AtomicReference<>();
    Thread worker = new Thread(() -> {
      try {
        listener.bsm(signedRecord, acknowledgment);
      } catch (Throwable error) {
        listenerFailure.set(error);
      }
    });
    worker.start();

    verify(output, timeout(1000)).publish(prepared);
    verify(decoder).prepareRaw(eq(metadata), eq(strippedBytes), eq("packet-key"),
        eq(SupportedMessageType.BSM));
    verify(acknowledgment, never()).acknowledge();
    verify(quarantineProducer, never()).send(any(ProducerRecord.class));

    confirmation.complete(FfmlibOutputPublisher.PublicationOutcome.PUBLISHED);
    worker.join(1000);

    assertFalse(worker.isAlive());
    assertEquals(null, listenerFailure.get());
    assertTrue(metadata.isCertPresent());
    assertEquals(SecurityResultCode.spduCryptoVerificationFailure,
        metadata.getSecurityResultCode());
    verify(decoder).recordRawConfirmed(prepared);
    verify(acknowledgment).acknowledge();
  }

  @Test
  @SuppressWarnings("unchecked")
  void malformedInputIsQuarantinedWithSourceHeadersBeforeAcknowledgement() throws Exception {
    when(rawService.parseFfmDecodeInput(anyString(), eq(SupportedMessageType.BSM)))
        .thenThrow(new JsonProcessingException("invalid") {});
    when(quarantineProducer.send(any(ProducerRecord.class)))
        .thenReturn(CompletableFuture.completedFuture(null));

    listener.bsm(record, acknowledgment);

    var captor = org.mockito.ArgumentCaptor.forClass(ProducerRecord.class);
    verify(quarantineProducer).send(captor.capture());
    ProducerRecord<String, String> quarantined = captor.getValue();
    assertEquals("dlq.OdeRawEncodedBSMJson", quarantined.topic());
    assertEquals(record.key(), quarantined.key());
    assertEquals(record.value(), quarantined.value());
    assertEquals("topic.OdeRawEncodedBSMJson",
        new String(quarantined.headers().lastHeader("source-topic").value(),
            StandardCharsets.UTF_8));
    assertEquals("7", new String(quarantined.headers().lastHeader("source-offset").value(),
        StandardCharsets.UTF_8));
    assertEquals("malformed_json",
        new String(quarantined.headers().lastHeader("failure-category").value(),
            StandardCharsets.UTF_8));
    verify(acknowledgment).acknowledge();
    verify(output, never()).publish(any());
  }

  @Test
  @SuppressWarnings("unchecked")
  void failedQuarantineLeavesInputUncommittedAndMarksHealthDown() throws Exception {
    when(rawService.parseFfmDecodeInput(anyString(), eq(SupportedMessageType.BSM)))
        .thenThrow(new IllegalArgumentException("bad bytes"));
    when(quarantineProducer.send(any(ProducerRecord.class)))
        .thenReturn(CompletableFuture.failedFuture(new IllegalStateException("broker down")));

    assertThrows(IllegalStateException.class, () -> listener.bsm(record, acknowledgment));

    verify(acknowledgment, never()).acknowledge();
    assertEquals("DOWN", listener.health().getStatus().getCode());
  }

  @Test
  @SuppressWarnings("unchecked")
  void nativeInfrastructureFailureMarksListenerDownWithoutQuarantineOrAcknowledgment() {
    IllegalStateException nativeFailure = new IllegalStateException("native buffer capacity");
    FfmlibDecodeService.InfrastructureFailure failure =
        new FfmlibDecodeService.InfrastructureFailure("native conversion unavailable",
            nativeFailure);
    when(decoder.prepareRaw(eq(metadata), any(byte[].class), eq("packet-key"),
        eq(SupportedMessageType.BSM))).thenThrow(failure);
    listener = listenerWithHaltThreshold(1);

    FfmlibDecodeService.InfrastructureFailure thrown = assertThrows(
        FfmlibDecodeService.InfrastructureFailure.class,
        () -> listener.bsm(record, acknowledgment));

    assertSame(failure, thrown);
    verify(quarantineProducer, never()).send(any(ProducerRecord.class));
    verify(output, never()).publish(any());
    verify(acknowledgment, never()).acknowledge();
    assertEquals("DOWN", listener.health().getStatus().getCode());
  }

  @Test
  @SuppressWarnings("unchecked")
  void infrastructureFailureBelowHaltThresholdIsQuarantinedWithoutRetry() {
    when(decoder.prepareRaw(eq(metadata), any(byte[].class), eq("packet-key"),
        eq(SupportedMessageType.BSM))).thenThrow(
            new FfmlibDecodeService.InfrastructureFailure("native capacity", null));
    when(quarantineProducer.send(any(ProducerRecord.class)))
        .thenReturn(CompletableFuture.completedFuture(null));
    SimpleMeterRegistry meters = new SimpleMeterRegistry();
    listener = listenerWithHaltThreshold(3, meters);

    listener.bsm(record, acknowledgment);

    verify(decoder, times(1)).prepareRaw(eq(metadata), any(byte[].class), eq("packet-key"),
        eq(SupportedMessageType.BSM));
    var captor = org.mockito.ArgumentCaptor.forClass(ProducerRecord.class);
    verify(quarantineProducer).send(captor.capture());
    ProducerRecord<String, String> deadLetter = captor.getValue();
    assertEquals("dlq.OdeRawEncodedBSMJson", deadLetter.topic());
    assertEquals("infrastructure",
        new String(deadLetter.headers().lastHeader("failure-category").value(),
            StandardCharsets.UTF_8));
    assertEquals("1", new String(deadLetter.headers().lastHeader("attempt-count").value(),
        StandardCharsets.UTF_8));
    verify(acknowledgment).acknowledge();
    verify(output, never()).publish(any());
    assertEquals("UP", listener.health().getStatus().getCode());
    assertEquals(1.0, meters.get("ode.ffmlib.infrastructure.quarantined").tag("type", "BSM")
        .counter().count());
  }

  @Test
  @SuppressWarnings("unchecked")
  void consecutiveInfrastructureFailuresReachingThresholdStopTheListener() {
    FfmlibDecodeService.InfrastructureFailure failure =
        new FfmlibDecodeService.InfrastructureFailure("codec missing", null);
    when(decoder.prepareRaw(eq(metadata), any(byte[].class), eq("packet-key"),
        eq(SupportedMessageType.BSM))).thenThrow(failure);
    when(quarantineProducer.send(any(ProducerRecord.class)))
        .thenReturn(CompletableFuture.completedFuture(null));
    listener = listenerWithHaltThreshold(2);
    ConsumerRecord<String, String> next =
        new ConsumerRecord<>(RAW_TOPIC, 0, 8, "packet-key", RAW_JSON);
    Acknowledgment nextAcknowledgment = mock(Acknowledgment.class);

    listener.bsm(record, acknowledgment);
    FfmlibDecodeService.InfrastructureFailure thrown = assertThrows(
        FfmlibDecodeService.InfrastructureFailure.class,
        () -> listener.bsm(next, nextAcknowledgment));

    assertSame(failure, thrown);
    verify(acknowledgment).acknowledge();
    verify(nextAcknowledgment, never()).acknowledge();
    verify(quarantineProducer, times(1)).send(any(ProducerRecord.class));
    assertEquals("DOWN", listener.health().getStatus().getCode());
  }

  @Test
  @SuppressWarnings("unchecked")
  void confirmedRecordAfterFailuresClearsHealthAndConsecutiveCount() {
    FfmlibDecodeService.InfrastructureFailure failure =
        new FfmlibDecodeService.InfrastructureFailure("native capacity", null);
    when(decoder.prepareRaw(eq(metadata), any(byte[].class), eq("packet-key"),
        eq(SupportedMessageType.BSM))).thenThrow(failure).thenReturn(prepared)
        .thenThrow(failure);
    when(output.publish(prepared)).thenReturn(
        CompletableFuture.completedFuture(FfmlibOutputPublisher.PublicationOutcome.PUBLISHED));
    when(quarantineProducer.send(any(ProducerRecord.class)))
        .thenReturn(CompletableFuture.completedFuture(null));
    listener = listenerWithHaltThreshold(2);

    listener.bsm(record, acknowledgment);
    listener.recordCommitFailure("RawEncodedBSMJsonRouter", new IllegalStateException("commit"));
    assertEquals("DOWN", listener.health().getStatus().getCode());
    listener.bsm(record, acknowledgment);
    assertEquals("UP", listener.health().getStatus().getCode());
    listener.bsm(record, acknowledgment);

    verify(quarantineProducer, times(2)).send(any(ProducerRecord.class));
    verify(acknowledgment, times(3)).acknowledge();
    assertEquals("UP", listener.health().getStatus().getCode());
  }

  @Test
  @SuppressWarnings("unchecked")
  void jerMappingFailureUsesDistinctMappingFailureCategory() throws Exception {
    JsonProcessingException mappingCause = new JsonProcessingException("cannot map JER") {};
    when(decoder.prepareRaw(eq(metadata), any(byte[].class), eq("packet-key"),
        eq(SupportedMessageType.BSM)))
            .thenThrow(new IllegalArgumentException("unable to map decoded ASN.1 JER",
                mappingCause));
    when(quarantineProducer.send(any(ProducerRecord.class)))
        .thenReturn(CompletableFuture.completedFuture(null));

    listener.bsm(record, acknowledgment);

    var captor = org.mockito.ArgumentCaptor.forClass(ProducerRecord.class);
    verify(quarantineProducer).send(captor.capture());
    assertEquals("mapping_failure", new String(
        captor.getValue().headers().lastHeader("failure-category").value(),
        StandardCharsets.UTF_8));
    verify(acknowledgment).acknowledge();
    verify(output, never()).publish(any());
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "{\"messageId\":999,\"value\":{}}",
      "{\"value\":{}}"
  })
  @SuppressWarnings("unchecked")
  void realGenericMessageFrameReaderFailuresAreQuarantinedAsMapping(String jer) throws Exception {
    SimpleMeterRegistry meters = new SimpleMeterRegistry();
    FfmlibRawJsonListener realListener = listenerWithRealMessageFrameReader(jer, meters);
    when(quarantineProducer.send(any(ProducerRecord.class))).thenAnswer(invocation -> {
      verify(acknowledgment, never()).acknowledge();
      return CompletableFuture.completedFuture(null);
    });

    realListener.bsm(record, acknowledgment);

    var captor = org.mockito.ArgumentCaptor.forClass(ProducerRecord.class);
    verify(quarantineProducer).send(captor.capture());
    assertEquals("mapping_failure", new String(
        captor.getValue().headers().lastHeader("failure-category").value(),
        StandardCharsets.UTF_8));
    assertEquals(1.0, meters.get("ode.ffmlib.decode.failures")
        .tag("type", "BSM").tag("source", "udp").tag("reason", "mapping")
        .counter().count());
    assertTrue(meters.find("ode.ffmlib.decode.failures")
        .tag("type", "BSM").tag("source", "udp").tag("reason", "infrastructure")
        .counters().isEmpty());
    verify(output, never()).publish(any());
    verify(acknowledgment).acknowledge();
  }

  @Test
  @SuppressWarnings("unchecked")
  void realGenericMessageFrameMappingFailureDoesNotAcknowledgeWhenDltConfirmationFails()
      throws Exception {
    SimpleMeterRegistry meters = new SimpleMeterRegistry();
    FfmlibRawJsonListener realListener = listenerWithRealMessageFrameReader(
        "{\"messageId\":999,\"value\":{}}", meters);
    when(quarantineProducer.send(any(ProducerRecord.class)))
        .thenReturn(CompletableFuture.failedFuture(new IllegalStateException("broker down")));

    assertThrows(IllegalStateException.class, () -> realListener.bsm(record, acknowledgment));

    var captor = org.mockito.ArgumentCaptor.forClass(ProducerRecord.class);
    verify(quarantineProducer).send(captor.capture());
    assertEquals("mapping_failure", new String(
        captor.getValue().headers().lastHeader("failure-category").value(),
        StandardCharsets.UTF_8));
    assertEquals(1.0, meters.get("ode.ffmlib.decode.failures")
        .tag("type", "BSM").tag("source", "udp").tag("reason", "mapping")
        .counter().count());
    verify(output, never()).publish(any());
    verify(acknowledgment, never()).acknowledge();
    assertEquals("DOWN", realListener.health().getStatus().getCode());
  }

  @Test
  @SuppressWarnings("unchecked")
  void realParserQuarantinesInvalidMetadataHexAsMalformedPayload() throws Exception {
    useRealParser();
    String invalidHexJson = "{\"metadata\":{\"asn1\":\"gg\"},"
        + "\"payload\":{\"data\":{\"bytes\":\"0014\"}}}";
    record = new ConsumerRecord<>(RAW_TOPIC, 0, 12, "packet-key", invalidHexJson);
    when(quarantineProducer.send(any(ProducerRecord.class)))
        .thenReturn(CompletableFuture.completedFuture(null));

    listener.bsm(record, acknowledgment);

    var captor = org.mockito.ArgumentCaptor.forClass(ProducerRecord.class);
    verify(quarantineProducer).send(captor.capture());
    assertEquals("malformed_payload", new String(
        captor.getValue().headers().lastHeader("failure-category").value(),
        StandardCharsets.UTF_8));
    verify(acknowledgment).acknowledge();
    verify(output, never()).publish(any());
    verify(decoder, never()).prepareRaw(any(), any(byte[].class), anyString(), any());
  }

  @Test
  @SuppressWarnings("unchecked")
  void realParserKeepsMetadataMappingErrorsInMalformedJsonCategory() throws Exception {
    useRealParser();
    String invalidMetadataJson = "{\"metadata\":{\"securityResultCode\":\"bad-code\"},"
        + "\"payload\":{\"data\":{\"bytes\":\"0014\"}}}";
    record = new ConsumerRecord<>(RAW_TOPIC, 0, 13, "packet-key", invalidMetadataJson);
    when(quarantineProducer.send(any(ProducerRecord.class)))
        .thenReturn(CompletableFuture.completedFuture(null));

    listener.bsm(record, acknowledgment);

    var captor = org.mockito.ArgumentCaptor.forClass(ProducerRecord.class);
    verify(quarantineProducer).send(captor.capture());
    assertEquals("malformed_json", new String(
        captor.getValue().headers().lastHeader("failure-category").value(),
        StandardCharsets.UTF_8));
    verify(acknowledgment).acknowledge();
    verify(output, never()).publish(any());
    verify(decoder, never()).prepareRaw(any(), any(byte[].class), anyString(), any());
  }

  @Test
  void interruptedConfirmationDoesNotAcknowledgeTheInput() throws Exception {
    CompletableFuture<FfmlibOutputPublisher.PublicationOutcome> confirmation =
        new CompletableFuture<>();
    when(output.publish(prepared)).thenReturn(confirmation);
    AtomicReference<Throwable> listenerFailure = new AtomicReference<>();
    Thread worker = new Thread(() -> invokeListener(listenerFailure));
    worker.start();
    verify(output, timeout(1000)).publish(prepared);

    worker.interrupt();
    worker.join(1000);

    assertFalse(worker.isAlive());
    assertTrue(worker.isInterrupted());
    assertTrue(listenerFailure.get() instanceof IllegalStateException);
    verify(acknowledgment, never()).acknowledge();
    confirmation.complete(FfmlibOutputPublisher.PublicationOutcome.PUBLISHED);
  }

  @Test
  void unresolvedPublicationTimesOutWithoutStartingAnOverlappingSend() {
    listener = new FfmlibRawJsonListener(rawService, decoder, output, quarantineProducer, topics,
        commitTracker, new SimpleMeterRegistry(), Duration.ofMillis(100));
    CompletableFuture<FfmlibOutputPublisher.PublicationOutcome> unresolved =
        new CompletableFuture<>();
    when(output.publish(prepared)).thenReturn(unresolved);

    assertThrows(RuntimeException.class, () -> listener.bsm(record, acknowledgment));

    verify(output, times(1)).publish(prepared);
    verify(decoder).recordRawPublishFailure(prepared);
    verify(acknowledgment, never()).acknowledge();
    assertEquals("DOWN", listener.health().getStatus().getCode());
    unresolved.complete(FfmlibOutputPublisher.PublicationOutcome.PUBLISHED);
  }

  @Test
  void readinessRemainsDownUntilProvisioningFinishes() {
    FfmlibRawJsonListener starting = new FfmlibRawJsonListener(rawService, decoder, output,
        quarantineProducer, topics, commitTracker, new SimpleMeterRegistry());

    assertEquals("DOWN", starting.health().getStatus().getCode());
    starting.markStartupComplete();
    assertEquals("UP", starting.health().getStatus().getCode());
  }

  @Test
  void disabledJsonDestinationIsAcknowledgedAsIntentionalSkip() {
    when(output.publish(prepared)).thenReturn(CompletableFuture.completedFuture(
        FfmlibOutputPublisher.PublicationOutcome.SKIPPED_DISABLED));

    listener.bsm(record, acknowledgment);

    verify(acknowledgment).acknowledge();
    verify(decoder, never()).recordRawConfirmed(prepared);
  }

  private FfmlibRawJsonListener listenerWithHaltThreshold(int haltThreshold) {
    return listenerWithHaltThreshold(haltThreshold, new SimpleMeterRegistry());
  }

  private FfmlibRawJsonListener listenerWithHaltThreshold(int haltThreshold,
      SimpleMeterRegistry meters) {
    FfmlibProperties properties = new FfmlibProperties();
    properties.setInfrastructureHaltThreshold(haltThreshold);
    FfmlibRawJsonListener configured = new FfmlibRawJsonListener(rawService, decoder, output,
        quarantineProducer, topics, commitTracker, meters, Duration.ofSeconds(30), properties);
    configured.markStartupComplete();
    return configured;
  }

  private void invokeListener(AtomicReference<Throwable> failure) {
    try {
      listener.bsm(record, acknowledgment);
    } catch (Throwable error) {
      failure.set(error);
    }
  }

  private void useRealParser() {
    rawService = new RawEncodedJsonService(new ObjectMapper());
    listener = new FfmlibRawJsonListener(rawService, decoder, output, quarantineProducer, topics,
        commitTracker, new SimpleMeterRegistry());
    listener.markStartupComplete();
  }

  @SuppressWarnings("unchecked")
  private FfmlibRawJsonListener listenerWithRealMessageFrameReader(String jer,
      SimpleMeterRegistry meters) {
    FfmlibMessageFrameCodec nativeCodec = mock(FfmlibMessageFrameCodec.class);
    ObjectProvider<FfmlibMessageFrameCodec> codecProvider = mock(ObjectProvider.class);
    when(codecProvider.getIfAvailable()).thenReturn(nativeCodec);
    when(nativeCodec.uperToIntermediate(any(byte[].class))).thenReturn(
        new FfmlibMessageFrameCodec.IntermediateDecodeResult(
            jer.getBytes(StandardCharsets.UTF_8),
            FfmlibMessageFrameCodec.IntermediateEncoding.JER));
    ObjectProvider<FfmlibOutputPublisher> outputProvider = mock(ObjectProvider.class);
    when(outputProvider.getObject()).thenReturn(output);
    Asn1CodecModeProperties mode = new Asn1CodecModeProperties();
    mode.setCodecMode(Asn1CodecModeProperties.CodecMode.ffm);
    FfmlibDecodeService realDecoder = new FfmlibDecodeService(codecProvider,
        new FfmlibProperties(), mode, new JsonTopics(),
        mock(KafkaTemplate.class), outputProvider, new ObjectMapper(), meters,
        "topic.Asn1DecoderInput");
    FfmlibRawJsonListener realListener = new FfmlibRawJsonListener(rawService, realDecoder,
        output, quarantineProducer, topics, commitTracker, meters);
    realListener.markStartupComplete();
    return realListener;
  }
}
