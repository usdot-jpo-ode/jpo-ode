package us.dot.its.jpo.ode.codec.ffmlib;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import j2735ffm.Asn1Codec;
import j2735ffm.AsnEncoding;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * ODE adapter around the generic, thread-safe native codec.
 *
 * <p>Every conversion records a bounded Micrometer timer tagged by PDU, encoding pair, and
 * outcome. The underlying {@link Asn1Codec} is a singleton and confines FFM memory per call.
 */
@Component
@ConditionalOnProperty(name = "ode.asn1.codec-mode", havingValue = "ffm")
public class FfmlibMessageFrameCodec {

  private static final String MESSAGE_FRAME = "MessageFrame";
  private final Asn1Codec codec;
  private final MeterRegistry meterRegistry;

  public FfmlibMessageFrameCodec(Asn1Codec codec, MeterRegistry meterRegistry) {
    this.codec = codec;
    this.meterRegistry = meterRegistry;
  }

  public byte[] convert(byte[] input, String pdu, AsnEncoding from, AsnEncoding to) {
    long start = System.nanoTime();
    String outcome = "success";
    try {
      return codec.convert(input, pdu, from, to);
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

  public String uperToXer(byte[] uperBytes) {
    return decodeToXer(uperBytes, MESSAGE_FRAME, AsnEncoding.UPER);
  }

  public byte[] xerToUper(String xer) {
    return encodeFromXer(xer, MESSAGE_FRAME, AsnEncoding.UPER);
  }

  private Timer timer(String pdu, AsnEncoding from, AsnEncoding to, String outcome) {
    return Timer.builder("ode.asn1.conversion")
        .description("In-process native ASN.1 conversion latency")
        .tag("pdu", pdu)
        .tag("from", from.name())
        .tag("to", to.name())
        .tag("outcome", outcome)
        .publishPercentileHistogram(false)
        .register(meterRegistry);
  }

  public enum IntermediateEncoding {
    XER
  }

  public record IntermediateDecodeResult(String text, IntermediateEncoding encoding) {
  }
}
