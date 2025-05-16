package us.dot.its.jpo.ode.importer;

/**
 * Enum representing the types of files that can be imported by the system.
 * This classification is used to determine the appropriate handling or processing
 * logic for different file formats or types encountered during import operations.
 *
 * <ul>
 * <li>LOG_FILE: Represents a standard log file.</li>
 * <li>UNKNOWN: Represents an unrecognized or unsupported file type.</li>
 * </ul>
 */
public enum ImporterFileType {
  LOG_FILE, UNKNOWN
}
