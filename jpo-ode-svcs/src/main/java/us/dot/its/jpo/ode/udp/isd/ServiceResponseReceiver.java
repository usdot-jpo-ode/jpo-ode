package us.dot.its.jpo.ode.udp.isd;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Arrays;
import java.util.concurrent.Callable;

import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oss.asn1.AbstractData;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.asn1.j2735.J2735Util;
import us.dot.its.jpo.ode.j2735.J2735;
import us.dot.its.jpo.ode.j2735.semi.ServiceResponse;

public class ServiceResponseReceiver implements Callable<ServiceResponse> {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private DatagramSocket socket;
	private OdeProperties odeProperties;

	public ServiceResponseReceiver(OdeProperties odeProps, DatagramSocket sock) {
		this.odeProperties = odeProps;
		this.socket = sock;
	}

	public ServiceResponse receiveServiceResponse() throws IOException {
		ServiceResponse servResponse = null;
		try {
			byte[] buffer = new byte[odeProperties.getServiceResponseBufferSize()];
			logger.debug("Waiting for ServiceResponse from SDC...");
			DatagramPacket responeDp = new DatagramPacket(buffer, buffer.length);
			socket.receive(responeDp);

			if (buffer.length <= 0)
				throw new IOException("Received empty service response from SDC");

			AbstractData response = J2735Util.decode(J2735.getPERUnalignedCoder(), buffer);
			if (response instanceof ServiceResponse) {
				logger.debug("Received ServiceResponse from SDC {}", response.toString());
				servResponse = (ServiceResponse) response;
				if (J2735Util.isExpired(servResponse.getExpiration())) {
					throw new IOException("ServiceResponse Expired");
				}

				byte[] actualPacket = Arrays.copyOf(responeDp.getData(), responeDp.getLength());
				logger.debug("\nServiceResponse in hex: \n{}\n", Hex.encodeHexString(actualPacket));
			}
		} catch (Exception e) {
			throw new IOException("Error Receiving Service Response", e);
		}

		return servResponse;
	}

	@Override
	public ServiceResponse call() throws Exception {
		return receiveServiceResponse();
	}

}
