package us.dot.its.jpo.ode.udp.isd;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oss.asn1.AbstractData;
import com.oss.asn1.DecodeFailedException;
import com.oss.asn1.DecodeNotSupportedException;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.asn1.j2735.J2735Util;
import us.dot.its.jpo.ode.j2735.J2735;
import us.dot.its.jpo.ode.j2735.semi.DataReceipt;

public class DataReceiptReceiver implements Callable<Object> {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private DatagramSocket socket;
	private OdeProperties odeProperties;

	public DataReceiptReceiver(OdeProperties odeProps, DatagramSocket sock) {
		this.odeProperties = odeProps;
		this.socket = sock;
	}

	@Override
	public DataReceipt call() throws Exception {

		return receiveDataReceipt();
	}

	public DataReceipt receiveDataReceipt() {

		AbstractData receipt = null;
		try {
			byte[] buffer = new byte[odeProperties.getServiceResponseBufferSize()];
			logger.debug("Waiting for data receipt from SDC...");
			DatagramPacket receiptDp = new DatagramPacket(buffer, buffer.length);
			socket.receive(receiptDp);

			if (buffer.length <= 0)
				throw new IOException("Received empty data receipt from SDC");

			receipt = J2735Util.decode(J2735.getPERUnalignedCoder(), buffer);
			if (receipt instanceof DataReceipt) {
				logger.debug("Successfully received data receipt from SDC {}", receipt.toString());

			}
		} catch (IOException | DecodeFailedException | DecodeNotSupportedException e) {
			logger.error("Error receiving data receipt from SDC.");
		}

		return (DataReceipt) receipt;

	}

}
