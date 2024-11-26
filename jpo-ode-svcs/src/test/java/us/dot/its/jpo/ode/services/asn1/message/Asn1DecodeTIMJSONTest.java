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
import us.dot.its.jpo.ode.model.OdeMsgMetadata;
import us.dot.its.jpo.ode.model.OdeTimMetadata;
import us.dot.its.jpo.ode.testUtilities.EmbeddedKafkaHolder;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(initializers = ConfigDataApplicationContextInitializer.class)
@EnableConfigurationProperties(value = {OdeKafkaProperties.class, Asn1CoderTopics.class})
class Asn1DecodeTIMJSONTest {
    private final String json = "{\"metadata\":{\"recordType\":\"timMsg\",\"securityResultCode\":\"success\",\"payloadType\":\"us.dot.its.jpo.ode.model.OdeAsn1Payload\",\"serialId\":{\"streamId\":\"9952caf6-81bd-490d-ad95-47dee31c3ba8\",\"bundleSize\":1,\"bundleId\":0,\"recordId\":0,\"serialNumber\":0},\"odeReceivedAt\":\"2024-03-15T19:38:48.578500100Z\",\"schemaVersion\":6,\"maxDurationTime\":0,\"recordGeneratedBy\":\"RSU\",\"sanitized\":false,\"originIp\":\"192.168.0.1\"},\"payload\":{\"dataType\":\"us.dot.its.jpo.ode.model.OdeHexByteArray\",\"data\":{\"bytes\":\"0381004003807C001F79201000000000012AA366D080729B8987D859717EE22001FFFE4FD0011589D828007E537130FB0B2E2FDC440001F46FFFF002B8B2E46E926E27CE6813D862CB90EDC9B89E11CE2CB8E98F9B89BCC4050518B2E365B66E26AE3B8B2E291A66E2591D8141462CB873969B89396C62CB86AFE9B89208E00000131560018300023E43A6A1351800023E4700EFC51881010100030180C620FB90CAAD3B9C5082080E1DDC905E10168E396921000325A0D73B83279C83010180034801090001260001808001838005008001F0408001828005008001304000041020407E800320409780050080012040000320409900018780032040958005000001E0408183E7139D7B70987019B526B8A950052F5C011D3C4B992143E885C71F95DA6071658082346CC03A50D66801F65288C30AB39673D0494536C559047E457AD291C99C20A7FB1244363E993EE3EE98C78742609340541DA01545A0F7339C26A527903576D30000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000\"}}}";

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

        Asn1DecodeTIMJSON testDecodeTimJson = new Asn1DecodeTIMJSON(odeKafkaProperties, asn1CoderTopics.getDecoderInput());

        OdeAsn1Data resultOdeObj = testDecodeTimJson.process(json);

        // Validate the metadata
        OdeTimMetadata jsonMetadataObj = (OdeTimMetadata) resultOdeObj.getMetadata();
        assertEquals(OdeMsgMetadata.GeneratedBy.RSU, jsonMetadataObj.getRecordGeneratedBy());
        assertEquals("unsecuredData", jsonMetadataObj.getEncodings().getFirst().getElementName());
        assertEquals("MessageFrame", jsonMetadataObj.getEncodings().getFirst().getElementType());
        assertEquals(EncodingRule.UPER, jsonMetadataObj.getEncodings().getFirst().getEncodingRule());

        // Validate the payload
        String expectedPayload = "{\"bytes\":\"001F79201000000000012AA366D080729B8987D859717EE22001FFFE4FD0011589D828007E537130FB0B2E2FDC440001F46FFFF002B8B2E46E926E27CE6813D862CB90EDC9B89E11CE2CB8E98F9B89BCC4050518B2E365B66E26AE3B8B2E291A66E2591D8141462CB873969B89396C62CB86AFE9B89208E00000131560018300023E43A6A1351800023E4700EFC51881010100030180C620FB90CAAD3B9C5082080E1DDC905E10168E396921000325A0D73B83279C83010180034801090001260001808001838005008001F0408001828005008001304000041020407E800320409780050080012040000320409900018780032040958005000001E0408183E7139D7B70987019B526B8A950052F5C011D3C4B992143E885C71F95DA6071658082346CC03A50D66801F65288C30AB39673D0494536C559047E457AD291C99C20A7FB1244363E993EE3EE98C78742609340541DA01545A0F7339C26A527903576D300\"}";
        OdeAsn1Payload jsonPayloadObj = (OdeAsn1Payload) resultOdeObj.getPayload();
        assertEquals("us.dot.its.jpo.ode.model.OdeHexByteArray", jsonPayloadObj.getDataType());
        assertEquals(expectedPayload, jsonPayloadObj.getData().toString());
    }
}
