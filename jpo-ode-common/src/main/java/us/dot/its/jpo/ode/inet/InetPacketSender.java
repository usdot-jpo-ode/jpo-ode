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

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import lombok.extern.slf4j.Slf4j;
import us.dot.its.jpo.ode.eventlog.EventLogger;

/**
 * Sender/Forwarder helper class for use by Forwarder, Transport, and Data Sink
 * that need to send packets around
 */
@Slf4j
public class InetPacketSender {

	private static final String INVALID_PARAMETERS_MSG = "Invalid Parameters. Parameters destination point and payload can not be null";

	/**
	 * Inet address and port to forward packets to
	 */
	private InetPoint frwdPoint;

	/**
	 * Specifies whether outbound IPv4 messages should be send directly or
	 * forwarded. Default is send directly.
	 * To force forwarding IPv4 messages, set this variable to true.
	 */
	private boolean forwardAll;

	public InetPacketSender() {
	}

	/**
	 * Creates an instance of the forwarder/sender helper class.
	 * 
	 * @param frwdPoint is the destination to use for forwarding
	 */
	public InetPacketSender(InetPoint frwdPoint) {
		this.frwdPoint = frwdPoint;
	}

	/**
	 * Forward packet. Intended client is the forwarder that received a packet
	 * 
	 * @param inbound UDP packet
	 * @throws InetPacketException
	 */
	public void forward(DatagramPacket packet) throws InetPacketException {
		if (packet == null) {
			log.warn("Ignoring forward request for null packet");
			return;
		}
		if (frwdPoint == null)
			throw new InetPacketException("Couldn't forward packet. Reason: Forwarding destination is not defined.");
		send(frwdPoint, new InetPacket(packet).getBundle());
	}

	/**
	 * Send packet. Intended client is the forwarder that sends outbound packet
	 * 
	 * @param packet outbound packet that contains destination+payload bundle
	 * @throws InetPacketException
	 */
	public void send(DatagramPacket packet) throws InetPacketException {
		if (packet == null) {
			log.warn("Ignoring send request for null packet");
			return;
		}
		InetPacket p = new InetPacket(packet);
		InetPoint point = p.getPoint();
		if (point == null)
			throw new InetPacketException(
					"Couldn't send packet. Reason: Destination is not defined in the packet (not a bundle?)");
		send(point, p.getPayload());
	}

	/**
	 * Forward payload to be sent to dstPoint. Intended clients are Transport or
	 * Data Sink sending via forwarder
	 * 
	 * @param dstPoint destination address and port for forwarder to forward to
	 * @param payload  data to forward
	 * @throws InetPacketException
	 */
	public void forward(InetPoint dstPoint, byte[] payload) throws InetPacketException {
		if (dstPoint == null || payload == null)
			throw new InetPacketException(INVALID_PARAMETERS_MSG);
		if (frwdPoint == null)
			log.warn("Couldn't forward packet. Reason: Forwarding destination is not defined.");
		if (frwdPoint != null && (dstPoint.isIPv6Address() || isForwardAll())) {
			send(frwdPoint, new InetPacket(dstPoint, payload).getBundle());
		} else {
			log.debug("Using direct send instead of forwarding");
			send(dstPoint, payload);
		}
	}

	/**
	 * Forward payload to be sent to dstPoint. Intended clients are Transport or
	 * Data Sink sending via forwarder or direct
	 * 
	 * @param dstPoint      destination address and port of the final destination
	 * @param payload       data to forward or send
	 * @param fromForwarder whether the original request came through a forwarder
	 * @throws InetPacketException
	 */
	public void forward(InetPoint dstPoint, byte[] payload, boolean fromForwarder) throws InetPacketException {
		if (dstPoint == null || payload == null)
			throw new InetPacketException(INVALID_PARAMETERS_MSG);
		if (frwdPoint != null && (dstPoint.isIPv6Address() || isForwardAll() || fromForwarder)) {
			send(frwdPoint, new InetPacket(dstPoint, payload).getBundle());
		} else {
			log.debug("Using direct send instead of forwarding");
			send(dstPoint, payload);
		}
	}

	/**
	 * Send payload to the destination specified. Intended clients are Transport or
	 * Data Sink sending directly to the client
	 * 
	 * @param dstPoint destination address and port to send to
	 * @param payload  data to send
	 * @throws InetPacketException
	 */
	public void send(InetPoint dstPoint, byte[] payload) throws InetPacketException {
		if (dstPoint == null || payload == null)
			throw new InetPacketException(INVALID_PARAMETERS_MSG);
		try (DatagramSocket sock = new DatagramSocket()) {
			DatagramPacket packet = new DatagramPacket(payload, payload.length, dstPoint.getInetAddress(),
					dstPoint.port);
			sock.send(packet);
		} catch (SocketException ex) {
			throw new InetPacketException("Couldn't send packet because socket closed.", ex);
		} catch (IOException ex) {
			throw new InetPacketException("Couldn't send packet due to IO exception.", ex);
		}
	}

	/**
	 * Reports whether outbound IPv4 messages should be send directly or forwarded.
	 * 
	 * @return true if IPv4 packets are forwarded in addition to IPv6 packets
	 */
	public boolean isForwardAll() {
		return forwardAll;
	}

	/**
	 * 
	 * @param forwardAll Directs how to handle IPv4 messages.
	 *                   Specify true to force forwarding IPv4 messages, and false
	 *                   to always send them directly.
	 */
	public void setForwardAll(boolean forwardAll) {
		this.forwardAll = forwardAll;
	}

}
