package us.dot.its.jpo.ode.snmp;

import java.io.IOException;
import us.dot.its.jpo.ode.plugin.RoadSideUnit.RSU;

/**
 * Creates an {@link SnmpSession} for a given {@link RSU}. Injected into controllers so tests can
 * substitute a factory that throws on session creation without resorting to constructor mocking.
 */
@FunctionalInterface
public interface SnmpSessionFactory {
  SnmpSession create(RSU rsu) throws IOException;
}
