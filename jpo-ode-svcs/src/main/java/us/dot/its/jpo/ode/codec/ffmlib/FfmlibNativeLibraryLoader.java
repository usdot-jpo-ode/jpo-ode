package us.dot.its.jpo.ode.codec.ffmlib;

import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/** Resolves the platform native library unpacked from the USDOT FFMLib artifact. */
final class FfmlibNativeLibraryLoader {

  private FfmlibNativeLibraryLoader() {
  }

  static Path resolve(String configuredPath) {
    String libraryName = libraryName();
    List<Path> candidates = new ArrayList<>();
    if (configuredPath != null && !configuredPath.isBlank()) {
      Path configured = Path.of(configuredPath);
      candidates.add(Files.isDirectory(configured) ? configured.resolve(libraryName) : configured);
    } else {
      String classifier = classifier();
      addBuildOutputCandidate(candidates, libraryName);
      candidates.add(Path.of("target", "ffmlib-native", "native", classifier, libraryName));
      candidates.add(Path.of("target", "libs", libraryName));
      candidates.add(Path.of("jpo-ode-svcs", "target", "libs", libraryName));
      candidates.add(Path.of("libs", libraryName));
      candidates.add(Path.of("/home", "libs", libraryName));
    }

    for (Path candidate : candidates) {
      if (Files.isRegularFile(candidate)) {
        return candidate.toAbsolutePath().normalize();
      }
    }

    StringBuilder details = new StringBuilder("FFMLib native library was not found. Checked:");
    for (Path candidate : candidates) {
      details.append(System.lineSeparator()).append("  ").append(candidate.toAbsolutePath());
    }
    throw new IllegalStateException(details.toString());
  }

  /**
   * Maven copies the native library to this module's {@code target/libs}. An IDE launch from the
   * repository root does not see that relative path, so also look beside the directory that
   * contains this class.
   */
  private static void addBuildOutputCandidate(List<Path> candidates, String libraryName) {
    try {
      var codeSource = FfmlibNativeLibraryLoader.class.getProtectionDomain().getCodeSource();
      if (codeSource == null || codeSource.getLocation() == null) {
        return;
      }
      Path location = Path.of(codeSource.getLocation().toURI());
      Path buildDirectory = location.getParent();
      if (buildDirectory != null) {
        candidates.add(buildDirectory.resolve("libs").resolve(libraryName));
      }
    } catch (URISyntaxException | IllegalArgumentException ignored) {
      // Working-directory candidates still apply.
    }
  }

  private static String libraryName() {
    String os = System.getProperty("os.name", "").toLowerCase();
    if (os.contains("win")) {
      return "asnapplication.dll";
    }
    if (os.contains("mac") || os.contains("darwin")) {
      return "libasnapplication.dylib";
    }
    return "libasnapplication.so";
  }

  private static String classifier() {
    String os = System.getProperty("os.name", "").toLowerCase();
    String architecture = System.getProperty("os.arch", "").toLowerCase();
    String normalizedArchitecture = architecture.contains("aarch64") || architecture.contains("arm64")
        ? "aarch64"
        : "x86_64";
    return (os.contains("win") ? "windows-" : os.contains("mac") ? "macos-" : "linux-")
        + normalizedArchitecture;
  }
}
