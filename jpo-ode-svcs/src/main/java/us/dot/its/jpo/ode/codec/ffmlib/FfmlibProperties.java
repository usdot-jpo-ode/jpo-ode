package us.dot.its.jpo.ode.codec.ffmlib;

import java.time.Duration;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for the FFMLib in-process ASN.1 codec.
 *
 * <p>Native {@code MessageFrameCodec} buffer parameters map to:
 * <ul>
 *   <li>{@code textBufferSize} — XER encode and JER decode text buffer</li>
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
   * Text buffer size in bytes for XER encoding and JER decoding (native
   * {@code textBufferSize}). Default 2 MiB accommodates large MAP/TIM messages.
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

  /** Smaller initial native buffers for the common BSM path; larger payloads use full buffers. */
  private long fastPathBufferSize = 32768L;

  /** Kafka producer linger for FFM raw and decoded records. Zero keeps the latency boundary low. */
  private int producerLingerMs;

  /** Kafka producer compression for FFM raw and decoded records. */
  private String producerCompressionType = "none";

  /** Whether FFM input offsets use synchronous commits instead of async callbacks. */
  private boolean syncCommits;

  /** Strategy for null-key FFM raw records; keyed records retain Kafka's default hash mapping. */
  private String rawPartitionStrategy = "round_robin";

  /** Minimum partition count for each raw and FFM dead-letter topic. */
  private int topicPartitions = 4;

  /** Number of Kafka consumers created for each FFM message type. */
  private int listenerConcurrency = 4;

  /** Maximum time to wait for every FFM consumer to join its group during startup. */
  private Duration startupTimeout = Duration.ofSeconds(120);

  /** Minimum raw-topic retention in milliseconds. */
  private long rawTopicRetentionMs = 86_400_000L;

  /** Minimum FFM dead-letter retention in milliseconds. */
  private long dltRetentionMs = 604_800_000L;

}
