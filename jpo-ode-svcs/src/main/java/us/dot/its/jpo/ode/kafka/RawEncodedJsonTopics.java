package us.dot.its.jpo.ode.kafka;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

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
}
