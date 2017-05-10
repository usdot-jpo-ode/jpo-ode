package us.dot.its.jpo.ode.inet;

import java.net.InetAddress;
import java.net.UnknownHostException;

import us.dot.its.jpo.ode.util.CodecUtils;

public class InetPoint {
	final public byte[] address;
	final public int port;
	final public boolean forward;
	
	public InetPoint(String host, int port, boolean forward) throws UnknownHostException {
		this(InetAddress.getByName(host).getAddress(), port, forward);
	}
	
	public InetPoint(byte[] address, int port) {
		this(address, port, false);
	}
	
	public InetPoint(byte[] address, int port, boolean forward ) {
		assert(address != null);
		this.address = address;
		this.port = port;
		this.forward = forward;
	}
	
	public InetAddress getInetAddress() throws UnknownHostException {
		return InetAddress.getByAddress(address);
	}
	
	public boolean isIPv6Address() {
		return address.length == 16;
	}
	
	@Override
	public String toString() {
		String host = "?";
		try {
			host = InetAddress.getByAddress(address).getHostAddress();
		} catch (UnknownHostException e) {
		}
		return String.format("%s { port = %d (0x%x); address = %s (%s, %s); forward = %s }",
				getClass().getSimpleName(),
				port, port,
				CodecUtils.toHex(address), address.length == 4 ? "IPv4" : "IPv6", host,
				forward ? "true" : "false"
				);
	}
}