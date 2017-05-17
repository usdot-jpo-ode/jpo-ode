package us.dot.its.jpo.ode.vsdm;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.SerializableMessageProducerPool;

@RunWith(JMockit.class)
public class VsdmReceiverTest {
	
	@Tested
	VsdmReceiver testVsdmReceiver;
	
	@Injectable
	OdeProperties mockOdeProperties;

	@Test
	public void testRunError(@Mocked final DatagramSocket mockDatagramSocket, @Mocked final SerializableMessageProducerPool mockSmpp) {
		try {
			new Expectations() {{
				mockDatagramSocket.receive((DatagramPacket) any);
				result = new IOException();
			}};
		} catch (IOException e) {
			fail("Unexpected exception in expectations block.");
		}
		testVsdmReceiver.run();
	}

}
