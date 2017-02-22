package us.dot.its.jpo.ode.snmp;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TimParametersTest {

    @Test
    public void shouldPopulate() {

        String testRsuSRMPsid = "1";
        int testRsuSRMDsrcMsgId = 2;
        int testRsuSRMTxMode = 3;
        int testRsuSRMTxChannel = 4;
        int testRsuSRMTxInterval = 5;
        String testRsuSRMDeliveryStart = "6";
        String testRsuSRMDeliveryStop = "7";
        String testRsuSRMPayload = "8";
        int testRsuSRMEnable = 9;
        int testRsuSRMStatus = 10;

        TimParameters actual = new TimParameters(testRsuSRMPsid, testRsuSRMDsrcMsgId, testRsuSRMTxMode,
                testRsuSRMTxChannel, testRsuSRMTxInterval, testRsuSRMDeliveryStart, testRsuSRMDeliveryStop,
                testRsuSRMPayload, testRsuSRMEnable, testRsuSRMStatus);
        
        assertEquals(testRsuSRMPsid, actual.rsuSRMPsid);
        assertEquals(testRsuSRMDsrcMsgId, actual.rsuSRMDsrcMsgId);
        assertEquals(testRsuSRMTxMode, actual.rsuSRMTxMode);
        assertEquals(testRsuSRMTxChannel, actual.rsuSRMTxChannel);
        assertEquals(testRsuSRMTxInterval, actual.rsuSRMTxInterval);
        assertEquals(testRsuSRMDeliveryStart, actual.rsuSRMDeliveryStart);
        assertEquals(testRsuSRMDeliveryStop, actual.rsuSRMDeliveryStop);
        assertEquals(testRsuSRMPayload, actual.rsuSRMPayload);
        assertEquals(testRsuSRMEnable, actual.rsuSRMEnable);
        assertEquals(testRsuSRMStatus, actual.rsuSRMStatus);
    }
}
