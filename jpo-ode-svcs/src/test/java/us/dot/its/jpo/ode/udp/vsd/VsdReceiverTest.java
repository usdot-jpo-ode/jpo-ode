package us.dot.its.jpo.ode.udp.vsd;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.SerializableMessageProducerPool;
import us.dot.its.jpo.ode.udp.vsd.VsdReceiver;
import us.dot.its.jpo.ode.wrapper.MessageProducer;

@RunWith(JMockit.class)
public class VsdReceiverTest {

	@Tested
	VsdReceiver testVsdReceiver;

	@Injectable
	OdeProperties mockOdeProperties;

	@Test @Ignore
	public void testRunError(@Mocked final DatagramSocket mockDatagramSocket,
			@Mocked final SerializableMessageProducerPool mockSmpp,
			@Mocked final MessageProducer mockMessageProducer) {
		try {
			new Expectations() {
				{
					mockDatagramSocket.receive((DatagramPacket) any);
					result = new IOException();
				}
			};
		} catch (IOException e) {
			fail("Unexpected exception in expectations block.");
		}
		testVsdReceiver.run();
	}

}
