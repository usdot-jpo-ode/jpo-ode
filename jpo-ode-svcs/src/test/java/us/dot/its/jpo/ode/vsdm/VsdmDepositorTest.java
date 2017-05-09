package us.dot.its.jpo.ode.vsdm;

import org.junit.Test;

public class VsdmDepositorTest {

	@Test
	public void testVsdmDepositor() throws InterruptedException {
		VsdmDepositor vsdmDepositorThreaded = new VsdmDepositor();
		vsdmDepositorThreaded.run();
	}
}
