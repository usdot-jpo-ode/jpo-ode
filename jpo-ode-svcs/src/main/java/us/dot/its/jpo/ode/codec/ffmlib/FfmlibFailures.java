package us.dot.its.jpo.ode.codec.ffmlib;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

/** Failure classification shared by the FFM raw-topic and direct UDP routes. */
final class FfmlibFailures {

  static final String INFRASTRUCTURE = "infrastructure";

  private FfmlibFailures() {
  }

  /** Returns the operational decode failure in a cause chain, or {@code null} for input errors. */
  static FfmlibDecodeService.InfrastructureFailure findInfrastructureFailure(Throwable error) {
    for (Throwable cause : causes(error)) {
      if (cause instanceof FfmlibDecodeService.InfrastructureFailure failure) {
        return failure;
      }
    }
    return null;
  }

  /**
   * Names the dead-letter {@code failure-category} for a rejected input.
   *
   * @param error the parse, decode, or publication failure
   * @param decodedMapping whether the failure happened after the raw input was parsed
   * @param publishFailure whether output publication was exhausted
   * @return the category header value
   */
  static String category(Exception error, boolean decodedMapping, boolean publishFailure) {
    boolean unsupportedSigned = false;
    boolean jsonProcessing = false;
    for (Throwable cause : causes(error)) {
      unsupportedSigned |= cause instanceof UnsupportedOperationException;
      jsonProcessing |= cause instanceof JsonProcessingException;
    }
    if (unsupportedSigned) {
      return "unsupported_signed";
    }
    if (publishFailure) {
      return "publish_failure";
    }
    if (decodedMapping && jsonProcessing) {
      return "mapping_failure";
    }
    return error instanceof JsonProcessingException ? "malformed_json" : "malformed_payload";
  }

  private static Iterable<Throwable> causes(Throwable error) {
    Set<Throwable> visited = Collections.newSetFromMap(new IdentityHashMap<>());
    ArrayList<Throwable> result = new ArrayList<>();
    for (Throwable current = error; current != null && visited.add(current);
        current = current.getCause()) {
      result.add(current);
    }
    return result;
  }
}
