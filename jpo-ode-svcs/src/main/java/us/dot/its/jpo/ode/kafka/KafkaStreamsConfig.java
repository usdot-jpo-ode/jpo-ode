package us.dot.its.jpo.ode.kafka;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import us.dot.its.jpo.ode.OdeTimJsonTopology;

/**
 * KafkaStreamsConfig is a Spring configuration class that provides beans
 * related to Kafka Streams topology setup.
 */
@Configuration
public class KafkaStreamsConfig {

  @Bean
  public OdeTimJsonTopology odeTimJsonTopology(
      @Value("${ode.kafka.topics.json.tim}") String timTopic,
      @Value("${ode.kafka.topics.json.tim-ktable}") String timKTableTopic,
      OdeKafkaProperties odeKafkaProperties,
      KafkaTemplate<String, String> kafkaTemplate) {
    return new OdeTimJsonTopology(odeKafkaProperties, timTopic, timKTableTopic, kafkaTemplate);
  }
}
