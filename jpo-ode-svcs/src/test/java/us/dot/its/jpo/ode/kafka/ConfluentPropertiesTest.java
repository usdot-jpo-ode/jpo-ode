package us.dot.its.jpo.ode.kafka;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class ConfluentPropertiesTest {

  @Test
  void buildConfluentProperties() {
    ConfluentProperties confluentProperties = new ConfluentProperties();
    confluentProperties.setPassword("password123");
    confluentProperties.setUsername("username322");

    Map<String, Object> expected = new HashMap<>();
    expected.put("ssl.endpoint.identification.algorithm", "https");
    expected.put("security.protocol", "SASL_SSL");
    expected.put("sasl.mechanism", "PLAIN");
    expected.put("sasl.jaas.config",
        "org.apache.kafka.common.security.plain.PlainLoginModule "
            + "required username=\"username322\" password=\"password123\";");

    assertEquals(expected, confluentProperties.buildConfluentProperties());
  }
}