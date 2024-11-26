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
import us.dot.its.jpo.ode.model.OdePsmMetadata;
import us.dot.its.jpo.ode.testUtilities.EmbeddedKafkaHolder;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(initializers = ConfigDataApplicationContextInitializer.class)
@EnableConfigurationProperties(value = {OdeKafkaProperties.class, Asn1CoderTopics.class})
class Asn1DecodePSMJSONTest {
    private final String json = "{\"metadata\":{\"recordType\":\"psmTx\",\"securityResultCode\":\"success\",\"payloadType\":\"us.dot.its.jpo.ode.model.OdeAsn1Payload\",\"serialId\":{\"streamId\":\"fa3dfe1b-80cd-45cb-ae2c-c604a214fe56\",\"bundleSize\":1,\"bundleId\":0,\"recordId\":0,\"serialNumber\":0},\"odeReceivedAt\":\"2024-03-15T19:16:35.212860500Z\",\"schemaVersion\":6,\"maxDurationTime\":0,\"recordGeneratedBy\":\"UNKNOWN\",\"sanitized\":false,\"psmSource\":\"RSU\",\"originIp\":\"192.168.0.1\"},\"payload\":{\"dataType\":\"us.dot.its.jpo.ode.model.OdeHexByteArray\",\"data\":{\"bytes\":\"00201A0000021BD86891DE75F84DA101C13F042E2214141FFF00022C2000270000000163B2CC798601000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000\"}}}";

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
        Asn1DecodePSMJSON testDecodePsmJson = new Asn1DecodePSMJSON(odeKafkaProperties, asn1CoderTopics.getDecoderInput());

        OdeAsn1Data resultOdeObj = testDecodePsmJson.process(json);

        // Validate the metadata
        OdePsmMetadata jsonMetadataObj = (OdePsmMetadata) resultOdeObj.getMetadata();
        assertEquals(OdePsmMetadata.PsmSource.RSU, jsonMetadataObj.getPsmSource());
        assertEquals("unsecuredData", jsonMetadataObj.getEncodings().getFirst().getElementName());
        assertEquals("MessageFrame", jsonMetadataObj.getEncodings().getFirst().getElementType());
        assertEquals(EncodingRule.UPER, jsonMetadataObj.getEncodings().getFirst().getEncodingRule());

        // Validate the payload
        String expectedPayload = "{\"bytes\":\"00201A0000021BD86891DE75F84DA101C13F042E2214141FFF00022C2000270000000163B2CC79860100\"}";
        OdeAsn1Payload jsonPayloadObj = (OdeAsn1Payload) resultOdeObj.getPayload();
        assertEquals("us.dot.its.jpo.ode.model.OdeHexByteArray", jsonPayloadObj.getDataType());
        assertEquals(expectedPayload, jsonPayloadObj.getData().toString());
    }
}
