package us.dot.its.jpo.ode;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;

/**
 * Tombstones a KTable entry once it has been stored, polling on a scheduler instead of blocking
 * the caller.
 *
 * <p>The tombstone must follow the stored record on the KTable topic. Sending it before the
 * topology has copied the record there would delete nothing and leave the record behind, so the
 * remover waits until the record is visible in the store before sending it.
 */
@Slf4j
final class KTableEntryRemover {

  private final Function<String, String> storedValue;
  private final Consumer<String> tombstone;
  private final ScheduledExecutorService scheduler;
  private final long pollIntervalMillis;
  private final int maxPolls;

  /**
   * Creates a remover.
   *
   * @param storedValue looks up the stored value for a key; may throw while the store is not
   *     queryable
   * @param tombstone sends the tombstone for a key
   * @param scheduler runs the polls
   * @param pollIntervalMillis delay between polls
   * @param maxPolls number of polls before giving up
   */
  KTableEntryRemover(Function<String, String> storedValue, Consumer<String> tombstone,
      ScheduledExecutorService scheduler, long pollIntervalMillis, int maxPolls) {
    this.storedValue = storedValue;
    this.tombstone = tombstone;
    this.scheduler = scheduler;
    this.pollIntervalMillis = pollIntervalMillis;
    this.maxPolls = maxPolls;
  }

  /** Schedules removal of the entry for {@code key} once it is stored. */
  void removeWhenStored(String key) {
    schedule(key, 1, 0);
  }

  private void schedule(String key, int poll, long delayMillis) {
    try {
      scheduler.schedule(() -> poll(key, poll), delayMillis, TimeUnit.MILLISECONDS);
    } catch (RejectedExecutionException e) {
      log.warn("Unable to schedule k-table removal for key {}: {}", key, e.getMessage());
    }
  }

  private void poll(String key, int poll) {
    String value = null;
    try {
      value = storedValue.apply(key);
    } catch (RuntimeException e) {
      log.debug("K-table store not queryable for key {} (poll {}/{}): {}", key, poll, maxPolls,
          e.getMessage());
    }
    if (value != null) {
      tombstone.accept(key);
      return;
    }
    if (poll >= maxPolls) {
      log.warn("K-table entry for key {} was not stored after {} polls; it was not removed", key,
          maxPolls);
      return;
    }
    schedule(key, poll + 1, pollIntervalMillis);
  }
}
