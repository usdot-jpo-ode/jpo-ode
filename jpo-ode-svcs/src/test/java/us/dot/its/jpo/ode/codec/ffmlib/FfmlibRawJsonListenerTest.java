package us.dot.its.jpo.ode.codec.ffmlib;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import us.dot.its.jpo.ode.codec.ffmlib.FfmlibDecodeService.PreparedDecodedMessage;
import us.dot.its.jpo.ode.kafka.listeners.json.RawEncodedJsonService;
import us.dot.its.jpo.ode.kafka.topics.RawEncodedJsonTopics;
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
    metadata = new OdeMessageFrameMetadata();
    metadata.setAsn1("0014");
    var rawData = new RawEncodedJsonService.FfmRawRecord(metadata, new byte[] {0, 20},
        new byte[] {0, 20});
    when(rawService.parseFfmRecord(eq(RAW_JSON), eq(SupportedMessageType.BSM)))
        .thenReturn(rawData);
    prepared = new PreparedDecodedMessage("topic.OdeBsmJson", "packet-key", "{}", metadata,
        "BSM", "udp", 1L, 1L);
    when(decoder.prepareRaw(eq(metadata), any(byte[].class), eq("packet-key"),
        eq(SupportedMessageType.BSM), any(byte[].class))).thenReturn(prepared);
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
  void checksOriginalPayloadBytesForSignedReplayWithoutAsn1Metadata() throws Exception {
    String signedJson = "{\"payload\":{\"data\":{\"bytes\":\"03810014\"}}}";
    byte[] originalBytes = {0x03, (byte) 0x81, 0x00, 0x14};
    metadata.setAsn1((String) null);
    var rawData = new RawEncodedJsonService.FfmRawRecord(metadata, new byte[] {0, 20},
        originalBytes);
    when(rawService.parseFfmRecord(eq(signedJson), eq(SupportedMessageType.BSM)))
        .thenReturn(rawData);
    when(decoder.prepareRaw(eq(metadata), any(byte[].class), eq("packet-key"),
        eq(SupportedMessageType.BSM), any(byte[].class))).thenAnswer(invocation -> {
          assertArrayEquals(originalBytes, invocation.getArgument(4));
          throw new UnsupportedOperationException("signed payload");
        });
    when(quarantineProducer.send(any(ProducerRecord.class)))
        .thenReturn(CompletableFuture.completedFuture(null));
    ConsumerRecord<String, String> signedRecord = new ConsumerRecord<>(RAW_TOPIC, 0, 8,
        "packet-key", signedJson);

    listener.bsm(signedRecord, acknowledgment);

    var captor = org.mockito.ArgumentCaptor.forClass(ProducerRecord.class);
    verify(quarantineProducer).send(captor.capture());
    ProducerRecord<String, String> quarantined = captor.getValue();
    assertEquals(signedJson, quarantined.value());
    assertEquals("unsupported_signed",
        new String(quarantined.headers().lastHeader("failure-category").value()));
    verify(acknowledgment).acknowledge();
  }

  @Test
  @SuppressWarnings("unchecked")
  void malformedInputIsQuarantinedWithSourceHeadersBeforeAcknowledgement() throws Exception {
    when(rawService.parseFfmRecord(anyString(), eq(SupportedMessageType.BSM)))
        .thenThrow(new JsonProcessingException("invalid") {});
    when(quarantineProducer.send(any(ProducerRecord.class)))
        .thenReturn(CompletableFuture.completedFuture(null));

    listener.bsm(record, acknowledgment);

    var captor = org.mockito.ArgumentCaptor.forClass(ProducerRecord.class);
    verify(quarantineProducer).send(captor.capture());
    ProducerRecord<String, String> quarantined = captor.getValue();
    assertEquals(RAW_TOPIC + ".FFM.DLT", quarantined.topic());
    assertEquals(record.key(), quarantined.key());
    assertEquals(record.value(), quarantined.value());
    assertEquals("topic.OdeRawEncodedBSMJson",
        new String(quarantined.headers().lastHeader("source-topic").value()));
    assertEquals("7", new String(quarantined.headers().lastHeader("source-offset").value()));
    assertEquals("malformed_json",
        new String(quarantined.headers().lastHeader("failure-category").value()));
    verify(acknowledgment).acknowledge();
    verify(output, never()).publish(any());
  }

  @Test
  @SuppressWarnings("unchecked")
  void failedQuarantineLeavesInputUncommittedAndMarksHealthDown() throws Exception {
    when(rawService.parseFfmRecord(anyString(), eq(SupportedMessageType.BSM)))
        .thenThrow(new IllegalArgumentException("bad bytes"));
    when(quarantineProducer.send(any(ProducerRecord.class)))
        .thenReturn(CompletableFuture.failedFuture(new IllegalStateException("broker down")));

    assertThrows(IllegalStateException.class, () -> listener.bsm(record, acknowledgment));

    verify(acknowledgment, never()).acknowledge();
    assertEquals("DOWN", listener.health().getStatus().getCode());
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
  void disabledJsonDestinationIsAcknowledgedAsIntentionalSkip() {
    when(output.publish(prepared)).thenReturn(CompletableFuture.completedFuture(
        FfmlibOutputPublisher.PublicationOutcome.SKIPPED_DISABLED));

    listener.bsm(record, acknowledgment);

    verify(acknowledgment).acknowledge();
    verify(decoder, never()).recordRawConfirmed(prepared);
  }

  private void invokeListener(AtomicReference<Throwable> failure) {
    try {
      listener.bsm(record, acknowledgment);
    } catch (Throwable error) {
      failure.set(error);
    }
  }
}
