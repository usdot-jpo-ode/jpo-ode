package us.dot.its.jpo.ode.kafka.listeners.asn1;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import us.dot.its.jpo.ode.config.SerializationConfig;
import us.dot.its.jpo.ode.kafka.KafkaConsumerConfig;
import us.dot.its.jpo.ode.kafka.OdeKafkaProperties;
import us.dot.its.jpo.ode.kafka.producer.KafkaProducerConfig;
import us.dot.its.jpo.ode.kafka.topics.RawEncodedJsonTopics;
import us.dot.its.jpo.ode.test.utilities.EmbeddedKafkaHolder;
import us.dot.its.jpo.ode.udp.controller.UDPReceiverProperties;

@SpringBootTest(
    classes = {
        KafkaProducerConfig.class,
        KafkaConsumerConfig.class,
        RawEncodedBSMJsonRouter.class,
        RawEncodedJsonService.class,
        SerializationConfig.class
    },
    properties = {
        "ode.kafka.topics.raw-encoded-json.bsm=topic.Asn1DecoderTestBSMJSON",
        "ode.kafka.topics.asn1.decoder-input=topic.Asn1DecoderBSMInput"
    })
@EnableConfigurationProperties
@ContextConfiguration(classes = {
    UDPReceiverProperties.class, OdeKafkaProperties.class,
    RawEncodedJsonTopics.class, KafkaProperties.class
})
@DirtiesContext
class RawEncodedBSMJsonRouterTest {

  @Value(value = "${ode.kafka.topics.raw-encoded-json.bsm}")
  private String rawEncodedBsmJson;

  @Value(value = "${ode.kafka.topics.asn1.decoder-input}")
  private String asn1DecoderInput;

  @Autowired
  KafkaTemplate<String, String> kafkaTemplate;

  @Test
  void testListen() throws JSONException, IOException {
    var embeddedKafka = EmbeddedKafkaHolder.getEmbeddedKafka();
    EmbeddedKafkaHolder.addTopics(asn1DecoderInput, rawEncodedBsmJson);

    Map<String, Object> consumerProps =
        KafkaTestUtils.consumerProps("Asn1DecodeBSMJSONTestConsumer", "false", embeddedKafka);
    var cf =
        new DefaultKafkaConsumerFactory<>(consumerProps,
            new StringDeserializer(), new StringDeserializer());
    Consumer<String, String> testConsumer = cf.createConsumer();
    embeddedKafka.consumeFromAnEmbeddedTopic(testConsumer, asn1DecoderInput);

    var classLoader = getClass().getClassLoader();
    InputStream inputStream = classLoader
        .getResourceAsStream(
            "us/dot/its/jpo/ode/kafka/listeners/asn1/decoder-input-bsm.json");
    assert inputStream != null;
    var bsmJson = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
    kafkaTemplate.send(rawEncodedBsmJson, bsmJson);

    inputStream = classLoader
        .getResourceAsStream("us/dot/its/jpo/ode/kafka/listeners/asn1/expected-bsm.xml");
    assert inputStream != null;
    var expectedBsm = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

    var produced =
        KafkaTestUtils.getSingleRecord(testConsumer, asn1DecoderInput);
    var odeBsmData = produced.value();
    assertEquals(expectedBsm, odeBsmData);
    testConsumer.close();
  }
}
