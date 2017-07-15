package us.dot.its.jpo.ode.udp;

import static org.junit.Assert.assertNotNull;

import java.util.concurrent.ThreadFactory;

import org.junit.Test;

import mockit.Capturing;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.dds.AbstractSubscriberDepositor;
import us.dot.its.jpo.ode.udp.manager.ServiceManager;
import us.dot.its.jpo.ode.udp.manager.UdpServicesController;

public class UdpServicesControllerTest {

   @Test
   public void shouldSubmitFiveThreads(@Injectable OdeProperties mockOdeProperties,
         @Capturing ServiceManager capturingServiceManager, @Mocked ServiceManager mockServiceManager,
         @Capturing AbstractUdpReceiverPublisher capturingAbstractUdpReceiverPublisher,
         @Capturing AbstractSubscriberDepositor<?,?> capturingAbstractSubscriberDepositor) {

      new Expectations() {
         {
            new ServiceManager((ThreadFactory) any);
            result = mockServiceManager;

            mockServiceManager.submit((AbstractUdpReceiverPublisher) any);
            times = 3;

            mockServiceManager.submit((AbstractSubscriberDepositor<?, ?>) any, anyString);
            times = 2;
         }
      };

      assertNotNull(new UdpServicesController(mockOdeProperties));
   }

}
