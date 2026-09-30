package us.dot.its.jpo.ode.codec.ffmlib;

import j2735ffm.MessageFrameCodec;
import java.nio.file.Path;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/** Creates the native codec when FFM mode is selected. */
@Configuration
public class Asn1CodecConfiguration {

  /** Creates the codec from the configured FFMLib library. */
  @Bean
  @Primary
  @ConditionalOnProperty(name = "ode.asn1.codec-mode", havingValue = "ffm")
  public MessageFrameCodec messageFrameCodec(FfmlibProperties properties) {
    Path nativeLibrary = FfmlibNativeLibraryLoader.resolve(properties.getNativeLibraryPath());
    return new MessageFrameCodec(
        properties.getTextBufferSize(),
        properties.getUperBufferSize(),
        properties.getErrorBufferSize(),
        nativeLibrary);
  }
}
