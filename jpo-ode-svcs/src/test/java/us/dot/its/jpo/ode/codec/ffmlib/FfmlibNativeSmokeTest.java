package us.dot.its.jpo.ode.codec.ffmlib;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import j2735ffm.MessageFrameCodec;
import j2735ffm.AsnEncoding;
import java.nio.file.Path;
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
      "001480b8494c4c950cd8cde6e9651116579f22a424dd78fffff00761e4fd7eb7"
      + "d07f7fff80005f11d1020214c1c0ffc7c016aff4017a0ff65403b0fd204c20ff"
      + "ccc04f8fe40c420ffe6404cefe60e9a10133408fcfde1438103ab4138f00e1ee"
      + "c1048ec160103e237410445c171104e26bc103dc4154305c2c84103b1c1c8f0a"
      + "82f42103f34262d1123198103dac25fb12034ce10381c259f12038ca10357425"
      + "1b10e3b2210324c23ad0f23d8efffe0000209340d10000004264bf00";

  @Test
  void nativeCodecLoadsAndDecodesBsmUper() {
    Path nativeLibrary = nativeLibraryOrNull();
    if (Boolean.getBoolean("ffmlib.smoke.required")) {
      assertNotNull(nativeLibrary,
          "Required FFMLib native library not present under target/libs");
    }
    assumeTrue(nativeLibrary != null,
        "FFMLib native library not present under target/libs");

    FfmlibProperties properties = new FfmlibProperties();
    properties.setNativeLibraryPath(nativeLibrary.toString());

    MessageFrameCodec messageFrameCodec = new MessageFrameCodec(
        properties.getTextBufferSize(),
        properties.getUperBufferSize(),
        properties.getErrorBufferSize(),
        Path.of(properties.getNativeLibraryPath()));
    MessageFrameCodec fastBsmCodec = new MessageFrameCodec(properties.getFastPathBufferSize(),
        properties.getFastPathBufferSize(), properties.getErrorBufferSize(),
        Path.of(properties.getNativeLibraryPath()));
    FfmlibMessageFrameCodec codec = new FfmlibMessageFrameCodec(
        messageFrameCodec, fastBsmCodec, new SimpleMeterRegistry());
    IntermediateDecodeResult result = codec.uperToIntermediate(
        HexUtils.fromHexString(BSM_HEX), us.dot.its.jpo.ode.uper.SupportedMessageType.BSM);
    byte[] fullBufferOutput = messageFrameCodec.convertGeneral(HexUtils.fromHexString(BSM_HEX),
        "MessageFrame", AsnEncoding.UPER, AsnEncoding.JER);
    org.junit.jupiter.api.Assertions.assertArrayEquals(fullBufferOutput, result.bytes());

    MessageFrameCodec undersizedFastCodec = new MessageFrameCodec(64, 64,
        properties.getErrorBufferSize(), Path.of(properties.getNativeLibraryPath()));
    FfmlibMessageFrameCodec fallbackCodec = new FfmlibMessageFrameCodec(messageFrameCodec,
        undersizedFastCodec, new SimpleMeterRegistry());
    IntermediateDecodeResult fallback = fallbackCodec.uperToIntermediate(
        HexUtils.fromHexString(BSM_HEX), us.dot.its.jpo.ode.uper.SupportedMessageType.BSM);
    org.junit.jupiter.api.Assertions.assertArrayEquals(fullBufferOutput, fallback.bytes());

    assertNotNull(result);
    assertNotNull(result.bytes());
    assertFalse(result.bytes().length == 0);
    String jer = new String(result.bytes(), java.nio.charset.StandardCharsets.UTF_8);
    assertTrue(jer.contains("BasicSafetyMessage"), () -> "Unexpected JER output: " + jer);

    String xer = messageFrameCodec.uperToXer(HexUtils.fromHexString(BSM_HEX));
    org.junit.jupiter.api.Assertions.assertArrayEquals(HexUtils.fromHexString(BSM_HEX),
        codec.xerToUper(xer), "XER encoding must remain compatible with beta2");
  }

  private static Path nativeLibraryOrNull() {
    try {
      return FfmlibNativeLibraryLoader.resolve("");
    } catch (IllegalStateException missing) {
      return null;
    }
  }
}
