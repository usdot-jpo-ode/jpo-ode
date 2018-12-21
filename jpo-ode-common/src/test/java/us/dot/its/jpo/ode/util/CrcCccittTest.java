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
package us.dot.its.jpo.ode.util;

import static org.junit.Assert.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class CrcCccittTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testCalculateCrcCccitt() {
		testCalculateCrcCccitt(0x0000, "");
		testCalculateCrcCccitt(0x2672, "1");
		testCalculateCrcCccitt(0x31c3, "123456789");
		testCalculateCrcCccitt(0x69df, "The generating polynomial used is the CRC-CCITT commonly expressed as x^16 + x^12 + x^5 + 1. An initial seed value of zero shall be used.");
		testCalculateCrcCccitt(0xd4f9, "MsgCRC ::= OCTET STRING (SIZE(2)) -- created with the CRC-CCITT polynomial");
	}
	
	@Test
	public void testCalculateCrcCccitt2() {
		int checkSum = CrcCccitt.calculateCrcCccitt(null);
		assertEquals(0, checkSum);
		checkSum = CrcCccitt.calculateCrcCccitt(null, 1, 10);
		assertEquals(0, checkSum);
		checkSum = CrcCccitt.calculateCrcCccitt("".getBytes(), 1, 10);
		assertEquals(0, checkSum);
		checkSum = CrcCccitt.calculateCrcCccitt("123".getBytes(), -1, 1);
		assertEquals(0, checkSum);
		checkSum = CrcCccitt.calculateCrcCccitt("1".getBytes(), 0, 10);
		assertEquals(0x2672, checkSum);
		checkSum = CrcCccitt.calculateCrcCccitt("123".getBytes(), 0, 1);
		assertEquals(0x2672, checkSum);
		checkSum = CrcCccitt.calculateCrcCccitt("0123456789abcdef".getBytes(), 1, 9);
		assertEquals(0x31c3, checkSum);
		checkSum = CrcCccitt.calculateCrcCccitt("0123456789abcdef".getBytes(), 5, 4);
		assertEquals(0xccf0, checkSum);
	}
	
	@Test
	public void testSetMsgCRC() {
		byte[] msg = "12345678900".getBytes();
		int checkSum = CrcCccitt.calculateCrcCccitt(msg,0,msg.length-2);
		assertEquals(0x31c3, checkSum);
		CrcCccitt.setMsgCRC(msg);
		assertEquals((byte)0x31, msg[msg.length-2]);
		assertEquals((byte)0xc3, msg[msg.length-1]);
		assertTrue(CrcCccitt.isValidMsgCRC(msg));
	}
	
	public void testCalculateCrcCccitt(int expectedCheckSum, String text ) {
		byte[] textBytes = text.getBytes();
		int checkSum = CrcCccitt.calculateCrcCccitt(textBytes);
		assertEquals(expectedCheckSum, checkSum);
		ByteBuffer buffer = ByteBuffer.allocate(2).order(ByteOrder.BIG_ENDIAN);
		buffer.putShort((short)checkSum);
		byte[] crcBytes = buffer.array();
		byte[] msg = new byte[text.length()+2];
		System.arraycopy(textBytes, 0, msg, 0, textBytes.length);
		System.arraycopy(crcBytes, 0, msg, textBytes.length, 2);
		boolean isValid = CrcCccitt.isValidMsgCRC(msg);
		assertTrue(isValid);
		//System.out.printf("0x%04x: %s: %3d %s\n", checkSum, isValid ? "valid" : "invalid", text.length(), text);
		System.arraycopy("12".getBytes(), 0, msg, textBytes.length, 2);
		isValid = CrcCccitt.isValidMsgCRC(msg);
		assertFalse(isValid);
		if ( textBytes.length > 0 ) {
			isValid = CrcCccitt.isValidMsgCRC(textBytes);
			assertFalse(isValid);
		}
	}

}
