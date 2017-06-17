package us.dot.its.jpo.ode.udp.isd;

import java.io.IOException;
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
import us.dot.its.jpo.ode.j2735.semi.ServiceResponse;
import us.dot.its.jpo.ode.udp.AbstractConcurrentUdpReceiver;

public class ServiceResponseReceiver extends AbstractConcurrentUdpReceiver {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	public ServiceResponseReceiver(OdeProperties odeProps, DatagramSocket sock) {
		super(sock, odeProps.getServiceResponseBufferSize());
		logger.debug("ServiceResponseReceiver spawned.");
	}

	@Override
	protected AbstractData processPacket(byte[] data)
	        throws DecodeFailedException, DecodeNotSupportedException, IOException {
		ServiceResponse returnMsg = null;

		AbstractData response = J2735Util.decode(J2735.getPERUnalignedCoder(), data);

		if (response instanceof ServiceResponse) {
			returnMsg = (ServiceResponse) response;
			if (J2735Util.isExpired(returnMsg.getExpiration())) {
				throw new IOException("Received expired ServiceResponse.");
			}

			String hex = HexUtils.toHexString(data);
			logger.debug("Received ServiceResponse (hex): {}", hex);
			logger.debug("Received ServiceResponse (json): {}", returnMsg);
		}
		return returnMsg;
	}

}
