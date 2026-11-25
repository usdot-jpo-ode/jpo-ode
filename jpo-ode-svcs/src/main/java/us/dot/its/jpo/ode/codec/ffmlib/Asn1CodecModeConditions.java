package us.dot.its.jpo.ode.codec.ffmlib;

import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import us.dot.its.jpo.ode.codec.ffmlib.Asn1CodecModeProperties.CodecMode;

/**
 * Bean conditions on {@code ode.asn1.codec-mode}, bound with the same rules as
 * {@link Asn1CodecModeProperties}. An unset or empty value therefore selects external mode for
 * these conditions exactly as it does for the properties, so a blank setting cannot leave both
 * the external routers and the FFM listeners disabled.
 */
public final class Asn1CodecModeConditions {

  private Asn1CodecModeConditions() {
  }

  /** Matches external mode, the ODE default. */
  public static final class External implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
      return codecMode(context) == CodecMode.external;
    }
  }

  /** Binds the configured codec mode, defaulting to external when it is unset or empty. */
  static CodecMode codecMode(ConditionContext context) {
    return Binder.get(context.getEnvironment()).bind("ode.asn1.codec-mode", CodecMode.class)
        .orElse(CodecMode.external);
  }
}
