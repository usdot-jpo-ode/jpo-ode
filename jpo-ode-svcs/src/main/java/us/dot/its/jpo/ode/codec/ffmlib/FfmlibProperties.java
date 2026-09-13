package us.dot.its.jpo.ode.codec.ffmlib;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for the FFMLib in-process ASN.1 codec.
 *
 * <p>Native {@code Asn1Codec} buffer parameters map to:
 * <ul>
 *   <li>{@code textBufferSize} — XER/JER text buffer (needed for both encode and decode)</li>
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
   * If blank, the library is auto-detected from the working directory, target/libs/, or libs/.
   */
  private String nativeLibraryPath = "";

  /**
   * Text buffer size in bytes for XER/JER encode/decode (native {@code textBufferSize}).
   * Default 256 KiB — large MAP/TIM XER output can exceed smaller sizes.
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

  /**
   * Intermediate encoding used after UPER decode before POJO mapping.
   * {@code jer} is preferred when the native library reports JER as supported; otherwise XER
   * is used. Set to {@code xer} to force the XER path.
   */
  private String intermediateEncoding = "auto";

}
