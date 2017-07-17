package us.dot.its.jpo.ode.udp.trust;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import org.junit.Test;

import com.oss.asn1.AbstractData;
import com.oss.asn1.PERUnalignedCoder;

import mockit.Capturing;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import us.dot.its.jpo.ode.j2735.J2735;
import us.dot.its.jpo.ode.j2735.semi.ServiceRequest;
import us.dot.its.jpo.ode.udp.trust.ServiceMessageUtil.ServiceMessageUtilException;

public class ServiceMessageUtilTest {

   @Test
   public void testPrivateConstructor() {

      Constructor<ServiceMessageUtil> constructor = null;
      try {
         constructor = ServiceMessageUtil.class.getDeclaredConstructor();
      } catch (NoSuchMethodException | SecurityException e) {
         fail("Unexpected exception: " + e);
      }

      if (null == constructor) {
         fail("Test failed to instantiate constructor.");
      }

      assertTrue(Modifier.isPrivate(constructor.getModifiers()));
      constructor.setAccessible(true);

      try {
         constructor.newInstance();
         fail("Expected " + ServiceMessageUtil.ServiceMessageUtilException.class);
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
            | UnsupportedOperationException | InvocationTargetException e) {
         assertTrue("Incorrect exception thrown: " + e.getCause(), e.getCause() instanceof UnsupportedOperationException);
         assertEquals("Incorrect exception message returned", "Cannot instantiate static class.", e.getCause().getMessage());
      }
   }

   @Test
   public void testCreateServiceResponse(@Injectable ServiceRequest mockServiceRequest) {
      assertNotNull(ServiceMessageUtil.createServiceResponse(mockServiceRequest, 5));
   }

   @Test
   public void testEncodeAndSend(@Mocked DatagramSocket mockDatagramSocket, @Injectable AbstractData mockAbstractData,
         @Capturing J2735 capturingJ2735, @Capturing PERUnalignedCoder capturingPERUnalignedCoder,
         @Capturing DatagramPacket capturingDatagramPacket) {
      try {
         new Expectations() {
            {
               mockDatagramSocket.send((DatagramPacket) any);
            }
         };
      } catch (IOException e) {
         fail("Unexpected exception in expectations block: " + e);
      }
      try {
         ServiceMessageUtil.encodeAndSend(mockDatagramSocket, mockAbstractData, "testIp", 1);
      } catch (ServiceMessageUtilException e) {
         fail("Unexpected exception: " + e);
      }
   }

   @Test
   public void testEncodeAndSendException(@Injectable DatagramSocket mockDatagramSocket,
         @Injectable AbstractData mockAbstractData, @Capturing J2735 mockJ2735) {

      String expectedExceptionText = "testException123";
      new Expectations() {
         {
            J2735.getPERUnalignedCoder();
            result = new IOException(expectedExceptionText);
         }
      };

      try {
         ServiceMessageUtil.encodeAndSend(mockDatagramSocket, mockAbstractData, "testIp", 1);
         fail("Expected " + ServiceMessageUtil.ServiceMessageUtilException.class);
      } catch (ServiceMessageUtilException e) {
         assertTrue("Incorrect exception thrown: " + e, e instanceof ServiceMessageUtilException);
         assertEquals("Incorrect exception message returned", expectedExceptionText, e.getCause().getMessage());
      }
   }

}
