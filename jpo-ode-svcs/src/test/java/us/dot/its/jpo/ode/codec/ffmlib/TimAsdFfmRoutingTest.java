package us.dot.its.jpo.ode.codec.ffmlib;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import j2735ffm.AsnEncoding;
import j2735ffm.MessageFrameCodec;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.beans.factory.ObjectProvider;
import us.dot.its.jpo.ode.OdeTimJsonTopology;
import us.dot.its.jpo.ode.kafka.listeners.asn1.Asn1EncodedDataRouter;
import us.dot.its.jpo.ode.kafka.topics.Asn1CoderTopics;
import us.dot.its.jpo.ode.kafka.topics.JsonTopics;
import us.dot.its.jpo.ode.rsu.RsuDepositor;
import us.dot.its.jpo.ode.security.SecurityServicesClient;
import us.dot.its.jpo.ode.security.SecurityServicesProperties;
import us.dot.its.jpo.ode.security.models.SignatureResultModel;
import us.dot.its.jpo.ode.model.SDXDeposit;
import us.dot.its.jpo.ode.util.CodecUtils;

class TimAsdFfmRoutingTest {

  private static final String ENCODER_INPUT = "topic.Asn1EncoderInput";
  private static final String SDX_INPUT = "topic.SdxDepositorInput";

  private final KafkaTemplate<String, String> kafka = mock(KafkaTemplate.class);
  private final FfmlibEncodeService encoder = mock(FfmlibEncodeService.class);
  private final SecurityServicesClient security = mock(SecurityServicesClient.class);
  private final SecurityServicesProperties signing = new SecurityServicesProperties();
  private final Asn1CodecModeProperties mode = new Asn1CodecModeProperties();

  @org.junit.jupiter.api.BeforeEach
  void kafkaAndRouterMocksHaveDeliberateDefaults() throws Exception {
    signing.setIsRsuSigningEnabled(false);
    signing.setIsSdwSigningEnabled(false);
    when(kafka.send(anyString(), anyString())).thenReturn(completedSend());
    when(encoder.encodeOdeAsn1Xml(anyString())).thenReturn(fixture(
        "asn1-encoder-output-tim-with-advisory-data.xml"));
  }

  @Test
  void ffmEncodesUnsignedAsdAndDepositsWithoutExternalEncoder() throws Exception {
    mode.setCodecMode(Asn1CodecModeProperties.CodecMode.ffm);
    signing.setIsSdwSigningEnabled(false);
    when(encoder.encodeOdeAsn1Xml(anyString())).thenReturn(fixture(
        "asn1-encoder-output-tim-with-advisory-data.xml"));

    router().processEncodedAsn1Xml(fixture("asn1-encoder-output-unsigned-tim-no-advisory-data.xml"));

    verify(encoder).encodeOdeAsn1Xml(anyString());
    verify(kafka).send(org.mockito.ArgumentMatchers.eq(SDX_INPUT), anyString());
    verify(kafka, never()).send(org.mockito.ArgumentMatchers.eq(ENCODER_INPUT), anyString());
  }

  @Test
  void generatedTimAsdEncodesAndDecodesWithNativeCodec() throws Exception {
    mode.setCodecMode(Asn1CodecModeProperties.CodecMode.ffm);
    signing.setIsSdwSigningEnabled(false);
    Path library = FfmlibNativeTestSupport.requireLibraryOrSkip();
    FfmlibProperties properties = new FfmlibProperties();
    MessageFrameCodec nativeCodec = new MessageFrameCodec(properties.getTextBufferSize(),
        properties.getUperBufferSize(), properties.getErrorBufferSize(), library);
    FfmlibMessageFrameCodec codec = new FfmlibMessageFrameCodec(nativeCodec,
        new SimpleMeterRegistry());
    ObjectProvider<FfmlibMessageFrameCodec> provider = mock(ObjectProvider.class);
    when(provider.getIfAvailable()).thenReturn(codec);
    FfmlibEncodeService realEncoder = new FfmlibEncodeService(provider, new XmlMapper());

    router(realEncoder).processEncodedAsn1Xml(
        fixture("asn1-encoder-output-unsigned-tim-no-advisory-data.xml"));

    ArgumentCaptor<String> depositJson = ArgumentCaptor.forClass(String.class);
    verify(kafka).send(org.mockito.ArgumentMatchers.eq(SDX_INPUT), depositJson.capture());
    SDXDeposit deposit = new ObjectMapper().readValue(depositJson.getValue(), SDXDeposit.class);
    String decoded = codec.decodeToXer(CodecUtils.fromHex(deposit.getEncodedMsg()),
        "AdvisorySituationData", AsnEncoding.UPER);
    assertTrue(decoded.contains("<AdvisorySituationData>"));
    assertTrue(decoded.contains("<advisoryMessage>001F8084"));
    verify(kafka, never()).send(org.mockito.ArgumentMatchers.eq(ENCODER_INPUT), anyString());
  }

  @Test
  void ffmPassesSignedTimIntoAsd() throws Exception {
    mode.setCodecMode(Asn1CodecModeProperties.CodecMode.ffm);
    signing.setIsSdwSigningEnabled(true);
    String signedHex = "0380001F01020304";
    SignatureResultModel result = new SignatureResultModel();
    result.setMessageSigned(CodecUtils.toBase64(CodecUtils.fromHex(signedHex)));
    result.setMessageExpiry(1710000000L);
    when(security.signMessage(anyString(), anyInt())).thenReturn(result);
    when(encoder.encodeOdeAsn1Xml(anyString())).thenReturn(fixture(
        "asn1-encoder-output-tim-with-advisory-data.xml"));

    router().processEncodedAsn1Xml(fixture("asn1-encoder-output-unsigned-tim-no-advisory-data.xml"));

    ArgumentCaptor<String> asdInput = ArgumentCaptor.forClass(String.class);
    verify(encoder).encodeOdeAsn1Xml(asdInput.capture());
    assertTrue(asdInput.getValue().contains("<advisoryMessage>" + signedHex + "</advisoryMessage>"));
    verify(kafka).send(org.mockito.ArgumentMatchers.eq(SDX_INPUT), anyString());
    verify(kafka, never()).send(org.mockito.ArgumentMatchers.eq(ENCODER_INPUT), anyString());
  }

  @Test
  void ffmSkipsAsdWithoutSdwAndDoesNotDepositOnEncodeFailure() throws Exception {
    mode.setCodecMode(Asn1CodecModeProperties.CodecMode.ffm);
    signing.setIsSdwSigningEnabled(false);
    router().processEncodedAsn1Xml(fixture("asn1-encoder-output-tim-with-rsus.xml"));
    verify(encoder, never()).encodeOdeAsn1Xml(anyString());
    verify(kafka, never()).send(org.mockito.ArgumentMatchers.eq(SDX_INPUT), anyString());

    when(encoder.encodeOdeAsn1Xml(anyString())).thenThrow(new IllegalArgumentException("invalid ASD"));
    IllegalStateException failure = assertThrows(IllegalStateException.class, () -> router()
        .processEncodedAsn1Xml(fixture("asn1-encoder-output-unsigned-tim-no-advisory-data.xml")));
    assertEquals("invalid ASD", failure.getCause().getMessage());
    verify(kafka, never()).send(org.mockito.ArgumentMatchers.eq(SDX_INPUT), anyString());
  }

  @Test
  void externalModeStillPublishesAsdToEncoderInput() throws Exception {
    signing.setIsSdwSigningEnabled(false);
    router().processEncodedAsn1Xml(fixture("asn1-encoder-output-unsigned-tim-no-advisory-data.xml"));
    verify(kafka).send(org.mockito.ArgumentMatchers.eq(ENCODER_INPUT), anyString());
    verify(encoder, never()).encodeOdeAsn1Xml(anyString());
  }

  @Test
  void ffmWaitsForRecursiveSdxPublicationFailure() throws Exception {
    mode.setCodecMode(Asn1CodecModeProperties.CodecMode.ffm);
    signing.setIsSdwSigningEnabled(false);
    CompletableFuture<SendResult<String, String>> failedSdx = new CompletableFuture<>();
    failedSdx.completeExceptionally(new IllegalStateException("SDX unavailable"));
    when(kafka.send(eq(SDX_INPUT), anyString())).thenReturn(failedSdx);

    Asn1EncodedDataRouter.Asn1EncodedDataRouterException failure = assertThrows(
        Asn1EncodedDataRouter.Asn1EncodedDataRouterException.class,
        () -> router().processEncodedAsn1Xml(
            fixture("asn1-encoder-output-unsigned-tim-no-advisory-data.xml")));

    assertTrue(failure.getCause().getMessage().contains("SDX unavailable"));
    verify(kafka).send(eq(SDX_INPUT), anyString());
  }

  @Test
  void ffmWaitsForFilteredTimAndInitialTimFutures() throws Exception {
    mode.setCodecMode(Asn1CodecModeProperties.CodecMode.ffm);
    signing.setIsSdwSigningEnabled(false);
    CompletableFuture<SendResult<String, String>> failedFiltered = new CompletableFuture<>();
    failedFiltered.completeExceptionally(new IllegalStateException("filtered topic unavailable"));
    when(kafka.send(eq("topic.TimFiltered"), anyString())).thenReturn(failedFiltered);

    var failure = assertThrows(Asn1EncodedDataRouter.Asn1EncodedDataRouterException.class,
        () -> router().processEncodedAsn1XmlWithPublishedTim(
            fixture("asn1-encoder-output-unsigned-tim-no-advisory-data.xml"),
            "{\"metadata\":{}}"));

    assertTrue(failure.getCause().getMessage().contains("filtered topic unavailable"));

    CompletableFuture<SendResult<String, String>> failedInitialTim = new CompletableFuture<>();
    failedInitialTim.completeExceptionally(new IllegalStateException("initial TIM unavailable"));
    failure = assertThrows(Asn1EncodedDataRouter.Asn1EncodedDataRouterException.class,
        () -> router().processEncodedAsn1XmlWithPublishedTim(
            fixture("asn1-encoder-output-tim-with-rsus.xml"), "{\"metadata\":{}}",
            failedInitialTim));
    assertTrue(failure.getCause().getMessage().contains("initial TIM unavailable"));
  }

  @Test
  void ffmWaitsForCertificateExpirationPublicationFailure() throws Exception {
    mode.setCodecMode(Asn1CodecModeProperties.CodecMode.ffm);
    signing.setIsSdwSigningEnabled(true);
    SignatureResultModel signature = new SignatureResultModel();
    signature.setMessageSigned(CodecUtils.toBase64(CodecUtils.fromHex("0380001F01020304")));
    signature.setMessageExpiry(1710000000L);
    when(security.signMessage(anyString(), anyInt())).thenReturn(signature);
    CompletableFuture<SendResult<String, String>> failedCertificate = new CompletableFuture<>();
    failedCertificate.completeExceptionally(new IllegalStateException("certificate topic unavailable"));
    when(kafka.send(eq("topic.TimCertExpiration"), anyString())).thenReturn(failedCertificate);
    when(encoder.encodeOdeAsn1Xml(anyString())).thenReturn(fixture(
        "asn1-encoder-output-tim-with-advisory-data.xml"));

    var failure = assertThrows(Asn1EncodedDataRouter.Asn1EncodedDataRouterException.class,
        () -> router().processEncodedAsn1XmlWithPublishedTim(
            fixture("asn1-encoder-output-unsigned-tim-no-advisory-data.xml"),
            "{\"metadata\":{}}"));

    assertTrue(failure.getCause().getMessage().contains("certificate topic unavailable"));
    verify(kafka).send(eq("topic.TimFiltered"), anyString());
    verify(kafka).send(eq(SDX_INPUT), anyString());
  }

  @Test
  void ffmSynchronousKafkaSendFailurePropagatesAsServerFailure() throws Exception {
    mode.setCodecMode(Asn1CodecModeProperties.CodecMode.ffm);
    signing.setIsSdwSigningEnabled(false);
    when(kafka.send(eq(SDX_INPUT), anyString()))
        .thenThrow(new IllegalStateException("synchronous Kafka send failure"));

    IllegalStateException failure = assertThrows(IllegalStateException.class,
        () -> router().processEncodedAsn1Xml(
            fixture("asn1-encoder-output-unsigned-tim-no-advisory-data.xml")));

    assertTrue(failure.getCause().getMessage().contains("synchronous Kafka send failure"));
  }

  @Test
  void ffmTreatsNullKafkaFutureAsFailure() throws Exception {
    mode.setCodecMode(Asn1CodecModeProperties.CodecMode.ffm);
    signing.setIsSdwSigningEnabled(false);
    when(kafka.send(eq(SDX_INPUT), anyString())).thenReturn(null);

    IllegalStateException failure = assertThrows(IllegalStateException.class,
        () -> router().processEncodedAsn1Xml(
            fixture("asn1-encoder-output-unsigned-tim-no-advisory-data.xml")));

    assertTrue(failure.getCause().getMessage().contains("null future"));
  }

  @Test
  void ffmRestoresInterruptWhenASelectedSendWaitIsInterrupted() throws Exception {
    mode.setCodecMode(Asn1CodecModeProperties.CodecMode.ffm);
    signing.setIsSdwSigningEnabled(false);
    CompletableFuture<SendResult<String, String>> interrupted = new CompletableFuture<>() {
      @Override
      public SendResult<String, String> get(long timeout, TimeUnit unit)
          throws InterruptedException {
        throw new InterruptedException("test interruption");
      }
    };
    when(kafka.send(eq(SDX_INPUT), anyString())).thenReturn(interrupted);

    try {
      assertThrows(Asn1EncodedDataRouter.Asn1EncodedDataRouterException.class,
          () -> router().processEncodedAsn1Xml(
              fixture("asn1-encoder-output-unsigned-tim-no-advisory-data.xml")));
      assertTrue(Thread.currentThread().isInterrupted());
    } finally {
      Thread.interrupted();
    }
  }

  @Test
  void externalModeDoesNotWaitForSendCompletion() throws Exception {
    CompletableFuture<SendResult<String, String>> pending = new CompletableFuture<>();
    when(kafka.send(eq(ENCODER_INPUT), anyString())).thenReturn(pending);

    router().processEncodedAsn1Xml(
        fixture("asn1-encoder-output-unsigned-tim-no-advisory-data.xml"));

    assertFalse(pending.isDone());
  }

  private Asn1EncodedDataRouter router() {
    return router(encoder);
  }

  private Asn1EncodedDataRouter router(FfmlibEncodeService selectedEncoder) {
    Asn1CoderTopics asn1Topics = new Asn1CoderTopics();
    asn1Topics.setEncoderInput(ENCODER_INPUT);
    JsonTopics jsonTopics = new JsonTopics();
    jsonTopics.setTimCertExpiration("topic.TimCertExpiration");
    jsonTopics.setTimTmcFiltered("topic.TimFiltered");
    return new Asn1EncodedDataRouter(asn1Topics, jsonTopics, signing,
        mock(OdeTimJsonTopology.class), mock(RsuDepositor.class), security, kafka, SDX_INPUT,
        new ObjectMapper(), new XmlMapper(), mode, selectedEncoder);
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  private static CompletableFuture<SendResult<String, String>> completedSend() {
    return CompletableFuture.completedFuture(mock(SendResult.class));
  }

  private static String fixture(String name) throws Exception {
    try (var input = TimAsdFfmRoutingTest.class.getClassLoader().getResourceAsStream(
        "us/dot/its/jpo/ode/services/asn1/" + name)) {
      return new String(input.readAllBytes(), StandardCharsets.UTF_8);
    }
  }
}
