package us.dot.its.jpo.ode.vsdm;

import org.junit.Test;

public class VsdmDepositorTest {
	private String sdcIp = "104.130.170.234";
	private int sdcPort = 46753;
	private int serviceRequestSenderPort = 5556;
	private int vsdmSenderPort = 6666;
	
	// The ip and port where the SDC will send the ServiceResponse back
	private String returnIp = "54.210.159.61";
	private int returnPort = 6666;

	@Test
	public void testVsdmDepositor() throws InterruptedException {
		VsdmDepositor vsdmDepositorThreaded = new VsdmDepositor(sdcIp, sdcPort, returnIp, returnPort, serviceRequestSenderPort, vsdmSenderPort);
		vsdmDepositorThreaded.run();
	}
}
