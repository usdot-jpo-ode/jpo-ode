package us.dot.its.jpo.ode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class KTableEntryRemoverTest {

  private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
  private final List<String> tombstoned = new CopyOnWriteArrayList<>();

  @AfterEach
  void stopScheduler() {
    scheduler.shutdownNow();
  }

  @Test
  void tombstonesOnlyAfterTheEntryIsStored() throws Exception {
    AtomicInteger lookups = new AtomicInteger();
    CountDownLatch removed = new CountDownLatch(1);
    KTableEntryRemover remover = new KTableEntryRemover(
        key -> lookups.incrementAndGet() < 3 ? null : "{\"metadata\":{}}",
        key -> {
          tombstoned.add(key);
          removed.countDown();
        },
        scheduler, 1, 10);

    remover.removeWhenStored("stream-1");

    assertTrue(removed.await(5, TimeUnit.SECONDS));
    assertEquals(List.of("stream-1"), tombstoned);
    assertEquals(3, lookups.get());
  }

  @Test
  void retriesWhileTheStoreIsNotQueryable() throws Exception {
    AtomicInteger lookups = new AtomicInteger();
    CountDownLatch removed = new CountDownLatch(1);
    KTableEntryRemover remover = new KTableEntryRemover(
        key -> {
          if (lookups.incrementAndGet() == 1) {
            throw new IllegalStateException("store is rebalancing");
          }
          return "{}";
        },
        key -> {
          tombstoned.add(key);
          removed.countDown();
        },
        scheduler, 1, 10);

    remover.removeWhenStored("stream-2");

    assertTrue(removed.await(5, TimeUnit.SECONDS));
    assertEquals(List.of("stream-2"), tombstoned);
  }

  @Test
  void givesUpAfterTheConfiguredPolls() throws Exception {
    AtomicInteger lookups = new AtomicInteger();
    CountDownLatch exhausted = new CountDownLatch(3);
    KTableEntryRemover remover = new KTableEntryRemover(
        key -> {
          lookups.incrementAndGet();
          exhausted.countDown();
          return null;
        },
        tombstoned::add, scheduler, 1, 3);

    remover.removeWhenStored("stream-3");

    assertTrue(exhausted.await(5, TimeUnit.SECONDS));
    scheduler.shutdown();
    assertTrue(scheduler.awaitTermination(5, TimeUnit.SECONDS));
    assertEquals(3, lookups.get());
    assertTrue(tombstoned.isEmpty());
  }
}
