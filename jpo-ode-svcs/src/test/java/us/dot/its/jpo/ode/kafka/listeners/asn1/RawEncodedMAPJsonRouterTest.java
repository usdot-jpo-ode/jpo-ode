package us.dot.its.jpo.ode.kafka.listeners.asn1;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static us.dot.its.jpo.ode.test.utilities.ApprovalTestCase.deserializeTestCases;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import us.dot.its.jpo.ode.kafka.listeners.json.RawEncodedJsonService;
import us.dot.its.jpo.ode.kafka.listeners.json.RawEncodedMAPJsonRouter;
import us.dot.its.jpo.ode.kafka.producer.KafkaProducerConfig;
import us.dot.its.jpo.ode.kafka.topics.RawEncodedJsonTopics;
import us.dot.its.jpo.ode.test.utilities.ApprovalTestCase;
import us.dot.its.jpo.ode.udp.controller.UDPReceiverProperties;

@Slf4j
@SpringBootTest(
    classes = {
        KafkaProducerConfig.class,
        KafkaConsumerConfig.class,
        RawEncodedMAPJsonRouter.class,
        RawEncodedJsonService.class,
        SerializationConfig.class,
        TestMetricsConfig.class,
        UDPReceiverProperties.class,
        OdeKafkaProperties.class,
        RawEncodedJsonTopics.class,
        KafkaProperties.class
    },
    properties = {
        "ode.kafka.topics.raw-encoded-json.map=topic.Asn1DecoderTestMAPJSON",
        "ode.kafka.topics.asn1.decoder-input=topic.Asn1DecoderMAPInput"
    })
@EmbeddedKafka
@TestPropertySource(properties = {
        "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}"
})
@EnableConfigurationProperties
@DirtiesContext
class RawEncodedMAPJsonRouterTest {

  @Value("${ode.kafka.topics.raw-encoded-json.map}")
  private String rawEncodedMapJson;

  @Autowired
  KafkaTemplate<String, String> kafkaTemplate;

  @org.springframework.test.context.bean.override.mockito.MockitoBean
  us.dot.its.jpo.ode.codec.ffmlib.FfmlibDecodeService decodeService;

  @Test
  void testProcess_ApprovalTest() throws IOException, InterruptedException {

    String path =
        "src/test/resources/us.dot.its.jpo.ode.udp.map/JSONEncodedMAP_to_Asn1DecoderInput_Validation.json";
    List<ApprovalTestCase> approvalTestCases = deserializeTestCases(path);

    for (ApprovalTestCase approvalTestCase : approvalTestCases) {
      kafkaTemplate.send(rawEncodedMapJson, approvalTestCase.getInput());
    }

    org.mockito.Mockito.verify(decodeService, org.mockito.Mockito.timeout(10000)
        .times(approvalTestCases.size()))
        .decode(org.mockito.ArgumentMatchers.any(us.dot.its.jpo.ode.model.OdeAsn1Data.class),
            org.mockito.ArgumentMatchers.any());
  }
}