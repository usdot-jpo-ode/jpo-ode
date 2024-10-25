package us.dot.its.jpo.ode.services.asn1.message;

import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.kafka.OdeKafkaProperties;
import us.dot.its.jpo.ode.model.Asn1Encoding.EncodingRule;
import us.dot.its.jpo.ode.model.OdeAsn1Data;
import us.dot.its.jpo.ode.model.OdeAsn1Payload;
import us.dot.its.jpo.ode.model.OdeSrmMetadata;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(initializers = ConfigDataApplicationContextInitializer.class)
@EnableConfigurationProperties(value = OdeKafkaProperties.class)
class Asn1DecodeSRMJSONTest {
    private final String json = "{\"metadata\":{\"recordType\":\"srmTx\",\"securityResultCode\":\"success\",\"payloadType\":\"us.dot.its.jpo.ode.model.OdeAsn1Payload\",\"serialId\":{\"streamId\":\"190cbd65-d1e2-488a-ba42-b7d3f03a5c69\",\"bundleSize\":1,\"bundleId\":0,\"recordId\":0,\"serialNumber\":0},\"odeReceivedAt\":\"2024-03-15T19:24:04.113614500Z\",\"schemaVersion\":6,\"maxDurationTime\":0,\"recordGeneratedBy\":\"OBU\",\"sanitized\":false,\"originIp\":\"192.168.0.1\",\"srmSource\":\"RSU\"},\"payload\":{\"dataType\":\"us.dot.its.jpo.ode.model.OdeHexByteArray\",\"data\":{\"bytes\":\"0381004003807C001D2130000010090BD341080D00855C6C0C6899853000A534F7C24CB29897694759B7C000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000\"}}}";

    @Autowired
    OdeKafkaProperties odeKafkaProperties;

    @Test
    void testConstructor() {
        OdeProperties properties = new OdeProperties();
        assertEquals("topic.OdeRawEncodedSRMJson", properties.getKafkaTopicOdeRawEncodedSRMJson());
    }

    @Test
    void testProcess() throws JSONException {
        OdeProperties properties = new OdeProperties();

        Asn1DecodeSRMJSON testDecodeSrmJson = new Asn1DecodeSRMJSON(properties, odeKafkaProperties);

        OdeAsn1Data resultOdeObj = testDecodeSrmJson.process(json);

        // Validate the metadata
        OdeSrmMetadata jsonMetadataObj = (OdeSrmMetadata) resultOdeObj.getMetadata();
        assertEquals(OdeSrmMetadata.SrmSource.RSU, jsonMetadataObj.getSrmSource());
        assertEquals("unsecuredData", jsonMetadataObj.getEncodings().get(0).getElementName());
        assertEquals("MessageFrame", jsonMetadataObj.getEncodings().get(0).getElementType());
        assertEquals(EncodingRule.UPER, jsonMetadataObj.getEncodings().get(0).getEncodingRule());

        // Validate the payload
        String expectedPayload = "{\"bytes\":\"001D2130000010090BD341080D00855C6C0C6899853000A534F7C24CB29897694759B7C000\"}";
        OdeAsn1Payload jsonPayloadObj = (OdeAsn1Payload) resultOdeObj.getPayload();
        assertEquals("us.dot.its.jpo.ode.model.OdeHexByteArray", jsonPayloadObj.getDataType());
        assertEquals(expectedPayload, jsonPayloadObj.getData().toString());
    }
}
