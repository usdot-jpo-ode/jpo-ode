package us.dot.its.jpo.ode.snmp;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.UserTarget;
import org.snmp4j.security.USM;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class SnmpSessionTest {
	SnmpProperties testProps;
	SnmpSession snmpSession;

	@Injectable USM mockUSM;
	
	@Before
	public void setUp() throws Exception {
		Address testTarget = GenericAddress.parse("127.0.0.1" + "/161");
		String testUsername = "testUser";
		String testPassword = "testPass";
		int testRetries = 1;
		int testTimeout = 2000;
		testProps = new SnmpProperties(testTarget, testUsername, testPassword, testRetries, testTimeout);
		snmpSession = new SnmpSession(testProps);
	}

	/**
	 * Test that the constructor breaks when given a purely null props object
	 */
	@Test
	public void constructorShouldFailWhenGivenNullPropsObject() {

		SnmpProperties nullProps = null;

		try {
			new SnmpSession(nullProps);
			fail("Expected IllegalArgumentException");
		} catch (Exception e) {
			assertEquals("Expected IllegalArgumentException", IllegalArgumentException.class, e.getClass());
		}

	}

	@Test(expected = IOException.class)
	public void constructorShouldWithIOException(@Mocked DefaultUdpTransportMapping mockDefaultUdpTransportMapping)
			throws IOException {

		new Expectations() {
			{
				new DefaultUdpTransportMapping();
				result = new IOException();
			}
		};

		new SnmpSession(testProps);

	}

	@Test
	public void shouldSendMockSetCall() {
		SnmpSession testSession = null;
		try {
			testSession = new SnmpSession(testProps);
		} catch (IOException e) {
			fail("Unexpected exception: " + e);
		}

		PDU mockPDU = Mockito.mock(PDU.class);
		Snmp mockSnmp = Mockito.mock(Snmp.class);
		TransportMapping mockTransport = Mockito.mock(TransportMapping.class);
		UserTarget mockTarget = Mockito.mock(UserTarget.class);

		try {
			testSession.set(mockPDU, mockSnmp, mockTransport, mockTarget);
		} catch (IOException e) {
			fail("Unexpected error: " + e);
		}
	}

	@Test
	public void testGetSetMethods(@Mocked Snmp mockSnmp, @Mocked TransportMapping mockTransportMapping,
			@Mocked UserTarget mockUserTarget) {

		snmpSession.setSnmp(mockSnmp);
		assertEquals(mockSnmp, snmpSession.getSnmp());

		snmpSession.setTransport(mockTransportMapping);
		assertEquals(mockTransportMapping, snmpSession.getTransport());

		snmpSession.setTarget(mockUserTarget);
		assertEquals(mockUserTarget, snmpSession.getTarget());
	}

	@Test(expected = IOException.class)
	public void testResponseEventUDPException(@Mocked Snmp mockSnmp, @Mocked TransportMapping mockTransportMapping,
			@Mocked UserTarget mockUserTarget, @Mocked PDU mockPDU) throws IOException {

		new Expectations() {
			{
				mockTransportMapping.listen();
				result = new IOException();
			}
		};
		snmpSession.set(mockPDU, mockSnmp, mockTransportMapping, mockUserTarget);
	}

	@Test(expected = IOException.class)
	public void testResponseEventSNMPException(@Mocked Snmp mockSnmp, @Mocked TransportMapping mockTransportMapping,
			@Mocked UserTarget mockUserTarget, @Mocked PDU mockPDU) throws IOException {

		new Expectations() {
			{
				mockSnmp.set(mockPDU, mockUserTarget);
				result = new IOException();
			}
		};
		snmpSession.set(mockPDU, mockSnmp, mockTransportMapping, mockUserTarget);
	}
}
