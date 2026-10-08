package us.dot.its.jpo.ode.codec.ffmlib;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import j2735ffm.AsnEncoding;
import j2735ffm.ConvertException;
import j2735ffm.MessageFrameCodec;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
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
    Path nativeLibrary = FfmlibNativeTestSupport.requireLibraryOrSkip();

    FfmlibProperties properties = new FfmlibProperties();
    properties.setNativeLibraryPath(nativeLibrary.toString());

    MessageFrameCodec messageFrameCodec = new MessageFrameCodec(
        properties.getTextBufferSize(),
        properties.getUperBufferSize(),
        properties.getErrorBufferSize(),
        Path.of(properties.getNativeLibraryPath()));
    FfmlibMessageFrameCodec codec = new FfmlibMessageFrameCodec(
        messageFrameCodec, new SimpleMeterRegistry());
    IntermediateDecodeResult result = codec.uperToIntermediate(HexUtils.fromHexString(BSM_HEX));
    byte[] fullBufferOutput = messageFrameCodec.convertGeneral(HexUtils.fromHexString(BSM_HEX),
        "MessageFrame", AsnEncoding.UPER, AsnEncoding.JER);
    org.junit.jupiter.api.Assertions.assertArrayEquals(fullBufferOutput, result.bytes());

    assertNotNull(result);
    assertNotNull(result.bytes());
    assertFalse(result.bytes().length == 0);
    String jer = new String(result.bytes(), java.nio.charset.StandardCharsets.UTF_8);
    assertTrue(jer.contains("BasicSafetyMessage"), () -> "Unexpected JER output: " + jer);

    String xer = messageFrameCodec.uperToXer(HexUtils.fromHexString(BSM_HEX));
    org.junit.jupiter.api.Assertions.assertArrayEquals(HexUtils.fromHexString(BSM_HEX),
        codec.xerToUper(xer), "XER encoding must remain compatible with FFMLib");
  }

  @Test
  void nativeCodecRejectsUnknownHashAlgorithmAndRecoversForValidBsm() {
    Path nativeLibrary = FfmlibNativeTestSupport.requireLibraryOrSkip();
    FfmlibProperties properties = new FfmlibProperties();
    properties.setNativeLibraryPath(nativeLibrary.toString());
    MessageFrameCodec messageFrameCodec = new MessageFrameCodec(
        properties.getTextBufferSize(),
        properties.getUperBufferSize(),
        properties.getErrorBufferSize(),
        Path.of(properties.getNativeLibraryPath()));
    FfmlibMessageFrameCodec codec = new FfmlibMessageFrameCodec(
        messageFrameCodec, new SimpleMeterRegistry());

    // OER accepts this unknown ENUMERATED value, but XER encoding it must fail safely.
    RuntimeException failure = assertThrows(RuntimeException.class,
        () -> codec.convert(new byte[] {0x7f}, "HashAlgorithm", AsnEncoding.OER, AsnEncoding.XER));
    ConvertException nativeFailure = assertInstanceOf(ConvertException.class, failure.getCause());
    assertNotNull(nativeFailure.getMessage());
    assertTrue(nativeFailure.getMessage().contains("Error encoding"),
        () -> "Expected a native encoding error, got: " + nativeFailure.getMessage());

    byte[] validBsmUper = HexUtils.fromHexString(BSM_HEX);
    IntermediateDecodeResult recovered = codec.uperToIntermediate(validBsmUper);
    String jer = new String(recovered.bytes(), java.nio.charset.StandardCharsets.UTF_8);
    assertTrue(jer.contains("BasicSafetyMessage"));
    String xer = codec.uperToXer(validBsmUper);
    org.junit.jupiter.api.Assertions.assertArrayEquals(validBsmUper, codec.xerToUper(xer));
  }

  @Test
  void sharedNativeCodecSupportsConcurrentConversionsAndRecoversAfterFailures() throws Exception {
    Path nativeLibrary = FfmlibNativeTestSupport.requireLibraryOrSkip();

    FfmlibProperties properties = new FfmlibProperties();
    properties.setNativeLibraryPath(nativeLibrary.toString());
    MessageFrameCodec messageFrameCodec = new MessageFrameCodec(
        properties.getTextBufferSize(),
        properties.getUperBufferSize(),
        properties.getErrorBufferSize(),
        Path.of(properties.getNativeLibraryPath()));
    FfmlibMessageFrameCodec codec = new FfmlibMessageFrameCodec(
        messageFrameCodec, new SimpleMeterRegistry());

    byte[] validBsmUper = HexUtils.fromHexString(BSM_HEX);
    byte[] expectedJer = codec.convert(validBsmUper, "MessageFrame",
        AsnEncoding.UPER, AsnEncoding.JER).clone();
    String expectedXer = codec.uperToXer(validBsmUper);

    int threadCount = 8;
    int iterationsPerThread = 50;
    ExecutorService executor = Executors.newFixedThreadPool(threadCount);
    CountDownLatch ready = new CountDownLatch(threadCount);
    CountDownLatch start = new CountDownLatch(1);
    List<Future<?>> workers = new ArrayList<>(threadCount);
    long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(30);
    try {
      for (int thread = 0; thread < threadCount; thread++) {
        workers.add(executor.submit(() -> {
          ready.countDown();
          assertTrue(start.await(remainingNanos(deadline), TimeUnit.NANOSECONDS),
              "Concurrent start was not released before the shared deadline");
          for (int iteration = 0; iteration < iterationsPerThread; iteration++) {
            byte[] jer = codec.convert(validBsmUper, "MessageFrame",
                AsnEncoding.UPER, AsnEncoding.JER);
            assertArrayEquals(expectedJer, jer);

            String xer = codec.uperToXer(validBsmUper);
            byte[] roundTrip = codec.xerToUper(xer);
            assertArrayEquals(validBsmUper, roundTrip,
                "XER-based encoding must preserve the original UPER bytes");

            RuntimeException failure = assertThrows(RuntimeException.class,
                () -> codec.convert(new byte[] {0x7f}, "HashAlgorithm",
                    AsnEncoding.OER, AsnEncoding.XER));
            ConvertException nativeFailure = assertInstanceOf(ConvertException.class,
                failure.getCause());
            assertNotNull(nativeFailure.getMessage());
            assertTrue(nativeFailure.getMessage().contains("Error encoding"),
                () -> "Expected a native encoding error, got: " + nativeFailure.getMessage());

            byte[] validAfterFailure = codec.convert(validBsmUper, "MessageFrame",
                AsnEncoding.UPER, AsnEncoding.JER);
            assertArrayEquals(expectedJer, validAfterFailure);

            // Keep outputs across later native calls to detect reuse that aliases returned arrays.
            assertArrayEquals(expectedJer, jer,
                "A later conversion must not overwrite a returned JER array");
            assertEquals(expectedXer, xer,
                "A later conversion must not alter a returned XER string");
            assertArrayEquals(validBsmUper, roundTrip,
                "A later conversion must not overwrite a returned UPER array");
          }
          return null;
        }));
      }

      assertTrue(ready.await(remainingNanos(deadline), TimeUnit.NANOSECONDS),
          "Workers did not reach the coordinated start in time");
      start.countDown();
      for (Future<?> worker : workers) {
        worker.get(remainingNanos(deadline), TimeUnit.NANOSECONDS);
      }
    } finally {
      start.countDown();
      executor.shutdownNow();
      try {
        executor.awaitTermination(remainingNanos(deadline), TimeUnit.NANOSECONDS);
      } catch (InterruptedException interrupted) {
        Thread.currentThread().interrupt();
      }
    }
  }

  private static long remainingNanos(long deadline) {
    return Math.max(0L, deadline - System.nanoTime());
  }
}
