package us.dot.its.jpo.ode.traveler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class TimIngestWatcherTest {

    @Test
    public void testRun() {
        TimIngestWatcher watcher = new TimIngestWatcher(0);
        watcher.run();

        // we can't easily test that the run method wrote the correct log message, but we can test that it reset the total messages received after running
        TimIngestTracker testTimIngestTracker = TimIngestTracker.getInstance();
        assertEquals(0, testTimIngestTracker.getTotalMessagesReceived());
    }

}