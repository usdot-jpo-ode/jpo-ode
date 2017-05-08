package us.dot.its.jpo.ode.vsdm;

import org.junit.Test;

public class VsdmDepositorThreadedTest {

	@Test
	public void test() throws InterruptedException {
		VsdmDepositorThreaded vsdmDepositorThreaded = new VsdmDepositorThreaded();
		vsdmDepositorThreaded.run();
		System.out.println("Ending the test");
	}
}
