package us.dot.its.jpo.ode.codec.ffmlib;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;
import us.dot.its.jpo.ode.codec.ffmlib.FfmlibProperties.UdpRouting;

class FfmlibPropertiesTest {

  @Test
  void defaultsToRawTopicRoutingWithRawPublishing() {
    FfmlibProperties properties = new FfmlibProperties();

    assertEquals(UdpRouting.RAW_TOPIC, properties.getUdpRouting());
    assertTrue(properties.publishesRawUdp());
    assertDoesNotThrow(properties::validate);
  }

  @Test
  void rawTopicRoutingRequiresRawPublishing() {
    FfmlibProperties properties = new FfmlibProperties();
    properties.setRawPublish(false);

    assertThrows(IllegalStateException.class, properties::validate);
  }

  @Test
  void directRoutingMayDisableRawPublishing() {
    FfmlibProperties properties = bind(Map.of("ode.ffmlib.udp-routing", "direct",
        "ode.ffmlib.raw-publish", "false"));

    assertEquals(UdpRouting.DIRECT, properties.getUdpRouting());
    assertTrue(properties.isDirectUdpRouting());
    assertFalse(properties.publishesRawUdp());
    assertDoesNotThrow(properties::validate);
  }

  @Test
  void bindsRawTopicRoutingFromItsDocumentedValue() {
    assertEquals(UdpRouting.RAW_TOPIC,
        bind(Map.of("ode.ffmlib.udp-routing", "raw-topic")).getUdpRouting());
  }

  private static FfmlibProperties bind(Map<String, String> values) {
    return new Binder(new MapConfigurationPropertySource(values))
        .bind("ode.ffmlib", FfmlibProperties.class).get();
  }
}
