package us.dot.its.jpo.ode.kafka;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * Test configuration class for setting up a MeterRegistry bean for testing
 * purposes.
 * This configuration provides a simple in-memory MeterRegistry implementation.
 */
@TestConfiguration
public class TestMetricsConfig {

  @Bean
  public MeterRegistry meterRegistry() {
    return new SimpleMeterRegistry(); // Simple in-memory registry for tests
  }
}
