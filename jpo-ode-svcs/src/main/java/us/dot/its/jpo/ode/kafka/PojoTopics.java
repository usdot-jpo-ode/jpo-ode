package us.dot.its.jpo.ode.kafka;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "ode.kafka.topics.pojo")
@Data
public class PojoTopics {
    private String bsm;
    private String spat;
    private String ssm;

    private String timBroadcast;

    private String bsmDuringEvent;

    private String rxBsm;
    private String rxSpat;

    private String txBsm;
    private String txMap;
    private String txPsm;
    private String txSpat;
    private String txSrm;
}
