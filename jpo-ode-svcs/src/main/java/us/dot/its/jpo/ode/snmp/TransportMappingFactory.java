package us.dot.its.jpo.ode.snmp;

import java.io.IOException;
import org.snmp4j.TransportMapping;

/**
 * Creates a {@link TransportMapping} for an {@link SnmpSession}. Extracted so tests can substitute
 * a factory that throws on construction without resorting to constructor mocking.
 */
@FunctionalInterface
public interface TransportMappingFactory {
  TransportMapping create() throws IOException;
}
