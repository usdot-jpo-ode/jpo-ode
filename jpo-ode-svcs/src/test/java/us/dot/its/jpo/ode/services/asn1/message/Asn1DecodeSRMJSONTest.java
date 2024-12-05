package us.dot.its.jpo.ode.services.asn1.message;

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
import us.dot.its.jpo.ode.model.OdeSrmMetadata;
import us.dot.its.jpo.ode.testUtilities.EmbeddedKafkaHolder;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(initializers = ConfigDataApplicationContextInitializer.class)
@EnableConfigurationProperties(value = {OdeKafkaProperties.class, Asn1CoderTopics.class})
class Asn1DecodeSRMJSONTest {
    private final String json = "{\"metadata\":{\"recordType\":\"srmTx\",\"securityResultCode\":\"success\",\"payloadType\":\"us.dot.its.jpo.ode.model.OdeAsn1Payload\",\"serialId\":{\"streamId\":\"190cbd65-d1e2-488a-ba42-b7d3f03a5c69\",\"bundleSize\":1,\"bundleId\":0,\"recordId\":0,\"serialNumber\":0},\"odeReceivedAt\":\"2024-03-15T19:24:04.113614500Z\",\"schemaVersion\":6,\"maxDurationTime\":0,\"recordGeneratedBy\":\"OBU\",\"sanitized\":false,\"originIp\":\"192.168.0.1\",\"srmSource\":\"RSU\"},\"payload\":{\"dataType\":\"us.dot.its.jpo.ode.model.OdeHexByteArray\",\"data\":{\"bytes\":\"0381004003807C001D2130000010090BD341080D00855C6C0C6899853000A534F7C24CB29897694759B7C000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000\"}}}";

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

        Asn1DecodeSRMJSON testDecodeSrmJson = new Asn1DecodeSRMJSON(odeKafkaProperties, asn1CoderTopics.getDecoderInput());

        OdeAsn1Data resultOdeObj = testDecodeSrmJson.process(json);

        // Validate the metadata
        OdeSrmMetadata jsonMetadataObj = (OdeSrmMetadata) resultOdeObj.getMetadata();
        assertEquals(OdeSrmMetadata.SrmSource.RSU, jsonMetadataObj.getSrmSource());
        assertEquals("unsecuredData", jsonMetadataObj.getEncodings().getFirst().getElementName());
        assertEquals("MessageFrame", jsonMetadataObj.getEncodings().getFirst().getElementType());
        assertEquals(EncodingRule.UPER, jsonMetadataObj.getEncodings().getFirst().getEncodingRule());

        // Validate the payload
        String expectedPayload = "{\"bytes\":\"001D2130000010090BD341080D00855C6C0C6899853000A534F7C24CB29897694759B7C000\"}";
        OdeAsn1Payload jsonPayloadObj = (OdeAsn1Payload) resultOdeObj.getPayload();
        assertEquals("us.dot.its.jpo.ode.model.OdeHexByteArray", jsonPayloadObj.getDataType());
        assertEquals(expectedPayload, jsonPayloadObj.getData().toString());
    }
}
