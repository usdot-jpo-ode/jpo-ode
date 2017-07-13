package us.dot.its.jpo.ode.udp;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

import org.junit.Test;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.j2735.semi.ServiceRequest;
import us.dot.its.jpo.ode.j2735.semi.ServiceResponse;

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

   @Test
   public void shouldSendServiceResponse(@Mocked final DatagramPacket mockDatagramPacket) {
      try {
         new Expectations() {
            {
               mockDatagramSocket.send((DatagramPacket) any);
            }
         };
      } catch (IOException e) {
         fail("Unexpected exception in expectations block" + e);
      }
      testTrustManager.sendServiceResponse(new ServiceResponse(), "testIp", 0);
   }

   @Test
   public void shouldCatchSendServiceResponseError(@Mocked final DatagramPacket mockDatagramPacket) {
      try {
         new Expectations() {
            {
               mockDatagramSocket.send((DatagramPacket) any);
               result = new IOException("testException123");
            }
         };
      } catch (IOException e) {
         fail("Unexpected exception in expectations block" + e);
      }
      testTrustManager.sendServiceResponse(new ServiceResponse(), "testIp", 0);
   }

   @Test
   public void shouldSendServiceRequest(@Mocked final DatagramPacket mockDatagramPacket) {
      try {
         new Expectations() {
            {
               mockDatagramSocket.send((DatagramPacket) any);
            }
         };
      } catch (IOException e) {
         fail("Unexpected exception in expectations block" + e);
      }
      testTrustManager.sendServiceRequest(new ServiceRequest(), "testIp", 0);
   }

   @Test
   public void shouldCatchSendServiceRequestError(@Mocked final DatagramPacket mockDatagramPacket) {
      try {
         new Expectations() {
            {
               mockDatagramSocket.send((DatagramPacket) any);
               result = new IOException("testException123");
            }
         };
      } catch (IOException e) {
         fail("Unexpected exception in expectations block" + e);
      }
      testTrustManager.sendServiceRequest(new ServiceRequest(), "testIp", 0);
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
