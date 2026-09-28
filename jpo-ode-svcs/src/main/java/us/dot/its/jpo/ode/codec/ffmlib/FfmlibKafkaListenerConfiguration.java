package us.dot.its.jpo.ode.codec.ffmlib;

import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.kafka.autoconfigure.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.CommonContainerStoppingErrorHandler;
import org.springframework.kafka.listener.ContainerProperties.AckMode;
import us.dot.its.jpo.ode.kafka.OdeKafkaClients;
import us.dot.its.jpo.ode.kafka.OdeKafkaProperties;

/** Configures durable FFM raw-topic consumers independently from the external consumer defaults. */
@Configuration
@ConditionalOnProperty(name = "ode.asn1.codec-mode", havingValue = "ffm")
public class FfmlibKafkaListenerConfiguration {

  /** Creates a consumer factory with one record per poll and automatic commits disabled. */
  @Bean("ffmlibKafkaListenerContainerFactory")
  public ConcurrentKafkaListenerContainerFactory<String, String> ffmlibKafkaListenerContainerFactory(
      KafkaProperties kafkaProperties, OdeKafkaProperties odeKafkaProperties,
      @Value("${ode.ffmlib.listener-concurrency:4}") int concurrency,
      FfmlibProperties ffmlibProperties) {
    Map<String, Object> properties =
        OdeKafkaClients.consumerProperties(kafkaProperties, odeKafkaProperties);
    properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
    properties.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 1);
    DefaultKafkaConsumerFactory<String, String> consumerFactory =
        new DefaultKafkaConsumerFactory<>(properties, new StringDeserializer(),
            new StringDeserializer());

    ConcurrentKafkaListenerContainerFactory<String, String> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(consumerFactory);
    factory.setConcurrency(concurrency);
    factory.setCommonErrorHandler(new CommonContainerStoppingErrorHandler());
    factory.getContainerProperties().setAckMode(AckMode.MANUAL_IMMEDIATE);
    factory.getContainerProperties().setSyncCommits(ffmlibProperties.isSyncCommits());
    return factory;
  }
}
