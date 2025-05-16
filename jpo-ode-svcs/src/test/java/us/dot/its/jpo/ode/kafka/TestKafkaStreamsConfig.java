package us.dot.its.jpo.ode.kafka;

import org.awaitility.Awaitility;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import us.dot.its.jpo.ode.OdeTimJsonTopology;
import us.dot.its.jpo.ode.test.utilities.EmbeddedKafkaHolder;

/**
 * TestKafkaStreamsConfig is a test configuration class that provides a Kafka Streams topology
 * for testing purposes. It utilizes an embedded Kafka broker to facilitate the testing of TIM
 * (Traveler Information Message) JSON data processing.
 */
@TestConfiguration
public class TestKafkaStreamsConfig {

  /**
   * Creates and initializes an instance of OdeTimJsonTopology for processing TIM (Traveler Information Message) JSON data.
   * This method adds the specified Kafka topic to the embedded Kafka broker, creates the topology,
   * and ensures it is in a running state before returning.
   *
   * @param odeKafkaProperties the configuration properties for Kafka
   * @param timTopic           the name of the Kafka topic used for consuming TIM JSON data.
   *
   * @return the initialized instance of OdeTimJsonTopology.
   */
  @Bean
  public OdeTimJsonTopology odeTimJsonTopology(OdeKafkaProperties odeKafkaProperties,
      @Value("${ode.kafka.topics.json.tim}") String timTopic) {
    EmbeddedKafkaHolder.addTopics(timTopic);
    var topology = new OdeTimJsonTopology(odeKafkaProperties, timTopic);
    Awaitility.await().until(topology::isRunning);
    return topology;
  }
}
