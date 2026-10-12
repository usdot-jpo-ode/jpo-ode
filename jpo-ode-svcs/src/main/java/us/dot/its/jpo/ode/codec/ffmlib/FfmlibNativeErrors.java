package us.dot.its.jpo.ode.codec.ffmlib;

import j2735ffm.ConvertException;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;

/**
 * Classifies FFMLib native conversion errors. Malformed input is a caller or data error; every
 * other native failure (buffer capacity, output encoding, unknown) is an operational failure.
 *
 * <p>FFMLib reports failures as message text only, so these patterns must be kept in step with
 * the library version.
 */
public final class FfmlibNativeErrors {

  private static final List<String> MALFORMED_INPUT_PREFIXES = List.of(
      "Unrecognized PDU:",
      "Decoding was successful, but constraint check failed, can't re-encode:");
  private static final List<String> MALFORMED_INPUT_SUFFIXES = List.of(
      ": Error decoding PDU");

  private FfmlibNativeErrors() {
  }

  /**
   * Finds the first native conversion exception in a cause chain.
   *
   * @param error the failure to inspect
   * @return the first {@link ConvertException}, or {@code null} when none is present
   */
  public static ConvertException findConvertException(Throwable error) {
    Set<Throwable> visited = Collections.newSetFromMap(new IdentityHashMap<>());
    for (Throwable cause = error; cause != null && visited.add(cause); cause = cause.getCause()) {
      if (cause instanceof ConvertException convertException) {
        return convertException;
      }
    }
    return null;
  }

  /**
   * Reports whether a failure is a native rejection of malformed ASN.1 input.
   *
   * @param error the failure to inspect, including nested causes
   * @return true when the cause chain holds a {@link ConvertException} for malformed input
   */
  public static boolean isMalformedInput(Throwable error) {
    ConvertException nativeError = findConvertException(error);
    return nativeError != null && isMalformedInputMessage(nativeError.getMessage());
  }

  /**
   * Reports whether a native error message describes malformed ASN.1 input.
   *
   * @param message the native error message
   * @return true for decoding and constraint-check failures
   */
  public static boolean isMalformedInputMessage(String message) {
    if (message == null) {
      return false;
    }
    return MALFORMED_INPUT_PREFIXES.stream().anyMatch(message::startsWith)
        || MALFORMED_INPUT_SUFFIXES.stream().anyMatch(message::endsWith);
  }
}
