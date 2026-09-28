package us.dot.its.jpo.ode.codec.ffmlib;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AlterConfigOp;
import org.apache.kafka.clients.admin.ConfigEntry;
import org.apache.kafka.clients.admin.NewPartitions;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.config.ConfigResource;
import org.apache.kafka.common.errors.RetriableException;
import org.apache.kafka.common.errors.TopicExistsException;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import us.dot.its.jpo.ode.kafka.OdeKafkaClients;
import us.dot.its.jpo.ode.kafka.OdeKafkaProperties;
import us.dot.its.jpo.ode.kafka.topics.JsonTopics;
import us.dot.its.jpo.ode.kafka.topics.RawEncodedJsonTopics;

/** Provisions and verifies the FFM raw and quarantine topics before starting FFM consumers. */
@Component
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@ConditionalOnProperty(name = "ode.asn1.codec-mode", havingValue = "ffm")
public class FfmlibTopicProvisioner implements ApplicationRunner {

  private static final String RETENTION_MS = "retention.ms";
  private static final int METADATA_ATTEMPTS = 20;
  private static final long METADATA_RETRY_DELAY_MS = 100;
  private static final int PARTITION_UPDATE_ATTEMPTS = 5;
  private static final long PARTITION_UPDATE_RETRY_DELAY_MS = 100;
  private static final List<String> LISTENER_IDS = List.of(
      "RawEncodedBSMJsonRouter", "RawEncodedSPATJsonRouter", "RawEncodedMAPJsonRouter",
      "RawEncodedTIMJsonRouter", "RawEncodedSRMJsonRouter", "RawEncodedSSMJsonRouter",
      "RawEncodedPSMJsonRouter", "RawEncodedSDSMJsonRouter", "RawEncodedRTCMJsonRouter",
      "RawEncodedRSMJsonRouter");

  private final Map<String, Object> adminProperties;
  private final RawEncodedJsonTopics rawTopics;
  private final KafkaListenerEndpointRegistry registry;
  private final FfmlibRawJsonListener rawJsonListener;
  private final FfmlibCommitTracker commitTracker;
  private final KafkaTemplate<String, String> rawProducer;
  private final KafkaTemplate<String, String> outputProducer;
  private final JsonTopics jsonTopics;
  private final int partitions;
  private final long rawRetentionMs;
  private final long dltRetentionMs;
  private final boolean syncCommits;

  /**
   * Creates the topic provisioner for the configured Kafka cluster.
   *
   * @param kafkaProperties Spring Kafka client defaults
   * @param odeKafkaProperties ODE broker configuration
   * @param rawTopics configured raw message topics
   * @param registry listener containers to start after verification
   * @param rawJsonListener health state for listener failures
   * @param commitTracker metrics for asynchronous offset commits
   * @param rawProducer dedicated UDP raw-topic producer
   * @param outputProducer dedicated decoded JSON and quarantine producer
   * @param jsonTopics configured decoded JSON topic names
   * @param ffmlibProperties FFM topic partitions and retention
   */
  public FfmlibTopicProvisioner(
      org.springframework.boot.kafka.autoconfigure.KafkaProperties kafkaProperties,
      OdeKafkaProperties odeKafkaProperties, RawEncodedJsonTopics rawTopics,
      KafkaListenerEndpointRegistry registry, FfmlibRawJsonListener rawJsonListener,
      FfmlibCommitTracker commitTracker,
      @org.springframework.beans.factory.annotation.Qualifier("ffmlibRawKafkaTemplate")
      KafkaTemplate<String, String> rawProducer,
      @org.springframework.beans.factory.annotation.Qualifier("ffmlibOutputKafkaTemplate")
      KafkaTemplate<String, String> outputProducer,
      JsonTopics jsonTopics,
      FfmlibProperties ffmlibProperties) {
    this.adminProperties = OdeKafkaClients.adminProperties(kafkaProperties, odeKafkaProperties);
    this.rawTopics = rawTopics;
    this.registry = registry;
    this.rawJsonListener = rawJsonListener;
    this.commitTracker = commitTracker;
    this.rawProducer = rawProducer;
    this.outputProducer = outputProducer;
    this.jsonTopics = jsonTopics;
    this.partitions = ffmlibProperties.getTopicPartitions();
    this.rawRetentionMs = ffmlibProperties.getRawTopicRetentionMs();
    this.dltRetentionMs = ffmlibProperties.getDltRetentionMs();
    this.syncCommits = ffmlibProperties.isSyncCommits();
  }

  @Override
  public void run(ApplicationArguments args) throws Exception {
    List<TopicRetention> topics = topicRetentions();
    try (AdminClient admin = AdminClient.create(adminProperties)) {
      createMissingTopics(admin, topics);
      ensureMinimumPartitions(admin, topics);
      ensureMinimumRetention(admin, topics);
      verifyTopics(admin, topics);
    }
    warmProducerMetadata(rawProducer, rawTopics.allTopics());
    warmProducerMetadata(outputProducer, outputTopics());
    commitTracker.setSynchronous(syncCommits);
    for (String listenerId : LISTENER_IDS) {
      var container = registry.getListenerContainer(listenerId);
      if (container == null) {
        throw new IllegalStateException("Missing FFM Kafka listener container " + listenerId);
      }
      var containerProperties = container.getContainerProperties();
      containerProperties.setSyncCommits(syncCommits);
      containerProperties.setCommitCallback((offsets, error) -> {
        commitTracker.completeCommit(error);
        if (error != null) {
          rawJsonListener.recordCommitFailure(listenerId, error);
          log.error("Stopping FFM listener {} after offset commit failure for {}", listenerId,
              offsets);
          container.stop(() -> log.info("Stopped FFM listener {} after commit failure", listenerId));
        }
      });
      container.start();
      awaitAssignment(listenerId, container);
    }
    log.info("Verified {} FFM raw and DLT topics and started the raw-topic consumers",
        topics.size());
  }

  private void warmProducerMetadata(KafkaTemplate<String, String> producer,
      List<String> topicNames) {
    for (String topicName : topicNames) {
      List<PartitionInfo> partitions = producer.partitionsFor(topicName);
      if (partitions == null || partitions.isEmpty()) {
        throw new IllegalStateException("No producer metadata available for FFM topic "
            + topicName);
      }
    }
  }

  private List<String> outputTopics() {
    List<String> topics = List.of(jsonTopics.getBsm(), jsonTopics.getMap(), jsonTopics.getPsm(),
        jsonTopics.getSpat(), jsonTopics.getSrm(), jsonTopics.getSsm(), jsonTopics.getTim(),
        jsonTopics.getSdsm(), jsonTopics.getRtcm(), jsonTopics.getRsm());
    List<String> withDeadLetters = new ArrayList<>(topics);
    for (String rawTopic : rawTopics.allTopics()) {
      withDeadLetters.add(rawTopic + ".FFM.DLT");
    }
    return withDeadLetters;
  }

  private static void awaitAssignment(String listenerId,
      org.springframework.kafka.listener.MessageListenerContainer container)
      throws InterruptedException {
    long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(30);
    while (container.getAssignedPartitions() == null
        || container.getAssignedPartitions().isEmpty()) {
      if (System.nanoTime() >= deadline) {
        throw new IllegalStateException("FFM listener " + listenerId
            + " did not receive a Kafka partition assignment within 30 seconds");
      }
      Thread.sleep(50);
    }
  }

  private List<TopicRetention> topicRetentions() {
    List<TopicRetention> topics = new ArrayList<>();
    for (String rawTopic : rawTopics.allTopics()) {
      topics.add(new TopicRetention(rawTopic, rawRetentionMs));
      topics.add(new TopicRetention(rawTopic + ".FFM.DLT", dltRetentionMs));
    }
    return topics;
  }

  private void createMissingTopics(AdminClient admin, List<TopicRetention> topics) throws Exception {
    for (TopicRetention topic : topics) {
      NewTopic newTopic = new NewTopic(topic.name(), partitions, (short) 1)
          .configs(Map.of(RETENTION_MS, Long.toString(topic.retentionMs())));
      try {
        admin.createTopics(List.of(newTopic)).all().get(30, TimeUnit.SECONDS);
      } catch (ExecutionException error) {
        if (!(error.getCause() instanceof TopicExistsException)) {
          throw error;
        }
      }
    }
  }

  private void ensureMinimumPartitions(AdminClient admin, List<TopicRetention> topics)
      throws Exception {
    for (int attempt = 1; attempt <= PARTITION_UPDATE_ATTEMPTS; attempt++) {
      Map<String, org.apache.kafka.clients.admin.TopicDescription> descriptions =
          describeTopicsWithRetry(admin, topics);
      Map<String, NewPartitions> increases = new HashMap<>();
      for (TopicRetention topic : topics) {
        int existing = descriptions.get(topic.name()).partitions().size();
        if (existing < partitions) {
          increases.put(topic.name(), NewPartitions.increaseTo(partitions));
        }
      }
      if (increases.isEmpty()) {
        return;
      }
      try {
        admin.createPartitions(increases).all().get(30, TimeUnit.SECONDS);
        return;
      } catch (ExecutionException error) {
        if (!(error.getCause() instanceof RetriableException)
            || attempt == PARTITION_UPDATE_ATTEMPTS) {
          throw error;
        }
        log.debug("Kafka metadata is not ready while increasing topic partitions; retrying "
            + "attempt {}/{}", attempt, PARTITION_UPDATE_ATTEMPTS, error);
        TimeUnit.MILLISECONDS.sleep(PARTITION_UPDATE_RETRY_DELAY_MS);
      }
    }
  }

  private void ensureMinimumRetention(AdminClient admin, List<TopicRetention> topics)
      throws Exception {
    Map<ConfigResource, Collection<AlterConfigOp>> alterations = new HashMap<>();
    for (TopicRetention topic : topics) {
      ConfigResource resource = topicResource(topic.name());
      var config = admin.describeConfigs(List.of(resource)).all().get(30, TimeUnit.SECONDS)
          .get(resource);
      ConfigEntry current = config.get(RETENTION_MS);
      if (current == null || shouldIncrease(current.value(), topic.retentionMs())) {
        alterations.put(resource, List.of(new AlterConfigOp(
            new ConfigEntry(RETENTION_MS, Long.toString(topic.retentionMs())),
            AlterConfigOp.OpType.SET)));
      }
    }
    if (!alterations.isEmpty()) {
      admin.incrementalAlterConfigs(alterations).all().get(30, TimeUnit.SECONDS);
    }
  }

  private void verifyTopics(AdminClient admin, List<TopicRetention> topics) throws Exception {
    Map<String, org.apache.kafka.clients.admin.TopicDescription> descriptions =
        describeTopicsWithRetry(admin, topics);
    Map<ConfigResource, org.apache.kafka.clients.admin.Config> configs = new HashMap<>();
    for (TopicRetention topic : topics) {
      ConfigResource resource = topicResource(topic.name());
      configs.put(resource,
          admin.describeConfigs(List.of(resource)).all().get(30, TimeUnit.SECONDS).get(resource));
    }
    for (TopicRetention topic : topics) {
      int existingPartitions = descriptions.get(topic.name()).partitions().size();
      ConfigEntry retention = configs.get(topicResource(topic.name())).get(RETENTION_MS);
      if (existingPartitions < partitions || retention == null
          || shouldIncrease(retention.value(), topic.retentionMs())) {
        throw new IllegalStateException("FFM topic settings do not meet requirements: "
            + topic.name());
      }
    }
  }

  private Map<String, org.apache.kafka.clients.admin.TopicDescription> describeTopicsWithRetry(
      AdminClient admin, List<TopicRetention> topics) throws Exception {
    List<String> topicNames = topics.stream().map(TopicRetention::name).toList();
    for (int attempt = 1; attempt <= METADATA_ATTEMPTS; attempt++) {
      try {
        return admin.describeTopics(topicNames).allTopicNames().get(30, TimeUnit.SECONDS);
      } catch (ExecutionException error) {
        if (!(error.getCause() instanceof RetriableException) || attempt == METADATA_ATTEMPTS) {
          throw error;
        }
        log.debug("Kafka metadata is not ready for configured FFM topics; retrying attempt {}/{}",
            attempt, METADATA_ATTEMPTS, error);
        TimeUnit.MILLISECONDS.sleep(METADATA_RETRY_DELAY_MS);
      }
    }
    throw new IllegalStateException("Could not read Kafka metadata for configured FFM topics");
  }

  private static boolean shouldIncrease(String existingValue, long requiredValue) {
    if (existingValue == null) {
      return true;
    }
    long current = Long.parseLong(existingValue);
    return current != -1 && current < requiredValue;
  }

  private static ConfigResource topicResource(String name) {
    return new ConfigResource(ConfigResource.Type.TOPIC, name);
  }

  private record TopicRetention(String name, long retentionMs) {
  }
}
