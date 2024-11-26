package us.dot.its.jpo.ode.udp.map;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import us.dot.its.jpo.ode.model.OdeMsgMetadata;
import us.dot.its.jpo.ode.testUtilities.ApprovalTestCase;
import us.dot.its.jpo.ode.testUtilities.EmbeddedKafkaHolder;
import us.dot.its.jpo.ode.testUtilities.TestUDPClient;
import us.dot.its.jpo.ode.udp.controller.UDPReceiverProperties;
import us.dot.its.jpo.ode.util.DateTimeUtils;

import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static us.dot.its.jpo.ode.testUtilities.ApprovalTestCase.deserializeTestCases;

@Slf4j
@SpringBootTest(properties = {"ode.kafka.topics.raw-encoded-json.map=topic.MapReceiverTestMAPJSON", "ode.receivers.map.receiver-port=12412"})
@DirtiesContext
class MapReceiverTest {
    private final EmbeddedKafkaBroker embeddedKafka = EmbeddedKafkaHolder.getEmbeddedKafka();

    @Autowired
    UDPReceiverProperties udpReceiverProperties;

    @Value(value = "${ode.kafka.topics.raw-encoded-json.map}")
    private String rawJsonMapTopic;

    @Value(value = "${ode.receivers.map.receiver-port}")
    private int mapReceiverPort;

    @Test
    void testMapReceiver() throws IOException {
        String path = "src/test/resources/us.dot.its.jpo.ode.udp.map/UDPMAP_To_EncodedJSON_Validation.json";
        List<ApprovalTestCase> approvalTestCases = deserializeTestCases(path, "\u0000\u0012");

        // Set the clock to a fixed time so that the MapReceiver will produce the same output every time
        DateTimeUtils.setClock(Clock.fixed(Instant.parse("2020-01-01T00:00:00Z"), Clock.systemUTC().getZone()));
        // Set the static schema version to 7 so that the MapReceiver will produce the same output every time
        // This is necessary because the schema version is set in only one of the OdeMsgMetadata constructors (this should be fixed)
        // and the schema version is set to the static schema version in the constructor. This means that the schema version
        // will be set to 6 for all OdeMsgMetadata objects created in the MapReceiver run method's code path.
        OdeMsgMetadata.setStaticSchemaVersion(7);

        try {
            embeddedKafka.addTopics(new NewTopic(rawJsonMapTopic, 1, (short) 1));
        } catch (Exception e) {
            log.warn("Couldn't create topics. If the error indicates the topics already exist, this message is safe to ignore: {}", e.getMessage());
        }

        // Set up a Kafka consumer
        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("test-group", "false", embeddedKafka);
        DefaultKafkaConsumerFactory<Integer, String> cf = new DefaultKafkaConsumerFactory<>(consumerProps);
        Consumer<Integer, String> consumer = cf.createConsumer();
        embeddedKafka.consumeFromAnEmbeddedTopic(consumer, rawJsonMapTopic);

        // Clarifying note (for Spring newbies):
        // UdpServicesController is annotated with @Controller, and this test class is annotated with @SpringBootTest
        // so a UdpServicesController will be instantiated before this test runs. That means the MapReceiver will also
        // be instantiated and ready to consume UDP traffic from the same port we configure the TestUDPClient to send packets to.
        TestUDPClient udpClient = new TestUDPClient(mapReceiverPort);

        for (ApprovalTestCase approvalTestCase : approvalTestCases) {
            udpClient.send(approvalTestCase.getInput());

            ConsumerRecord<Integer, String> produced = KafkaTestUtils.getSingleRecord(consumer, rawJsonMapTopic);

            JSONObject producedJson = new JSONObject(produced.value());
            JSONObject expectedJson = new JSONObject(approvalTestCase.getExpected());

            // assert that the UUIDs are different, then remove them so that the rest of the JSON can be compared
            assertNotEquals(expectedJson.getJSONObject("metadata").get("serialId"), producedJson.getJSONObject("metadata").get("serialId"));
            expectedJson.getJSONObject("metadata").remove("serialId");
            producedJson.getJSONObject("metadata").remove("serialId");

            assertEquals(expectedJson.toString(), producedJson.toString());
        }
    }
}

