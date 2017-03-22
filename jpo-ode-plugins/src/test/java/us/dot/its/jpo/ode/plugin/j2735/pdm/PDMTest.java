package us.dot.its.jpo.ode.plugin.j2735.pdm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Tested;
import mockit.integration.junit4.JMockit;

@RunWith(JMockit.class)
public class PDMTest {

    @Tested
    PDM testPDM;

    @Test
    public void testSettersAndGetters() {

        int testDirections = 11;
        testPDM.setDirections(testDirections);
        assertEquals(testDirections, testPDM.getDirections());

        int testMaxSnapshotDistance = 13;
        testPDM.setMaxSnapshotDistance(testMaxSnapshotDistance);
        assertEquals(testMaxSnapshotDistance, testPDM.getMaxSnapshotDistance());

        int testMinSnapshotDistance = 13;
        testPDM.setMinSnapshotDistance(testMinSnapshotDistance);
        assertEquals(testMinSnapshotDistance, testPDM.getMinSnapshotDistance());

        int testMaxSnapshotTime = 15;
        testPDM.setMaxSnapshotTime(testMaxSnapshotTime);
        assertEquals(testMaxSnapshotTime, testPDM.getMaxSnapshotTime());

        int testMinSnapshotTime = 17;
        testPDM.setMinSnapshotTime(testMinSnapshotTime);
        assertEquals(testMinSnapshotTime, testPDM.getMinSnapshotTime());

        int testSampleEnd = 19;
        testPDM.setSampleEnd(testSampleEnd);
        assertEquals(testSampleEnd, testPDM.getSampleEnd());

        int testSampleStart = 21;
        testPDM.setSampleStart(testSampleStart);
        assertEquals(testSampleStart, testPDM.getSampleStart());

        int testSnapshotChoice = 23;
        testPDM.setSnapshotChoice(testSnapshotChoice);
        assertEquals(testSnapshotChoice, testPDM.getSnapshotChoice());

        int testSnapshotMaxSpeed = 25;
        testPDM.setSnapshotMaxSpeed(testSnapshotMaxSpeed);
        assertEquals(testSnapshotMaxSpeed, testPDM.getSnapshotMaxSpeed());

        int testSnapshotMinSpeed = 27;
        testPDM.setSnapshotMinSpeed(testSnapshotMinSpeed);
        assertEquals(testSnapshotMinSpeed, testPDM.getSnapshotMinSpeed());

        int testTermChoice = 29;
        testPDM.setTermChoice(testTermChoice);
        assertEquals(testTermChoice, testPDM.getTermChoice());

        int testTermDistance = 31;
        testPDM.setTermDistance(testTermDistance);
        assertEquals(testTermDistance, testPDM.getTermDistance());

        int testTermTime = 33;
        testPDM.setTermTime(testTermTime);
        assertEquals(testTermTime, testPDM.getTermTime());

        int testTxInterval = 35;
        testPDM.setTxInterval(testTxInterval);
        assertEquals(testTxInterval, testPDM.getTxInterval());

        testPDM.setVehicleStatusRequestList(null);
        assertNull(testPDM.getVehicleStatusRequestList());
    }

}
