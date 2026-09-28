package us.dot.its.jpo.ode.codec.ffmlib;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;

class FfmlibCommitTrackerTest {

  @Test
  void tracksCommitCompletionAndFailures() {
    SimpleMeterRegistry meters = new SimpleMeterRegistry();
    FfmlibCommitTracker tracker = new FfmlibCommitTracker(meters);

    tracker.beginCommit();
    assertEquals(1, tracker.inFlight());
    tracker.completeCommit(new IllegalStateException("broker unavailable"));

    assertEquals(0, tracker.inFlight());
    assertEquals(1.0, meters.counter("ode.ffmlib.offset.commit.failures").count());
  }

  @Test
  void cancelsCommitMetricWhenImmediateAcknowledgementFails() {
    FfmlibCommitTracker tracker = new FfmlibCommitTracker(new SimpleMeterRegistry());

    tracker.beginCommit();
    tracker.cancelCommit();

    assertEquals(0, tracker.inFlight());
  }

  @Test
  void synchronousAcknowledgementCompletesCommitWithoutCallback() {
    FfmlibCommitTracker tracker = new FfmlibCommitTracker(new SimpleMeterRegistry());
    tracker.setSynchronous(true);

    tracker.beginCommit();
    tracker.completeSynchronousCommit();

    assertEquals(0, tracker.inFlight());
  }
}
