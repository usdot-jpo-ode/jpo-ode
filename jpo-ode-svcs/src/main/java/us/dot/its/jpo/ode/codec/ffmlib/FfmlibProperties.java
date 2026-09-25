package us.dot.its.jpo.ode.codec.ffmlib;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for the FFMLib in-process ASN.1 codec.
 *
 * <p>Native {@code MessageFrameCodec} buffer parameters map to:
 * <ul>
 *   <li>{@code textBufferSize} — XER text buffer (needed for both encode and decode)</li>
 *   <li>{@code uperBufferSize} — UPER binary buffer</li>
 *   <li>{@code errorBufferSize} — native error message buffer</li>
 * </ul>
 */
@Configuration
@ConfigurationProperties(prefix = "ode.ffmlib")
@Data
public class FfmlibProperties {

  /**
   * Explicit path to the native shared library (asnapplication.dll / libasnapplication.so).
   * If blank, the library is resolved beside the compiled classes ({@code target/libs}), then from
   * the working directory and {@code /home/libs}.
   */
  private String nativeLibraryPath = "";

  /**
   * Text buffer size in bytes for XER encode/decode (native {@code textBufferSize}).
   * Default 2 MiB — large MAP/TIM XER output can exceed smaller sizes.
   */
  private long textBufferSize = 2097152L;

  /**
   * UPER binary buffer size in bytes (native {@code uperBufferSize}).
   * Default 256 KiB — accommodates UPER as well as IEEE 1609.2 OER/COER envelopes.
   */
  private long uperBufferSize = 262144L;

  /**
   * Native error buffer size in bytes (native {@code errorBufferSize}).
   */
  private long errorBufferSize = 1024L;

}
