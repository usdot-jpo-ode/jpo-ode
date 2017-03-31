package us.dot.its.jpo.ode.snmp;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;

import org.junit.Test;

public class TimParametersTest {

    @Test
    public void shouldPopulate() throws ParseException {

        String testRsuSRMPsid = "1";
        int testRsuSRMDsrcMsgId = 2;
        int testRsuSRMTxMode = 3;
        int testRsuSRMTxChannel = 4;
        int testRsuSRMTxInterval = 5;
        String expectedRsuSRMDeliveryStart = "0C011411112F";
        String expectedRsuSRMDeliveryStop = "0C011411112F";
        String testRsuSRMPayload = "8";
        int testRsuSRMEnable = 9;
        int testRsuSRMStatus = 10;

        TimParameters actual = new TimParameters(testRsuSRMPsid, testRsuSRMDsrcMsgId, testRsuSRMTxMode,
                testRsuSRMTxChannel, testRsuSRMTxInterval, "2017-12-01T17:47:11-05:00", "2017-12-01T17:47:11-05:00",
                testRsuSRMPayload, testRsuSRMEnable, testRsuSRMStatus);
        
        assertEquals(testRsuSRMPsid, actual.rsuSRMPsid);
        assertEquals(testRsuSRMDsrcMsgId, actual.rsuSRMDsrcMsgId);
        assertEquals(testRsuSRMTxMode, actual.rsuSRMTxMode);
        assertEquals(testRsuSRMTxChannel, actual.rsuSRMTxChannel);
        assertEquals(testRsuSRMTxInterval, actual.rsuSRMTxInterval);
        assertEquals(expectedRsuSRMDeliveryStart, actual.rsuSRMDeliveryStart);
        assertEquals(expectedRsuSRMDeliveryStop, actual.rsuSRMDeliveryStop);
        assertEquals(testRsuSRMPayload, actual.rsuSRMPayload);
        assertEquals(testRsuSRMEnable, actual.rsuSRMEnable);
        assertEquals(testRsuSRMStatus, actual.rsuSRMStatus);
    }
}
