package us.dot.its.jpo.ode.udp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.DatagramPacket;
import java.net.InetAddress;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.kafka.core.KafkaTemplate;
import us.dot.its.jpo.ode.codec.ffmlib.Asn1CodecModeProperties;
import us.dot.its.jpo.ode.uper.SupportedMessageType;
import us.dot.its.jpo.ode.util.CodecUtils;

class UdpIngestPublisherTest {

  private static final String RAW_TOPIC = "topic.OdeRawEncodedBSMJson";
  private static final String BSM_HEX =
      "001480ADDA7CDE5517E962C66947240CB711E804C8B106B7DB7B12B3056B8AA1AA4E838D00400F86822A3CD398D89E1B"
          + "B8405B72C3C7A398C3CAFF63338526C646F4FFF524AD9E404039D5DA2FA62FEB57E305B552C7BE088B61E52A6BFC8CAF"
          + "5AF64414F3E4513FEC189F8B5E1138B824A48B29BA1F43CB12CE296BCA3DFA8F651AB44AB1B81B633B797D5645DAA4ED"
          + "ADAB4AC22A0BC38AB361443395BAA2C81CC4538E7413E9C8C3F696BB2C9B6B0000";

  @Test
  @SuppressWarnings("unchecked")
  void externalModePublishesHistoricalRawJsonContract() throws Exception {
    KafkaTemplate<String, String> external = mock(KafkaTemplate.class);
    KafkaTemplate<String, String> ffmlibRaw = mock(KafkaTemplate.class);
    ObjectProvider<KafkaTemplate<String, String>> ffmlibProvider = mock(ObjectProvider.class);
    Asn1CodecModeProperties mode = codecMode(Asn1CodecModeProperties.CodecMode.external);
    UdpIngestPublisher publisher = new UdpIngestPublisher(external, ffmlibProvider, mode);

    publisher.publish(bsmPacket(), SupportedMessageType.BSM, RAW_TOPIC);

    var sent = org.mockito.ArgumentCaptor.forClass(String.class);
    verify(external).send(eq(RAW_TOPIC), sent.capture());
    verify(ffmlibRaw, never()).send(any(), any());
    assertRawContract(sent.getValue());
  }

  @Test
  @SuppressWarnings("unchecked")
  void ffmlibModeAlsoPublishesTheDurableRawJsonContract() throws Exception {
    KafkaTemplate<String, String> external = mock(KafkaTemplate.class);
    KafkaTemplate<String, String> ffmlibRaw = mock(KafkaTemplate.class);
    ObjectProvider<KafkaTemplate<String, String>> ffmlibProvider = mock(ObjectProvider.class);
    when(ffmlibProvider.getIfAvailable()).thenReturn(ffmlibRaw);
    Asn1CodecModeProperties mode = codecMode(Asn1CodecModeProperties.CodecMode.ffm);
    UdpIngestPublisher publisher = new UdpIngestPublisher(external, ffmlibProvider, mode);

    publisher.publish(bsmPacket(), SupportedMessageType.BSM, RAW_TOPIC);

    var sent = org.mockito.ArgumentCaptor.forClass(String.class);
    verify(ffmlibRaw).send(eq(RAW_TOPIC), sent.capture());
    verify(external, never()).send(any(), any());
    assertRawContract(sent.getValue());
  }

  private static void assertRawContract(String json) throws Exception {
    var root = new ObjectMapper().readTree(json);
    assertNotNull(root.path("metadata").path("asn1").textValue());
    assertEquals("0014", root.path("payload").path("data").path("bytes").textValue()
        .substring(0, 4));
    assertEquals("bsmTx", root.path("metadata").path("recordType").asText());
  }

  private static DatagramPacket bsmPacket() throws Exception {
    byte[] bytes = CodecUtils.fromHex(BSM_HEX);
    return new DatagramPacket(bytes, bytes.length, InetAddress.getLoopbackAddress(), 46800);
  }

  private static Asn1CodecModeProperties codecMode(Asn1CodecModeProperties.CodecMode mode) {
    Asn1CodecModeProperties properties = new Asn1CodecModeProperties();
    properties.setCodecMode(mode);
    return properties;
  }
}
