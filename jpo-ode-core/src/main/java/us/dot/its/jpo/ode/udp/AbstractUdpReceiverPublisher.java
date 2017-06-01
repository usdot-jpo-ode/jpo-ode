package us.dot.its.jpo.ode.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.oss.asn1.AbstractData;
import com.oss.asn1.Coder;
import com.oss.asn1.DecodeFailedException;
import com.oss.asn1.DecodeNotSupportedException;

import us.dot.its.jpo.ode.asn1.j2735.J2735Util;
import us.dot.its.jpo.ode.j2735.J2735;

public abstract class AbstractUdpReceiverPublisher implements Runnable {

	public class UdpReceiverException extends Exception {
        private static final long serialVersionUID = 1L;

        public UdpReceiverException(String string, Exception e) {
            super(string, e);
        }

    }

    private static Logger logger = LoggerFactory.getLogger(AbstractUdpReceiverPublisher.class);
	private static Coder coder = J2735.getPERUnalignedCoder();

	private DatagramSocket socket;

    private int port;
    private int bufferSize;

	@Autowired
	public AbstractUdpReceiverPublisher(int port, int bufferSize) {

		this.port = port;
		this.bufferSize = bufferSize;

		try {
			socket = new DatagramSocket(this.port);
			logger.info("Created UDP socket bound to port {}", this.port);
		} catch (SocketException e) {
			logger.error("Error creating socket with port " + this.port, e);
		}
	}

	@Override
	public void run() {

		logger.debug("UDP Receiver Service started.");

		byte[] buffer = new byte[bufferSize];

		DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

		boolean stopped = false;
		while (!stopped) {
			try {
				logger.debug("Waiting for UDP packets...");
				socket.receive(packet);
				logger.debug("Packet received.");
				String obuIp = packet.getAddress().getHostAddress();
				int obuPort = packet.getPort();

				// extract the actualPacket from the buffer
				byte[] actualPacket = Arrays.copyOf(packet.getData(), packet.getLength());
				if (packet.getLength() > 0) {
				    AbstractData decoded = decodeData(actualPacket, obuIp, obuPort);
				    publish(decoded);
				}
			} catch (IOException e) {
				logger.error("Error receiving packet", e);
			} catch (UdpReceiverException e) {
                logger.error("Error decoding packet", e);
            }
		}
	}

	protected AbstractData decodeData(byte[] msg, String obuIp, int obuPort) 
	        throws UdpReceiverException {
        AbstractData decoded = null;
		try {
			decoded = J2735Util.decode(coder, msg);
		} catch (DecodeFailedException | DecodeNotSupportedException e) {
		    throw new UdpReceiverException("Unable to decode UDP message", e);
		}
        return decoded;
	}

	   protected abstract void publish(AbstractData data);
}
