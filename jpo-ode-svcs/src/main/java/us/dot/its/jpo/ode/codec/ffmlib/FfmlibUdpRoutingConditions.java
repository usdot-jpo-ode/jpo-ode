package us.dot.its.jpo.ode.codec.ffmlib;

import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import us.dot.its.jpo.ode.codec.ffmlib.Asn1CodecModeProperties.CodecMode;
import us.dot.its.jpo.ode.codec.ffmlib.FfmlibProperties.UdpRouting;

/**
 * Bean conditions for the FFM UDP routes. Properties are bound with the same relaxed rules as
 * {@link Asn1CodecModeProperties} and {@link FfmlibProperties}, so {@code raw-topic},
 * {@code RAW_TOPIC}, and {@code rawTopic} select the same route.
 */
public final class FfmlibUdpRoutingConditions {

  private FfmlibUdpRoutingConditions() {
  }

  /** Matches FFM mode with durable raw-topic routing, the FFM default. */
  public static final class RawTopic implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
      return matchesRouting(context, UdpRouting.RAW_TOPIC);
    }
  }

  /** Matches FFM mode with decode on the UDP receiver. */
  public static final class Direct implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
      return matchesRouting(context, UdpRouting.DIRECT);
    }
  }

  private static boolean matchesRouting(ConditionContext context, UdpRouting routing) {
    Binder binder = Binder.get(context.getEnvironment());
    CodecMode mode = binder.bind("ode.asn1.codec-mode", CodecMode.class)
        .orElse(CodecMode.external);
    UdpRouting configured = binder.bind("ode.ffmlib.udp-routing", UdpRouting.class)
        .orElse(UdpRouting.RAW_TOPIC);
    return mode == CodecMode.ffm && configured == routing;
  }
}
