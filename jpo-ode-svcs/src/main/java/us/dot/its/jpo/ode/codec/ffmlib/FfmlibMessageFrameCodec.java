package us.dot.its.jpo.ode.codec.ffmlib;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import j2735ffm.AsnEncoding;
import j2735ffm.MessageFrameCodec;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import us.dot.its.jpo.ode.uper.SupportedMessageType;

/**
 * ODE adapter around the generic, thread-safe native codec.
 *
 * <p>Every conversion records a bounded Micrometer timer tagged by PDU, encoding pair, and
 * outcome. The underlying {@link MessageFrameCodec} is a singleton and confines FFM memory per call.
 */
@Component
@ConditionalOnProperty(name = "ode.asn1.codec-mode", havingValue = "ffm")
public class FfmlibMessageFrameCodec {

  private static final String MESSAGE_FRAME = "MessageFrame";
  private final MessageFrameCodec codec;
  private final MessageFrameCodec fastBsmCodec;
  private final MeterRegistry meterRegistry;

  /**
   * Creates the native codec adapter with its full and compact BSM codecs.
   *
   * @param codec full-size native codec for general decoding and fallback
   * @param fastBsmCodec compact codec for common BSM payloads
   * @param meterRegistry registry for conversion timing metrics
   */
  @Autowired
  public FfmlibMessageFrameCodec(MessageFrameCodec codec,
      @Qualifier("ffmlibFastPathMessageFrameCodec") MessageFrameCodec fastBsmCodec,
      MeterRegistry meterRegistry) {
    this.codec = codec;
    this.fastBsmCodec = fastBsmCodec;
    this.meterRegistry = meterRegistry;
  }

  /** Convenience constructor used when native integration tests do not need the BSM fast path. */
  public FfmlibMessageFrameCodec(MessageFrameCodec codec, MeterRegistry meterRegistry) {
    this(codec, codec, meterRegistry);
  }

  /**
   * Converts encoded data between two ASN.1 representations.
   *
   * @param input encoded input bytes
   * @param pdu ASN.1 protocol data unit name
   * @param from source encoding
   * @param to target encoding
   * @return converted bytes
   */
  public byte[] convert(byte[] input, String pdu, AsnEncoding from, AsnEncoding to) {
    long start = System.nanoTime();
    String outcome = "success";
    try {
      return codec.convertGeneral(input, pdu, from, to);
    } catch (RuntimeException error) {
      outcome = "failure";
      throw error;
    } finally {
      timer(pdu, from, to, outcome).record(System.nanoTime() - start, TimeUnit.NANOSECONDS);
    }
  }

  public String decodeToXer(byte[] input, String pdu, AsnEncoding encoding) {
    return new String(convert(input, pdu, encoding, AsnEncoding.XER), StandardCharsets.UTF_8);
  }

  public byte[] encodeFromXer(String xer, String pdu, AsnEncoding encoding) {
    return convert(xer.getBytes(StandardCharsets.UTF_8), pdu, AsnEncoding.XER, encoding);
  }

  public IntermediateDecodeResult uperToIntermediate(byte[] uperBytes) {
    return new IntermediateDecodeResult(
        decodeToXer(uperBytes, MESSAGE_FRAME, AsnEncoding.UPER), IntermediateEncoding.XER);
  }

  /** Uses compact native buffers for small BSMs and retries with configured full buffers on error. */
  public IntermediateDecodeResult uperToIntermediate(byte[] uperBytes,
      SupportedMessageType knownType) {
    if (knownType != SupportedMessageType.BSM || fastBsmCodec == codec) {
      return uperToIntermediate(uperBytes);
    }
    long start = System.nanoTime();
    String outcome = "success";
    try {
      byte[] xerBytes;
      try {
        xerBytes = fastBsmCodec.convertGeneral(uperBytes, MESSAGE_FRAME, AsnEncoding.UPER,
            AsnEncoding.XER);
      } catch (RuntimeException fastPathFailure) {
        try {
          xerBytes = codec.convertGeneral(uperBytes, MESSAGE_FRAME, AsnEncoding.UPER,
              AsnEncoding.XER);
        } catch (RuntimeException fullPathFailure) {
          fullPathFailure.addSuppressed(fastPathFailure);
          throw fullPathFailure;
        }
      }
      return new IntermediateDecodeResult(new String(xerBytes, StandardCharsets.UTF_8),
          IntermediateEncoding.XER);
    } catch (RuntimeException error) {
      outcome = "failure";
      throw error;
    } finally {
      timer(MESSAGE_FRAME, AsnEncoding.UPER, AsnEncoding.XER, outcome)
          .record(System.nanoTime() - start, TimeUnit.NANOSECONDS);
    }
  }

  public String uperToXer(byte[] uperBytes) {
    return decodeToXer(uperBytes, MESSAGE_FRAME, AsnEncoding.UPER);
  }

  public byte[] xerToUper(String xer) {
    return encodeFromXer(xer, MESSAGE_FRAME, AsnEncoding.UPER);
  }

  private Timer timer(String pdu, AsnEncoding from, AsnEncoding to, String outcome) {
    return meterRegistry.timer(
        "ode.asn1.conversion",
        "pdu", pdu,
        "from", from.name(),
        "to", to.name(),
        "outcome", outcome);
  }

  /** Intermediate text encodings produced by the native codec. */
  public enum IntermediateEncoding {
    XER
  }

  /** Native decode result containing intermediate text and its encoding. */
  public record IntermediateDecodeResult(String text, IntermediateEncoding encoding) {
  }
}
