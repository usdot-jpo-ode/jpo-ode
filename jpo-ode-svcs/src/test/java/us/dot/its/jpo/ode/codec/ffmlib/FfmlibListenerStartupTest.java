package us.dot.its.jpo.ode.codec.ffmlib;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.ConsumerAwareRebalanceListener;
import org.springframework.kafka.listener.MessageListenerContainer;
import us.dot.its.jpo.ode.kafka.topics.JsonTopics;
import us.dot.its.jpo.ode.kafka.topics.RawEncodedJsonTopics;

class FfmlibListenerStartupTest {

  private static final String RAW_BSM = "topic.StartupRawBSM";
  private static final String DLT_BSM = "dlq.StartupRawBSM";

  @Test
  void startsListenersAndHandlesCommitFailure() throws Exception {
    final FfmlibProperties ffmlibProperties = new FfmlibProperties();
    ffmlibProperties.setListenerConcurrency(1);
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
      doAnswer(invocation -> {
        ((ConsumerAwareRebalanceListener) properties.getConsumerRebalanceListener())
            .onPartitionsAssigned(mock(Consumer.class), List.of());
        return null;
      }).when(container).start();
    }
    when(registry.getListenerContainer(anyString())).thenAnswer(invocation ->
        containers.get(invocation.getArgument(0)));

    FfmlibListenerStartup startup = new FfmlibListenerStartup(rawTopics(), registry, rawJsonListener,
        commitTracker, rawProducer,
        outputProducer, jsonTopics(), ffmlibProperties);
    startup.run(null);

    for (String listenerId : listenerIds()) {
      verify(containers.get(listenerId)).start();
      assertFalse(containerProperties.get(listenerId).isSyncCommits());
    }
    verify(rawJsonListener).markStartupComplete();
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

  @Test
  void startupDeadlineStopsEveryStartedListener() throws Exception {
    FfmlibProperties ffmlibProperties = new FfmlibProperties();
    ffmlibProperties.setListenerConcurrency(1);
    ffmlibProperties.setStartupTimeout(Duration.ofMillis(10));
    KafkaListenerEndpointRegistry registry = mock(KafkaListenerEndpointRegistry.class);
    FfmlibRawJsonListener rawJsonListener = mock(FfmlibRawJsonListener.class);
    FfmlibCommitTracker commitTracker = mock(FfmlibCommitTracker.class);
    @SuppressWarnings("unchecked")
    KafkaTemplate<String, String> rawProducer = mock(KafkaTemplate.class);
    @SuppressWarnings("unchecked")
    KafkaTemplate<String, String> outputProducer = mock(KafkaTemplate.class);
    when(rawProducer.partitionsFor(anyString())).thenReturn(List.of(mock(PartitionInfo.class)));
    when(outputProducer.partitionsFor(anyString())).thenReturn(List.of(mock(PartitionInfo.class)));
    Map<String, MessageListenerContainer> containers = new HashMap<>();
    for (String listenerId : listenerIds()) {
      MessageListenerContainer container = mock(MessageListenerContainer.class);
      containers.put(listenerId, container);
      when(container.getContainerProperties()).thenReturn(new ContainerProperties(RAW_BSM));
    }
    when(registry.getListenerContainer(anyString())).thenAnswer(invocation ->
        containers.get(invocation.getArgument(0)));
    FfmlibListenerStartup startup = new FfmlibListenerStartup(rawTopics("topic.StartupTimeoutRaw"), registry, rawJsonListener,
        commitTracker, rawProducer,
        outputProducer, jsonTopics(), ffmlibProperties);

    assertThrows(IllegalStateException.class, () -> startup.run(null));

    for (String listenerId : listenerIds()) {
      verify(containers.get(listenerId)).start();
      verify(containers.get(listenerId)).stop();
    }
    verify(rawJsonListener).markStartupFailure(any(Throwable.class));
  }

  @Test
  void delayedGroupJoinBeyondThirtySecondsDoesNotTriggerStartupFailure() throws Exception {
    FfmlibProperties ffmlibProperties = new FfmlibProperties();
    ffmlibProperties.setListenerConcurrency(1);
    ffmlibProperties.setStartupTimeout(Duration.ofSeconds(40));
    KafkaListenerEndpointRegistry registry = mock(KafkaListenerEndpointRegistry.class);
    FfmlibRawJsonListener rawJsonListener = mock(FfmlibRawJsonListener.class);
    FfmlibCommitTracker commitTracker = mock(FfmlibCommitTracker.class);
    @SuppressWarnings("unchecked")
    KafkaTemplate<String, String> rawProducer = mock(KafkaTemplate.class);
    @SuppressWarnings("unchecked")
    KafkaTemplate<String, String> outputProducer = mock(KafkaTemplate.class);
    when(rawProducer.partitionsFor(anyString())).thenReturn(List.of(mock(PartitionInfo.class)));
    when(outputProducer.partitionsFor(anyString())).thenReturn(List.of(mock(PartitionInfo.class)));
    Map<String, MessageListenerContainer> containers = new HashMap<>();
    for (String listenerId : listenerIds()) {
      MessageListenerContainer container = mock(MessageListenerContainer.class);
      ContainerProperties properties = new ContainerProperties(RAW_BSM);
      containers.put(listenerId, container);
      when(container.getContainerProperties()).thenReturn(properties);
      doAnswer(invocation -> {
        CompletableFuture.delayedExecutor(31, TimeUnit.SECONDS).execute(() -> {
          try {
            ((ConsumerAwareRebalanceListener) properties.getConsumerRebalanceListener())
                .onPartitionsAssigned(mock(Consumer.class), List.of());
          } catch (Exception error) {
            throw new IllegalStateException(error);
          }
        });
        return null;
      }).when(container).start();
    }
    when(registry.getListenerContainer(anyString())).thenAnswer(invocation ->
        containers.get(invocation.getArgument(0)));
    FfmlibListenerStartup startup = new FfmlibListenerStartup(rawTopics("topic.StartupDelayedRaw"), registry, rawJsonListener,
        commitTracker, rawProducer, outputProducer, jsonTopics(), ffmlibProperties);

    startup.run(null);

    verify(rawJsonListener).markStartupComplete();
  }

  private static RawEncodedJsonTopics rawTopics() {
    return rawTopics("topic.StartupRaw");
  }

  private static RawEncodedJsonTopics rawTopics(String prefix) {
    RawEncodedJsonTopics topics = new RawEncodedJsonTopics();
    topics.setBsm(prefix + "BSM");
    topics.setSpat(prefix + "SPAT");
    topics.setMap(prefix + "MAP");
    topics.setTim(prefix + "TIM");
    topics.setSrm(prefix + "SRM");
    topics.setSsm(prefix + "SSM");
    topics.setPsm(prefix + "PSM");
    topics.setSdsm(prefix + "SDSM");
    topics.setRtcm(prefix + "RTCM");
    topics.setRsm(prefix + "RSM");
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
