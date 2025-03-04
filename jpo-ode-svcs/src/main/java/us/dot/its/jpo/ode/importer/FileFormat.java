package us.dot.its.jpo.ode.importer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;

/**
 * Represents the file type based on its format or compression method.
 * Used for determining how to process or handle a specific file.
 */
public enum FileFormat {
  GZIP,
  ZIP,
  UNKNOWN;

  private static final Pattern gZipPattern = Pattern.compile("application/.*gzip");
  private static final Pattern zipPattern = Pattern.compile("application/.*zip.*");

  /**
   * Detects the format of the given file based on its content type and file extension.
   * The method attempts to identify whether the file is a GZIP, ZIP, or falls into the UNKNOWN category.
   *
   * @param filePath the path of the file to analyze for format detection
   *
   * @return the detected {@link FileFormat}, which can be GZIP, ZIP, or UNKNOWN
   *
   * @throws IOException if an I/O error occurs while probing the file's content type
   */
  public static FileFormat detectFileFormat(Path filePath) throws IOException {
    String probeContentType = Files.probeContentType(filePath);
    if (probeContentType != null) {
      if (gZipPattern.matcher(probeContentType).matches() || filePath.toString().toLowerCase().endsWith("gz")) {
        return FileFormat.GZIP;
      } else if (zipPattern.matcher(probeContentType).matches() || filePath.toString().endsWith("zip")) {
        return FileFormat.ZIP;
      }
    }
    return FileFormat.UNKNOWN;
  }
}
