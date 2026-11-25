package us.dot.its.jpo.ode.codec.ffmlib;

/**
 * Kafka did not confirm a decoded or quarantined FFM record. The input stays uncommitted and the
 * listener retries it with backoff instead of stopping, so a broker outage does not need a restart.
 */
final class FfmlibPublicationFailure extends RuntimeException {

  FfmlibPublicationFailure(String message, Throwable cause) {
    super(message, cause);
  }
}
