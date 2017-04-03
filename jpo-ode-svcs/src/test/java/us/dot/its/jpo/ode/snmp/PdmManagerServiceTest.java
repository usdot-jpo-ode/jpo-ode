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
import us.dot.its.jpo.ode.ManagerAndControllerServices;
import us.dot.its.jpo.ode.plugin.j2735.pdm.PDM;

public class PdmManagerServiceTest {

	@Injectable
	SnmpProperties mockSnmpProperties;
	@Injectable
	PDM mockPdmParameters;

	@Test
	public void createAndSendshouldReturnNullWhenGivenNullPdmParameters() {

		PDM testNullParams = null;

		assertNull(ManagerAndControllerServices.createAndSend(testNullParams, mockSnmpProperties));
	}

	@Test
	public void createAndSendshouldReturnNullWhenGivenNullSnmpProperties() {

		SnmpProperties testNullSnmpProperties = null;

		assertNull(ManagerAndControllerServices.createAndSend(mockPdmParameters, testNullSnmpProperties));
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

		assertNull(ManagerAndControllerServices.createAndSend(mockPdmParameters, mockSnmpProperties));
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

		assertNull(ManagerAndControllerServices.createAndSend(mockPdmParameters, mockSnmpProperties));
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
				ManagerAndControllerServices.createAndSend(mockPdmParameters, mockSnmpProperties).getClass());
	}

	@Test
	public void createPDUshouldReturnNullWhenGivenNullParams() {

		PDM nullParams = null;
		ScopedPDU result = PdmManagerService.createPDU(nullParams);
		assertNull(result);
	}

}
