/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */

package us.dot.its.jpo.ode.traveler;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TimIngestWatcherTest {

    @Test
    public void testRun() {
        TimIngestWatcher watcher = new TimIngestWatcher();
        watcher.run();

        // we can't easily test that the run method wrote the correct log message, but we can test that it reset the total messages received after running
        TimIngestTracker testTimIngestTracker = TimIngestTracker.getInstance();
        assertEquals(0, testTimIngestTracker.getTotalMessagesReceived().intValue());
    }

}