package us.dot.its.jpo.ode.traveler;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Configuration
@ConfigurationProperties(prefix = "ode.tim-ingest-monitoring")
@Validated(value = TimIngestPropertiesValidator.class)
@Data
public class TimIngestTrackerProperties {
    private boolean trackingEnabled;
    private long interval = 3600; // in seconds. Default is 1 hour (3600 seconds)
}
