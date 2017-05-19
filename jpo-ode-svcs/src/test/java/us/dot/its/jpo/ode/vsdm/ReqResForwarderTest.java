package us.dot.its.jpo.ode.vsdm;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.j2735.semi.ServiceRequest;

@RunWith(JMockit.class)
public class ReqResForwarderTest {

	@Injectable
	OdeProperties mockOdeProperties;

	@Mocked
	DatagramSocket mockDatagramSocket;

	@Test
	public void testConstructor(@Mocked ServiceRequest mockRequest) {
		String obuIp = "1.1.1.1";
		int obuPort = 12321;
		int forwarderPort = 5555;

		new Expectations() {
			{	
				mockOdeProperties.getReturnIp();
				result = "2.2.2.2";

				mockOdeProperties.getReturnPort();
				result = forwarderPort;

				mockOdeProperties.getForwarderPort();
				result = forwarderPort;
			}
		};

		ReqResForwarder forwarder = new ReqResForwarder(mockOdeProperties, mockRequest, obuIp, obuPort);
	}

}
