package us.dot.its.jpo.ode.codec.ffmlib;

import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.kafka.autoconfigure.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.CommonContainerStoppingErrorHandler;
import org.springframework.kafka.listener.CommonDelegatingErrorHandler;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.ContainerProperties.AckMode;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.BackOff;
import org.springframework.util.backoff.FixedBackOff;
import us.dot.its.jpo.ode.kafka.OdeKafkaClients;
import us.dot.its.jpo.ode.kafka.OdeKafkaProperties;

/** Configures durable FFM raw-topic consumers independently from the external consumer defaults. */
@Configuration
@Conditional(FfmlibUdpRoutingConditions.RawTopic.class)
public class FfmlibKafkaListenerConfiguration {

  /** Pause between redeliveries of a record whose output or quarantine was not confirmed. */
  static final long PUBLICATION_RETRY_INTERVAL_MILLIS = 5_000;

  /** Creates a consumer factory with one record per poll and automatic commits disabled. */
  @Bean("ffmlibKafkaListenerContainerFactory")
  public ConcurrentKafkaListenerContainerFactory<String, String> ffmlibKafkaListenerContainerFactory(
      KafkaProperties kafkaProperties, OdeKafkaProperties odeKafkaProperties,
      FfmlibProperties ffmlibProperties) {
    Map<String, Object> properties =
        OdeKafkaClients.consumerProperties(kafkaProperties, odeKafkaProperties);
    properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
    properties.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 1);
    // At-least-once delivery: a partition without a committed offset (new partitions, or offsets
    // expired after downtime) must start at its oldest retained record, not skip to the end.
    properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    DefaultKafkaConsumerFactory<String, String> consumerFactory =
        new DefaultKafkaConsumerFactory<>(properties, new StringDeserializer(),
            new StringDeserializer());

    ConcurrentKafkaListenerContainerFactory<String, String> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(consumerFactory);
    factory.setConcurrency(ffmlibProperties.getListenerConcurrency());
    factory.setCommonErrorHandler(
        errorHandler(new FixedBackOff(PUBLICATION_RETRY_INTERVAL_MILLIS,
            FixedBackOff.UNLIMITED_ATTEMPTS)));
    factory.getContainerProperties().setAckMode(AckMode.MANUAL_IMMEDIATE);
    factory.getContainerProperties().setSyncCommits(ffmlibProperties.isSyncCommits());
    return factory;
  }

  /**
   * Retries Kafka publication outages in place and stops the listener for anything else.
   *
   * <p>A record whose decoded output or quarantine copy Kafka did not confirm is redelivered with
   * {@code publicationBackOff} until it succeeds; its offset stays uncommitted meanwhile. Other
   * failures that reach the container, such as a systemic native codec fault, stop the listener.
   *
   * @param publicationBackOff pause between redeliveries; it must not run out, because the
   *     error handler would then skip and commit the record
   * @return the listener error handler
   */
  static CommonErrorHandler errorHandler(BackOff publicationBackOff) {
    CommonDelegatingErrorHandler handler =
        new CommonDelegatingErrorHandler(new CommonContainerStoppingErrorHandler());
    handler.addDelegate(FfmlibPublicationFailure.class,
        new DefaultErrorHandler(publicationBackOff));
    return handler;
  }
}
