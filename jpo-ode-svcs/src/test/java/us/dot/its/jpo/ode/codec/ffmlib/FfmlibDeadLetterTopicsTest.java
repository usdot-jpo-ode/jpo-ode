package us.dot.its.jpo.ode.codec.ffmlib;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class FfmlibDeadLetterTopicsTest {

  @Test
  void replacesStandardTopicPrefix() {
    assertEquals("dlq.OdeRawEncodedBSMJson",
        FfmlibDeadLetterTopics.forRawTopic("topic.OdeRawEncodedBSMJson"));
  }

  @Test
  void preservesCustomTopicNameAfterDlqPrefix() {
    assertEquals("dlq.custom.RawBSM", FfmlibDeadLetterTopics.forRawTopic("custom.RawBSM"));
  }
}
