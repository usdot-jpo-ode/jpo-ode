package us.dot.its.jpo.ode.kafka;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "ode.kafka.confluent")
@Data
public class ConfluentProperties {
    private String username;
    private String password;

    public String getSaslJaasConfig() {
        return "org.apache.kafka.common.security.plain.PlainLoginModule required " +
                "username=\"" + username + "\" " +
                "password=\"" + password + "\";";
    }
}
