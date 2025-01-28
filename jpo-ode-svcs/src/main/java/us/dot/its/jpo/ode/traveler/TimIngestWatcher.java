package us.dot.its.jpo.ode.traveler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimIngestWatcher implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(TimIngestWatcher.class.getName());
    private final long interval;

    public TimIngestWatcher(long interval) {
        this.interval = interval;
    }

    @Override
    public void run() {
        TimIngestTracker tracker = TimIngestTracker.getInstance();
        long ingested = tracker.getTotalMessagesReceived();

        if (ingested == 0) {
            logger.warn("ODE has not received TIM deposits in {} seconds.", interval);
        } else {
            logger.debug("ODE has received {} TIM deposits in the last {} seconds.", ingested, interval);
        }
        
        // After checking the number of TIMs ingested in the last interval, reset the counter
        tracker.resetTotalMessagesReceived();
    }
}