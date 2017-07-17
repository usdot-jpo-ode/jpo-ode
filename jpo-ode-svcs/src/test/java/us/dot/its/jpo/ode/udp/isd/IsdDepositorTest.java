package us.dot.its.jpo.ode.udp.isd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.udp.trust.TrustSession;

@RunWith(JMockit.class)
public class IsdDepositorTest {
	
	@Mocked LoggerFactory mockLogFact;

	@Injectable
	OdeProperties mockOdeProps;
	@Injectable
	Environment mockEnv;
	

	@Test @Ignore
	public void testDeposit(@Mocked final DatagramSocket mockSock, @Mocked ConsumerRecord<String, byte[]> mockRec, @Mocked
			InetSocketAddress mockInetSocketAddress, @Injectable final TrustSession mockTrustManager) {

		try {
			new Expectations() {
				{
				   new TrustSession((OdeProperties) any, (DatagramSocket) any);
				   result = mockTrustManager;
					mockSock.send((DatagramPacket) any);
				}
			};
		} catch (IOException e) {
			fail("Unexpected exception in expectations block.");
		}

		IsdDepositor testIsdDepositor = new IsdDepositor(mockOdeProps);
		testIsdDepositor.setRecord(mockRec);
		assertEquals(testIsdDepositor.deposit(), mockRec.value());
	}
	
	@Test @Ignore
	public void testDepositWithException(@Mocked final DatagramSocket mockSock, @Mocked ConsumerRecord<String, byte[]> mockRec, @Mocked
			InetSocketAddress mockInetSocketAddress) {

		try {
			new Expectations() {
				{
					mockSock.send((DatagramPacket) any);
					result = new IOException("testException123");
				}
			};
		} catch (IOException e) {
			fail("Unexpected exception in expectations block.");
		}

		IsdDepositor testIsdDepositor = new IsdDepositor(mockOdeProps);
		testIsdDepositor.setRecord(mockRec);
		testIsdDepositor.deposit();
	}

}
