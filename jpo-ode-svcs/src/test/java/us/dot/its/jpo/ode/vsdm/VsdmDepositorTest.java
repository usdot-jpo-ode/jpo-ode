package us.dot.its.jpo.ode.vsdm;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Expectations;
import mockit.Injectable;
import mockit.integration.junit4.JMockit;
import us.dot.its.jpo.ode.OdeProperties;

@RunWith(JMockit.class)
public class VsdmDepositorTest {
	private String sdcIp = "104.130.170.234";
	private int sdcPort = 46753;
	private int serviceRequestSenderPort = 5556;
	private int vsdmSenderPort = 6666;

	// The ip and port where the SDC will send the ServiceResponse back
	private String returnIp = "54.210.159.61";
	private int returnPort = 6666;

	@Injectable
	OdeProperties mockOdeProperties;

	@Before
	public void setUp() {

	}

	// Runs end to end testing
	@Test
	@Ignore
	public void testVsdmDepositor() throws InterruptedException {
		VsdmDepositor vsdmDepositorThreaded = new VsdmDepositor(sdcIp, sdcPort, returnIp, returnPort,
				serviceRequestSenderPort, vsdmSenderPort);
		vsdmDepositorThreaded.run();
	}

	@Test
	public void testConstructor1() {
		VsdmDepositor vsdmDepositor = new VsdmDepositor(sdcIp, sdcPort, returnIp, returnPort, serviceRequestSenderPort,
				vsdmSenderPort);
		assertEquals(vsdmDepositor.getSdcIp(), sdcIp);
		assertEquals(vsdmDepositor.getSdcPort(), sdcPort);
		assertEquals(vsdmDepositor.getReturnIp(), returnIp);
		assertEquals(vsdmDepositor.getReturnPort(), returnPort);
		assertEquals(vsdmDepositor.getServiceRequestSenderPort(), serviceRequestSenderPort);
		assertEquals(vsdmDepositor.getVsdmSenderPort(), vsdmSenderPort);
	}

	@Test
	public void testConstructor2() {
		new Expectations() {
			{
				mockOdeProperties.getSdcIp();
				result = sdcIp;
				mockOdeProperties.getSdcPort();
				result = sdcPort;
				mockOdeProperties.getServiceRequestSenderPort();
				result = serviceRequestSenderPort;
				mockOdeProperties.getVsdmSenderPort();
				result = vsdmSenderPort;
				mockOdeProperties.getReturnIp();
				result = returnIp;
				mockOdeProperties.getReturnPort();
				result = returnPort;
			}
		};

		VsdmDepositor vsdmDepositor = new VsdmDepositor(mockOdeProperties);
		assertEquals(vsdmDepositor.getSdcIp(), sdcIp);
		assertEquals(vsdmDepositor.getSdcPort(), sdcPort);
		assertEquals(vsdmDepositor.getReturnIp(), returnIp);
		assertEquals(vsdmDepositor.getReturnPort(), returnPort);
		assertEquals(vsdmDepositor.getServiceRequestSenderPort(), serviceRequestSenderPort);
		assertEquals(vsdmDepositor.getVsdmSenderPort(), vsdmSenderPort);
	}

	@Test
	public void test2() {
		new Expectations() {
			{
				mockOdeProperties.getSdcIp();
				result = sdcIp;
				mockOdeProperties.getSdcPort();
				result = sdcPort;
				mockOdeProperties.getServiceRequestSenderPort();
				result = serviceRequestSenderPort;
				mockOdeProperties.getVsdmSenderPort();
				result = vsdmSenderPort;
				mockOdeProperties.getReturnIp();
				result = returnIp;
				mockOdeProperties.getReturnPort();
				result = returnPort;
			}
		};

		VsdmDepositor vsdmDepositorThreaded = new VsdmDepositor(mockOdeProperties);
		vsdmDepositorThreaded.run();
	}
}
