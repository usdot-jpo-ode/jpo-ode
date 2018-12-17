package us.dot.its.jpo.ode.udp.controller;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.junit.Test;

import mockit.Capturing;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import us.dot.its.jpo.ode.dds.AbstractSubscriberDepositor;
import us.dot.its.jpo.ode.udp.AbstractUdpReceiverPublisher;
import us.dot.its.jpo.ode.udp.controller.ServiceManager;

public class ServiceManagerTest {

   @Tested
   ServiceManager testServiceManager;

   @Mocked
   Executors mockExecutors;
   
   @Injectable
   ThreadFactory mockThreadFactory;
   

   @Test
   public void receiverSubmitCallsExecutorService(@Capturing Executors mockExecutors,
         @Injectable AbstractUdpReceiverPublisher mockAbstractUdpReceiverPublisher,
         @Mocked ExecutorService mockExecutorService) {

      new Expectations() {
         {
            Executors.newSingleThreadExecutor((ThreadFactory) any);
            result = mockExecutorService;

            mockExecutorService.submit((AbstractUdpReceiverPublisher) any);
         }
      };

      testServiceManager.submit(mockAbstractUdpReceiverPublisher);

   }
@Test
   public void depositorCallsSubscribe(@Mocked AbstractSubscriberDepositor mockAbstractSubscriberDepositor) {

      new Expectations() {
         {
            mockAbstractSubscriberDepositor.start(anyString);
            times = 1;
         }
      };

      testServiceManager.submit(mockAbstractSubscriberDepositor, "Test");
   }

}
