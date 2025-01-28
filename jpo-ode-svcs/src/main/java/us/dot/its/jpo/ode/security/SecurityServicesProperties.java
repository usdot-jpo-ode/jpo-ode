package us.dot.its.jpo.ode.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Configuration
@ConfigurationProperties(prefix = "ode.security-services")
@Data
@Validated(value = SecurityServicesPropertiesValidator.class)
public class SecurityServicesProperties {
    private String hostIP;
    private String signatureEndpoint;
    private Integer port = -1;
    private Boolean isSdwSigningEnabled = true;
    private Boolean isRsuSigningEnabled = false;

    public String getSignatureEndpoint() {
        if (signatureEndpoint == null || signatureEndpoint.isEmpty()) {
            // if signatureEndpoint is not set, then construct it from hostIP and port
            // to provide a useful default. We can't use the default value in the annotation
            // because it doesn't allow us to reference other properties.
            this.signatureEndpoint = "http://" + hostIP + ":" + port + "/sign";
        }
        return signatureEndpoint;
    }
}
