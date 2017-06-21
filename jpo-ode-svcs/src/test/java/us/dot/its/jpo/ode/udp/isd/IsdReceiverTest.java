/**
 * 
 */
package us.dot.its.jpo.ode.udp.isd;

import static org.junit.Assert.fail;

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
import us.dot.its.jpo.ode.wrapper.MessageProducer;

@RunWith(JMockit.class)
public class IsdReceiverTest {

	@Injectable
	OdeProperties mockOdeProps;

	@Test
	public void test(@Mocked final MessageProducer<?, ?> mockMessageProducer, @Mocked final DatagramSocket mockSocket) {

		try {
			new Expectations() {
				{
					new DatagramSocket(anyInt);
					result = mockSocket;
					
					//mockSocket.receive((DatagramPacket) any);
					
				}
			};
		} catch (IOException e) {
			fail("Unexpected exception in expectations block " + e);
		}

		IsdReceiver testIsdReceiver = new IsdReceiver(mockOdeProps);
		testIsdReceiver.setStopped(true);
		testIsdReceiver.run();
	}

}
