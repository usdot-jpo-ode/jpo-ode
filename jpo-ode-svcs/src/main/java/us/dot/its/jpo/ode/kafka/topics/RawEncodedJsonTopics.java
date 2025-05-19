package us.dot.its.jpo.ode.kafka.topics;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for raw encoded JSON Kafka topics. Prefix: ode.kafka.topics.raw-encoded-json
 */
@Configuration
@ConfigurationProperties(prefix = "ode.kafka.topics.raw-encoded-json")
@Data
public class RawEncodedJsonTopics {
  private String bsm;
  private String map;
  private String psm;
  private String spat;
  private String srm;
  private String ssm;
  private String tim;
  private String sdsm;
}
