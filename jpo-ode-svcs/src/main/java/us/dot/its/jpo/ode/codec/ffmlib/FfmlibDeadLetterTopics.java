package us.dot.its.jpo.ode.codec.ffmlib;

/** Names FFM quarantine topics with the separate DLQ prefix used by Jikkou. */
final class FfmlibDeadLetterTopics {

  private FfmlibDeadLetterTopics() {
  }

  static String forRawTopic(String rawTopic) {
    String topicName = rawTopic.startsWith("topic.") ? rawTopic.substring("topic.".length())
        : rawTopic;
    return "dlq." + topicName;
  }
}
