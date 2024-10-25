package us.dot.its.jpo.ode.kafka;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

@Configuration
@ConfigurationProperties(prefix = "ode.kafka")
@Data
public class OdeKafkaProperties {

    private static final Logger logger = LoggerFactory.getLogger(OdeKafkaProperties.class);

    @Value("${ode.kafka.brokers:localhost:9092}")
    private String brokers;
    @Value("${ode.kafka.producer-type:sync}")
    private String producerType;
    @Value("${ode.kafka.disabled-topics:}")
    private Set<String> disabledTopics;
}