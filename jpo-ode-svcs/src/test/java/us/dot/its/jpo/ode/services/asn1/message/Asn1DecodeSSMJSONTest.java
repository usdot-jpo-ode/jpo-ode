package us.dot.its.jpo.ode.services.asn1.message;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.kafka.clients.admin.NewTopic;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import us.dot.its.jpo.ode.kafka.OdeKafkaProperties;
import us.dot.its.jpo.ode.kafka.topics.Asn1CoderTopics;
import us.dot.its.jpo.ode.model.Asn1Encoding.EncodingRule;
import us.dot.its.jpo.ode.model.OdeAsn1Data;
import us.dot.its.jpo.ode.model.OdeAsn1Payload;
import us.dot.its.jpo.ode.model.OdeSsmMetadata;
import us.dot.its.jpo.ode.test.utilities.EmbeddedKafkaHolder;

@SuppressWarnings({"checkstyle:abbreviationAsWordInName", "checkstyle:linelength"})
@ExtendWith(SpringExtension.class)
@ContextConfiguration(initializers = ConfigDataApplicationContextInitializer.class)
@EnableConfigurationProperties(value = {OdeKafkaProperties.class, Asn1CoderTopics.class})
class Asn1DecodeSSMJSONTest {

  @Autowired
  OdeKafkaProperties odeKafkaProperties;

  @Autowired
  Asn1CoderTopics asn1CoderTopics;

  @Test
  void testProcess() throws JSONException {
    var embeddedKafka = EmbeddedKafkaHolder.getEmbeddedKafka();
    try {
      embeddedKafka.addTopics(new NewTopic(asn1CoderTopics.getDecoderInput(), 1, (short) 1));
    } catch (Exception e) {
      // ignore because we only care that the topic exists not that it was created in this test.
      // this test doesn't actually consume anything from the queue, it just needs the kafka broker running
      // and configured with the decoder input topic so that it can verify the message produced to the topic
    }

    Asn1DecodeSSMJSON testDecodeSsmJson =
        new Asn1DecodeSSMJSON(odeKafkaProperties, asn1CoderTopics.getDecoderInput());

    String json =
        "{\"metadata\":{\"recordType\":\"ssmTx\",\"securityResultCode\":\"success\",\"payloadType\":\"us.dot.its.jpo.ode.model.OdeAsn1Payload\",\"serialId\":{\"streamId\":\"c4e4e92d-dccc-45f5-813f-7d36795529a0\",\"bundleSize\":1,\"bundleId\":0,\"recordId\":0,\"serialNumber\":0},\"odeReceivedAt\":\"2024-03-15T19:31:02.907835400Z\",\"schemaVersion\":6,\"maxDurationTime\":0,\"recordGeneratedBy\":\"RSU\",\"sanitized\":false,\"originIp\":\"192.168.0.1\",\"ssmSource\":\"RSU\"},\"payload\":{\"dataType\":\"us.dot.its.jpo.ode.model.OdeHexByteArray\",\"data\":{\"bytes\":\"0381004003807C001E120000000005E9C04071A26614C06000040BA000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000\"}}}";
    OdeAsn1Data resultOdeObj = testDecodeSsmJson.process(json);

    // Validate the metadata
    OdeSsmMetadata jsonMetadataObj = (OdeSsmMetadata) resultOdeObj.getMetadata();
    assertEquals(OdeSsmMetadata.SsmSource.RSU, jsonMetadataObj.getSsmSource());
    assertEquals("unsecuredData", jsonMetadataObj.getEncodings().getFirst().getElementName());
    assertEquals("MessageFrame", jsonMetadataObj.getEncodings().getFirst().getElementType());
    assertEquals(EncodingRule.UPER, jsonMetadataObj.getEncodings().getFirst().getEncodingRule());

    // Validate the payload
    String expectedPayload = "{\"bytes\":\"001E120000000005E9C04071A26614C06000040BA000\"}";
    OdeAsn1Payload jsonPayloadObj = (OdeAsn1Payload) resultOdeObj.getPayload();
    assertEquals("us.dot.its.jpo.ode.model.OdeHexByteArray", jsonPayloadObj.getDataType());
    assertEquals(expectedPayload, jsonPayloadObj.getData().toString());
  }
}
