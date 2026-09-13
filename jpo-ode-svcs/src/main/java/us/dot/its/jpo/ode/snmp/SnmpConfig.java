package us.dot.its.jpo.ode.snmp;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring configuration exposing SNMP-related beans so that
 * {@link SnmpSession} construction can be injected and overridden in tests.
 */
@Configuration
public class SnmpConfig {

  @Bean
  public SnmpSessionFactory snmpSessionFactory() {
    return SnmpSession::new;
  }
}
