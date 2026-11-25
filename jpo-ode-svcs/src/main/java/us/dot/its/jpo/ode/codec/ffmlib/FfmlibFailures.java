package us.dot.its.jpo.ode.codec.ffmlib;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;
import org.apache.kafka.clients.consumer.CommitFailedException;
import org.apache.kafka.common.InvalidRecordException;
import org.apache.kafka.common.errors.RebalanceInProgressException;
import org.apache.kafka.common.errors.RecordBatchTooLargeException;
import org.apache.kafka.common.errors.RecordTooLargeException;
import org.apache.kafka.common.errors.RetriableException;

/** Failure classification shared by the FFM raw-topic and direct UDP routes. */
final class FfmlibFailures {

  static final String INFRASTRUCTURE = "infrastructure";

  /** Dead-letter category for a decoded record that Kafka rejected, such as an oversized one. */
  static final String OUTPUT_REJECTED = "output_rejected";

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
   * @param error the parse or decode failure
   * @param decodedMapping whether the failure happened after the raw input was parsed
   * @return the category header value
   */
  static String category(Exception error, boolean decodedMapping) {
    boolean jsonProcessing = false;
    for (Throwable cause : causes(error)) {
      jsonProcessing |= cause instanceof JsonProcessingException;
    }
    if (decodedMapping && jsonProcessing) {
      return "mapping_failure";
    }
    return error instanceof JsonProcessingException ? "malformed_json" : "malformed_payload";
  }

  /**
   * Reports whether Kafka rejected a record because of the record itself. Retrying that record
   * fails the same way, unlike outages and authorization errors, which clear once fixed.
   *
   * @param error a send failure, including nested causes
   * @return true for oversized or otherwise invalid records
   */
  static boolean isRecordRejection(Throwable error) {
    for (Throwable cause : causes(error)) {
      if (cause instanceof RecordTooLargeException
          || cause instanceof RecordBatchTooLargeException
          || cause instanceof InvalidRecordException) {
        return true;
      }
    }
    return false;
  }

  /**
   * Reports whether an offset commit failed only because of a group rebalance or a retriable
   * broker condition. Such failures are routine and must not stop the listener: a later commit
   * covers the offsets, or the records are redelivered after the rebalance.
   *
   * @param error the commit failure
   * @return true for rebalance and retriable commit failures
   */
  static boolean isTransientCommitFailure(Exception error) {
    return error instanceof RetriableException
        || error instanceof RebalanceInProgressException
        || error instanceof CommitFailedException;
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
