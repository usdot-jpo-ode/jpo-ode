package us.dot.its.jpo.ode.kafka;

import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * A configuration properties class that holds configuration settings for Confluent Kafka
 * integration. This class is designed to be used with Spring Boot's configuration properties
 * mechanism, allowing for easy externalization and injection of property values.
 *
 * <p>This class provides a method to build a map of Confluent-specific Kafka properties, which
 * includes authentication settings necessary for connecting to a Confluent-managed Kafka cluster.
 */
@Configuration
@ConfigurationProperties(prefix = "ode.kafka.confluent")
@Data
public class ConfluentProperties {

  private String username;
  private String password;

  /**
   * Builds and returns a map of configuration properties specific to Confluent Kafka integration.
   * These properties include security configurations necessary for authentication with a
   * Confluent-managed Kafka cluster using SASL_SSL and PLAIN mechanisms.
   *
   * @return a map containing Confluent Kafka properties with security protocol and SASL
   *      configurations including username and password.
   */
  public Map<String, Object> buildConfluentProperties() {
    Map<String, Object> props = new HashMap<>();
    props.put("ssl.endpoint.identification.algorithm", "https");
    props.put("security.protocol", "SASL_SSL");
    props.put("sasl.mechanism", "PLAIN");
    props.put("sasl.jaas.config",
        "org.apache.kafka.common.security.plain.PlainLoginModule required " + "username=\""
            + username + "\" " + "password=\"" + password + "\";");
    return props;
  }
}
