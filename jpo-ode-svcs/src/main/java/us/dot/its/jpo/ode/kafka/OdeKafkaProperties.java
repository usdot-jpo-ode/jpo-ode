package us.dot.its.jpo.ode.kafka;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

@Configuration
@ConfigurationProperties(prefix = "ode.kafka")
@Data
public class OdeKafkaProperties {
    private String brokers;
    private String producerType;
    private Set<String> disabledTopics;
}