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
	
	private static final int magicNumber = 982451653; 
	private static final int minBundleLength = 4 + 4 + 1 + 4 + 2; // magic + port + type + ip4 + CRC

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
		if ( parseBundle(payload) == false )
			this.payload = payload;
	}
	
	public InetPacket(DatagramPacket packet) {
		point = new InetPoint(packet.getAddress().getAddress(), packet.getPort());
		byte[] data = Arrays.copyOfRange(packet.getData(), packet.getOffset(), packet.getLength());
		if ( parseBundle(data) == false )
			payload = data;
	}
	
	public InetPacket(byte[] bundle) {
		if ( parseBundle(bundle) == false )
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
		final int header_length = minBundleLength - 4 + point.address.length;
		byte [] bundle = new byte[header_length + payloadLength];
		ByteBuffer buffer = ByteBuffer.allocate(header_length).order(ByteOrder.BIG_ENDIAN);
		buffer.putInt(magicNumber);
		buffer.putInt(point.port);
		buffer.put((byte)(point.address.length == 16 ? 1 : 0));
		buffer.put(point.address);
		byte[] header = buffer.array();
		assert(header.length == header_length);
		CrcCccitt.setMsgCRC(header);
		System.arraycopy(header, 0, bundle, 0, header_length);
		if ( payload != null )
			System.arraycopy(payload, 0, bundle, header_length, payloadLength);
		return bundle;
	}
	
	private boolean parseBundle(byte[] bundle) {
		if ( bundle == null || bundle.length < minBundleLength )
			return false;
		ByteBuffer buffer = ByteBuffer.wrap(bundle);
		int magic = buffer.getInt();
		if ( magic != magicNumber ) 
			return false;
		int port = buffer.getInt();
		byte type = buffer.get();
		int addressLength = type == 1 ? 16 : 4;
		if ( buffer.remaining() < addressLength + 2 )
			return false;
		if ( !CrcCccitt.isValidMsgCRC(bundle, 0, minBundleLength - 4 + addressLength) ) 
			return false;
		byte[] address = new byte[addressLength];
		buffer.get(address,0,addressLength);
		buffer.getShort();
		point = new InetPoint(address, port, true);
		int payloadLength = bundle.length - minBundleLength + 4 - addressLength;
		payload = new byte[payloadLength];
		buffer.get(payload,0,payloadLength);
		return true;
	} 
	
	public String toHexString() {
		return CodecUtils.toHex(getBundle());
	}

}
