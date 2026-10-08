package us.dot.its.jpo.ode.codec.ffmlib;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.stereotype.Component;

/** Tracks async FFM offset-commit requests so shutdown and benchmarks can verify drain. */
@Component
public class FfmlibCommitTracker {

  private final AtomicInteger inFlight = new AtomicInteger();
  private final ConcurrentHashMap<Thread, ConcurrentLinkedQueue<Long>> startsByThread =
      new ConcurrentHashMap<>();
  private final Timer completion;
  private final Counter failures;
  private volatile boolean synchronous;

  /**
   * Creates the commit tracker and registers its metrics.
   *
   * @param meters registry for asynchronous commit metrics
   */
  public FfmlibCommitTracker(MeterRegistry meters) {
    Gauge.builder("ode.ffmlib.offset.commit.in.flight", inFlight, AtomicInteger::get)
        .register(meters);
    completion = Timer.builder("ode.ffmlib.offset.commit.completion")
        .minimumExpectedValue(Duration.ofNanos(1_000)).publishPercentileHistogram()
        .register(meters);
    failures = meters.counter("ode.ffmlib.offset.commit.failures");
  }

  /** Marks a commit request before acknowledging the corresponding input record. */
  public void beginCommit() {
    inFlight.incrementAndGet();
    startsByThread.computeIfAbsent(Thread.currentThread(), ignored -> new ConcurrentLinkedQueue<>())
        .add(System.nanoTime());
  }

  /** Configures whether acknowledgement returns only after the broker confirms the commit. */
  public void setSynchronous(boolean syncCommits) {
    synchronous = syncCommits;
  }

  /** Completes the tracked request after a synchronous acknowledgement returns. */
  public void completeSynchronousCommit() {
    if (synchronous) {
      completeCommit(null);
    }
  }

  /** Records async commit completion and releases its in-flight slot. */
  public void completeCommit(Exception error) {
    Long startedNanos = removeStartForCurrentThread();
    if (startedNanos == null) {
      return;
    }
    completion.record(System.nanoTime() - startedNanos, TimeUnit.NANOSECONDS);
    if (error != null) {
      failures.increment();
    }
    inFlight.decrementAndGet();
  }

  /** Removes a pending metric sample if an immediate acknowledgement cannot be issued. */
  public void cancelCommit() {
    if (removeStartForCurrentThread() != null) {
      inFlight.decrementAndGet();
      failures.increment();
    }
  }

  private Long removeStartForCurrentThread() {
    ConcurrentLinkedQueue<Long> starts = startsByThread.get(Thread.currentThread());
    Long startedNanos = starts == null ? null : starts.poll();
    if (startedNanos == null) {
      return null;
    }
    if (starts.isEmpty()) {
      startsByThread.remove(Thread.currentThread(), starts);
    }
    return startedNanos;
  }

  /** Number of offset commits awaiting Kafka callbacks. */
  public int inFlight() {
    return inFlight.get();
  }
}
