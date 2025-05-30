package us.dot.its.jpo.ode.kafka.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import io.micrometer.core.instrument.MeterRegistry;
import java.util.Map;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import us.dot.its.jpo.ode.kafka.OdeKafkaProperties;
import us.dot.its.jpo.ode.kafka.XMLOdeObjectSerializer;
import us.dot.its.jpo.ode.model.OdeBsmData;
import us.dot.its.jpo.ode.model.OdeObject;
import us.dot.its.jpo.ode.wrapper.serdes.MessagingSerializer;

/**
 * KafkaProducerConfig is a configuration class for setting up Kafka producers
 * with Spring Boot.
 * This class utilizes the KafkaProperties and OdeKafkaProperties to define and
 * construct the
 * necessary producer factories and Kafka templates for producing messages to
 * Kafka topics.
 *
 * </p>
 * It provides configuration for two types of Kafka producer factories and
 * templates:
 * one for producing regular String messages and another for producing
 * `OdeObject` messages
 * serialized as XML.
 *
 * </p>
 * This configuration is crucial for integrating with Kafka by providing
 * necessary producer settings and managing producer instances.
 */
@EnableKafka
@Configuration
public class KafkaProducerConfig {

  private final KafkaProperties kafkaProperties;
  private final OdeKafkaProperties odeKafkaProperties;
  private final MeterRegistry meterRegistry;

  /**
   * Constructor for the KafkaProducerConfig class, which sets up the
   * configuration for Kafka
   * producers using provided Kafka properties.
   *
   * @param kafkaProperties    the properties related to Kafka configuration as
   *                           set up in the Spring
   *                           environment, providing necessary configurations for
   *                           creating Kafka
   *                           producers.
   * @param odeKafkaProperties the properties specific to the ODE Kafka setup,
   *                           including custom
   *                           configurations like Kafka type (e.g., CONFLUENT)
   *                           and other
   *                           specialized settings for integrating with the ODE
   *                           infrastructure.
   */
  public KafkaProducerConfig(KafkaProperties kafkaProperties,
      OdeKafkaProperties odeKafkaProperties, MeterRegistry meterRegistry) {
    this.kafkaProperties = kafkaProperties;
    this.odeKafkaProperties = odeKafkaProperties;
    this.meterRegistry = meterRegistry;
  }

  /**
   * Creates a Kafka ProducerFactory configured for producing messages with String
   * keys and String
   * values. This factory sets up and manages the configuration needed for
   * producing messages to
   * Kafka topics using the properties defined in the application configuration.
   *
   * @return a ProducerFactory instance for creating Kafka producers with String
   *         key and value
   *         serializers. This includes any custom properties defined for Kafka
   *         producers, as well as
   *         additional settings for Confluent-based Kafka setups if applicable.
   */
  @Bean
  public ProducerFactory<String, String> producerFactory() {
    return new DefaultKafkaProducerFactory<>(buildProducerProperties());
  }

  /**
   * Creates and returns a KafkaTemplate that allows for sending messages with
   * String keys and
   * String values to Kafka topics. This template is configured using the
   * ProducerFactory instance
   * provided by the producerFactory() method, ensuring that it is set up with the
   * necessary
   * serializers and additional properties defined in the application
   * configuration.
   *
   * @return a KafkaTemplate instance configured for publishing messages to Kafka
   *         topics with String
   *         keys and values, facilitating message sending operations in Kafka.
   */
  @Bean
  public KafkaTemplate<String, String> kafkaTemplate(
      ProducerFactory<String, String> producerFactory, ObjectMapper objectMapper) {
    var template = new InterceptingKafkaTemplate<>(producerFactory,
        this.odeKafkaProperties.getDisabledTopics(), meterRegistry, objectMapper);

    template.setProducerListener(new LoggingProducerListener<>());

    return template;
  }

  /**
   * Creates a Kafka ProducerFactory specifically configured for handling messages
   * with String keys
   * and OdeObject values. This factory utilizes a custom XML serializer for
   * OdeObjects, enabling
   * proper serialization for Kafka message transmission.
   *
   * @return a ProducerFactory instance configured with String serializers for
   *         keys and a custom
   *         XMLOdeObjectSerializer for OdeObject values, using producer
   *         properties tailored to the
   *         application's Kafka and ODE settings.
   */
  @Bean
  public ProducerFactory<String, OdeObject> odeDataProducerFactory(XmlMapper xmlMapper) {
    return new DefaultKafkaProducerFactory<>(buildProducerProperties(),
        new StringSerializer(), new XMLOdeObjectSerializer(xmlMapper));
  }

  /**
   * Creates and returns a KafkaTemplate for sending messages with String keys and
   * OdeObject values
   * to Kafka topics. The template is configured using the ProducerFactory
   * provided by the
   * odeDataProducerFactory() method, ensuring it incorporates the necessary
   * serializers and
   * properties for proper message handling in the context of ODE and Kafka
   * integration.
   *
   * @return a KafkaTemplate instance configured for handling messages with String
   *         keys and
   *         OdeObject values, enabling seamless message publication to Kafka
   *         topics within the
   *         application's messaging infrastructure.
   */
  @Bean
  public KafkaTemplate<String, OdeObject> odeDataKafkaTemplate(
      ProducerFactory<String, OdeObject> producerFactory, ObjectMapper objectMapper) {
    var template = new InterceptingKafkaTemplate<>(producerFactory,
        this.odeKafkaProperties.getDisabledTopics(), meterRegistry, objectMapper);
    template.setProducerListener(new LoggingProducerListener<>());

    return template;
  }

  /**
   * Creates a Kafka ProducerFactory configured for producing messages with String
   * keys and
   * OdeBsmData values. This factory utilizes the provided producer properties,
   * along with specific
   * serializers for the key and value types.
   *
   * @return a ProducerFactory instance configured for handling String keys and
   *         OdeBsmData values
   *         with the necessary serialization settings and application-specific
   *         properties.
   */
  @Bean
  public ProducerFactory<String, OdeBsmData> odeBsmProducerFactory() {
    return new DefaultKafkaProducerFactory<>(buildProducerProperties(),
        new StringSerializer(), new MessagingSerializer<>());
  }

  /**
   * Creates a KafkaTemplate specifically configured for handling messages with
   * String keys and
   * OdeBsmData values. This KafkaTemplate incorporates a
   * {@link InterceptingKafkaTemplate} to block
   * messages being sent to disabled topics and a LoggingProducerListener for
   * logging producer
   * activity.
   *
   * @param producerFactory the ProducerFactory used to create Kafka producers for
   *                        sending messages
   *                        with String keys and OdeBsmData values. Configures the
   *                        necessary
   *                        serializers and properties.
   * @return a KafkaTemplate instance configured for publishing messages to Kafka
   *         topics with String
   *         keys and OdeBsmData values, ensuring proper handling of disabled
   *         topics and logging.
   */
  @Bean
  public KafkaTemplate<String, OdeBsmData> odeBsmKafkaTemplate(
      ProducerFactory<String, OdeBsmData> producerFactory, ObjectMapper objectMapper) {
    var template = new InterceptingKafkaTemplate<>(producerFactory,
        this.odeKafkaProperties.getDisabledTopics(), meterRegistry, objectMapper);
    template.setProducerListener(new LoggingProducerListener<>());
    return template;
  }

  private Map<String, Object> buildProducerProperties() {
    var producerProps = kafkaProperties.buildProducerProperties();
    if ("CONFLUENT".equals(this.odeKafkaProperties.getKafkaType())) {
      producerProps.putAll(this.odeKafkaProperties.getConfluent().buildConfluentProperties());
    }
    // linger.ms isn't present in the KafkaProperties object above, but it is
    // important to limit the amount of time
    // we wait before publishing messages via the KafkaTemplate producer while the
    // data size of the batch is less than the
    // batch-size set in the application.yaml. The default is (2^31)-1 millis, which
    // is not suitable for our use case.
    producerProps.put(ProducerConfig.LINGER_MS_CONFIG, odeKafkaProperties.getProducer().getLingerMs());
    producerProps.put(ProducerConfig.RETRIES_CONFIG, odeKafkaProperties.getProducer().getRetries());
    return producerProps;
  }
}
