package us.dot.its.jpo.ode.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import us.dot.its.jpo.ode.testUtilities.ApprovalTestCase;
import us.dot.its.jpo.ode.testUtilities.EmbeddedKafkaHolder;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static us.dot.its.jpo.ode.testUtilities.ApprovalTestCase.deserializeTestCases;

@Slf4j
@SpringBootTest(properties = {"ode.kafka.topics.raw-encoded-json.map=topic.Asn1DecoderTestMAPJSON",
        "ode.kafka.topics.asn1.decoder-input=topic.Asn1DecoderMAPInput"})
@ContextConfiguration(initializers = ConfigDataApplicationContextInitializer.class)
@DirtiesContext
class Asn1DecodeMAPJSONTest {

    @Value(value = "${ode.kafka.topics.raw-encoded-json.map}")
    private String rawEncodedMapJson;

    @Value(value = "${ode.kafka.topics.asn1.decoder-input}")
    private String asn1DecoderInput;

    private static final EmbeddedKafkaBroker embeddedKafka = EmbeddedKafkaHolder.getEmbeddedKafka();

    @Test
    void testProcess_ApprovalTest() throws IOException {
        Awaitility.setDefaultTimeout(250, java.util.concurrent.TimeUnit.MILLISECONDS);
        try {
            EmbeddedKafkaHolder.getEmbeddedKafka().addTopics(
                    new NewTopic(rawEncodedMapJson, 1, (short) 1),
                    new NewTopic(asn1DecoderInput, 1, (short) 1)
            );
        } catch (Exception e) {
            log.warn("New topics not created: {}", e.getMessage());
        }

        String path = "src/test/resources/us.dot.its.jpo.ode.udp.map/JSONEncodedMAP_to_Asn1DecoderInput_Validation.json";
        List<ApprovalTestCase> approvalTestCases = deserializeTestCases(path);

        Map<String, Object> producerProps = KafkaTestUtils.producerProps(embeddedKafka);
        DefaultKafkaProducerFactory<Integer, String> producerFactory = new DefaultKafkaProducerFactory<>(producerProps);
        Producer<Integer, String> producer = producerFactory.createProducer();

        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps(this.getClass().getSimpleName(), "false", embeddedKafka);
        DefaultKafkaConsumerFactory<Integer, String> cf = new DefaultKafkaConsumerFactory<>(consumerProps);
        Consumer<Integer, String> testConsumer = cf.createConsumer();
        embeddedKafka.consumeFromAnEmbeddedTopic(testConsumer, asn1DecoderInput);

        for (ApprovalTestCase approvalTestCase : approvalTestCases) {
            // produce the test case input to the topic for consumption by the asn1RawMAPJSONConsumer
            ProducerRecord<Integer, String> r = new ProducerRecord<>(rawEncodedMapJson, approvalTestCase.getInput());
            var sent = producer.send(r);
            Awaitility.await().until(sent::isDone);

            ConsumerRecord<Integer, String> actualRecord = KafkaTestUtils.getSingleRecord(testConsumer, asn1DecoderInput);
            assertEquals(approvalTestCase.getExpected(), actualRecord.value(), approvalTestCase.getDescription());
        }
    }
}
