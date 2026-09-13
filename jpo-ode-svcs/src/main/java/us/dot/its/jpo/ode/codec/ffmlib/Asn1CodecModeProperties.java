package us.dot.its.jpo.ode.codec.ffmlib;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/** Selects the legacy external ADM/AEM path or the in-process FFM codec path. */
@Configuration
@ConfigurationProperties(prefix = "ode.asn1")
@Data
public class Asn1CodecModeProperties {

  public enum CodecMode {
    external,
    ffm
  }

  private CodecMode codecMode = CodecMode.ffm;

  public boolean isFfm() {
    return codecMode == CodecMode.ffm;
  }
}
