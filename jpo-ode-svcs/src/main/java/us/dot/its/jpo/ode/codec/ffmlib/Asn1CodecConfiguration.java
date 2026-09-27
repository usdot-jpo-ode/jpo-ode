package us.dot.its.jpo.ode.codec.ffmlib;

import j2735ffm.MessageFrameCodec;
import java.nio.file.Path;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/** Creates full-size and compact native codecs when FFM mode is selected. */
@Configuration
public class Asn1CodecConfiguration {

  /** Creates the full-capacity codec from the configured FFMLib library. */
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

  /** Creates a smaller initial BSM codec; the adapter retries through the full codec if needed. */
  @Bean("ffmlibFastPathMessageFrameCodec")
  @ConditionalOnProperty(name = "ode.asn1.codec-mode", havingValue = "ffm")
  public MessageFrameCodec fastPathMessageFrameCodec(FfmlibProperties properties) {
    Path nativeLibrary = FfmlibNativeLibraryLoader.resolve(properties.getNativeLibraryPath());
    long bufferSize = Math.max(1L, Math.min(properties.getFastPathBufferSize(),
        Math.min(properties.getTextBufferSize(), properties.getUperBufferSize())));
    return new MessageFrameCodec(bufferSize, bufferSize, properties.getErrorBufferSize(),
        nativeLibrary);
  }
}
