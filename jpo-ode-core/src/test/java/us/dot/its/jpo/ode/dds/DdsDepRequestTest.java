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
package us.dot.its.jpo.ode.dds;

import static org.junit.Assert.*;
import org.junit.Test;

public class DdsDepRequestTest {

	@Test
	public void testSystemDepositName() {
		DdsDepRequest ddsDepRequest = new DdsDepRequest();
		String depositName = "test deposit name";
		ddsDepRequest.setSystemDepositName(depositName);
		assertEquals(depositName, ddsDepRequest.getSystemDepositName());
	}
	
	@Test
	public void testEncodeType() {
		DdsDepRequest ddsDepRequest = new DdsDepRequest();
		String encodeType = "hex";
		ddsDepRequest.setEncodeType(encodeType);
		assertEquals(encodeType, ddsDepRequest.getEncodeType());
		assertNotEquals("base64", ddsDepRequest.getEncodeType());
	}
	
	@Test
	public void testEncodedMsg() {
		DdsDepRequest ddsDepRequest = new DdsDepRequest();
		String encodedMsg = "Sample Encode Msg";
		ddsDepRequest.setEncodedMsg(encodedMsg);
		assertEquals(encodedMsg, ddsDepRequest.getEncodedMsg());
		assertNotEquals("invalid encoded msg", ddsDepRequest.getEncodedMsg());
	}
	
	@Test
	public void testToString() {
		DdsDepRequest ddsDepRequest = new DdsDepRequest();
		String encodedMsg = "testEncodeMsg";
		String encodeType = "hex";
		String depositName = "testDepositName";
		
		ddsDepRequest.setEncodeType(encodedMsg);
		ddsDepRequest.setEncodeType(encodeType);
		ddsDepRequest.setSystemDepositName(depositName);
		String expectedStr = "DEPOSIT:{\"systemDepositName\":\"testDepositName\",\"encodeType\":\"hex\",\"dialogID\":0}";
		assertEquals(expectedStr, ddsDepRequest.toString());
	}
	
	@Test
	public void testHashCode() {
		DdsDepRequest ddsDepRequest1 = new DdsDepRequest();
		DdsDepRequest ddsDepRequest2 = new DdsDepRequest();
		assertEquals(ddsDepRequest1.hashCode(), ddsDepRequest2.hashCode());
		
		String encodeType = "hex";
		ddsDepRequest1.setEncodeType(encodeType);
		assertNotEquals(ddsDepRequest1.hashCode(), ddsDepRequest2.hashCode());
		
		ddsDepRequest2.setEncodeType(encodeType);
		assertEquals(ddsDepRequest1.hashCode(), ddsDepRequest2.hashCode());
		
		String encodedMsg = "testEncodeMsg";
		ddsDepRequest1.setEncodedMsg(encodedMsg);
		ddsDepRequest2.setEncodedMsg(encodedMsg);
		
		String depositName = "testDepositName";
		ddsDepRequest1.setSystemDepositName(depositName);
		ddsDepRequest2.setSystemDepositName(depositName);
		
		assertEquals(ddsDepRequest1.hashCode(), ddsDepRequest2.hashCode());
	}
	
	@Test
	public void testEquals() {
		DdsDepRequest ddsDepRequest1 = new DdsDepRequest();
		DdsDepRequest ddsDepRequest2 = new DdsDepRequest();
		Object randomObject = new Object();
		assertTrue(ddsDepRequest1.equals(ddsDepRequest2));
		assertTrue(ddsDepRequest1.equals(ddsDepRequest1));
		assertFalse(ddsDepRequest1.equals(randomObject));
		assertFalse(ddsDepRequest1.equals(null));
		
		ddsDepRequest2.setEncodeType("NotNull");
		assertFalse(ddsDepRequest1.equals(ddsDepRequest2));
		
		String encodeType = "hex";
		ddsDepRequest1.setEncodeType(encodeType);
		assertFalse(ddsDepRequest1.equals(ddsDepRequest2));
		
		ddsDepRequest2.setEncodeType(encodeType);
		assertTrue(ddsDepRequest1.equals(ddsDepRequest2));
		
		ddsDepRequest2.setEncodedMsg("NotNull");
		assertFalse(ddsDepRequest1.equals(ddsDepRequest2));
		
		String encodedMsg = "testEncodeMsg";
		ddsDepRequest1.setEncodedMsg(encodedMsg);
		assertFalse(ddsDepRequest1.equals(ddsDepRequest2));
		
		ddsDepRequest2.setEncodedMsg(encodedMsg);
		assertTrue(ddsDepRequest1.equals(ddsDepRequest2));
		
		ddsDepRequest2.setSystemDepositName("NotNull");
		assertFalse(ddsDepRequest1.equals(ddsDepRequest2));
		
		String depositName = "testDepositName";
		ddsDepRequest1.setSystemDepositName(depositName);
		assertFalse(ddsDepRequest1.equals(ddsDepRequest2));
		
		ddsDepRequest2.setSystemDepositName(depositName);
		assertTrue(ddsDepRequest1.equals(ddsDepRequest2));
	}
	
	

}
