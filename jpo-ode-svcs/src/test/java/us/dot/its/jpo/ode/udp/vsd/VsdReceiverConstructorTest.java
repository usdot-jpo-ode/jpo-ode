package us.dot.its.jpo.ode.udp.vsd;

import static org.junit.Assert.fail;

import java.net.DatagramSocket;
import java.net.SocketException;

import org.junit.Test;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.SerializableMessageProducerPool;
import us.dot.its.jpo.ode.udp.vsd.VsdReceiver;
import us.dot.its.jpo.ode.wrapper.MessageProducer;

public class VsdReceiverConstructorTest {

	@Test
	public void testConstructorSuccess(@Injectable OdeProperties mockOdeProps,
			@Mocked final SerializableMessageProducerPool<?, ?> mockSerializableMessageProducerPool, @Mocked final MessageProducer mockMessageProducer) {
		VsdReceiver testrec = new VsdReceiver(mockOdeProps);
	}

	@Test
	public void testConstructorError(@Injectable OdeProperties mockOdeProps, @Mocked DatagramSocket mockDatagramSocket,
			@Mocked final SerializableMessageProducerPool<?, ?> mockSerializableMessageProducerPool, @Mocked final MessageProducer mockMessageProducer) {
		try {
			new Expectations() {
				{
					new DatagramSocket(anyInt);
					result = new SocketException();
				}
			};
		} catch (SocketException e) {
			fail("Unexpected exception in expectations blow: " + e);
		}
		VsdReceiver testrec = new VsdReceiver(mockOdeProps);
	}

}
