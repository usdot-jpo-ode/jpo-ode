package us.dot.its.jpo.ode.kafka.listeners.asn1;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.kafka.autoconfigure.KafkaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import us.dot.its.jpo.ode.config.SerializationConfig;
import us.dot.its.jpo.ode.kafka.KafkaConsumerConfig;
import us.dot.its.jpo.ode.kafka.OdeKafkaProperties;
import us.dot.its.jpo.ode.kafka.TestMetricsConfig;
import us.dot.its.jpo.ode.kafka.listeners.json.RawEncodedBSMJsonRouter;
import us.dot.its.jpo.ode.kafka.listeners.json.RawEncodedJsonService;
import us.dot.its.jpo.ode.kafka.producer.KafkaProducerConfig;
import us.dot.its.jpo.ode.kafka.topics.RawEncodedJsonTopics;
import us.dot.its.jpo.ode.udp.controller.UDPReceiverProperties;

@SpringBootTest(
    classes = { KafkaProducerConfig.class, KafkaConsumerConfig.class, RawEncodedBSMJsonRouter.class,
        RawEncodedJsonService.class, SerializationConfig.class, TestMetricsConfig.class,
        UDPReceiverProperties.class, OdeKafkaProperties.class,
        RawEncodedJsonTopics.class, KafkaProperties.class},
    properties = {"ode.kafka.topics.asn1.decoder-input=topic.Asn1DecoderBSMInput",
        "ode.kafka.topics.raw-encoded-json.bsm=topic.OdeRawEncodedBSMJson"})
@EmbeddedKafka
@TestPropertySource(properties = "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}")
@EnableConfigurationProperties
@DirtiesContext
class RawEncodedBSMJsonRouterTest {

  @Autowired
  RawEncodedJsonTopics rawEncodedJsonTopics;

  @Autowired
  KafkaTemplate<String, String> kafkaTemplate;

  private CompletableFuture<String> future;

  @Test
  void testListen() throws JSONException, IOException, InterruptedException {

    future = new CompletableFuture<>();
    var classLoader = getClass().getClassLoader();
    String bsmJson;
    try (InputStream inputStream = classLoader.getResourceAsStream(
            "us/dot/its/jpo/ode/kafka/listeners/asn1/decoder-input-bsm.json")) {

      assert inputStream != null;
      bsmJson = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
    }

    kafkaTemplate.send(rawEncodedJsonTopics.getBsm(), bsmJson);

    String expectedBsm;
    try (InputStream inputStream = classLoader.getResourceAsStream(
            "us/dot/its/jpo/ode/kafka/listeners/asn1/expected-bsm.xml")) {

      assert inputStream != null;
      expectedBsm = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
    }

    String odeBsmData;
    try {
      odeBsmData = future.get(3, TimeUnit.SECONDS);
    } catch (ExecutionException | TimeoutException e) {
      throw new AssertionError("BSM message was not received within the timeout period", e);
    }

    assertEquals(expectedBsm, odeBsmData);
  }

    @KafkaListener(topics = {"topic.Asn1DecoderBSMInput"} , groupId = "test-group")
    public void receive(String payload) {
        future.complete(payload);
    }
}