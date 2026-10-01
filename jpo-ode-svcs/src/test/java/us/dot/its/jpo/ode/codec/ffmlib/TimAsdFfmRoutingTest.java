package us.dot.its.jpo.ode.codec.ffmlib;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
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
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.kafka.core.KafkaTemplate;
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
  void generatedTimAsdEncodesAndDecodesWithBeta2NativeCodec() throws Exception {
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

  private static String fixture(String name) throws Exception {
    try (var input = TimAsdFfmRoutingTest.class.getClassLoader().getResourceAsStream(
        "us/dot/its/jpo/ode/services/asn1/" + name)) {
      return new String(input.readAllBytes(), StandardCharsets.UTF_8);
    }
  }
}
