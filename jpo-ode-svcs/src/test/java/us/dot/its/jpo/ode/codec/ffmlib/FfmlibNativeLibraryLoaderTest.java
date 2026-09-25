package us.dot.its.jpo.ode.codec.ffmlib;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class FfmlibNativeLibraryLoaderTest {

  @Test
  void resolveFindsLibraryCopiedBesideClasses() {
    Path resolved;
    try {
      resolved = FfmlibNativeLibraryLoader.resolve("");
    } catch (IllegalStateException missing) {
      assumeTrue(false, missing.getMessage());
      return;
    }

    assertTrue(Files.isRegularFile(resolved));
    assertEquals("libs", resolved.getParent().getFileName().toString());
    assertEquals("target", resolved.getParent().getParent().getFileName().toString());
  }
}
