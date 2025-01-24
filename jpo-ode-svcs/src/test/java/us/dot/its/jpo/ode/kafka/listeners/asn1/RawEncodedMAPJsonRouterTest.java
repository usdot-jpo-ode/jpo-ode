package us.dot.its.jpo.ode.kafka.listeners.asn1;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static us.dot.its.jpo.ode.test.utilities.ApprovalTestCase.deserializeTestCases;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import us.dot.its.jpo.ode.config.SerializationConfig;
import us.dot.its.jpo.ode.kafka.KafkaConsumerConfig;
import us.dot.its.jpo.ode.kafka.OdeKafkaProperties;
import us.dot.its.jpo.ode.kafka.producer.KafkaProducerConfig;
import us.dot.its.jpo.ode.kafka.topics.RawEncodedJsonTopics;
import us.dot.its.jpo.ode.test.utilities.ApprovalTestCase;
import us.dot.its.jpo.ode.test.utilities.EmbeddedKafkaHolder;
import us.dot.its.jpo.ode.udp.controller.UDPReceiverProperties;

@Slf4j
@SpringBootTest(
    classes = {
        KafkaProducerConfig.class,
        KafkaConsumerConfig.class,
        RawEncodedMAPJsonRouter.class,
        RawEncodedJsonService.class,
        SerializationConfig.class
    },
    properties = {
        "ode.kafka.topics.raw-encoded-json.map=topic.Asn1DecoderTestMAPJSON",
        "ode.kafka.topics.asn1.decoder-input=topic.Asn1DecoderMAPInput"
    })
@EnableConfigurationProperties
@ContextConfiguration(classes = {
    UDPReceiverProperties.class, OdeKafkaProperties.class,
    RawEncodedJsonTopics.class, KafkaProperties.class
})
@DirtiesContext
class RawEncodedMAPJsonRouterTest {

  @Value(value = "${ode.kafka.topics.raw-encoded-json.map}")
  private String rawEncodedMapJson;

  @Value(value = "${ode.kafka.topics.asn1.decoder-input}")
  private String asn1DecoderInput;
  @Autowired
  KafkaTemplate<String, String> producer;

  private static final EmbeddedKafkaBroker embeddedKafka = EmbeddedKafkaHolder.getEmbeddedKafka();

  @Test
  void testProcess_ApprovalTest() throws IOException {
    String[] topics = {rawEncodedMapJson, asn1DecoderInput};
    EmbeddedKafkaHolder.addTopics(topics);

    String path =
        "src/test/resources/us.dot.its.jpo.ode.udp.map/JSONEncodedMAP_to_Asn1DecoderInput_Validation.json";
    List<ApprovalTestCase> approvalTestCases = deserializeTestCases(path);

    Map<String, Object> consumerProps =
        KafkaTestUtils.consumerProps("Asn1DecodeMapJSONTestConsumer", "false", embeddedKafka);
    var cf =
        new DefaultKafkaConsumerFactory<>(consumerProps,
            new StringDeserializer(), new StringDeserializer());
    Consumer<String, String> testConsumer = cf.createConsumer();
    embeddedKafka.consumeFromAnEmbeddedTopic(testConsumer, asn1DecoderInput);

    for (ApprovalTestCase approvalTestCase : approvalTestCases) {
      // produce the test case input to the topic for consumption by the asn1RawMAPJSONConsumer
      producer.send(rawEncodedMapJson, approvalTestCase.getInput());

      var actualRecord =
          KafkaTestUtils.getSingleRecord(testConsumer, asn1DecoderInput);
      assertEquals(approvalTestCase.getExpected(), actualRecord.value(),
          approvalTestCase.getDescription());
    }
    testConsumer.close();
  }
}
