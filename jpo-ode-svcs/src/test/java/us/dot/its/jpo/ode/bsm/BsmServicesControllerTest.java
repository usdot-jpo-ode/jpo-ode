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
import us.dot.its.jpo.ode.wrapper.MessageProducer;

@RunWith(JMockit.class)
public class BsmServicesControllerTest {

    @Injectable
    OdeProperties mockOdeProperties;

    @Mocked
	ExecutorService mockedExecutorService;
    @Mocked
    MessageProducer<?, ?> mockedStringProducer;
    
    @Mocked
    MessageProducer<?, ?> mockedByteArrayProducer;

	@Test
	public void testConstructor() {
		new Expectations(MessageProducer.class, Executors.class) {
			{
		        MessageProducer.defaultStringMessageProducer(anyString, anyString);
		        result = mockedStringProducer;

		        MessageProducer.defaultByteArrayMessageProducer(anyString, anyString);
                result = mockedByteArrayProducer;

		        Executors.newSingleThreadExecutor();
				result = mockedExecutorService;

				mockedExecutorService.submit((BsmReceiver2) any);
            }
		};

		new BsmServicesController(mockOdeProperties);
	}

}
