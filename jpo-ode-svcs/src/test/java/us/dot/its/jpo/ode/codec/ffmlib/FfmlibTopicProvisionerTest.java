package us.dot.its.jpo.ode.codec.ffmlib;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ConfigEntry;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.config.ConfigResource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.kafka.autoconfigure.KafkaProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import us.dot.its.jpo.ode.kafka.OdeKafkaProperties;
import us.dot.its.jpo.ode.kafka.topics.JsonTopics;
import us.dot.its.jpo.ode.kafka.topics.RawEncodedJsonTopics;

@SpringBootTest(classes = FfmlibTopicProvisionerTest.TestContext.class,
    properties = "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}")
@EmbeddedKafka(partitions = 1)
class FfmlibTopicProvisionerTest {

  private static final String RAW_BSM = "topic.ProvisionerRawBSM";
  private static final String DLT_BSM = RAW_BSM + ".FFM.DLT";

  @Autowired
  private EmbeddedKafkaBroker embeddedKafka;

  @Configuration
  @EnableAutoConfiguration
  static class TestContext {
  }

  @Test
  void provisionsTopicsAndPreservesLargerExistingSettings() throws Exception {
    String broker = embeddedKafka.getBrokersAsString();
    Map<String, Object> adminProperties = Map.of("bootstrap.servers", broker);
    try (AdminClient admin = AdminClient.create(adminProperties)) {
      Map<String, String> rawConfig = Map.of("retention.ms", "172800000");
      Map<String, String> dltConfig = Map.of("retention.ms", "1209600000");
      admin.createTopics(List.of(
          new NewTopic(RAW_BSM, 6, (short) 1).configs(rawConfig),
          new NewTopic(DLT_BSM, 1, (short) 1).configs(dltConfig)))
          .all().get(10, TimeUnit.SECONDS);
    }

    OdeKafkaProperties odeProperties = new OdeKafkaProperties();
    odeProperties.setBrokers(broker);
    KafkaProperties kafkaProperties = new KafkaProperties();
    kafkaProperties.setBootstrapServers(List.of(broker));
    final FfmlibProperties ffmlibProperties = new FfmlibProperties();
    final KafkaListenerEndpointRegistry registry = mock(KafkaListenerEndpointRegistry.class);
    final FfmlibRawJsonListener rawJsonListener = mock(FfmlibRawJsonListener.class);
    final FfmlibCommitTracker commitTracker = mock(FfmlibCommitTracker.class);
    @SuppressWarnings("unchecked")
    KafkaTemplate<String, String> rawProducer = mock(KafkaTemplate.class);
    @SuppressWarnings("unchecked")
    KafkaTemplate<String, String> outputProducer = mock(KafkaTemplate.class);
    when(rawProducer.partitionsFor(anyString())).thenReturn(List.of(mock(PartitionInfo.class)));
    when(outputProducer.partitionsFor(anyString())).thenReturn(List.of(mock(PartitionInfo.class)));
    Map<String, MessageListenerContainer> containers = new HashMap<>();
    Map<String, ContainerProperties> containerProperties = new HashMap<>();
    for (String listenerId : listenerIds()) {
      MessageListenerContainer container = mock(MessageListenerContainer.class);
      containers.put(listenerId, container);
      when(container.getAssignedPartitions()).thenReturn(List.of(new TopicPartition(RAW_BSM, 0)));
      ContainerProperties properties = new ContainerProperties(RAW_BSM);
      containerProperties.put(listenerId, properties);
      when(container.getContainerProperties()).thenReturn(properties);
    }
    when(registry.getListenerContainer(anyString())).thenAnswer(invocation ->
        containers.get(invocation.getArgument(0)));

    FfmlibTopicProvisioner provisioner = new FfmlibTopicProvisioner(kafkaProperties,
        odeProperties, rawTopics(), registry, rawJsonListener, commitTracker, rawProducer,
        outputProducer, jsonTopics(), ffmlibProperties);
    provisioner.run(null);

    try (AdminClient admin = AdminClient.create(adminProperties)) {
      var descriptions = admin.describeTopics(List.of(RAW_BSM, DLT_BSM,
          "topic.ProvisionerRawMAP", "topic.ProvisionerRawMAP.FFM.DLT"))
          .allTopicNames().get(10, TimeUnit.SECONDS);
      assertEquals(6, descriptions.get(RAW_BSM).partitions().size());
      assertEquals(4, descriptions.get(DLT_BSM).partitions().size());
      assertEquals(4, descriptions.get("topic.ProvisionerRawMAP").partitions().size());
      assertEquals(4, descriptions.get("topic.ProvisionerRawMAP.FFM.DLT").partitions().size());

      var configs = admin.describeConfigs(List.of(
          new ConfigResource(ConfigResource.Type.TOPIC, RAW_BSM),
          new ConfigResource(ConfigResource.Type.TOPIC, DLT_BSM),
          new ConfigResource(ConfigResource.Type.TOPIC, "topic.ProvisionerRawMAP"),
          new ConfigResource(ConfigResource.Type.TOPIC, "topic.ProvisionerRawMAP.FFM.DLT")))
          .all().get(10, TimeUnit.SECONDS);
      assertEquals("172800000", retention(configs,
          new ConfigResource(ConfigResource.Type.TOPIC, RAW_BSM)));
      assertEquals("1209600000", retention(configs,
          new ConfigResource(ConfigResource.Type.TOPIC, DLT_BSM)));
      assertEquals("86400000", retention(configs,
          new ConfigResource(ConfigResource.Type.TOPIC, "topic.ProvisionerRawMAP")));
      assertEquals("604800000", retention(configs,
          new ConfigResource(ConfigResource.Type.TOPIC, "topic.ProvisionerRawMAP.FFM.DLT")));
    }

    for (String listenerId : listenerIds()) {
      verify(containers.get(listenerId)).start();
      assertFalse(containerProperties.get(listenerId).isSyncCommits());
    }
    verify(rawProducer).partitionsFor(RAW_BSM);
    verify(outputProducer).partitionsFor("topic.OdeBsmJson");
    verify(outputProducer).partitionsFor(DLT_BSM);

    var callback = containerProperties.get("RawEncodedBSMJsonRouter").getCommitCallback();
    var commitError = new IllegalStateException("broker unavailable");
    callback.onComplete(Map.of(new org.apache.kafka.common.TopicPartition(RAW_BSM, 0),
        new org.apache.kafka.clients.consumer.OffsetAndMetadata(1L)), commitError);
    verify(rawJsonListener).recordCommitFailure("RawEncodedBSMJsonRouter", commitError);
    verify(containers.get("RawEncodedBSMJsonRouter")).stop(any(Runnable.class));
    verify(commitTracker).completeCommit(commitError);
  }

  private static String retention(
      Map<ConfigResource, org.apache.kafka.clients.admin.Config> configs,
      ConfigResource resource) {
    ConfigEntry retention = configs.get(resource).get("retention.ms");
    return retention.value();
  }

  private static RawEncodedJsonTopics rawTopics() {
    RawEncodedJsonTopics topics = new RawEncodedJsonTopics();
    topics.setBsm(RAW_BSM);
    topics.setSpat("topic.ProvisionerRawSPAT");
    topics.setMap("topic.ProvisionerRawMAP");
    topics.setTim("topic.ProvisionerRawTIM");
    topics.setSrm("topic.ProvisionerRawSRM");
    topics.setSsm("topic.ProvisionerRawSSM");
    topics.setPsm("topic.ProvisionerRawPSM");
    topics.setSdsm("topic.ProvisionerRawSDSM");
    topics.setRtcm("topic.ProvisionerRawRTCM");
    topics.setRsm("topic.ProvisionerRawRSM");
    return topics;
  }

  private static JsonTopics jsonTopics() {
    JsonTopics topics = new JsonTopics();
    topics.setBsm("topic.OdeBsmJson");
    topics.setSpat("topic.OdeSpatJson");
    topics.setMap("topic.OdeMapJson");
    topics.setTim("topic.OdeTimJson");
    topics.setSrm("topic.OdeSrmJson");
    topics.setSsm("topic.OdeSsmJson");
    topics.setPsm("topic.OdePsmJson");
    topics.setSdsm("topic.OdeSdsmJson");
    topics.setRtcm("topic.OdeRtcmJson");
    topics.setRsm("topic.OdeRsmJson");
    return topics;
  }

  private static List<String> listenerIds() {
    return List.of("RawEncodedBSMJsonRouter", "RawEncodedSPATJsonRouter",
        "RawEncodedMAPJsonRouter", "RawEncodedTIMJsonRouter", "RawEncodedSRMJsonRouter",
        "RawEncodedSSMJsonRouter", "RawEncodedPSMJsonRouter", "RawEncodedSDSMJsonRouter",
        "RawEncodedRTCMJsonRouter", "RawEncodedRSMJsonRouter");
  }
}
