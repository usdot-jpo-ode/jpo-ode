package us.dot.its.jpo.ode.codec.ffmlib;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

class FfmlibUdpRoutingConditionsTest {

  private final ApplicationContextRunner runner = new ApplicationContextRunner()
      .withUserConfiguration(RoutedBeans.class);

  @Test
  void externalModeStartsNeitherFfmRoute() {
    runner.run(context -> {
      assertThat(context).doesNotHaveBean("rawTopicRoute");
      assertThat(context).doesNotHaveBean("directRoute");
    });
  }

  @Test
  void ffmModeDefaultsToRawTopicRouting() {
    runner.withPropertyValues("ode.asn1.codec-mode=ffm").run(context -> {
      assertThat(context).hasBean("rawTopicRoute");
      assertThat(context).doesNotHaveBean("directRoute");
    });
  }

  @Test
  void ffmDirectRoutingReplacesRawTopicConsumers() {
    runner.withPropertyValues("ode.asn1.codec-mode=ffm", "ode.ffmlib.udp-routing=direct")
        .run(context -> {
          assertThat(context).hasBean("directRoute");
          assertThat(context).doesNotHaveBean("rawTopicRoute");
        });
  }

  @Test
  void routingValuesBindWithRelaxedNames() {
    runner.withPropertyValues("ode.asn1.codec-mode=ffm", "ode.ffmlib.udp-routing=RAW_TOPIC")
        .run(context -> assertThat(context).hasBean("rawTopicRoute"));
  }

  @Test
  void rawTopicComponentsUseTheRawTopicCondition() {
    for (Class<?> component : new Class<?>[] {FfmlibRawJsonListener.class,
        FfmlibListenerStartup.class, FfmlibKafkaListenerConfiguration.class}) {
      assertEquals(FfmlibUdpRoutingConditions.RawTopic.class,
          component.getAnnotation(Conditional.class).value()[0], component.getSimpleName());
    }
    for (Class<?> component : new Class<?>[] {FfmlibDirectUdpDecoder.class,
        FfmlibDirectStartup.class}) {
      assertEquals(FfmlibUdpRoutingConditions.Direct.class,
          component.getAnnotation(Conditional.class).value()[0], component.getSimpleName());
    }
  }

  @Configuration(proxyBeanMethods = false)
  static class RoutedBeans {
    @Bean
    @Conditional(FfmlibUdpRoutingConditions.RawTopic.class)
    String rawTopicRoute() {
      return "raw-topic";
    }

    @Bean
    @Conditional(FfmlibUdpRoutingConditions.Direct.class)
    String directRoute() {
      return "direct";
    }
  }
}
