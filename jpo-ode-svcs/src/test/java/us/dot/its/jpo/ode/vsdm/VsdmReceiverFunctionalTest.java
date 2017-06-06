package us.dot.its.jpo.ode.vsdm;

import com.oss.asn1.Coder;
import com.oss.asn1.EncodeFailedException;
import com.oss.asn1.EncodeNotSupportedException;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.dot.its.jpo.ode.asn1.j2735.CVSampleMessageBuilder;
import us.dot.its.jpo.ode.j2735.J2735;
import us.dot.its.jpo.ode.j2735.semi.ServiceRequest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

public class VsdmReceiverFunctionalTest {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private static Coder coder = J2735.getPERUnalignedCoder();

	@Test
	@Ignore
	public void test() throws SocketException {
		int selfPort = 12321;
		String targetHost = "localhost";
		int targetPort = 5556;
		DatagramSocket socket = new DatagramSocket(selfPort);
		ServiceRequest sr = CVSampleMessageBuilder.buildVehicleSituationDataServiceRequest();
		ByteArrayOutputStream sink = new ByteArrayOutputStream();
		try {
			coder.encode(sr, sink);
			byte[] payload = sink.toByteArray();
			logger.info("ODE: Sending VSD Deposit ServiceRequest ...");
			socket.send(new DatagramPacket(payload, payload.length, new InetSocketAddress(targetHost, targetPort)));
		} catch (EncodeFailedException | EncodeNotSupportedException | IOException e) {
			logger.error("ODE: Error Sending VSD Deposit ServiceRequest", e);
		}
		if (socket != null)
			socket.close();
	}
}
