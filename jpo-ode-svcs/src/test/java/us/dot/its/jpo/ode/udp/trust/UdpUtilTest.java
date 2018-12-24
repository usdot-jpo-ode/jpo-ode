/*******************************************************************************
 * Copyright 2018 572682
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package us.dot.its.jpo.ode.udp.trust;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import org.junit.Test;

import us.dot.its.jpo.ode.udp.UdpUtil;

public class UdpUtilTest {

   @Test
   public void testPrivateConstructor() {

      Constructor<UdpUtil> constructor = null;
      try {
         constructor = UdpUtil.class.getDeclaredConstructor();
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
         fail("Expected " + UdpUtil.UdpUtilException.class);
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
            | UnsupportedOperationException | InvocationTargetException e) {
         assertTrue("Incorrect exception thrown: " + e.getCause(),
               e.getCause() instanceof UnsupportedOperationException);
         assertEquals("Incorrect exception message returned", "Cannot instantiate static class.",
               e.getCause().getMessage());
      }
   }

   //TODO open-ode
//   @Test
//   public void testCreateServiceResponse(@Injectable ServiceRequest mockServiceRequest) {
//      assertNotNull(UdpUtil.createServiceResponse(mockServiceRequest, 5));
//   }
//
//   @Test
//   public void testEncodeAndSend(@Mocked DatagramSocket mockDatagramSocket, @Injectable AbstractData mockAbstractData,
//         @Capturing J2735 capturingJ2735, @Capturing PERUnalignedCoder capturingPERUnalignedCoder,
//         @Capturing DatagramPacket capturingDatagramPacket) {
//      try {
//         new Expectations() {
//            {
//               mockDatagramSocket.send((DatagramPacket) any);
//            }
//         };
//      } catch (IOException e) {
//         fail("Unexpected exception in expectations block: " + e);
//      }
//      try {
//         UdpUtil.send(mockDatagramSocket, mockAbstractData, "testIp", 1);
//      } catch (UdpUtilException e) {
//         fail("Unexpected exception: " + e);
//      }
//   }
//
//   @Test @Ignore // TODO
//   public void testEncodeAndSendException(@Injectable DatagramSocket mockDatagramSocket,
//         @Injectable byte[] mockAbstractData, @Capturing J2735 mockJ2735) {
//
//      String expectedExceptionText = "testException123";
//      new Expectations() {
//         {
//            J2735.getPERUnalignedCoder();
//            result = new IOException(expectedExceptionText);
//         }
//      };
//
//      try {
//         UdpUtil.send(mockDatagramSocket, mockAbstractData, "testIp", 1);
//         fail("Expected " + UdpUtil.UdpUtilException.class);
//      } catch (Exception e) {
//         assertTrue("Incorrect exception thrown: " + e, e instanceof UdpUtilException);
//         assertEquals("Incorrect exception message returned", expectedExceptionText, e.getCause().getMessage());
//      }
//   }

}
