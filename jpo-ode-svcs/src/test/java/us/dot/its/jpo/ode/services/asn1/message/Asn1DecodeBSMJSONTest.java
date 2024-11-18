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
import us.dot.its.jpo.ode.model.OdeBsmMetadata;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(initializers = ConfigDataApplicationContextInitializer.class)
@EnableConfigurationProperties(value = {OdeKafkaProperties.class, Asn1CoderTopics.class})
@DirtiesContext
@EmbeddedKafka(partitions = 1, topics = {"topic.Asn1DecoderInputBSM"}, ports = 4242)
class Asn1DecodeBSMJSONTest {
    private final String json = "{\"metadata\":{\"bsmSource\":\"EV\",\"recordType\":\"bsmTx\",\"securityResultCode\":\"success\",\"receivedMessageDetails\":{\"locationData\":{\"latitude\":\"unavailable\",\"longitude\":\"unavailable\",\"elevation\":\"unavailable\",\"speed\":\"unavailable\",\"heading\":\"unavailable\"},\"rxSource\":\"RSU\"},\"payloadType\":\"us.dot.its.jpo.ode.model.OdeAsn1Payload\",\"serialId\":{\"streamId\":\"be071349-9bb6-4b66-b1c7-8df1f9e0cb74\",\"bundleSize\":1,\"bundleId\":0,\"recordId\":0,\"serialNumber\":0},\"odeReceivedAt\":\"2024-03-15T16:46:45.297174600Z\",\"schemaVersion\":6,\"maxDurationTime\":0,\"recordGeneratedBy\":\"OBU\",\"sanitized\":false,\"originIp\":\"192.168.0.1\"},\"payload\":{\"dataType\":\"us.dot.its.jpo.ode.model.OdeHexByteArray\",\"data\":{\"bytes\":\"03810040038081B1001480AD4644A9EA5442BC26E97C7496576E052569B214000070007050FD7D0FA1007FFF8000681250020214C1C0FF64BFFA0FB84F720FF71BFF9500DFFFC0FF564006D001FFFC0FF5BBFE5B031FFFC0FF573FF73075FFFC0FF973FFB708FFFFC0FFEFC00B50B5FFFC0FFF0401150BBFFFC0FFDF4015D0C1FFFC0FFB9C01690C7FFFC0FFC0401550C9FFFC0FFBAC014F0C9FFFC0FFBBC01530CBFFFC0FFBB400B30E7FFFC100383FFCD0E3FFFCFFFEC800400120000243450D45B978805B073A8672E91E9D80824A65C65F85E35B61502149263F000FE804E6B84AF66507D51690DE76F30D1468A68F8986B58E6AECB2C5FC4766C223F0B977E87678DDF714FE123C483622CC7500000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000\"}}}";

    @Autowired
    OdeKafkaProperties odeKafkaProperties;

    @Autowired
    Asn1CoderTopics asn1CoderTopics;

    @Test
    void testProcess() throws JSONException {
        Asn1DecodeBSMJSON testDecodeBsmJson = new Asn1DecodeBSMJSON(odeKafkaProperties, asn1CoderTopics.getDecoderInput());

        OdeAsn1Data resultOdeObj = testDecodeBsmJson.process(json);

        // Validate the metadata
        OdeBsmMetadata jsonMetadataObj = (OdeBsmMetadata) resultOdeObj.getMetadata();
        assertEquals(OdeBsmMetadata.BsmSource.EV, jsonMetadataObj.getBsmSource());
        assertEquals("unsecuredData", jsonMetadataObj.getEncodings().getFirst().getElementName());
        assertEquals("MessageFrame", jsonMetadataObj.getEncodings().getFirst().getElementType());
        assertEquals(EncodingRule.UPER, jsonMetadataObj.getEncodings().getFirst().getEncodingRule());

        // Validate the payload
        String expectedPayload = "{\"bytes\":\"001480AD4644A9EA5442BC26E97C7496576E052569B214000070007050FD7D0FA1007FFF8000681250020214C1C0FF64BFFA0FB84F720FF71BFF9500DFFFC0FF564006D001FFFC0FF5BBFE5B031FFFC0FF573FF73075FFFC0FF973FFB708FFFFC0FFEFC00B50B5FFFC0FFF0401150BBFFFC0FFDF4015D0C1FFFC0FFB9C01690C7FFFC0FFC0401550C9FFFC0FFBAC014F0C9FFFC0FFBBC01530CBFFFC0FFBB400B30E7FFFC100383FFCD0E3FFFCFFFEC800400120000243450D45B978805B073A8672E91E9D80824A65C65F85E35B61502149263F000FE804E6B84AF66507D51690DE76F30D1468A68F8986B58E6AECB2C5FC4766C223F0B977E87678DDF714FE123C483622CC7500\"}";
        OdeAsn1Payload jsonPayloadObj = (OdeAsn1Payload) resultOdeObj.getPayload();
        assertEquals("us.dot.its.jpo.ode.model.OdeHexByteArray", jsonPayloadObj.getDataType());
        assertEquals(expectedPayload, jsonPayloadObj.getData().toString());
    }
}
