package us.dot.its.jpo.ode.traveler;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class TimIngestTrackerTest {

    @Test
    public void testCanIncrementTotalMessagesReceived() {
        TimIngestTracker testTimIngestTracker = TimIngestTracker.getInstance();
        long priorCount = testTimIngestTracker.getTotalMessagesReceived();
        testTimIngestTracker.incrementTotalMessagesReceived();
        assertTrue(testTimIngestTracker.getTotalMessagesReceived() > priorCount);
    }

    @Test
    public void testCanResetTotalMessagesReceived() {
        TimIngestTracker testTimIngestTracker = TimIngestTracker.getInstance();
        testTimIngestTracker.incrementTotalMessagesReceived();
        assertTrue(testTimIngestTracker.getTotalMessagesReceived()> 0);
        testTimIngestTracker.resetTotalMessagesReceived();
        assertEquals(0, testTimIngestTracker.getTotalMessagesReceived());
    }
}
