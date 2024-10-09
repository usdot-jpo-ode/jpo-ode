package us.dot.its.jpo.ode.traveler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimIngestWatcher implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(TimIngestWatcher.class.getName());

    @Override
    public void run() {
        Integer ingested = TimIngestTracker.getInstance().getTotalMessagesReceived();

        if (ingested == 0) {
            logger.warn("ODE has not received TIM deposits.");
        } else {
            logger.debug("ODE has received {} TIM deposits.", ingested);
        }
    }
}