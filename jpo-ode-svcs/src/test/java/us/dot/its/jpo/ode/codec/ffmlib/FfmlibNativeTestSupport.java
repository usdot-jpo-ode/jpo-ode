package us.dot.its.jpo.ode.codec.ffmlib;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.nio.file.Path;

/** Shared optional-library guard for tests that exercise the native FFMLib. */
final class FfmlibNativeTestSupport {

  private FfmlibNativeTestSupport() {
  }

  static Path requireLibraryOrSkip() {
    Path library = null;
    String missingMessage = "FFMLib native library not present under target/libs";
    try {
      library = FfmlibNativeLibraryLoader.resolve("");
    } catch (IllegalStateException missing) {
      missingMessage = missing.getMessage();
    }

    if (Boolean.getBoolean("ffmlib.smoke.required")) {
      assertNotNull(library, "Required " + missingMessage);
    }
    assumeTrue(library != null, missingMessage);
    return library;
  }
}
