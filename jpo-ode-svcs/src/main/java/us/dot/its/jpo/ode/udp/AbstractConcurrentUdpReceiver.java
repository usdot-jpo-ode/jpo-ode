package us.dot.its.jpo.ode.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Arrays;
import java.util.concurrent.Callable;

import com.oss.asn1.AbstractData;
import com.oss.asn1.DecodeFailedException;
import com.oss.asn1.DecodeNotSupportedException;

import us.dot.its.jpo.ode.OdeProperties;

/**
 * Threadable DatagramSocket receiver
 */
public abstract class AbstractConcurrentUdpReceiver implements Callable<AbstractData> {

	public class AbstractConcurrentUdpReceiverException extends Exception {

		private static final long serialVersionUID = 1L;

		public AbstractConcurrentUdpReceiverException(String string, Exception e) {
			super(string, e);
		}
	}

	protected DatagramSocket socket;
	protected OdeProperties odeProperties;
	protected int bufferSize;

	protected abstract AbstractData processPacket(byte[] p)
	      throws DecodeFailedException, DecodeNotSupportedException, IOException;

	protected AbstractConcurrentUdpReceiver(DatagramSocket sock, int bufSize) {
		this.socket = sock;
		this.bufferSize = bufSize;
	}

	protected AbstractData receiveDatagram() throws AbstractConcurrentUdpReceiverException {
		AbstractData response = null;
		try {
			byte[] buffer = new byte[bufferSize];
			DatagramPacket resPack = new DatagramPacket(buffer, buffer.length);
			socket.receive(resPack);

			if (buffer.length <= 0)
				throw new IOException("Empty datagram packet.");

			byte[] packetData = Arrays.copyOf(resPack.getData(), resPack.getLength());

			response = processPacket(packetData);
		} catch (Exception e) {
			throw new AbstractConcurrentUdpReceiverException("Error receiving data on UDP socket.", e);
		}

		return response;
	}

	@Override
	public AbstractData call() throws Exception {
		return receiveDatagram();
	}

}
