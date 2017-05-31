package us.dot.its.jpo.ode.bsm;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.dot.its.jpo.ode.OdeProperties;

/*
 * This class receives VSD from the OBU and forwards it to the SDC.
 */
public class VsdDepositor implements Runnable {
	private OdeProperties odeProps;
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private DatagramSocket socket = null;
	private byte[] encodedVsd;

	public VsdDepositor(OdeProperties odeProps, byte[] encodedVsd) {
		this.encodedVsd = encodedVsd;
		this.odeProps = odeProps;
		try {
			socket = new DatagramSocket(odeProps.getForwarderPort());
			logger.debug("Created VSD depositor Socket with port {}", odeProps.getVsdmSenderPort());
		} catch (SocketException e) {
			logger.error("Error creating socket with port {}", odeProps.getForwarderPort(),
					e);
		}
	}

	@Override
	public void run() {
		try {
			logger.debug("\nVSD in hex: \n{}\n", Hex.encodeHexString(encodedVsd));
			logger.debug("Sending VSD to SDC IP: {} Port: {}", odeProps.getSdcIp(),
					odeProps.getSdcPort());
			socket.send(new DatagramPacket(encodedVsd, encodedVsd.length,
					new InetSocketAddress(odeProps.getSdcIp(), odeProps.getSdcPort())));
		} catch (IOException e) {
			logger.error("Error Sending VSD to SDC {}", e);
		}

		if (this.socket != null) {
			logger.debug("Closing vsd depositor socket with port {}", odeProps.getVsdmSenderPort());
			socket.close();
		}
	}

}
