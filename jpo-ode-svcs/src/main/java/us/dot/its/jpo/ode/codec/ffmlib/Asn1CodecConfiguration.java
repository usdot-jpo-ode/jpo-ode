package us.dot.its.jpo.ode.codec.ffmlib;

import j2735ffm.Asn1Codec;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Creates exactly one native codec when FFM mode is selected. */
@Configuration
public class Asn1CodecConfiguration {

  /** Creates the native codec from the configured FFMLib library. */
  @Bean
  @ConditionalOnProperty(name = "ode.asn1.codec-mode", havingValue = "ffm")
  public Asn1Codec asn1Codec(FfmlibProperties properties) {
    java.nio.file.Path nativeLibrary = FfmlibNativeLibraryLoader.resolve(
        properties.getNativeLibraryPath());
    return new Asn1Codec(
        properties.getTextBufferSize(),
        properties.getUperBufferSize(),
        properties.getErrorBufferSize(),
        nativeLibrary);
  }
}
