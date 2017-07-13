package us.dot.its.jpo.ode.dds;

import static org.junit.Assert.assertNull;

import java.net.DatagramSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Test;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import us.dot.its.jpo.ode.OdeProperties;

public class AbstractSubscriberDepositorTest {

   @Tested
   TestAbstractSubscriberDepositor testAbstractSubscriberDepositor;

   @Injectable
   OdeProperties mockOdeProperties;

   @Injectable
   int mockPort;

   @Test
   public void testCall() {
      assertNull(testAbstractSubscriberDepositor.call());
   }

   @Test
   public void testSocketGetterAndSetter() {
      testAbstractSubscriberDepositor.setSocket(null);
      assertNull(testAbstractSubscriberDepositor.getSocket());
   }

   @Test
   public void testSubscribe(@Mocked final Executors mockExecutors, @Mocked final DatagramSocket mockDatagramSocket,
         @Mocked final ExecutorService mockExecutorService) {

         new Expectations() {
            {
               //new DatagramSocket(anyInt);
               Executors.newSingleThreadExecutor();
               result = mockExecutorService;
               mockExecutorService.submit((Runnable) any);
               //Executors.newCachedThreadPool((ThreadFactory) any);
               //Executors.defaultThreadFactory();
            }
         };
      testAbstractSubscriberDepositor.subscribe(null, null);
   }

}
