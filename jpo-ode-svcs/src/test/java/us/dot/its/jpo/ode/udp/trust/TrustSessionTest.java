package us.dot.its.jpo.ode.udp.trust;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

import org.junit.Test;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.udp.trust.TrustSession;

public class TrustSessionTest {

   @Tested
   TrustSession testTrustManager;

   @Injectable
   OdeProperties mockOdeProperties;
   @Injectable
   DatagramSocket mockDatagramSocket;

   @Test
   public void testSettersAndGetters() {
      testTrustManager.setTrustEstablished(false);
      assertFalse(testTrustManager.isTrustEstablished());

      testTrustManager.setTrustEstablished(true);
      assertTrue(testTrustManager.isTrustEstablished());
   }

   @Test
   public void shouldReturnTrustAlreadyEstablished(@Mocked final DatagramPacket mockDatagramPacket) {
      testTrustManager.setTrustEstablished(true);
      assertTrue(testTrustManager.establishTrust(null, null));
   }

   @Test
   public void establishTrustShouldReturnFalseZeroRetries(@Mocked final DatagramPacket mockDatagramPacket,
         @Mocked final InetSocketAddress mockInetSocketAddress) {

      new Expectations() {
         {
            mockOdeProperties.getTrustRetries();
            result = 0;
         }
      };
      testTrustManager.setTrustEstablished(false);
      assertFalse(testTrustManager.establishTrust(null, null));
   }
}
