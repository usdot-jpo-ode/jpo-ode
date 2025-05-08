package us.dot.its.jpo.ode.importer;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.spi.FileTypeDetector;

/**
 * A file type detector implementation that identifies the MIME type of a file
 * based on its extension in a system-agnostic manner. Intended for usage in tests only.
 */
public class SystemAgnosticFileTypeDetector extends FileTypeDetector {

  @Override
  public String probeContentType(Path path) throws IOException {
    if (path.toString().endsWith(".gz")) {
      return "application/gzip";
    } else if (path.toString().endsWith(".zip")) {
      return "application/zip";
    } else {
      return null; // Unknown type
    }
  }
}