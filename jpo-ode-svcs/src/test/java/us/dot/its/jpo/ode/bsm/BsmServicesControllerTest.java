package us.dot.its.jpo.ode.bsm;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;
import us.dot.its.jpo.ode.OdeProperties;

@RunWith(JMockit.class)
public class BsmServicesControllerTest {

	@Injectable
	OdeProperties mockOdeProperties;

	@Mocked
	Executors mockedExecutors;

	@Mocked
	BsmReceiver mockedBsmReceiver;

	@Mocked
	ExecutorService mockedExecutorService;

	@Test
	public void testConstructor() {
		new Expectations() {
			{
				Executors.newSingleThreadExecutor();
				result = mockedExecutorService;

				mockedExecutorService.submit((BsmReceiver) any);
			}
		};

		new BsmServicesController(mockOdeProperties);
	}

}
