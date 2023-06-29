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
package us.dot.its.jpo.ode.inet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.junit.jupiter.api.Test;

import mockit.Capturing;
import mockit.Expectations;

public class InetPointTest {
   

   @Test
   public void testStringConstructorCreatesAddress() {
      try {
         new Expectations() {
            {
               InetAddress.getByName(anyString).getAddress();
            }
         };

         new InetPoint("bah.com", 5, true);
      } catch (UnknownHostException e) {
         fail("Unexpected exception: " + e);
      }
   }

   // TODO:
   // CUrrently this is a workaround to run other tests
   @Test
   public void testStringConstructorFailsNullAddress() {
      Throwable err = assertThrows(UnknownHostException.class, () -> {
         // try {
         new Expectations() {
            {
               InetAddress.getByName(anyString).getAddress();
               result = null;
            }
         };
         new InetPoint("something123", 5, true);
         // } 
         // catch (UnknownHostException e) {
         //    fail("Unexpected exception: " + e);
         // }
      });

      assertEquals("something123: Name or service not known", err.getMessage());
   }

   @Test
   public void testByteConstructorCreatesAddress() {
      new InetPoint(new byte[] { 1, 2, 3 }, 5, true);
   }

   @Test
   public void testByteConstructorFailsNullAddress() {
      Exception err = assertThrows(IllegalArgumentException.class, () -> {
         new InetPoint((byte[]) null, 5, true);
      });

      assertEquals("IP Address is required", err.getMessage());
   }

   @Test
   public void testBBytePortConstructorCreatesAddress() {
      new InetPoint(new byte[] { 1, 2, 3 }, 5);
   }

   @Test
   public void testBytePortConstructorFailsNullAddress() {
      Exception err = assertThrows(IllegalArgumentException.class, () -> {
         new InetPoint((byte[]) null, 5);
      });
      assertEquals("IP Address is required", err.getMessage());
   }

   @Test
   public void getInetAddressCallsGetAddress() {
      try {
         new Expectations() {
            {
               InetAddress.getByAddress(new byte[]{127, 0, 0, 1});
            }
         };
         new InetPoint(new byte[] { 1, 2, 3 ,0}, 5).getInetAddress();
      } catch (UnknownHostException e) {
         fail("Unexpected exception: " + e);
      }
   }

   @Test
   public void testAddressLength16IsIpv6() {
      assertTrue(new InetPoint(new byte[16], 5).isIPv6Address());
   }

   @Test
   public void testAddressLength4IsNotIpv6() {
      assertFalse(new InetPoint(new byte[4], 5).isIPv6Address());
   }

   @Test
   public void testToStringMethodIPV6() {
      assertEquals(
            "InetPoint { port = 5 (0x5); address = 00000000000000000000000000000000 (IPv6, 0:0:0:0:0:0:0:0); forward = false }",
            new InetPoint(new byte[16], 5).toString());
   }

   @Test
   public void testToStringException() {
         assertEquals(
               "InetPoint { port = 5 (0x5); address = 00000000000000000000000000000000 (IPv6, 0:0:0:0:0:0:0:0); forward = false }",
               new InetPoint(new byte[16], 5).toString());
   }

   @Test
   public void testToStringMethodIPV4() {
      assertEquals("InetPoint { port = 5 (0x5); address = 00000000 (IPv4, 0.0.0.0); forward = false }",
            new InetPoint(new byte[4], 5).toString());
   }

}
