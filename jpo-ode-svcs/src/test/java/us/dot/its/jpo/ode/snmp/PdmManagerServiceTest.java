package us.dot.its.jpo.ode.snmp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Test;
import org.snmp4j.PDU;
import org.snmp4j.ScopedPDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.UserTarget;
import org.snmp4j.event.ResponseEvent;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import us.dot.its.jpo.ode.plugin.j2735.pdm.J2735ProbeDataManagement.PdmParameters;

public class PdmManagerServiceTest {

    @Injectable
    SnmpProperties mockSnmpProperties;
    @Injectable
    PdmParameters mockPdmParameters;

    @Test
    public void createAndSendshouldReturnNullWhenGivenNullPdmParameters() {

        PdmParameters testNullParams = null;

        assertNull(PdmManagerService.createAndSend(testNullParams, mockSnmpProperties));
    }

    @Test
    public void createAndSendshouldReturnNullWhenGivenNullSnmpProperties() {

        SnmpProperties testNullSnmpProperties = null;

        assertNull(PdmManagerService.createAndSend(mockPdmParameters, testNullSnmpProperties));
    }

    @Test
    public void createAndSendShouldReturnNullFailedToCreateSnmpSession(@Mocked final SnmpSession mockSnmpSession) {

        try {
            new Expectations() {
                {
                    new SnmpSession((SnmpProperties) any);
                    result = new IOException("testException123");
                }
            };
        } catch (IOException e) {
            fail("Unexpected exception in expectations block: " + e);
        }

        assertNull(PdmManagerService.createAndSend(mockPdmParameters, mockSnmpProperties));
    }

    @Test
    public void createAndSendShouldReturnNullWhenSetThrowsException(@Mocked final SnmpSession mockSnmpSession) {

        try {
            new Expectations() {
                {
                    new SnmpSession((SnmpProperties) any);

                    mockSnmpSession.set((PDU) any, (Snmp) any, (TransportMapping) any, (UserTarget) any);
                    result = new IOException("testException123");
                }
            };
        } catch (IOException e) {
            fail("Unexpected exception in expectations block: " + e);
        }

        assertNull(PdmManagerService.createAndSend(mockPdmParameters, mockSnmpProperties));
    }

    @Test
    public void testCreateAndSendShould(@Mocked final SnmpSession mockSnmpSession) {

        try {
            new Expectations() {
                {
                    new SnmpSession((SnmpProperties) any);

                    mockSnmpSession.set((PDU) any, (Snmp) any, (TransportMapping) any, (UserTarget) any);
                }
            };
        } catch (IOException e) {
            fail("Unexpected exception in expectations block: " + e);
        }

        assertEquals(ResponseEvent.class,
                PdmManagerService.createAndSend(mockPdmParameters, mockSnmpProperties).getClass());
    }

    @Test
    public void createPDUshouldReturnNullWhenGivenNullParams() {

        PdmParameters nullParams = null;
        ScopedPDU result = PdmManagerService.createPDU(nullParams);
        assertNull(result);
    }

}
