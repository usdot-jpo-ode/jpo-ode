package us.dot.its.jpo.ode.codec.ffmlib;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class FfmlibNativeLibraryLoaderTest {

  @TempDir
  Path tempDirectory;

  @Test
  void buildOutputCandidateSkipsJarCodeSources() {
    assertTrue(FfmlibNativeLibraryLoader.buildOutputLibraryCandidate(
        URI.create("jar:nested:/opt/ode.jar/!BOOT-INF/classes!/"), "libasnapplication.so")
        .isEmpty());
    assertTrue(FfmlibNativeLibraryLoader.buildOutputLibraryCandidate(
        URI.create("jar:file:/opt/ode.jar!/"), "libasnapplication.so").isEmpty());
  }

  @Test
  void buildOutputCandidateUsesParentOfClassesDirectory() throws Exception {
    URI classesUri = FfmlibNativeLibraryLoader.class.getProtectionDomain()
        .getCodeSource().getLocation().toURI();
    Optional<Path> candidate = FfmlibNativeLibraryLoader.buildOutputLibraryCandidate(
        classesUri, "libasnapplication.so");

    assertEquals(Optional.of(Path.of(classesUri).getParent().resolve("libs/libasnapplication.so")),
        candidate);
  }

  @Test
  void buildOutputCandidateSkipsFileCodeSource() throws Exception {
    Path jarFile = Files.createFile(tempDirectory.resolve("ode.jar"));

    assertTrue(FfmlibNativeLibraryLoader.buildOutputLibraryCandidate(
        jarFile.toUri(), "libasnapplication.so").isEmpty());
  }

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
