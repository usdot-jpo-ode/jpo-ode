package us.dot.its.jpo.ode.udp;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.DatagramSocket;

import org.junit.Test;

import mockit.Injectable;
import mockit.Tested;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.j2735.dsrc.TemporaryID;
import us.dot.its.jpo.ode.j2735.semi.GroupID;
import us.dot.its.jpo.ode.j2735.semi.SemiDialogID;
import us.dot.its.jpo.ode.j2735.semi.ServiceRequest;
import us.dot.its.jpo.ode.udp.TrustManager.TrustManagerException;

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
   public void shouldCreateServiceRequest(@Injectable TemporaryID mockTemporaryID,
         @Injectable SemiDialogID mockSemiDialogID, @Injectable GroupID mockGroupID) {
      try {
         assertNotNull(testTrustManager.createServiceRequest(mockTemporaryID, mockSemiDialogID, mockGroupID));
      } catch (TrustManagerException e) {
         fail("Unexpected exception: " + e);
      }
   }

   @Test
   public void testSettersAndGetters() {
      testTrustManager.setTrustEstablished(false);
      assertFalse(testTrustManager.isTrustEstablished());

      testTrustManager.setTrustEstablished(true);
      assertTrue(testTrustManager.isTrustEstablished());

      testTrustManager.setEstablishingTrust(false);
      assertFalse(testTrustManager.isEstablishingTrust());

      testTrustManager.setEstablishingTrust(true);
      assertTrue(testTrustManager.isEstablishingTrust());
   }
}
