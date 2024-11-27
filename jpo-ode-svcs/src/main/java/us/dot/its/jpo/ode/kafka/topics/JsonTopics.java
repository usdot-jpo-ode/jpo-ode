package us.dot.its.jpo.ode.kafka.topics;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "ode.kafka.topics.json")
@Data
public class JsonTopics {
    private String bsm;
    private String map;
    private String psm;
    private String spat;
    private String srm;
    private String ssm;
    private String tim;

    private String driverAlert;

    private String timBroadcast;

    private String bsmFiltered;
    private String spatFiltered;
    private String timFiltered;
    private String timTmcFiltered;

    private String timCertExpiration;

    private String dnMessage;

    private String rxTim;
    private String rxSpat;

    private String j2735TimBroadcast;
}
