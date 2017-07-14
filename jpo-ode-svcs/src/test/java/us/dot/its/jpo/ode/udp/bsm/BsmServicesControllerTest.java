package us.dot.its.jpo.ode.udp.bsm;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.udp.UdpServicesController;
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
   @Ignore
   public void testConstructor() {

      // TODO - Replace this with a UDP services controller test

      new Expectations(MessageProducer.class, Executors.class) {
         {
            MessageProducer.defaultStringMessageProducer(anyString, anyString);
            result = mockedStringProducer;

            MessageProducer.defaultByteArrayMessageProducer(anyString, anyString);
            result = mockedByteArrayProducer;

            Executors.newSingleThreadExecutor();
            result = mockedExecutorService;

            mockedExecutorService.submit((BsmReceiver) any);
         }
      };

      new UdpServicesController(mockOdeProperties);
   }

}
