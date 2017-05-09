package us.dot.its.jpo.ode.vsdm;

import org.junit.Test;

public class VsdmDepositorThreadedTest {

	@Test
	public void testVsdmDepositor() throws InterruptedException {
		VsdmDepositorThreaded vsdmDepositorThreaded = new VsdmDepositorThreaded();
		vsdmDepositorThreaded.run();
	}
}
