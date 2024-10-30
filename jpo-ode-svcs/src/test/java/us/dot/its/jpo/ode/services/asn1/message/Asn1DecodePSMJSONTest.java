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
import us.dot.its.jpo.ode.model.OdePsmMetadata;
import us.dot.its.jpo.ode.util.XmlUtils.XmlUtilsException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(initializers = ConfigDataApplicationContextInitializer.class)
@EnableConfigurationProperties(value = OdeKafkaProperties.class)
public class Asn1DecodePSMJSONTest {
    private final String json = "{\"metadata\":{\"recordType\":\"psmTx\",\"securityResultCode\":\"success\",\"payloadType\":\"us.dot.its.jpo.ode.model.OdeAsn1Payload\",\"serialId\":{\"streamId\":\"fa3dfe1b-80cd-45cb-ae2c-c604a214fe56\",\"bundleSize\":1,\"bundleId\":0,\"recordId\":0,\"serialNumber\":0},\"odeReceivedAt\":\"2024-03-15T19:16:35.212860500Z\",\"schemaVersion\":6,\"maxDurationTime\":0,\"recordGeneratedBy\":\"UNKNOWN\",\"sanitized\":false,\"psmSource\":\"RSU\",\"originIp\":\"192.168.0.1\"},\"payload\":{\"dataType\":\"us.dot.its.jpo.ode.model.OdeHexByteArray\",\"data\":{\"bytes\":\"00201A0000021BD86891DE75F84DA101C13F042E2214141FFF00022C2000270000000163B2CC798601000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000\"}}}";

    @Autowired
    OdeKafkaProperties odeKafkaProperties;

    @Test
    public void testConstructor() {
        OdeProperties properties = new OdeProperties();
        assertEquals(properties.getKafkaTopicOdeRawEncodedPSMJson(), "topic.OdeRawEncodedPSMJson");
    }

    @Test
    public void testProcess() throws XmlUtilsException, JSONException {
        OdeProperties properties = new OdeProperties();
        Asn1DecodePSMJSON testDecodePsmJson = new Asn1DecodePSMJSON(properties, odeKafkaProperties);

        OdeAsn1Data resultOdeObj = testDecodePsmJson.process(json);

        // Validate the metadata
        OdePsmMetadata jsonMetadataObj = (OdePsmMetadata) resultOdeObj.getMetadata();
        assertEquals(jsonMetadataObj.getPsmSource(), OdePsmMetadata.PsmSource.RSU);
        assertEquals(jsonMetadataObj.getEncodings().get(0).getElementName(), "unsecuredData");
        assertEquals(jsonMetadataObj.getEncodings().get(0).getElementType(), "MessageFrame");
        assertEquals(jsonMetadataObj.getEncodings().get(0).getEncodingRule(), EncodingRule.UPER);

        // Validate the payload
        String expectedPayload = "{\"bytes\":\"00201A0000021BD86891DE75F84DA101C13F042E2214141FFF00022C2000270000000163B2CC79860100\"}";
        OdeAsn1Payload jsonPayloadObj = (OdeAsn1Payload) resultOdeObj.getPayload();
        assertEquals(jsonPayloadObj.getDataType(), "us.dot.its.jpo.ode.model.OdeHexByteArray");
        assertEquals(jsonPayloadObj.getData().toString(), expectedPayload);
    }
}
