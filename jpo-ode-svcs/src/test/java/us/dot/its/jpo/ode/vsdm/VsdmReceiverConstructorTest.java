package us.dot.its.jpo.ode.vsdm;

import static org.junit.Assert.fail;

import java.net.DatagramSocket;
import java.net.SocketException;

import org.junit.Test;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.SerializableMessageProducerPool;

public class VsdmReceiverConstructorTest {

	@Test
	public void testConstructorSuccess(@Injectable OdeProperties mockOdeProps,
			@Mocked final SerializableMessageProducerPool<?, ?> mockSerializableMessageProducerPool) {
		VsdmReceiver testrec = new VsdmReceiver(mockOdeProps);
	}

	@Test
	public void testConstructorError(@Injectable OdeProperties mockOdeProps, @Mocked DatagramSocket mockDatagramSocket,
			@Mocked final SerializableMessageProducerPool<?, ?> mockSerializableMessageProducerPool) {
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
		VsdmReceiver testrec = new VsdmReceiver(mockOdeProps);
	}

}
