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
package us.dot.its.jpo.ode.udp.bsm;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import org.apache.tomcat.util.buf.HexUtils;
import org.junit.Ignore;
import org.junit.Test;

import mockit.Capturing;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.coder.StringPublisher;
import us.dot.its.jpo.ode.coder.stream.LogFileToAsn1CodecPublisher;

@Ignore
public class BsmReceiverTest {

   @Tested
   BsmReceiver testBsmReceiver;

   @Injectable
   OdeProperties mockOdeProperties;

   @Capturing
   LogFileToAsn1CodecPublisher capturingLogFileToAsn1CodecPublisher;
   @Capturing
   StringPublisher capturingStringPublisher;
   @Capturing
   DatagramSocket capturingDatagramSocket;

   @Capturing
   DatagramPacket capturingDatagramPacket;
   @Mocked
   DatagramPacket mockDatagramPacket;

   @Test(timeout = 4000)
   public void testEmptyPacket() throws Exception {
      new Expectations() {
         {
            capturingDatagramSocket.receive((DatagramPacket) any);
            result = null;

            testBsmReceiver.publish((byte[]) any);
            times = 0;
         }
      };
      testBsmReceiver.setStopped(true);
      testBsmReceiver.run();
   }

   @Test(timeout = 4000)
   public void testNonEmptyPacket() throws Exception {
      new Expectations() {
         {
            new DatagramPacket((byte[]) any, anyInt);
            result = mockDatagramPacket;

            mockDatagramPacket.getLength();
            result = 1;

            testBsmReceiver.publish((byte[]) any);
            times = 1;
         }
      };
      testBsmReceiver.setStopped(true);
      testBsmReceiver.run();
   }

   @Test(timeout = 4000)
   public void testPublishException() throws Exception {
      new Expectations() {
         {
            new DatagramPacket((byte[]) any, anyInt);
            result = mockDatagramPacket;

            mockDatagramPacket.getLength();
            result = 1;

            testBsmReceiver.publish((byte[]) any);
            result = new IOException("testException123");
         }
      };
      testBsmReceiver.setStopped(true);
      testBsmReceiver.run();
   }

   /**
    * Test when packet starts with "0014" it removes nothing
    */
   @Test
   public void testRemoveHeaderNoHeader() {
      String expectedHex = "0014012345678900";
      byte[] testInput = HexUtils.fromHexString(expectedHex);

      assertEquals(expectedHex, HexUtils.toHexString(testBsmReceiver.removeHeader(testInput)));
   }

   /**
    * Test when packet contains no "0014" it removes nothing
    */
   @Test
   public void testRemoveHeaderNoBsm() {
      String expectedHex = "012345678900";
      byte[] testInput = HexUtils.fromHexString(expectedHex);

      assertEquals(expectedHex, HexUtils.toHexString(testBsmReceiver.removeHeader(testInput)));
   }

   /**
    * Test when packet does not start with 0014 but still contains it, removes
    * header. Note that headers are >= 20 bytes <br>
    * Input = "0123456789012345678900140987654321" <br>
    * Output = "00140987654321"
    */
   @Test
   public void testRemoveHeaderContainsHeader() {
      String inputHex = "0123456789012345678900140987654321";
      String expectedHex = "00140987654321";
      byte[] testInput = HexUtils.fromHexString(inputHex);

      assertEquals(expectedHex, HexUtils.toHexString(testBsmReceiver.removeHeader(testInput)));
   }

}
