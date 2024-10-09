package us.dot.its.jpo.ode.traveler;

public class TimIngestTracker {

    private Integer totalMessagesReceived;

    private TimIngestTracker() {
        totalMessagesReceived = 0;
    }

    public static TimIngestTracker getInstance() {
        return TimIngestMonitorHolder.INSTANCE;
    }

    private static class TimIngestMonitorHolder {
        private static final TimIngestTracker INSTANCE = new TimIngestTracker();
    }

    public Integer getTotalMessagesReceived() {
        return totalMessagesReceived;
    }

    public void incrementTotalMessagesReceived() {
        totalMessagesReceived++;
    }

    public void resetTotalMessagesReceived() {
        totalMessagesReceived = 0;
    }
 }
