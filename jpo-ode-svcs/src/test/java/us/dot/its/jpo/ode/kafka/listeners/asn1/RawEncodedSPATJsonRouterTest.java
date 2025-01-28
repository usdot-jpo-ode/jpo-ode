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
import us.dot.its.jpo.ode.kafka.topics.Asn1CoderTopics;
import us.dot.its.jpo.ode.kafka.topics.RawEncodedJsonTopics;
import us.dot.its.jpo.ode.test.utilities.EmbeddedKafkaHolder;
import us.dot.its.jpo.ode.udp.controller.UDPReceiverProperties;

@SpringBootTest(
    classes = {
        KafkaProducerConfig.class,
        RawEncodedSPATJsonRouter.class,
        RawEncodedJsonService.class,
        KafkaConsumerConfig.class,
        SerializationConfig.class
    },
    properties = {
        "ode.kafka.topics.raw-encoded-json.spat=topic.Asn1DecoderTestSPATJSON",
        "ode.kafka.topics.asn1.decoder-input=topic.Asn1DecoderSPATInput"
    })
@EnableConfigurationProperties
@ContextConfiguration(classes = {
    UDPReceiverProperties.class, OdeKafkaProperties.class,
    RawEncodedJsonTopics.class, KafkaProperties.class, Asn1CoderTopics.class
})
@DirtiesContext
class RawEncodedSPATJsonRouterTest {

  @Autowired
  Asn1CoderTopics asn1CoderTopics;
  @Autowired
  RawEncodedJsonTopics rawEncodedJsonTopics;
  @Autowired
  KafkaTemplate<String, String> kafkaTemplate;

  @Test
  void testListen() throws JSONException, IOException {
    var embeddedKafka = EmbeddedKafkaHolder.getEmbeddedKafka();
    EmbeddedKafkaHolder.addTopics(asn1CoderTopics.getDecoderInput(),
        rawEncodedJsonTopics.getSpat());

    Map<String, Object> consumerProps =
        KafkaTestUtils.consumerProps("Asn1DecodeSPATJSONTestConsumer", "false", embeddedKafka);
    var cf =
        new DefaultKafkaConsumerFactory<>(consumerProps,
            new StringDeserializer(), new StringDeserializer());
    Consumer<String, String> testConsumer = cf.createConsumer();
    embeddedKafka.consumeFromAnEmbeddedTopic(testConsumer, asn1CoderTopics.getDecoderInput());

    var classLoader = getClass().getClassLoader();
    InputStream inputStream = classLoader
        .getResourceAsStream(
            "us/dot/its/jpo/ode/kafka/listeners/asn1/decoder-input-spat.json");
    assert inputStream != null;
    var spatJson = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
    kafkaTemplate.send(rawEncodedJsonTopics.getSpat(), spatJson);

    var consumedSpat =
        KafkaTestUtils.getSingleRecord(testConsumer, asn1CoderTopics.getDecoderInput());

    inputStream = classLoader
        .getResourceAsStream("us/dot/its/jpo/ode/kafka/listeners/asn1/expected-spat.xml");
    assert inputStream != null;
    var expectedSpat = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
    assertEquals(expectedSpat, consumedSpat.value());
  }
}
