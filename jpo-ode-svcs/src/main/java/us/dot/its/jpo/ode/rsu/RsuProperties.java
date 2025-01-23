package us.dot.its.jpo.ode.rsu;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "ode.rsu")
@Data
public class RsuProperties {
    private int srmSlots; // number of "store and repeat message" indices for RSU TIMs
    private String username;
    private String password;
}
