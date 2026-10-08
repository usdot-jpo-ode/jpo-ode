package us.dot.its.jpo.ode.kafka.topics;

import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for raw encoded JSON Kafka topics.
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
  private String rtcm;
  private String rsm;

  /** Returns all ten configured raw topics in stable J2735 type order. */
  public List<String> allTopics() {
    return List.of(bsm, spat, map, tim, srm, ssm, psm, sdsm, rtcm, rsm);
  }
}
