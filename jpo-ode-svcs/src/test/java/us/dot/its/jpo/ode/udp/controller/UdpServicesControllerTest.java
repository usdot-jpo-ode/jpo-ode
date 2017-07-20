package us.dot.its.jpo.ode.udp.controller;

import static org.junit.Assert.assertNotNull;

import java.util.concurrent.ThreadFactory;

import org.junit.Test;

import mockit.Capturing;
import mockit.Expectations;
import mockit.Mocked;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.dds.AbstractSubscriberDepositor;
import us.dot.its.jpo.ode.udp.AbstractUdpReceiverPublisher;
import us.dot.its.jpo.ode.udp.vsd.VsdDepositor;

public class UdpServicesControllerTest {

   @Mocked
   OdeProperties mockOdeProperties;

   @Capturing
   ServiceManager capturingServiceManager;
   @Mocked
   ServiceManager mockServiceManager;
   @Capturing
   AbstractUdpReceiverPublisher capturingAbstractUdpReceiverPublisher;
   @Capturing
   AbstractSubscriberDepositor capturingAbstractSubscriberDepositor;
   @Capturing
   VsdDepositor capturingVsdDepositor;

   @Test
   public void shouldSubmit5ThreadsVsdOn() {

      new Expectations() {
         {
            mockOdeProperties.getDepositSanitizedBsmToSdc();
            result = true;

            new ServiceManager((ThreadFactory) any);
            result = mockServiceManager;

            mockServiceManager.submit((AbstractUdpReceiverPublisher) any);
            times = 3;

            mockServiceManager.submit((AbstractSubscriberDepositor) any, anyString);
            times = 2;
         }
      };

      assertNotNull(new UdpServicesController(mockOdeProperties));
   }

   @Test
   public void shouldSubmit4ThreadsVsdOff() {

      new Expectations() {
         {
            mockOdeProperties.getDepositSanitizedBsmToSdc();
            result = false;

            new ServiceManager((ThreadFactory) any);
            result = mockServiceManager;

            mockServiceManager.submit((AbstractUdpReceiverPublisher) any);
            times = 3;

            mockServiceManager.submit((AbstractSubscriberDepositor) any, anyString);
            times = 1;

            new VsdDepositor((OdeProperties) any);
            times = 0;
         }
      };

      assertNotNull(new UdpServicesController(mockOdeProperties));
   }

}
