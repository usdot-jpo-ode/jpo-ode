package us.dot.its.jpo.ode.config;

import io.micrometer.core.instrument.MeterRegistry;
import java.net.InetAddress;
import java.net.UnknownHostException;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for setting up metrics with common tags.
 */
@Configuration
public class MetricsConfig {

  /**
   * Bean for customizing the MeterRegistry with common tags.
   *
   * @return a MeterRegistryCustomizer that adds common tags to the MeterRegistry
   */
  @Bean
  public MeterRegistryCustomizer<MeterRegistry> metricsCommonTags() {
    return registry -> registry.config()
        .commonTags(
            "host", getHostName()
        );
  }

  private String getHostName() {
    try {
      // Get hostname from environment variable if running in Kubernetes
      String hostFromEnv = System.getenv("HOSTNAME");
      if (hostFromEnv != null && !hostFromEnv.isEmpty()) {
        return hostFromEnv;
      }
      // Fallback to system hostname for local deployments in Docker
      return InetAddress.getLocalHost().getHostName();
    } catch (UnknownHostException e) {
      return "unknown";
    }
  }
}