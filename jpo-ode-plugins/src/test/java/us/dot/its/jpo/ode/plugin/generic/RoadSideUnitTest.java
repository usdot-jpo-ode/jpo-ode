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
package us.dot.its.jpo.ode.plugin.generic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import org.junit.Test;

import mockit.Tested;
import us.dot.its.jpo.ode.plugin.RoadSideUnit;
import us.dot.its.jpo.ode.plugin.SnmpProtocol;
import us.dot.its.jpo.ode.util.JsonUtils;
import us.dot.its.jpo.ode.plugin.RoadSideUnit.RSU;

public class RoadSideUnitTest {
	@Tested
	RSU testRSU;

	@Test
	public void testGettersAndSetters() {
		String rsuTarget = "target";
		testRSU.setRsuTarget(rsuTarget);
		assertEquals(rsuTarget, testRSU.getRsuTarget());
		String rsuUsername = "name";
		testRSU.setRsuUsername(rsuUsername);
		assertEquals(rsuUsername, testRSU.getRsuUsername());
		String rsuPassword = "password";
		testRSU.setRsuPassword(rsuPassword);
		assertEquals(rsuPassword, testRSU.getRsuPassword());
		int rsuRetries = 2;
		testRSU.setRsuRetries(rsuRetries);
		assertEquals(rsuRetries, testRSU.getRsuRetries());
		int rsuTimeout = 10000;
		testRSU.setRsuTimeout(rsuTimeout);
		assertEquals(rsuTimeout, testRSU.getRsuTimeout());
		SnmpProtocol snmpProtocol = SnmpProtocol.NTCIP1218;
		testRSU.setSnmpProtocol(snmpProtocol);
		assertEquals(snmpProtocol, testRSU.getSnmpProtocol());
	}

	@Test
	public void testConstructorIsPrivate() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
		Constructor<RoadSideUnit> constructor = RoadSideUnit.class.getDeclaredConstructor();
		assertTrue(Modifier.isPrivate(constructor.getModifiers()));
		constructor.setAccessible(true);
		try {
			constructor.newInstance();
			fail("Expected IllegalAccessException.class");
		} catch (Exception e) {
			assertEquals(InvocationTargetException.class, e.getClass());
		}
	}

	@Test
	public void testDeserializationFromJson() {
		String defaultRSU = "{\"rsuTarget\":\"10.10.10.10\",\"rsuUsername\":\"user\",\"rsuPassword\":\"pass\",\"rsuRetries\":\"3\",\"rsuTimeout\":\"5000\"}";
		
		RSU deserializedRSU = (RSU) JsonUtils.fromJson(defaultRSU, RSU.class);

		assertEquals("10.10.10.10", deserializedRSU.getRsuTarget());
		assertEquals("user", deserializedRSU.getRsuUsername());
		assertEquals("pass", deserializedRSU.getRsuPassword());
		assertEquals(3, deserializedRSU.getRsuRetries());
		assertEquals(5000, deserializedRSU.getRsuTimeout());
	}

	@Test
	public void testDeserializationFromJson_fourDot1RSU() {
		String fourDot1RSU = "{\"rsuTarget\":\"10.10.10.10\",\"rsuUsername\":\"user\",\"rsuPassword\":\"pass\",\"rsuRetries\":\"3\",\"rsuTimeout\":\"5000\",\"snmpProtocol\":\"FOURDOT1\"}";

		RSU deserializedRSU = (RSU) JsonUtils.fromJson(fourDot1RSU, RSU.class);

		assertEquals("10.10.10.10", deserializedRSU.getRsuTarget());
		assertEquals("user", deserializedRSU.getRsuUsername());
		assertEquals("pass", deserializedRSU.getRsuPassword());
		assertEquals(3, deserializedRSU.getRsuRetries());
		assertEquals(5000, deserializedRSU.getRsuTimeout());
		assertEquals(SnmpProtocol.FOURDOT1, deserializedRSU.getSnmpProtocol());
	}

	@Test
	public void testDeserializationFromJson_ntcip1218RSU() {
		String ntcip1218RSU = "{\"rsuTarget\":\"10.10.10.10\",\"rsuUsername\":\"user\",\"rsuPassword\":\"pass\",\"rsuRetries\":\"3\",\"rsuTimeout\":\"5000\",\"snmpProtocol\":\"NTCIP1218\"}";

		RSU deserializedRSU = (RSU) JsonUtils.fromJson(ntcip1218RSU, RSU.class);

		assertEquals("10.10.10.10", deserializedRSU.getRsuTarget());
		assertEquals("user", deserializedRSU.getRsuUsername());
		assertEquals("pass", deserializedRSU.getRsuPassword());
		assertEquals(3, deserializedRSU.getRsuRetries());
		assertEquals(5000, deserializedRSU.getRsuTimeout());
		assertEquals(SnmpProtocol.NTCIP1218, deserializedRSU.getSnmpProtocol());
	}

	// TODO: implement deserialization with unrecognized protocol so defaults to fourDot1

}
