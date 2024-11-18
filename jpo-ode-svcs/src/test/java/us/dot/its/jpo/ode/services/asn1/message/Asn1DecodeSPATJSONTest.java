package us.dot.its.jpo.ode.services.asn1.message;

import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import us.dot.its.jpo.ode.kafka.Asn1CoderTopics;
import us.dot.its.jpo.ode.kafka.OdeKafkaProperties;
import us.dot.its.jpo.ode.model.Asn1Encoding.EncodingRule;
import us.dot.its.jpo.ode.model.OdeAsn1Data;
import us.dot.its.jpo.ode.model.OdeAsn1Payload;
import us.dot.its.jpo.ode.model.OdeSpatMetadata;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(initializers = ConfigDataApplicationContextInitializer.class)
@EnableConfigurationProperties(value = {OdeKafkaProperties.class, Asn1CoderTopics.class})
@DirtiesContext
@EmbeddedKafka(partitions = 1, topics = {"topic.Asn1DecoderInputSPAT"}, ports = 4242)
class Asn1DecodeSPATJSONTest {
    private final String json = "{\"metadata\":{\"recordType\":\"spatTx\",\"securityResultCode\":\"success\",\"payloadType\":\"us.dot.its.jpo.ode.model.OdeAsn1Payload\",\"serialId\":{\"streamId\":\"5ec410a3-bec6-4724-9601-1e08778e1dfc\",\"bundleSize\":1,\"bundleId\":0,\"recordId\":0,\"serialNumber\":0},\"odeReceivedAt\":\"2024-03-15T19:43:22.604870100Z\",\"schemaVersion\":6,\"maxDurationTime\":0,\"recordGeneratedBy\":\"RSU\",\"sanitized\":false,\"spatSource\":\"RSU\",\"originIp\":\"192.168.0.1\",\"isCertPresent\":false},\"payload\":{\"dataType\":\"us.dot.its.jpo.ode.model.OdeHexByteArray\",\"data\":{\"bytes\":\"0381004003807C00134700081132000000E437070010434257925790010232119A11CE800C10D095E495E400808684AF24AF20050434257925790030232119A11CE801C10D095E495E401008684AF24AF20000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000\"}}}";

    @Autowired
    OdeKafkaProperties odeKafkaProperties;

    @Autowired
    Asn1CoderTopics asn1CoderTopics;

    @Test
    void testProcess() throws JSONException {
        Asn1DecodeSPATJSON testDecodeSpatJson = new Asn1DecodeSPATJSON(odeKafkaProperties, asn1CoderTopics.getDecoderInput());

        OdeAsn1Data resultOdeObj = testDecodeSpatJson.process(json);

        // Validate the metadata
        OdeSpatMetadata jsonMetadataObj = (OdeSpatMetadata) resultOdeObj.getMetadata();
        assertEquals(OdeSpatMetadata.SpatSource.RSU, jsonMetadataObj.getSpatSource());
        assertEquals("unsecuredData", jsonMetadataObj.getEncodings().getFirst().getElementName());
        assertEquals("MessageFrame", jsonMetadataObj.getEncodings().getFirst().getElementType());
        assertEquals(EncodingRule.UPER, jsonMetadataObj.getEncodings().getFirst().getEncodingRule());

        // Validate the payload
        String expectedPayload = "{\"bytes\":\"00134700081132000000E437070010434257925790010232119A11CE800C10D095E495E400808684AF24AF20050434257925790030232119A11CE801C10D095E495E401008684AF24AF200\"}";
        OdeAsn1Payload jsonPayloadObj = (OdeAsn1Payload) resultOdeObj.getPayload();
        assertEquals("us.dot.its.jpo.ode.model.OdeHexByteArray", jsonPayloadObj.getDataType());
        assertEquals(expectedPayload, jsonPayloadObj.getData().toString());
    }
}
