package us.dot.its.jpo.ode.snmp;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;
import org.snmp4j.ScopedPDU;

import ch.qos.logback.classic.Logger;
import mockit.Expectations;
import mockit.Mocked;
import mockit.Verifications;

public class PdmManagerServiceTest {

 // Create and send tests
    /**
     * Test that a null argument to createAndSend() short circuits, returns
     * null, and logs error
     */
    @Test
    public void createAndSendshouldReturnNullWhenGivenNullArguments(@Mocked SnmpProperties mockSnmpProperties,
            @Mocked final Logger logger) {

        us.dot.its.jpo.ode.plugin.j2735.J2735ProbeDataManagement.PdmParameters testParams = null;

        assertNull(PdmManagerService.createAndSend(testParams, mockSnmpProperties));

        new Verifications() {
            {
                logger.error("PDM SERVICE - Received null object");
            }
        };
    }

    /**
     * Test that if initializing an SnmpSession returns null, null is returned
     * and an exception is logged
     */
    @Test
    public void createAndSendShouldReturnNullWhenSessionInitThrowsException(@Mocked us.dot.its.jpo.ode.plugin.j2735.J2735ProbeDataManagement.PdmParameters mockPdmParameters,
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

        assertNull(PdmManagerService.createAndSend(mockPdmParameters, mockSnmpProperties));

        new Verifications() {
            {
                logger.error("PDM SERVICE - Failed to create SNMP session: {}", expectedException);
            }
        };

    }

    // Create PDU tests
    @Test
    public void createPDUshouldReturnNullWhenGivenNullParams() {

        us.dot.its.jpo.ode.plugin.j2735.J2735ProbeDataManagement.PdmParameters nullParams = null;
        ScopedPDU result = PdmManagerService.createPDU(nullParams);
        assertNull(result);
    }

}
