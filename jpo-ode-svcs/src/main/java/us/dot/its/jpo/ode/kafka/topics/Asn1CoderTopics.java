package us.dot.its.jpo.ode.kafka.topics;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("ode.kafka.topics.asn1")
@Data
public class Asn1CoderTopics {
    private String decoderInput;
    private String decoderOutput;
    private String encoderInput;
    private String encoderOutput;
}
