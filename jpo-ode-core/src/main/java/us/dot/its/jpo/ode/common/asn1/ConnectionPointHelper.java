package us.dot.its.jpo.ode.common.asn1;

import us.dot.its.jpo.ode.j2735.semi.ConnectionPoint;
import us.dot.its.jpo.ode.j2735.semi.IPv4Address;
import us.dot.its.jpo.ode.j2735.semi.IPv6Address;
import us.dot.its.jpo.ode.j2735.semi.IpAddress;
import us.dot.its.jpo.ode.j2735.semi.PortNumber;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;

public class ConnectionPointHelper {
	
	public static ConnectionPoint createConnectionPoint(InetAddress ipAddress, int port) {
		if ( ipAddress != null ) {
			byte[] ipBytes = ipAddress.getAddress();
			IpAddress connectionPointIP =
				ipAddress instanceof Inet6Address ? IpAddress.createIpAddressWithIpv6Address(new IPv6Address(ipBytes)) :
				ipAddress instanceof Inet4Address ? IpAddress.createIpAddressWithIpv4Address(new IPv4Address(ipBytes)) : null;
			return connectionPointIP != null ? new ConnectionPoint(connectionPointIP, new PortNumber(port)) : null;
		} else {
			return createConnectionPoint(port);
		}
	}
	
	public static ConnectionPoint createConnectionPoint(int port) {
		return new ConnectionPoint(new PortNumber(port));
	}
}
