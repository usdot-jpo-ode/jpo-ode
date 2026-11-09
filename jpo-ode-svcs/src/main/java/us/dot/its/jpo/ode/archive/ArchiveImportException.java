package us.dot.its.jpo.ode.archive;

/**
 * Thrown when an archived record or file cannot be imported.
 */
public class ArchiveImportException extends Exception {

  public ArchiveImportException(String message) {
    super(message);
  }

  public ArchiveImportException(String message, Throwable cause) {
    super(message, cause);
  }
}
