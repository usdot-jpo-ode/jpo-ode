package us.dot.its.jpo.ode.codec.ffmlib;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import us.dot.its.jpo.ode.kafka.listeners.json.RawEncodedBSMJsonRouter;
import us.dot.its.jpo.ode.kafka.listeners.json.RawEncodedMAPJsonRouter;
import us.dot.its.jpo.ode.kafka.listeners.json.RawEncodedPSMJsonRouter;
import us.dot.its.jpo.ode.kafka.listeners.json.RawEncodedRSMJsonRouter;
import us.dot.its.jpo.ode.kafka.listeners.json.RawEncodedRTCMJsonRouter;
import us.dot.its.jpo.ode.kafka.listeners.json.RawEncodedSDSMJsonRouter;
import us.dot.its.jpo.ode.kafka.listeners.json.RawEncodedSPATJsonRouter;
import us.dot.its.jpo.ode.kafka.listeners.json.RawEncodedSRMJsonRouter;
import us.dot.its.jpo.ode.kafka.listeners.json.RawEncodedSSMJsonRouter;
import us.dot.its.jpo.ode.kafka.listeners.json.RawEncodedTIMJsonRouter;

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

  @Test
  void blankCodecModeSelectsExternalForTheRoutersAndTheProperties() {
    runner.withPropertyValues("ode.asn1.codec-mode=").run(context -> {
      assertThat(context).hasBean("externalRoute");
      assertThat(context).doesNotHaveBean("rawTopicRoute");
      assertThat(context).doesNotHaveBean("directRoute");
      assertFalse(context.getBean(Asn1CodecModeProperties.class).isFfm());
    });
  }

  @Test
  void externalRoutersFollowTheBoundCodecMode() {
    runner.run(context -> assertThat(context).hasBean("externalRoute"));
    runner.withPropertyValues("ode.asn1.codec-mode=EXTERNAL")
        .run(context -> assertThat(context).hasBean("externalRoute"));
    runner.withPropertyValues("ode.asn1.codec-mode=ffm")
        .run(context -> assertThat(context).doesNotHaveBean("externalRoute"));
  }

  @Test
  void externalRoutersUseTheCodecModeCondition() {
    for (Class<?> router : new Class<?>[] {RawEncodedBSMJsonRouter.class,
        RawEncodedMAPJsonRouter.class, RawEncodedPSMJsonRouter.class,
        RawEncodedRSMJsonRouter.class, RawEncodedRTCMJsonRouter.class,
        RawEncodedSDSMJsonRouter.class, RawEncodedSPATJsonRouter.class,
        RawEncodedSRMJsonRouter.class, RawEncodedSSMJsonRouter.class,
        RawEncodedTIMJsonRouter.class}) {
      assertEquals(Asn1CodecModeConditions.External.class,
          router.getAnnotation(Conditional.class).value()[0], router.getSimpleName());
    }
  }

  @Configuration(proxyBeanMethods = false)
  @EnableConfigurationProperties(Asn1CodecModeProperties.class)
  static class RoutedBeans {
    @Bean
    @Conditional(Asn1CodecModeConditions.External.class)
    String externalRoute() {
      return "external";
    }

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
