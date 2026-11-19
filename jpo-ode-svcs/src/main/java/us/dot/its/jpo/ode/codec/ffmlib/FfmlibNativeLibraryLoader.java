package us.dot.its.jpo.ode.codec.ffmlib;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Resolves the platform native library unpacked from the FFMLib artifact. */
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
      addBuildOutputCandidate(candidates, libraryName);
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
      buildOutputLibraryCandidate(codeSource.getLocation().toURI(), libraryName)
          .ifPresent(candidates::add);
    } catch (URISyntaxException | IllegalArgumentException ignored) {
      // Working-directory candidates still apply.
    }
  }

  /**
   * Returns the native library beside a Maven build output when the code source is a directory on
   * disk. Packaged jars and nested jars do not have a filesystem path that can be used here.
   */
  static Optional<Path> buildOutputLibraryCandidate(URI codeSourceUri, String libraryName) {
    if (!"file".equalsIgnoreCase(codeSourceUri.getScheme())) {
      return Optional.empty();
    }

    try {
      Path location = Path.of(codeSourceUri);
      Path buildDirectory = Files.isDirectory(location) ? location.getParent() : null;
      return buildDirectory == null
          ? Optional.empty()
          : Optional.of(buildDirectory.resolve("libs").resolve(libraryName));
    } catch (IllegalArgumentException ignored) {
      return Optional.empty();
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

}
