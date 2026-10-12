package us.dot.its.jpo.ode.codec.ffmlib;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ConsumerAwareRebalanceListener;
import org.springframework.stereotype.Component;
import us.dot.its.jpo.ode.kafka.topics.JsonTopics;
import us.dot.its.jpo.ode.kafka.topics.RawEncodedJsonTopics;

/** Starts FFM consumers after warming producer metadata for externally provisioned topics. */
@Component
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@ConditionalOnProperty(name = "ode.asn1.codec-mode", havingValue = "ffm")
public class FfmlibListenerStartup implements ApplicationRunner {

  private static final List<String> LISTENER_IDS = List.of(
      "RawEncodedBSMJsonRouter", "RawEncodedSPATJsonRouter", "RawEncodedMAPJsonRouter",
      "RawEncodedTIMJsonRouter", "RawEncodedSRMJsonRouter", "RawEncodedSSMJsonRouter",
      "RawEncodedPSMJsonRouter", "RawEncodedSDSMJsonRouter", "RawEncodedRTCMJsonRouter",
      "RawEncodedRSMJsonRouter");

  private final RawEncodedJsonTopics rawTopics;
  private final KafkaListenerEndpointRegistry registry;
  private final FfmlibRawJsonListener rawJsonListener;
  private final FfmlibCommitTracker commitTracker;
  private final KafkaTemplate<String, String> rawProducer;
  private final KafkaTemplate<String, String> outputProducer;
  private final JsonTopics jsonTopics;
  private final boolean syncCommits;
  private final int listenerConcurrency;
  private final Duration startupTimeout;

  /**
   * Creates the listener startup coordinator for the configured Kafka cluster.
   *
   * @param rawTopics configured raw message topics
   * @param registry listener containers to start after metadata warmup
   * @param rawJsonListener health state for listener failures
   * @param commitTracker metrics for asynchronous offset commits
   * @param rawProducer dedicated UDP raw-topic producer
   * @param outputProducer dedicated decoded JSON and quarantine producer
   * @param jsonTopics configured decoded JSON topic names
   * @param ffmlibProperties FFM listener and startup configuration
   */
  public FfmlibListenerStartup(
      RawEncodedJsonTopics rawTopics,
      KafkaListenerEndpointRegistry registry, FfmlibRawJsonListener rawJsonListener,
      FfmlibCommitTracker commitTracker,
      @org.springframework.beans.factory.annotation.Qualifier("ffmlibRawKafkaTemplate")
      KafkaTemplate<String, String> rawProducer,
      @org.springframework.beans.factory.annotation.Qualifier("ffmlibOutputKafkaTemplate")
      KafkaTemplate<String, String> outputProducer,
      JsonTopics jsonTopics,
      FfmlibProperties ffmlibProperties) {
    this.rawTopics = rawTopics;
    this.registry = registry;
    this.rawJsonListener = rawJsonListener;
    this.commitTracker = commitTracker;
    this.rawProducer = rawProducer;
    this.outputProducer = outputProducer;
    this.jsonTopics = jsonTopics;
    this.syncCommits = ffmlibProperties.isSyncCommits();
    this.listenerConcurrency = ffmlibProperties.getListenerConcurrency();
    this.startupTimeout = ffmlibProperties.getStartupTimeout();
  }

  @Override
  public void run(ApplicationArguments args) throws Exception {
    List<org.springframework.kafka.listener.MessageListenerContainer> started = new ArrayList<>();
    try {
      warmProducerMetadata(rawProducer, rawTopics.allTopics());
      warmProducerMetadata(outputProducer, outputTopics());
      commitTracker.setSynchronous(syncCommits);
      Map<String, AssignmentTracker> assignments = new HashMap<>();
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
            container.stop(() -> log.info("Stopped FFM listener {} after commit failure",
                listenerId));
          }
        });
        AssignmentTracker tracker = new AssignmentTracker(listenerConcurrency);
        containerProperties.setConsumerRebalanceListener(tracker);
        assignments.put(listenerId, tracker);
        started.add(container);
        container.start();
      }
      long deadline = System.nanoTime() + startupTimeout.toNanos();
      for (String listenerId : LISTENER_IDS) {
        awaitGroupJoin(listenerId, assignments.get(listenerId), deadline);
      }
      rawJsonListener.markStartupComplete();
      log.info("Started FFM raw-topic consumers using externally provisioned topics");
    } catch (Exception error) {
      rawJsonListener.markStartupFailure(error);
      stopStarted(started);
      throw error;
    } catch (Error error) {
      rawJsonListener.markStartupFailure(error);
      stopStarted(started);
      throw error;
    }
  }

  private static void stopStarted(
      List<org.springframework.kafka.listener.MessageListenerContainer> started) {
    for (var container : started) {
      try {
        container.stop();
      } catch (RuntimeException stopError) {
        log.warn("Unable to stop a partially started FFM listener container", stopError);
      }
    }
  }

  private void awaitGroupJoin(String listenerId, AssignmentTracker tracker, long deadline)
      throws InterruptedException {
    while (!tracker.isReady()) {
      long remaining = deadline - System.nanoTime();
      if (remaining <= 0) {
        throw new IllegalStateException("FFM listener " + listenerId
            + " did not join its Kafka consumer group within " + startupTimeout);
      }
      TimeUnit.NANOSECONDS.sleep(Math.min(remaining, TimeUnit.MILLISECONDS.toNanos(50)));
    }
  }

  private static final class AssignmentTracker implements ConsumerAwareRebalanceListener {
    private final int expectedConsumers;
    private final Set<Consumer<?, ?>> assignedConsumers = ConcurrentHashMap.newKeySet();

    private AssignmentTracker(int expectedConsumers) {
      this.expectedConsumers = expectedConsumers;
    }

    @Override
    public void onPartitionsAssigned(Consumer<?, ?> consumer, Collection<TopicPartition> partitions) {
      // An empty assignment is a successful group join when all topic partitions are owned by
      // other ODE instances. The callback, rather than a nonempty partition list, is readiness.
      assignedConsumers.add(consumer);
    }

    private boolean isReady() {
      return assignedConsumers.size() >= expectedConsumers;
    }
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
      withDeadLetters.add(FfmlibDeadLetterTopics.forRawTopic(rawTopic));
    }
    return withDeadLetters;
  }

}
