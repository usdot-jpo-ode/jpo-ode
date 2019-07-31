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
package us.dot.its.jpo.ode.inet;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import us.dot.its.jpo.ode.util.CodecUtils;
import us.dot.its.jpo.ode.util.CrcCccitt;

public class InetPacket {
	
	private static final int MAGIC_NUMBER = 982451653; 
	private static final int MIN_BUNDLE_LENGTH = 4 + 4 + 1 + 4 + 2; // magic + port + type + ip4 + CRC

	private InetPoint point;
	private byte[] payload;
	
	public InetPacket(String host, int port, byte[] payload) throws UnknownHostException {
		this(InetAddress.getByName( host ), port, payload);
	}
	
	public InetPacket(InetAddress ipAddress, int port, byte[] payload) {
		this(new InetPoint(ipAddress.getAddress(), port), payload);
	}
	
	public InetPacket(InetPoint point, byte[] payload) {
		this.point = point;
		if ( !parseBundle(payload) )
			this.payload = payload;
	}
	
	public InetPacket(DatagramPacket packet) {
		point = new InetPoint(packet.getAddress().getAddress(), packet.getPort());
		byte[] data = Arrays.copyOfRange(packet.getData(), packet.getOffset(), packet.getLength());
		if ( !parseBundle(data) )
			payload = data;
	}
	
	public InetPacket(byte[] bundle) {
		if ( !parseBundle(bundle) )
			payload = bundle;
	}
	
	public InetPoint getPoint() {
		return point;
	}

	public byte[] getPayload() {
		return payload;
	}

	public byte[] getBundle() {
		if ( point == null )
			return payload;
		int payloadLength = payload != null ? payload.length : 0;
		int headerLength = MIN_BUNDLE_LENGTH - 4 + point.address.length;
		byte [] bundle = new byte[headerLength + payloadLength];
		ByteBuffer buffer = ByteBuffer.allocate(headerLength).order(ByteOrder.BIG_ENDIAN);
		buffer.putInt(MAGIC_NUMBER);
		buffer.putInt(point.port);
		buffer.put((byte)(point.address.length == 16 ? 1 : 0));
		buffer.put(point.address);
		byte[] header = buffer.array();
		assert(header.length == headerLength);
		CrcCccitt.setMsgCRC(header);
		System.arraycopy(header, 0, bundle, 0, headerLength);
		if ( payload != null )
			System.arraycopy(payload, 0, bundle, headerLength, payloadLength);
		return bundle;
	}
	
	public boolean parseBundle(byte[] bundle) {
		if ( bundle == null || bundle.length < MIN_BUNDLE_LENGTH )
			return false;
		ByteBuffer buffer = ByteBuffer.wrap(bundle);
		int magic = buffer.getInt();
		if ( magic != MAGIC_NUMBER ) 
			return false;
		int port = buffer.getInt();
		byte type = buffer.get();
		int addressLength = type == 1 ? 16 : 4;
		if ( buffer.remaining() < addressLength + 2 ) 
			return false;
		if ( !CrcCccitt.isValidMsgCRC(bundle, 0, MIN_BUNDLE_LENGTH - 4 + addressLength) ) 
			return false;
		byte[] address = new byte[addressLength];
		buffer.get(address,0,addressLength);
		buffer.getShort();
		point = new InetPoint(address, port, true);
		int payloadLength = bundle.length - MIN_BUNDLE_LENGTH + 4 - addressLength;
		payload = new byte[payloadLength];
		buffer.get(payload,0,payloadLength);
		return true;
	} 
	
	public String toHexString() {
		return CodecUtils.toHex(getBundle());
	}

}
