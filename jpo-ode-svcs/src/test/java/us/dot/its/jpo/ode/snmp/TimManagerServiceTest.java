package us.dot.its.jpo.ode.snmp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.snmp4j.ScopedPDU;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;

public class TimManagerServiceTest {

    // Create and send tests
    @Test
    public void shouldReturnNullWhenGivenNullTimParameters() {

        Address addr = GenericAddress.parse("127.0.0.1" + "/161");

        SnmpProperties testProps = new SnmpProperties(addr, "v3user", "password");
        TimParameters testParams = null;
        ResponseEvent response = null;
        try {
            response = TimManagerService.createAndSend(testParams, testProps);
        } catch (Exception e) {
            fail("Unexpected exception: " + e);
        }

        assertNull(response);
    }

    // Create PDU tests
    @Test
    public void shouldReturnNullPDUWhenGivenNullParams() {

        TimParameters nullParams = null;
        ScopedPDU result = TimManagerService.createPDU(nullParams);
        assertNull(result);
    }

    @Test
    public void shouldCreatePDU() {

        String expectedResult = "[1.0.15628.4.1.4.1.2.3 = 11, 1.0.15628.4.1.4.1.3.3 = 2, 1.0.15628.4.1.4.1.4.3 = 3, 1.0.15628.4.1.4.1.5.3 = 4, 1.0.15628.4.1.4.1.6.3 = 5, 1.0.15628.4.1.4.1.7.3 = f, 1.0.15628.4.1.4.1.8.3 = w, 1.0.15628.4.1.4.1.9.3 = 88, 1.0.15628.4.1.4.1.10.3 = 9, 1.0.15628.4.1.4.1.11.3 = 10]";

        String rsuSRMPsid = "11";
        int rsuSRMDsrcMsgId = 2;
        int rsuSRMTxMode = 3;
        int rsuSRMTxChannel = 4;
        int rsuSRMTxInterval = 5;
        String rsuSRMDeliveryStart = "66";
        String rsuSRMDeliveryStop = "77";
        String rsuSRMPayload = "88";
        int rsuSRMEnable = 9;
        int rsuSRMStatus = 10;

        TimParameters testParams = new TimParameters(rsuSRMPsid, rsuSRMDsrcMsgId, rsuSRMTxMode, rsuSRMTxChannel,
                rsuSRMTxInterval, rsuSRMDeliveryStart, rsuSRMDeliveryStop, rsuSRMPayload, rsuSRMEnable, rsuSRMStatus);

        ScopedPDU result = TimManagerService.createPDU(testParams);

        assertEquals("Incorrect type, expected PDU.SET (-93)", -93, result.getType());
        assertEquals(expectedResult, result.getVariableBindings().toString());
    }
}
