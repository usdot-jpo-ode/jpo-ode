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
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.text.ParseException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.snmp4j.PDU;
import org.snmp4j.ScopedPDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.UserTarget;
import org.snmp4j.security.USM;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import us.dot.its.jpo.ode.plugin.RoadSideUnit.RSU;
import us.dot.its.jpo.ode.plugin.SnmpProtocol;
import us.dot.its.jpo.ode.plugin.SNMP;
import us.dot.its.jpo.ode.plugin.ServiceRequest.OdeInternal.RequestVerb;

public class SnmpSessionTest {
	RSU testProps;
	SnmpSession snmpSession;

	@Injectable
	USM mockUSM;

	@BeforeEach
	public void setUp() throws Exception {
		String testUsername = "testUser";
		String testPassword = "testPass";
		int testRetries = 1;
		int testTimeout = 2000;
		testProps = new RSU("127.0.0.1" + "/161", testUsername, testPassword, testRetries, testTimeout);
		snmpSession = new SnmpSession(testProps);
	}

	@Test
	public void constructorShouldWithIOException(@Mocked DefaultUdpTransportMapping mockDefaultUdpTransportMapping)
			throws IOException {
		assertThrows(IOException.class, () -> {
			new Expectations() {
				{
					new DefaultUdpTransportMapping();
					result = new IOException();
				}
			};

			new SnmpSession(testProps);
		});
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

	@Test
	public void testResponseEventUDPException(@Mocked Snmp mockSnmp, @Mocked TransportMapping mockTransportMapping,
			@Mocked UserTarget mockUserTarget, @Mocked PDU mockPDU) throws IOException {
		assertThrows(IOException.class, () -> {
			new Expectations() {
				{
					mockTransportMapping.listen();
					result = new IOException();
				}
			};
			snmpSession.setTransport(mockTransportMapping);
			snmpSession.set(mockPDU, mockSnmp, mockUserTarget, false);
		});
	}

	@Test
	public void testResponseEventSNMPException(@Mocked Snmp mockSnmp, @Mocked UserTarget mockUserTarget,
			@Mocked PDU mockPDU) throws IOException {
		assertThrows(IOException.class, () -> {
			new Expectations() {
				{
					mockSnmp.set(mockPDU, mockUserTarget);
					result = new IOException();
				}
			};
			snmpSession.set(mockPDU, mockSnmp, mockUserTarget, false);
		});
	}

	@Test
	public void shouldCreatePDUWithFourDot1Protocol() throws ParseException {

		String expectedResult = "[1.0.15628.4.1.4.1.2.3 = 80:03, 1.0.15628.4.1.4.1.3.3 = 2, 1.0.15628.4.1.4.1.4.3 = 3, 1.0.15628.4.1.4.1.5.3 = 4, 1.0.15628.4.1.4.1.6.3 = 5, 1.0.15628.4.1.4.1.7.3 = 07:e1:0c:02:11:2f, 1.0.15628.4.1.4.1.8.3 = 07:e1:0c:02:11:2f, 1.0.15628.4.1.4.1.9.3 = 88, 1.0.15628.4.1.4.1.10.3 = 9, 1.0.15628.4.1.4.1.11.3 = 10]";
		String expectedResult2 = "[1.0.15628.4.1.4.1.2.3 = 80:03, 1.0.15628.4.1.4.1.3.3 = 2, 1.0.15628.4.1.4.1.4.3 = 3, 1.0.15628.4.1.4.1.5.3 = 4, 1.0.15628.4.1.4.1.6.3 = 5, 1.0.15628.4.1.4.1.7.3 = 07:e1:0c:02:11:2f, 1.0.15628.4.1.4.1.8.3 = 07:e1:0c:02:11:2f, 1.0.15628.4.1.4.1.9.3 = 88, 1.0.15628.4.1.4.1.10.3 = 9]";

		String rsuSRMPsid = "00000083";
		int rsuSRMDsrcMsgId = 2;
		int rsuSRMTxMode = 3;
		int rsuSRMTxChannel = 4;
		int rsuSRMTxInterval = 5;
		String rsuSRMPayload = "88";
		int rsuSRMEnable = 9;
		int rsuSRMStatus = 10;

		SNMP testParams = new SNMP(rsuSRMPsid, rsuSRMDsrcMsgId, rsuSRMTxMode, rsuSRMTxChannel, rsuSRMTxInterval,
				"2017-12-02T17:47:11-05:00", "2017-12-02T17:47:11-05:00", rsuSRMEnable, rsuSRMStatus);

		boolean rsuDataSigningEnabled = true;

		ScopedPDU result = SnmpSession.createPDU(testParams, rsuSRMPayload, 3, RequestVerb.POST, SnmpProtocol.FOURDOT1, rsuDataSigningEnabled);

		assertEquals("Incorrect type, expected PDU.SET (-93)", -93, result.getType());
		assertEquals(expectedResult, result.getVariableBindings().toString());

		ScopedPDU result2 = SnmpSession.createPDU(testParams, rsuSRMPayload, 3, RequestVerb.GET, SnmpProtocol.FOURDOT1, rsuDataSigningEnabled);

		assertEquals("Incorrect type, expected PDU.SET (-93)", -93, result2.getType());
		assertEquals(expectedResult2, result2.getVariableBindings().toString());
	}

	@Test
	public void shouldCreatePDUWithNTCIP1218Protocol_dataSigningEnabledRsu_True() throws ParseException {
		// prepare
		String expectedResult = "[1.3.6.1.4.1.1206.4.2.18.3.2.1.2.3 = 80:03, 1.3.6.1.4.1.1206.4.2.18.3.2.1.3.3 = 4, 1.3.6.1.4.1.1206.4.2.18.3.2.1.4.3 = 5, 1.3.6.1.4.1.1206.4.2.18.3.2.1.5.3 = 07:e1:0c:02:11:2f:0b:00, 1.3.6.1.4.1.1206.4.2.18.3.2.1.6.3 = 07:e1:0c:02:11:2f:0b:00, 1.3.6.1.4.1.1206.4.2.18.3.2.1.7.3 = 88, 1.3.6.1.4.1.1206.4.2.18.3.2.1.8.3 = 9, 1.3.6.1.4.1.1206.4.2.18.3.2.1.9.3 = 10, 1.3.6.1.4.1.1206.4.2.18.3.2.1.10.3 = 6, 1.3.6.1.4.1.1206.4.2.18.3.2.1.11.3 = 00]";
		String expectedResult2 = "[1.3.6.1.4.1.1206.4.2.18.3.2.1.2.3 = 80:03, 1.3.6.1.4.1.1206.4.2.18.3.2.1.3.3 = 4, 1.3.6.1.4.1.1206.4.2.18.3.2.1.4.3 = 5, 1.3.6.1.4.1.1206.4.2.18.3.2.1.5.3 = 07:e1:0c:02:11:2f:0b:00, 1.3.6.1.4.1.1206.4.2.18.3.2.1.6.3 = 07:e1:0c:02:11:2f:0b:00, 1.3.6.1.4.1.1206.4.2.18.3.2.1.7.3 = 88, 1.3.6.1.4.1.1206.4.2.18.3.2.1.8.3 = 9, 1.3.6.1.4.1.1206.4.2.18.3.2.1.10.3 = 6, 1.3.6.1.4.1.1206.4.2.18.3.2.1.11.3 = 00]";
		String rsuSRMPsid = "00000083";
		int rsuSRMTxChannel = 4;
		int rsuSRMTxInterval = 5;
		String rsuSRMPayload = "88";
		int rsuSRMEnable = 9;
		int rsuSRMStatus = 10;

		SNMP testParams = new SNMP(rsuSRMPsid, 0, 0, rsuSRMTxChannel, rsuSRMTxInterval, "2017-12-02T17:47:11-05:00",
				"2017-12-02T17:47:11-05:00", rsuSRMEnable, rsuSRMStatus);

		boolean rsuDataSigningEnabled = true;

		// execute
		ScopedPDU result = SnmpSession.createPDU(testParams, rsuSRMPayload, 3, RequestVerb.POST, SnmpProtocol.NTCIP1218, true);
		ScopedPDU result2 = SnmpSession.createPDU(testParams, rsuSRMPayload, 3, RequestVerb.GET, SnmpProtocol.NTCIP1218, true);

		// verify
		assertEquals("Incorrect type, expected PDU.SET (-93)", -93, result.getType());
		assertEquals(expectedResult, result.getVariableBindings().toString());
		assertEquals("Incorrect type, expected PDU.SET (-93)", -93, result2.getType());
		assertEquals(expectedResult2, result2.getVariableBindings().toString());
	}

	@Test
	public void shouldCreatePDUWithNTCIP1218Protocol_dataSigningEnabledRsu_False() throws ParseException {
		// prepare
		String expectedResult = "[1.3.6.1.4.1.1206.4.2.18.3.2.1.2.3 = 80:03, 1.3.6.1.4.1.1206.4.2.18.3.2.1.3.3 = 4, 1.3.6.1.4.1.1206.4.2.18.3.2.1.4.3 = 5, 1.3.6.1.4.1.1206.4.2.18.3.2.1.5.3 = 07:e1:0c:02:11:2f:0b:00, 1.3.6.1.4.1.1206.4.2.18.3.2.1.6.3 = 07:e1:0c:02:11:2f:0b:00, 1.3.6.1.4.1.1206.4.2.18.3.2.1.7.3 = 88, 1.3.6.1.4.1.1206.4.2.18.3.2.1.8.3 = 9, 1.3.6.1.4.1.1206.4.2.18.3.2.1.9.3 = 10, 1.3.6.1.4.1.1206.4.2.18.3.2.1.10.3 = 6, 1.3.6.1.4.1.1206.4.2.18.3.2.1.11.3 = 80]";
		String expectedResult2 = "[1.3.6.1.4.1.1206.4.2.18.3.2.1.2.3 = 80:03, 1.3.6.1.4.1.1206.4.2.18.3.2.1.3.3 = 4, 1.3.6.1.4.1.1206.4.2.18.3.2.1.4.3 = 5, 1.3.6.1.4.1.1206.4.2.18.3.2.1.5.3 = 07:e1:0c:02:11:2f:0b:00, 1.3.6.1.4.1.1206.4.2.18.3.2.1.6.3 = 07:e1:0c:02:11:2f:0b:00, 1.3.6.1.4.1.1206.4.2.18.3.2.1.7.3 = 88, 1.3.6.1.4.1.1206.4.2.18.3.2.1.8.3 = 9, 1.3.6.1.4.1.1206.4.2.18.3.2.1.10.3 = 6, 1.3.6.1.4.1.1206.4.2.18.3.2.1.11.3 = 80]";
		String rsuSRMPsid = "00000083";
		int rsuSRMTxChannel = 4;
		int rsuSRMTxInterval = 5;
		String rsuSRMPayload = "88";
		int rsuSRMEnable = 9;
		int rsuSRMStatus = 10;

		SNMP testParams = new SNMP(rsuSRMPsid, 0, 0, rsuSRMTxChannel, rsuSRMTxInterval, "2017-12-02T17:47:11-05:00",
				"2017-12-02T17:47:11-05:00", rsuSRMEnable, rsuSRMStatus);

		boolean rsuDataSigningEnabled = false;

		// execute
		ScopedPDU result = SnmpSession.createPDU(testParams, rsuSRMPayload, 3, RequestVerb.POST, SnmpProtocol.NTCIP1218, rsuDataSigningEnabled);
		ScopedPDU result2 = SnmpSession.createPDU(testParams, rsuSRMPayload, 3, RequestVerb.GET, SnmpProtocol.NTCIP1218, rsuDataSigningEnabled);

		// verify
		assertEquals("Incorrect type, expected PDU.SET (-93)", -93, result.getType());
		assertEquals(expectedResult, result.getVariableBindings().toString());
		assertEquals("Incorrect type, expected PDU.SET (-93)", -93, result2.getType());
		assertEquals(expectedResult2, result2.getVariableBindings().toString());
	}

	@Test
	public void shouldProperlyPEncode(){
		String oid = "1.0.15628.4.1.4.1.2.3";
		String tim_hex="00000083";
		String bsm_hex="00000020";
		String data_log_transfer_hex="0020408E";
		String ota_update_hex = "0020408F";
		
		VariableBinding vb1 = SnmpSession.getPEncodedVariableBinding(oid, tim_hex);
		VariableBinding vb2 = SnmpSession.getPEncodedVariableBinding(oid, bsm_hex);
		VariableBinding vb3 = SnmpSession.getPEncodedVariableBinding(oid, data_log_transfer_hex);
		VariableBinding vb4 = SnmpSession.getPEncodedVariableBinding(oid, ota_update_hex);
		
		assertEquals("P-Encoding failed for " + tim_hex,"80:03", vb1.toValueString());
		assertEquals("P-Encoding failed for " + bsm_hex,"20", vb2.toValueString());
		assertEquals("P-Encoding failed for " + data_log_transfer_hex,"e0:00:00:0e", vb3.toValueString());
		assertEquals("P-Encoding failed for " + ota_update_hex,"e0:00:00:0f", vb4.toValueString());
	}
}