package us.dot.its.jpo.ode.udp.map;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import us.dot.its.jpo.ode.kafka.OdeKafkaProperties;
import us.dot.its.jpo.ode.kafka.RawEncodedJsonTopics;
import us.dot.its.jpo.ode.model.OdeMsgMetadata;
import us.dot.its.jpo.ode.testUtilities.TestUDPClient;
import us.dot.its.jpo.ode.udp.controller.ServiceManager;
import us.dot.its.jpo.ode.udp.controller.UDPReceiverProperties;
import us.dot.its.jpo.ode.udp.controller.UdpServiceThreadFactory;
import us.dot.its.jpo.ode.util.DateTimeUtils;
import us.dot.its.jpo.ode.testUtilities.ApprovalTestCase;

import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static us.dot.its.jpo.ode.testUtilities.ApprovalTestCase.deserializeTestCases;

@Slf4j
@ExtendWith(SpringExtension.class)
@ContextConfiguration(initializers = ConfigDataApplicationContextInitializer.class)
@EnableConfigurationProperties(value = {UDPReceiverProperties.class, OdeKafkaProperties.class, RawEncodedJsonTopics.class, KafkaProperties.class})
@RunWith(SpringRunner.class)
@DirtiesContext
@EmbeddedKafka(partitions = 1, topics = {"topic.OdeRawEncodedMAPJson"}, ports = 4242)
class MapReceiverTest {

    @Autowired
    UDPReceiverProperties udpReceiverProperties;

    @Autowired
    OdeKafkaProperties odeKafkaProperties;

    @Autowired
    RawEncodedJsonTopics rawEncodedJsonTopics;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafka;

    ServiceManager rm;
    TestUDPClient udpClient;
    MapReceiver mapReceiver;

    @BeforeEach
    public void setUp() {
        rm = new ServiceManager(new UdpServiceThreadFactory("UdpReceiverManager"));

        mapReceiver = new MapReceiver(udpReceiverProperties.getMap(),
                odeKafkaProperties,
                rawEncodedJsonTopics.getMap());

        // Set the clock to a fixed time so that the MapReceiver will produce the same output every time
        DateTimeUtils.setClock(Clock.fixed(Instant.parse("2020-01-01T00:00:00Z"), Clock.systemUTC().getZone()));
        // Set the static schema version to 7 so that the MapReceiver will produce the same output every time
        // This is necessary because the schema version is set in only one of the OdeMsgMetadata constructors (this should be fixed)
        // and the schema version is set to the static schema version in the constructor. This means that the schema version
        // will be set to 6 for all OdeMsgMetadata objects created in the MapReceiver run method's code path.
        OdeMsgMetadata.setStaticSchemaVersion(7);
    }

    @AfterEach
    public void tearDown() {
        mapReceiver.setStopped(true);
        udpClient.close();
    }

    @Test
    void testMapReceiver() throws IOException {
        String path = "src/test/resources/us.dot.its.jpo.ode.udp.map/UDPMAP_To_EncodedJSON_Validation.json";
        List<ApprovalTestCase> approvalTestCases = deserializeTestCases(path, "\u0000\u0012");

        // Start the MapReceiver in a new thread
        rm.submit(mapReceiver);

        // Set up a Kafka consumer
        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("testT", "false", embeddedKafka);
        DefaultKafkaConsumerFactory<Integer, String> cf = new DefaultKafkaConsumerFactory<>(consumerProps);
        Consumer<Integer, String> consumer = cf.createConsumer();
        embeddedKafka.consumeFromAnEmbeddedTopic(consumer, rawEncodedJsonTopics.getMap());

        udpClient = new TestUDPClient(udpReceiverProperties.getMap().getReceiverPort());

        for (ApprovalTestCase approvalTestCase : approvalTestCases) {
            udpClient.send(approvalTestCase.getInput());

            ConsumerRecord<Integer, String> produced = KafkaTestUtils.getSingleRecord(consumer, rawEncodedJsonTopics.getMap());

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

