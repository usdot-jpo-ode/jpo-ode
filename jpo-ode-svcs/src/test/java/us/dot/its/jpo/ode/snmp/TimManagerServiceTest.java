package us.dot.its.jpo.ode.snmp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.text.ParseException;

import org.junit.Test;
import org.snmp4j.ScopedPDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.UserTarget;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;

import ch.qos.logback.classic.Logger;
import kafka.network.RequestChannel.Response;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;
import mockit.Verifications;

public class TimManagerServiceTest {

    // Create and send tests
    /**
     * Test that a null argument to createAndSend() short circuits, returns
     * null, and logs error
     */
    @Test
    public void createAndSendshouldReturnNullWhenGivenNullArguments(@Mocked SnmpProperties mockSnmpProperties,
            @Mocked final Logger logger) {

        TimParameters testParams = null;

        assertNull(TimManagerService.createAndSend(testParams, mockSnmpProperties));

        new Verifications() {
            {
                logger.error("TIM SERVICE - Received null object");
            }
        };
    }

    /**
     * Test that if initializing an SnmpSession returns null, null is returned
     * and an exception is logged
     */
    @Test
    public void createAndSendShouldReturnNullWhenSessionInitThrowsException(@Mocked TimParameters mockTimParameters,
            @Mocked SnmpProperties mockSnmpProperties, @Mocked final Logger logger,
            @Mocked SnmpSession mockSnmpSession) {

        IOException expectedException = new IOException("testException123");
        try {
            new Expectations() {
                {
                    new SnmpSession((SnmpProperties) any);
                    result = expectedException;
                }
            };
        } catch (IOException e) {
            fail("Unexpected exception: " + e);
        }

        assertNull(TimManagerService.createAndSend(mockTimParameters, mockSnmpProperties));

        new Verifications() {
            {
                logger.error("TIM SERVICE - Failed to create SNMP session: {}", expectedException);
            }
        };

    }

//    @Test
//    public void createAndSendShouldReturnNullWhenSetThrowsException(@Mocked TimParameters mockTimParameters,
//            @Mocked SnmpProperties mockSnmpProperties, @Mocked final Logger logger,
//            @Mocked final SnmpSession mockSnmpSession, @Mocked final TimManagerService mockTimManagerService,
//            @Mocked ScopedPDU mockScopedPDU, @Mocked Snmp mockSnmp, @Mocked TransportMapping mockTransport,
//            @Mocked UserTarget mockTarget) {
//
//        IOException expectedException = new IOException("testException123");
//        try {
//            new Expectations() {
//                {
//                    new SnmpSession(mockSnmpProperties);
//                    result = mockSnmpSession;
//                    
//                    mockSnmpSession.set((ScopedPDU) any, (Snmp) any, (TransportMapping) any, (UserTarget) any);
//                    result = expectedException;
//
//                    mockSnmpSession.getSnmp();
//                    result = mockSnmp;
//                    mockSnmpSession.getTransport();
//                    result = mockTransport;
//                    mockSnmpSession.getTarget();
//                    result = mockTarget;
//                    
//                    
//                    TimManagerService.createPDU(mockTimParameters);
//                    result = mockScopedPDU;
//                    
//                }
//            };
//            
//        } catch (IOException e) {
//            fail("Unexpected exception mocking SnmpSession constructor: " + e);
//        }
//        
//        
//        assertNull(TimManagerService.createAndSend(mockTimParameters, mockSnmpProperties));
//        new Verifications() {
//            {
//                logger.error("TIM SERVICE - Error while sending PDU: {}", expectedException);
//            }
//        };
//    }

    // Create PDU tests
    @Test
    public void createPDUshouldReturnNullWhenGivenNullParams() {

        TimParameters nullParams = null;
        ScopedPDU result = TimManagerService.createPDU(nullParams);
        assertNull(result);
    }

    @Test
    public void shouldCreatePDU() throws ParseException {

        String expectedResult = "[1.0.15628.4.1.4.1.2.3 = 11, 1.0.15628.4.1.4.1.3.3 = 2, 1.0.15628.4.1.4.1.4.3 = 3, 1.0.15628.4.1.4.1.5.3 = 4, 1.0.15628.4.1.4.1.6.3 = 5, 1.0.15628.4.1.4.1.7.3 = 0c:02:14:11:11:2f, 1.0.15628.4.1.4.1.8.3 = 0c:02:14:11:11:2f, 1.0.15628.4.1.4.1.9.3 = 88, 1.0.15628.4.1.4.1.10.3 = 9, 1.0.15628.4.1.4.1.11.3 = 10]";

        String rsuSRMPsid = "11";
        int rsuSRMDsrcMsgId = 2;
        int rsuSRMTxMode = 3;
        int rsuSRMTxChannel = 4;
        int rsuSRMTxInterval = 5;
        String rsuSRMDeliveryStart = "0C011411112F";
        String rsuSRMDeliveryStop = "0C011411112F";
        String rsuSRMPayload = "88";
        int rsuSRMEnable = 9;
        int rsuSRMStatus = 10;

        TimParameters testParams = new TimParameters(rsuSRMPsid, rsuSRMDsrcMsgId, rsuSRMTxMode, rsuSRMTxChannel,
                rsuSRMTxInterval, "2017-12-02T17:47:11-05:00", "2017-12-02T17:47:11-05:00", rsuSRMPayload, rsuSRMEnable,
                rsuSRMStatus);

        ScopedPDU result = TimManagerService.createPDU(testParams);

        assertEquals("Incorrect type, expected PDU.SET (-93)", -93, result.getType());
        assertEquals(expectedResult, result.getVariableBindings().toString());
    }
}
