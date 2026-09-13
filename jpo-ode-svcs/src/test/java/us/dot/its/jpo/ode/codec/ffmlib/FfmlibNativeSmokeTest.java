package us.dot.its.jpo.ode.codec.ffmlib;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import java.nio.file.Files;
import java.nio.file.Path;
import j2735ffm.Asn1Codec;
import org.apache.tomcat.util.buf.HexUtils;
import org.junit.jupiter.api.Test;
import us.dot.its.jpo.ode.codec.ffmlib.FfmlibMessageFrameCodec.IntermediateDecodeResult;

/**
 * Smoke-tests real FFMLib native load + UPER decode on the current JVM (JDK 25+).
 * Skips when the platform-classified native library was not copied into the build directory.
 */
class FfmlibNativeSmokeTest {

  // Same payload used by BsmReceiverTest (valid J2735 BSM MessageFrame UPER hex).
  private static final String BSM_HEX =
      "001480b8494c4c950cd8cde6e9651116579f22a424dd78fffff00761e4fd7eb7d07f7fff80005f11d1020214c1c0ffc7c016aff4017a0ff65403b0fd204c20ffccc04f8fe40c420ffe6404cefe60e9a10133408fcfde1438103ab4138f00e1eec1048ec160103e237410445c171104e26bc103dc4154305c2c84103b1c1c8f0a82f42103f34262d1123198103dac25fb12034ce10381c259f12038ca103574251b10e3b2210324c23ad0f23d8efffe0000209340d10000004264bf00";

  @Test
  void nativeCodecLoadsAndDecodesBsmUper() {
    Path so = Path.of("target", "libs", "libasnapplication.so");
    assumeTrue(Files.exists(so),
        "FFMLib native library not present under target/libs");

    FfmlibProperties properties = new FfmlibProperties();
    properties.setNativeLibraryPath(
        so.toAbsolutePath().toString());
    properties.setIntermediateEncoding("xer");

    Asn1Codec asn1Codec = new Asn1Codec(
        properties.getTextBufferSize(),
        properties.getUperBufferSize(),
        properties.getErrorBufferSize(),
        Path.of(properties.getNativeLibraryPath()));
    FfmlibMessageFrameCodec codec = new FfmlibMessageFrameCodec(
        asn1Codec, new SimpleMeterRegistry());
    IntermediateDecodeResult result = codec.uperToIntermediate(HexUtils.fromHexString(BSM_HEX));

    assertNotNull(result);
    assertNotNull(result.text());
    assertFalse(result.text().isBlank());
    assertTrue(
        result.text().contains("MessageFrame") || result.text().contains("basicSafetyMessage"),
        () -> "Unexpected decode output: " + result.text().substring(0, Math.min(200, result.text().length())));
  }
}
