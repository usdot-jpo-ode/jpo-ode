package us.dot.its.jpo.ode.storage;

/**
 * An enumeration representing the different types of files that can be uploaded.
 * This enum defines the following types:
 * <ul>
 *   <li>{@code BSM} - Represents logs related to Basic Safety Messages.</li>
 *   <li>{@code OBU} - Represents logs related to On-Board Unit operations.</li>
 *   <li>{@code ARCHIVE} - Represents S3 archive files written by the jpo-utils S3 archive
 *       connectors.</li>
 *   <li>{@code UNKNOWN} - Represents an unknown or unrecognized log file type.</li>
 * </ul>
 *
 * <p>The {@code LogFileType} enum provides a utility method to map a string representation
 * of a log file type to the corresponding enum value.</p>
 */
public enum LogFileType {
  BSM,
  OBU,
  ARCHIVE,
  UNKNOWN;

  /**
   * Converts a string representation of a log file type into its corresponding {@link LogFileType} enumeration value.
   *
   * @param type The string representation of the log file type.
   *
   * @return The corresponding {@link LogFileType} enum value.
   */
  public static LogFileType fromString(String type) {
    if ("bsmlog".equalsIgnoreCase(type)) {
      return BSM;
    } else if ("obulog".equalsIgnoreCase(type)) {
      return OBU;
    } else if ("archive".equalsIgnoreCase(type)) {
      return ARCHIVE;
    } else {
      return UNKNOWN;
    }
  }
}
