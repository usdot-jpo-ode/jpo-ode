package us.dot.its.jpo.ode.vsdm;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import org.apache.commons.codec.binary.Hex;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.oss.asn1.Coder;
import com.oss.asn1.EncodeFailedException;
import com.oss.asn1.EncodeNotSupportedException;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.asn1.j2735.CVSampleMessageBuilder;
import us.dot.its.jpo.ode.j2735.J2735;
import us.dot.its.jpo.ode.j2735.semi.ConnectionPoint;
import us.dot.its.jpo.ode.j2735.semi.PortNumber;
import us.dot.its.jpo.ode.j2735.semi.ServiceRequest;

@RunWith(JMockit.class)
public class ReqResForwarderTest {

	@Injectable
	OdeProperties mockOdeProperties;

	@Mocked
	DatagramSocket mockDatagramSocket;
	
	ReqResForwarder forwarder;
	
	@Before
	public void setUp() throws Exception {
		String obuIp = "1.1.1.1";
		int obuPort = 12321;
		int forwarderPort = 5555;

		new Expectations() {
			{	
				mockOdeProperties.getReturnIp();
				result = "3.3.3.3";

				mockOdeProperties.getForwarderPort();
				result = forwarderPort;

				mockOdeProperties.getForwarderPort();
				result = forwarderPort;
			}
		};
		
		ServiceRequest req = CVSampleMessageBuilder.buildVehicleSituationDataServiceRequest();
		ConnectionPoint newReturnAddr = new ConnectionPoint();
		newReturnAddr.setPort(new PortNumber(12321));
		req.setDestination(newReturnAddr);
		
		Coder coder = J2735.getPERUnalignedCoder();
		ByteArrayOutputStream sink = new ByteArrayOutputStream();
		try {
			coder.encode(req, sink);
		} catch (EncodeFailedException | EncodeNotSupportedException e) {
			System.out.println("ODE: Error Encoding VSD Deposit ServiceRequest");
		}

		System.out.println("Hex Req" + Hex.encodeHexString(sink.toByteArray()));

		forwarder = new ReqResForwarder(mockOdeProperties, req, obuIp, obuPort);
		String expectedHexString = "8000000000002020203018181818ad98";
		byte[] payload = forwarder.getPayload();
		assertEquals(expectedHexString, Hex.encodeHexString(payload));
	}

	@After
	public void tearDown() throws Exception {
		forwarder = null;
		System.out.println("ReqResForwarder Test ended");
	}

	@Test
	public void testSend() throws IOException {
		new Expectations() {
			{	
				mockOdeProperties.getSdcIp();
				result = "127.0.0.1";

				mockOdeProperties.getSdcPort();
				result = 12345;
				
				mockDatagramSocket.send((DatagramPacket)any);
			}
		};
		forwarder.send();
	}
	
	@Test
	public void testReceive() throws IOException {
		new Expectations() {
			{
				mockDatagramSocket.receive((DatagramPacket)any);
			}
		};
		forwarder.receiveVsdServiceResponse();
	}
}
