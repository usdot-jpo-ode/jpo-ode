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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class CrcCccitt {
	// CRC-CCITT polynomial: x^16 + x^12 + x^5 + 1
	final private static int ccittPolynomial = 0x1021;
	final private static int ccittTableSize = 256;
	final private static int[] ccittTable = new int[ccittTableSize];

	static {
		for (int i = 0; i < ccittTableSize; i++) {
			int checkSum = i << 8;
			for (int j = 0; j < 8; j++)
				checkSum = (checkSum & 0x8000) == 0x8000 ? (checkSum << 1) ^ ccittPolynomial : (checkSum << 1);
			ccittTable[i] = checkSum & 0xFFFF;
		}
	}
	
	public static boolean isValidMsgCRC(byte[] msgIncludingMsgCRC) {
		return calculateCrcCccitt(msgIncludingMsgCRC) == 0;
	}
	
	
	public static boolean isValidMsgCRC(byte[] msgIncludingMsgCRC, int offset, int count) {
		return calculateCrcCccitt(msgIncludingMsgCRC, offset, count) == 0;
	}
	
	// update last two bytes of the message with CRC of all preceding bytes
	public static void setMsgCRC(byte[] msg) {
		if ( msg != null && msg.length > 2) {
			int checkSum = calculateCrcCccitt(msg, 0, msg.length-2);
			ByteBuffer buffer = ByteBuffer.allocate(2).order(ByteOrder.BIG_ENDIAN);
			buffer.putShort((short)checkSum);
			byte[] crcBytes = buffer.array();
			System.arraycopy(crcBytes, 0, msg, msg.length-2, 2);
		}
	}
	
	public static int calculateCrcCccitt(byte[] msg) {
		return msg != null ? calculateCrcCccitt(msg, 0, msg.length) : 0;
	}
	
	public static int calculateCrcCccitt(byte[] msg, int offset, int count) {
		int checkSum = 0;
		if ( msg != null && offset >= 0 ) {
			final int size = Math.min(msg.length, count);
			for( int i = 0; i < size; i++ ) {
				checkSum = (ccittTable[((checkSum >> 8) & 0xFF) ^ (msg[i+offset] & 0xFF)] ^ (checkSum << 8)) & 0xFFFF;
			}
		}
		return checkSum;
	}
}
