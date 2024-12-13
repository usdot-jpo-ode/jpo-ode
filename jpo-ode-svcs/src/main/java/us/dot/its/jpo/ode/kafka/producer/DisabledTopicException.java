package us.dot.its.jpo.ode.kafka.producer;

/**
 * DisabledTopicException is a custom exception that extends RuntimeException.
 * It is thrown to indicate that a particular Kafka topic is disabled and cannot
 * be used for publishing messages. This exception is typically used in Kafka
 * producer configurations or interceptors to prevent message delivery to topics
 * that are not allowed or are inactive.
 *
 * </p>The exception takes the name of the disabled topic as a parameter and constructs
 * an informative error message indicating that the topic is disabled.
 *
 */
public final class DisabledTopicException extends RuntimeException {

  public DisabledTopicException(String topic) {
    super(String.format("Blocked attempt to send data to disabled topic %s", topic));
  }
}
