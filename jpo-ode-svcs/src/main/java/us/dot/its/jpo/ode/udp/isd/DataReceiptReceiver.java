package us.dot.its.jpo.ode.udp.isd;

import java.net.DatagramSocket;

import org.apache.tomcat.util.buf.HexUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oss.asn1.AbstractData;
import com.oss.asn1.DecodeFailedException;
import com.oss.asn1.DecodeNotSupportedException;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.asn1.j2735.J2735Util;
import us.dot.its.jpo.ode.j2735.J2735;
import us.dot.its.jpo.ode.j2735.semi.DataReceipt;
import us.dot.its.jpo.ode.udp.AbstractConcurrentUdpReceiver;

public class DataReceiptReceiver extends AbstractConcurrentUdpReceiver {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	public DataReceiptReceiver(OdeProperties odeProps, DatagramSocket sock) {
		super(sock, odeProps.getDataReceiptBufferSize());
		logger.debug("DataReceiptReceiver spawned.");
	}

	@Override
	protected AbstractData processPacket(byte[] data) throws DecodeFailedException, DecodeNotSupportedException {

		DataReceipt receipt = null;
		AbstractData response = J2735Util.decode(J2735.getPERUnalignedCoder(), data);

		if (response instanceof DataReceipt) {
			receipt = (DataReceipt) response;

			String hex = HexUtils.toHexString(data);
			logger.debug("Received DataReceipt (hex): {}", hex);
			logger.debug("Received DataReceipt (json): {}", receipt);
		}
		return receipt;
	}

}
