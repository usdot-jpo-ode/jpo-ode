package us.dot.its.jpo.ode.udp.isd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.DatagramSocket;

import org.junit.Test;
import org.slf4j.LoggerFactory;

import com.oss.asn1.Coder;
import com.oss.asn1.DecodeFailedException;
import com.oss.asn1.DecodeNotSupportedException;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.asn1.j2735.J2735Util;
import us.dot.its.jpo.ode.j2735.dsrc.DDateTime;
import us.dot.its.jpo.ode.j2735.semi.DataReceipt;
import us.dot.its.jpo.ode.j2735.semi.ServiceResponse;

public class ServiceResponseReceiverTest {

	@Tested
	ServiceResponseReceiver testServiceResponseReceiver;
	@Injectable
	OdeProperties mockOdeProperties;
	@Injectable
	DatagramSocket mockDatagramSocket;

	@Test
	public void test(@Mocked final J2735Util mockJ2735Util, @Mocked final LoggerFactory disabledLoggerFactory) {
		try {
			new Expectations() {
				{
					J2735Util.decode((Coder) any, (byte[]) any);
					result = new ServiceResponse();
				}
			};
		} catch (DecodeFailedException | DecodeNotSupportedException e1) {
			fail("Unexpected exception in expectations block.");
		}
		try {
			assertTrue(testServiceResponseReceiver.processPacket(new byte[0]) instanceof ServiceResponse);
		} catch (DecodeFailedException | DecodeNotSupportedException | IOException e) {
			fail("Unexpected exception." + e);
		}
	}

	@Test
	public void shouldThrowExceptionExpiredResponse(@Mocked final J2735Util mockJ2735Util,
			@Mocked final LoggerFactory disabledLoggerFactory) {
		try {
			new Expectations() {
				{
					J2735Util.decode((Coder) any, (byte[]) any);
					result = new ServiceResponse();
					J2735Util.isExpired((DDateTime) any);
					result = true;
				}
			};
		} catch (DecodeFailedException | DecodeNotSupportedException e1) {
			fail("Unexpected exception in expectations block.");
		}
		try {
			testServiceResponseReceiver.processPacket(new byte[0]);
			fail("Expected IOException");
		} catch (IOException e) {
			assertEquals("Received expired ServiceResponse.", e.getMessage());
		} catch (DecodeFailedException | DecodeNotSupportedException e) {
			fail("Incorrect exception type thrown." + e);
		}
	}

	@Test
	public void shouldReturnNullNotServiceResponse(@Mocked final J2735Util mockJ2735Util,
			@Mocked final LoggerFactory disabledLoggerFactory) {
		try {
			new Expectations() {
				{
					J2735Util.decode((Coder) any, (byte[]) any);
					result = new DataReceipt();
				}
			};
		} catch (DecodeFailedException | DecodeNotSupportedException e1) {
			fail("Unexpected exception in expectations block.");
		}
		try {
			assertNull(testServiceResponseReceiver.processPacket(new byte[0]));
		} catch (IOException | DecodeFailedException | DecodeNotSupportedException e) {
			fail("Unexpected exception." + e);
		}
	}

}
