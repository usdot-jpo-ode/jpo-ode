package us.dot.its.jpo.ode.rsu;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import us.dot.its.jpo.ode.security.SecurityServicesProperties;

/**
 * Configuration class responsible for creating and configuring an instance of RsuDepositor.
 *
 * <p>This class sets up the necessary bean for RsuDepositor, ensuring its lifecycle
 * management is handled by the Spring framework. The RsuDepositor is initialized
 * with the required properties and started to begin its operations.</p>
 */
@Configuration
public class RsuDepositorConfig {

  /**
   * Creates and configures an instance of {@link RsuDepositor} as a Spring-managed bean.
   * The RsuDepositor is initialized with the provided properties and started immediately.
   *
   * @param rsuProperties the configuration properties for RSU, including such values
   *                      as username, password, and the number of "store and repeat" message slots.
   * @param securityServicesProps the configuration properties for security services,
   *                              used to determine whether the ODE should send the message to be signed
   *                              prior to sending it to the RSU
   * @return an initialized and running instance of {@link RsuDepositor}.
   */
  @Bean
  public RsuDepositor rsuDepositor(RsuProperties rsuProperties, SecurityServicesProperties securityServicesProps) {
    var rsuDepositor = new RsuDepositor(rsuProperties, securityServicesProps.getIsRsuSigningEnabled());
    rsuDepositor.start();
    return rsuDepositor;
  }
}
