package us.dot.its.jpo.ode.services.asn1;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import us.dot.its.jpo.ode.kafka.Asn1CoderTopics;
import us.dot.its.jpo.ode.kafka.JsonTopics;
import us.dot.its.jpo.ode.kafka.OdeKafkaProperties;
import us.dot.its.jpo.ode.kafka.PojoTopics;
import us.dot.its.jpo.ode.kafka.RawEncodedJsonTopics;
import us.dot.its.jpo.ode.model.OdeMapData;
import us.dot.its.jpo.ode.testUtilities.ApprovalTestCase;
import us.dot.its.jpo.ode.wrapper.MessageConsumer;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(initializers = ConfigDataApplicationContextInitializer.class)
@EnableConfigurationProperties(value = {OdeKafkaProperties.class, Asn1CoderTopics.class, RawEncodedJsonTopics.class})
@EmbeddedKafka(
        partitions = 1,
        topics = { Asn1DecodedDataRouterApprovalTest.INPUT_TOPIC, Asn1DecodedDataRouterApprovalTest.OUTPUT_TOPIC_TX, Asn1DecodedDataRouterApprovalTest.OUTPUT_TOPIC_JSON },
        ports = 4242
)
@DirtiesContext
class Asn1DecodedDataRouterApprovalTest {

    static final String INPUT_TOPIC = "topic.Asn1DecoderOutput";
    static final String OUTPUT_TOPIC_TX = "topic.OdeMapTxPojo";
    static final String OUTPUT_TOPIC_JSON = "topic.OdeMapJson";

    @Autowired
    EmbeddedKafkaBroker embeddedKafka;

    @Autowired
    OdeKafkaProperties odeKafkaProperties;

    @Test
    void testAsn1DecodedDataRouter() throws IOException {
        List<ApprovalTestCase> testCases = ApprovalTestCase.deserializeTestCases("src/test/resources/us.dot.its.jpo.ode.udp.map/Asn1DecoderRouter_ApprovalTestCases_MapTxPojo.json");

        PojoTopics pojoTopics = new PojoTopics();
        pojoTopics.setTxMap(OUTPUT_TOPIC_TX);

        JsonTopics jsonTopics = new JsonTopics();
        jsonTopics.setMap(OUTPUT_TOPIC_JSON);

        Asn1DecodedDataRouter decoderRouter = new Asn1DecodedDataRouter(odeKafkaProperties, pojoTopics, jsonTopics);

        MessageConsumer<String, String> asn1DecoderConsumer = MessageConsumer.defaultStringMessageConsumer(
                odeKafkaProperties.getBrokers(), this.getClass().getSimpleName(), decoderRouter);

        asn1DecoderConsumer.setName("Asn1DecoderConsumer");
        decoderRouter.start(asn1DecoderConsumer, INPUT_TOPIC);

        Map<String, Object> producerProps = KafkaTestUtils.producerProps(embeddedKafka);
        DefaultKafkaProducerFactory<Integer, String> producerFactory = new DefaultKafkaProducerFactory<>(producerProps);
        Producer<Integer, String> producer = producerFactory.createProducer();

        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("testT", "false", embeddedKafka);
        DefaultKafkaConsumerFactory<Integer, String> cf = new DefaultKafkaConsumerFactory<>(consumerProps);

        Consumer<Integer, String> consumer = cf.createConsumer();
        embeddedKafka.consumeFromEmbeddedTopics(consumer, OUTPUT_TOPIC_TX, OUTPUT_TOPIC_JSON);

        for (ApprovalTestCase testCase : testCases) {
            ProducerRecord<Integer, String> producerRecord = new ProducerRecord<>(INPUT_TOPIC, 0, 0, testCase.getInput());
            producer.send(producerRecord);

            String received = KafkaTestUtils.getSingleRecord(consumer, OUTPUT_TOPIC_TX).value();
            ObjectMapper mapper = new ObjectMapper();
            OdeMapData receivedMapData = mapper.readValue(received, OdeMapData.class);
            OdeMapData expectedMapData = mapper.readValue(testCase.getExpected(), OdeMapData.class);
            assertEquals(expectedMapData.toJson(), receivedMapData.toJson(), "Failed test case: " + testCase.getDescription());
            // discard the JSON output
            KafkaTestUtils.getSingleRecord(consumer, OUTPUT_TOPIC_JSON);
        }

        List<ApprovalTestCase> jsonTestCases = ApprovalTestCase.deserializeTestCases("src/test/resources/us.dot.its.jpo.ode.udp.map/Asn1DecoderRouter_ApprovalTestCases_MapJson.json");

        for (ApprovalTestCase testCase : jsonTestCases) {
            ProducerRecord<Integer, String> producerRecord = new ProducerRecord<>(INPUT_TOPIC, 0, 0, testCase.getInput());
            producer.send(producerRecord);

            String received = KafkaTestUtils.getSingleRecord(consumer, OUTPUT_TOPIC_JSON).value();
            ObjectMapper mapper = new ObjectMapper();
            OdeMapData receivedMapData = mapper.readValue(received, OdeMapData.class);
            OdeMapData expectedMapData = mapper.readValue(testCase.getExpected(), OdeMapData.class);
            assertEquals(expectedMapData.toJson(), receivedMapData.toJson(), "Failed test case: " + testCase.getDescription());
        }
    }
}
