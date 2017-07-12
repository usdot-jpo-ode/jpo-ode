package us.dot.its.jpo.ode.udp;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.DatagramSocket;

import org.junit.Test;

import mockit.Injectable;
import mockit.Tested;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.j2735.semi.ServiceRequest;

public class TrustManagerTest {

   @Tested
   TrustManager testTrustManager;

   @Injectable
   OdeProperties mockOdeProperties;
   @Injectable
   DatagramSocket mockDatagramSocket;

   @Test
   public void shouldCreateServiceResponse(@Injectable ServiceRequest mockServiceRequest) {
      assertNotNull(testTrustManager.createServiceResponse(mockServiceRequest));
   }

   @Test
   public void testSettersAndGetters() {
      testTrustManager.setTrustEstablished(false);
      assertFalse(testTrustManager.isTrustEstablished());

      testTrustManager.setTrustEstablished(true);
      assertTrue(testTrustManager.isTrustEstablished());
   }
}
