/*******************************************************************************
 * Copyright 2018 572682
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package us.dot.its.jpo.ode.snmp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.UserTarget;
import org.snmp4j.security.USM;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import us.dot.its.jpo.ode.plugin.RoadSideUnit.RSU;

public class SnmpSessionTest {
	RSU testProps;
	SnmpSession snmpSession;

	@Injectable USM mockUSM;
	
	@Before
	public void setUp() throws Exception {
		String testUsername = "testUser";
		String testPassword = "testPass";
		int testRetries = 1;
		int testTimeout = 2000;
		testProps = new RSU("127.0.0.1" + "/161", testUsername, testPassword, testRetries, testTimeout);
		snmpSession = new SnmpSession(testProps);
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
		UserTarget mockTarget = Mockito.mock(UserTarget.class);

		try {
			testSession.set(mockPDU, mockSnmp, mockTarget, false);
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

	@Ignore // TODO update this test
	@Test(expected = IOException.class)
	public void testResponseEventUDPException(@Mocked Snmp mockSnmp, @Mocked TransportMapping mockTransportMapping,
			@Mocked UserTarget mockUserTarget, @Mocked PDU mockPDU) throws IOException {

		new Expectations() {
			{
				mockTransportMapping.listen();
				result = new IOException();
			}
		};
		snmpSession.set(mockPDU, mockSnmp, mockUserTarget, false);
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
		snmpSession.set(mockPDU, mockSnmp, mockUserTarget, false);
	}
}
