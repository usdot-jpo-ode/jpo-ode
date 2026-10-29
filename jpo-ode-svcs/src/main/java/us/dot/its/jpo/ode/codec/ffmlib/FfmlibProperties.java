package us.dot.its.jpo.ode.codec.ffmlib;

import jakarta.annotation.PostConstruct;
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

  /** Selects how FFM mode routes UDP datagrams to the in-process decoder. */
  public enum UdpRouting {
    /**
     * Publish each datagram to its raw encoded topic, then decode from Kafka consumers that
     * confirm output before committing input (at-least-once).
     */
    RAW_TOPIC,
    /**
     * Decode on the UDP receiver thread and publish decoded JSON without waiting for
     * confirmation (at-most-once). Raw-topic publication is controlled by {@code rawPublish}.
     */
    DIRECT
  }

  /** UDP routing in FFM mode: {@code raw-topic} (default) or {@code direct}. */
  private UdpRouting udpRouting = UdpRouting.RAW_TOPIC;

  /**
   * Whether direct routing also publishes raw encoded JSON for downstream consumers. Raw-topic
   * routing always publishes raw records because its decoder reads them.
   */
  private boolean rawPublish = true;

  /** Whether FFM input offsets use synchronous commits instead of async callbacks. */
  private boolean syncCommits;

  /** Strategy for null-key FFM raw records; keyed records retain Kafka's default hash mapping. */
  private String rawPartitionStrategy = "round_robin";

  /**
   * Number of Kafka consumers created for each FFM message type. Consumers beyond the raw topic's
   * partition count stay idle.
   */
  private int listenerConcurrency = 1;

  /** Maximum time to wait for every FFM consumer to join its group during startup. */
  private Duration startupTimeout = Duration.ofSeconds(120);

  /**
   * Consecutive raw records that fail with operational (non-input) decode errors before the
   * listener stops. Earlier failing records are quarantined so one record cannot block its type.
   */
  private int infrastructureHaltThreshold = 3;

  /** Whether UDP datagrams are decoded on the receiver thread. */
  public boolean isDirectUdpRouting() {
    return udpRouting == UdpRouting.DIRECT;
  }

  /** Whether UDP datagrams are published to their raw encoded topics. */
  public boolean publishesRawUdp() {
    return !isDirectUdpRouting() || rawPublish;
  }

  /**
   * Rejects settings that would leave UDP traffic undecoded.
   *
   * @throws IllegalStateException when raw-topic routing is combined with raw publishing disabled
   */
  @PostConstruct
  public void validate() {
    if (udpRouting == UdpRouting.RAW_TOPIC && !rawPublish) {
      throw new IllegalStateException("ode.ffmlib.raw-publish=false requires "
          + "ode.ffmlib.udp-routing=direct; raw-topic routing decodes from the raw topics");
    }
  }

}
