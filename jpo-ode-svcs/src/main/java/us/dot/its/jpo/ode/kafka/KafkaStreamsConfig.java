package us.dot.its.jpo.ode.kafka;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import us.dot.its.jpo.ode.OdeTimJsonTopology;

/**
 * KafkaStreamsConfig is a Spring configuration class that provides
 * beans related to Kafka Streams topology setup.
 */
@Configuration
public class KafkaStreamsConfig {

  @Bean
  public OdeTimJsonTopology odeTimJsonTopology(
      @Value("${ode.kafka.topics.json.tim}") String timTopic,
      OdeKafkaProperties odeKafkaProperties) {
    return new OdeTimJsonTopology(odeKafkaProperties, timTopic);
  }
}
